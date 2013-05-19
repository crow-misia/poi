/*
 *  ====================================================================
 *	Licensed to the Apache Software Foundation (ASF) under one or more
 *	contributor license agreements.  See the NOTICE file distributed with
 *	this work for additional information regarding copyright ownership.
 *	The ASF licenses this file to You under the Apache License, Version 2.0
 *	(the "License"); you may not use this file except in compliance with
 *	the License.  You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 * ====================================================================
 */

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
 *  AVERAGEIF ( <b>average_range</b>, <b>criteria1</>[, <b>criteria_range1</b>]) <br/>
 *	<ul>
 *	  <li><b>average_range</b> Required. One or more cells to average, including numbers or names, ranges,
 *	  or cell references that contain numbers. Blank and text values are ignored.</li>
 *	  <li><b>criteria1</b> Required. The criteria in the form of a number, expression,
 *		cell reference, or text that define which cells in the criteria_range1
 *		argument will be added</li>
 *	  <li><b>criteria1_range</b> The first range in which
 *	  to evaluate the associated criteria.</li>
 *	</ul>
 * </p>
 *
 * @author Zenichi Amano (sumifs original by Yegor Kozlov)
 */
public final class Averageif implements FreeRefFunction {
	public static final FreeRefFunction instance = new Averageif();

	public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
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

	/**
	 *
	 * @param ranges  criteria ranges, each range must be of the same dimensions as <code>aeSum</code>
	 * @param predicates  array of predicates, a predicate for each value in <code>ranges</code>
	 * @param aeSum  the range to average
	 *
	 * @return the computed value
	 */
	private static double averageMatchingCells(final AreaEval aeRange, final I_MatchPredicate mp, final AreaEval aeAverage) {
		final int height = aeAverage.getHeight();
		final int width = aeAverage.getWidth();

		int count = 0;
		double result = 0.0;
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {

				if (mp.matches(aeRange.getRelativeValue(r, c))) {
					// average only if all of the corresponding criteria specified are true for that cell.
					result += accumulate(aeAverage, r, c);
					count++;
				}
			}
		}
		return result / count;
	}

	private static double accumulate(final AreaEval aeAverage, final int relRowIndex,
			final int relColIndex) {

		final ValueEval addend = aeAverage.getRelativeValue(relRowIndex, relColIndex);
		if (addend instanceof NumberEval) {
			return ((NumberEval)addend).getNumberValue();
		}
		// everything else (including string and boolean values) counts as zero
		return 0.0;
	}

	private static AreaEval convertRangeArg(final ValueEval eval) throws EvaluationException {
		if (eval instanceof AreaEval) {
			return (AreaEval) eval;
		}
		if (eval instanceof RefEval) {
			return ((RefEval)eval).offset(0, 0, 0, 0);
		}
		throw EvaluationException.invalidValue();
	}

}
