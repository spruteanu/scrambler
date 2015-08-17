import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.ReferenceValue

import java.util.zip.ZipFile

/**
 * A script that defines person entity for generation
 *
 * @author Serge Pruteanu
 */

Set<String> femaleFirstNames
Set<String> lastNames
Set<String> allFirstNames = new LinkedHashSet<>(6000)

ZipFile zip = null
try {
    zip = new ZipFile(new File(this.class.getResource('/census-names.zip').toURI()))
//http://deron.meranda.us/data/census-dist-female-first.txt
    final parseClosure = { String line ->
        return line.split('\\s')[0].toLowerCase().capitalize()
    }
    InputStream inputStream = zip.getInputStream(zip.getEntry('census-dist-female-first.txt'))
    femaleFirstNames = inputStream.readLines().collect(parseClosure) as Set<String>
    try { inputStream?.close() } catch (Exception ignore) { }

//http://deron.meranda.us/data/census-dist-male-first.txt
    inputStream = zip.getInputStream(zip.getEntry('census-dist-male-first.txt'))
    final maleFirstNames = inputStream.readLines().collect(parseClosure) as Set<String>
    try { inputStream?.close() } catch (Exception ignore) { }

    allFirstNames += femaleFirstNames
    allFirstNames += maleFirstNames

//http://deron.meranda.us/data/census-dist-2500-last.txt
    inputStream = zip.getInputStream(zip.getEntry('census-dist-2500-last.txt'))
    lastNames = inputStream.readLines().collect(parseClosure) as Set<String>
    try { inputStream?.close() } catch (Exception ignore) { }
} finally {
    try {
        zip?.close()
    } catch (Exception ignore) { }
}


final firstNamePattern = ~/(?i)(?:first\s*Name)|(?:first)/
definition(firstNamePattern, allFirstNames.randomOf())

//middle Name
definition(~/(?i)middle\w*/, new ReferenceValue(firstNamePattern) {
    Value randomRange = Integer.random(1, 100)
    Value randomMiddle = allFirstNames.randomOf()

    @Override
    protected Object doNext() {
        String value = null
        final middleRandomRange = randomRange.next()
        if (50 <= middleRandomRange && 61 > middleRandomRange) {
            value = randomMiddle.next()
        }
        return value
    }
})

definition(~/(?i)(?:last\s*Name)|(?:last)/, lastNames.randomOf())

//gender
definition(~/(?i)gender/, new ReferenceValue(firstNamePattern) {
    @Override
    protected Object doNext() {
        final firstName = super.doNext()
        return femaleFirstNames.contains(firstName) ? 'Female' : 'Male'
    }
})

//dob
definition(~/(?i)(?:\w*dob)|(?:\w*birth)/, new Constant() {
    Value group1 = Integer.random(1, 12)
    Value group2 = Integer.random(1, 31)
    Value group3 = Integer.random(1920, 2015)

    @Override
    protected Object doNext() {
        return String.format('%s/%s/%s', group1.next(), group2.next(), group3.next())
    }
})

//phone
definition(~/(?i)\w*phone/, new Constant() {
    Value group1 = Integer.random(100, 999)
    Value group2 = Integer.random(100, 999)
    Value group3 = Integer.random(1000, 9999)

    @Override
    protected Object doNext() {
        return String.format('(%s)-%s-%s', group1.next(), group2.next(), group3.next())
    }
})
