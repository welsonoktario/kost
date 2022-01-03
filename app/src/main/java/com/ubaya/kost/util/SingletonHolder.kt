package com.ubaya.kost.util

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var INSTANCE: T? = null

    fun getInstance(arg: A): T {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: constructor(arg).also {
                INSTANCE = it
            }
        }
    }
}
