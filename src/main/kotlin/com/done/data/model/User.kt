package com.done.data.model

import com.done.data.model.feed.MotorcycleStyle
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    var id: String = ObjectId().toString(),
    val name: String,
    val surname: String,
    val age: Int,
    val email: String,
    val imageUrl: String,
    val motorcycleStyle: MotorcycleStyle
)