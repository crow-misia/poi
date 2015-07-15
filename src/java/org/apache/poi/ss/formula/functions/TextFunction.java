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

import java.util.regex.Pattern;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 * @author Josh Micich
 * @author Stephen Wolke (smwolke at geistig.com)
 */
public abstract class TextFunction implements Function {
	protected static final DataFormatter formatter = new DataFormatter();
   protected static final String EMPTY_STRING = "";

	protected static final String evaluateStringArg(ValueEval eval, int srcRow, int srcCol) throws EvaluationException {
		ValueEval ve = OperandResolver.getSingleValue(eval, srcRow, srcCol);
		return OperandResolver.coerceValueToString(ve);
	}
	protected static final int evaluateIntArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
		ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
		return OperandResolver.coerceValueToInt(ve);
	}
	
	protected static final double evaluateDoubleArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
		ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
		return OperandResolver.coerceValueToDouble(ve);
	}

	public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
		try {
			return evaluateFunc(args, srcCellRow, srcCellCol);
		} catch (EvaluationException e) {
			return e.getErrorEval();
		}
	}

	protected abstract ValueEval evaluateFunc(ValueEval[] args, int srcCellRow, int srcCellCol) throws EvaluationException;

	/* ---------------------------------------------------------------------- */

	private static abstract class SingleArgTextFunc extends Fixed1ArgFunction {

		protected SingleArgTextFunc() {
			// no fields to initialise
		}
		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
			String arg;
			try {
				arg = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
			} catch (EvaluationException e) {
				return e.getErrorEval();
			}
			return evaluate(arg);
		}
		protected abstract ValueEval evaluate(String arg);
	}

    /**
     * Returns the character specified by a number.
     */
    public static final Function CHAR = new Fixed1ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            int arg;
            try {
                arg = evaluateIntArg(arg0, srcRowIndex, srcColumnIndex);
                if (arg < 0 || arg >= 256) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }

            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new StringEval(String.valueOf((char)arg));
        }
    };

	public static final Function LEN = new SingleArgTextFunc() {
		protected ValueEval evaluate(String arg) {
			return new NumberEval(arg.length());
		}
	};
	public static final Function LOWER = new SingleArgTextFunc() {
		protected ValueEval evaluate(String arg) {
			return new StringEval(arg.toLowerCase());
		}
	};
	public static final Function UPPER = new SingleArgTextFunc() {
		protected ValueEval evaluate(String arg) {
			return new StringEval(arg.toUpperCase());
		}
	};

	/**
	 * Implementation of the PROPER function:
     * Normalizes all words (separated by non-word characters) by
     * making the first letter upper and the rest lower case.
	 */
	public static final Function PROPER = new SingleArgTextFunc() {
	    final Pattern nonAlphabeticPattern = Pattern.compile("\\P{IsL}");
		protected ValueEval evaluate(String text) {
			StringBuilder sb = new StringBuilder();
			boolean shouldMakeUppercase = true;
			String lowercaseText = text.toLowerCase();
			String uppercaseText = text.toUpperCase();
			for(int i = 0; i < text.length(); ++i) {
				if (shouldMakeUppercase) {
					sb.append(uppercaseText.charAt(i));
				}
				else {
					sb.append(lowercaseText.charAt(i));
				}
				shouldMakeUppercase = nonAlphabeticPattern.matcher(text.subSequence(i, i + 1)).matches();
			}
			return new StringEval(sb.toString());
		}
	};

	/**
	 * An implementation of the TRIM function:
	 * Removes leading and trailing spaces from value if evaluated operand
	 *  value is string.
	 * Author: Manda Wilson &lt; wilson at c bio dot msk cc dot org &gt;
	 */
	public static final Function TRIM = new SingleArgTextFunc() {
		protected ValueEval evaluate(String arg) {
			return new StringEval(arg.trim());
		}
	};
	
	/**
	 * An implementation of the CLEAN function:
	 * In Excel, the Clean function removes all non-printable characters from a string.
     *
	 * Author: Aniket Banerjee(banerjee@google.com)
	 */
    public static final Function CLEAN = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < arg.length(); i++) {
                char c = arg.charAt(i);
                if (isPrintable(c)) {
                    result.append(c);
                }
            }
            return new StringEval(result.toString());
        }

        /**
         * From Excel docs: The CLEAN function was designed to remove the first 32 nonprinting characters
         * in the 7-bit ASCII code (values 0 through 31) from text. In the Unicode character set,
         * there are additional nonprinting characters (values 127, 129, 141, 143, 144, and 157). By itself,
         * the CLEAN function does not remove these additional  nonprinting characters. To do this task,
         * use the SUBSTITUTE function to replace the higher value Unicode characters with the 7-bit ASCII
         * characters for which the TRIM and CLEAN functions were designed.
         *
         * @param c the character to test
         * @return  whether the character is printable
         */
        private boolean isPrintable(char c){
            int charCode = c;
            return charCode >= 32;
        }
    };

    /**
	 * An implementation of the MID function<br/>
	 * MID returns a specific number of
	 * characters from a text string, starting at the specified position.<p/>
	 *
	 * <b>Syntax<b>:<br/> <b>MID</b>(<b>text</b>, <b>start_num</b>,
	 * <b>num_chars</b>)<br/>
	 *
	 * Author: Manda Wilson &lt; wilson at c bio dot msk cc dot org &gt;
	 */
	public static final Function MID = new Fixed3ArgFunction() {

		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0,
				ValueEval arg1, ValueEval arg2) {
			String text;
			int startCharNum;
			int numChars;
			try {
				text = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
				startCharNum = evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
				numChars = evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
			} catch (EvaluationException e) {
				return e.getErrorEval();
			}
			int startIx = startCharNum - 1; // convert to zero-based

			// Note - for start_num arg, blank/zero causes error(#VALUE!),
			// but for num_chars causes empty string to be returned.
			if (startIx < 0) {
				return ErrorEval.VALUE_INVALID;
			}
			if (numChars < 0) {
				return ErrorEval.VALUE_INVALID;
			}
			int len = text.length();
			if (numChars < 0 || startIx > len) {
				return new StringEval("");
			}
			int endIx = Math.min(startIx + numChars, len);
			String result = text.substring(startIx, endIx);
			return new StringEval(result);
		}
	};

	private static final class LeftRight extends Var1or2ArgFunction {
		private static final ValueEval DEFAULT_ARG1 = new NumberEval(1.0);
		private final boolean _isLeft;
		protected LeftRight(boolean isLeft) {
			_isLeft = isLeft;
		}
		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
			return evaluate(srcRowIndex, srcColumnIndex, arg0, DEFAULT_ARG1);
		}
		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0,
				ValueEval arg1) {
			String arg;
			int index;
			try {
				arg = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
				index = evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
			} catch (EvaluationException e) {
				return e.getErrorEval();
			}
			
			if(index < 0) {
				return ErrorEval.VALUE_INVALID;
			}
			
			String result;
			if (_isLeft) {
				result = arg.substring(0, Math.min(arg.length(), index));
			} else {
				result = arg.substring(Math.max(0, arg.length()-index));
			}
			return new StringEval(result);
		}
	}

	public static final Function LEFT = new LeftRight(true);
	public static final Function RIGHT = new LeftRight(false);

	public static final Function CONCATENATE = new Function() {

		public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
			StringBuilder sb = new StringBuilder();
			for (int i=0, iSize=args.length; i<iSize; i++) {
				try {
					sb.append(evaluateStringArg(args[i], srcRowIndex, srcColumnIndex));
				} catch (EvaluationException e) {
					return e.getErrorEval();
				}
			}
			return new StringEval(sb.toString());
		}
	};

	public static final Function EXACT = new Fixed2ArgFunction() {

		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0,
				ValueEval arg1) {
			String s0;
			String s1;
			try {
				s0 = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
				s1 = evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
			} catch (EvaluationException e) {
				return e.getErrorEval();
			}
			return BoolEval.valueOf(s0.equals(s1));
		}
	};

	/**
	 * An implementation of the TEXT function<br/>
	 * TEXT returns a number value formatted with the given number formatting string. 
	 * This function is not a complete implementation of the Excel function, but
	 *  handles most of the common cases. All work is passed down to 
	 *  {@link DataFormatter} to be done, as this works much the same as the
	 *  display focused work that that does. 
	 *
	 * <b>Syntax<b>:<br/> <b>TEXT</b>(<b>value</b>, <b>format_text</b>)<br/>
	 */
	public static final Function TEXT = new Fixed2ArgFunction() {

		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
			double s0;
			String s1;
			try {
				s0 = evaluateDoubleArg(arg0, srcRowIndex, srcColumnIndex);
				s1 = evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
			} catch (EvaluationException e) {
				return e.getErrorEval();
			}
			
			try {
            // Ask DataFormatter to handle the String for us
			   String formattedStr = formatter.formatRawCellContents(s0, -1, s1);
				return new StringEval(formattedStr);
			} catch (Exception e) {
				return ErrorEval.VALUE_INVALID;
			}
		}
	};
	
	private static final class SearchFind extends Var2or3ArgFunction {

		private final boolean _isCaseSensitive;

		public SearchFind(boolean isCaseSensitive) {
			_isCaseSensitive = isCaseSensitive;
		}
		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
			try {
				String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
				String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
				return eval(haystack, needle, 0);
			} catch (EvaluationException e) {
				return e.getErrorEval();
			}
		}
		public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1,
				ValueEval arg2) {
			try {
				String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
				String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
				// evaluate third arg and convert from 1-based to 0-based index
				int startpos = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex) - 1;
				if (startpos < 0) {
					return ErrorEval.VALUE_INVALID;
				}
				return eval(haystack, needle, startpos);
			} catch (EvaluationException e) {
				return e.getErrorEval();
			}
		}
		private ValueEval eval(String haystack, String needle, int startIndex) {
			int result;
			if (_isCaseSensitive) {
				result = haystack.indexOf(needle, startIndex);
			} else {
				result = haystack.toUpperCase().indexOf(needle.toUpperCase(), startIndex);
			}
			if (result == -1) {
				return ErrorEval.VALUE_INVALID;
			}
			return new NumberEval(result + 1);
		}
	}
	/**
	 * Implementation of the FIND() function.<p/>
	 *
	 * <b>Syntax</b>:<br/>
	 * <b>FIND</b>(<b>find_text</b>, <b>within_text</b>, start_num)<p/>
	 *
	 * FIND returns the character position of the first (case sensitive) occurrence of
	 * <tt>find_text</tt> inside <tt>within_text</tt>.  The third parameter,
	 * <tt>start_num</tt>, is optional (default=1) and specifies where to start searching
	 * from.  Character positions are 1-based.<p/>
	 *
	 * Author: Torstein Tauno Svendsen (torstei@officenet.no)
	 */
	public static final Function FIND = new SearchFind(true);
	/**
	 * Implementation of the FIND() function.<p/>
	 *
	 * <b>Syntax</b>:<br/>
	 * <b>SEARCH</b>(<b>find_text</b>, <b>within_text</b>, start_num)<p/>
	 *
	 * SEARCH is a case-insensitive version of FIND()
	 */
	public static final Function SEARCH = new SearchFind(false);

    /**
     * An implementation of the MIDB function<br/>
     * MIDB returns a specific number of
     * bytes from a text string, starting at the specified position.<p/>
     *
     * <b>Syntax<b>:<br/> <b>MIDB</b>(<b>text</b>, <b>start_num</b>, <b>num_bytes</b>)
     */
    public static final Function MIDB = new Fixed3ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
            String text;
            int startIx;
            int numBytes;
            try {
                text = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                startIx = evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                numBytes = evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
            } catch (final EvaluationException e) {
                return e.getErrorEval();
            }

            // Note - for start_num arg, blank/zero causes error(#VALUE!),
            // but for num_bytes causes empty string to be returned.
            if (startIx <= 0) {
                return ErrorEval.VALUE_INVALID;
            }
            if (numBytes < 0) {
                return ErrorEval.VALUE_INVALID;
            }

            final int endIx = startIx + numBytes;
            final int n = text.length();
            int idx = 0;
            boolean padLeft = false;
            boolean padRight = false;
            int s = -1;
            int e = n;
            for (int i = 0; i < n; i++) {
                final char c = text.charAt(i);
                if (c >= 0x00 && c <= 0xff) {
                    // single-byte character
                    idx++;
                    if (s < 0 && idx >= startIx) {
                        s = i;
                    }
                    // end.
                    if (idx >= endIx) {
                        e = i;
                        break;
                    }
                } else {
                    // double-byte character
                    idx += 2;
                    if (s < 0 && idx >= startIx) {
                        if (idx == startIx) {
                            padLeft = true;
                            s = i + 1;
                        } else {
                            s = i;
                        }
                    }
                    // end.
                    if (idx >= endIx) {
                        padRight = (idx == endIx);
                        e = i;
                        break;
                    }
                }
            }

            String result;
            if (e == 0) {
                result = (padLeft ? " " : EMPTY_STRING);
            } else if (s < 0 || e < 0) {
                result = (padLeft ? " " : EMPTY_STRING) + (padRight ? " " : EMPTY_STRING);
            } else {
                result = (padLeft ? " " : EMPTY_STRING) + text.substring(s, e) + (padRight ? " " : EMPTY_STRING);
            }
            return new StringEval(result);
        }
    };
    /**
     * Implementation of the LENB() function.<p/>
     *
     * <b>Syntax</b>:<br/>
     * <b>LENB</b>(<b>text</b>)<p/>
     *
     * LENB is byte count version of LEN()
     */
    public static final Function LENB = new SingleArgTextFunc() {
        protected ValueEval evaluate(String arg) {
            final int n = arg.length();
            int l = 0;
            for (int i = 0; i < n; i++) {
                final char c = arg.charAt(i);
                if (c >= 0x00 && c <= 0xff) {
                    l++;
                } else {
                    l += 2;
                }
            }
            return new NumberEval(l);
        }
    };
    /**
     * Implementation of the LEFTB() function.<p/>
     *
     * <b>Syntax</b>:<br/>
     * <b>LEFTB</b>(<b>text</b>, <b>bytes</b>)<p/>
     *
     * LEFTB is byte count version of LEFT()
     */
    public static final Function LEFTB = new Var1or2ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            return evaluate(srcRowIndex, srcColumnIndex, arg0, NumberEval.ONE);
        }

        @Override
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            String text;
            int numBytes;
            try {
                text = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                numBytes = evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
            } catch (final EvaluationException e) {
                return e.getErrorEval();
            }
            final int endIx = 1 + numBytes;

            if (numBytes < 0) {
                return ErrorEval.VALUE_INVALID;
            }
            if (numBytes == 0) {
                return new StringEval("");
            }

            final int n = text.length();
            int idx = 0;
            boolean padRight = false;
            int e = n;
            for (int i = 0; i < n; i++) {
                final char c = text.charAt(i);
                if (c >= 0x00 && c <= 0xff) {
                    // single-byte character
                    idx++;
                    // end.
                    if (idx >= endIx) {
                        e = i;
                        break;
                    }
                } else {
                    // double-byte character
                    idx += 2;
                    // end.
                    if (idx >= endIx) {
                        padRight = (idx == endIx);
                        e = i;
                        break;
                    }
                }
            }

            return new StringEval(text.substring(0, e) + (padRight ? " " : EMPTY_STRING));
        }
    };

    /**
     * Implementation of the RIGHTB() function.<p/>
     *
     * <b>Syntax</b>:<br/>
     * <b>RIGHTB</b>(<b>text</b>, <b>bytes</b>)<p/>
     *
     * RIGHTB is byte count version of RIGHT()
     */
    public static final Function RIGHTB = new Var1or2ArgFunction() {
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            return evaluate(srcRowIndex, srcColumnIndex, arg0, NumberEval.ONE);
        }

        @Override
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            String text;
            int numBytes;
            try {
                text = evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                numBytes = evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
            } catch (final EvaluationException e) {
                return e.getErrorEval();
            }
            final int endIx = 1 + numBytes;

            if (numBytes < 0) {
                return ErrorEval.VALUE_INVALID;
            }
            if (numBytes == 0) {
                return new StringEval("");
            }

            final int n = text.length();
            int idx = 0;
            boolean padLeft = false;
            int s = 0;
            for (int i = n - 1; i > 0; i--) {
                final char c = text.charAt(i);
                if (c >= 0x00 && c <= 0xff) {
                    // single-byte character
                    idx++;
                    // end.
                    if (idx >= endIx) {
                        s = i + 1;
                        break;
                    }
                } else {
                    // double-byte character
                    idx += 2;
                    // end.
                    if (idx >= endIx) {
                        padLeft = (idx == endIx);
                        s = i + 1;
                        break;
                    }
                }
            }

            return new StringEval((padLeft ? " " : EMPTY_STRING) + text.substring(s, n));
        }
    };
    private static final class SearchFindB extends Var2or3ArgFunction {
        private final boolean _isCaseSensitive;

        public SearchFindB(boolean isCaseSensitive) {
            _isCaseSensitive = isCaseSensitive;
        }
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            try {
                String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                return eval(haystack, needle, 0);
            } catch (final EvaluationException e) {
                return e.getErrorEval();
            }
        }
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
            try {
                String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                // evaluate third arg and convert from 1-based to 0-based index
                int startpos = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex) - 1;
                if (startpos < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                return eval(haystack, needle, startpos);
            } catch (final EvaluationException e) {
                return e.getErrorEval();
            }
        }
        private ValueEval eval(String haystack, String needle, int startIndex) {
            int result;
            if (_isCaseSensitive) {
                result = haystack.indexOf(needle, startIndex);
            } else {
                result = haystack.toUpperCase().indexOf(needle.toUpperCase(), startIndex);
            }
            if (result == -1) {
                return ErrorEval.VALUE_INVALID;
            }

            int idx = 1;
            for (int i = 0; i < result; i++) {
                final char c = haystack.charAt(i);
                if (c >= 0x00 && c <= 0xff) {
                    idx++;
                } else {
                    idx += 2;
                }
            }

            return new NumberEval(idx);
        }
    }
    /**
     * Implementation of the FINDB() function.<p/>
     *
     * <b>Syntax</b>:<br/>
     * <b>FINDB</b>(<b>find_text</b>, <b>within_text</b>, start_num)<p/>
     * 
     * FINDB is byte count version of FIND()
     */
    public static final Function FINDB = new SearchFindB(true);
    /**
     * Implementation of the FINDB() function.<p/>
     *
     * <b>Syntax</b>:<br/>
     * <b>SEARCHB</b>(<b>find_text</b>, <b>within_text</b>, start_num)<p/>
     *
     * SEARCHB is a case-insensitive version of FINDB()
     */
    public static final Function SEARCHB = new SearchFindB(false);
}
