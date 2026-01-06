package com.example.project_codego.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_codego.dto.UserPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.google.firebase.auth.FirebaseAuth

class PostViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val postsCollection = firestore.collection("posts")

    private val _posts = MutableStateFlow<List<UserPost>>(emptyList())
    val posts = _posts.asStateFlow()

    init {
        // READ: Listen for real-time updates
        fetchPostsRealtime() 
    }

    // 1. CREATE
    fun createPost(content: String, category: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val newPostRef = postsCollection.document() // Generate random ID
                val post = UserPost(
                    id = newPostRef.id,
                    userId = user.uid,
                    authorName = user.displayName ?: user.email?.substringBefore("@") ?: "Anonymous",
                    content = content,
                    category = category
                )
                newPostRef.set(post)
            }
        }
    }

    // 2. READ (Real-time)
    private fun fetchPostsRealtime() {
        postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                
                if (snapshot != null) {
                    val postList = snapshot.toObjects(UserPost::class.java)
                    _posts.value = postList
                }
            }
    }

    // 3. UPDATE
    fun updatePost(postId: String, newContent: String) {
        viewModelScope.launch {
            postsCollection.document(postId)
                .update("content", newContent)
        }
    }

    // 4. DELETE
    fun deletePost(postId: String) {
        viewModelScope.launch {
            postsCollection.document(postId).delete()
        }
    }
}
