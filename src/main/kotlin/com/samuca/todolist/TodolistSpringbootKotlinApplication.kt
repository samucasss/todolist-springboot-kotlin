package com.samuca.todolist

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodolistSpringbootKotlinApplication

fun main(args: Array<String>) {
    runApplication<TodolistSpringbootKotlinApplication>(*args)
}
