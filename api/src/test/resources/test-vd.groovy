/**
 * Example of data definition
 *
 * @author Serge Pruteanu
 */
definition(Integer.random(1, 100))
definition('randomElement', [1, 2, 3].randomOf())
definition(new Date().increment(1, Calendar.HOUR))
constant 'some template string'
definition(new HashSet().of(4.increment(10), 100))
definition(prop1: 2.random(1, 100))
definition('prop2', 'some template string'.random('%s pattern %d'))

'in'.incrementArray('cucu', 10) // todo Serge: WRONG method this one is CORRECT: org.prismus.scrambler.data.GroovyDataDefinition.StringCategory.incrementArray(java.lang.String, java.lang.String, java.lang.Integer)
