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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.poi.hssf.HSSFTestDataSamples;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/**
 * Test cases for AVERAGEIFS()
 */
public final class TestAverageifs extends TestCase {

    private static final OperationEvaluationContext EC = new OperationEvaluationContext(null, null, 0, 1, 0, null);

	private static ValueEval invokeSumifs(ValueEval[] args, OperationEvaluationContext ec) {
		return new Averageifs().evaluate(args, ec);
	}
	private static void confirmDouble(double expected, ValueEval actualEval) {
		if(!(actualEval instanceof NumericValueEval)) {
		    System.out.println(actualEval);
			throw new AssertionFailedError("Expected numeric result");
		}
		NumericValueEval nve = (NumericValueEval)actualEval;
		assertEquals(expected, nve.getNumberValue(), 0);
	}

    private static void confirm(double expectedResult, ValueEval[] args) {
        confirmDouble(expectedResult, invokeSumifs(args, EC));
    }

    /**
     *  Example 1 from
     *  https://support.office.com/en-US/article/AVERAGEIFS-function-48910C45-1FC0-4389-A028-F7C5C3001690
     */
	public void testExample1() {
        ValueEval[] b2b5 = new ValueEval[] {
                new StringEval("Quiz"),
                new StringEval("Grade"),
                new NumberEval(75),
                new NumberEval(94),
        };

        ValueEval[] c2c5 = new ValueEval[] {
                new StringEval("Quiz"),
                new StringEval("Grade"),
                new NumberEval(85),
                new NumberEval(80)
        };

        ValueEval[] d2d5 = new ValueEval[] {
                new StringEval("Exam"),
                new StringEval("Grade"),
                new NumberEval(87),
                new NumberEval(88)
        };

        ValueEval[] args;
        // "=AVERAGEIFS(B2:B5, B2:B5, ">70", B2:B5, "<90")"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("B2:B5", b2b5),
                EvalFactory.createAreaEval("B2:B5", b2b5),
                new StringEval(">70"),
                EvalFactory.createAreaEval("B2:B5", b2b5),
                new StringEval("<90")
        };
        confirm(75.0, args);

        // "=AVERAGEIFS(C2:C5, C2:C5, ">95")"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("C2:C5", c2c5),
                EvalFactory.createAreaEval("C2:C5", c2c5),
                new StringEval(">95")
        };
        assertEquals(ErrorEval.DIV_ZERO, new Averageifs().evaluate(args, EC));

        // "=AVERAGEIFS(D2:D5, D2:D5, "<>Incomplete", D2:D5, ">80")"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("D2:D5", d2d5),
                EvalFactory.createAreaEval("D2:D5", d2d5),
                new StringEval("<>Incomplete"),
                EvalFactory.createAreaEval("D2:D5", d2d5),
                new StringEval(">80")
        };
        assertEquals(ErrorEval.DIV_ZERO, new Averageifs().evaluate(args, EC));
    }

    /**
     *  Example 2 from
     *  http://office.microsoft.com/en-us/excel-help/sumifs-function-HA010047504.aspx
     */
    public void testExample2() {
        ValueEval[] b2e2 = new ValueEval[] {
                new NumberEval(100),
                new NumberEval(390),
                new NumberEval(8321),
                new NumberEval(500)
        };
        // 1%	0.5%	3%	4%
        ValueEval[] b3e3 = new ValueEval[] {
                new NumberEval(0.01),
                new NumberEval(0.005),
                new NumberEval(0.03),
                new NumberEval(0.04)
        };

        // 1%	1.3%	2.1%	2%
        ValueEval[] b4e4 = new ValueEval[] {
                new NumberEval(0.01),
                new NumberEval(0.013),
                new NumberEval(0.021),
                new NumberEval(0.02)
        };

        // 0.5%	3%	1%	4%
        ValueEval[] b5e5 = new ValueEval[] {
                new NumberEval(0.005),
                new NumberEval(0.03),
                new NumberEval(0.01),
                new NumberEval(0.04)
        };

        ValueEval[] args;

        // "=AVERAGEIFS(B2:E2, B3:E3, ">3%", B4:E4, ">=2%")"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("B2:E2", b2e2),
                EvalFactory.createAreaEval("B3:E3", b3e3),
                new StringEval(">0.03"), // 3% in the MSFT example
                EvalFactory.createAreaEval("B4:E4", b4e4),
                new StringEval(">=0.02"),   // 2% in the MSFT example
        };
        confirm(500.0, args);
    }

    /**
     *  Example 3 from
     *  http://office.microsoft.com/en-us/excel-help/sumifs-function-HA010047504.aspx
     */
    public void testExample3() {
        //3.3	0.8	5.5	5.5
        ValueEval[] b2e2 = new ValueEval[] {
                new NumberEval(3.3),
                new NumberEval(0.8),
                new NumberEval(5.5),
                new NumberEval(5.5)
        };
        // 55	39	39	57.5
        ValueEval[] b3e3 = new ValueEval[] {
                new NumberEval(55),
                new NumberEval(39),
                new NumberEval(39),
                new NumberEval(57.5)
        };

        // 6.5	19.5	6	6.5
        ValueEval[] b4e4 = new ValueEval[] {
                new NumberEval(6.5),
                new NumberEval(19.5),
                new NumberEval(6),
                new NumberEval(6.5)
        };

        ValueEval[] args;

        // "=SUMIFS(B2:E2, B3:E3, ">=40", B4:E4, "<10")"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("B2:E2", b2e2),
                EvalFactory.createAreaEval("B3:E3", b3e3),
                new StringEval(">=40"),
                EvalFactory.createAreaEval("B4:E4", b4e4),
                new StringEval("<10"),
        };
        confirm(4.4, args);
    }

    public void testFromFile() {

        HSSFWorkbook wb = HSSFTestDataSamples.openSampleWorkbook("averageifs.xls");
        HSSFFormulaEvaluator fe = new HSSFFormulaEvaluator(wb);

        HSSFSheet example1 = wb.getSheet("Example 1");
        HSSFCell ex1cell1 = example1.getRow(10).getCell(2);
        fe.evaluate(ex1cell1);
        assertEquals(10.0, ex1cell1.getNumericCellValue());
        HSSFCell ex1cell2 = example1.getRow(11).getCell(2);
        fe.evaluate(ex1cell2);
        assertEquals(10.0, ex1cell2.getNumericCellValue());

        HSSFSheet example2 = wb.getSheet("Example 2");
        HSSFCell ex2cell1 = example2.getRow(6).getCell(2);
        fe.evaluate(ex2cell1);
        assertEquals(500.0, ex2cell1.getNumericCellValue());
        HSSFCell ex2cell2 = example2.getRow(7).getCell(2);
        fe.evaluate(ex2cell2);
        assertEquals(4355.5, ex2cell2.getNumericCellValue());

        HSSFSheet example3 = wb.getSheet("Example 3");
        HSSFCell ex3cell = example3.getRow(5).getCell(2);
        fe.evaluate(ex3cell);
        assertEquals(4.4, ex3cell.getNumericCellValue());

        HSSFSheet example4 = wb.getSheet("Example 4");
        HSSFCell ex4cell = example4.getRow(8).getCell(2);
        fe.evaluate(ex4cell);
        assertEquals(3.5 / 3.0, ex4cell.getNumericCellValue());

        HSSFSheet example5 = wb.getSheet("Example 5");
        HSSFCell ex5cell = example5.getRow(9).getCell(2);
        fe.evaluate(ex5cell);
        assertEquals(312500., ex5cell.getNumericCellValue());

    }
}
