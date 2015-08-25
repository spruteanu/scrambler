import org.prismus.scrambler.Value
import org.prismus.scrambler.value.AbstractRandomRange
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.ReferenceValue

import java.util.zip.ZipFile

/**
 * Address elements definition
 *
 * @author Serge Pruteanu
 */
definition(~/(?i)\w*number/, new Constant<String>() {
    private Value<Integer> randomNumberValue = Integer.random(1, 99999)

    @Override
    protected String doNext() {
        return randomNumberValue.next().toString()
    }
})

final Map<String, Map<String, String>> stateInfoMap = """Alabama,AL,Montgomery,35-36
Alaska,AK,Juneau,995-999
Arizona,AZ,Phoenix,85-86
Arkansas,AR,Little Rock,716-729
California,CA,Sacramento,900-961
Colorado,CO,Denver,80-81
Connecticut,CT,Hartford,06
Delaware,DE,Dover,197-199
Florida,FL,Tallahassee,32-34
Georgia,GA,Atlanta,30-31
Hawaii,HI,Honolulu,967-968
Idaho,ID,Boise,832-839
Illinois,IL,Springfield,60-62
Indiana,IN,Indianapolis,46-47
Iowa,IA,Des Moines,50-52
Kansas,KS,Topeka,66-67
Kentucky,KY,Frankfort,40-42
Louisiana,LA,Baton Rouge,700-715
Maine,ME,Augusta,039-049
Maryland,MD,Annapolis,206-219
Massachusetts,MA,Boston,010-027
Michigan,MI,Lansing,48-49
Minnesota,MN,Saint Paul,550-567
Mississippi,MS,Jackson,386-399
Missouri,MO,Jefferson City,63-65
Montana,MT,Helena,59
Nebraska,NE,Lincoln,68-69
Nevada,NV,Carson City,889-899
New Hampshire,NH,Concord,039-038
New Jersey,NJ,Trenton,07-08
New Mexico,NM,Santa Fe,870-884
New York,NY,Albany,10-14
North Carolina,NC,Raleigh,27-28
North Dakota,ND,Bismarck,58
Ohio,OH,Columbus,43-45
Oklahoma,OK,Oklahoma City,73-74
Oregon,OR,Salem,97
Pennsylvania,PA,Harrisburg,150-196
Rhode Island,RI,Providence,028-029
South Carolina,SC,Columbia,29
South Dakota,SD,Pierre,57
Tennessee,TN,Nashville,370-385
Texas,TX,Austin,75-79
Utah,UT,Salt Lake City,84
Vermont,VT,Montpelier,05
Virginia,VA,Richmond,220-246
Washington,WA,Olympia,9800-9940
West Virginia,WV,Charleston,246-269
Wisconsin,WI,Madison,53-54
Wyoming,WY,Cheyenne,820-831""".split('\n').collect { String line ->
    final stateInfo = line.split(',')
    return [stateFull: stateInfo[0], state: stateInfo[1], government: stateInfo[2], zipRange: stateInfo[3]]
}.collectEntries {[(it.stateFull): it]}

final Map<String, List<String>> stateCitiesMap = new LinkedHashMap<String, List<String>>(52)
ZipFile zip = null
def inputStream = null
try {
    zip = new ZipFile(new File(this.class.getResource("/us-cities.zip").toURI()))
    inputStream = zip.getInputStream(zip.getEntry("us-cities.csv"))
    inputStream.splitEachLine(',') {
        String stateFull = it.last()
        stateFull = stateFull.endsWith('\"') ? stateFull.substring(0, stateFull.length() - 1) : stateFull
        if (!stateCitiesMap.containsKey(stateFull)) {
            stateCitiesMap.put(stateFull, new ArrayList<String>(256))
        }
        stateCitiesMap.get(stateFull).add(it.first())
    }
} finally {
    try {
        zip?.close()
    } catch (Exception ignore) { }
    try {
        inputStream?.close()
    } catch (Exception ignore) { }
}

definition(~/(?i)street/, new Constant<String>() {
    private static Map<Integer, String> sideMap = [1: 'NE', 2: 'NW', 3: 'SE', 4: 'SW']
    private static Map<Integer, String> suffixMap = [1: 'st', 2: 'nd', 3: 'rd', ]

    private Value<Integer> randomNumber = Integer.random(1, 24)
    private Value<Integer> randomStreetNumber = Integer.random(1, 270)

    @Override
    protected String doNext() {
        final Integer streetId = randomNumber.next()
        final Integer sideIndex = streetId / 5 + 1
        final Integer streetNumber = randomStreetNumber.next()
        final String streetNumberSuffix = getStreetNumberSuffix(streetNumber)

        final String street
        if (streetId >= 1 && streetId < 10) {
            // Street
            street = sideMap.get(sideIndex) + " " + streetNumber + streetNumberSuffix + " St"
        } else if (streetId >= 10 && streetId < 19) {
            // AVE
            street = streetNumber + streetNumberSuffix + " AVE " + sideMap.get(sideIndex as Integer)
        } else {
            street = streetNumber + streetNumberSuffix + ' Main St'
        }
        return street
    }

    protected String getStreetNumberSuffix(Integer streetNumber) {
        final Integer index = streetNumber % 10
        return suffixMap.containsKey(index) ? suffixMap.get(index) : 'th'
    }

})

definition(~/(?i)state/, new Constant<String>() {
    private Value randomState = stateInfoMap.keySet().randomOf()
    private String state = getContextProperty('state')
    @Override
    protected String doNext() {
        return this.state == null ? randomState.next() : this.state
    }
})

definition(~/(?i)city/, new ReferenceValue(~/(?i)state/) {
    @Override
    protected Object doNext() {
        String state = super.doNext()
        return stateCitiesMap.get(state).randomOf().next()
    }
})

definition(~/(?i)(?:postal\w*)|(?:zip\w*)/, new ReferenceValue(~/(?i)state/) {
    private AbstractRandomRange<Integer> randomRange = Integer.random(1, 1000)
    @Override
    protected Object doNext() {
        final String state = super.doNext()
        final stateInfo = stateInfoMap.get(state)
        String code = stateInfo.get('zipRange')
        if (code) {
            String[] range = code.split('-')
            if (range.length > 1) {
                String format = '%0' + range[0].length() + 'd'
                randomRange.between(Integer.valueOf(range[0]), Integer.valueOf(range[1]))
                code = stateInfo.get('state') + '-' + String.format(format, randomRange.next())
            } else {
                code = stateInfo.get('state') + '-' + range[0]
            }
        }
        return code
    }
})
