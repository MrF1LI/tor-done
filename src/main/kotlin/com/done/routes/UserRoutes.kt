package com.done.routes

import com.done.data.createUser
import com.done.data.getUserById
import com.done.data.model.User
import com.done.data.model.feed.MotorcycleStyle
import com.done.data.setUserMotorcycleStyle
import com.done.request.MotorcycleStyleRequest
import com.done.request.UserRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    route("/create-user") {
        post {
            val request = try {
                call.receive<User>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (createUser(request)) {
                call.respond(HttpStatusCode.OK, "User successfully created.")
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }

    route("/set-user-bike-style") {
        post {
            val request = try {
                call.receive<MotorcycleStyleRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (setUserMotorcycleStyle(request.userId, request.motorcycleStyle)) {
                call.respond(HttpStatusCode.OK, "User style successfully added.")
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }

    route("/get-user") {
        get {
            val userId = call.receive<UserRequest>().id
            val user = getUserById(userId)
            user?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(HttpStatusCode.OK, "There is no user with this id.")
        }
    }
}
