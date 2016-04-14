package main.java.com.epam.refstest;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Soft and phantom references test.
 * Checks that phantom reference is
 * the last one that has object.
 */
public class SoftPhantomRefsTest {
    /**
     * Actual main test function. Creates soft
     * and phantom references to one object,
     * fills memory with rubbish and checks that
     * phantom reference is the last one that
     * can own object before its complete destruction.
     */
    public void run() {
        Object object = new Object();
        SoftReference<Object> softReference = new SoftReference<>(object);
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference =
                new PhantomReference<>(object, referenceQueue);
        PhantomReferenceWrapper phantomReferenceWrapper = new PhantomReferenceWrapper(referenceQueue);
        phantomReferenceWrapper.start();
        object = null;
        Thread.yield();

        List<Object> dummiesList = new LinkedList<>();
        for (int i = 0; true; i++) {
            if (softReference.get() == null) {
                System.out.println("Soft reference is dead");
                break;
            }
            dummiesList.add(new Object());
            if (i%10 == 0) {
                System.gc();
                Thread.yield();
            }
            if (softReference.get() == null) {
                System.out.println("Soft reference is dead");
                break;
            }
            if (i%1000 == 0)
                System.out.println(Runtime.getRuntime().freeMemory());
        }
        try {
            phantomReferenceWrapper.join();
        } catch (InterruptedException e) {
            System.out.println("Error while joining phantom reference wrapper");
        }
    }

    /**
     * Main entry point.
     * @param args command line arguments.
     */
    public static void main(String... args) {
        SoftPhantomRefsTest softPhantomRefsTest = new SoftPhantomRefsTest();
        softPhantomRefsTest.run();
    }

    /**
     * Phantom reference wrapper.
     */
    public static class PhantomReferenceWrapper extends Thread {
        private ReferenceQueue<Object> referenceQueue;

        public PhantomReferenceWrapper(ReferenceQueue<Object> referenceQueue) {
            this.referenceQueue = referenceQueue;
        }

        /**
         * Will be checking queue for
         * garbage collecting object.
         */
        @Override
        public void run() {
            try {
                Object object = referenceQueue.remove();
            } catch (InterruptedException e) {
                System.out.println("Error while removing object from reference queue");
            }
            System.out.println("Phantom reference is dead");
        }
    }
}
