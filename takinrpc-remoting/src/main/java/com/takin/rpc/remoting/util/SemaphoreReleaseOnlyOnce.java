package com.takin.rpc.remoting.util;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 限制资源访问的并发数  是说一个资源每次只能由一个资源来访问吗？？
 * 只使用一次的信息量类
 * 使用布尔原子变量，信号量保证只释放一次
 */
public class SemaphoreReleaseOnlyOnce {
    private final AtomicBoolean released = new AtomicBoolean(false);
    private final Semaphore semaphore;

    public SemaphoreReleaseOnlyOnce(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public void release() {
        if (this.semaphore != null) {
            if (this.released.compareAndSet(false, true)) {
                this.semaphore.release();
            }
        }
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
