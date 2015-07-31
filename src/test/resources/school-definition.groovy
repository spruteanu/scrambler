import org.prismus.scrambler.DataScrambler
import org.prismus.scrambler.beans.Address
import org.prismus.scrambler.beans.ClassRoom
import org.prismus.scrambler.beans.Person
import org.prismus.scrambler.beans.School
import org.prismus.scrambler.value.ReferenceValue

/**
 * School entities definition, with adequate address and person definitions
 *
 * @author Serge Pruteanu
 */
definition('*Id', 1.increment())
definition('name', ['Enatai', 'Medina', 'Value Crest', 'Newport', 'Cherry Crest', 'Eastgate Elementary'].randomOf())

final personDefinition = DataScrambler.parseDefinition('/person-definition.groovy')
final addressDefinition = DataScrambler.parseDefinition('/address-definition.groovy')
usingDefinition(personDefinition)

definition('address', DataScrambler.instanceOf(Address).usingDefinitions(addressDefinition))

definition('staff', [].of(DataScrambler.instanceOf(Person)
        .usingDefinitions(personDefinition).usingDefinitions(addressDefinition)))

definition('principle', new ReferenceValue('staff') {
    @Override
    Object next() {
        final staffList = super.next()
        final principle = staffList[0]
        setValue(principle)
        return principle
    }
})

definition('rooms', [].of(DataScrambler.instanceOf(ClassRoom)
        .usingDefinitions(personDefinition).usingDefinitions(addressDefinition)
        .usingDefinitions(
            parent: School.reference(), schoolId: School.reference('schoolId'),
            roomNumber: "101A".random(4),
            teacher: new ReferenceValue(School, 'staff') {
                @Override
                Object next() {
                    final staffList = super.next()
                    final principle = staffList[0]
                    setValue(principle)
                    return principle
                }
            },
            students: [].of(DataScrambler.instanceOf(Person)
                    .usingDefinitions(personDefinition).usingDefinitions(addressDefinition))
        )
))
