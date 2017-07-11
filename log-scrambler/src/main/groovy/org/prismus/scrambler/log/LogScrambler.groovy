package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogScrambler {

    LogContext parse(InputStream inputStream) {
        return parse(new LogContext(), inputStream)
    }

    protected LogContext parse(LogContext context, Reader reader, String line) {
        throw new RuntimeException('implement me')
        return context
    }

    protected LogContext parse(LogContext context, InputStream inputStream) {
        Reader reader = null
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream))
            String line
            final pattern = Pattern.compile('')
            while ((line = reader.readLine()) != null) {
                final matcher = pattern.matcher(line)
                final groupCount = matcher.groupCount()
                if (groupCount) {

                }
            }
            throw new RuntimeException('implement me')
        } finally {
            closeQuietly(reader)
        }
        return context
    }

    static closeQuietly(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (Exception ignore) { }
        }
    }

}
