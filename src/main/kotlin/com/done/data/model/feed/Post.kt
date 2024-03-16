package com.done.data.model.feed

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class Post(
    @BsonId
    var id: String = ObjectId().toString(),
    val content: String,
    var media: Map<String, String>?,
    val timestamp: Date,
    val authorId: String,
    val author: Author?,
    val reactsCount: Int = 0,
    val commentsCount: Int = 0
)
