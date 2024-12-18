package com.dicoding.mysubmissionintermediate.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository
import com.dicoding.mysubmissionintermediate.data.response.Story
import com.dicoding.mysubmissionintermediate.data.result.ResultCode
import kotlinx.coroutines.launch

class ViewModelMaps(private val repository: StoryRepository) : ViewModel() {
    private val _storyLocation = MutableLiveData<ResultCode<List<Story>>>()
    val storyLocation: MutableLiveData<ResultCode<List<Story>>> = _storyLocation

    init {
        getStoryWithLocation()
    }

    private fun getStoryWithLocation() {
        viewModelScope.launch {
            _storyLocation.value = ResultCode.Loading // Set loading state
            try {
                val response = repository.getStoriesWithLocation()
                if (response is ResultCode.Success) {
                    val storyList: List<Story> = response.data.listStory.map { listItem ->
                        Story(
                            id = listItem.id,
                            name = listItem.name,
                            description = listItem.description,
                            lat = listItem.lat,
                            lon = listItem.lon,
                            photoUrl = listItem.photoUrl,
                            createdAt = listItem.createdAt
                        )
                    }

                    _storyLocation.value = if (storyList.isNotEmpty()) {
                        ResultCode.Success(storyList)
                    } else {
                        ResultCode.Error("Data not found")
                    }
                } else if (response is ResultCode.Error) {
                    _storyLocation.value = ResultCode.Error(response.error)
                }
            } catch (e: Exception) {
                _storyLocation.value = ResultCode.Error(e.message.toString())
            }
        }
    }
}
