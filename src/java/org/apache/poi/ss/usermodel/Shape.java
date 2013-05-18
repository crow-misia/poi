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

import org.apache.poi.hssf.usermodel.HSSFChildAnchor;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFChildAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

/**
 * An interface shape.
 *
 * @author Zenichi Amano
 */
public interface Shape
{
    /**
     * Return the drawing that owns this shape
     *
     * @return the parent drawing that owns this shape
     */
    Drawing getDrawing();

    /**
     * Gets the parent shape.
     */
    Shape getParent();

    /**
     * @return  the anchor that is used by this shape.
     */
    Anchor getAnchor();

    /**
     * Sets a particular anchor.  A top-level shape must have an anchor of
     * ClientAnchor.  A child anchor must have an anchor of ChildAnchor
     *
     * @param anchor    the anchor to use.
     * @throws IllegalArgumentException     when the wrong anchor is used for
     *                                      this particular shape.
     *
     * @see HSSFChildAnchor
     * @see HSSFClientAnchor
     * @see XSSFClientAnchor
     * @see XSSFChildAnchor
     */
    void setAnchor( Anchor anchor );}
