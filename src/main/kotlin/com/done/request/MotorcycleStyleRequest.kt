package com.done.request

import com.done.data.model.feed.MotorcycleStyle

data class MotorcycleStyleRequest(
    val userId: String,
    val motorcycleStyle: MotorcycleStyle
)
