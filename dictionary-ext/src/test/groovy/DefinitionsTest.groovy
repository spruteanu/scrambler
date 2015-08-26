import org.prismus.scrambler.value.GroovyValueDefinition
import org.prismus.scrambler.value.ValueDefinition
import spock.lang.Specification

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class DefinitionsTest extends Specification {

    void 'verify person definitions'() {
        final definition = new ValueDefinition()
        GroovyValueDefinition.Holder.instance.parseDefinition(definition, '/person-definition.groovy')
        expect:
        definition.definitionMap.size() > 0
    }

    void 'verify address definitions'() {
        final definition = new ValueDefinition()
        GroovyValueDefinition.Holder.instance.parseDefinition(definition, '/address-definition.groovy')
        expect:
        definition.definitionMap.size() > 0
    }

}
