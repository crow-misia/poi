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

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.apache.poi.POIDataSamples;
import org.apache.poi.hssf.HSSFTestDataSamples;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;

/**
 * 
 */
public final class TestOLE2Embeding extends TestCase {

    public void testEmbeding() {
        // This used to break, until bug #43116 was fixed
        HSSFWorkbook workbook = HSSFTestDataSamples.openSampleWorkbook("ole2-embedding.xls");

        // Check we can get at the Escher layer still
        workbook.getAllPictures();
    }

    public void testEmbeddedObjects() throws Exception {
        HSSFWorkbook workbook = HSSFTestDataSamples.openSampleWorkbook("ole2-embedding.xls");

        List<HSSFObjectData> objects = workbook.getAllEmbeddedObjects();
        assertEquals("Wrong number of objects", 2, objects.size());
        assertEquals("Wrong name for first object", "MBD06CAB431",
                ((HSSFObjectData)
                objects.get(0)).getDirectory().getName());
        assertEquals("Wrong name for second object", "MBD06CAC85A",
                ((HSSFObjectData)
                objects.get(1)).getDirectory().getName());
    }
    
    public void testReallyEmbedSomething() throws Exception {
    	HSSFWorkbook wb = new HSSFWorkbook();
    	HSSFSheet sheet = wb.createSheet();
    	HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

    	byte[] pictureData = HSSFTestDataSamples.getTestDataFileContent("logoKarmokar4.png");
    	byte[] picturePPT = POIDataSamples.getSlideShowInstance().readFile("clock.jpg");
    	int imgIdx = wb.addPicture(pictureData, HSSFWorkbook.PICTURE_TYPE_PNG);
    	POIFSFileSystem pptPoifs = getSamplePPT();
    	int pptIdx = wb.addOlePackage(pptPoifs, "Sample-PPT", "sample.ppt", "sample.ppt");
    	POIFSFileSystem xlsPoifs = getSampleXLS();
    	int imgPPT = wb.addPicture(picturePPT, HSSFWorkbook.PICTURE_TYPE_JPEG);
    	int xlsIdx = wb.addOlePackage(xlsPoifs, "Sample-XLS", "sample.xls", "sample.xls");
    	int txtIdx = wb.addOlePackage(getSampleTXT(), "Sample-TXT", "sample.txt", "sample.txt");
    	
        int rowoffset = 5;
        int coloffset = 5;

        CreationHelper ch = wb.getCreationHelper();
        HSSFClientAnchor anchor = (HSSFClientAnchor)ch.createClientAnchor();
        anchor.setAnchor((short)(2+coloffset), 1+rowoffset, 0, 0, (short)(3+coloffset), 5+rowoffset, 0, 0);
        anchor.setAnchorType(ClientAnchor.DONT_MOVE_AND_RESIZE);
    	
        patriarch.createObjectData(anchor, pptIdx, imgPPT);

        anchor = (HSSFClientAnchor)ch.createClientAnchor();
        anchor.setAnchor((short)(5+coloffset), 1+rowoffset, 0, 0, (short)(6+coloffset), 5+rowoffset, 0, 0);
        anchor.setAnchorType(ClientAnchor.DONT_MOVE_AND_RESIZE);
        
        patriarch.createObjectData(anchor, xlsIdx, imgIdx);
        
        anchor = (HSSFClientAnchor)ch.createClientAnchor();
        anchor.setAnchor((short)(3+coloffset), 10+rowoffset, 0, 0, (short)(5+coloffset), 11+rowoffset, 0, 0);
        anchor.setAnchorType(ClientAnchor.DONT_MOVE_AND_RESIZE);
        
        patriarch.createObjectData(anchor, txtIdx, imgIdx);
        
        anchor = (HSSFClientAnchor)ch.createClientAnchor();
        anchor.setAnchor((short)(1+coloffset), -2+rowoffset, 0, 0, (short)(7+coloffset), 14+rowoffset, 0, 0);
        anchor.setAnchorType(ClientAnchor.DONT_MOVE_AND_RESIZE);

        HSSFSimpleShape circle = patriarch.createSimpleShape(anchor);
        circle.setShapeType(HSSFSimpleShape.OBJECT_TYPE_OVAL);
        circle.setNoFill(true);

        if (false) {
	        FileOutputStream fos = new FileOutputStream("embed.xls");
	        wb.write(fos);
	        fos.close();
        }
        
        wb = HSSFTestDataSamples.writeOutAndReadBack(wb);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HSSFObjectData od = wb.getAllEmbeddedObjects().get(0);
        Ole10Native ole10 = Ole10Native.createFromEmbeddedOleObject((DirectoryNode)od.getDirectory());
        bos.reset();
        pptPoifs.writeFilesystem(bos);
        assertArrayEquals(ole10.getDataBuffer(), bos.toByteArray());

        od = wb.getAllEmbeddedObjects().get(1);
        ole10 = Ole10Native.createFromEmbeddedOleObject((DirectoryNode)od.getDirectory());
        bos.reset();
        xlsPoifs.writeFilesystem(bos);
        assertArrayEquals(ole10.getDataBuffer(), bos.toByteArray());

        od = wb.getAllEmbeddedObjects().get(2);
        ole10 = Ole10Native.createFromEmbeddedOleObject((DirectoryNode)od.getDirectory());
        assertArrayEquals(ole10.getDataBuffer(), getSampleTXT());
    
    }
    
    static POIFSFileSystem getSamplePPT() throws IOException {
    	// scratchpad classes are not available, so we use something pre-cooked
    	InputStream is = POIDataSamples.getSlideShowInstance().openResourceAsStream("with_textbox.ppt");
    	POIFSFileSystem poifs = new POIFSFileSystem(is);
    	is.close();
        
        return poifs;
    }
    
    static POIFSFileSystem getSampleXLS() throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        sheet.createRow(5).createCell(2).setCellValue("yo dawg i herd you like embeddet objekts, so we put a ole in your ole so you can save a file while you save a file");

    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	wb.write(bos);
    	POIFSFileSystem poifs = new POIFSFileSystem(new ByteArrayInputStream(bos.toByteArray()));
        
        return poifs;
    }
    
    static byte[] getSampleTXT() {
        return "All your base are belong to us".getBytes();
    }    
}