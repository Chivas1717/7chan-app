package com.example.mobile.viewmodel

data class UserProfileResponse(
    val id: Int,
    val username: String,
    val email: String, // замінюємо displayName на email
    val posts: List<PostResponse>
)

data class PostResponse(
    val id: Int,
    val title: String,
    val content: String,
    val hashtag_list: List<String>,
    val comments: List<CommentResponse>, // нове поле для коментарів
    val createdAt: String,
    val author: AuthorResponse,
)

data class CommentResponse(
    val id: Int,
    val author: AuthorResponse,
    val content: String,
    val createdAt: String
)

data class AuthorResponse(
    val id: Int,
    val username: String,
    val email: String,
)
