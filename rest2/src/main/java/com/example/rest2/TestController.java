package com.example.rest2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/aggregator")
    public String aggregatorNowtv2() {
        return "Identity for NOWTV 2";
    }
}
