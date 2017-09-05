/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class Utils {

    static closeQuietly(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (IOException ignore) { }
        }
    }

    static closeQuietly(AutoCloseable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (IOException ignore) { }
        }
    }

    static String readResourceText(String resource) {
        final URL url = Utils.getResource(resource)
        String text
        if (url == null) {
            final file = new File(resource)
            if (!file.exists()) {
                throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
            } else {
                text = file.text
            }
        } else {
            text = url.text
        }
        return text
    }

    static String readGroovyResourceText(String resource) {
        return resource.endsWith('groovy') ? readResourceText(resource) : resource
    }

    static int indexOfFileFilter(String path) {
        int idx = -1
        if (path.matches('[\\*\\?]')) {
            idx = path.lastIndexOf('/')
            if (idx < 0) {
                idx = path.lastIndexOf('\\')
            }
            if (idx < 0) {
                idx = 0
            }
        }
        return idx
    }

    static String defaultFolderFilter(String path, String fileFilter, String defaultFilter = '*') {
        return new File(path).isDirectory() && !fileFilter ? fileFilter = defaultFilter : fileFilter
    }
}
