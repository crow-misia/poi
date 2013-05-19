/*
 *  ====================================================================
 *    Licensed to the Apache Software Foundation (ASF) under one or more
 *    contributor license agreements.  See the NOTICE file distributed with
 *    this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0
 *    (the "License"); you may not use this file except in compliance with
 *    the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * ====================================================================
 */

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
 * Test cases for AVERAGEIF()
 *
 * @author Zenichi Amano (sumifs original by Yegor Kozlov)
 */
public final class TestAverageif extends TestCase {

    private static final OperationEvaluationContext EC = new OperationEvaluationContext(null, null, 0, 1, 0, null);

	private static ValueEval invokeAverageifs(final ValueEval[] args, final OperationEvaluationContext ec) {
		return new Averageif().evaluate(args, EC);
	}
	private static void confirmDouble(final double expected, final ValueEval actualEval) {
		if(!(actualEval instanceof NumericValueEval)) {
			throw new AssertionFailedError("Expected numeric result");
		}
		final NumericValueEval nve = (NumericValueEval)actualEval;
		assertEquals(expected, nve.getNumberValue(), 0);
	}

    private static void confirm(final double expectedResult, final ValueEval[] args) {
        confirmDouble(expectedResult, invokeAverageifs(args, EC));
    }

    /**
     *  Example 1 from
     *  http://office.microsoft.com/en-us/excel-help/sumifs-function-HA010047504.aspx
     */
	public void testExample1() {
        // mimic test sample from http://office.microsoft.com/en-us/excel-help/sumifs-function-HA010047504.aspx
        final ValueEval[] a2a9 = new ValueEval[] {
                new NumberEval(5),
                new NumberEval(4),
                new NumberEval(15),
                new NumberEval(3),
                new NumberEval(22),
                new NumberEval(12),
                new NumberEval(10),
                new NumberEval(33)
        };

        final ValueEval[] b2b9 = new ValueEval[] {
                new StringEval("Apples"),
                new StringEval("Apples"),
                new StringEval("Artichokes"),
                new StringEval("Artichokes"),
                new StringEval("Bananas"),
                new StringEval("Bananas"),
                new StringEval("Carrots"),
                new StringEval("Carrots"),
        };

        final ValueEval[] c2c9 = new ValueEval[] {
                new NumberEval(1),
                new NumberEval(2),
                new NumberEval(1),
                new NumberEval(2),
                new NumberEval(1),
                new NumberEval(2),
                new NumberEval(1),
                new NumberEval(2)
        };

        ValueEval[] args;
        // "=AVERAGEIF(B2:B9, "=A*", A2:A9)"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("B2:B9", b2b9),
                new StringEval("A*"),
                EvalFactory.createAreaEval("A2:A9", a2a9),
        };
        confirm(27.0 / 4, args);

        // "=AVERAGEIF(B2:B9, "<>Bananas", A2:A9)"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("B2:B9", b2b9),
                new StringEval("<>Bananas"),
                EvalFactory.createAreaEval("A2:A9", a2a9),
        };
        confirm(70.0 / 6, args);

        // a test case that returns ErrorEval.VALUE_INVALID :
        // the dimensions of the first and second criteria ranges are different
        // "=AVERAGEIF(B2:B8, "<>Bananas", A2:A9)"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("A2:A9", a2a9),
                EvalFactory.createAreaEval("B2:B8", new ValueEval[] {
                        new StringEval("Apples"),
                        new StringEval("Apples"),
                        new StringEval("Artichokes"),
                        new StringEval("Artichokes"),
                        new StringEval("Bananas"),
                        new StringEval("Bananas"),
                        new StringEval("Carrots"),
                }),
                new StringEval("<>Bananas"),
                EvalFactory.createAreaEval("C2:C9", c2c9),
                new NumberEval(1),
        };
        assertEquals(ErrorEval.VALUE_INVALID, invokeAverageifs(args, EC));

	}

    /**
     *  Example 2 from
     *  http://office.microsoft.com/en-us/excel-help/sumifs-function-HA010047504.aspx
     */
    public void testExample2() {
        final ValueEval[] b2e2 = new ValueEval[] {
                new NumberEval(100),
                new NumberEval(390),
                new NumberEval(8321),
                new NumberEval(500)
        };
        // 1%	0.5%	3%	4%
        final ValueEval[] b3e3 = new ValueEval[] {
                new NumberEval(0.01),
                new NumberEval(0.005),
                new NumberEval(0.03),
                new NumberEval(0.04)
        };

        ValueEval[] args;

        // "=AVERAGEIF(B3:E3, ">3%", B2:E2)"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("B3:E3", b3e3),
                new StringEval(">0.03"), // 3% in the MSFT example
                EvalFactory.createAreaEval("B2:E2", b2e2),
        };
        confirm(500.0, args);
    }

    /**
     *  Example 3 from
     *  http://office.microsoft.com/en-us/excel-help/sumifs-function-HA010047504.aspx
     */
    public void testExample3() {
        //3.3	0.8	5.5	5.5
        final ValueEval[] b2e2 = new ValueEval[] {
                new NumberEval(3.3),
                new NumberEval(0.8),
                new NumberEval(5.5),
                new NumberEval(5.5)
        };
        // 55	39	39	57.5
        final ValueEval[] b3e3 = new ValueEval[] {
                new NumberEval(55),
                new NumberEval(39),
                new NumberEval(39),
                new NumberEval(57.5)
        };

        ValueEval[] args;

        // "=SUMIFS(B2:E2, B3:E3, ">=40", B4:E4, "<10")"
        args = new ValueEval[]{
                EvalFactory.createAreaEval("B3:E3", b3e3),
                new StringEval(">=40"),
                EvalFactory.createAreaEval("B2:E2", b2e2),
        };
        confirm(8.8 / 2, args);
    }

    /**
     *  Example 5 from
     *  http://office.microsoft.com/en-us/excel-help/sumifs-function-HA010047504.aspx
     *
     *  Criteria entered as reference and by using wildcard characters
     */
    public void testFromFile() {

        final HSSFWorkbook wb = HSSFTestDataSamples.openSampleWorkbook("averageif.xls");
        final HSSFFormulaEvaluator fe = new HSSFFormulaEvaluator(wb);

        final HSSFSheet example1 = wb.getSheet("Example 1");
        final HSSFCell ex1cell1 = example1.getRow(10).getCell(2);
        fe.evaluate(ex1cell1);
        assertEquals(6.75, ex1cell1.getNumericCellValue());
        final HSSFCell ex1cell2 = example1.getRow(11).getCell(2);
        fe.evaluate(ex1cell2);
        assertEquals(11.666666666666666, ex1cell2.getNumericCellValue());

        final HSSFSheet example2 = wb.getSheet("Example 2");
        final HSSFCell ex2cell1 = example2.getRow(6).getCell(2);
        fe.evaluate(ex2cell1);
        assertEquals(500.0, ex2cell1.getNumericCellValue());
        final HSSFCell ex2cell2 = example2.getRow(7).getCell(2);
        fe.evaluate(ex2cell2);
        assertEquals(3070.333333333333333, ex2cell2.getNumericCellValue());

        final HSSFSheet example3 = wb.getSheet("Example 3");
        final HSSFCell ex3cell = example3.getRow(5).getCell(2);
        fe.evaluate(ex3cell);
        assertEquals(4.4, ex3cell.getNumericCellValue());

        final HSSFSheet example4 = wb.getSheet("Example 4");
        final HSSFCell ex4cell = example4.getRow(8).getCell(2);
        fe.evaluate(ex4cell);
        assertEquals(1.46, ex4cell.getNumericCellValue());

        final HSSFSheet example5 = wb.getSheet("Example 5");
        final HSSFCell ex5cell = example5.getRow(10).getCell(2);
        fe.evaluate(ex5cell);
        assertEquals(260250., ex5cell.getNumericCellValue());

    }
}
