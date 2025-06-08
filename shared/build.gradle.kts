plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.kotlinxRpcPlugin)

}

dependencies {
    api(libs.kotlinxRpcCore)
    api(libs.kotlinxSerializationCore)
}