package com.example.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.mobile.data.remote.ApiService

class HomeViewModel(private val apiService: ApiService) : ViewModel() {

    // Список всіх хештегів з бекенду
    private val _hashtags = MutableStateFlow<List<HashtagResponse>>(emptyList())
    val hashtags: StateFlow<List<HashtagResponse>> get() = _hashtags

    // Список відфільтрованих або всіх постів
    private val _posts = MutableStateFlow<List<PostResponse>>(emptyList())
    val posts: StateFlow<List<PostResponse>> get() = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    // Запам'ятовуємо, який хештег зараз обрано (щоб виділяти його на UI)
    private val _selectedHashtag = MutableStateFlow<String?>(null)
    val selectedHashtag: StateFlow<String?> get() = _selectedHashtag

    // Завантажити всі хештеги
    fun fetchHashtags() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val hashtagsFromApi = apiService.getHashtags()
                _hashtags.value = hashtagsFromApi
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Завантажити пости із опційним фільтром
    fun fetchPosts(hashtag: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            // Запам'ятовуємо обраний хештег
            _selectedHashtag.value = hashtag
            try {
                _posts.value = apiService.getPosts(hashtag)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}



class HomeViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}