package com.example.rest3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/aggregator")
    public String aggregatorOthers() {
        return "Default Identity";
    }
}
