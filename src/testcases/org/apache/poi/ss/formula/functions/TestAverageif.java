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

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/**
 * Test cases for AVERAGEIF()
 */
public final class TestAverageif extends TestCase {
	private static final NumberEval _30 = new NumberEval(30);
	private static final NumberEval _40 = new NumberEval(40);
	private static final NumberEval _50 = new NumberEval(50);
	private static final NumberEval _60 = new NumberEval(60);

	private static ValueEval invokeAverageif(int rowIx, int colIx, ValueEval...args) {
		return new Averageif().evaluate(args, new OperationEvaluationContext(null, null, 0, rowIx, colIx, null));
	}
	private static void confirmDouble(double expected, ValueEval actualEval) {
		if(!(actualEval instanceof NumericValueEval)) {
			throw new AssertionFailedError("Expected numeric result");
		}
		NumericValueEval nve = (NumericValueEval)actualEval;
		assertEquals(expected, nve.getNumberValue(), 0);
	}

	public void testBasic() {
		ValueEval[] arg0values = new ValueEval[] { _30, _30, _40, _40, _50, _50  };
		ValueEval[] arg2values = new ValueEval[] { _30, _40, _50, _60, _60, _60 };

		AreaEval arg0;
		AreaEval arg2;

		arg0 = EvalFactory.createAreaEval("A3:B5", arg0values);
		arg2 = EvalFactory.createAreaEval("D1:E3", arg2values);

		confirm(30.0, arg0, new NumberEval(30.0));
		confirm(35.0, arg0, new NumberEval(30.0), arg2);
		confirm(50.0, arg0, new StringEval(">45"));
		confirm(50.0, arg0, new StringEval(">=45"));
		confirm(50.0, arg0, new StringEval(">=50.0"));
		confirm(35.0, arg0, new StringEval("<45"));
		confirm(35.0, arg0, new StringEval("<=45"));
		confirm(35.0, arg0, new StringEval("<=40.0"));
		confirm(40.0, arg0, new StringEval("<>40.0"));
		confirm(40.0, arg0, new StringEval("=40.0"));
		

	}
	private static void confirm(double expectedResult, ValueEval...args) {
		confirmDouble(expectedResult, invokeAverageif(-1, -1, args));
	}


	/**
	 * test for bug observed near svn r882931
	 */
	public void testCriteriaArgRange() {
		ValueEval[] arg0values = new ValueEval[] { _50, _60, _50, _50, _50, _30,  };
		ValueEval[] arg1values = new ValueEval[] { _30, _40, _50, _60,  };

		AreaEval arg0;
		AreaEval arg1;
		ValueEval ve;

		arg0 = EvalFactory.createAreaEval("A3:B5", arg0values);
		arg1 = EvalFactory.createAreaEval("A2:D2", arg1values); // single row range

		ve = invokeAverageif(0, 2, arg0, arg1);  // invoking from cell C1
		if (ve instanceof NumberEval) {
			NumberEval ne = (NumberEval) ve;
			if (ne.getNumberValue() == 30.0) {
				throw new AssertionFailedError("identified error in AVERAGEIF - criteria arg not evaluated properly");
			}
		}

		confirmDouble(50, ve);

		arg0 = EvalFactory.createAreaEval("C1:D3", arg0values);
		arg1 = EvalFactory.createAreaEval("B1:B4", arg1values); // single column range

		ve = invokeAverageif(3, 0, arg0, arg1); // invoking from cell A4

		confirmDouble(60, ve);
	}
}
