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
        # Bellow line is not for continuous log watch, path will be parsed always from start position
        sincedb_path => "/dev/null"
        start_position => "beginning"
    }
}
filter {
#if [type] == "sample3" {
    #some matching here
#}
    grok {
        match => { "message" => '%{TIMESTAMP_ISO8601:Date} (?<Priority>[\\w ]{5,}) %{JAVACLASS:EventCategory} \\[(?<Thread>.+)\\] - (?<Message>.+)' }
        # Date-format => yyyy-MM-dd HH:mm:ss,SSS
    }
    mutate {
        strip => "Message"
        # Remove not needed fields
        remove_field => [ 'message', '@version', '@timestamp', 'host', 'path']
    }
}
output {
#if [type] == "sample3" {
    #some output here
#}
    elasticsearch { hosts => ["localhost:9200"] }
    index => "sample3-%{+YYYY.MM.dd}"
    #template => "absolute_file_path_of_logstash_json_config"
    #document_id => "document_id_if_needed"
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
        3 == LogCrawler.listFiles(folder, 'sample*.rb').size()
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
