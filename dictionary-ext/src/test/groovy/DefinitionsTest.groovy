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
        final definition = new ValueDefinition().usingLibraryDefinitions()
        expect: 'verify definitions loaded'
        definition.definitionMap.size() > 0

        and: 'verify person names generation'
        0 < MapScrambler.mapOf(['firstName', 'lastName', 'middleName', 'gender', 'dateOfBirth', 'phone']).next().size()

        and: 'verify address generation for Washington state'
        0 < MapScrambler.mapOf(['Building Number', 'Street', 'State', 'City', 'Postal Code'], [state: 'Washington']).next().size()
    }

}
