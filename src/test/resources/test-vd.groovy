/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
//org.prismus.scrambler.builder.ValueDefinition.with {
random 1, 100
randomOf([1, 2, 3])
incremental new Date(), 1, Calendar.HOUR
constant 'some template string'
random(new HashSet(), 4.incremental(10), 100)
prop1: 2.random(1, 100)
//}
