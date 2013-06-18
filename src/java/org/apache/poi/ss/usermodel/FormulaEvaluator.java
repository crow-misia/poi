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

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.udf.UDFFinder;

/**
 * Evaluates formula cells.<p/>
 * 
 * For performance reasons, this class keeps a cache of all previously calculated intermediate
 * cell values.  Be sure to call {@link #clearAllCachedResultValues()} if any workbook cells are changed between
 * calls to evaluate~ methods on this class.
 * 
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 * @author Josh Micich
 * @author Zenichi Amano
 */
public abstract class FormulaEvaluator {
    protected final WorkbookEvaluator _bookEvaluator;
    private final CreationHelper _helper;

    public FormulaEvaluator(final EvaluationWorkbook workbook, final CreationHelper helper, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        this._bookEvaluator = new WorkbookEvaluator(workbook, stabilityClassifier, udfFinder);
        this._helper = helper;
    }

    protected abstract EvaluationCell getEvaluationCell(final Cell cell);
    protected abstract Workbook getWorkbook();

    /**
     * Should be called whenever there are changes to input cells in the evaluated workbook.
     * Failure to call this method after changing cell values will cause incorrect behaviour
     * of the evaluate~ methods of this class
     */
    public final void clearAllCachedResultValues() {
        _bookEvaluator.clearAllCachedResultValues();
    }

    /**
     * Should be called to tell the cell value cache that the specified (value or formula) cell 
     * has changed.
     * Failure to call this method after changing cell values will cause incorrect behaviour
     * of the evaluate~ methods of this class
     */
    public final void notifySetFormula(final Cell cell) {
        _bookEvaluator.notifyUpdateCell(getEvaluationCell(cell));
    }
    /**
     * Should be called to tell the cell value cache that the specified cell has just become a
     * formula cell, or the formula text has changed 
     */
    public final void notifyDeleteCell(final Cell cell) {
        _bookEvaluator.notifyDeleteCell(getEvaluationCell(cell));
    }

    /**
     * Should be called to tell the cell value cache that the specified (value or formula) cell
     * has changed.
     * Failure to call this method after changing cell values will cause incorrect behaviour
     * of the evaluate~ methods of this class
     */
    public final void notifyUpdateCell(final Cell cell) {
        _bookEvaluator.notifyUpdateCell(getEvaluationCell(cell));
    }

    /**
    * Loops over all cells in all sheets of the associated workbook.
    * For cells that contain formulas, their formulas are evaluated, 
    *  and the results are saved. These cells remain as formula cells.
    * For cells that do not contain formulas, no changes are made.
    * This is a helpful wrapper around looping over all cells, and 
    *  calling evaluateFormulaCell on each one.
     */
    public final void evaluateAll() {
        evaluateAllFormulaCells(getWorkbook(), this);
    }
    
    public static void evaluateAllFormulaCells(final Workbook wb) {
        final FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        evaluateAllFormulaCells(wb, evaluator);
    }

    private static void evaluateAllFormulaCells(final Workbook wb, final FormulaEvaluator evaluator) {
        for(int i=0, n=wb.getNumberOfSheets(); i < n; i++) {
            Sheet sheet = wb.getSheetAt(i);

            for(final Row r : sheet) {
                for (final Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }
        }
    }

    /**
     * If cell contains a formula, the formula is evaluated and returned,
     * else the CellValue simply copies the appropriate cell value from
     * the cell and also its cell type. This method should be preferred over
     * evaluateInCell() when the call should not modify the contents of the
     * original cell.
     * @param cell
     */
    public final CellValue evaluate(final Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return CellValue.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_ERROR:
                return CellValue.getError(cell.getErrorCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return evaluateFormulaCellValue(cell);
            case Cell.CELL_TYPE_NUMERIC:
                return new CellValue(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return new CellValue(cell.getRichStringCellValue().getString());
            case Cell.CELL_TYPE_BLANK:
                return null;
        }
        throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
    }

    /**
     * If cell contains formula, it evaluates the formula,
     *  and saves the result of the formula. The cell
     *  remains as a formula cell.
     * Else if cell does not contain formula, this method leaves
     *  the cell unchanged.
     * Note that the type of the formula result is returned,
     *  so you know what kind of value is also stored with
     *  the formula.
     * <pre>
     * int evaluatedCellType = evaluator.evaluateFormulaCell(cell);
     * </pre>
     * Be aware that your cell will hold both the formula,
     *  and the result. If you want the cell replaced with
     *  the result of the formula, use {@link #evaluateInCell(Cell)}
     * @param cell The cell to evaluate
     * @return The type of the formula result (the cell's type remains as Cell.CELL_TYPE_FORMULA however)
     */
    public final int evaluateFormulaCell(Cell cell) {
        if (cell == null || cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
            return -1;
        }
        final CellValue cv = evaluateFormulaCellValue(cell);
        // cell remains a formula cell, but the cached value is changed
        setCellValue(cell, cv);
        return cv.getCellType();
    }

    /**
     * If cell contains formula, it evaluates the formula, and
     *  puts the formula result back into the cell, in place
     *  of the old formula.
     * Else if cell does not contain formula, this method leaves
     *  the cell unchanged.
     * Note that the same instance of Cell is returned to
     * allow chained calls like:
     * <pre>
     * int evaluatedCellType = evaluator.evaluateInCell(cell).getCellType();
     * </pre>
     * Be aware that your cell value will be changed to hold the
     *  result of the formula. If you simply want the formula
     *  value computed for you, use {@link #evaluateFormulaCell(Cell)}
     * @param cell
     */
    public final Cell evaluateInCell(final Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            final CellValue cv = evaluateFormulaCellValue(cell);
            setCellType(cell, cv); // cell will no longer be a formula cell
            setCellValue(cell, cv);
        }
        return cell;
    }

    /**
     * Whether to ignore missing references to external workbooks and
     * use cached formula results in the main workbook instead.
     * <p>
     * In some cases exetrnal workbooks referenced by formulas in the main workbook are not avaiable.
     * With this method you can control how POI handles such missing references:
     * <ul>
     *     <li>by default ignoreMissingWorkbooks=false and POI throws {@link org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment.WorkbookNotFoundException}
     *     if an external reference cannot be resolved</li>
     *     <li>if ignoreMissingWorkbooks=true then POI uses cached formula result
     *     that already exists in the main workbook</li>
     * </ul>
     *
     * @param ignore whether to ignore missing references to external workbooks
     */
    public final void setIgnoreMissingWorkbooks(final boolean ignore){
        _bookEvaluator.setIgnoreMissingWorkbooks(ignore);
    }

    /**
     * Perform detailed output of formula evaluation for next evaluation only?
     * Is for developer use only (also developers using POI for their XLS files).
     * Log-Level WARN is for basic info, INFO for detailed information. These quite
     * high levels are used because you have to explicitly enable this specific logging.
     
     * @param value whether to perform detailed output
     */
    public final void setDebugEvaluationOutputForNextEval(final boolean value) {
        _bookEvaluator.setDebugEvaluationOutputForNextEval(value);
    }

    /**
     * Returns a CellValue wrapper around the supplied ValueEval instance.
     */
    private CellValue evaluateFormulaCellValue(final Cell cell) {
        final ValueEval eval = _bookEvaluator.evaluate(getEvaluationCell(cell));
        if (eval instanceof NumberEval) {
            final NumberEval ne = (NumberEval) eval;
            return new CellValue(ne.getNumberValue());
        }
        if (eval instanceof BoolEval) {
            final BoolEval be = (BoolEval) eval;
            return CellValue.valueOf(be.getBooleanValue());
        }
        if (eval instanceof StringEval) {
            final StringEval ne = (StringEval) eval;
            return new CellValue(ne.getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval)eval).getErrorCode());
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }

    private static void setCellType(final Cell cell, final CellValue cv) {
        int cellType = cv.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_BOOLEAN:
            case Cell.CELL_TYPE_ERROR:
            case Cell.CELL_TYPE_NUMERIC:
            case Cell.CELL_TYPE_STRING:
                cell.setCellType(cellType);
                return;
            case Cell.CELL_TYPE_BLANK:
                // never happens - blanks eventually get translated to zero
            case Cell.CELL_TYPE_FORMULA:
                // this will never happen, we have already evaluated the formula
        }
        throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
    }

    private void setCellValue(final Cell cell, final CellValue cv) {
        final int cellType = cv.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_BOOLEAN:
                cell.setCellValue(cv.getBooleanValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                cell.setCellErrorValue(cv.getErrorValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellValue(cv.getNumberValue());
                break;
            case Cell.CELL_TYPE_STRING:
                cell.setCellValue(_helper.createRichTextString(cv.getStringValue()));
                break;
            case Cell.CELL_TYPE_BLANK:
                // never happens - blanks eventually get translated to zero
            case Cell.CELL_TYPE_FORMULA:
                // this will never happen, we have already evaluated the formula
            default:
                throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
        }
    }
}
