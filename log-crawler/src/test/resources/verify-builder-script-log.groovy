import org.prismus.scrambler.log.LogCrawlerTest

/**
 * @author Serge Pruteanu
 */
final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
log4j {
    file = folder
    pattern = new File(folder, 'log4j.properties').path
}
