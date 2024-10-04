package com.deloitte.usnewsapp.data.login.repository


import com.deloitte.usnewsapp.data.login.database.UserDao
import com.deloitte.usnewsapp.data.login.model.User
import com.deloitte.usnewsapp.util.EncryptionUtils

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        val encryptedPassword = EncryptionUtils.encryptPassword(user.password)
        val encryptedUser = user.copy(password = encryptedPassword)
        userDao.insertUser(encryptedUser)
    }

    suspend fun getUser(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email)
        return if (user != null) {
            val decryptedPassword = try {
                EncryptionUtils.decryptPassword(user.password)
            } catch (e: IllegalArgumentException) {
                null
            }
            if (decryptedPassword == password) user else null
        } else {
            null
        }
    }
    suspend fun getUserByEmail(email: String): User? {
        val user = userDao.getUserByEmail(email)
        return user
    }
}
