import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.ReferenceValue

/**
 * A script that defines person entity for generation
 *
 * @author Serge Pruteanu
 */
//first name    //http://deron.meranda.us/data/census-dist-female-first.txt
//http://deron.meranda.us/data/census-dist-male-first.txt
final femaleFirstNames = 'http://deron.meranda.us/data/census-dist-female-first.txt'.toURL().readLines().collect { String line ->
    return line.split('\\s')[0].toLowerCase().capitalize()
} as Set<String>

final maleFirstNames = 'http://deron.meranda.us/data/census-dist-male-first.txt'.toURL().readLines().collect { String line ->
    return line.split('\\s')[0].toLowerCase().capitalize()
} as Set<String>

final allFirstNames = femaleFirstNames + maleFirstNames

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

//last name     //http://deron.meranda.us/data/popular-last.txt
final lastNames = 'http://deron.meranda.us/data/popular-last.txt'.toURL().readLines().collect { String line ->
    return line.toLowerCase().capitalize()
} as Set<String>
definition(~/(?i)(?:last\s*Name)|(?:last)/, lastNames.randomOf())

//sex
definition(~/(?i)sex/, new ReferenceValue(firstNamePattern) {
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
