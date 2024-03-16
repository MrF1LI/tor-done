package com.done.routes

import com.done.data.*
import com.done.data.model.feed.Comment
import com.done.data.model.feed.Post
import com.done.data.model.feed.Reaction
import com.done.request.CommentsRequest
import com.done.request.PostsRequest
import com.done.request.ReactionsRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postRoutes() {
    route("/get-posts") {
        get {
            val postRequest = call.receive<PostsRequest>()
            val posts = getPosts(postRequest.after, postRequest.count)

            call.respond(HttpStatusCode.OK, posts)
        }
    }

    route("/create-post") {
        post {
            val request = try {
                call.receive<Post>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (createPost(request)) {
                call.respond(HttpStatusCode.OK, "Post successfully created.")
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }

    route("/react-post") {
        post {
            val request = try {
                call.receive<Reaction>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (reactPost(request)) {
                call.respond(HttpStatusCode.OK, "Post reacted successfully.")
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }

    route("/add-post-comment") {
        post {
            val request = try {
                call.receive<Comment>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (addPostComment(request)) {
                call.respond(HttpStatusCode.OK, "Comment added successfully.")
            } else {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }

    route("/get-comments") {
        get {
            val request = call.receive<CommentsRequest>()
            val comments = getCommentsOfPost(request.postId, request.after, request.count)

            call.respond(HttpStatusCode.OK, comments)
        }
    }

    route("/get-reacts") {
        get {
            val request = call.receive<ReactionsRequest>()
            val comments = getReactsOfPost(request.postId, request.after, request.count)

            call.respond(HttpStatusCode.OK, comments)
        }
    }
}