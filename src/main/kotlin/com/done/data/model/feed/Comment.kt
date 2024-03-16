package com.done.data.model.feed

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.Date

data class Comment(
    @BsonId
    var id: String = ObjectId().toString(),
    val postId: String,
    val authorId: String,
    val author: Author?,
    val content: String,
    val timestamp: Date
)
