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

import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.IEvaluationCell;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Evaluates formula cells.<p/>
 *
 * For performance reasons, this class keeps a cache of all previously calculated intermediate
 * cell values.  Be sure to call {@link #clearAllCachedResultValues()} if any workbook cells are changed between
 * calls to evaluate~ methods on this class.
 *
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 * @author Josh Micich
 */
public final class HSSFFormulaEvaluator extends FormulaEvaluator {

	private final HSSFWorkbook _book;

	public HSSFFormulaEvaluator(HSSFWorkbook workbook) {
		this(workbook, null, null);
	}

	/**
	 * @param udfFinder pass <code>null</code> for default (AnalysisToolPak only)
	 */
	private HSSFFormulaEvaluator(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
		super(HSSFEvaluationWorkbook.create(workbook), workbook.getCreationHelper(), stabilityClassifier, udfFinder);
		this._book = workbook;
	}

	@Override
	protected IEvaluationCell getEvaluationCell(final Cell cell) {
		return new EvaluationCell(cell);
	}

	@Override
	protected Workbook getWorkbook() {
		return this._book;
	}

	/**
	 * @param stabilityClassifier used to optimise caching performance. Pass <code>null</code>
	 * for the (conservative) assumption that any cell may have its definition changed after
	 * evaluation begins.
	 * @param udfFinder pass <code>null</code> for default (AnalysisToolPak only)
	 */
	public static HSSFFormulaEvaluator create(HSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
		return new HSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
	}

	/**
	 * Coordinates several formula evaluators together so that formulas that involve external
	 * references can be evaluated.
	 * @param workbookNames the simple file names used to identify the workbooks in formulas
	 * with external links (for example "MyData.xls" as used in a formula "[MyData.xls]Sheet1!A1")
	 * @param evaluators all evaluators for the full set of workbooks required by the formulas.
	 */
	public static void setupEnvironment(String[] workbookNames, HSSFFormulaEvaluator[] evaluators) {
		final int n = evaluators.length;
		WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[n];
		for (int i = 0; i < n; i++) {
			wbEvals[i] = evaluators[i]._bookEvaluator;
		}
		CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
	}

	/**
	 * Loops over all cells in all sheets of the supplied
	 *  workbook.
	 * For cells that contain formulas, their formulas are
	 *  evaluated, and the results are saved. These cells
	 *  remain as formula cells.
	 * For cells that do not contain formulas, no changes
	 *  are made.
	 * This is a helpful wrapper around looping over all
	 *  cells, and calling evaluateFormulaCell on each one.
	 */
	public static void evaluateAllFormulaCells(HSSFWorkbook wb) {
		FormulaEvaluator.evaluateAllFormulaCells(wb);
	}
}
