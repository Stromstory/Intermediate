package com.dicoding.mysubmissionintermediate.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mysubmissionintermediate.data.preference.UserModel
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository
import com.dicoding.mysubmissionintermediate.data.response.ListStoryItem
import kotlinx.coroutines.launch

class ViewModelMain(private val repository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStoryList(token: String): LiveData<PagingData<ListStoryItem>> {
        return repository.getStories(token).cachedIn(viewModelScope)
    }
}