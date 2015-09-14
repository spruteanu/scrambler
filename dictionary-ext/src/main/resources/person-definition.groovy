import groovy.transform.CompileStatic
import org.prismus.scrambler.CollectionScrambler
import org.prismus.scrambler.NumberScrambler
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.RandomString
import org.prismus.scrambler.value.ReferenceValue

import java.util.regex.Pattern
import java.util.zip.ZipFile

/**
 * A script that defines person entity for generation
 *
 * @author Serge Pruteanu
 */

def (Set<String> allFirstNames, Set<String> lastNames, Set<String> femaleFirstNames) = loadNames()

definition(~/(?i)(?:first\s*Name)|(?:first)/, allFirstNames.randomOf())
definition(~/(?i)middle\w*/, new MiddleNameValue(allFirstNames, ~/(?i)middle\w*/))
definition(~/(?i)(?:last\s*Name)|(?:last)/, lastNames.randomOf())
definition(~/(?i)gender/, new GenderValue(femaleFirstNames, ~/(?i)gender/))
definition(~/(?i)(?:\w*dob)|(?:\w*birth)/, new DobValue())
definition(~/(?i)\w*phone/, new PhoneValue())
definition(~/(?i)\w*email\w*/, EmailValue.of(getContextMap()))

@CompileStatic
private static List loadNames() {
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
        try {
            inputStream?.close()
        } catch (Exception ignore) { }

//http://deron.meranda.us/data/census-dist-male-first.txt
        inputStream = zip.getInputStream(zip.getEntry('census-dist-male-first.txt'))
        final maleFirstNames = inputStream.readLines().collect(parseClosure) as Set<String>
        try {
            inputStream?.close()
        } catch (Exception ignore) { }

        allFirstNames += femaleFirstNames
        allFirstNames += maleFirstNames

//http://deron.meranda.us/data/census-dist-2500-last.txt
        inputStream = zip.getInputStream(zip.getEntry('census-dist-2500-last.txt'))
        lastNames = inputStream.readLines().collect(parseClosure) as Set<String>
        try {
            inputStream?.close()
        } catch (Exception ignore) { }
    } finally {
        try {
            zip?.close()
        } catch (Exception ignore) { }
    }
    return [allFirstNames, lastNames, femaleFirstNames]
}

@CompileStatic
class MiddleNameValue extends ReferenceValue {
    private final Value<Integer> randomRange
    private final Value randomMiddle

    MiddleNameValue(Set<String> allFirstNames, Pattern fieldPattern) {
        super(fieldPattern)
        randomRange = NumberScrambler.random(1, 100)
        randomMiddle = CollectionScrambler.randomOf(allFirstNames)
    }

    @Override
    protected Object doNext() {
        String value = null
        final middleRandomRange = randomRange.next()
        if (50 <= middleRandomRange && 61 > middleRandomRange) {
            value = randomMiddle.next()
        }
        return value
    }
}

@CompileStatic
class GenderValue extends ReferenceValue {
    private final Set<String> femaleFirstNames

    GenderValue(Set<String> femaleFirstNames, Pattern fieldPattern) {
        super(fieldPattern)
        this.femaleFirstNames = femaleFirstNames
    }

    @Override
    protected Object doNext() {
        final firstName = super.doNext()
        return femaleFirstNames.contains(firstName) ? 'Female' : 'Male'
    }
}

@CompileStatic
class DobValue extends Constant<String> {
    private Value group1 = NumberScrambler.random(1, 12)
    private Value group2 = NumberScrambler.random(1, 31)
    private Value group3 = NumberScrambler.random(1920, 2015)

    @Override
    protected String doNext() {
        return String.format('%s/%s/%s', group1.next(), group2.next(), group3.next())
    }
}

@CompileStatic
class PhoneValue extends Constant<String> {
    private Value group1 = NumberScrambler.random(100, 999)
    private Value group2 = NumberScrambler.random(100, 999)
    private Value group3 = NumberScrambler.random(1000, 9999)

    @Override
    protected String doNext() {
        return String.format('(%s)-%s-%s', group1.next(), group2.next(), group3.next())
    }
}

@CompileStatic
class DomainValue extends Constant<String> {
    private final Value<String> nameValue;
    private final Value<String> extensionValue;

    DomainValue(Value<String> nameValue, Value<String> extensionValue) {
        this.nameValue = nameValue
        this.extensionValue = extensionValue
    }

    @Override
    protected String doNext() {
        return String.format('%s.%s', nameValue.next() , extensionValue.next())
    }

    static DomainValue of(Map<String, Object> contextMap) {
        Value<String> domainValue = new RandomString(EmailValue.getTemplateString()).maxCount(20)
        Value<String> extensionValue = new RandomString(EmailValue.getTemplateString()).maxCount(10)
        if (contextMap.containsKey('domain-template')) {
            domainValue = new RandomString(contextMap.get('domain-template').toString())
        }
        if (contextMap.containsKey('domain-extension')) {
            domainValue = new Constant<String>(contextMap.get('domain-extension').toString())
        }
        return new DomainValue(domainValue, extensionValue)
    }
}

@CompileStatic
class EmailValue extends Constant<String> {
    private final Value<String> domainValue;
    private final Value<String> nameValue;

    EmailValue(Value<String> domainValue, Value<String> nameValue) {
        this.domainValue = domainValue
        this.nameValue = nameValue
    }

    @Override
    protected String doNext() {
        return String.format('%s@%s', nameValue.next() , domainValue.next())
    }

    static Value<String> of(Map<String, Object> contextMap) {
        if (contextMap.containsKey('email')) {
            return new Constant<String>(contextMap.get('email').toString())
        }
        if (contextMap.containsKey('domain')) {
            String domainNamePattern = getTemplateString()
            return new EmailValue(
                    new Constant<String>(contextMap.get('domain').toString()),
                    new RandomString(domainNamePattern).maxCount(20)
            )
        }
        return new EmailValue(DomainValue.of(contextMap), new RandomString(getTemplateString()))
    }

    @CompileStatic
    static String getTemplateString() {
        List list = new ArrayList()
        list += 'a'..'z'
        list += 0..9
        list += ['.', '_', '-']
        return list.join('')
    }
}
