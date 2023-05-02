package com.example.news

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.news"])
@EntityScan("com.example.news.domain")
class NewsApplication

fun main(args: Array<String>) {
    runApplication<NewsApplication>(*args)
}
