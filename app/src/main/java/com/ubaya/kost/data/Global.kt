package com.ubaya.kost.data

import com.ubaya.kost.data.models.Kost
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.data.models.User

object Global {
    lateinit var authUser: User
    lateinit var authTenant: Tenant
    lateinit var authToken: String
    lateinit var authKost: Kost
}