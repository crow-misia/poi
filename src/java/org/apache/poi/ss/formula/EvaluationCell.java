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

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.IEvaluationCell;
import org.apache.poi.ss.formula.IEvaluationSheet;
import org.apache.poi.ss.usermodel.Cell;
/**
 * HSSF wrapper for a cell under evaluation
 * 
 * @author Josh Micich
 */
public final class EvaluationCell implements IEvaluationCell {

	private final IEvaluationSheet _evalSheet;
	private final Cell _cell;

	public EvaluationCell(Cell cell, IEvaluationSheet evalSheet) {
		_cell = cell;
		_evalSheet = evalSheet;
	}
	public EvaluationCell(Cell cell) {
		this(cell, new EvaluationSheet(cell.getSheet()));
	}
	public Object getIdentityKey() {
		// save memory by just using the cell itself as the identity key
		// Note - this assumes HSSFCell has not overridden hashCode and equals
		return _cell;
	}

	public Cell getCell() {
		return _cell;
	}
	public boolean getBooleanCellValue() {
		return _cell.getBooleanCellValue();
	}
	public int getCellType() {
		return _cell.getCellType();
	}
	public int getColumnIndex() {
		return _cell.getColumnIndex();
	}
	public int getErrorCellValue() {
		return _cell.getErrorCellValue();
	}
	public double getNumericCellValue() {
		return _cell.getNumericCellValue();
	}
	public int getRowIndex() {
		return _cell.getRowIndex();
	}
	public IEvaluationSheet getSheet() {
		return _evalSheet;
	}
	public String getStringCellValue() {
		return _cell.getRichStringCellValue().getString();
	}
	public int getCachedFormulaResultType() {
		return _cell.getCachedFormulaResultType();
	}
}
