package club.ozgur.server.util

interface TimeProvider {
    fun currentTimeMillis(): Long
    
    object Default : TimeProvider {
        override fun currentTimeMillis(): Long = System.currentTimeMillis()
    }
    
    class Fixed(private val fixedTime: Long) : TimeProvider {
        override fun currentTimeMillis(): Long = fixedTime
    }
} 