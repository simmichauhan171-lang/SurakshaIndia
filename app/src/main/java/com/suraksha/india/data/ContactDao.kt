package com.suraksha.india.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM trusted_contacts ORDER BY isPrimary DESC, name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM trusted_contacts WHERE isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryContact(): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    @Query("UPDATE trusted_contacts SET isPrimary = 0")
    suspend fun clearPrimaryFlags()

    @Query("UPDATE trusted_contacts SET isPrimary = 1 WHERE id = :contactId")
    suspend fun setSinglePrimary(contactId: Long)

    @Transaction
    suspend fun makePrimary(contactId: Long) {
        clearPrimaryFlags()
        setSinglePrimary(contactId)
    }
}
