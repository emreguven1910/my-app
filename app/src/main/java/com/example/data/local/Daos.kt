package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    fun getPostById(id: Int): Flow<PostEntity?>

    @Query("SELECT * FROM posts WHERE isBookmarked = 1 ORDER BY id DESC")
    fun getBookmarkedPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("UPDATE posts SET title = :title, content = :content, plainText = :plainText, date = :date, link = :link, destinationCity = :city WHERE id = :id")
    suspend fun updatePostContent(id: Int, title: String, content: String, plainText: String, date: String, link: String, city: String)

    @Query("UPDATE posts SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setBookmark(id: Int, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int
}

@Dao
interface TripNoteDao {
    @Query("SELECT * FROM trip_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<TripNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: TripNoteEntity): Long

    @Query("DELETE FROM trip_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items ORDER BY id ASC")
    fun getAllItems(): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("SELECT COUNT(*) FROM checklist_items")
    suspend fun getCount(): Int
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET isLoggedIn = 0 WHERE id = 1")
    suspend fun logout()

    @Query("DELETE FROM user_profile WHERE id = 1")
    suspend fun deleteProfile()
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenseAmount(): Flow<Double?>
}


