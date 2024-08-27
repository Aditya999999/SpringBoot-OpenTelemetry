package com.example.opentelemetry.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
public class Controller {
    private final ItemService orderService;

    @Autowired
    public Controller(ItemService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/book")
    public String orderBook() {
        return orderService.orderBook();
    }

    @GetMapping("/order/movie")
    public String orderMovie() {
        return orderService.orderMovie();
    }

    @GetMapping("/users/active")
    public Number getActiveUsers() {
        return orderService.fetchActiveUsers();
    }
}
