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

package org.apache.poi.xssf.usermodel.helpers;

import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.util.CTColComparator;
import org.apache.poi.xssf.util.NumericRanges;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

/**
 * Helper class for dealing with the Column settings on
 *  a CTWorksheet (the data part of a sheet).
 * Note - within POI, we use 0 based column indexes, but
 *  the column definitions in the XML are 1 based!
 */
public final class ColumnHelper {

    private final CTWorksheet worksheet;

    public ColumnHelper(CTWorksheet worksheet) {
        super();
        this.worksheet = worksheet;
        cleanColumns();
    }

    public void cleanColumns() {
        CTCols newCols = CTCols.Factory.newInstance();
        final List<CTCols> colsArray = worksheet.getColsList();
        final int n = colsArray.size();
        for (final CTCols cols : colsArray) {
            for (CTCol col : cols.getColList()) {
                newCols = addCleanColIntoCols(newCols, col);
            }
        }
        for (int y = n - 1; y >= 0; y--) {
            worksheet.removeCols(y);
        }
        worksheet.addNewCols();
        worksheet.setColsArray(0, newCols);
    }

    public static void sortColumns(CTCols newCols) {
        final List<CTCol> colArray = newCols.getColList();
        Collections.sort(colArray, CTColComparator.INSTANCE);
        newCols.setColArray(colArray.toArray(new CTCol[colArray.size()]));
    }

    public CTCol cloneCol(CTCols cols, CTCol col) {
        CTCol newCol = cols.addNewCol();
        newCol.setMin(col.getMin());
        newCol.setMax(col.getMax());
        setColumnAttributes(col, newCol);
        return newCol;
    }

    /**
     * Returns the Column at the given 0 based index
     */
    public CTCol getColumn(long index, boolean splitColumns) {
    	return getColumn1Based(index+1, splitColumns);
    }
    /**
     * Returns the Column at the given 1 based index.
     * POI default is 0 based, but the file stores
     *  as 1 based.
     */
    public CTCol getColumn1Based(long index1, boolean splitColumns) {
        CTCols colsArray = worksheet.getColsArray(0);
		for (final CTCol col : colsArray.getColList()) {
			if (col.getMin() <= index1 && col.getMax() >= index1) {
				if (splitColumns) {
					if (col.getMin() < index1) {
						insertCol(colsArray, col.getMin(), (index1 - 1), new CTCol[]{col});
					}
					if (col.getMax() > index1) {
						insertCol(colsArray, (index1 + 1), col.getMax(), new CTCol[]{col});
					}
					col.setMin(index1);
					col.setMax(index1);
				}
                return col;
            }
        }
        return null;
    }

    public CTCols addCleanColIntoCols(CTCols cols, CTCol col) {
        boolean colOverlaps = false;
        int sizeOfColArray = cols.sizeOfColArray();
        for (int i = 0; i < sizeOfColArray; i++) {
            CTCol ithCol = cols.getColArray(i);
            long[] range1 = { ithCol.getMin(), ithCol.getMax() };
            long[] range2 = { col.getMin(), col.getMax() };
            long[] overlappingRange = NumericRanges.getOverlappingRange(range1,
                    range2);
            int overlappingType = NumericRanges.getOverlappingType(range1,
                    range2);
            // different behavior required for each of the 4 different
            // overlapping types
            if (overlappingType == NumericRanges.OVERLAPS_1_MINOR) {
                ithCol.setMax(overlappingRange[0] - 1);
                insertCol(cols, overlappingRange[0],
                        overlappingRange[1], new CTCol[] { ithCol, col });
                insertCol(cols, (overlappingRange[1] + 1), col
                        .getMax(), new CTCol[] { col });
                i += 2;
            } else if (overlappingType == NumericRanges.OVERLAPS_2_MINOR) {
                ithCol.setMin(overlappingRange[1] + 1);
                insertCol(cols, overlappingRange[0],
                        overlappingRange[1], new CTCol[] { ithCol, col });
                insertCol(cols, col.getMin(),
                        (overlappingRange[0] - 1), new CTCol[] { col });
                i += 2;
            } else if (overlappingType == NumericRanges.OVERLAPS_2_WRAPS) {
                setColumnAttributes(col, ithCol);
                if (col.getMin() != ithCol.getMin()) {
                    insertCol(cols, col.getMin(), (ithCol
                            .getMin() - 1), new CTCol[] { col });
                    i++;
                }
                if (col.getMax() != ithCol.getMax()) {
                    insertCol(cols, (ithCol.getMax() + 1),
                            col.getMax(), new CTCol[] { col });
                    i++;
                }
            } else if (overlappingType == NumericRanges.OVERLAPS_1_WRAPS) {
                if (col.getMin() != ithCol.getMin()) {
                    insertCol(cols, ithCol.getMin(), (col
                            .getMin() - 1), new CTCol[] { ithCol });
                    i++;
                }
                if (col.getMax() != ithCol.getMax()) {
                    insertCol(cols, (col.getMax() + 1),
                            ithCol.getMax(), new CTCol[] { ithCol });
                    i++;
                }
                ithCol.setMin(overlappingRange[0]);
                ithCol.setMax(overlappingRange[1]);
                setColumnAttributes(col, ithCol);
            }
            if (overlappingType != NumericRanges.NO_OVERLAPS) {
                colOverlaps = true;
            }
        }
        if (!colOverlaps) {
            cloneCol(cols, col);
        }
        sortColumns(cols);
        return cols;
    }

    /*
     * Insert a new CTCol at position 0 into cols, setting min=min, max=max and
     * copying all the colsWithAttributes array cols attributes into newCol
     */
    private CTCol insertCol(CTCols cols, long min, long max,
        CTCol[] colsWithAttributes) {
        if(!columnExists(cols,min,max)){
                CTCol newCol = cols.insertNewCol(0);
                newCol.setMin(min);
                newCol.setMax(max);
                for (CTCol col : colsWithAttributes) {
                        setColumnAttributes(col, newCol);
                }
                return newCol;
        }
        return null;
    }

    /**
     * Does the column at the given 0 based index exist
     *  in the supplied list of column definitions?
     */
    public boolean columnExists(CTCols cols, long index) {
    	return columnExists1Based(cols, index+1);
    }
    private boolean columnExists1Based(CTCols cols, long index1) {
        for (final CTCol col : cols.getColList()) {
            if (col.getMin() == index1) {
                return true;
            }
        }
        return false;
    }

    public void setColumnAttributes(CTCol fromCol, CTCol toCol) {
    	if(fromCol.isSetBestFit()) toCol.setBestFit(fromCol.getBestFit());
        if(fromCol.isSetCustomWidth()) toCol.setCustomWidth(fromCol.getCustomWidth());
        if(fromCol.isSetHidden()) toCol.setHidden(fromCol.getHidden());
        if(fromCol.isSetStyle()) toCol.setStyle(fromCol.getStyle());
        if(fromCol.isSetWidth()) toCol.setWidth(fromCol.getWidth());
        if(fromCol.isSetCollapsed()) toCol.setCollapsed(fromCol.getCollapsed());
        if(fromCol.isSetPhonetic()) toCol.setPhonetic(fromCol.getPhonetic());
        if(fromCol.isSetOutlineLevel()) toCol.setOutlineLevel(fromCol.getOutlineLevel());
        toCol.setCollapsed(fromCol.isSetCollapsed());
    }

    public void setColBestFit(long index, boolean bestFit) {
        CTCol col = getOrCreateColumn1Based(index+1, false);
        col.setBestFit(bestFit);
    }
    public void setCustomWidth(long index, boolean bestFit) {
        CTCol col = getOrCreateColumn1Based(index+1, true);
        col.setCustomWidth(bestFit);
    }

    public void setColWidth(long index, double width) {
        CTCol col = getOrCreateColumn1Based(index+1, true);
        col.setWidth(width);
    }

    public void setColHidden(long index, boolean hidden) {
        CTCol col = getOrCreateColumn1Based(index+1, true);
        col.setHidden(hidden);
    }

    /**
     * Return the CTCol at the given (0 based) column index,
     *  creating it if required.
     */
    protected CTCol getOrCreateColumn1Based(long index1, boolean splitColumns) {
        CTCol col = getColumn1Based(index1, splitColumns);
        if (col == null) {
            col = worksheet.getColsArray(0).addNewCol();
            col.setMin(index1);
            col.setMax(index1);
        }
        return col;
    }

	public void setColDefaultStyle(long index, CellStyle style) {
		setColDefaultStyle(index, style.getIndex());
	}
	
	public void setColDefaultStyle(long index, int styleId) {
		CTCol col = getOrCreateColumn1Based(index+1, true);
		col.setStyle(styleId);
	}
	
	// Returns -1 if no column is found for the given index
	public int getColDefaultStyle(long index) {
		if (getColumn(index, false) != null) {
			return (int) getColumn(index, false).getStyle();
		}
		return -1;
	}

	private boolean columnExists(CTCols cols, long min, long max) {
	    for (final CTCol t : cols.getColList()) {
	        if (t.getMin() == min && t.getMax() == max) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public int getIndexOfColumn(CTCols cols, CTCol col) {
		int i = 0;
	    for (final CTCol t : cols.getColList()) {
	        if (t.getMin() == col.getMin() && t.getMax() == col.getMax()) {
	            return i;
	        }
	        i++;
	    }
	    return -1;
	}
}
