package club.ozgur.server.util

import java.util.UUID

interface IdGenerator {
    fun generate(): String
    
    object Default : IdGenerator {
        override fun generate(): String = UUID.randomUUID().toString()
    }
    
    class Sequential(private val prefix: String = "note") : IdGenerator {
        private var counter = 0L
        
        override fun generate(): String {
            return "$prefix-${System.currentTimeMillis()}-${counter++}"
        }
    }
} 