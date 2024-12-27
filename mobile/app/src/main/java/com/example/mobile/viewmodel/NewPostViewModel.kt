package com.example.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.remote.ApiService
import com.example.mobile.data.remote.CreatePostRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPostViewModel(private val apiService: ApiService) : ViewModel() {
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _hashtag = MutableStateFlow("")
    val hashtag: StateFlow<String> get() = _hashtag

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> get() = _content

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onHashtagChange(newHashtag: String) {
        _hashtag.value = newHashtag
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
    }

    fun createPost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.createPost(
                    CreatePostRequest(
                        title = _title.value,
                        hashtags = listOf(_hashtag.value),
                        content = _content.value
                    )
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Failed to create post: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class NewPostViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPostViewModel::class.java)) {
            return NewPostViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
