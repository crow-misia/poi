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

package org.apache.poi.ddf;

import org.apache.poi.util.LittleEndian;

/**
 * Generates escher records when provided the byte array containing those records.
 *
 * @author Glen Stampoultzis
 * @author Nick Burch   (nick at torchbox . com)
 *
 * @see EscherRecordFactory
 */
public class DefaultEscherRecordFactory implements EscherRecordFactory {
    /**
     * Creates an instance of the escher record factory
     */
    public DefaultEscherRecordFactory() {
        // no instance initialisation
    }

    /**
     * Generates an escher record including the any children contained under that record.
     * An exception is thrown if the record could not be generated.
     *
     * @param data   The byte array containing the records
     * @param offset The starting offset into the byte array
     * @return The generated escher record
     */
    public EscherRecord createRecord(byte[] data, int offset) {
        final short options = LittleEndian.getShort( data, offset );
        final short recordId = LittleEndian.getShort( data, offset + 2 );
        final EscherRecord r;

        // int remainingBytes = LittleEndian.getInt( data, offset + 4 );

        // Options of 0x000F means container record
        // However, EscherTextboxRecord are containers of records for the
        //  host application, not of other Escher records, so treat them
        //  differently
        if (isContainer(options, recordId)) {
            r = new EscherContainerRecord();
        }
        else if (recordId >= EscherBlipRecord.RECORD_ID_START
                && recordId <= EscherBlipRecord.RECORD_ID_END) {
            if (recordId == EscherBitmapBlip.RECORD_ID_DIB ||
                    recordId == EscherBitmapBlip.RECORD_ID_JPEG ||
                    recordId == EscherBitmapBlip.RECORD_ID_PNG)
            {
                r = new EscherBitmapBlip();
            }
            else if (recordId == EscherMetafileBlip.RECORD_ID_EMF ||
                    recordId == EscherMetafileBlip.RECORD_ID_WMF ||
                    recordId == EscherMetafileBlip.RECORD_ID_PICT)
            {
                r = new EscherMetafileBlip();
            } else {
                r = new EscherBlipRecord();
            }

        } else {

            switch (recordId) {
            case EscherBSERecord.RECORD_ID:
                r = new EscherBSERecord();
                break;
            case EscherOptRecord.RECORD_ID:
                r = new EscherOptRecord();
                break;
            case EscherTertiaryOptRecord.RECORD_ID:
                r = new EscherTertiaryOptRecord();
                break;
            case EscherClientAnchorRecord.RECORD_ID:
                r = new EscherClientAnchorRecord();
                break;
            case EscherDgRecord.RECORD_ID:
                r = new EscherDgRecord();
                break;
            case EscherSpgrRecord.RECORD_ID:
                r = new EscherSpgrRecord();
                break;
            case EscherSpRecord.RECORD_ID:
                r = new EscherSpRecord();
                break;
            case EscherClientDataRecord.RECORD_ID:
                r = new EscherClientDataRecord();
                break;
            case EscherDggRecord.RECORD_ID:
                r = new EscherDggRecord();
                break;
            case EscherSplitMenuColorsRecord.RECORD_ID:
                r = new EscherSplitMenuColorsRecord();
                break;
            case EscherChildAnchorRecord.RECORD_ID:
                r = new EscherChildAnchorRecord();
                break;
            case EscherTextboxRecord.RECORD_ID:
                r = new EscherTextboxRecord();
                break;
            default:
                return new UnknownEscherRecord();
            }
        }

        r.setRecordId(recordId);
        r.setOptions(options);

        return r;
    }

    public static boolean isContainer(short options, short recordId){
        if(recordId >= EscherContainerRecord.DGG_CONTAINER &&
           recordId <= EscherContainerRecord.SOLVER_CONTAINER){
            return true;
        }
        if (recordId == EscherTextboxRecord.RECORD_ID) {
        	return false;
        }
        return ( options & (short) 0x000F ) == (short) 0x000F;
    }
}
