plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.kotlinxRpcPlugin)
    alias(libs.plugins.ktor)
    
}

application {
    mainClass = "club.ozgur.server.ApplicationKt"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(project(":shared"))

    // RPC Server dependencies
    implementation(libs.bundles.kotlinxRpcServer)

    // Ktor
    implementation(libs.ktorServerCore)
    implementation(libs.ktorServerNetty)
    implementation(libs.ktorServerContentNegotiation)
    implementation(libs.ktorSerializationKotlinxJson)
    implementation(libs.ktorServerCallLogging)
    implementation(libs.ktorServerCallId)
    implementation(libs.ktorServerDefaultHeaders)
    implementation(libs.ktorServerStatusPages)
    implementation(libs.ktorServerConfigYaml)
    implementation(libs.ktorServerWebsockets)

    // Configuration
    implementation(libs.typesafeConfig)

    // Monitoring and Metrics
    implementation(libs.ktorServerMetricsMicrometer)
    implementation(libs.micrometerPrometheus)

    // Logging
    implementation(libs.bundles.logging)

    // Coroutines
    implementation(libs.kotlinxCoroutines)

    // Testing
    testImplementation(libs.ktorServerTestHost)
    testImplementation(libs.bundles.testing)
}