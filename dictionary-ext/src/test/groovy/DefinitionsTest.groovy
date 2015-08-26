import org.prismus.scrambler.MapScrambler
import org.prismus.scrambler.value.ValueDefinition
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class DefinitionsTest extends Specification {

    void 'verify person definitions'() {
        final definition = new ValueDefinition().usingDefinitions('/person-definition.groovy')
        expect:
        definition.definitionMap.size() > 0
    }

    void 'verify address definitions'() {
        final definition = new ValueDefinition().usingDefinitions('/address-definition.groovy')
        expect:
        definition.definitionMap.size() > 0
    }

    void 'verify definitions scanning'() {
        final definition = new ValueDefinition().scanLibraryDefinitions()
        expect:
        definition.definitionMap.size() > 0
        0 < MapScrambler.mapOf(['firstName', 'lastName', 'middleName', 'gender', 'dateOfBirth', 'phone']).next().size()
    }

}
