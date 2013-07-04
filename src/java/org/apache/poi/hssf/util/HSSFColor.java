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

package org.apache.poi.hssf.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Color;


/**
 * Intends to provide support for the very evil index to triplet issue and
 * will likely replace the color constants interface for HSSF 2.0.
 * This class contains static inner class members for representing colors.
 * Each color has an index (for the standard palette in Excel (tm) ),
 * native (RGB) triplet and string triplet.  The string triplet is as the
 * color would be represented by Gnumeric.  Having (string) this here is a bit of a
 * collusion of function between HSSF and the HSSFSerializer but I think its
 * a reasonable one in this case.
 *
 * @author  Andrew C. Oliver (acoliver at apache dot org)
 * @author  Brian Sanders (bsanders at risklabs dot com) - full default color palette
 */
public class HSSFColor implements Color {
    private static Map<Integer,HSSFColor> indexHash; 

    /** Creates a new instance of HSSFColor */
    public HSSFColor()
    {
    }

    /**
     * This function returns all the colours in an unmodifiable Map.
     * The map is cached on first use.
     *
     * @return a Map containing all colours keyed by <tt>Integer</tt> excel-style palette indexes
     */
    public static final Map<Integer,HSSFColor> getIndexHash() {
        if(indexHash == null) {
           indexHash = Collections.unmodifiableMap( createColorsByIndexMap() );
        }

        return indexHash;
    }
    /**
     * This function returns all the Colours, stored in a HashMap that
     *  can be edited. No caching is performed. If you don't need to edit
     *  the table, then call {@link #getIndexHash()} which returns a
     *  statically cached imuatable map of colours.
     */
    public static final Map<Integer,HSSFColor> getMutableIndexHash() {
       return createColorsByIndexMap();
    }

    private static Map<Integer,HSSFColor> createColorsByIndexMap() {
        HSSFColor[] colors = getAllColors();
        Map<Integer,HSSFColor> result = new HashMap<>(colors.length * 3 / 2);

        for (final HSSFColor color : colors) {
            Integer index1 = Integer.valueOf(color.getIndex());
            if (result.containsKey(index1)) {
                HSSFColor prevColor = (HSSFColor)result.get(index1);
                throw new RuntimeException("Dup color index (" + index1
                        + ") for colors (" + prevColor.getClass().getName()
                        + "),(" + color.getClass().getName() + ")");
            }
            result.put(index1, color);
        }

        for (final HSSFColor color : colors) {
            Integer index2 = getIndex2(color);
            if (index2 == null) {
                // most colors don't have a second index
                continue;
            }
            if (result.containsKey(index2)) {
                if (false) { // Many of the second indexes clash
                    HSSFColor prevColor = (HSSFColor)result.get(index2);
                    throw new RuntimeException("Dup color index (" + index2
                            + ") for colors (" + prevColor.getClass().getName()
                            + "),(" + color.getClass().getName() + ")");
                }
            }
            result.put(index2, color);
        }
        return result;
    }

    private static Integer getIndex2(HSSFColor color) {

        Field f;
        try {
            f = color.getClass().getDeclaredField("index2");
        } catch (NoSuchFieldException e) {
            // can happen because not all colors have a second index
            return null;
        }

        Short s;
        try {
            s = (Short) f.get(color);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Integer.valueOf(s.intValue());
    }

    private static HSSFColor[] getAllColors() {

        return new HSSFColor[] {
                new BLACK(), new BROWN(), new OLIVE_GREEN(), new DARK_GREEN(),
                new DARK_TEAL(), new DARK_BLUE(), new INDIGO(), new GREY_80_PERCENT(),
                new ORANGE(), new DARK_YELLOW(), new GREEN(), new TEAL(), new BLUE(),
                new BLUE_GREY(), new GREY_50_PERCENT(), new RED(), new LIGHT_ORANGE(), new LIME(),
                new SEA_GREEN(), new AQUA(), new LIGHT_BLUE(), new VIOLET(), new GREY_40_PERCENT(),
                new PINK(), new GOLD(), new YELLOW(), new BRIGHT_GREEN(), new TURQUOISE(),
                new DARK_RED(), new SKY_BLUE(), new PLUM(), new GREY_25_PERCENT(), new ROSE(),
                new LIGHT_YELLOW(), new LIGHT_GREEN(), new LIGHT_TURQUOISE(), new PALE_BLUE(),
                new LAVENDER(), new WHITE(), new CORNFLOWER_BLUE(), new LEMON_CHIFFON(),
                new MAROON(), new ORCHID(), new CORAL(), new ROYAL_BLUE(),
                new LIGHT_CORNFLOWER_BLUE(), new TAN(),
        };
    }

    /**
     * this function returns all colors in a hastable.  Its not implemented as a
     * static member/staticly initialized because that would be dirty in a
     * server environment as it is intended.  This means you'll eat the time
     * it takes to create it once per request but you will not hold onto it
     * if you have none of those requests.
     *
     * @return a hashtable containing all colors keyed by String gnumeric-like triplets
     */
    public static final Map<String,HSSFColor> getTripletHash()
    {
        return createColorsByHexStringMap();
    }

    private static Map<String,HSSFColor> createColorsByHexStringMap() {
        HSSFColor[] colors = getAllColors();
        Map<String,HSSFColor> result = new HashMap<>(colors.length * 3 / 2);

        for (final HSSFColor color : colors) {
            String hexString = color.getHexString();
            if (result.containsKey(hexString)) {
            	HSSFColor other = (HSSFColor)result.get(hexString);
                throw new RuntimeException(
                		"Dup color hexString (" + hexString
                        + ") for color (" + color.getClass().getName() + ") - "
                        + " already taken by (" + other.getClass().getName() + ")"
                );
            }
            result.put(hexString, color);
        }
        return result;
    }

    /**
     * @return index to the standard palette
     */

    public short getIndex()
    {
        return BLACK.index;
    }

    /**
     * @return  triplet representation like that in Excel
     */

    public short[] getTriplet()
    {
        return BLACK.triplet;
    }

    // its a hack but its a good hack

    /**
     * @return a hex string exactly like a gnumeric triplet
     */

    public String getHexString()
    {
        return BLACK.hexString;
    }

    /**
     * Class BLACK
     *
     */

    public static final class BLACK
        extends HSSFColor
    {
        public static final short   index     = 0x8;
        public static final short[] triplet   =
        {
            0, 0, 0
        };
        public static final String  hexString = "0:0:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class BROWN
     *
     */

    public static final class BROWN
        extends HSSFColor
    {
        public static final short   index     = 0x3c;
        public static final short[] triplet   =
        {
            153, 51, 0
        };
        public static final String  hexString = "9999:3333:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class OLIVE_GREEN
     *
     */

    public static class OLIVE_GREEN
        extends HSSFColor
    {
        public static final short   index     = 0x3b;
        public static final short[] triplet   =
        {
            51, 51, 0
        };
        public static final String  hexString = "3333:3333:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class DARK_GREEN
     *
     */

    public static final class DARK_GREEN
        extends HSSFColor
    {
        public static final short   index     = 0x3a;
        public static final short[] triplet   =
        {
            0, 51, 0
        };
        public static final String  hexString = "0:3333:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class DARK_TEAL
     *
     */

    public static final class DARK_TEAL
        extends HSSFColor
    {
        public static final short   index     = 0x38;
        public static final short[] triplet   =
        {
            0, 51, 102
        };
        public static final String  hexString = "0:3333:6666";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class DARK_BLUE
     *
     */

    public static final class DARK_BLUE
        extends HSSFColor
    {
        public static final short   index     = 0x12;
        public static final short   index2    = 0x20;
        public static final short[] triplet   =
        {
            0, 0, 128
        };
        public static final String  hexString = "0:0:8080";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class INDIGO
     *
     */

    public static final class INDIGO
        extends HSSFColor
    {
        public static final short   index     = 0x3e;
        public static final short[] triplet   =
        {
            51, 51, 153
        };
        public static final String  hexString = "3333:3333:9999";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class GREY_80_PERCENT
     *
     */

    public static final class GREY_80_PERCENT
        extends HSSFColor
    {
        public static final short   index     = 0x3f;
        public static final short[] triplet   =
        {
            51, 51, 51
        };
        public static final String  hexString = "3333:3333:3333";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class DARK_RED
     *
     */

    public static final class DARK_RED
        extends HSSFColor
    {
        public static final short   index     = 0x10;
        public static final short   index2    = 0x25;
        public static final short[] triplet   =
        {
            128, 0, 0
        };
        public static final String  hexString = "8080:0:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class ORANGE
     *
     */

    public static final class ORANGE
        extends HSSFColor
    {
        public static final short   index     = 0x35;
        public static final short[] triplet   =
        {
            255, 102, 0
        };
        public static final String  hexString = "FFFF:6666:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class DARK_YELLOW
     *
     */

    public static final class DARK_YELLOW
        extends HSSFColor
    {
        public static final short   index     = 0x13;
        public static final short[] triplet   =
        {
            128, 128, 0
        };
        public static final String  hexString = "8080:8080:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class GREEN
     *
     */

    public static final class GREEN
        extends HSSFColor
    {
        public static final short   index     = 0x11;
        public static final short[] triplet   =
        {
            0, 128, 0
        };
        public static final String  hexString = "0:8080:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class TEAL
     *
     */

    public static final class TEAL
        extends HSSFColor
    {
        public static final short   index     = 0x15;
        public static final short   index2    = 0x26;
        public static final short[] triplet   =
        {
            0, 128, 128
        };
        public static final String  hexString = "0:8080:8080";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class BLUE
     *
     */

    public static final class BLUE
        extends HSSFColor
    {
        public static final short   index     = 0xc;
        public static final short   index2    = 0x27;
        public static final short[] triplet   =
        {
            0, 0, 255
        };
        public static final String  hexString = "0:0:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class BLUE_GREY
     *
     */

    public static final class BLUE_GREY
        extends HSSFColor
    {
        public static final short   index     = 0x36;
        public static final short[] triplet   =
        {
            102, 102, 153
        };
        public static final String  hexString = "6666:6666:9999";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class GREY_50_PERCENT
     *
     */

    public static final class GREY_50_PERCENT
        extends HSSFColor
    {
        public static final short   index     = 0x17;
        public static final short[] triplet   =
        {
            128, 128, 128
        };
        public static final String  hexString = "8080:8080:8080";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class RED
     *
     */

    public static final class RED
        extends HSSFColor
    {
        public static final short   index     = 0xa;
        public static final short[] triplet   =
        {
            255, 0, 0
        };
        public static final String  hexString = "FFFF:0:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LIGHT_ORANGE
     *
     */

    public static final class LIGHT_ORANGE
        extends HSSFColor
    {
        public static final short   index     = 0x34;
        public static final short[] triplet   =
        {
            255, 153, 0
        };
        public static final String  hexString = "FFFF:9999:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LIME
     *
     */

    public static final class LIME
        extends HSSFColor
    {
        public static final short   index     = 0x32;
        public static final short[] triplet   =
        {
            153, 204, 0
        };
        public static final String  hexString = "9999:CCCC:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class SEA_GREEN
     *
     */

    public static final class SEA_GREEN
        extends HSSFColor
    {
        public static final short   index     = 0x39;
        public static final short[] triplet   =
        {
            51, 153, 102
        };
        public static final String  hexString = "3333:9999:6666";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class AQUA
     *
     */

    public static final class AQUA
        extends HSSFColor
    {
        public static final short   index     = 0x31;
        public static final short[] triplet   =
        {
            51, 204, 204
        };
        public static final String  hexString = "3333:CCCC:CCCC";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LIGHT_BLUE
     *
     */

    public static final class LIGHT_BLUE
        extends HSSFColor
    {
        public static final short   index     = 0x30;
        public static final short[] triplet   =
        {
            51, 102, 255
        };
        public static final String  hexString = "3333:6666:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class VIOLET
     *
     */

    public static final class VIOLET
        extends HSSFColor
    {
        public static final short   index     = 0x14;
        public static final short   index2    = 0x24;
        public static final short[] triplet   =
        {
            128, 0, 128
        };
        public static final String  hexString = "8080:0:8080";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class GREY_40_PERCENT
     *
     */

    public static final class GREY_40_PERCENT
        extends HSSFColor
    {
        public static final short   index     = 0x37;
        public static final short[] triplet   =
        {
            150, 150, 150
        };
        public static final String  hexString = "9696:9696:9696";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class PINK
     *
     */

    public static final class PINK
        extends HSSFColor
    {
        public static final short   index     = 0xe;
        public static final short   index2    = 0x21;
        public static final short[] triplet   =
        {
            255, 0, 255
        };
        public static final String  hexString = "FFFF:0:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class GOLD
     *
     */

    public static final class GOLD
        extends HSSFColor
    {
        public static final short   index     = 0x33;
        public static final short[] triplet   =
        {
            255, 204, 0
        };
        public static final String  hexString = "FFFF:CCCC:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class YELLOW
     *
     */

    public static final class YELLOW
        extends HSSFColor
    {
        public static final short   index     = 0xd;
        public static final short   index2    = 0x22;
        public static final short[] triplet   =
        {
            255, 255, 0
        };
        public static final String  hexString = "FFFF:FFFF:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class BRIGHT_GREEN
     *
     */

    public static final class BRIGHT_GREEN
        extends HSSFColor
    {
        public static final short   index     = 0xb;
        public static final short   index2    = 0x23;
        public static final short[] triplet   =
        {
            0, 255, 0
        };
        public static final String  hexString = "0:FFFF:0";

        public short getIndex()
        {
            return index;
        }

        public String getHexString()
        {
            return hexString;
        }

        public short[] getTriplet()
        {
            return triplet;
        }
    }

    /**
     * Class TURQUOISE
     *
     */

    public static final class TURQUOISE
        extends HSSFColor
    {
        public static final short   index     = 0xf;
        public static final short   index2    = 0x23;
        public static final short[] triplet   =
        {
            0, 255, 255
        };
        public static final String  hexString = "0:FFFF:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class SKY_BLUE
     *
     */

    public static final class SKY_BLUE
        extends HSSFColor
    {
        public static final short   index     = 0x28;
        public static final short[] triplet   =
        {
            0, 204, 255
        };
        public static final String  hexString = "0:CCCC:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class PLUM
     *
     */

    public static final class PLUM
        extends HSSFColor
    {
        public static final short   index     = 0x3d;
        public static final short   index2    = 0x19;
        public static final short[] triplet   =
        {
            153, 51, 102
        };
        public static final String  hexString = "9999:3333:6666";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class GREY_25_PERCENT
     *
     */

    public static final class GREY_25_PERCENT
        extends HSSFColor
    {
        public static final short   index     = 0x16;
        public static final short[] triplet   =
        {
            192, 192, 192
        };
        public static final String  hexString = "C0C0:C0C0:C0C0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class ROSE
     *
     */

    public static final class ROSE
        extends HSSFColor
    {
        public static final short   index     = 0x2d;
        public static final short[] triplet   =
        {
            255, 153, 204
        };
        public static final String  hexString = "FFFF:9999:CCCC";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class TAN
     *
     */

    public static final class TAN
        extends HSSFColor
    {
        public static final short   index     = 0x2f;
        public static final short[] triplet   =
        {
            255, 204, 153
        };
        public static final String  hexString = "FFFF:CCCC:9999";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LIGHT_YELLOW
     *
     */

    public static final class LIGHT_YELLOW
        extends HSSFColor
    {
        public static final short   index     = 0x2b;
        public static final short[] triplet   =
        {
            255, 255, 153
        };
        public static final String  hexString = "FFFF:FFFF:9999";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LIGHT_GREEN
     *
     */

    public static final class LIGHT_GREEN
        extends HSSFColor
    {
        public static final short   index     = 0x2a;
        public static final short[] triplet   =
        {
            204, 255, 204
        };
        public static final String  hexString = "CCCC:FFFF:CCCC";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LIGHT_TURQUOISE
     *
     */

    public static final class LIGHT_TURQUOISE
        extends HSSFColor
    {
        public static final short   index     = 0x29;
        public static final short   index2    = 0x1b;
        public static final short[] triplet   =
        {
            204, 255, 255
        };
        public static final String  hexString = "CCCC:FFFF:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class PALE_BLUE
     *
     */

    public static final class PALE_BLUE
        extends HSSFColor
    {
        public static final short   index     = 0x2c;
        public static final short[] triplet   =
        {
            153, 204, 255
        };
        public static final String  hexString = "9999:CCCC:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LAVENDER
     *
     */

    public static final class LAVENDER
        extends HSSFColor
    {
        public static final short   index     = 0x2e;
        public static final short[] triplet   =
        {
            204, 153, 255
        };
        public static final String  hexString = "CCCC:9999:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class WHITE
     *
     */

    public static final class WHITE
        extends HSSFColor
    {
        public static final short   index     = 0x9;
        public static final short[] triplet   =
        {
            255, 255, 255
        };
        public static final String  hexString = "FFFF:FFFF:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class CORNFLOWER_BLUE
     */
    public static final class CORNFLOWER_BLUE
        extends HSSFColor
    {
        public static final short   index     = 0x18;
        public static final short[] triplet   =
        {
            153, 153, 255
        };
        public static final String  hexString = "9999:9999:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }


    /**
     * Class LEMON_CHIFFON
     */
    public static final class LEMON_CHIFFON
        extends HSSFColor
    {
        public static final short   index     = 0x1a;
        public static final short[] triplet   =
        {
            255, 255, 204
        };
        public static final String  hexString = "FFFF:FFFF:CCCC";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class MAROON
     */
    public static final class MAROON
        extends HSSFColor
    {
        public static final short   index     = 0x19;
        public static final short[] triplet   =
        {
            127, 0, 0
        };
        public static final String  hexString = "8000:0:0";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class ORCHID
     */
    public static final class ORCHID
        extends HSSFColor
    {
        public static final short   index     = 0x1c;
        public static final short[] triplet   =
        {
            102, 0, 102
        };
        public static final String  hexString = "6666:0:6666";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class CORAL
     */
    public static final class CORAL
        extends HSSFColor
    {
        public static final short   index     = 0x1d;
        public static final short[] triplet   =
        {
            255, 128, 128
        };
        public static final String  hexString = "FFFF:8080:8080";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class ROYAL_BLUE
     */
    public static final class ROYAL_BLUE
        extends HSSFColor
    {
        public static final short   index     = 0x1e;
        public static final short[] triplet   =
        {
            0, 102, 204
        };
        public static final String  hexString = "0:6666:CCCC";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Class LIGHT_CORNFLOWER_BLUE
     */
    public static final class LIGHT_CORNFLOWER_BLUE
        extends HSSFColor
    {
        public static final short   index     = 0x1f;
        public static final short[] triplet   =
        {
            204, 204, 255
        };
        public static final String  hexString = "CCCC:CCCC:FFFF";

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return triplet;
        }

        public String getHexString()
        {
            return hexString;
        }
    }

    /**
     * Special Default/Normal/Automatic color.
     * <p><i>Note:</i> This class is NOT in the default HashTables returned by HSSFColor.
     * The index is a special case which is interpreted in the various setXXXColor calls.
     *
     * @author Jason
     *
     */
    public static final class AUTOMATIC extends HSSFColor
    {
        private static HSSFColor instance = new AUTOMATIC();

        public static final short   index     = 0x40;

        public short getIndex()
        {
            return index;
        }

        public short[] getTriplet()
        {
            return BLACK.triplet;
        }

        public String getHexString()
        {
            return BLACK.hexString;
        }

        public static HSSFColor getInstance() {
          return instance;
        }
    }
}
