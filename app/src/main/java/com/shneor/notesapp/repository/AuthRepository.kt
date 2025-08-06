package com.shneor.notesapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shneor.notesapp.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun getCurrentUser() = auth.currentUser

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(email: String, password: String, name: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User not created")

        val userMap = hashMapOf(
            "name" to name,
            "email" to email
        )

        firestore.collection("users").document(uid).set(userMap).await()
    }

    suspend fun getUserData(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = firestore.collection("users").document(uid).get().await()
        return if (doc.exists()) {
            User(
                uid = uid,
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: ""
            )
        } else null
    }

    fun logout() {
        auth.signOut()
    }
}