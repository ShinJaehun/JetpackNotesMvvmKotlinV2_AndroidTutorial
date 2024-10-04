package com.shinjaehun.jetpacknotesmvvmkotlinv2.login.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv2.login.UserViewModel
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.IUserRepository
import kotlinx.coroutines.Dispatchers

class UserViewModelFactory(
    private val userRepo: IUserRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRepo, Dispatchers.Main) as T
    }
}