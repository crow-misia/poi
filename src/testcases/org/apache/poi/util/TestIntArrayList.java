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

import org.apache.poi.util.list.IntArrayList;

import junit.framework.TestCase;

/**
 * Class to test IntArrayList
 *
 * @author Marc Johnson
 */
public final class TestIntArrayList extends TestCase {

    public void testConstructors() {
        IntArrayList list = new IntArrayList();

        assertTrue(list.isEmpty());
        list.add(0);
        list.add(1);
        IntArrayList list2 = new IntArrayList(list);

        assertEquals(list, list2);
        IntArrayList list3 = new IntArrayList(2);

        assertTrue(list3.isEmpty());
    }

    public void testAdd() {
        IntArrayList list      = new IntArrayList();
        int[]   testArray =
        {
            0, 1, 2, 3, 5
        };

        for (int j = 0; j < testArray.length; j++)
        {
            list.add(testArray[ j ]);
        }
        for (int j = 0; j < testArray.length; j++)
        {
            assertEquals(testArray[ j ], list.get(j));
        }
        assertEquals(testArray.length, list.size());

        // test growth
        list = new IntArrayList(0);
        for (int j = 0; j < 1000; j++)
        {
            list.add(j);
        }
        assertEquals(1000, list.size());
        for (int j = 0; j < 1000; j++)
        {
            assertEquals(j, list.get(j));
        }
    }

    public void testAddAll() {
        IntArrayList list = new IntArrayList();

        for (int j = 0; j < 5; j++)
        {
            list.add(j);
        }
        IntArrayList list2 = new IntArrayList(0);

        list2.addAll(list);
        list2.addAll(list);
        assertEquals(2 * list.size(), list2.size());
        for (int j = 0; j < 5; j++)
        {
            assertEquals(list2.get(j), j);
            assertEquals(list2.get(j + list.size()), j);
        }
    }

    public void testClear() {
        IntArrayList list = new IntArrayList();

        for (int j = 0; j < 500; j++)
        {
            list.add(j);
        }
        assertEquals(500, list.size());
        list.clear();
        assertEquals(0, list.size());
        for (int j = 0; j < 500; j++)
        {
            list.add(j + 1);
        }
        assertEquals(500, list.size());
        for (int j = 0; j < 500; j++)
        {
            assertEquals(j + 1, list.get(j));
        }
    }

    public void testGet() {
        IntArrayList list = new IntArrayList();

        for (int j = 0; j < 1000; j++)
        {
            list.add(j);
        }
        for (int j = 0; j < 1001; j++)
        {
            try
            {
                assertEquals(j, list.get(j));
                if (j == 1000)
                {
                    fail("should have gotten exception");
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                if (j != 1000)
                {
                    fail("unexpected IndexOutOfBoundsException");
                }
            }
        }
    }

    public void testIsEmpty() {
        IntArrayList list1 = new IntArrayList();
        IntArrayList list2 = new IntArrayList(1000);
        IntArrayList list3 = new IntArrayList(list1);

        assertTrue(list1.isEmpty());
        assertTrue(list2.isEmpty());
        assertTrue(list3.isEmpty());
        list1.add(1);
        list2.add(2);
        list3 = new IntArrayList(list2);
        assertTrue(!list1.isEmpty());
        assertTrue(!list2.isEmpty());
        assertTrue(!list3.isEmpty());
        list1.clear();
        assertTrue(list1.isEmpty());
    }

    public void testSet() {
        IntArrayList list = new IntArrayList();

        for (int j = 0; j < 1000; j++)
        {
            list.add(j);
        }
        for (int j = 0; j < 1001; j++)
        {
            try
            {
                list.set(j, j + 1);
                if (j == 1000)
                {
                    fail("Should have gotten exception");
                }
                assertEquals(j + 1, list.get(j));
            }
            catch (IndexOutOfBoundsException e)
            {
                if (j != 1000)
                {
                    fail("premature exception");
                }
            }
        }
    }

    public void testSize() {
        IntArrayList list = new IntArrayList();

        for (int j = 0; j < 1000; j++)
        {
            assertEquals(j, list.size());
            list.add(j);
            assertEquals(j + 1, list.size());
        }
    }

    public void testToArray() {
        IntArrayList list = new IntArrayList();

        for (int j = 0; j < 1000; j++)
        {
            list.add(j);
        }
        int[] a1 = list.toArray();

        assertEquals(a1.length, list.size());
        for (int j = 0; j < 1000; j++)
        {
            assertEquals(a1[ j ], list.get(j));
        }
    }
}
