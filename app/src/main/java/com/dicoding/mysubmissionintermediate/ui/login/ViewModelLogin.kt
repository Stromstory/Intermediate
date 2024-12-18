package com.dicoding.mysubmissionintermediate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mysubmissionintermediate.data.preference.UserModel
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository
import kotlinx.coroutines.launch

class ViewModelLogin (private val repository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(userModel: UserModel) = viewModelScope.launch {
        repository.saveSession(userModel)
    }
}