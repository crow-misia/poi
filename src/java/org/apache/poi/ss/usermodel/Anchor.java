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

package org.apache.poi.ss.usermodel;

import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.util.Internal;


/**
 * An anchor is what specifics the position of a shape within a client object
 *
 * @author Zenichi Amano
 */
public interface Anchor {

    /**
     * Returns the x coordinate within the first cell.
     * 
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @return the x coordinate within the first cell
     */
    int getDx1();

    /**
     * Sets the x coordinate within the first cell
     *
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @param dx1 the x coordinate within the first cell
     */
    void setDx1(int dx1);

    /**
     * Returns the y coordinate within the first cell
     *
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @return the y coordinate within the first cell
     */
    int getDy1();

    /**
     * Sets the y coordinate within the first cell
     *
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @param dy1 the y coordinate within the first cell
     */
    void setDy1(int dy1);

    /**
     * Sets the y coordinate within the second cell
     *
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @return the y coordinate within the second cell
     */
    int getDy2();

    /**
     * Sets the y coordinate within the second cell
     *
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @param dy2 the y coordinate within the second cell
     */
    void setDy2(int dy2);

    /**
     * Returns the x coordinate within the second cell
     *
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @return the x coordinate within the second cell
     */
    int getDx2();

    /**
     * Sets the x coordinate within the second cell
     *
     * Note - XSSF and HSSF have a slightly different coordinate
     *  system, values in XSSF are larger by a factor of
     *  {@link org.apache.poi.xssf.usermodel.XSSFShape#EMU_PER_PIXEL}
     *
     * @param dx2 the x coordinate within the second cell
     */
    void setDx2(int dx2);

    @Internal
    EscherRecord getEscherAnchor();

    /**
     * @return whether this shape is horizontally flipped
     */
    boolean isHorizontallyFlipped();

    /**
     * @return  whether this shape is vertically flipped
     */
    boolean isVerticallyFlipped();
}
