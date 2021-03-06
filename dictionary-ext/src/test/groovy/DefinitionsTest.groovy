import org.prismus.scrambler.MapScrambler
import org.prismus.scrambler.data.DataDefinition
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class DefinitionsTest extends Specification {

    void 'verify person definitions'() {
        final definition = new DataDefinition().usingDefinitions('/person-definition.groovy')
        expect:
        definition.definitionMap.size() > 0
    }

    void 'verify address definitions'() {
        final definition = new DataDefinition().usingDefinitions('/address-definition.groovy')
        expect:
        definition.definitionMap.size() > 0
    }

    void 'verify definitions scanning'() {
        final definition = new DataDefinition().usingLibraryDefinitions()
        expect: 'verify definitions loaded'
        definition.definitionMap.size() > 0

        and: 'verify person names generation'
        0 < MapScrambler.mapOf(['firstName', 'lastName', 'middleName', 'gender', 'dateOfBirth', 'phone']).next().size()

        and: 'verify address generation for Washington state'
        0 < MapScrambler.mapOf(['Building Number', 'Street', 'State', 'City', 'Postal Code', 'email'],
                [state: 'Washington', 'domain': 'google.com']).next().size()
    }

}
