package org.prismus.scrambler.log

import spock.lang.Specification

import static RegexConsumer.of
import static org.prismus.scrambler.log.Log4jConsumer.*

/**
 * @author Serge Pruteanu
 */
class Log4JLogConsumerTest extends Specification {

    void 'verify adding non specifier char'() {
        final sb = new StringBuilder()
        appendNonSpecifierChar(sb, ch as char)

        expect:
        expected == sb.toString()

        where:
        ch << ['[', ']', '{', '}', '\\', '^', '$', '|', '?', '*', '+', '(', ')', '#', 'a', 'f', 'c', 's']
        expected << ['\\[', '\\]', '\\{', '\\}', '\\\\', '\\^', '\\$', '\\|', '\\?', '\\*', '\\+', '\\(', '\\)', '#', 'a', 'f', 'c', 's']
    }

    void 'verify precision specifier'() {
        final sb = new StringBuilder()
        final entryProcessor = new Log4jConsumer()
        int idx = appendSpecifierRegex(entryProcessor, sb, '%' as char, 0, specString)

        expect:
        expected == sb.toString()
        idx == specString.length() - 1

        where:
        specString << ['%20c', '%-20c', '%.30c', '%20.30c', '%-20.30c', '%r',
                       '%d', '%d{dd MMM yyyy HH:mm:ss,SSS}',
                       '%d{ABSOLUTE}', '%d{DATE}',
                       '%d{ISO8601}',
        ]
        expected << ['([^ ]{20,})', '(\\s*[^ ]{20,})', '([^ ]{1,30})', '([^ ]{20,30})', '(\\s*[^ ]{20,30})', '([\\d^ ]+)',
                     '(\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+)', '(\\w+ \\w+ \\w+ \\w+:\\w+:\\w+.\\w+)',
                     '(\\w+:\\w+:\\w+.\\w+)', '(\\w+ \\w+ \\w+ \\w+:\\w+:\\w+.\\w+)',
                     '(\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+)',
        ]
    }

    void 'verify conversionPatternToRegEx'() {
        final entryProcessor = new Log4jConsumer()
        String patternRegEx = conversionPatternToRegex(entryProcessor, specString)

        expect:
        expected == patternRegEx

        where:
        specString << [
                '%-6r [%15.15t] %-5p %30.30c %x - %m%n',
                '%r [%t] %-5p %c %x - %m%n',
                '%5p | %d | %F | %L | %m%n',
        ]
        expected << [
                '(\\s*[\\d^ ]{6,}) \\[(.{15,15})\\] (\\s*[\\w ]{5,}) ([^ ]{30,30}) ([^ ]*) - (.+)',
                '([\\d^ ]+) \\[(.+)\\] (\\s*[\\w ]{5,}) ([^ ]+) ([^ ]*) - (.+)',
                '([\\w ]{5,}) \\| (\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+) \\| ([^ ]+) \\| ([\\d^ ]+) \\| (.+)',
        ]
    }

    void 'verify conversionPatternToRegEx with log entry parsing'() {
        final conversionPattern = '%-4r [%t] %-5p %C %x - %m%n'
        String patternRegEx = conversionPatternToRegex(new Log4jConsumer(), conversionPattern)
        def processor = of(~/${patternRegEx}/)
                .group('logTime', 1)
                .group('ThreadName', 2)
                .group('LogLevel', 3)
                .group('CallerClass', 4)
                .group('NDC', 5)
                .group('Message', 6)
        LogEntry logEntry = new LogEntry('0    [main] DEBUG com.vaannila.helloworld.HelloWorld  - Sample debug message')
        processor.consume(logEntry)

        expect:
        '0' == logEntry.getLogValue('logTime')
        'main' == logEntry.getLogValue('ThreadName')
        'DEBUG' == logEntry.getLogValue('LogLevel')
        'com.vaannila.helloworld.HelloWorld' == logEntry.getLogValue('CallerClass')
        null == logEntry.getLogValue('NDC')
        'Sample debug message' == logEntry.getLogValue('Message')

        and: 'with exception'
        null != (logEntry = new LogEntry("""ERROR | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | OMG, Something bad happened
javax.servlet.ServletException: Something bad happened
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:60)
    at com.example.myproject.ExceptionHandlerFilter.doFilter(ExceptionHandlerFilter.java:28)
    at com.example.myproject.OutputBufferFilter.doFilter(OutputBufferFilter.java:33)
    at org.mortbay.jetty.servlet.ServletHandler.consume(ServletHandler.java:388)
    at org.mortbay.jetty.security.SecurityHandler.consume(SecurityHandler.java:216)
    at org.mortbay.jetty.servlet.SessionHandler.consume(SessionHandler.java:182)
    at org.mortbay.jetty.handler.ContextHandler.consume(ContextHandler.java:765)
    at org.mortbay.jetty.webapp.WebAppContext.consume(WebAppContext.java:418)
    at org.mortbay.jetty.handler.HandlerWrapper.consume(HandlerWrapper.java:152)
    at org.mortbay.jetty.Server.consume(Server.java:326)
    at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
    at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:756)
    at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:218)
    at org.mortbay.jetty.HttpConnection.consume(HttpConnection.java:404)
Caused by: com.example.myproject.MyProjectServletException
    at com.example.myproject.MyServlet.doPost(MyServlet.java:169)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:727)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:820)
    at org.mortbay.jetty.servlet.ServletHolder.consume(ServletHolder.java:511)
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:30)
    ... 27 more
Caused by: org.hibernate.exception.ConstraintViolationException: could not insert: [com.example.myproject.MyEntity]
    at org.hibernate.exception.SQLStateConverter.convert(SQLStateConverter.java:96)
    at org.hibernate.exception.JDBCExceptionHelper.convert(JDBCExceptionHelper.java:66)
    at org.hibernate.cacheKey.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:64)
    at org.hibernate.persister.entity.AbstractEntityPersister.insert(AbstractEntityPersister.java:2329)
    at org.hibernate.persister.entity.AbstractEntityPersister.insert(AbstractEntityPersister.java:2822)
    at org.hibernate.action.EntityIdentityInsertAction.execute(EntityIdentityInsertAction.java:71)
    at org.hibernate.engine.ActionQueue.execute(ActionQueue.java:268)
    at org.hibernate.event.def.AbstractSaveEventListener.performSaveOrReplicate(AbstractSaveEventListener.java:321)
    at org.hibernate.event.def.AbstractSaveEventListener.performSave(AbstractSaveEventListener.java:204)
    at org.hibernate.event.def.AbstractSaveEventListener.saveWithGeneratedId(AbstractSaveEventListener.java:130)
    at org.hibernate.event.def.DefaultSaveOrUpdateEventListener.saveWithGeneratedOrRequestedId(DefaultSaveOrUpdateEventListener.java:210)
    at org.hibernate.event.def.DefaultSaveEventListener.saveWithGeneratedOrRequestedId(DefaultSaveEventListener.java:56)
    at org.hibernate.event.def.DefaultSaveOrUpdateEventListener.entityIsTransient(DefaultSaveOrUpdateEventListener.java:195)
    at org.hibernate.event.def.DefaultSaveEventListener.performSaveOrUpdate(DefaultSaveEventListener.java:50)
    at org.hibernate.event.def.DefaultSaveOrUpdateEventListener.onSaveOrUpdate(DefaultSaveOrUpdateEventListener.java:93)
    at org.hibernate.impl.SessionImpl.fireSave(SessionImpl.java:705)
    at org.hibernate.impl.SessionImpl.save(SessionImpl.java:693)
    at org.hibernate.impl.SessionImpl.save(SessionImpl.java:689)
    at sun.reflect.GeneratedMethodAccessor5.invoke(Unknown Source)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
    at java.lang.reflect.Method.invoke(Method.java:597)
    at com.example.myproject.MyEntityService.save(MyEntityService.java:59) <-- relevant call (see notes below)
    at com.example.myproject.MyServlet.doPost(MyServlet.java:164)
    ... 32 more
Caused by: java.sql.SQLException: Violation of unique constraint MY_ENTITY_UK_1: duplicate value(s) for column(s) MY_COLUMN in statement [...]
    at org.hsqldb.jdbc.Util.throwError(Unknown Source)
    at org.hsqldb.jdbc.jdbcPreparedStatement.executeUpdate(Unknown Source)
    at com.mchange.v2.c3p0.impl.NewProxyPreparedStatement.executeUpdate(NewProxyPreparedStatement.java:105)
    at org.hibernate.cacheKey.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:57)
    ... 54 more
"""))
        null != (processor = of(~/(?ms)${conversionPatternToRegex(new Log4jConsumer(), '%5p | %d | %F | %L | %m%n')}/)
                .group('LogLevel', 1)
                .group('Timestamp', 2)
                .group('CallerFileName', 3)
                .group('Line', 4)
                .group('Message', 5))
        processor.consume(logEntry)
        false == logEntry.isEmpty()
        'ERROR' == logEntry.getLogValue('LogLevel')
        '2008-09-06 10:51:45,473' == logEntry.getLogValue('Timestamp')
        'SQLErrorCodesFactory.java' == logEntry.getLogValue('CallerFileName')
        '128' == logEntry.getLogValue('Line')

        """OMG, Something bad happened
javax.servlet.ServletException: Something bad happened
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:60)
    at com.example.myproject.ExceptionHandlerFilter.doFilter(ExceptionHandlerFilter.java:28)
    at com.example.myproject.OutputBufferFilter.doFilter(OutputBufferFilter.java:33)
    at org.mortbay.jetty.servlet.ServletHandler.consume(ServletHandler.java:388)
    at org.mortbay.jetty.security.SecurityHandler.consume(SecurityHandler.java:216)
    at org.mortbay.jetty.servlet.SessionHandler.consume(SessionHandler.java:182)
    at org.mortbay.jetty.handler.ContextHandler.consume(ContextHandler.java:765)
    at org.mortbay.jetty.webapp.WebAppContext.consume(WebAppContext.java:418)
    at org.mortbay.jetty.handler.HandlerWrapper.consume(HandlerWrapper.java:152)
    at org.mortbay.jetty.Server.consume(Server.java:326)
    at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
    at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:756)
    at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:218)
    at org.mortbay.jetty.HttpConnection.consume(HttpConnection.java:404)
Caused by: com.example.myproject.MyProjectServletException
    at com.example.myproject.MyServlet.doPost(MyServlet.java:169)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:727)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:820)
    at org.mortbay.jetty.servlet.ServletHolder.consume(ServletHolder.java:511)
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:30)
    ... 27 more
Caused by: org.hibernate.exception.ConstraintViolationException: could not insert: [com.example.myproject.MyEntity]
    at org.hibernate.exception.SQLStateConverter.convert(SQLStateConverter.java:96)
    at org.hibernate.exception.JDBCExceptionHelper.convert(JDBCExceptionHelper.java:66)
    at org.hibernate.cacheKey.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:64)
    at org.hibernate.persister.entity.AbstractEntityPersister.insert(AbstractEntityPersister.java:2329)
    at org.hibernate.persister.entity.AbstractEntityPersister.insert(AbstractEntityPersister.java:2822)
    at org.hibernate.action.EntityIdentityInsertAction.execute(EntityIdentityInsertAction.java:71)
    at org.hibernate.engine.ActionQueue.execute(ActionQueue.java:268)
    at org.hibernate.event.def.AbstractSaveEventListener.performSaveOrReplicate(AbstractSaveEventListener.java:321)
    at org.hibernate.event.def.AbstractSaveEventListener.performSave(AbstractSaveEventListener.java:204)
    at org.hibernate.event.def.AbstractSaveEventListener.saveWithGeneratedId(AbstractSaveEventListener.java:130)
    at org.hibernate.event.def.DefaultSaveOrUpdateEventListener.saveWithGeneratedOrRequestedId(DefaultSaveOrUpdateEventListener.java:210)
    at org.hibernate.event.def.DefaultSaveEventListener.saveWithGeneratedOrRequestedId(DefaultSaveEventListener.java:56)
    at org.hibernate.event.def.DefaultSaveOrUpdateEventListener.entityIsTransient(DefaultSaveOrUpdateEventListener.java:195)
    at org.hibernate.event.def.DefaultSaveEventListener.performSaveOrUpdate(DefaultSaveEventListener.java:50)
    at org.hibernate.event.def.DefaultSaveOrUpdateEventListener.onSaveOrUpdate(DefaultSaveOrUpdateEventListener.java:93)
    at org.hibernate.impl.SessionImpl.fireSave(SessionImpl.java:705)
    at org.hibernate.impl.SessionImpl.save(SessionImpl.java:693)
    at org.hibernate.impl.SessionImpl.save(SessionImpl.java:689)
    at sun.reflect.GeneratedMethodAccessor5.invoke(Unknown Source)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
    at java.lang.reflect.Method.invoke(Method.java:597)
    at com.example.myproject.MyEntityService.save(MyEntityService.java:59) <-- relevant call (see notes below)
    at com.example.myproject.MyServlet.doPost(MyServlet.java:164)
    ... 32 more
Caused by: java.sql.SQLException: Violation of unique constraint MY_ENTITY_UK_1: duplicate value(s) for column(s) MY_COLUMN in statement [...]
    at org.hsqldb.jdbc.Util.throwError(Unknown Source)
    at org.hsqldb.jdbc.jdbcPreparedStatement.executeUpdate(Unknown Source)
    at com.mchange.v2.c3p0.impl.NewProxyPreparedStatement.executeUpdate(NewProxyPreparedStatement.java:105)
    at org.hibernate.cacheKey.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:57)
    ... 54 more""" == logEntry.getLogValue('Message')
    }

    void 'verify log4j entry processor'() {
        final processor = ofPattern('%-4r [%t] %-5p %C %x - %m%n')
        final logEntry = new LogEntry('0    [main] DEBUG com.vaannila.helloworld.HelloWorld  - Sample debug message')
        expect:
        processor.consume(logEntry)
        [
                (LOGGING_DURATION): '0',
                (THREAD_NAME)     : 'main',
                (PRIORITY)        : 'DEBUG',
                (CALLER_CLASS)    : 'com.vaannila.helloworld.HelloWorld',
                (MESSAGE)         : 'Sample debug message',
        ] == logEntry.logValueMap
    }

    void 'verify log4j toFileFilterConversionMap conversion'() {
        given:
        final filterConversionMap = toFileFilterConversionMap("""# Root logger option
log4j.rootLogger=INFO, file, stdout

# Direct log messages to a log file
log4j.appender.file=org.apache.log4j.RollingFileAppender
log4j.appender.file.File=log-file.log
log4j.appender.file.MaxFileSize=10MB
log4j.appender.file.MaxBackupIndex=10
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

# Direct log messages to stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

log4j.appender.file1=org.apache.log4j.RollingFileAppender
log4j.appender.file1.File=log-file1.log
log4j.appender.file1.MaxFileSize=10MB
log4j.appender.file1.MaxBackupIndex=10
log4j.appender.file1.layout=org.apache.log4j.PatternLayout
log4j.appender.file1.layout.ConversionPattern=%d %5p %37c - %m%n

log4j.appender.file2=org.apache.log4j.RollingFileAppender
log4j.appender.file2.File=log-file2.log
log4j.appender.file2.MaxFileSize=10MB
log4j.appender.file2.MaxBackupIndex=10
log4j.appender.file2.layout=org.apache.log4j.PatternLayout
log4j.appender.file2.layout.ConversionPattern=%d %5p %37c - %m%n

log4j.appender.file3=org.apache.log4j.RollingFileAppender
log4j.appender.file3.File=log-file3.log
log4j.appender.file3.MaxFileSize=10MB
log4j.appender.file3.MaxBackupIndex=10
log4j.appender.file3.layout=org.apache.log4j.PatternLayout
log4j.appender.file3.layout.ConversionPattern=%d - %p - %m%n
""".readLines())

        expect:
        ["log-file.log*" : "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n",
         "log-file1.log*": "%d %5p %37c - %m%n",
         "log-file2.log*": "%d %5p %37c - %m%n",
         "log-file3.log*": "%d - %p - %m%n"] == filterConversionMap
    }

}
