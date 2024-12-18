package com.dicoding.mysubmissionintermediate.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mysubmissionintermediate.data.di.Injection
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository
import com.dicoding.mysubmissionintermediate.ui.detail.ViewModelDetail
import com.dicoding.mysubmissionintermediate.ui.login.ViewModelLogin
import com.dicoding.mysubmissionintermediate.ui.main.ViewModelMain
import com.dicoding.mysubmissionintermediate.ui.maps.ViewModelMaps
import com.dicoding.mysubmissionintermediate.ui.register.ViewModelRegister
import com.dicoding.mysubmissionintermediate.ui.upl.ViewModelUpload

class FactoryViewModel(private val repository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ViewModelMaps::class.java) -> {
                ViewModelMaps(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelDetail::class.java) -> {
                ViewModelDetail(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelUpload::class.java) -> {
                ViewModelUpload(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelMain::class.java) -> {
                ViewModelMain(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelLogin::class.java) -> {
                ViewModelLogin(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelRegister::class.java) -> {
                ViewModelRegister(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FactoryViewModel? = null
        @JvmStatic
        fun getInstance(context: Context): FactoryViewModel {
            if (INSTANCE == null) {
                synchronized(FactoryViewModel::class.java) {
                    INSTANCE = FactoryViewModel(Injection.provideRepository(context))
                }
            }
            return INSTANCE as FactoryViewModel
        }
    }
}