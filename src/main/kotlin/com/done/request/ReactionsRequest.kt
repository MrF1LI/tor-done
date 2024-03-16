package com.done.request

data class ReactionsRequest(
    val postId: String,
    val after: String,
    val count: Int
)
