package com.dicoding.mysubmissionintermediate.ui.register

import androidx.lifecycle.ViewModel
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository

class ViewModelRegister(
    private val repository: StoryRepository
) : ViewModel() {
    fun register(
        name: String,
        email: String,
        password: String
    ) = repository.register(name, email, password)

}