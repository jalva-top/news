package com.example.news.exception

class NotFoundException(actualMessage: String, cause: Throwable? = null) :
    RuntimeException(actualMessage, cause)

class AlreadyExistException(actualMessage: String) : RuntimeException(actualMessage)