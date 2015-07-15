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

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate;

/**
 * Implementation for the Excel function AVERAGEIF<p>
 *
 * Syntax : <br/>
 *  AVERAGEIF ( <b>range</b>, <b>criteria</b>, average_range ) <br/>
 *    <table border="0" cellpadding="1" cellspacing="0" summary="Parameter descriptions">
 *      <tr><th>range</th><td>The range over which criteria is applied.  Also used for addend values when the third parameter is not present</td></tr>
 *      <tr><th>criteria</th><td>The value or expression used to filter rows from <b>range</b></td></tr>
 *      <tr><th>average_range</th><td>Locates the top-left corner of the corresponding range of averages - values to be averaged (after being selected by the criteria)</td></tr>
 *    </table><br/>
 * </p>
 */
public final class Averageif implements FreeRefFunction {
    public static final FreeRefFunction instance = new Averageif();

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        final int len = args.length;
        if(len < 2 || len > 3) {
            return ErrorEval.VALUE_INVALID;
        }

        try {
            final AreaEval range;
            if (len == 2) {
                range = convertRangeArg(args[0]);
            } else {
                range = convertRangeArg(args[2]);
            }

            // collect pairs of ranges and criteria
            final AreaEval ae = convertRangeArg(args[0]);
            final I_MatchPredicate mp = Countif.createCriteriaPredicate(args[1], ec.getRowIndex(), ec.getColumnIndex());

            validateCriteriaRanges(ae, range);

            final double result = averageMatchingCells(ae, mp, range);
            return new NumberEval(result);
        } catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }

    /**
     * Verify that each <code>criteriaRanges</code> argument contains the same number of rows and columns
     * as the <code>ar</code> argument
     *
     * @throws EvaluationException if
     */
    private void validateCriteriaRanges(final AreaEval r, final AreaEval ar) throws EvaluationException {
        if(r.getHeight() != ar.getHeight() ||
           r.getWidth() != ar.getWidth() ) {
            throw EvaluationException.invalidValue();
        }
    }

	private static double averageMatchingCells(AreaEval aeRange, I_MatchPredicate mp, AreaEval aeAverage) throws EvaluationException {
		int height = aeRange.getHeight();
		int width = aeRange.getWidth();

		int count = 0;
		double result = 0.0;
		for (int r=0; r<height; r++) {
			for (int c=0; c<width; c++) {
				if (mp.matches(aeRange.getRelativeValue(r, c))) {
					result += accumulate(aeRange, mp, aeAverage, r, c);
					count++;
				}
			}
		}
		if (count <= 0) {
		    throw new EvaluationException(ErrorEval.DIV_ZERO);
		}
		return result / count;
	}

	private static double accumulate(AreaEval aeRange, I_MatchPredicate mp, AreaEval aeAverage, int relRowIndex,
			int relColIndex) {

		ValueEval addend = aeAverage.getRelativeValue(relRowIndex, relColIndex);
		if (addend instanceof NumberEval) {
			return ((NumberEval)addend).getNumberValue();
		}
		// everything else (including string and boolean values) counts as zero
		return 0.0;
	}

	private static AreaEval convertRangeArg(ValueEval eval) throws EvaluationException {
		if (eval instanceof AreaEval) {
			return (AreaEval) eval;
		}
		if (eval instanceof RefEval) {
			return ((RefEval)eval).offset(0, 0, 0, 0);
		}
		throw new EvaluationException(ErrorEval.VALUE_INVALID);
	}
}
