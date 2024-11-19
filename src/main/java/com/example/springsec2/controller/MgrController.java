package com.example.springsec2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class MgrController {

    @GetMapping("/hello")
    public String hello() {
        return "this is management side";
    }
}
