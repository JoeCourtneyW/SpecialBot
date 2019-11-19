package com.joecourtneyw.specialbot.utils.http;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RequestPool {

    private ScheduledExecutorService timer;
    private Queue<Runnable> pool;
    public int requests_per_second;
    private long period;
    private long time_since_last_request;

    public RequestPool(int requests_per_second) {
        this.pool = new LinkedList<>();
        this.requests_per_second = requests_per_second;
        this.period = (long) (1000 * (1.0 / requests_per_second));
        beginTimer();
    }

    private void beginTimer() {
        timer = Executors.newSingleThreadScheduledExecutor();

        timer.scheduleAtFixedRate(this::processPool,
                1000, period, TimeUnit.MILLISECONDS);
    }

    private void processPool() {
        if (time_since_last_request >= period && !pool.isEmpty()) {
            time_since_last_request = 0;
            pool.poll().run();
        } else {
            time_since_last_request += period;
        }
    }

    public void offer(Runnable request) {
        if (time_since_last_request >= period && pool.isEmpty()) {
            request.run();
            time_since_last_request = 0;
        } else {
            pool.add(request);
        }
    }
}
