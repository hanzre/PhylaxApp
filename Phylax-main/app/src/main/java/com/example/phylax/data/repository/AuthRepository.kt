package com.example.phylax.data.repository

import android.app.Activity
import com.example.phylax.data.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getCurrentUser() = auth.currentUser

    suspend fun getUserProfile(uid: String): User? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            // Older profile documents may not contain a uid field. The
            // authenticated UID is authoritative, so never return a blank one.
            doc.toObject(User::class.java)?.copy(uid = uid)
        } catch (e: Exception) {
            null
        }
    }

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    ) {
        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
        
        forceResendingToken?.let {
            builder.setForceResendingToken(it)
        }
        
        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    suspend fun signInWithCredential(credential: PhoneAuthCredential, userProfile: User? = null): Result<User> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user?.uid ?: ""
            
            val user = if (userProfile != null) {
                // Registration: Save new profile
                val finalUser = userProfile.copy(uid = uid)
                withTimeout(10000) { // 10 second timeout for Firestore
                    firestore.collection("users").document(uid).set(finalUser).await()
                }
                finalUser
            } else {
                // Login: Fetch existing profile
                withTimeout(10000) {
                    val doc = firestore.collection("users").document(uid).get().await()
                    doc.toObject(User::class.java)?.copy(uid = uid) ?: run {
                        // Create default profile if missing
                        val defaultUser = User(uid = uid, name = "", phone = "", email = "")
                        firestore.collection("users").document(uid).set(defaultUser).await()
                        defaultUser
                    }
                }
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(user: User, password: String): Result<User> {
        return try {
            // 1. Try to create user
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            
            val uid = result.user?.uid ?: ""
            
            // 2. Save/Update Profile in Firestore
            val finalUser = user.copy(uid = uid)
            withTimeout(15000) { // Increased timeout to 15s
                firestore.collection("users").document(uid).set(finalUser).await()
            }
            Result.success(finalUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: ""
            val user = withTimeout(10000) {
                val doc = firestore.collection("users").document(uid).get().await()
                doc.toObject(User::class.java)?.copy(uid = uid) ?: run {
                    val defaultUser = User(uid = uid, name = "", phone = "", email = email)
                    firestore.collection("users").document(uid).set(defaultUser).await()
                    defaultUser
                }
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
}
