package com.example.opentelemetry.demo;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
@Service
public class ItemService {
    private static int bookOrderId = 0;
    private static int movieOrderId = 0;
    private final Counter bookCounter;
    private final Counter movieCounter;
    private final AtomicInteger activeUsers;

    public ItemService(CompositeMeterRegistry meterRegistry) {
        // Initialize counters
        this.bookCounter = meterRegistry.counter("order.books");
        this.movieCounter = meterRegistry.counter("order.movies");

        // Initialize gauge for active users
        this.activeUsers = meterRegistry.gauge("number.of.active.users", new AtomicInteger(0));

        // Set a random value for active users for demonstration
        Random random = new Random();
        this.activeUsers.set(random.nextInt(100));
    }

    public Number fetchActiveUsers() {
        return activeUsers.get();
    }

    public String orderBook() {
        bookOrderId += 1;
        bookCounter.increment();
        return "Ordered Book with id = " + bookOrderId;
    }

    public String orderMovie() {
        movieOrderId += 1;
        movieCounter.increment();
        return "Ordered Movie with id = " + movieOrderId;
    }
}
