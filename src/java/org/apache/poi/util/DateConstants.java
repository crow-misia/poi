
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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author Zenichi Amano
 */
public final class DateConstants {
    public static final ThreadLocal<SimpleDateFormat> ddMMMyyyy = createInstance("dd-MMM-yyyy", false);
    public static final ThreadLocal<SimpleDateFormat> yyyyMMdd = createInstance("yyyy/MM/dd", false);
    public static final ThreadLocal<SimpleDateFormat> Mdyy = createInstance("M/d/yy", false);
    public static final ThreadLocal<SimpleDateFormat> Mdy = createInstance("M/d/y", false);
    public static final ThreadLocal<SimpleDateFormat> dMMMyy = createInstance("d-MMM-yy", false);
    public static final ThreadLocal<SimpleDateFormat> dMMM = createInstance("d-MMM", false);
    public static final ThreadLocal<SimpleDateFormat> MMMyy = createInstance("MMM-yy", false);
    public static final ThreadLocal<SimpleDateFormat> hmma = createInstance("h:mm a", false);
    public static final ThreadLocal<SimpleDateFormat> hmmssa = createInstance("h:mm:ss a", false);
    public static final ThreadLocal<SimpleDateFormat> hmm = createInstance("h:mm", false);
    public static final ThreadLocal<SimpleDateFormat> hmmss = createInstance("h:mm:ss", false);
    public static final ThreadLocal<SimpleDateFormat> Mdyyhmm = createInstance("M/d/yy h:mm", false);
    public static final ThreadLocal<SimpleDateFormat> mmss = createInstance("mm:ss", false);
    public static final ThreadLocal<SimpleDateFormat> mmss0 = createInstance("mm:ss.0", false);
    public static final ThreadLocal<SimpleDateFormat> ddMMM = createInstance("dd-MMM", false);
    public static final ThreadLocal<SimpleDateFormat> yyyy = createInstance("yyyy", false);
    public static final ThreadLocal<SimpleDateFormat> MMMM = createInstance("MMMM", false);
    public static final ThreadLocal<SimpleDateFormat> yyyyMMddTHHmmssZ = createInstance("yyyy-MM-dd'T'HH:mm:ss'Z'", true);
    public static final ThreadLocal<SimpleDateFormat> yyyyMMddTHHmmssSSSZ = createInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", true);
    public static final ThreadLocal<SimpleDateFormat> EdMMMyyyyHHmmssZ = createInstance("E, d MMM yyyy HH:mm:ss Z", true);
    public static final ThreadLocal<SimpleDateFormat> EdMMMyyyyHHmmss = createInstance("E, d MMM yyyy HH:mm:ss", false);
    public static final ThreadLocal<SimpleDateFormat> yyyyMMddHHmmssSSS = createInstance("yyyy-MM-dd HH:mm:ss.SSS", false);
    public static final ThreadLocal<SimpleDateFormat> yyyyMMddHHmmss = createInstance("yyyy-MM-dd HH:mm:ss", false);

    /**
     * Create ThreadLocal Instance
     * @param p SimpleDateFormat's pattern
     * @return ThreadLocal Instance
     */
    private static ThreadLocal<SimpleDateFormat> createInstance(final String p, final boolean utc) {
        return new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                final SimpleDateFormat f = new SimpleDateFormat(p);
                if (utc) {
                    f.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                return f;
            }
        };
    }

    private DateConstants() {
        // nop.
    }
}
