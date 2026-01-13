package com.example.project_codego.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_codego.dto.UserPost
import com.example.project_codego.dto.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    private val _currentPage = MutableStateFlow(1)
    val currentPage = _currentPage.asStateFlow()
    
    private val _totalPages = MutableStateFlow(1)
    val totalPages = _totalPages.asStateFlow()
    
    private val postsPerPage = 5
    private var allPosts = listOf<UserPost>()

    init {
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
                if (e != null) {
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    allPosts = snapshot.toObjects(UserPost::class.java)
                    updatePagination()
                    _isLoading.value = false
                }
            }
    }
    
    private fun updatePagination() {
        val total = allPosts.size
        _totalPages.value = if (total > 0) {
            (total + postsPerPage - 1) / postsPerPage
        } else {
            1
        }
        updateCurrentPagePosts()
    }
    
    private fun updateCurrentPagePosts() {
        val page = _currentPage.value
        val startIndex = (page - 1) * postsPerPage
        val endIndex = minOf(startIndex + postsPerPage, allPosts.size)
        
        _posts.value = if (startIndex < allPosts.size) {
            allPosts.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }
    
    fun goToNextPage() {
        if (_currentPage.value < _totalPages.value) {
            _currentPage.value += 1
            updateCurrentPagePosts()
        }
    }
    
    fun goToPreviousPage() {
        if (_currentPage.value > 1) {
            _currentPage.value -= 1
            updateCurrentPagePosts()
        }
    }
    
    fun goToPage(page: Int) {
        if (page in 1.._totalPages.value) {
            _currentPage.value = page
            updateCurrentPagePosts()
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
    
    // 5. LIKE/UNLIKE
    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch {
            val postRef = postsCollection.document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val likes = snapshot.get("likes") as? List<*> ?: emptyList<String>()
                val currentLikes = likes.filterIsInstance<String>().toMutableList()
                
                if (currentLikes.contains(userId)) {
                    currentLikes.remove(userId)
                } else {
                    currentLikes.add(userId)
                }
                
                transaction.update(postRef, "likes", currentLikes)
            }
        }
    }
    
    // 6. ADD COMMENT
    fun addComment(postId: String, commentText: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val comment = Comment(
                    id = firestore.collection("temp").document().id,
                    userId = user.uid,
                    userName = user.displayName ?: user.email?.substringBefore("@") ?: "Anonymous",
                    text = commentText,
                    timestamp = System.currentTimeMillis()
                )
                
                postsCollection.document(postId)
                    .update("comments", FieldValue.arrayUnion(comment))
            }
        }
    }
    
    // 7. DELETE COMMENT
    fun deleteComment(postId: String, comment: Comment) {
        viewModelScope.launch {
            postsCollection.document(postId)
                .update("comments", FieldValue.arrayRemove(comment))
        }
    }
    
    // 8. EDIT COMMENT
    fun editComment(postId: String, oldComment: Comment, newText: String) {
        viewModelScope.launch {
            val postRef = postsCollection.document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val comments = snapshot.get("comments") as? List<*> ?: emptyList<Comment>()
                val currentComments = comments.mapNotNull { commentData ->
                    if (commentData is Map<*, *>) {
                        Comment(
                            id = commentData["id"] as? String ?: "",
                            userId = commentData["userId"] as? String ?: "",
                            userName = commentData["userName"] as? String ?: "",
                            text = commentData["text"] as? String ?: "",
                            timestamp = (commentData["timestamp"] as? Long) ?: 0L
                        )
                    } else null
                }.toMutableList()
                
                val index = currentComments.indexOfFirst { it.id == oldComment.id }
                if (index != -1) {
                    currentComments[index] = oldComment.copy(text = newText)
                    transaction.update(postRef, "comments", currentComments)
                }
            }
        }
    }
}
