package com.samuca.todolist.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloRestController {

    @GetMapping("/")
    fun hello(): String {
        return "Hello world com spring boot com kotlin"
    }
}