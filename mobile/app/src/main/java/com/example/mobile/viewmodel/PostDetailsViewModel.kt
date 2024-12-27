package com.example.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.remote.ApiService
import com.example.mobile.data.remote.PostCommentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostDetailsViewModel(private val apiService: ApiService) : ViewModel() {

    private val _postDetails = MutableStateFlow<PostDetailsResponse?>(null)
    val postDetails: StateFlow<PostDetailsResponse?> get() = _postDetails

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _newComment = MutableStateFlow("")
    val newComment: StateFlow<String> get() = _newComment

    fun fetchPostDetails(postId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val post = apiService.getPostDetails(postId)
                _postDetails.value = post
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCommentChange(comment: String) {
        _newComment.value = comment
    }

    fun postComment(postId: Int) {
        viewModelScope.launch {
            try {
                apiService.postComment(PostCommentRequest(postId, _newComment.value))
                fetchPostDetails(postId) // Оновити коментарі після успішного постінгу
                _newComment.value = ""
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}

class PostDetailsViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailsViewModel::class.java)) {
            return PostDetailsViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
