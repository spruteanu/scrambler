package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class Log4jLogstashTest extends Specification {

    void 'verify log4j to grok config conversion'() {
        given:
        final logstash = new Log4jLogstash(lfSeparator: '\n', oneLogstash: false)
        final writer = new StringWriter()
        logstash.writeLogstashConfig(writer, 'sample3', 'c:/temp', 'sample-3.log', '%d %5p %c [%t] - %m%n')

        expect:
'''
input {
    file {
        path => "c:/temp/**sample-3.log"
        type => "sample3"
        start_position => "beginning"
    }
}
filter {
#if [type] == "sample3" {
    #some matching here
#}
    grok {
        match => { "logLine" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\\w ]{5,}) %{JAVACLASS:EventCategory} \\[(?<Thread>.+)\\] - (?<Message>.+)' }
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
}
output {
#if [type] == "sample3" {
    #some output here
#}
    elasticsearch { hosts => ["localhost:9200"] }
    # Next lines are only for debugging.
    stdout { codec => rubydebug }
    # file {path => "sample3.result" codec => rubydebug}
}''' == writer.toString()
    }

    void 'verify each log4j appender to logstash config'() {
        given:
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        final logstash = new Log4jLogstash(confFolder: folder, oneLogstash: false)
        logstash.toLogstashConfig(new File(folder, 'log4j.properties').path)

        expect:
        3 == LogCrawler.listFiles(folder, '*.rb').size()
    }

    void 'verify log4j appenders to one logstash config'() {
        given:
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        final logstash = new Log4jLogstash(confFolder: folder)
        logstash.toLogstashConfig(new File(folder, 'log4j.properties').path)

        expect:
        1 == LogCrawler.listFiles(folder, 'log4j.properties.rb').size()
    }

}
