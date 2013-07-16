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

package org.apache.poi.hwpf.model;

import java.io.IOException;

import org.apache.poi.hwpf.model.io.HWPFFileSystem;
import org.apache.poi.hwpf.model.io.HWPFOutputStream;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/**
 * FontTable or in MS terminology sttbfffn is a common data structure written in all
 * Word files. The sttbfffn is an sttbf where each string is an FFN structure instead
 * of pascal-style strings. An sttbf is a string Table stored in file. Thus sttbffn
 * is like an Sttbf with an array of FFN structures that stores the font name strings
 *
 * @author Praveen Mathew
 */
@Internal
public final class FontTable
{
  private static final POILogger _logger = POILogFactory.getLogger(FontTable.class);
  private short _stringCount;// how many strings are included in the string table
  private short _extraDataSz;// size in bytes of the extra data

  // added extra facilitator members
  private int lcbSttbfffn;// count of bytes in sttbfffn

  // FFN structure containing strings of font names
  private Ffn[] _fontNames = null;


  public FontTable(byte[] buf, int offset, int lcbSttbfffn)
  {
    this.lcbSttbfffn = lcbSttbfffn;

    int fcSttbfffn = offset;

    _stringCount = LittleEndian.getShort(buf, fcSttbfffn);
    fcSttbfffn += LittleEndian.SHORT_SIZE;
    _extraDataSz = LittleEndian.getShort(buf, fcSttbfffn);
    fcSttbfffn += LittleEndian.SHORT_SIZE;

    _fontNames = new Ffn[_stringCount]; //Ffn corresponds to a Pascal style String in STTBF.

    for(int i = 0;i<_stringCount; i++)
    {
      _fontNames[i] = new Ffn(buf, fcSttbfffn);
      fcSttbfffn += _fontNames[i].getSize();
    }
  }

  public short getStringCount()
  {
    return  _stringCount;
  }

  public short getExtraDataSz()
  {
  	return _extraDataSz;
  }

  public Ffn[] getFontNames()
  {
  	return _fontNames;
  }

  public int getSize()
  {
    return lcbSttbfffn;
  }

  public String getMainFont(int chpFtc )
  {
    if(chpFtc >= _stringCount)
    {
      _logger.log(POILogger.INFO, "Mismatch in chpFtc with stringCount");
      return null;
    }

    return _fontNames[chpFtc].getMainFontName();
  }

  public String getAltFont(int chpFtc )
  {
    if(chpFtc >= _stringCount)
    {
      _logger.log(POILogger.INFO, "Mismatch in chpFtc with stringCount");
      return null;
    }

    return _fontNames[chpFtc].getAltFontName();
  }

  public void setStringCount(short stringCount)
  {
    this._stringCount = stringCount;
  }

    public void writeTo( HWPFOutputStream tableStream ) throws IOException
    {
	  byte[] buf = new byte[LittleEndian.SHORT_SIZE];
	  LittleEndian.putShort(buf, 0, _stringCount);
	  tableStream.write(buf);
	  LittleEndian.putShort(buf, 0, _extraDataSz);
	  tableStream.write(buf);

	  for(final Ffn f : _fontNames)
	  {
		  tableStream.write(f.toByteArray());
	  }

  }

  public boolean equals(Object o)
  {
  	boolean retVal = true;

    if(((FontTable)o).getStringCount() == _stringCount)
    {
      if(((FontTable)o).getExtraDataSz() == _extraDataSz)
      {
        Ffn[] fontNamesNew = ((FontTable)o).getFontNames();
        for(int i = 0;i<_stringCount; i++)
        {
          if(!(_fontNames[i].equals(fontNamesNew[i])))
            retVal = false;
        }
      }
      else
        retVal = false;
    }
    else
	    retVal = false;


	  return retVal;
  }



}


