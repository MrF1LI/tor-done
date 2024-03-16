package com.done.data

import com.done.data.model.User
import com.done.data.model.feed.*
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.*

private val client = KMongo.createClient()
private val database = client.getDatabase("DoneDatabase")

// Users
private val users = database.getCollection<User>()

fun getUserById(id: String): User? {
    return users.findOneById(id)
}

fun createUser(user: User): Boolean {
    val userExists = users.findOneById(user.id) != null

    return if (userExists) {
        users.updateOneById(user.id, user).wasAcknowledged()
    } else {
        users.insertOne(user).wasAcknowledged()
    }
}

fun setUserMotorcycleStyle(userId: String, motorcycleStyle: MotorcycleStyle): Boolean {
    val userExists = users.findOneById(userId) != null

    return if (userExists) {
        val update = Updates.set("motorcycleStyle", motorcycleStyle)
        users.updateOneById(userId, update).wasAcknowledged()
    } else {
        return false
    }
}

// Posts
private val posts = database.getCollection<Post>()
private val reacts = database.getCollection<Reaction>()
private val comments = database.getCollection<Comment>()

fun createPost(post: Post): Boolean {
    post.id = ObjectId().toString()

    // Ensure that the media field is not null
    val media = post.media ?: emptyMap()
    post.media = media

    return posts.insertOne(post).wasAcknowledged()
}

suspend fun getPosts(after: String, count: Int): List<Post> {
    val afterTimestamp = after.let { postId ->
        posts.findOneById(postId)?.timestamp
    }

    val filter = afterTimestamp?.let { Filters.gt("timestamp", it) } ?: Filters.exists("timestamp")

    val postsCursor = posts
        .find(filter)
        .descendingSort(Post::timestamp)
        .limit(count)

    val postsWithUserInfo = coroutineScope {
        postsCursor.toList().map { post ->
            async {
                val user = users.findOneById(post.authorId) ?: return@async post

                post.copy(author = Author(user.name, user.surname, user.imageUrl))
            }
        }.awaitAll()
    }

    return postsWithUserInfo
}

fun reactPost(reaction: Reaction): Boolean {
    val filter = Filters.and(
        eq("authorId", reaction.authorId),
        eq("postId", reaction.postId)
    )

    val reactExists = reacts.findOne(filter) != null

    return if (reactExists) {
        val update = Document("\$set", Document().apply {
            put("authorId", reaction.authorId)
            put("postId", reaction.postId)
            put("reactType", reaction.reactType.toString())
            put("timestamp", reaction.timestamp)
        })
        reacts.updateOne(filter, update).wasAcknowledged()
    } else {
        reaction.id = ObjectId().toString()
        val insertResult = reacts.insertOne(reaction).wasAcknowledged()

        if (insertResult) {
            val update = Updates.inc("reactsCount", 1)
            posts.updateOneById(reaction.postId, update).wasAcknowledged()
        }

        insertResult
    }
}

fun addPostComment(comment: Comment): Boolean {
    comment.id = ObjectId().toString()
    val commentResult = comments.insertOne(comment).wasAcknowledged()

    if (commentResult) {
        val update = Updates.inc("commentsCount", 1)
        return posts.updateOneById(comment.postId, update).wasAcknowledged()
    }

    return false
}

suspend fun getCommentsOfPost(postId: String, after: String, count: Int): List<Comment> {
    val afterTimestamp = after.let { comments.findOneById(it)?.timestamp }

    val filter = afterTimestamp?.let {
        Filters.gt("timestamp", it)
        eq("postId", postId)
    } ?: Filters.exists("timestamp")

    val commentsCursor = comments
        .find(filter)
        .descendingSort(Comment::timestamp)
        .limit(count)

    return coroutineScope {
        commentsCursor.toList().map { comment ->
            async {
                val user = users.findOneById(comment.authorId) ?: return@async comment
                comment.copy(author = Author(user.name, user.surname, user.imageUrl))
            }
        }.awaitAll()
    }
}

suspend fun getReactsOfPost(postId: String, after: String, count: Int): List<Reaction> {
    val afterTimestamp = after.let { reacts.findOneById(it)?.timestamp }

    val filter = afterTimestamp?.let {
        Filters.gt("timestamp", it)
        eq("postId", postId)
    } ?: Filters.exists("timestamp")

    val reactsCursor = reacts
        .find(filter)
        .descendingSort(Reaction::timestamp)
        .limit(count)

    return coroutineScope {
        reactsCursor.toList().map { react ->
            async {
                val user = users.findOneById(react.authorId) ?: return@async react
                react.copy(author = Author(user.name, user.surname, user.imageUrl))
            }
        }.awaitAll()
    }
}

// Stories
private val stories = database.getCollection<Story>()

fun createStory(story: Story): Boolean {
    story.id = ObjectId().toString()
    return stories.insertOne(story).wasAcknowledged()
}
