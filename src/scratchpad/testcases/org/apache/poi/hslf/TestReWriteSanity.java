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

package org.apache.poi.hslf;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.poi.POIDataSamples;
import org.apache.poi.hslf.record.CurrentUserAtom;
import org.apache.poi.hslf.record.PersistPtrHolder;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.UserEditAtom;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Tests that HSLFSlideShow writes the powerpoint bit of data back out
 *  in a sane manner - i.e. records end up in the right place
 *
 * @author Nick Burch (nick at torchbox dot com)
 */
public final class TestReWriteSanity extends TestCase {
	// HSLFSlideShow primed on the test data
	private HSLFSlideShow ss;
	// POIFS primed on the test data
	private POIFSFileSystem pfs;

    public TestReWriteSanity() throws Exception {
        POIDataSamples slTests = POIDataSamples.getSlideShowInstance();
		pfs = new POIFSFileSystem(slTests.openResourceAsStream("basic_test_ppt_file.ppt"));
		ss = new HSLFSlideShow(pfs);
    }

	public void testUserEditAtomsRight() throws Exception {
		// Write out to a byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ss.write(baos);

		// Build an input stream of it
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

		// Create a new one from that
		HSLFSlideShow wss = new HSLFSlideShow(bais);

		// Find the location of the PersistPtrIncrementalBlocks and
		// UserEditAtoms
		Record[] r = wss.getRecords();
		final Map<Integer, Record> pp = new HashMap<>();
		final Map<Integer, Object> ue = new HashMap<>();
		ue.put(Integer.valueOf(0), Integer.valueOf(0)); // Will show 0 if first
		int pos = 0;
		int lastUEPos = -1;

		for(int i=0; i<r.length; i++) {
			if(r[i] instanceof PersistPtrHolder) {
				pp.put(Integer.valueOf(pos), r[i]);
			}
			if(r[i] instanceof UserEditAtom) {
				ue.put(Integer.valueOf(pos), r[i]);
				lastUEPos = pos;
			}

			ByteArrayOutputStream bc = new ByteArrayOutputStream();
			r[i].writeOut(bc);
			pos += bc.size();
		}

		// Check that the UserEditAtom's point to right stuff
		for(final Record ri : r) {
			if(ri instanceof UserEditAtom) {
				UserEditAtom uea = (UserEditAtom)ri;
				int luPos = uea.getLastUserEditAtomOffset();
				int ppPos = uea.getPersistPointersOffset();

				assertTrue(pp.containsKey(Integer.valueOf(ppPos)));
				assertTrue(ue.containsKey(Integer.valueOf(luPos)));
			}
		}

		// Check that the CurrentUserAtom points to the right UserEditAtom
		CurrentUserAtom cua = wss.getCurrentUserAtom();
		int listedUEPos = (int)cua.getCurrentEditOffset();
		assertEquals(lastUEPos,listedUEPos);
	}
}
