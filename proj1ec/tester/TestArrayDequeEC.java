package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.algs4.StdRandom;

public class TestArrayDequeEC {
    @Test
    public void randomTest() {
        int TESTNUMBER = 100;
        ArrayDequeSolution<Integer> right = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> buggy = new StudentArrayDeque<>();
        String message = new String();

        for (int i = 0; i < TESTNUMBER; i++) {
            int operationFlag = StdRandom.uniform(4);
            if (operationFlag == 0) {
                int addInteger = StdRandom.uniform(100);
                right.addFirst(addInteger);
                buggy.addFirst(addInteger);
                message += ("addFirst(" + addInteger + ")\n");
            } else if (operationFlag == 1) {
                int addInteger = StdRandom.uniform(100);
                right.addLast(addInteger);
                buggy.addLast(addInteger);
                message += ("addLast(" + addInteger + ")\n");
            } else if (operationFlag == 2) {
                if (!(right.isEmpty() || buggy.isEmpty())) {
                    Integer r_first = right.removeFirst();
                    Integer b_first = buggy.removeFirst();
                    message += ("removeFirst()\n");
                    assertEquals(message, r_first, b_first);
                }
            } else if (operationFlag == 3) {
                if (!(right.isEmpty() || buggy.isEmpty())) {
                    Integer r_last = right.removeLast();
                    Integer b_last = buggy.removeLast();
                    message += ("removeLast()\n");
                    assertEquals(message, r_last, b_last);
                }
            }
        }

    }
}
