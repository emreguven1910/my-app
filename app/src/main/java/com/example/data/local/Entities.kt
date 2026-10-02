package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    val plainText: String,
    val date: String,
    val link: String,
    val category: String,
    val destinationCity: String,
    val readTimeMinutes: Int,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "trip_notes")
data class TripNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val destination: String,
    val date: String,
    val notes: String,
    val rating: Int = 5,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val email: String,
    val homeCity: String = "Ankara",
    val memberLevel: String = "Gezgin Kaşif",
    val bio: String = "Yeni şehirler, tren rotaları ve tarihi rotaları keşfetmeyi seven gezgin.",
    val joinedDate: String,
    val isLoggedIn: Boolean = true
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String, // Ulaşım, Yeme-İçme, Konaklama, Müze & Giriş, Alışveriş, Diğer
    val destination: String,
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)


