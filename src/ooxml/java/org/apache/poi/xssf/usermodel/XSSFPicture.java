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

package org.apache.poi.xssf.usermodel;

import java.awt.Dimension;
import java.io.IOException;

import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPictureNonVisual;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;

/**
 * Represents a picture shape in a SpreadsheetML drawing.
 *
 * @author Yegor Kozlov
 */
public final class XSSFPicture extends XSSFShape implements Picture {
    private static final POILogger logger = POILogFactory.getLogger(XSSFPicture.class);

    /**
     * Column width measured as the number of characters of the maximum digit width of the
     * numbers 0, 1, 2, ..., 9 as rendered in the normal style's font. There are 4 pixels of margin
     * padding (two on each side), plus 1 pixel padding for the gridlines.
     *
     * This value is the same for default font in Office 2007 (Calibry) and Office 2003 and earlier (Arial)
     */
    private static float DEFAULT_COLUMN_WIDTH = 9.140625f;

    /**
     * A default instance of CTShape used for creating new shapes.
     */
    private static CTPicture prototype = null;

    /**
     * This object specifies a picture object and all its properties
     */
    private CTPicture ctPicture;

    /**
     * Construct a new XSSFPicture object. This constructor is called from
     *  {@link XSSFDrawing#createPicture(XSSFClientAnchor, int)}
     *
     * @param drawing the XSSFDrawing that owns this picture
     */
    protected XSSFPicture(XSSFDrawing drawing, CTPicture ctPicture){
        this.drawing = drawing;
        this.ctPicture = ctPicture;
    }

    /**
     * Returns a prototype that is used to construct new shapes
     *
     * @return a prototype that is used to construct new shapes
     */
    protected static CTPicture prototype(){
        if(prototype == null) {
            CTPicture pic = CTPicture.Factory.newInstance();
            CTPictureNonVisual nvpr = pic.addNewNvPicPr();
            CTNonVisualDrawingProps nvProps = nvpr.addNewCNvPr();
            nvProps.setId(1);
            nvProps.setName("Picture 1");
            nvProps.setDescr("Picture");
            CTNonVisualPictureProperties nvPicProps = nvpr.addNewCNvPicPr();
            nvPicProps.addNewPicLocks().setNoChangeAspect(true);

            CTBlipFillProperties blip = pic.addNewBlipFill();
            blip.addNewBlip().setEmbed("");
            blip.addNewStretch().addNewFillRect();

            CTShapeProperties sppr = pic.addNewSpPr();
            CTTransform2D t2d = sppr.addNewXfrm();
            CTPositiveSize2D ext = t2d.addNewExt();
            //should be original picture width and height expressed in EMUs
            ext.setCx(0);
            ext.setCy(0);

            CTPoint2D off = t2d.addNewOff();
            off.setX(0);
            off.setY(0);

            CTPresetGeometry2D prstGeom = sppr.addNewPrstGeom();
            prstGeom.setPrst(STShapeType.RECT);
            prstGeom.addNewAvLst();

            prototype = pic;
        }
        return prototype;
    }

    /**
     * Link this shape with the picture data
     *
     * @param rel relationship referring the picture data
     */
    protected void setPictureReference(PackageRelationship rel){
        ctPicture.getBlipFill().getBlip().setEmbed(rel.getId());
    }

    /**
     * Return the underlying CTPicture bean that holds all properties for this picture
     *
     * @return the underlying CTPicture bean
     */
    @Internal
    public CTPicture getCTPicture(){
        return ctPicture;
    }

    /**
     * @return shapeId
     */
    public int getPictureIndex(){
    	return (int) ctPicture.getNvPicPr().getCNvPr().getId();
    }

    public void resize(){
        resize(1.0);
    }

    public void resize(double scale){
        XSSFClientAnchor anchor = (XSSFClientAnchor)getAnchor();

        XSSFClientAnchor pref = getPreferredSize(scale);

        int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());

        anchor.setCol2(col2);
        anchor.setDx1(0);
        anchor.setDx2(pref.getDx2());

        anchor.setRow2(row2);
        anchor.setDy1(0);
        anchor.setDy2(pref.getDy2());
    }

    public XSSFClientAnchor getPreferredSize(){
        return getPreferredSize(1.0);
    }

    public XSSFClientAnchor getPreferredSize(double scale){
        XSSFClientAnchor anchor = (XSSFClientAnchor)getAnchor();

        XSSFPictureData data = getPictureData();
        Dimension size = getImageDimension(data.getPackagePart(), data.getPictureType());
        double scaledWidth = size.getWidth() * scale;
        double scaledHeight = size.getHeight() * scale;

        return getPreferredSize(anchor, data, scaledWidth, scaledHeight);
    }

    public XSSFClientAnchor getPreferredSize(final double width, final double height, final boolean aspectLock) {
        final XSSFClientAnchor anchor = (XSSFClientAnchor)getAnchor();

        final XSSFPictureData data = getPictureData();
        double scaledWidth;
        double scaledHeight;
        if (aspectLock) {
            final Dimension size = getImageDimension(data.getPackagePart(), data.getPictureType());
            final double aspect = size.getWidth() / size.getHeight();
            if (width > height) {
                scaledHeight = width / aspect;
                scaledWidth = scaledHeight * aspect;
            } else {
                scaledWidth = height * aspect;
                scaledHeight = scaledWidth / aspect;
            }
        } else {
            scaledWidth = width;
            scaledHeight = height;
        }

        return getPreferredSize(anchor, data, scaledWidth, scaledHeight);
    }

    private XSSFClientAnchor getPreferredSize(final XSSFClientAnchor anchor, final XSSFPictureData data, final double width, final double height) {
        //space in the leftmost cell
        double w = getColumnWidthInPixels(anchor.getCol1()) - Units.emuToPixel(anchor.getDx1());
        int col2 = anchor.getCol1() + 1;
        int dx2 = 0;

        while (w < width) {
            w += getColumnWidthInPixels(col2++);
        }

        if(w > width) {
            //calculate dx2, offset in the rightmost cell
            col2--;
            final double cw = getColumnWidthInPixels(col2);
            final double delta = w - width;
            dx2 = Units.pixelToEMU(cw - delta);
        }
        anchor.setCol2(col2);
        anchor.setDx2(dx2);

        double h = getRowHeightInPixels(anchor.getRow1()) - Units.emuToPixel(anchor.getDy1());
        int row2 = anchor.getRow1() + 1;
        int dy2 = 0;

        while (h < height) {
            h += getRowHeightInPixels(row2++);
        }

        if(h > height) {
            row2--;
            final double ch = getRowHeightInPixels(row2);
            final double delta = h - height;
            dy2 = Units.pixelToEMU(ch - delta);
        }
        anchor.setRow2(row2);
        anchor.setDy2(dy2);

        final CTPositiveSize2D size2d =  ctPicture.getSpPr().getXfrm().getExt();
        size2d.setCx(Units.pixelToEMU(width));
        size2d.setCy(Units.pixelToEMU(height));

        return anchor;
    }

    private float getColumnWidthInPixels(int columnIndex){
        XSSFSheet sheet = (XSSFSheet)getDrawing().getParent();

        CTCol col = sheet.getColumnHelper().getColumn(columnIndex, false);
        double numChars = col == null || !col.isSetWidth() ? DEFAULT_COLUMN_WIDTH : col.getWidth();

        return (float)numChars*XSSFWorkbook.DEFAULT_CHARACTER_WIDTH;
    }

    private float getRowHeightInPixels(int rowIndex){
        XSSFSheet sheet = (XSSFSheet)getDrawing().getParent();

        XSSFRow row = sheet.getRow(rowIndex);
        float height = row != null ?  row.getHeightInPoints() : sheet.getDefaultRowHeightInPoints();
        return height*PIXEL_DPI/POINT_DPI;
    }

    /**
     * Return the dimension of this image
     *
     * @param part the package part holding raw picture data
     * @param type type of the picture: {@link Workbook#PICTURE_TYPE_JPEG},
     * {@link Workbook#PICTURE_TYPE_PNG} or {@link Workbook#PICTURE_TYPE_DIB}
     *
     * @return image dimension in pixels
     */
    protected static Dimension getImageDimension(PackagePart part, int type){
        try {
            return ImageUtils.getImageDimension(part.getInputStream(), type);
        } catch (IOException e){
            //return a "singulariry" if ImageIO failed to read the image
            logger.log(POILogger.WARN, e);
            return new Dimension();
        }
    }

    /**
     * Return picture data for this shape
     *
     * @return picture data for this shape
     */
    public XSSFPictureData getPictureData() {
        String blipId = ctPicture.getBlipFill().getBlip().getEmbed();
        return  (XSSFPictureData)getDrawing().getRelationById(blipId);
    }

    protected CTShapeProperties getShapeProperties(){
        return ctPicture.getSpPr();
    }

}
