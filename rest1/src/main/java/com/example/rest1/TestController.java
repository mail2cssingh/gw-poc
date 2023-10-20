package com.example.rest1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/aggregator")
    public String aggregatorNowtv() {
        return "Identity for NOWTV";
    }

    @GetMapping("/profile/id/{id}")
    public String getProfileById(@PathVariable String id) {
        return "Profile details for id : " + id;
    }
}
