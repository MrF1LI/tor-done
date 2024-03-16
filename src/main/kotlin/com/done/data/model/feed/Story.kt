package com.done.data.model.feed

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class Story(
    @BsonId
    var id: String = ObjectId().toString(),
    var media: String,
    val timestamp: Date,
    val authorId: String,
    val author: Author?
)
