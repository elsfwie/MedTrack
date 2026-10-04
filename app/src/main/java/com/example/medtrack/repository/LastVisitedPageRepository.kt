package com.example.medtrack.repository

import com.example.medtrack.model.LastVisitedPage

interface LastVisitedPageRepository {

    suspend fun getLastPage(
        userId: String
    ): LastVisitedPage?

    suspend fun saveLastPage(
        userId: String,
        page: LastVisitedPage
    )

    suspend fun clearLastPage(
        userId: String
    )
}