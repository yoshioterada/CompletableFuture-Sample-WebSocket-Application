package com.yoshio3.websockets.tasks;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yoshio Terada
 */
public class MyCallableTask implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(MyCallableTask.class.getPackage().getName());
    private final int counter;

    public MyCallableTask(int counter) {
        this.counter = counter;
    }

    /*
     並列処理タスク
     ランダムな時間(最長 2 秒間)で処理を待機するタスク    
     */
    @Override
    public Integer call() throws Exception {
        int randomValue = (int) (Math.random() * 2000);
        try {
            Thread.sleep(randomValue);
            if (counter == 20) {
                throw new IllegalStateException("Exception Occur : COUNTER " + counter);
            }
            return counter;
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
