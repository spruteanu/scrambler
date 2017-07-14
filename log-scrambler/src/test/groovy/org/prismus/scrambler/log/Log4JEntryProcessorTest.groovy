package org.prismus.scrambler.log

import spock.lang.Specification

import static org.prismus.scrambler.log.Log4jEntryProcessor.*
import static org.prismus.scrambler.log.RegexEntryProcessor.of

/**
 * @author Serge Pruteanu
 */
class Log4JEntryProcessorTest extends Specification {

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
        final entryProcessor = new Log4jEntryProcessor()
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
                     '(\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+)', '(\\w+ \\w+ \\w+ \\w+:\\w+:\\w+,\\w+)',
                     '(\\w+:\\w+:\\w+,\\w+)', '(\\w+ \\w+ \\w+ \\w+:\\w+:\\w+,\\w+)',
                     '(\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+)',
        ]
    }

    void 'verify conversionPatternToRegEx'() {
        final entryProcessor = new Log4jEntryProcessor()
        String patternRegEx = conversionPatternToRegex(entryProcessor, specString)

        expect:
        patternRegEx == expected

        where:
        specString << [
                '%-6r [%15.15t] %-5p %30.30c %x - %m%n',
                '%r [%t] %-5p %c %x - %m%n',
                '%5p | %d | %F | %L | %m%n',
        ]
        expected << [
                '(\\s*[\\d^ ]{6,}) \\[([\\w^ ]{15,15})\\] (\\s*\\w{5,}) ([^ ]{30,30}) ([\\w^ ]*) - (.+)',
                '([\\d^ ]+) \\[([\\w^ ]+)\\] (\\s*\\w{5,}) ([^ ]+) ([\\w^ ]*) - (.+)',
                '(\\w{5,}) \\| (\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+) \\| ([^ ]+) \\| ([\\d^ ]+) \\| (.+)',
        ]
    }

    void 'verify conversionPatternToRegEx with log entry parsing'() {
        final conversionPattern = '%-4r [%t] %-5p %C %x - %m%n'
        String patternRegEx = conversionPatternToRegex(new Log4jEntryProcessor(), conversionPattern)
        LogEntry logEntry = of(~/${patternRegEx}/)
                .register(1, 'logTime')
                .register(2, 'ThreadName')
                .register(3, 'LogLevel')
                .register(4, 'CallerClass')
                .register(5, 'NDC')
                .register(6, 'Message')
                .process(new LogEntry('0    [main] DEBUG com.vaannila.helloworld.HelloWorld  - Sample debug message'))

        expect:
        '0   ' == logEntry.getEntryValue('logTime')
        'main' == logEntry.getEntryValue('ThreadName')
        'DEBUG' == logEntry.getEntryValue('LogLevel')
        'com.vaannila.helloworld.HelloWorld' == logEntry.getEntryValue('CallerClass')
        null == logEntry.getEntryValue('NDC')
        'Sample debug message' == logEntry.getEntryValue('Message')

        and: 'with exception'
        null != (logEntry = new LogEntry("""ERROR | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | OMG, Something bad happened
javax.servlet.ServletException: Something bad happened
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:60)
    at com.example.myproject.ExceptionHandlerFilter.doFilter(ExceptionHandlerFilter.java:28)
    at com.example.myproject.OutputBufferFilter.doFilter(OutputBufferFilter.java:33)
    at org.mortbay.jetty.servlet.ServletHandler.handle(ServletHandler.java:388)
    at org.mortbay.jetty.security.SecurityHandler.handle(SecurityHandler.java:216)
    at org.mortbay.jetty.servlet.SessionHandler.handle(SessionHandler.java:182)
    at org.mortbay.jetty.handler.ContextHandler.handle(ContextHandler.java:765)
    at org.mortbay.jetty.webapp.WebAppContext.handle(WebAppContext.java:418)
    at org.mortbay.jetty.handler.HandlerWrapper.handle(HandlerWrapper.java:152)
    at org.mortbay.jetty.Server.handle(Server.java:326)
    at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
    at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:756)
    at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:218)
    at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:404)
Caused by: com.example.myproject.MyProjectServletException
    at com.example.myproject.MyServlet.doPost(MyServlet.java:169)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:727)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:820)
    at org.mortbay.jetty.servlet.ServletHolder.handle(ServletHolder.java:511)
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:30)
    ... 27 more
Caused by: org.hibernate.exception.ConstraintViolationException: could not insert: [com.example.myproject.MyEntity]
    at org.hibernate.exception.SQLStateConverter.convert(SQLStateConverter.java:96)
    at org.hibernate.exception.JDBCExceptionHelper.convert(JDBCExceptionHelper.java:66)
    at org.hibernate.id.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:64)
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
    at org.hibernate.id.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:57)
    ... 54 more
"""))
        false == of(~/(?ms)${conversionPatternToRegex(new Log4jEntryProcessor(), '%5p | %d | %F | %L | %m%n')}/)
                .register(1, 'LogLevel')
                .register(2, 'Timestamp')
                .register(3, 'CallerFileName')
                .register(4, 'Line')
                .register(5, 'Message')
                .process(logEntry)
                .isEmpty()
        'ERROR' == logEntry.getEntryValue('LogLevel')
        '2008-09-06 10:51:45,473' == logEntry.getEntryValue('Timestamp')
        'SQLErrorCodesFactory.java' == logEntry.getEntryValue('CallerFileName')
        '128' == logEntry.getEntryValue('Line')

        """OMG, Something bad happened
javax.servlet.ServletException: Something bad happened
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:60)
    at com.example.myproject.ExceptionHandlerFilter.doFilter(ExceptionHandlerFilter.java:28)
    at com.example.myproject.OutputBufferFilter.doFilter(OutputBufferFilter.java:33)
    at org.mortbay.jetty.servlet.ServletHandler.handle(ServletHandler.java:388)
    at org.mortbay.jetty.security.SecurityHandler.handle(SecurityHandler.java:216)
    at org.mortbay.jetty.servlet.SessionHandler.handle(SessionHandler.java:182)
    at org.mortbay.jetty.handler.ContextHandler.handle(ContextHandler.java:765)
    at org.mortbay.jetty.webapp.WebAppContext.handle(WebAppContext.java:418)
    at org.mortbay.jetty.handler.HandlerWrapper.handle(HandlerWrapper.java:152)
    at org.mortbay.jetty.Server.handle(Server.java:326)
    at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
    at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:756)
    at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:218)
    at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:404)
Caused by: com.example.myproject.MyProjectServletException
    at com.example.myproject.MyServlet.doPost(MyServlet.java:169)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:727)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:820)
    at org.mortbay.jetty.servlet.ServletHolder.handle(ServletHolder.java:511)
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:30)
    ... 27 more
Caused by: org.hibernate.exception.ConstraintViolationException: could not insert: [com.example.myproject.MyEntity]
    at org.hibernate.exception.SQLStateConverter.convert(SQLStateConverter.java:96)
    at org.hibernate.exception.JDBCExceptionHelper.convert(JDBCExceptionHelper.java:66)
    at org.hibernate.id.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:64)
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
    at org.hibernate.id.insert.AbstractSelectingDelegate.performInsert(AbstractSelectingDelegate.java:57)
    ... 54 more
""" == logEntry.getEntryValue('Message')
    }

    void 'verify log4j entry processor'() {
        final processor = ofPattern('%-4r [%t] %-5p %C %x - %m%n')
        expect:
        [
                (LOGGING_DURATION_SP): '0   ',
                (THREAD_NAME_SP)     : 'main',
                (PRIORITY_SP)        : 'DEBUG',
                (CALLER_CLASS_SP)    : 'com.vaannila.helloworld.HelloWorld',
                (MESSAGE_SP)         : 'Sample debug message',
        ] == processor.process(new LogEntry('0    [main] DEBUG com.vaannila.helloworld.HelloWorld  - Sample debug message')).entryValueMap
    }

}
