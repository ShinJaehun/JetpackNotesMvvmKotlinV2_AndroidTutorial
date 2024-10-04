package com.shinjaehun.jetpacknotesmvvmkotlinv2.login.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.implementations.FirebaseUserRepoImpl
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.IUserRepository

class LoginInjector(application: Application): AndroidViewModel(application) {

    init {
        FirebaseApp.initializeApp(application)
    }

    private fun getUserRepository(): IUserRepository {
        return FirebaseUserRepoImpl()
    }

    fun provideUserViewModelFactory(): UserViewModelFactory =
        UserViewModelFactory(
            getUserRepository()
        )
}