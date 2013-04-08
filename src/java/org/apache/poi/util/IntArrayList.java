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

import java.util.Arrays;
import java.util.Collection;

/**
 * Integer Primitive ArrayList
 *
 * @author Zenichi Amano
 */
public final class IntArrayList
{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private int[] values;

    private int pos;

    public IntArrayList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public IntArrayList(final int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + capacity);
        }
        this.values = new int[capacity];
        this.pos = 0;
    }

    public IntArrayList(final Collection<Integer> c) {
        final int l = c.size();
        this.values = new int[l];
        this.pos = l;
        
        int i = 0;
        for (final Integer v : c) {
            this.values[i++] = v.intValue();
        }
    }

    public void add(final int v) {
        final int newPos = pos + 1;
        verifyBufferSize(newPos);

        this.values[pos] = v;

        pos = newPos;
    }

    public void addAll(final IntArrayList list) {
        final int l = list.pos;
        final int newPos = pos + l;
        verifyBufferSize(newPos);

        System.arraycopy(list.values, 0, this.values, pos, l);

        pos = newPos;
    }

    public int get(final int i) {
        if (i < 0 || i >= pos) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + pos);
        }
        return this.values[i];
    }

    public int size() {
        return this.pos;
    }

    public void clear() {
        this.pos = 0;
    }

    public int[] toArray() {
        return Arrays.copyOfRange(this.values, 0, pos);
    }

    public void sort() {
        Arrays.sort(this.values);
    }

    private void verifyBufferSize(final int sz) {
        if (sz > values.length) {
            values = Arrays.copyOf(values, Math.max(sz, 2 * values.length));
        }
    }

    public void iterate(final Iteratable i) {
        for (final int v : this.values) {
            i.run(v);
        }
    }

    public interface Iteratable {
        void run(final int i);
    }
}
