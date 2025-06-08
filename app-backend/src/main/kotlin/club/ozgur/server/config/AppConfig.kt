package club.ozgur.server.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

data class AppConfig(
    val server: ServerConfig,
    val rpc: RpcConfiguration
) {
    data class ServerConfig(
        val host: String,
        val port: Int,
        val developmentMode: Boolean
    )
    
    data class RpcConfiguration(
        val endpoint: String,
        val maxMessageSize: Long,
        val keepAliveTime: Long
    )
    
    companion object {
        fun load(): AppConfig {
            val config = HoconApplicationConfig(ConfigFactory.load())
            
            return AppConfig(
                server = ServerConfig(
                    host = config.property("ktor.deployment.host").getString(),
                    port = config.property("ktor.deployment.port").getString().toInt(),
                    developmentMode = config.propertyOrNull("ktor.development")
                        ?.getString()?.toBoolean() ?: false
                ),
                rpc = RpcConfiguration(
                    endpoint = config.property("app.rpc.endpoint").getString(),
                    maxMessageSize = config.property("app.rpc.maxMessageSize").getString().toLong(),
                    keepAliveTime = config.property("app.rpc.keepAliveTime").getString().toLong()
                )
            )
        }
    }
} 