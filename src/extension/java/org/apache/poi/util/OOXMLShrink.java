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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build a 'shrink' version of the ooxml-schemas sources
 *
 * @author Zenichi Amano
 */
public final class OOXMLShrink {

    private static final Pattern SYNC_PATTERN = Pattern.compile("        synchronized \\(monitor\\(\\)\\).*?        \\{\n(.*?)\n        \\}", Pattern.DOTALL);

    /**
     * Reference to the ooxml-schemas Sources
     */
    private final File _sourceDir;


    OOXMLShrink(final String sourceDir) {
        _sourceDir = new File(sourceDir);
    }

    public static void main(final String[] args) throws IOException {

        if (args.length != 1) {
            return;
        }
        final String sourceDir = args[0];
        final OOXMLShrink builder = new OOXMLShrink(sourceDir);
        builder.build();
    }

	void build() throws IOException{

        //see what classes from the ooxml-schemas.jar are loaded
        System.out.println("Shrink synchronized in " + _sourceDir);

        final LinkedList<File> dirs = new LinkedList<>();
        dirs.add(_sourceDir);
        while (!dirs.isEmpty()) {
            final File dir = dirs.removeFirst();
            final File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(final File path) {
                    if (path.isDirectory()) {
                        dirs.add(path);
                        return false;
                    }
                    return path.getName().endsWith(".java");
                }
            });

            for (final File file : files) {
                final Path src = file.toPath();
                final StringBuilder tmp = new StringBuilder((int) file.length());
                try (final BufferedReader br = Files.newBufferedReader(src, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        tmp.append(line).append('\n');
                    }
                }
                String source = tmp.toString();
                if (source.contains("public interface ")) {
                    continue;
                }
                Matcher matcher = SYNC_PATTERN.matcher(source);
                while (matcher.find()) {
                    source = matcher.replaceFirst(Matcher.quoteReplacement(matcher.group(1)));
                    matcher = SYNC_PATTERN.matcher(source);
                }
                Files.write(src, source.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }

}