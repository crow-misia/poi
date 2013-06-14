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
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.property.Property;

/**
 * A block of Property instances
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */
public final class PropertyBlock extends BigBlock {
    private final Property[]       _properties;

    /**
     * Create a single instance initialized with default values
     *
     * @param properties the properties to be inserted
     * @param offset the offset into the properties array
     */

    private PropertyBlock(final POIFSBigBlockSize bigBlockSize, final Property [] properties, final int offset)
    {
        super(bigBlockSize);
        
        _properties = new Property[ bigBlockSize.getPropertiesPerBlock() ]; 
        System.arraycopy(properties, offset, _properties, 0, _properties.length);
    }

    /**
     * Create an array of PropertyBlocks from an array of Property
     * instances, creating empty Property instances to make up any
     * shortfall
     *
     * @param properties the Property instances to be converted into
     *                   PropertyBlocks, in a java List
     *
     * @return the array of newly created PropertyBlock instances
     */

    public static BlockWritable [] createPropertyBlockArray(
            final POIFSBigBlockSize bigBlockSize, final List<Property> properties)
    {
        int _properties_per_block = bigBlockSize.getPropertiesPerBlock();
        int        block_count   =
            (properties.size() + _properties_per_block - 1)
            / _properties_per_block;
        final int ps = block_count * _properties_per_block;
        final Property[] to_be_written = properties.toArray(new Property[ ps ]);
        for (int j = properties.size(); j < ps; j++)
        {

            // create an instance of an anonymous inner class that
            // extends Property
            to_be_written[ j ] = Property.NULL;
        }
        BlockWritable[] rvalue = new BlockWritable[ block_count ];

        for (int j = 0; j < block_count; j++)
        {
            rvalue[ j ] = new PropertyBlock(bigBlockSize, to_be_written,
                                            j * _properties_per_block);
        }
        return rvalue;
    }

    /* ********** START extension of BigBlock ********** */

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
        int _properties_per_block = bigBlockSize.getPropertiesPerBlock();
        for (int j = 0; j < _properties_per_block; j++)
        {
            _properties[ j ].writeData(stream);
        }
    }

    /* **********  END  extension of BigBlock ********** */
}   // end public class PropertyBlock

