package com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository

import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.User

interface IUserRepository {
    suspend fun getCurrentUser(): Result<Exception, User?>
    suspend fun signOutCurrentUser(): Result<Exception, Unit>
    suspend fun signInGoogleUser(idToken: String): Result<Exception, Unit>
}