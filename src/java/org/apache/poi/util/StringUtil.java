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

package org.apache.poi.util;

import java.nio.charset.Charset;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Iterator;

import org.apache.commons.codec.Charsets;
import org.apache.poi.hssf.record.RecordInputStream;

/**
 *  Title: String Utility Description: Collection of string handling utilities<p/>
 *
 * Note - none of the methods in this class deals with {@link org.apache.poi.hssf.record.ContinueRecord}s.  For such
 * functionality, consider using {@link RecordInputStream
} *
 *
 *@author     Andrew C. Oliver
 *@author     Sergei Kozello (sergeikozello at mail.ru)
 *@author     Toshiaki Kamoshida (kamoshida.toshiaki at future dot co dot jp)
 */
public final class StringUtil {
	private StringUtil() {
		// no instances of this class
	}

	/**
	 *  Given a byte array of 16-bit unicode characters in Little Endian
	 *  format (most important byte last), return a Java String representation
	 *  of it.
	 *
	 * { 0x16, 0x00 } -0x16
	 *
	 * @param  string  the byte array to be converted
	 * @param  offset  the initial offset into the
	 *                 byte array. it is assumed that string[ offset ] and string[ offset +
	 *                 1 ] contain the first 16-bit unicode character
     * @param len the length of the final string
	 * @return the converted string, never <code>null</code>.
	 * @exception  ArrayIndexOutOfBoundsException  if offset is out of bounds for
	 *      the byte array (i.e., is negative or is greater than or equal to
	 *      string.length)
	 * @exception  IllegalArgumentException        if len is too large (i.e.,
	 *      there is not enough data in string to create a String of that
	 *      length)
	 */
	public static String getFromUnicodeLE(
		final byte[] string,
		final int offset,
		final int len)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		if ((offset < 0) || (offset >= string.length)) {
			throw new ArrayIndexOutOfBoundsException("Illegal offset " + offset + " (String data is of length " + string.length + ")");
		}
		if ((len < 0) || (((string.length - offset) / 2) < len)) {
			throw new IllegalArgumentException("Illegal length " + len);
		}

		return new String(string, offset, len * 2, Charsets.UTF_16LE);
	}

	/**
	 *  Given a byte array of 16-bit unicode characters in little endian
	 *  format (most important byte last), return a Java String representation
	 *  of it.
	 *
	 * { 0x16, 0x00 } -0x16
	 *
	 * @param  string  the byte array to be converted
	 * @return the converted string, never <code>null</code>
	 */
	public static String getFromUnicodeLE(byte[] string) {
		if(string.length == 0) { return ""; }
		return getFromUnicodeLE(string, 0, string.length / 2);
	}

	/**
	 * Read 8 bit data (in ISO-8859-1 codepage) into a (unicode) Java
	 * String and return.
	 * (In Excel terms, read compressed 8 bit unicode as a string)
	 *
	 * @param string byte array to read
	 * @param offset offset to read byte array
	 * @param len    length to read byte array
	 * @return String generated String instance by reading byte array
	 */
	public static String getFromCompressedUnicode(
		final byte[] string,
		final int offset,
		final int len) {

		final int len_to_use = Math.min(len, string.length - offset);
		return new String(string, offset, len_to_use, Charsets.ISO_8859_1);
	}
	public static String readCompressedUnicode(LittleEndianInput in, int nChars) {
		char[] buf = new char[nChars];
		for (int i = 0; i < nChars; i++) {
			buf[i] = (char) in.readUByte();
		}
		return String.valueOf(buf);
	}
	/**
	 * InputStream <tt>in</tt> is expected to contain:
	 * <ol>
	 * <li>ushort nChars</li>
	 * <li>byte is16BitFlag</li>
	 * <li>byte[]/char[] characterData</li>
	 * </ol>
	 * For this encoding, the is16BitFlag is always present even if nChars==0.
	 * 
	 * This structure is also known as a XLUnicodeString.
	 */
	public static String readUnicodeString(LittleEndianInput in) {

		int nChars = in.readUShort();
		byte flag = in.readByte();
		if ((flag & 0x01) == 0) {
			return readCompressedUnicode(in, nChars);
		}
		return readUnicodeLE(in, nChars);
	}
	/**
	 * InputStream <tt>in</tt> is expected to contain:
	 * <ol>
	 * <li>byte is16BitFlag</li>
	 * <li>byte[]/char[] characterData</li>
	 * </ol>
	 * For this encoding, the is16BitFlag is always present even if nChars==0.
	 * <br/>
	 * This method should be used when the nChars field is <em>not</em> stored
	 * as a ushort immediately before the is16BitFlag. Otherwise, {@link
	 * #readUnicodeString(LittleEndianInput)} can be used.
	 */
	public static String readUnicodeString(LittleEndianInput in, int nChars) {
		byte is16Bit = in.readByte();
		if ((is16Bit & 0x01) == 0) {
			return readCompressedUnicode(in, nChars);
		}
		return readUnicodeLE(in, nChars);
	}
	/**
	 * OutputStream <tt>out</tt> will get:
	 * <ol>
	 * <li>ushort nChars</li>
	 * <li>byte is16BitFlag</li>
	 * <li>byte[]/char[] characterData</li>
	 * </ol>
	 * For this encoding, the is16BitFlag is always present even if nChars==0.
	 */
	public static void writeUnicodeString(LittleEndianOutput out, String value) {

		int nChars = value.length();
		out.writeShort(nChars);
		boolean is16Bit = hasMultibyte(value);
		out.writeByte(is16Bit ? 0x01 : 0x00);
		if (is16Bit) {
			putUnicodeLE(value, out);
		} else {
			putCompressedUnicode(value, out);
		}
	}
	/**
	 * OutputStream <tt>out</tt> will get:
	 * <ol>
	 * <li>byte is16BitFlag</li>
	 * <li>byte[]/char[] characterData</li>
	 * </ol>
	 * For this encoding, the is16BitFlag is always present even if nChars==0.
	 * <br/>
	 * This method should be used when the nChars field is <em>not</em> stored
	 * as a ushort immediately before the is16BitFlag. Otherwise, {@link
	 * #writeUnicodeString(LittleEndianOutput, String)} can be used.
	 */
	public static void writeUnicodeStringFlagAndData(LittleEndianOutput out, String value) {
		boolean is16Bit = hasMultibyte(value);
		out.writeByte(is16Bit ? 0x01 : 0x00);
		if (is16Bit) {
			putUnicodeLE(value, out);
		} else {
			putCompressedUnicode(value, out);
		}
	}

	/**
	 * @return the number of bytes that would be written by {@link #writeUnicodeString(LittleEndianOutput, String)}
	 */
	public static int getEncodedSize(String value) {
		int result = 2 + 1;
		result += value.length() * (StringUtil.hasMultibyte(value) ? 2 : 1);
		return result;
	}

	/**
	 * Takes a unicode (java) string, and returns it as 8 bit data (in ISO-8859-1
	 * codepage).
	 * (In Excel terms, write compressed 8 bit unicode)
	 *
	 * @param  input   the String containing the data to be written
	 * @param  output  the byte array to which the data is to be written
	 * @param  offset  an offset into the byte arrat at which the data is start
	 *      when written
	 */
	public static void putCompressedUnicode(String input, byte[] output, int offset) {
		final byte[] bytes = input.getBytes(Charsets.ISO_8859_1);
		System.arraycopy(bytes, 0, output, offset, bytes.length);
	}
	public static void putCompressedUnicode(String input, LittleEndianOutput out) {
		out.write(input.getBytes(Charsets.ISO_8859_1));
	}

	/**
	 * Takes a unicode string, and returns it as little endian (most
	 * important byte last) bytes in the supplied byte array.
	 * (In Excel terms, write uncompressed unicode)
	 *
	 * @param  input   the String containing the unicode data to be written
	 * @param  output  the byte array to hold the uncompressed unicode, should be twice the length of the String
	 * @param  offset  the offset to start writing into the byte array
	 */
	public static void putUnicodeLE(String input, byte[] output, int offset) {
		final byte[] bytes = input.getBytes(Charsets.UTF_16LE);
		System.arraycopy(bytes, 0, output, offset, bytes.length);
	}
	public static void putUnicodeLE(String input, LittleEndianOutput out) {
		out.write(input.getBytes(Charsets.UTF_16LE));
	}

	public static String readUnicodeLE(LittleEndianInput in, int nChars) {
		char[] buf = new char[nChars];
		for (int i = 0; i < nChars; i++) {
			buf[i] = (char) in.readUShort();
		}
		return String.valueOf(buf);
	}

	/**
	 *  Apply printf() like formatting to a string.
	 *  Primarily used for logging.
	 * @param  message  the string with embedded formatting info
	 *                 eg. "This is a test %2.2"
	 * @param  params   array of values to format into the string
	 * @return          The formatted string
	 */
	public static String format(String message, Object[] params) {
		int currentParamNumber = 0;
		final int l = message.length();
		final StringBuffer formattedMessage = new StringBuffer(l);
		for (int i = 0; i < l; i++) {
			if (message.charAt(i) == '%') {
				if (currentParamNumber >= params.length) {
					formattedMessage.append("?missing data?");
				} else if (
					(params[currentParamNumber] instanceof Number)
						&& (i + 1 < l)) {
					i
						+= matchOptionalFormatting(
							(Number) params[currentParamNumber++],
							message.substring(i + 1),
							formattedMessage);
				} else {
					formattedMessage.append(
						params[currentParamNumber++].toString());
				}
			} else {
				if ((message.charAt(i) == '\\')
					&& (i + 1 < l)
					&& (message.charAt(i + 1) == '%')) {
					formattedMessage.append('%');
					i++;
				} else {
					formattedMessage.append(message.charAt(i));
				}
			}
		}
		return formattedMessage.toString();
	}


	private static int matchOptionalFormatting(
		Number number,
		String formatting,
		StringBuffer outputTo) {
		
		final int l = formatting.length();
		final NumberFormat numberFormat = NumberFormat.getInstance();
		if ((0 < l)
			&& Character.isDigit(formatting.charAt(0))) {
			numberFormat.setMinimumIntegerDigits(formatting.charAt(0) - '0');
			if ((2 < l)
				&& (formatting.charAt(1) == '.')
				&& Character.isDigit(formatting.charAt(2))) {
				numberFormat.setMaximumFractionDigits(formatting.charAt(2) - '0');
				numberFormat.format(number, outputTo, new FieldPosition(0));
				return 3;
			}
			numberFormat.format(number, outputTo, new FieldPosition(0));
			return 1;
		} else if (
			(0 < l) && (formatting.charAt(0) == '.')) {
			if ((1 < l)
				&& Character.isDigit(formatting.charAt(1))) {
				numberFormat.setMaximumFractionDigits(formatting.charAt(1) - '0');
				numberFormat.format(number, outputTo, new FieldPosition(0));
				return 2;
			}
		}
		numberFormat.format(number, outputTo, new FieldPosition(0));
		return 1;
	}

	/**
	 * @return the encoding we want to use, currently hardcoded to ISO-8859-1
	 */
	public static Charset getPreferredEncoding() {
		return Charsets.ISO_8859_1;
	}

	/**
	 * check the parameter has multibyte character
	 *
	 * @param value string to check
	 * @return boolean result true:string has at least one multibyte character
	 */
	public static boolean hasMultibyte(String value) {
		if (value == null)
			return false;
		final int n = value.length();
		for (int i = 0; i < n; i++) {
			char c = value.charAt(i);
			if (c > 0xFF) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if a given String needs to be represented as Unicode
	 *
	 * @param value
	 * @return true if string needs Unicode to be represented.
	 */
	public static boolean isUnicodeString(final String value) {
		return !value.equals(new String(value.getBytes(Charsets.ISO_8859_1), Charsets.ISO_8859_1));
	}

	public static boolean isEmpty(final String s) {
		return s == null || s.isEmpty();
	}

	public static boolean isNotEmpty(final String s) {
		return s != null && !s.isEmpty();
	}

	public static boolean isBlank(final String s) {
		if (s != null) {
			final char[] a = s.toCharArray();
			for (final char c : a) {
				if (c != '\u0020') {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isNotBlank(final String s) {
		if (s != null) {
			final char[] a = s.toCharArray();
			for (final char c : a) {
				if (c != '\u0020') {
					return true;
				}
			}
		}
		return false;
	}

   /**
    * An Iterator over an array of Strings.
    */
   public static class StringsIterator implements Iterator<String> {
      private String[] strings;
      private int position = 0;
      public StringsIterator(String[] strings) {
         if(strings != null) {
            this.strings = strings;
         } else {
            this.strings = ArrayUtil.EMPTY_STRING_ARRAY;
         }
      }

      public boolean hasNext() {
         return position < strings.length;
      }
      public String next() {
         int ourPos = position++;
         if(ourPos >= strings.length)
            throw new ArrayIndexOutOfBoundsException(ourPos);
         return strings[ourPos];
      }
      public void remove() {}
   }
}
