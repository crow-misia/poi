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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.poi.hssf.HSSFTestDataSamples;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Class TestDateUtil
 *
 *
 * @author  Dan Sherman (dsherman at isisph.com)
 * @author  Hack Kampbjorn (hak at 2mba.dk)
 * @author  Pavel Krupets (pkrupets at palmtreebusiness dot com)
 * @author Alex Jacoby (ajacoby at gmail.com)
 * @version %I%, %G%
 */
public final class TestDateUtil extends TestCase {

    public static final int CALENDAR_JANUARY = 0;
    public static final int CALENDAR_FEBRUARY = 1;
    public static final int CALENDAR_MARCH = 2;
    public static final int CALENDAR_APRIL = 3;
    public static final int CALENDAR_JULY = 6;
    public static final int CALENDAR_OCTOBER = 9;

    /**
     * Checks the date conversion functions in the DateUtil class.
     */

    public void testDateConversion() {

        // Iteratating over the hours exposes any rounding issues.
        for (int hour = 0; hour < 23; hour++)
        {
            GregorianCalendar date      = new GregorianCalendar(2002, 0, 1,
                    hour, 1, 1);
            double            excelDate =
                    DateUtil.getExcelDate(date.getTime(), false);

            assertEquals("Checking hour = " + hour, date.getTime().getTime(),
                    DateUtil.getJavaDate(excelDate, false).getTime());
        }

        // check 1900 and 1904 date windowing conversions
        double excelDate = 36526.0;
        // with 1900 windowing, excelDate is Jan. 1, 2000
        // with 1904 windowing, excelDate is Jan. 2, 2004
        GregorianCalendar cal = new GregorianCalendar(2000,0,1); // Jan. 1, 2000
        Date dateIf1900 = cal.getTime();
        cal.add(GregorianCalendar.YEAR,4); // now Jan. 1, 2004
        cal.add(GregorianCalendar.DATE,1); // now Jan. 2, 2004
        Date dateIf1904 = cal.getTime();
        // 1900 windowing
        assertEquals("Checking 1900 Date Windowing",
                dateIf1900.getTime(),
                DateUtil.getJavaDate(excelDate,false).getTime());
        // 1904 windowing
        assertEquals("Checking 1904 Date Windowing",
                dateIf1904.getTime(),
                DateUtil.getJavaDate(excelDate,true).getTime());
    }

    /**
     * Checks the conversion of a java.util.date to Excel on a day when
     * Daylight Saving Time starts.
     */
    public void testExcelConversionOnDSTStart() {
        TimeZone cet = TimeZone.getTimeZone("Europe/Copenhagen");
        TimeZone.setDefault(cet);
        Calendar cal = new GregorianCalendar(2004, CALENDAR_MARCH, 28);
        for (int hour = 0; hour < 24; hour++) {

            // Skip 02:00 CET as that is the Daylight change time
            // and Java converts it automatically to 03:00 CEST
            if (hour == 2) {
                continue;
            }

            cal.set(Calendar.HOUR_OF_DAY, hour);
            Date javaDate = cal.getTime();
            double excelDate = DateUtil.getExcelDate(javaDate, false);
            double difference = excelDate - Math.floor(excelDate);
            int differenceInHours = (int) (difference * 24 * 60 + 0.5) / 60;
            assertEquals("Checking " + hour + " hour on Daylight Saving Time start date",
                    hour,
                    differenceInHours);
            assertEquals("Checking " + hour + " hour on Daylight Saving Time start date",
                    javaDate.getTime(),
                    DateUtil.getJavaDate(excelDate, false).getTime());
        }
    }

    /**
     * Checks the conversion of an Excel date to a java.util.date on a day when
     * Daylight Saving Time starts.
     */
    public void testJavaConversionOnDSTStart() {
        TimeZone cet = TimeZone.getTimeZone("Europe/Copenhagen");
        TimeZone.setDefault(cet);
        Calendar cal = new GregorianCalendar(2004, CALENDAR_MARCH, 28);
        double excelDate = DateUtil.getExcelDate(cal.getTime(), false);
        double oneHour = 1.0 / 24;
        double oneMinute = oneHour / 60;
        for (int hour = 0; hour < 24; hour++, excelDate += oneHour) {

            // Skip 02:00 CET as that is the Daylight change time
            // and Java converts it automatically to 03:00 CEST
            if (hour == 2) {
                continue;
            }

            cal.set(Calendar.HOUR_OF_DAY, hour);
            Date javaDate = DateUtil.getJavaDate(excelDate, false);
            assertEquals("Checking " + hour + " hours on Daylight Saving Time start date",
                    excelDate,
                    DateUtil.getExcelDate(javaDate, false), oneMinute);
        }
    }

    /**
     * Checks the conversion of a java.util.Date to Excel on a day when
     * Daylight Saving Time ends.
     */
    public void testExcelConversionOnDSTEnd() {
        TimeZone cet = TimeZone.getTimeZone("Europe/Copenhagen");
        TimeZone.setDefault(cet);
        Calendar cal = new GregorianCalendar(2004, CALENDAR_OCTOBER, 31);
        for (int hour = 0; hour < 24; hour++) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
            Date javaDate = cal.getTime();
            double excelDate = DateUtil.getExcelDate(javaDate, false);
            double difference = excelDate - Math.floor(excelDate);
            int differenceInHours = (int) (difference * 24 * 60 + 0.5) / 60;
            assertEquals("Checking " + hour + " hour on Daylight Saving Time end date",
                    hour,
                    differenceInHours);
            assertEquals("Checking " + hour + " hour on Daylight Saving Time start date",
                    javaDate.getTime(),
                    DateUtil.getJavaDate(excelDate, false).getTime());
        }
    }

    /**
     * Checks the conversion of an Excel date to java.util.Date on a day when
     * Daylight Saving Time ends.
     */
    public void testJavaConversionOnDSTEnd() {
        TimeZone cet = TimeZone.getTimeZone("Europe/Copenhagen");
        TimeZone.setDefault(cet);
        Calendar cal = new GregorianCalendar(2004, CALENDAR_OCTOBER, 31);
        double excelDate = DateUtil.getExcelDate(cal.getTime(), false);
        double oneHour = 1.0 / 24;
        double oneMinute = oneHour / 60;
        for (int hour = 0; hour < 24; hour++, excelDate += oneHour) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
            Date javaDate = DateUtil.getJavaDate(excelDate, false);
            assertEquals("Checking " + hour + " hours on Daylight Saving Time start date",
                    excelDate,
                    DateUtil.getExcelDate(javaDate, false), oneMinute);
        }
    }

    /**
     * Tests that we deal with time-zones properly
     */
    public void testCalendarConversion() {
        GregorianCalendar date = new GregorianCalendar(2002, 0, 1, 12, 1, 1);
        Date expected = date.getTime();

        // Iterating over the hours exposes any rounding issues.
        for (int hour = -12; hour <= 12; hour++)
        {
            String id = "GMT" + (hour < 0 ? "" : "+") + hour + ":00";
            date.setTimeZone(TimeZone.getTimeZone(id));
            date.set(Calendar.HOUR_OF_DAY, 12);
            double excelDate = DateUtil.getExcelDate(date, false);
            Date javaDate = DateUtil.getJavaDate(excelDate);

            // Should match despite time-zone
            assertEquals("Checking timezone " + id, expected.getTime(), javaDate.getTime());
        }
        
        // Check that the timezone aware getter works correctly 
        TimeZone cet = TimeZone.getTimeZone("Europe/Copenhagen");
        TimeZone ldn = TimeZone.getTimeZone("Europe/London");
        TimeZone.setDefault(cet);
        
        // 12:45 on 27th April 2012
        double excelDate = 41026.53125;
        
        // Same, no change
        assertEquals(
              DateUtil.getJavaDate(excelDate, false).getTime(),
              DateUtil.getJavaDate(excelDate, false, cet).getTime()
        );
        
        // London vs Copenhagen, should differ by an hour
        Date cetDate = DateUtil.getJavaDate(excelDate, false);
        Date ldnDate = DateUtil.getJavaDate(excelDate, false, ldn);
        assertEquals(ldnDate.getTime() - cetDate.getTime(), 60*60*1000);
    }

    /**
     * Tests that we correctly detect date formats as such
     */
    public void testIdentifyDateFormats() {
        // First up, try with a few built in date formats
        short[] builtins = new short[] { 0x0e, 0x0f, 0x10, 0x16, 0x2d, 0x2e };
        for(int i=0; i<builtins.length; i++) {
            String formatStr = HSSFDataFormat.getBuiltinFormat(builtins[i]);
            assertTrue( DateUtil.isInternalDateFormat(builtins[i]) );
            assertTrue( DateUtil.isADateFormat(builtins[i],formatStr) );
        }

        // Now try a few built-in non date formats
        builtins = new short[] { 0x01, 0x02, 0x17, 0x1f, 0x30 };
        for(int i=0; i<builtins.length; i++) {
            String formatStr = HSSFDataFormat.getBuiltinFormat(builtins[i]);
            assertFalse( DateUtil.isInternalDateFormat(builtins[i]) );
            assertFalse( DateUtil.isADateFormat(builtins[i],formatStr) );
        }

        // Now for some non-internal ones
        // These come after the real ones
        int numBuiltins = HSSFDataFormat.getNumberOfBuiltinBuiltinFormats();
        assertTrue(numBuiltins < 60);
        short formatId = 60;
        assertFalse( DateUtil.isInternalDateFormat(formatId) );

        // Valid ones first
        String[] formats = new String[] {
                "yyyy-mm-dd", "yyyy/mm/dd", "yy/mm/dd", "yy/mmm/dd",
                "dd/mm/yy", "dd/mm/yyyy", "dd/mmm/yy",
                "dd-mm-yy", "dd-mm-yyyy",
                "DD-MM-YY", "DD-mm-YYYY",
                "dd\\-mm\\-yy", // Sometimes escaped
                "dd.mm.yyyy", "dd\\.mm\\.yyyy",
                "dd\\ mm\\.yyyy AM", "dd\\ mm\\.yyyy pm",
                 "dd\\ mm\\.yyyy\\-dd", "[h]:mm:ss",
                 "mm/dd/yy", "\"mm\"/\"dd\"/\"yy\"",
                 "m\\/d\\/yyyy", 

                // These crazy ones are valid
                "yyyy-mm-dd;@", "yyyy/mm/dd;@",
                "dd-mm-yy;@", "dd-mm-yyyy;@",
                // These even crazier ones are also valid
                // (who knows what they mean though...)
                "[$-F800]dddd\\,\\ mmm\\ dd\\,\\ yyyy",
                "[$-F900]ddd/mm/yyy",
                // These ones specify colours, who knew that was allowed?
                "[BLACK]dddd/mm/yy",
                "[yeLLow]yyyy-mm-dd"
        };
        for(int i=0; i<formats.length; i++) {
            assertTrue(
                    formats[i] + " is a date format",
                    DateUtil.isADateFormat(formatId, formats[i])
            );
        }

        // Then time based ones too
        formats = new String[] {
                "yyyy-mm-dd hh:mm:ss", "yyyy/mm/dd HH:MM:SS",
                "mm/dd HH:MM", "yy/mmm/dd SS",
                "mm/dd HH:MM AM", "mm/dd HH:MM am",
                "mm/dd HH:MM PM", "mm/dd HH:MM pm",
                "m/d/yy h:mm AM/PM",
                "hh:mm:ss", "hh:mm:ss.0", "mm:ss.0",
                //support elapsed time [h],[m],[s]
                "[hh]", "[mm]", "[ss]", "[SS]", "[red][hh]"
        };
        for(int i=0; i<formats.length; i++) {
            assertTrue(
                    formats[i] + " is a datetime format",
                    DateUtil.isADateFormat(formatId, formats[i])
            );
        }

        // Then invalid ones
        formats = new String[] {
                "yyyy*mm*dd",
                "0.0", "0.000",
                "0%", "0.0%",
                "[]Foo", "[BLACK]0.00%",
                "[ms]", "[Mh]",
                "", null
        };
        for(int i=0; i<formats.length; i++) {
            assertFalse(
                    formats[i] + " is not a date or datetime format",
                    DateUtil.isADateFormat(formatId, formats[i])
            );
        }

        // And these are ones we probably shouldn't allow,
        //  but would need a better regexp
        formats = new String[] {
                "yyyy:mm:dd",
        };
        for(int i=0; i<formats.length; i++) {
        //    assertFalse( DateUtil.isADateFormat(formatId, formats[i]) );
        }
    }

    /**
     * Test that against a real, test file, we still do everything
     *  correctly
     */
    public void testOnARealFile() {

        HSSFWorkbook workbook = HSSFTestDataSamples.openSampleWorkbook("DateFormats.xls");
        HSSFSheet sheet       = workbook.getSheetAt(0);

        HSSFRow  row;
        HSSFCell cell;
        HSSFCellStyle style;

        double aug_10_2007 = 39304.0;

        // Should have dates in 2nd column
        // All of them are the 10th of August
        // 2 US dates, 3 UK dates
        row  = sheet.getRow(0);
        cell = row.getCell(1);
        style = cell.getCellStyle();
        assertEquals(aug_10_2007, cell.getNumericCellValue(), 0.0001);
        assertEquals("d-mmm-yy", style.getDataFormatString());
        assertTrue(DateUtil.isInternalDateFormat(style.getDataFormat()));
        assertTrue(DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString()));
        assertTrue(DateUtil.isCellDateFormatted(cell));

        row  = sheet.getRow(1);
        cell = row.getCell(1);
        style = cell.getCellStyle();
        assertEquals(aug_10_2007, cell.getNumericCellValue(), 0.0001);
        assertFalse(DateUtil.isInternalDateFormat(cell.getCellStyle().getDataFormat()));
        assertTrue(DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString()));
        assertTrue(DateUtil.isCellDateFormatted(cell));

        row  = sheet.getRow(2);
        cell = row.getCell(1);
        style = cell.getCellStyle();
        assertEquals(aug_10_2007, cell.getNumericCellValue(), 0.0001);
        assertTrue(DateUtil.isInternalDateFormat(cell.getCellStyle().getDataFormat()));
        assertTrue(DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString()));
        assertTrue(DateUtil.isCellDateFormatted(cell));

        row  = sheet.getRow(3);
        cell = row.getCell(1);
        style = cell.getCellStyle();
        assertEquals(aug_10_2007, cell.getNumericCellValue(), 0.0001);
        assertFalse(DateUtil.isInternalDateFormat(cell.getCellStyle().getDataFormat()));
        assertTrue(DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString()));
        assertTrue(DateUtil.isCellDateFormatted(cell));

        row  = sheet.getRow(4);
        cell = row.getCell(1);
        style = cell.getCellStyle();
        assertEquals(aug_10_2007, cell.getNumericCellValue(), 0.0001);
        assertFalse(DateUtil.isInternalDateFormat(cell.getCellStyle().getDataFormat()));
        assertTrue(DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString()));
        assertTrue(DateUtil.isCellDateFormatted(cell));
    }

    public void testDateBug_2Excel() {
        assertEquals(59.0, DateUtil.getExcelDate(createDate(1900, CALENDAR_FEBRUARY, 28), false), 0.00001);
        assertEquals(61.0, DateUtil.getExcelDate(createDate(1900, CALENDAR_MARCH, 1), false), 0.00001);

        assertEquals(37315.00, DateUtil.getExcelDate(createDate(2002, CALENDAR_FEBRUARY, 28), false), 0.00001);
        assertEquals(37316.00, DateUtil.getExcelDate(createDate(2002, CALENDAR_MARCH, 1), false), 0.00001);
        assertEquals(37257.00, DateUtil.getExcelDate(createDate(2002, CALENDAR_JANUARY, 1), false), 0.00001);
        assertEquals(38074.00, DateUtil.getExcelDate(createDate(2004, CALENDAR_MARCH, 28), false), 0.00001);
    }

    public void testDateBug_2Java() {
        assertEquals(createDate(1900, CALENDAR_FEBRUARY, 28), DateUtil.getJavaDate(59.0, false));
        assertEquals(createDate(1900, CALENDAR_MARCH, 1), DateUtil.getJavaDate(61.0, false));

        assertEquals(createDate(2002, CALENDAR_FEBRUARY, 28), DateUtil.getJavaDate(37315.00, false));
        assertEquals(createDate(2002, CALENDAR_MARCH, 1), DateUtil.getJavaDate(37316.00, false));
        assertEquals(createDate(2002, CALENDAR_JANUARY, 1), DateUtil.getJavaDate(37257.00, false));
        assertEquals(createDate(2004, CALENDAR_MARCH, 28), DateUtil.getJavaDate(38074.00, false));
    }

    public void testDate1904() {
        assertEquals(createDate(1904, CALENDAR_JANUARY, 2), DateUtil.getJavaDate(1.0, true));
        assertEquals(createDate(1904, CALENDAR_JANUARY, 1), DateUtil.getJavaDate(0.0, true));
        assertEquals(0.0, DateUtil.getExcelDate(createDate(1904, CALENDAR_JANUARY, 1), true), 0.00001);
        assertEquals(1.0, DateUtil.getExcelDate(createDate(1904, CALENDAR_JANUARY, 2), true), 0.00001);

        assertEquals(createDate(1998, CALENDAR_JULY, 5), DateUtil.getJavaDate(35981, false));
        assertEquals(createDate(1998, CALENDAR_JULY, 5), DateUtil.getJavaDate(34519, true));

        assertEquals(35981.0, DateUtil.getExcelDate(createDate(1998, CALENDAR_JULY, 5), false), 0.00001);
        assertEquals(34519.0, DateUtil.getExcelDate(createDate(1998, CALENDAR_JULY, 5), true), 0.00001);
    }

    /**
     * @param month zero based
     * @param day one based
     */
    private static Date createDate(int year, int month, int day) {
        return createDate(year, month, day, 0, 0, 0);
    }

    /**
     * @param month zero based
     * @param day one based
     */
    private static Date createDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar c = new GregorianCalendar();
        c.set(year, month, day, hour, minute, second);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Check if DateUtil.getAbsoluteDay works as advertised.
     */
    public void testAbsoluteDay() {
        // 1 Jan 1900 is 1 day after 31 Dec 1899
        GregorianCalendar calendar = new GregorianCalendar(1900, 0, 1);
        assertEquals("Checking absolute day (1 Jan 1900)", 1, DateUtil.absoluteDay(calendar, false));
        // 1 Jan 1901 is 366 days after 31 Dec 1899
        calendar = new GregorianCalendar(1901, 0, 1);
        assertEquals("Checking absolute day (1 Jan 1901)", 366, DateUtil.absoluteDay(calendar, false));
    }

    public void testConvertTime() {

        final double delta = 1E-7; // a couple of digits more accuracy than strictly required
        assertEquals(0.5, DateUtil.convertTime("12:00"), delta);
        assertEquals(2.0/3, DateUtil.convertTime("16:00"), delta);
        assertEquals(0.0000116, DateUtil.convertTime("0:00:01"), delta);
        assertEquals(0.7330440, DateUtil.convertTime("17:35:35"), delta);
    }

    public void testParseDate() {
        assertEquals(createDate(2008, Calendar.AUGUST, 3), DateUtil.parseYYYYMMDDDate("2008/08/03"));
        assertEquals(createDate(1994, Calendar.MAY, 1), DateUtil.parseYYYYMMDDDate("1994/05/01"));
    }

    /**
     * Ensure that date values *with* a fractional portion get the right time of day
     */
    public void testConvertDateTime() {
    	// Excel day 30000 is date 18-Feb-1982
        // 0.7 corresponds to time 16:48:00
        Date actual = DateUtil.getJavaDate(30000.7);
        Date expected = createDate(1982, 1, 18, 16, 48, 0);
        assertEquals(expected, actual);
    }

    /**
     * User reported a datetime issue in POI-2.5:
     *  Setting Cell's value to Jan 1, 1900 without a time doesn't return the same value set to
     */
    public void testBug19172()
    {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFCell cell = sheet.createRow(0).createCell(0);

        Calendar cal = Calendar.getInstance();

        // A pseduo special Excel dates
        cal.set(1900, 0, 1);

        Date valueToTest = cal.getTime();

        cell.setCellValue(valueToTest);

        Date returnedValue = cell.getDateCellValue();

        assertEquals(valueToTest.getTime(), returnedValue.getTime());
    }

    /**
     * DateUtil.isCellFormatted(Cell) should not true for a numeric cell 
     * that's formatted as ".0000"
     */
    public void testBug54557() throws Exception {
       final String format = ".0000";
       boolean isDateFormat = DateUtil.isADateFormat(165, format);
       
       assertEquals(false, isDateFormat);
    }
}
