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

package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

/**
 * The block containing the archive header
 */
public final class HeaderBlock implements HeaderBlockConstants {
   
	/**
	 * What big block size the file uses. Most files
	 *  use 512 bytes, but a few use 4096
	 */
	private final POIFSBigBlockSize bigBlockSize;

	/** 
	 * Number of big block allocation table blocks (int).
	 * (Number of FAT Sectors in Microsoft parlance).
	 */
	private int _bat_count;

	/** 
	 * Start of the property set block (int index of the property set
	 * chain's first big block).
	 */
	private int _property_start;

	/** 
	 * start of the small block allocation table (int index of small
	 * block allocation table's first big block)
	 */
	private int _sbat_start;
	/**
	 * Number of small block allocation table blocks (int)
	 * (Number of MiniFAT Sectors in Microsoft parlance)
	 */
	private int _sbat_count;

	/** 
	 * Big block index for extension to the big block allocation table
	 */
	private int _xbat_start;
	/**
	 * Number of big block allocation table blocks (int)
	 * (Number of DIFAT Sectors in Microsoft parlance)
	 */
	private int _xbat_count;
	
	/**
	 * The data. Only ever 512 bytes, because 4096 byte
	 *  files use zeros for the extra header space.
	 */
	private final byte[] _data;
	
   private static final byte _default_value = ( byte ) 0xFF;

	/**
	 * create a new HeaderBlockReader from an InputStream
	 *
	 * @param stream the source InputStream
	 *
	 * @exception IOException on errors or bad data
	 */
	public HeaderBlock(InputStream stream) throws IOException {
		// Grab the first 512 bytes
	   // (For 4096 sized blocks, the remaining 3584 bytes are zero)
		// Then, process the contents
		this(readFirst512(stream));
		
		// Fetch the rest of the block if needed
		if(bigBlockSize.getBigBlockSize() != 512) {
		   int rest = bigBlockSize.getBigBlockSize() - 512;
		   byte[] tmp = new byte[rest];
		   IOUtils.readFully(stream, tmp);
		}
	}
	
	public HeaderBlock(ByteBuffer buffer) throws IOException {
	   this(IOUtils.toByteArray(buffer, POIFSConstants.SMALLER_BIG_BLOCK_SIZE));
	}
	
	private HeaderBlock(byte[] data) throws IOException {
	   this._data = data;
	   
		// verify signature
		long signature = LittleEndian.getLong(_data, _signature_offset);

		if (signature != _signature) {
			// Is it one of the usual suspects?
			byte[] OOXML_FILE_HEADER = POIFSConstants.OOXML_FILE_HEADER;
			if(data[0] == OOXML_FILE_HEADER[0] &&
				data[1] == OOXML_FILE_HEADER[1] &&
				data[2] == OOXML_FILE_HEADER[2] &&
				data[3] == OOXML_FILE_HEADER[3]) {
				throw new OfficeXmlFileException("The supplied data appears to be in the Office 2007+ XML. You are calling the part of POI that deals with OLE2 Office Documents. You need to call a different part of POI to process this data (eg XSSF instead of HSSF)");
			}
			if ((signature & 0xFF8FFFFFFFFFFFFFL) == 0x0010000200040009L) {
				// BIFF2 raw stream starts with BOF (sid=0x0009, size=0x0004, data=0x00t0)
				throw new IllegalArgumentException("The supplied data appears to be in BIFF2 format.  "
						+ "POI only supports BIFF8 format");
			}

			// Give a generic error if the OLE2 signature isn't found
			throw new IOException("Invalid header signature; read "
				                  + longToHex(signature) + ", expected "
				                  + longToHex(_signature) + " - Your file appears "
				                  + "not to be a valid OLE2 document");
		}


		// Figure out our block size
		if (data[30] == 12) {
			this.bigBlockSize = POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS;
		} else if(data[30] == 9) {
			this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
		} else {
		   throw new IOException("Unsupported blocksize  (2^"+ data[30] + "). Expected 2^9 or 2^12.");
		}

	   // Setup the fields to read and write the counts and starts
      _bat_count      = LittleEndian.getInt(data, _bat_count_offset);
      _property_start = LittleEndian.getInt(data, _property_start_offset);
      _sbat_start     = LittleEndian.getInt(data, _sbat_start_offset);
      _sbat_count     = LittleEndian.getInt(data, _sbat_block_count_offset);
      _xbat_start     = LittleEndian.getInt(data, _xbat_start_offset);
      _xbat_count     = LittleEndian.getInt(data, _xbat_count_offset);
	}
	
   /**
    * Create a single instance initialized with default values
    */
   public HeaderBlock(POIFSBigBlockSize bigBlockSize)
   {
      this.bigBlockSize = bigBlockSize;

      // Our data is always 512 big no matter what
      final byte[] data = new byte[ POIFSConstants.SMALLER_BIG_BLOCK_SIZE ];
      _data = data;
      Arrays.fill(data, _default_value);
      
      // Set all the default values
      LittleEndian.putLong(data, _signature_offset, _signature);
      LittleEndian.putInt(data, 0x08, 0);
      LittleEndian.putInt(data, 0x0c, 0);
      LittleEndian.putInt(data, 0x10, 0);
      LittleEndian.putInt(data, 0x14, 0);
      LittleEndian.putShort(data, 0x18, ( short ) 0x3b);
      LittleEndian.putShort(data, 0x1a, ( short ) 0x03);
      LittleEndian.putShort(data, 0x1c, ( short ) -2);
      
      LittleEndian.putShort(data, 0x1e, bigBlockSize.getHeaderValue());
      LittleEndian.putInt(data, 0x20, 0x06);
      LittleEndian.putInt(data, 0x24, 0);
      LittleEndian.putInt(data, 0x28, 0);
      LittleEndian.putInt(data, 0x34, 0);
      LittleEndian.putInt(data, 0x38, 0x1000);
      
      // Initialise the variables
      _bat_count = 0;
      _sbat_count = 0;
      _xbat_count = 0;
      _property_start = POIFSConstants.END_OF_CHAIN;
      _sbat_start = POIFSConstants.END_OF_CHAIN;
      _xbat_start = POIFSConstants.END_OF_CHAIN;
   }
   
	private static byte[] readFirst512(InputStream stream) throws IOException {
      // Grab the first 512 bytes
      // (For 4096 sized blocks, the remaining 3584 bytes are zero)
      byte[] data = new byte[512];
      int bsCount = IOUtils.readFully(stream, data);
      if(bsCount != 512) {
         throw alertShortRead(bsCount, 512);
      }
      return data;
	}

	private static String longToHex(long value) {
		return new String(HexDump.longToHex(value));
	}

	private static IOException alertShortRead(int pRead, int expectedReadSize) {
		int read;
		if (pRead < 0) {
			//Can't have -1 bytes read in the error message!
			read = 0;
		} else {
			read = pRead;
		}
		String type = " byte" + (read == 1 ? (""): ("s"));

		return new IOException("Unable to read entire header; "
				+ read + type + " read; expected "
				+ expectedReadSize + " bytes");
	}

	/**
	 * get start of Property Table
	 *
	 * @return the index of the first block of the Property Table
	 */
	public int getPropertyStart() {
		return _property_start;
	}
   /**
    * Set start of Property Table
    *
    * @param startBlock the index of the first block of the Property Table
    */
   public void setPropertyStart(final int startBlock) {
       _property_start = startBlock;
   }

	/**
	 * @return start of small block (MiniFAT) allocation table
	 */
	public int getSBATStart() {
		return _sbat_start;
	}
	public int getSBATCount() {
	   return _sbat_count;
	}
	
   /**
    * Set start of small block allocation table
    *
    * @param startBlock the index of the first big block of the small
    *                   block allocation table
    */
   public void setSBATStart(final int startBlock) {
       _sbat_start = startBlock;
   }
   /**
    * Set count of SBAT blocks
    *
    * @param count the number of SBAT blocks
    */
   public void setSBATBlockCount(final int count)
   {
      _sbat_count = count;
   }

	/**
	 * @return number of BAT blocks
	 */
	public int getBATCount() {
		return _bat_count;
	}
   /**
    * Sets the number of BAT blocks that are used.
    * This is the number used in both the BAT and XBAT. 
    */
   public void setBATCount(final int count) {
      _bat_count = count;
   }

	/**
	 * Returns the offsets to the first (up to) 109
	 *  BAT sectors.
	 * Any additional BAT sectors are held in the XBAT (DIFAT)
	 *  sectors in a chain.
	 * @return BAT offset array
	 */
	public int[] getBATArray() {
      // Read them in
		final int n = Math.min(_bat_count,_max_bats_in_header);
		int[] result = new int[ n ];
		int offset = _bat_array_offset;
		for (int j = 0; j < n; j++) {
			result[ j ] = LittleEndian.getInt(_data, offset);
			offset     += LittleEndian.INT_SIZE;
		}
		return result;
	}
	/**
	 * Sets the offsets of the first (up to) 109
	 *  BAT sectors.
	 */
	public void setBATArray(int[] bat_array) {
	   int count = Math.min(bat_array.length, _max_bats_in_header);
	   int blank = _max_bats_in_header - count;
	   
      int offset = _bat_array_offset;
	   for(int i=0; i<count; i++) {
	      LittleEndian.putInt(_data, offset, bat_array[i]);
         offset += LittleEndian.INT_SIZE;
	   }
	   for(int i=0; i<blank; i++) {
         LittleEndian.putInt(_data, offset, POIFSConstants.UNUSED_BLOCK);
         offset += LittleEndian.INT_SIZE;
	   }
	}

	/**
	 * @return XBAT (DIFAT) count
	 */
	public int getXBATCount() {
		return _xbat_count;
	}
	/**
	 * Sets the number of XBAT (DIFAT) blocks used
	 */
	public void setXBATCount(final int count) {
	   _xbat_count = count;
	}

	/**
	 * @return XBAT (DIFAT) index
	 */
	public int getXBATIndex() {
		return _xbat_start;
	}
	/**
	 * Sets the first XBAT (DIFAT) block location
	 */
   public void setXBATStart(final int startBlock) {
      _xbat_start = startBlock;
  }

	/**
	 * @return The Big Block size, normally 512 bytes, sometimes 4096 bytes
	 */
	public POIFSBigBlockSize getBigBlockSize() {
		return bigBlockSize;
	}
	
   /**
    * Write the block's data to an OutputStream
    *
    * @param stream the OutputStream to which the stored data should
    *               be written
    *
    * @exception IOException on problems writing to the specified
    *            stream
    */
   void writeData(final OutputStream stream)
       throws IOException
   {
      // Update the counts and start positions
      LittleEndian.putInt(_data, _bat_count_offset, _bat_count);
      LittleEndian.putInt(_data, _property_start_offset, _property_start);
      LittleEndian.putInt(_data, _sbat_start_offset, _sbat_start);
      LittleEndian.putInt(_data, _sbat_block_count_offset, _sbat_count);
      LittleEndian.putInt(_data, _xbat_start_offset, _xbat_start);
      LittleEndian.putInt(_data, _xbat_count_offset, _xbat_count);
      
      // Write the main data out
      stream.write(_data, 0, 512);
      
      // Now do the padding if needed
      for(int i=POIFSConstants.SMALLER_BIG_BLOCK_SIZE; i<bigBlockSize.getBigBlockSize(); i++) {
         stream.write(0);
      }
   }
}
