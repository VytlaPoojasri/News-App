package com.deloitte.usnewsapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.deloitte.usnewsapp.data.login.database.LoginDatabase
import com.deloitte.usnewsapp.data.login.datastore.DataStoreManager
import com.deloitte.usnewsapp.data.login.model.User
import com.deloitte.usnewsapp.data.login.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = LoginDatabase.getDatabase(application).userDao()
    private val userRepository = UserRepository(userDao)
    private val dataStoreManager = DataStoreManager(application)

    val username: LiveData<String> = dataStoreManager.username.asLiveData()

    fun login(email: String, password: String): LiveData<Boolean> = liveData {
        val user = userRepository.getUser(email, password)
        if (user != null) {
            dataStoreManager.setLoggedIn(true, user.username)
            emit(true)
        } else {
            emit(false)
        }
    }

    fun signup(username: String, email: String, password: String): LiveData<Boolean> = liveData {
        val existingUser = userRepository.getUserByEmail(email)
        if (existingUser == null) {
            val user = User(username = username, email = email, password = password)
            userRepository.insertUser(user)
            emit(true)
        } else {
            emit(false)
        }
    }
}
