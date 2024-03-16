package com.done.routes

import com.done.data.createStory
import com.done.data.model.feed.Story
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.storyRoutes() {
    route("/create-story") {
        post {
            val request = try {
                call.receive<Story>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (createStory(request)) {
                call.respond(HttpStatusCode.OK, "Story successfully created.")
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }
}
