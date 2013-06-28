
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

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author Zenichi Amano
 */
public final class FastByteArrayOutputStream extends OutputStream {
    /** The array backing the output stream. */
    private static final int DEFAULT_INITIAL_CAPACITY = 4096;

    /** The buffer backing the output stream. */
    private byte[] buf;

    /** The current writing position. */
    private int pos;

    /** Creates a new array output stream with an initial capacity of {@link #DEFAULT_INITIAL_CAPACITY} bytes. */
    public FastByteArrayOutputStream() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /** Creates a new array output stream with a given initial capacity.
     *
     * @param initialCapacity the initial length of the backing array.
     */
    public FastByteArrayOutputStream(final int initialCapacity) {
        buf = new byte[initialCapacity];
    }

    /** Marks this array output stream as empty. */
    public void reset() {
        pos = 0;
    }

    @Override
    public void write(final int b) {
        final int newPos = pos + 1;
        
        verifyBufferSize(newPos);
        buf[pos] = (byte) b;

        pos = newPos;
    }

    @Override
    public void write(final byte[] b, final int off, final int len) {
        final int newPos = pos + len;
        verifyBufferSize(newPos);

        System.arraycopy(b, off, buf, pos, len);

        pos = newPos;
    }

    @Override
    public void close() {
        // do nothing
    }

    public int size() {
        return pos;
    }

    public ByteArrayInputStream toInputStream() {
        return new ByteArrayInputStream(buf, 0, pos);
    }

    public byte[] getRawArray() {
        return this.buf;
    }

    public void arraycopy(final int pos, final byte[] dest, final int destPos, final int length) {
        System.arraycopy(buf, pos, dest, destPos, length);
    }

    private void verifyBufferSize(final int sz) {
        if (sz > buf.length) {
            buf = Arrays.copyOf(buf, Math.max(sz, 2 * buf.length));
        }
    }
}
