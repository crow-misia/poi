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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

/**
 * Unit test for {@link FastByteArrayOutputStream}
 *
 * @author Zenichi Amano
 */
public class TestFastByteArrayOutputStream extends TestCase {
    public void test() {
        FastByteArrayOutputStream baos = new FastByteArrayOutputStream(1);
        baos.write(1);
        baos.write(2);
        baos.write(3);
        assertThat(baos.size(), equalTo(3));
        
        ByteArrayInputStream bais = baos.toInputStream();
        assertThat(bais.read(), equalTo(1));
        assertThat(bais.read(), equalTo(2));
        assertThat(bais.read(), equalTo(3));
    }
}
