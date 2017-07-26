package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class RegexProcessorTest extends Specification {

    void 'verify date format parser'() {
        expect:
        '\\w+/\\w+/\\w+ \\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('yyyy/MM/dd HH:mm:ss.SSS')
        '\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS')

        '\\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('HH:mm:ss,SSS')
        '\\w+ \\w+ \\w+ \\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('dd MMM yyyy HH:mm:ss,SSS')

        and: 'check converted regex matches value'
        '2008-09-06 10:51:45,473' =~ /${RegexConsumer.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS')}/
        !('2008-09-06 wrong time' =~ /${RegexConsumer.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS')}/)

        '15:49:37,459' =~ /${RegexConsumer.dateFormatToRegEx('HH:mm:ss,SSS')}/
        '06 Nov 1994 08:49:37,459' =~ /${RegexConsumer.dateFormatToRegEx('dd MMM yyyy HH:mm:ss,SSS')}/
    }

    void 'verify reg ex parser'() {
        LogEntry logEntry = new LogEntry('DEBUG | 2008-09-06 10:51:44,817 | DefaultBeanDefinitionDocumentReader.java | 86 | Loading bean definitions')
        expect:
        RegexConsumer.of(~/(\w+) \| (\w+-\w+-\w+ \w+:\w+:\w+.\w+) \| (\w+\.\w+) \| (\d+) \| (.+)/)
                .group('LogLevel', 1)
                .group('Timestamp', 2)
                .group('Caller', 3)
                .group('Line', 4)
                .group('Message', 5)
                .consume(logEntry)
        false == logEntry.isEmpty()
        'DEBUG' == logEntry.getLogValue('LogLevel')
        '2008-09-06 10:51:44,817' == logEntry.getLogValue('Timestamp')
        'DefaultBeanDefinitionDocumentReader.java' == logEntry.getLogValue('Caller')
        '86' == logEntry.getLogValue('Line')
        'Loading bean definitions' == logEntry.getLogValue('Message')

        and: 'verify group entry processor'
        null != (logEntry = new LogEntry('INFO | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase]'))
        RegexConsumer.of(~/(\w+) \| (\w+-\w+-\w+ \w+:\w+:\w+.\w+) \| (\w+\.\w+) \| (\d+) \| (.+)/)
                .group('LogLevel', 1)
                .group('Timestamp', 2)
                .group('Caller', 3)
                .group('Line', 4)
                .group('Message', 5)
                .groupConsumer('Message', new RegexConsumer(~/.+\[(.+)\]/, 'Message').group('SQLErrorCodes', 1))
                .consume(logEntry)
        false == logEntry.isEmpty()
        'SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase]' == logEntry.getLogValue('Message')
        'DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase' == logEntry.getLogValue('SQLErrorCodes')

        and: 'verify parsing log entry with exception'
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
        RegexConsumer.of(~/(?ms)(\w+) \| (\w+-\w+-\w+ \w+:\w+:\w+.\w+) \| (\w+\.\w+) \| (\d+) \| (.+)/)
                .group('LogLevel', 1)
                .group('Timestamp', 2)
                .group('Caller', 3)
                .group('Line', 4)
                .group('Message', 5)
                .groupConsumer('Message', new RegexConsumer(~/(?ms)(${MessageExceptionConsumer.EXCEPTION_REGEX})/, 'Message').group('Exception', 1))
                .consume(logEntry)
        false == logEntry.isEmpty()
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
    ... 54 more
""" == logEntry.getLogValue('Exception')
    }

}
