package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> right = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        right.addLast(4); buggy.addLast(4);
        right.addLast(5); buggy.addLast(5);
        right.addLast(6); buggy.addLast(6);

        assertEquals(right.size(), buggy.size());
        assertEquals(right.removeLast(), buggy.removeLast());
        assertEquals(right.removeLast(), buggy.removeLast());
        assertEquals(right.removeLast(), buggy.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> right = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                right.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int r_size = right.size();
                int b_size = buggy.size();
                assertEquals(r_size, b_size);
            } else if (operationNumber == 2) {
                // getLast
                assertEquals(right.size(), buggy.size());
                if (right.size() > 0) {
                    Integer r_last = right.getLast();
                    Integer b_last = buggy.getLast();
                    assertEquals(r_last, b_last);
                }
            } else if (operationNumber == 3) {
                // removeLast
                assertEquals(right.size(), buggy.size());
                if (right.size() > 0) {
                    Integer r_last = right.removeLast();
                    Integer b_last = buggy.removeLast();
                    assertEquals(r_last, b_last);
                }
            }
        }
    }
}
