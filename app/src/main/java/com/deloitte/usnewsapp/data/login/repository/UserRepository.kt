package com.deloitte.usnewsapp.data.login.repository

import com.deloitte.usnewsapp.data.login.database.UserDao
import com.deloitte.usnewsapp.data.login.model.User


class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUser(email: String, password: String): User? {
        return userDao.getUser(email, password)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}
