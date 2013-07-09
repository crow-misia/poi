/*
 *  ====================================================================
 *    Licensed to the Apache Software Foundation (ASF) under one or more
 *    contributor license agreements.  See the NOTICE file distributed with
 *    this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0
 *    (the "License"); you may not use this file except in compliance with
 *    the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * ====================================================================
 */

package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;

import java.util.ArrayList;

/**
 * @author Yegor Kozlov
 */
public final class XSSFConditionalFormatting implements ConditionalFormatting {
    private final CTConditionalFormatting _cf;
    private final XSSFWorkbook _wb;

    /*package*/ XSSFConditionalFormatting(XSSFWorkbook wb){
        this(wb, CTConditionalFormatting.Factory.newInstance());
    }

    /*package*/ XSSFConditionalFormatting(XSSFWorkbook wb, CTConditionalFormatting cf){
        _cf = cf;
        _wb = wb;
    }

    /*package*/  CTConditionalFormatting getCTConditionalFormatting(){
        return _cf;
    }

    /**
      * @return array of <tt>CellRangeAddress</tt>s. Never <code>null</code>
      */
     public CellRangeAddress[] getFormattingRanges(){
         ArrayList<CellRangeAddress> lst = new ArrayList<>();
         for (Object stRef : _cf.getSqref()) {
             String[] regions = stRef.toString().split(" ");
             for (int i = 0; i < regions.length; i++) {
                 lst.add(CellRangeAddress.valueOf(regions[i]));
             }
         }
         return lst.toArray(new CellRangeAddress[lst.size()]);
     }

     /**
      * Replaces an existing Conditional Formatting rule at position idx.
      * Excel allows to create up to 3 Conditional Formatting rules.
      * This method can be useful to modify existing  Conditional Formatting rules.
      *
      * @param idx position of the rule. Should be between 0 and 2.
      * @param cfRule - Conditional Formatting rule
      */
     public void setRule(int idx, ConditionalFormattingRule cfRule){
         XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule)cfRule;
         _cf.getCfRuleArray(idx).set(xRule.getCTCfRule());
     }

     /**
      * Add a Conditional Formatting rule.
      * Excel allows to create up to 3 Conditional Formatting rules.
      *
      * @param cfRule - Conditional Formatting rule
      */
     public void addRule(ConditionalFormattingRule cfRule){
        XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule)cfRule;
         _cf.addNewCfRule().set(xRule.getCTCfRule());
     }

     /**
      * @return the Conditional Formatting rule at position idx.
      */
     public XSSFConditionalFormattingRule getRule(int idx){
         return new XSSFConditionalFormattingRule(_wb, _cf.getCfRuleArray(idx));
     }

     /**
      * @return number of Conditional Formatting rules.
      */
     public int getNumberOfRules(){
         return _cf.sizeOfCfRuleArray();
     }
}
