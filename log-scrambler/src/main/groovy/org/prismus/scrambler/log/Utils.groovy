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

}
