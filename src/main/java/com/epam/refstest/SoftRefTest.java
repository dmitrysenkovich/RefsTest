package main.java.com.epam.refstest;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Soft reference test. Checks that
 * soft reference returns null when
 * there is small amount of memory only.
 */
public class SoftRefTest {
    /**
     * Actual test function. Creates
     * soft reference and fills memory
     * with rubbish to force soft reference
     * to give its object to garbage collector.
     */
    public void run() {
        Object object = new Object();
        SoftReference<Object> softReference = new SoftReference<>(object);
        object = null;
        Thread.yield();
        List<Object> dummiesList = new LinkedList<>();

        for (int i = 0; true; i++) {
            dummiesList.add(new Object());
            if (i%10 == 0) {
                System.gc();
                Thread.yield();
            }
            if (softReference.get() == null) {
                break;
            }
            if (i%1000 == 0)
                System.out.println(Runtime.getRuntime().freeMemory());
        }
    }

    /**
     * Main entry point.
     * @param args command line arguments.
     */
    public static void main(String... args) {
        SoftRefTest refsTest = new SoftRefTest();
        refsTest.run();
    }
}
