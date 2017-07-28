import org.prismus.scrambler.log.LogScramblerTest

/**
 * @author Serge Pruteanu
 */
final folder = new File(LogScramblerTest.protectionDomain.codeSource.location.path)
log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',)
log4jSourceFolder(folder, '%-4r [%t] %-5p %c %x - %m%n', '*sample-2.log',)
