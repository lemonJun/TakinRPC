package test.fiber;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;

/**
 *  
 * @author Administrator
 * @version 1.0
 * @date  2017年5月9日 下午11:58:11
 * @see https://segmentfault.com/a/1190000006079389?from=groupmessage&isappinstalled=0
 * @see http://www.open-open.com/lib/view/open1468892872350.html
 * @since
 */
public class FiberTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException, SuspendExecution {
        int FiberNumber = 1_00_000;
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < FiberNumber; i++) {
            new Fiber(() -> {
                counter.incrementAndGet();
                if (counter.get() == FiberNumber) {
                    System.out.println("done");
                }
                Strand.sleep(Integer.MAX_VALUE);
            }).start();
        }
        latch.await();
    }

}
