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

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/**
 * An implementation of the Excel REPLACEB() function:<p/>
 * Replaces part of a text string based on the number of bytes
 * you specify, with another text string.<br/>
 *
 * <b>Syntax</b>:<br/>
 * <b>REPLACEB</b>(<b>oldText</b>, <b>startNum</b>, <b>numBytes</b>, <b>newText</b>)<p/>
 *
 * <b>oldText</b>  The text string containing characters to replace<br/>
 * <b>startNum</b> The position of the first character to replace (1-based)<br/>
 * <b>numBytes</b> The number of bytes to replace<br/>
 * <b>newText</b> The new text value to replace the removed section<br/>
 */
public final class ReplaceB extends Fixed4ArgFunction {

	public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1,
			final ValueEval arg2, final ValueEval arg3) {

		String oldStr;
        int startIx;
        int numBytes;
		String newStr;
		try {
			oldStr = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            startIx = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
            numBytes = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
			newStr = TextFunction.evaluateStringArg(arg3, srcRowIndex, srcColumnIndex);
		} catch (final EvaluationException e) {
			return e.getErrorEval();
		}

        if (startIx < 1 || numBytes < 0) {
			return ErrorEval.VALUE_INVALID;
		}

        // Note - for start_num arg, blank/zero causes error(#VALUE!),
        // but for num_chars causes empty string to be returned.
        if (startIx <= 0) {
            return ErrorEval.VALUE_INVALID;
        }
        if (numBytes < 0) {
            return ErrorEval.VALUE_INVALID;
        }
        final int endIx = startIx + numBytes;

        final int n = oldStr.length();
        int idx = 0;
        boolean padLeft = false;
        boolean padRight = false;
        int s = -1;
        int e = n;
        for (int i = 0; i < n; i++) {
            final char c = oldStr.charAt(i);
            // 半角
            if (c >= 0x00 && c <= 0xff) {
                idx++;
                if (s < 0 && idx >= startIx) {
                    s = i;
                }
                // end.
                if (idx >= endIx) {
                    e = i;
                    break;
                }
                // 全角
            } else {
                idx += 2;
                if (s < 0 && idx >= startIx) {
                    if (idx == startIx) {
                        padLeft = true;
                    }
                    s = i;
                }
                // end.
                if (idx >= endIx) {
                    padRight = (idx == endIx);
                    e = i + (padRight ? 1 : 0);
                    break;
                }
            }
        }

        return new StringEval(oldStr.substring(0, s) +
                (padLeft ? " " : "") +
                newStr +
                (padRight ? " " : "") +
                oldStr.substring(e));
	}
}
