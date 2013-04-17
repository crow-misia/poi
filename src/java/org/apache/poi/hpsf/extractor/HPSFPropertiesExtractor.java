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

package org.apache.poi.hpsf.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.POIDocument;
import org.apache.poi.POITextExtractor;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.SpecialPropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Extracts all of the HPSF properties, both
 *  build in and custom, returning them in
 *  textual form.
 */
public class HPSFPropertiesExtractor extends POITextExtractor {
    public HPSFPropertiesExtractor(POITextExtractor mainExtractor) {
        super(mainExtractor);
    }
    public HPSFPropertiesExtractor(POIDocument doc) {
        super(doc);
    }
    public HPSFPropertiesExtractor(POIFSFileSystem fs) {
        super(new HPSFPropertiesOnlyDocument(fs));
    }
    public HPSFPropertiesExtractor(NPOIFSFileSystem fs) {
        super(new HPSFPropertiesOnlyDocument(fs));
    }

    public String getDocumentSummaryInformationText() {
        DocumentSummaryInformation dsi = document.getDocumentSummaryInformation();
        StringBuilder text = new StringBuilder();

        // Normal properties
        text.append( getPropertiesText(dsi) );

        // Now custom ones
        CustomProperties cps = dsi == null ? null : dsi.getCustomProperties();
        if (cps != null) {
            Iterator<String> keys = cps.nameSet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String val = HelperPropertySet.getPropertyValueText( cps.get(key) );
                text.append(key + " = " + val + "\n");
            }
        }

        // All done
        return text.toString();
    }
    public String getSummaryInformationText() {
        SummaryInformation si = document.getSummaryInformation();

        // Just normal properties
        return getPropertiesText(si);
    }

    private static String getPropertiesText(SpecialPropertySet ps) {
        if (ps == null) {
            // Not defined, oh well
            return "";
        }

        StringBuilder text = new StringBuilder();

        PropertyIDMap idMap = ps.getPropertySetIDMap();
        Property[] props = ps.getProperties();
        for (int i=0; i<props.length; i++) {
            String type = Long.toString( props[i].getID() );
            Object typeObj = idMap.get(props[i].getID());
            if(typeObj != null) {
                type = typeObj.toString();
            }

            String val = HelperPropertySet.getPropertyValueText( props[i].getValue() );
            text.append(type + " = " + val + "\n");
        }

        return text.toString();
    }

    /**
     * @return the text of all the properties defined in
     *  the document.
     */
    public String getText() {
        return getSummaryInformationText() + getDocumentSummaryInformationText();
    }

    /**
     * Prevent recursion!
     */
    public POITextExtractor getMetadataTextExtractor() {
        throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
    }
    
    private static abstract class HelperPropertySet extends SpecialPropertySet {
        public HelperPropertySet() {
            super(null);
        }
        public static String getPropertyValueText(Object val) {
            if (val == null) {
                return "(not set)";
            }
            return SpecialPropertySet.getPropertyStringValue(val);
        }
    }

    public static void main(String[] args) throws IOException {
        for (String file : args) {
            HPSFPropertiesExtractor ext = new HPSFPropertiesExtractor(
                    new NPOIFSFileSystem(new File(file)));
            System.out.println(ext.getText());
        }
    }
}
