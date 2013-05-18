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
package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.ss.usermodel.Hyperlink;

/**
 * Represents an Excel hyperlink.
 *
 * @author Yegor Kozlov (yegor at apache dot org)
 */
public class HSSFHyperlink implements Hyperlink {

    /**
     * Low-level record object that stores the actual hyperlink data
     */
    protected HyperlinkRecord record = null;

    /**
     * If we create a new hyperlink remember its type
     */
    protected int link_type;

    /**
     * Construct a new hyperlink
     *
     * @param type the type of hyperlink to create
     */
    public HSSFHyperlink( int type )
    {
        this.link_type = type;
        record = new HyperlinkRecord();
        switch(type){
            case LINK_URL:
            case LINK_EMAIL:
                record.newUrlLink();
                break;
            case LINK_FILE:
                record.newFileLink();
                break;
            case LINK_DOCUMENT:
                record.newDocumentLink();
                break;
        }
    }

    /**
     * Initialize the hyperlink by a <code>HyperlinkRecord</code> record
     *
     * @param record
     */
    protected HSSFHyperlink( HyperlinkRecord record )
    {
        this.record = record;
        
        // Figure out the type
        if(record.isFileLink()) {
           link_type = LINK_FILE;
        } else if(record.isDocumentLink()) {
           link_type = LINK_DOCUMENT;
        } else {
           if(record.getAddress() != null &&
                 record.getAddress().startsWith("mailto:")) {
              link_type = LINK_EMAIL;
           } else {
              link_type = LINK_URL;
           }
        }
    }

    public int getFirstRow(){
        return record.getFirstRow();
    }

    public void setFirstRow(int row){
        record.setFirstRow(row);
    }

    public int getLastRow(){
        return record.getLastRow();
    }

    public void setLastRow(int row){
        record.setLastRow(row);
    }

    public int getFirstColumn(){
        return record.getFirstColumn();
    }

    public void setFirstColumn(int col){
        record.setFirstColumn((short)col);
    }

    public int getLastColumn(){
        return record.getLastColumn();
    }

    public void setLastColumn(int col){
        record.setLastColumn((short)col);
    }

    public String getAddress(){
        return record.getAddress();
    }
    public String getTextMark(){
        return record.getTextMark();
    }

    /**
     * Convenience method equivalent to {@link #setAddress(String)}
     *
     * @param textMark the place in worksheet this hyperlink refers to, e.g. 'Target Sheet'!A1'
     */
    public void setTextMark(String textMark) {
        record.setTextMark(textMark);
    }
    public String getShortFilename(){
        return record.getShortFilename();
    }
    /**
     * Convenience method equivalent to {@link #setAddress(String)}
     *
     * @param shortFilename the path to a file this hyperlink points to, e.g. 'readme.txt'
     */
    public void setShortFilename(String shortFilename) {
        record.setShortFilename(shortFilename);
    }

    public void setAddress(String address){
        record.setAddress(address);
    }

    public String getLabel(){
        return record.getLabel();
    }

    public void setLabel(String label){
        record.setLabel(label);
    }

    public int getType(){
        return link_type;
    }
}
