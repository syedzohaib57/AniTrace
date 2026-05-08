package com.example.anitrace.presentation.anime_screen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anitrace.data.ApiService
import com.example.anitrace.domain.AnimeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AnimeState(
    val loading: Boolean = false,
    val results: List<AnimeResult> = emptyList(),
    val error: String? = null
)
class AnimeViewModel : ViewModel() {

    private val _state = MutableStateFlow(AnimeState())
    val state: StateFlow<AnimeState> = _state


    fun search(imageUrl: String) {
        viewModelScope.launch {
            _state.value =  _state.value.copy(
                loading = true,
                error = null
            )
            try {
                val response = ApiService.searchAnime(imageUrl)
                if (response.error?.isNotEmpty() == true){
                    _state.value = _state.value.copy(
                        error = response.error,
                        loading = false
                    )
                    return@launch
                }
                _state.value = _state.value.copy(
                    results = response.result,
                    loading = false
                )
                Log.d("AnimeVM", "search: ${response.result} ")

            } catch (e: Exception) {
                Log.d("AnimeVM", "search failed: ${e.message}")
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Unknown error"
                )
                e.printStackTrace()
            }
        }
    }
    fun uploadImage(imageUrl: String, context: Context? = null, uri: Uri? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val response = if (uri != null && context != null) {

                    ApiService.uploadAndSearch(context, uri)
                } else {

                    ApiService.searchAnime(imageUrl)
                }

                _state.value = _state.value.copy(
                    results = response.result,
                    loading = false,
                    error = if (response.error?.isNotEmpty() == true) response.error else null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}