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

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.util.list.DoubleArrayList;

/**
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 * This is the super class for all excel function evaluator
 * classes that take variable number of operands, and
 * where the order of operands does not matter
 */
public abstract class MultiOperandNumericFunction implements Function {

	private final boolean _isReferenceBoolCounted;
	private final boolean _isBlankCounted;

    protected MultiOperandNumericFunction(boolean isReferenceBoolCounted, boolean isBlankCounted) {
        _isReferenceBoolCounted = isReferenceBoolCounted;
        _isBlankCounted = isBlankCounted;
    }

	private static final int DEFAULT_MAX_NUM_OPERANDS = 30;

	public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {

		double d;
		try {
			double[] values = getNumberArray(args);
			d = evaluate(values);
		} catch (EvaluationException e) {
			return e.getErrorEval();
		}

		if (Double.isNaN(d) || Double.isInfinite(d))
			return ErrorEval.NUM_ERROR;

		return new NumberEval(d);
	}

	protected abstract double evaluate(double[] values) throws EvaluationException;

	/**
	 * Maximum number of operands accepted by this function.
	 * Subclasses may override to change default value.
	 */
	protected int getMaxNumOperands() {
		return DEFAULT_MAX_NUM_OPERANDS;
	}

	/**
	 * Returns a double array that contains values for the numeric cells
	 * from among the list of operands. Blanks and Blank equivalent cells
	 * are ignored. Error operands or cells containing operands of type
	 * that are considered invalid and would result in #VALUE! error in
	 * excel cause this function to return <code>null</code>.
	 *
	 * @return never <code>null</code>
	 */
	protected final double[] getNumberArray(ValueEval[] operands) throws EvaluationException {
		if (operands.length > getMaxNumOperands()) {
			throw EvaluationException.invalidValue();
		}
		DoubleArrayList retval = new DoubleArrayList();

		for (int i=0, iSize=operands.length; i<iSize; i++) {
			collectValues(operands[i], retval);
		}
		return retval.toArray();
	}

    /**
     *  Whether to count nested subtotals.
     */
    public boolean isSubtotalCounted(){
        return true;
    }

	/**
	 * Collects values from a single argument
	 */
	private void collectValues(ValueEval operand, DoubleArrayList temp) throws EvaluationException {

		if (operand instanceof TwoDEval) {
			TwoDEval ae = (TwoDEval) operand;
			int width = ae.getWidth();
			int height = ae.getHeight();
			for (int rrIx=0; rrIx<height; rrIx++) {
				for (int rcIx=0; rcIx<width; rcIx++) {
					ValueEval ve = ae.getValue(rrIx, rcIx);
                    if(!isSubtotalCounted() && ae.isSubTotal(rrIx, rcIx)) continue;
                    collectValue(ve, true, temp);
				}
			}
			return;
		}
		if (operand instanceof RefEval) {
			RefEval re = (RefEval) operand;
			collectValue(re.getInnerValueEval(), true, temp);
			return;
		}
		collectValue(operand, false, temp);
	}
	private void collectValue(ValueEval ve, boolean isViaReference, DoubleArrayList temp)  throws EvaluationException {
		if (ve == null) {
			throw new IllegalArgumentException("ve must not be null");
		}
		if (ve instanceof NumberEval) {
			NumberEval ne = (NumberEval) ve;
			temp.add(ne.getNumberValue());
			return;
		}
		if (ve instanceof ErrorEval) {
			throw new EvaluationException((ErrorEval) ve);
		}
		if (ve instanceof StringEval) {
			if (isViaReference) {
				// ignore all ref strings
				return;
			}
			String s = ((StringEval) ve).getStringValue();
			Double d = OperandResolver.parseDouble(s);
			if(d == null) {
				throw new EvaluationException(ErrorEval.VALUE_INVALID);
			}
			temp.add(d.doubleValue());
			return;
		}
		if (ve instanceof BoolEval) {
			if (!isViaReference || _isReferenceBoolCounted) {
				BoolEval boolEval = (BoolEval) ve;
				temp.add(boolEval.getNumberValue());
			}
			return;
		}
		if (ve == BlankEval.instance) {
			if (_isBlankCounted) {
				temp.add(0.0);
			}
			return;
		}
		throw new RuntimeException("Invalid ValueEval type passed for conversion: ("
				+ ve.getClass() + ")");
	}
}
