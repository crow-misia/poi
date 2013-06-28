/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.xwpf.extractor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xwpf.model.XWPFCommentsDecorator;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlink;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

/**
 * Helper class to extract text from an OOXML Word file
 */
public class XWPFWordExtractor extends POIXMLTextExtractor {
   public static final XWPFRelation[] SUPPORTED_TYPES = new XWPFRelation[] {
      XWPFRelation.DOCUMENT, XWPFRelation.TEMPLATE,
      XWPFRelation.MACRO_DOCUMENT, 
      XWPFRelation.MACRO_TEMPLATE_DOCUMENT
   };
   
	private XWPFDocument document;
	private boolean fetchHyperlinks = false;
	
	public XWPFWordExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
		this(new XWPFDocument(container));
	}
	public XWPFWordExtractor(XWPFDocument document) {
		super(document);
		this.document = document;
	}

	/**
	 * Should we also fetch the hyperlinks, when fetching 
	 *  the text content? Default is to only output the
	 *  hyperlink label, and not the contents
	 */
	public void setFetchHyperlinks(boolean fetch) {
		fetchHyperlinks = fetch;
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.err.println("Use:");
			System.err.println("  XWPFWordExtractor <filename.docx>");
			System.exit(1);
		}
		try (final POIXMLTextExtractor extractor = 
			new XWPFWordExtractor(POIXMLDocument.openPackage(
					args[0]
			))) {
			System.out.println(extractor.getText());
		}
	}
	
	public String getText() {
		StringBuilder text = new StringBuilder();
		XWPFHeaderFooterPolicy hfPolicy = document.getHeaderFooterPolicy();

		// Start out with all headers
		extractHeaders(text, hfPolicy);
		
		// body elements
      for (IBodyElement e : document.getBodyElements()){
         appendBodyElementText(text, e);
         text.append('\n');
     }
		
		// Finish up with all the footers
		extractFooters(text, hfPolicy);
		
		return text.toString();
	}

   public void appendBodyElementText(StringBuilder text, IBodyElement e){
      if (e instanceof XWPFParagraph){
          appendParagraphText(text, (XWPFParagraph)e);
      } else if (e instanceof XWPFTable){
          appendTableText(text, (XWPFTable)e);
      } else if (e instanceof XWPFSDT){
          text.append(((XWPFSDT)e).getContent().getText());
      }
   }
   
   public void appendParagraphText(StringBuilder text, XWPFParagraph paragraph){
      try {
          CTSectPr ctSectPr = null;
          if (paragraph.getCTP().getPPr()!=null) {
              ctSectPr = paragraph.getCTP().getPPr().getSectPr();
          }

          XWPFHeaderFooterPolicy headerFooterPolicy = null;

          if (ctSectPr!=null) {
              headerFooterPolicy = new XWPFHeaderFooterPolicy(document, ctSectPr);
              extractHeaders(text, headerFooterPolicy);
          }


          for(IRunElement run : paragraph.getRuns()) {
              text.append(run.toString());
              if(run instanceof XWPFHyperlinkRun && fetchHyperlinks) {
                  XWPFHyperlink link = ((XWPFHyperlinkRun)run).getHyperlink(document);
                  if(link != null)
                      text.append(" <" + link.getURL() + ">");
              }
          }

          // Add comments
          XWPFCommentsDecorator decorator = new XWPFCommentsDecorator(paragraph, null);
          String commentText = decorator.getCommentText();
          if (commentText.length() > 0){
              text.append(commentText).append('\n');
          }

          // Do endnotes and footnotes
          String footnameText = paragraph.getFootnoteText();
          if(footnameText != null && footnameText.length() > 0) {
              text.append(footnameText + '\n');
          }

          if (ctSectPr!=null) {
              extractFooters(text, headerFooterPolicy);
          }
      } catch (IOException e) {
          throw new POIXMLException(e);
      } catch (XmlException e) {
          throw new POIXMLException(e);
      }
     
   }

   private void appendTableText(StringBuilder text, XWPFTable table){
      //this works recursively to pull embedded tables from tables
      for (XWPFTableRow row : table.getRows()){
          List<XWPFTableCell> cells = row.getTableCells();
          for (int i = 0; i < cells.size(); i++){
              XWPFTableCell cell = cells.get(i);
              text.append(cell.getTextRecursively());
              if (i < cells.size()-1){
                  text.append("\t");
              }
          }
          text.append('\n');
      }
   }
   
	private void extractFooters(StringBuilder text, XWPFHeaderFooterPolicy hfPolicy) {
		if(hfPolicy.getFirstPageFooter() != null) {
			text.append( hfPolicy.getFirstPageFooter().getText() );
		}
		if(hfPolicy.getEvenPageFooter() != null) {
			text.append( hfPolicy.getEvenPageFooter().getText() );
		}
		if(hfPolicy.getDefaultFooter() != null) {
			text.append( hfPolicy.getDefaultFooter().getText() );
		}
	}

	private void extractHeaders(StringBuilder text, XWPFHeaderFooterPolicy hfPolicy) {
		if(hfPolicy.getFirstPageHeader() != null) {
			text.append( hfPolicy.getFirstPageHeader().getText() );
		}
		if(hfPolicy.getEvenPageHeader() != null) {
			text.append( hfPolicy.getEvenPageHeader().getText() );
		}
		if(hfPolicy.getDefaultHeader() != null) {
			text.append( hfPolicy.getDefaultHeader().getText() );
		}
	}
}
