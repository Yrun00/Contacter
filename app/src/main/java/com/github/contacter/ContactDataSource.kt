package com.github.contacter

import android.content.ContentResolver
import android.provider.ContactsContract

class ContactDataSource(private val contentResolver: ContentResolver) {

    suspend fun getContacts(): List<Contact> {

        val contactsList = mutableListOf<Contact>()
        val uri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
            val hasPhoneIndex = it.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)

            while (it.moveToNext()) {
                val id: String = it.getString(idIndex)
                val hasPhone = it.getInt(hasPhoneIndex)
                val phoneNumber = if (hasPhone > 0) {
                    getPhoneNumber(id)
                } else {
                    "No phone"
                }
                val name = it.getString(nameIndex) ?: "Unknown"
                contactsList.add(Contact(id, name, phoneNumber))
            }

        }
        return contactsList

    }

    private fun getPhoneNumber(contactId: String): String {
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val phoneProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val phoneSelection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val phoneArgs = arrayOf(contactId)

        val phoneCursor = contentResolver.query(
            phoneUri,
            phoneProjection,
            phoneSelection,
            phoneArgs,
            null
        )

        var phoneNumber = "No phone"
        phoneCursor?.use {
            if (it.moveToFirst()) {
                val numberIndex = it.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                phoneNumber = it.getString(numberIndex) ?: "No phone"
            }
        }

        return phoneNumber
    }
}