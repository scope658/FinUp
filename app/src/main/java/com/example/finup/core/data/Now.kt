package com.example.finup.core.data

interface Now {
    fun timeInMills(): Long
    class Base: Now {
        override fun timeInMills(): Long {
            return System.currentTimeMillis()
        }
    }
}