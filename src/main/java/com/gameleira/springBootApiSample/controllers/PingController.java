package com.gameleira.springBootApiSample.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @RequestMapping("/ping")
    public String index() {
        return "{ \"success\": true }";
    }
}