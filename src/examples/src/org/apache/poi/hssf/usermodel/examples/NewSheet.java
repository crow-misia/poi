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

package org.apache.poi.hssf.usermodel.examples;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.IOException;
import java.io.FileOutputStream;

/**
 * Creates a new workbook with a sheet that's been explicitly defined.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class NewSheet {
    public static void main(String[] args) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        wb.createSheet("new sheet");
        wb.createSheet(); // create with default name
        final String name = "second sheet";
        wb.setSheetName(1, WorkbookUtil.createSafeSheetName(name)); // setting sheet name later
        try (final FileOutputStream fileOut = new FileOutputStream("workbook.xls")) {
            wb.write(fileOut);
        }
    }
}
