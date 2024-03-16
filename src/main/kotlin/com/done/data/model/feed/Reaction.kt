package com.done.data.model.feed

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.Date

data class Reaction(
    @BsonId
    var id: String = ObjectId().toString(),
    val postId: String,
    val authorId: String,
    val author: Author?,
    val reactType: ReactionType,
    val timestamp: Date
)

enum class ReactionType {
    LIKE,
    LOVE,
    HAHA,
    SAD
}