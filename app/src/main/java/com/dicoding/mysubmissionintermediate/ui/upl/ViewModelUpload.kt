package com.dicoding.mysubmissionintermediate.ui.upl

import androidx.lifecycle.ViewModel
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ViewModelUpload(
    private val repository: StoryRepository,
) : ViewModel() {
    fun storyUpload(file : MultipartBody.Part, description: RequestBody, latRequestBody:RequestBody?,
                    lonRequestBody:RequestBody?) = repository.storyUpload(file,description, latRequestBody, lonRequestBody)
}