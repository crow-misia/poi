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

package org.apache.poi.util.list;

import java.util.Arrays;
import java.util.Collection;

/**
 * Double Primitive ArrayList
 *
 * @author Zenichi Amano
 */
public final class DoubleArrayList
{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private double[] values;

    private int pos;

    public DoubleArrayList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public DoubleArrayList(final int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + capacity);
        }
        this.values = new double[capacity];
        this.pos = 0;
    }

    public DoubleArrayList(final DoubleArrayList list) {
        this.values = list.values.clone();
        this.pos = list.pos;
    }

    public DoubleArrayList(final Collection<Double> c) {
        final int l = c.size();
        this.values = new double[l];
        this.pos = l;
        
        int i = 0;
        for (final Double v : c) {
            this.values[i++] = v.doubleValue();
        }
    }

    public void add(final double v) {
        final int newPos = pos + 1;
        verifyBufferSize(newPos);

        this.values[pos] = v;

        pos = newPos;
    }

    public void addAll(final DoubleArrayList list) {
        final int l = list.pos;
        final int newPos = pos + l;
        verifyBufferSize(newPos);

        System.arraycopy(list.values, 0, this.values, pos, l);

        pos = newPos;
    }

    public double get(final int i) {
        if (i < 0 || i >= pos) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + pos);
        }
        return this.values[i];
    }

    public void set(final int i, final double v) {
        if (i < 0 || i >= pos) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + pos);
        }
        this.values[i] = v;
    }

    public int size() {
        return this.pos;
    }

    public boolean isEmpty() {
        return this.pos == 0;
    }

    public void clear() {
        this.pos = 0;
    }

    public double[] toArray() {
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
        for (final double v : this.values) {
            i.run(v);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof DoubleArrayList) {
            return Arrays.equals(this.values, ((DoubleArrayList) o).values);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    public interface Iteratable {
        void run(final double v);
    }
}
