package com.dicoding.mysubmissionintermediate.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository
import com.dicoding.mysubmissionintermediate.data.response.DetailResponse
import com.dicoding.mysubmissionintermediate.data.result.ResultCode
import kotlinx.coroutines.flow.flow

class ViewModelDetail(private val repository: StoryRepository) : ViewModel() {
    private lateinit var _detailStory: LiveData<ResultCode<DetailResponse>>
    val detailStory: LiveData<ResultCode<DetailResponse>> get() = _detailStory

    fun getStoryById(storyId: String) {
        _detailStory = flow {
            emit(ResultCode.Loading)
            try {
                val response = repository.getStoryDetail(storyId)
                emit(response)
            } catch (e: Exception) {
                emit(ResultCode.Error(e.message ?: "Unknown error"))
            }
        }.asLiveData()
    }
}