package com.suraksha.india.data

import kotlinx.coroutines.flow.Flow

class ContactRepository(private val dao: ContactDao) {
    val allContacts: Flow<List<ContactEntity>> = dao.getAllContacts()

    suspend fun insertContact(contact: ContactEntity): Long = dao.insertContact(contact)
    suspend fun updateContact(contact: ContactEntity) = dao.updateContact(contact)
    suspend fun deleteContact(contact: ContactEntity) = dao.deleteContact(contact)
    suspend fun makePrimary(contactId: Long) = dao.makePrimary(contactId)
    suspend fun getPrimaryContact(): ContactEntity? = dao.getPrimaryContact()
}
