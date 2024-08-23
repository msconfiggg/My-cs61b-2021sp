package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {

    @Test
    public void IntegerTest() {
        Comparator<Integer> cmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };

        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<Integer>(cmp);

        mad1.addFirst(13); mad1.addLast(154);
        mad1.addFirst(0); mad1.addLast(43);

        assertEquals((Integer)154, mad1.max());
    }

    @Test
    public void StringTest() {
        Comparator<String> cmp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        MaxArrayDeque<String> mad1 = new MaxArrayDeque<String>(cmp);

        mad1.addFirst("and"); mad1.addLast("i");
        mad1.addFirst("discovered"); mad1.addLast("that");

        assertEquals("that", mad1.max());
    }
}