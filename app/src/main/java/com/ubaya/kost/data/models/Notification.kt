package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

/**
 * Represents a Notification.
 * @property id The id of the notification.
 * @property message The message/body of notification.
 * @property isRead Does the notification is read by the user.
 * @property message The date of notification created.
 */
data class Notification(
    val id: Int,
    val message: String,
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("created_at") val date: String
)
