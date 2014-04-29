import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class SolutionTest extends Specification {

    void 'verify bit count on swap'(int expected, int count, String inValue) {
        given:
        final solution = new Solution()

        expect:
        expected == solution.countOnBits(count, inValue)

        where:
        expected << [6, 5, ]
        count << [8, 6,]
        inValue << ['1 0 0 1 0 0 1 0', '0 0 0 0 1 0']
/*
// 1001110001101
// 0101110001100
// 0101010000011
// 0101010101010
// 0101010101011
// 1101010101000
// 1 0 0 1 0 0 1 0 1 0 0 1 0 0 1 0
// 1 0 0 1 0 0 1 0 1 0 0 1 0 0 0 0
// 1 0 0 1 1 1 1 1 1 1 1 1 1 1 1 0
// 1 0 0 1 1 0 1 1 1 1 0 0 1 1 1 0
// 1 1 0 1 1 1 1 0 1 1 1 0 1 1 0 1
// 1 1 0 1 1 1 1 0 1 0 1 1 1 1 0 0 1
// 1 1 0 1 1 1 1 0 0 0 1 1 1 1 0 0 1
// 1 1 0 1 1 1 1 0 0 0 0 1 1 1 1 0 0
// 1 1 0 1 1 1 1 0 0 0 0 1 1 1 0 0 0
// 1 1 0 1 0 1 0 1 0 1 0 1 1 1 0 0 0
// 1 1 0 1 0 1 0 1 0 1 1 1 0 1 1 0 1
// 0 1 0 1 0 1 0 1 0 1 1 1 0 0 0 0 1
// 0 1 0 1 1 1 1 1 1 1 1 1 0 0 0 0 1
// 0 1 0 1 1 0 1 0 1 1 1 0 1 0 1 1 0
// 1111011111111
// 1111111111110
// 0111111111111
// 1111111111111
// 0000000000000
* */
    }

}
