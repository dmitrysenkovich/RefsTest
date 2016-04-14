package main.java.com.epam.refstest;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Strong, soft and phantom references
 * tests. Checks that the object weak
 * reference points to will be garbage
 * collected as fast as it will become
 * unused. Also checks that phantom
 * reference will get a reference to
 * the object being garbage collected
 * right before collecting only.
 */
public class StrongSoftPhantomRefsTest {
    /**
     * Actual main test function. Creates strong,
     * weak and phantom references pointing to
     * one object, fills memory with garbage and
     * force object to be garbage collected verifying
     * order of its destructing.
     */
    public void run() {
        StrongReferenceWrapper strongReferenceWrapper = new StrongReferenceWrapper();
        WeakReference<StrongReferenceWrapper> weakReference = new WeakReference<>(strongReferenceWrapper);
        ReferenceQueue<StrongReferenceWrapper> referenceQueue = new ReferenceQueue<>();
        PhantomReference<StrongReferenceWrapper> phantomReference =
                new PhantomReference<>(strongReferenceWrapper, referenceQueue);
        strongReferenceWrapper = null;
        Thread.yield();

        List<Object> dummiesList = new LinkedList<>();
        boolean referenceQueueStillReturnsNull;
        for (int i = 0; true; i++) {
            dummiesList.add(new Object());
            if (i%10 == 0) {
                System.gc();
                Thread.yield();
            }
            if (weakReference.get() == null) {
                referenceQueueStillReturnsNull = referenceQueue.poll() == null;
                System.out.println("Weak reference is dead");
                break;
            }
            if (i%1000 == 0)
                System.out.println(Runtime.getRuntime().freeMemory());
        }

        for (int i = 0; true; i++) {
            dummiesList.add(new Object());
            if (i%10 == 0) {
                System.gc();
                Thread.yield();
            }
            if (referenceQueue.poll() != null) {
                System.out.println("Phantom reference is dead");
                break;
            }
            if (i%1000 == 0)
                System.out.println(Runtime.getRuntime().freeMemory());
        }

        System.out.println("Phantom reference got object " +
                "after weak reference had gone dead only: " + referenceQueueStillReturnsNull);
    }

    /**
     * Main entry point.
     * @param args command line arguments.
     */
    public static void main(String... args) {
        StrongSoftPhantomRefsTest otherRefsTest = new StrongSoftPhantomRefsTest();
        otherRefsTest.run();
    }

    /**
     * Strong reference wrapper.
     */
    public static class StrongReferenceWrapper {
        /**
         * Just to show message while finalizing.
         */
        @Override
        protected void finalize() {
            try {
                super.finalize();
            } catch (Throwable throwable) {
                System.out.println("Error while finalizing strong reference wrapper");
            }
            System.out.println("Strong reference is dead");
        }
    }
}
