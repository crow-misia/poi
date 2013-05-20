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

package org.apache.poi.ss.formula.functions;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/**
 * Test case for TEXT()
 *
 * @author Stephen Wolke (smwolke at geistig.com)
 */
public final class TestText extends TestCase {
	private static final TextFunction T = null;

	public void testTextWithStringFirstArg() {

		ValueEval strArg = new StringEval("abc");
		ValueEval formatArg = new StringEval("abc");
		ValueEval[] args = { strArg, formatArg };
		ValueEval result = T.TEXT.evaluate(args, -1, (short)-1);
		assertEquals(ErrorEval.VALUE_INVALID, result);
	}

	public void testTextWithDeciamlFormatSecondArg() {

		ValueEval numArg = new NumberEval(321321.321);
		ValueEval formatArg = new StringEval("#,###.00000");
		ValueEval[] args = { numArg, formatArg };
		ValueEval result = T.TEXT.evaluate(args, -1, (short)-1);
		char groupSeparator = new DecimalFormatSymbols(Locale.getDefault()).getGroupingSeparator();
		char decimalSeparator = new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
		ValueEval testResult = new StringEval("321" + groupSeparator + "321" + decimalSeparator + "32100");
		assertEquals(testResult.toString(), result.toString());
		numArg = new NumberEval(321.321);
		formatArg = new StringEval("00000.00000");
		args[0] = numArg;
		args[1] = formatArg;
		result = T.TEXT.evaluate(args, -1, (short)-1);
		testResult = new StringEval("00321" + decimalSeparator + "32100");
		assertEquals(testResult.toString(), result.toString());

		formatArg = new StringEval("$#.#");
		args[1] = formatArg;
		result = T.TEXT.evaluate(args, -1, (short)-1);
		testResult = new StringEval("$321" + decimalSeparator + "3");
		assertEquals(testResult.toString(), result.toString());
	}

	public void testTextWithFractionFormatSecondArg() {

		ValueEval numArg = new NumberEval(321.321);
		ValueEval formatArg = new StringEval("# #/#");
		ValueEval[] args = { numArg, formatArg };
		ValueEval result = T.TEXT.evaluate(args, -1, (short)-1);
		ValueEval testResult = new StringEval("321 1/3");
		assertEquals(testResult.toString(), result.toString());

		formatArg = new StringEval("# #/##");
		args[1] = formatArg;
		result = T.TEXT.evaluate(args, -1, (short)-1);
		testResult = new StringEval("321 26/81");
		assertEquals(testResult.toString(), result.toString());

		formatArg = new StringEval("#/##");
		args[1] = formatArg;
		result = T.TEXT.evaluate(args, -1, (short)-1);
		testResult = new StringEval("26027/81");
		assertEquals(testResult.toString(), result.toString());
	}

   public void testTextWithDateFormatSecondArg() {
      // Test with Java style M=Month
      ValueEval numArg = new NumberEval(321.321);
      ValueEval formatArg = new StringEval("dd:MM:yyyy hh:mm:ss");
      ValueEval[] args = { numArg, formatArg };
      ValueEval result = T.TEXT.evaluate(args, -1, (short)-1);
      ValueEval testResult = new StringEval("16:11:1900 07:42:14");
      assertEquals(testResult.toString(), result.toString());

      // Excel also supports "m before h is month"
      formatArg = new StringEval("dd:mm:yyyy hh:mm:ss");
      args[1] = formatArg;
      result = T.TEXT.evaluate(args, -1, (short)-1);
      testResult = new StringEval("16:11:1900 07:42:14");
      assertEquals(testResult.toString(), result.toString());

      // this line is intended to compute how "November" would look like in the current locale
      String november = new SimpleDateFormat("MMMM").format(new GregorianCalendar(2010,10,15).getTime());

      // Again with Java style
      formatArg = new StringEval("MMMM dd, yyyy");
      args[1] = formatArg;
      result = T.TEXT.evaluate(args, -1, (short)-1);
      testResult = new StringEval(november + " 16, 1900");
      assertEquals(testResult.toString(), result.toString());

      // And Excel style
      formatArg = new StringEval("mmmm dd, yyyy");
      args[1] = formatArg;
      result = T.TEXT.evaluate(args, -1, (short)-1);
      testResult = new StringEval(november + " 16, 1900");
      assertEquals(testResult.toString(), result.toString());
   }
   
   public void testTextMidB() {
       final ValueEval numArg = new NumberEval(1);
       final ValueEval num2Arg = new NumberEval(2);
       final ValueEval strArg = new StringEval("あいうえお");
       ValueEval[] args = new ValueEval[] { strArg };

       ValueEval result = TextFunction.LENB.evaluate(args, -1, (short) -1);
       ValueEval testResult = new NumberEval(10.0);
       assertEquals(testResult.toString(), result.toString());

       args = new ValueEval[] { strArg, numArg, num2Arg, };
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("あ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(2);
       args[2] = new NumberEval(3);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" い");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(2);
       args[2] = new NumberEval(5);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" いう");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       args[2] = new NumberEval(5);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("あい ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       args[2] = new NumberEval(6);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("あいう");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(2);
       args[2] = new NumberEval(1);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(2);
       args[2] = new NumberEval(0);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       args[2] = new NumberEval(0);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = StringEval.EMPTY_INSTANCE;
       assertEquals(testResult.toString(), result.toString());

       args[0] = new StringEval("abcdあいうえお");
       args[1] = new NumberEval(1);
       args[2] = new NumberEval(0);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = StringEval.EMPTY_INSTANCE;
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       args[2] = new NumberEval(1);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("a");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       args[2] = new NumberEval(5);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("abcd ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       args[2] = new NumberEval(6);
       result = TextFunction.MIDB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("abcdあ");
       assertEquals(testResult.toString(), result.toString());
   }

   public void testTextLeftB() {
       final ValueEval numArg = new NumberEval(2);
       final ValueEval strArg = new StringEval("あいうえお");
       ValueEval[] args = new ValueEval[] { strArg };

       args = new ValueEval[] { strArg, numArg, };
       ValueEval result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       ValueEval testResult = new StringEval("あ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(3);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("あ ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(5);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("あい ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(6);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("あいう");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(0);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = StringEval.EMPTY_INSTANCE;
       assertEquals(testResult.toString(), result.toString());

       args[0] = new StringEval("abcあいうえお");
       args[1] = new NumberEval(0);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = StringEval.EMPTY_INSTANCE;
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("a");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(4);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("abc ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(5);
       result = TextFunction.LEFTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("abcあ");
       assertEquals(testResult.toString(), result.toString());
   }

   public void testTextRightB() {
       final ValueEval numArg = new NumberEval(2);
       final ValueEval strArg = new StringEval("あいうえお");
       ValueEval[] args = new ValueEval[] { strArg };

       args = new ValueEval[] { strArg, numArg, };
       ValueEval result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       ValueEval testResult = new StringEval("お");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" ");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(3);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" お");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(5);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" えお");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(6);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("うえお");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(0);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = StringEval.EMPTY_INSTANCE;
       assertEquals(testResult.toString(), result.toString());

       args[0] = new StringEval("あいうえおabc");
       args[1] = new NumberEval(0);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = StringEval.EMPTY_INSTANCE;
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(1);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("c");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(4);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" abc");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(5);
       result = TextFunction.RIGHTB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("おabc");
       assertEquals(testResult.toString(), result.toString());
   }

   public void testTextReplaceB() {
       final Function REPLACEB = new ReplaceB();

       final ValueEval oldStrArg = new StringEval("あいうえお");
       final ValueEval numArg = new NumberEval(1);
       final ValueEval num2Arg = new NumberEval(2);
       final ValueEval newStrArg = new StringEval("ABC");

       final ValueEval[] args = new ValueEval[] { oldStrArg, numArg, num2Arg, newStrArg, };

       ValueEval result = REPLACEB.evaluate(args, -1, (short) -1);
       ValueEval testResult = new StringEval("ABCいうえお");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(2);
       result = REPLACEB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" ABC うえお");
       assertEquals(testResult.toString(), result.toString());

       args[2] = new NumberEval(1);
       result = REPLACEB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" ABCいうえお");
       assertEquals(testResult.toString(), result.toString());

       args[2] = new NumberEval(1);
       args[3] = new StringEval("かきく");
       result = REPLACEB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" かきくいうえお");
       assertEquals(testResult.toString(), result.toString());

       args[2] = new NumberEval(10);
       args[3] = new StringEval("かきく");
       result = REPLACEB.evaluate(args, -1, (short) -1);
       testResult = new StringEval(" かきく");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(3);
       args[2] = new NumberEval(10);
       args[3] = new StringEval("かきく");
       result = REPLACEB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("あかきく");
       assertEquals(testResult.toString(), result.toString());

       args[0] = new StringEval("ABCあいうえおDEF");
       args[1] = new NumberEval(3);
       args[2] = new NumberEval(1);
       args[3] = new StringEval("かきく");
       result = REPLACEB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("ABかきくあいうえおDEF");
       assertEquals(testResult.toString(), result.toString());

       args[1] = new NumberEval(3);
       args[2] = new NumberEval(2);
       args[3] = new StringEval("かきく");
       result = REPLACEB.evaluate(args, -1, (short) -1);
       testResult = new StringEval("ABかきく いうえおDEF");
       assertEquals(testResult.toString(), result.toString());
   }

   public void testTextAsc() {
       final Function ASC = TextFunction.ASC;

       final ValueEval strArg = new StringEval("アイウエオ");

       final ValueEval[] args = new ValueEval[] { strArg, };

       final ValueEval result = ASC.evaluate(args, -1, (short) -1);
       final ValueEval testResult = new StringEval("ｱｲｳｴｵ");
       assertEquals(testResult.toString(), result.toString());
   }
}
