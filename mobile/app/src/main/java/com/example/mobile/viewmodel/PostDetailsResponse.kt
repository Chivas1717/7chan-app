package com.example.mobile.viewmodel

import java.util.Date

data class PostDetailsResponse(
    val id: Int,
    val author: AuthorResponse,
    val title: String,
    val content: String,
    val created_at: String,
    val comments: List<CommentResponse>,
    val hashtag_list: List<String>,
)

