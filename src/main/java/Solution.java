public class Solution {

    static final char ZERO_BIT = '0';
    static final char ONE_BIT = '1';

    public static void main(String args[]) throws Exception {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
        final Solution solution = new Solution();
        System.out.println(solution.countOnBits(solution.readArraySize(), solution.readLine()));
    }

    String readLine() {
        final String inValue = System.console().readLine();
        if (inValue == null || inValue.trim().length() == 0) {
            throw new RuntimeException("An empty string is entered");
        }
        return inValue;
    }

    int readArraySize() {
        final String inValue = readLine();
        int result;
        try {
            result = Integer.parseInt(inValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("Array size must be an integer: entered value: %s", inValue), e);
        }
        if (result < 1 || result > 100000) {
            throw new RuntimeException("0/1 arrays size must be between 1 and 100_000");
        }
        return result;
    }

    int countOnBits(int nSize, String inValue) {
        int count0 = 0;
        int count1 = 0;

        int max0seqIdx = 0;
        int max0count = 0;

        int max1seqIdx = 0;
        int max1count = 0;

        int currentIdx = 0;
        int currentCount = 0;

        final char[] chars = inValue.toCharArray();
        char currentBit = chars[0];
        for (char digit : chars) {
            switch (digit) {
                case ZERO_BIT:
                    if (currentBit != ZERO_BIT) {
                        currentBit = digit;
                        if (currentCount > max1count) {
                            max1seqIdx = currentIdx - currentCount;
                            max1count = currentCount;
                        }
                        currentCount = 0;
                    }
                    count0++;
                    currentIdx++;
                    currentCount++;
                    break;
                case ONE_BIT:
                    if (currentBit != ONE_BIT) {
                        currentBit = digit;
                        if (currentCount > max0count) {
                            max0seqIdx = currentIdx - currentCount;
                            max0count = currentCount;
                        }
                        currentCount = 0;
                    }
                    count1++;
                    currentIdx++;
                    currentCount++;
                    break;
            }
        }
        final int nDigits = count0 + count1;
        if (nDigits < nSize) {
            throw new RuntimeException(String.format("Entered 0/1 array size: %d doesn't correspond entered array size: %d", nDigits, nSize));
        }
        return count1;
    }

}
// 1001110001101
// 0101110001100
// 0101010000011
// 0101010101010
// 0101010101011
// 1101010101000
// 1 0 0 1 0 0 1 0
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
// 0 0 0 0 1 0
// 1111011111111
// 1111111111110
// 0111111111111
// 1111111111111
// 0000000000000
/*
You are given an array of size N elements: d[0], d[1], ... d[N - 1] where each d[i] is either 0 or 1. You can perform at most one move on the array: choose any two integers [L, R], and flip all the elements between (and including) the Lth and Rth bits. L and R represent the left-most and right-most index of the bits marking the boundaries of the segment which you have decided to flip.

What is the maximum number of '1'-bits (indicated by S) which you can obtain in the final bit-string? 'Flipping' a bit means, that a 0 is transformed to a 1 and a 1 is transformed to a 0.

Input Format:
A single integer N
The next line contains the N elements in the array separated by a space: d[0] d[1] ... d[N - 1]

Output format:
Output a single integer that denotes the maximum number of 1-bits which can be obtained in the final bit string

Constraints:
1 <= N <= 100000
d[i] can only be 0 or 1
0 <= L <= R < n

Sample Input:
8
1 0 0 1 0 0 1 0

Sample Output:
6

Explanation:
We can get a maximum of 6 ones in the given binary array by performing either of the following operations:
Flip [1, 5] ==> 1 1 1 0 1 1 1 0
or
Flip [1, 7] ==> 1 1 1 0 1 1 0 1

* */