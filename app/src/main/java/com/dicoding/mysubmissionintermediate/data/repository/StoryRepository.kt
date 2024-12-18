package com.dicoding.mysubmissionintermediate.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.mysubmissionintermediate.data.db.StoryDatabase
import com.dicoding.mysubmissionintermediate.data.preference.UserModel
import com.dicoding.mysubmissionintermediate.data.preference.UserPref
import com.dicoding.mysubmissionintermediate.data.response.DetailResponse
import com.dicoding.mysubmissionintermediate.data.response.ErrorResponse
import com.dicoding.mysubmissionintermediate.data.response.ListStoryItem
import com.dicoding.mysubmissionintermediate.data.response.LoginResponse
import com.dicoding.mysubmissionintermediate.data.response.RegisterResponse
import com.dicoding.mysubmissionintermediate.data.response.StoryResponse
import com.dicoding.mysubmissionintermediate.data.result.ResultCode
import com.dicoding.mysubmissionintermediate.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val userPref: UserPref,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase

) {
    suspend fun saveSession(userModel: UserModel) = userPref.saveSession(userModel)

    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    suspend fun logout() {
        userPref.logout()
    }

    fun register(
        name: String,
        email: String,
        password: String,
    ): LiveData<ResultCode<RegisterResponse>> = liveData {
        emit(ResultCode.Loading)
        try {
            val response = apiService.register(name = name, email = email, password = password)
            emit(ResultCode.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultCode.Error(errorMessage.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<ResultCode<LoginResponse>> = liveData {
        emit(ResultCode.Loading)

        try {
            val response = apiService.login(email, password)
            emit(ResultCode.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultCode.Error(errorMessage.toString()))
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            remoteMediator = RemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                PagingResource(apiService, token)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun storyUpload(file: MultipartBody.Part,
                    description: RequestBody,
                    latRequestBody:RequestBody?,
                    lonRequestBody:RequestBody?): LiveData<ResultCode<ErrorResponse>> = liveData {
        emit(ResultCode.Loading)
        try{
            val token = userPref.getSession().first().token
            val response = apiService.uploadStory("Bearer $token",file,description, latRequestBody, lonRequestBody)
            emit(ResultCode.Success(response))
        }catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultCode.Error(errorMessage.toString()))
        }
    }

    suspend fun getStoryDetail(storyId: String): ResultCode<DetailResponse> {
        val userModel = userPref.getSession().first()
        val token = userModel.token

        return if (token.isNotEmpty()) {
            try {
                val response = apiService.getStoryDetail(storyId, "Bearer $token")
                if (response.error) {
                    ResultCode.Error(response.message)
                } else {
                    ResultCode.Success(response)
                }
            } catch (e: Exception) {
                ResultCode.Error(e.message ?: "Unknown error")
            }
        } else {
            ResultCode.Error("User is not logged in")
        }
    }
    suspend fun getStoriesWithLocation(location: Int = 1): ResultCode<StoryResponse> {
        val userModel = userPref.getSession().first()
        val token = userModel.token

        return if (token.isNotEmpty()) {
            try {
                val response = apiService.getStoriesWithLocation(location, "Bearer $token")
                ResultCode.Success(response)
            } catch (e: Exception) {
                ResultCode.Error(e.message ?: "Unknown error")
            }
        } else {
            ResultCode.Error("User is not logged in")
        }
    }
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            userPref: UserPref,
            apiService: ApiService,
            storyDatabase: StoryDatabase

        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPref, apiService, storyDatabase)
            }.also { instance = it }
    }



}