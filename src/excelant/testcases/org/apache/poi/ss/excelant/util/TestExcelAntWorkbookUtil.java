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
package org.apache.poi.ss.excelant.util;

import java.util.List;

import junit.framework.TestCase;

import org.apache.poi.ss.examples.formula.CalculateMortgageFunction;
import org.apache.poi.ss.excelant.util.ExcelAntEvaluationResult;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.IFormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

public class TestExcelAntWorkbookUtil extends TestCase {
	
	private static final String mortgageCalculatorFileName =
		                                  "test-data/spreadsheet/excelant.xls" ;

	public void testStringConstructor() {
		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
				                                  mortgageCalculatorFileName ) ;
		
		assertNotNull( fixture ) ;
		
	}
	
	public void testAddFunction() {
		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
                mortgageCalculatorFileName ) ;

		assertNotNull( fixture ) ;
		
		fixture.addFunction("h2_ZFactor", new CalculateMortgageFunction() ) ;
		
		UDFFinder functions = fixture.getFunctions() ;
		
		assertNotNull( functions ) ;
	}

	public void testGetWorkbook() {
		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
                mortgageCalculatorFileName ) ;
		
		assertNotNull( fixture ) ;
		
		Workbook workbook = fixture.getWorkbook() ;
		
		assertNotNull( workbook ) ;
	}
	
	public void testFileName() {
		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
                mortgageCalculatorFileName ) ;
		
		assertNotNull( fixture ) ;

		String fileName = fixture.getFileName() ;
		
		assertNotNull( fileName ) ;
		
		assertEquals( mortgageCalculatorFileName, fileName ) ;
		
	}
	
	public void testGetEvaluator() {
		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
                mortgageCalculatorFileName ) ;
		
		IFormulaEvaluator evaluator = fixture.getEvaluator( 
				                                  mortgageCalculatorFileName ) ;
		
		assertNotNull( evaluator ) ;
		
		
 	}

	public void testEvaluateCell() {
		String cell = "'MortgageCalculator'!B4" ;
		double expectedValue = 790.79 ;
		double precision = 0.1 ;

		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
                mortgageCalculatorFileName ) ;

		ExcelAntEvaluationResult result = fixture.evaluateCell( cell, 
				                                                expectedValue, 
				                                                precision ) ;
		
		System.out.println(  result ) ;
		
		assertTrue( result.didTestPass() ) ;
	}
	
	public void testGetSheets() {
		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
                mortgageCalculatorFileName ) ;
		
		List<String> sheets = fixture.getSheets() ;
		
		assertNotNull( sheets ) ;
		assertEquals( sheets.size(), 3 ) ; 
	}
	
	public void testSetString() {
		String cell = "'MortgageCalculator'!C14" ;
		String cellValue = "testString" ;
		
		final ExcelAntWorkbookUtil fixture = new ExcelAntWorkbookUtil( 
                mortgageCalculatorFileName ) ;
		
		fixture.setStringValue( cell, cellValue ) ;
		
		String value = fixture.getCellAsString( cell ) ;
		
		assertNotNull( value ) ;
		
		assertEquals( cellValue, value ) ;
		
	}
}
