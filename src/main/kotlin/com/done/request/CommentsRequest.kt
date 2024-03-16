package com.done.request

data class CommentsRequest(
    val postId: String,
    val after: String,
    val count: Int
)
