package com.done.plugins

import com.done.routes.postRoutes
import com.done.routes.storyRoutes
import com.done.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRoutes()
        postRoutes()
        storyRoutes()
    }
}
