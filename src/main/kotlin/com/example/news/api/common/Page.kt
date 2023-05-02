package com.example.news.api.common

import javax.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.Range

data class PaginationRequest(
    @field:PositiveOrZero(message = "{pagingRequest.page.positiveOrZero}")
    val page: Int = 0,
    @field:Range(min = 1, max = 30, message = "{pagingRequest.pageSize.range}")
    val pageSize: Int = 10,
)

data class PaginationDetails(val page: Int, val pageSize: Int, val totalElements: Long)

data class PaginationResponse<T>(val content: List<T>, val pagination: PaginationDetails)
