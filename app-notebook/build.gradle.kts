import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("buildsrc.convention.kotlin-jvm")

    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinPluginCompose)

    alias(libs.plugins.kotlinxRpcPlugin)
}


dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)

    implementation(libs.voyagerNavigator)
    implementation(libs.voyagerTransitions)
    implementation(libs.materialIconsExtended)

    implementation(libs.bundles.kotlinxRpcClient)

    implementation(libs.bundles.ktorClient)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.ktorClientMock)

}

compose.desktop {
    application {
        mainClass = "club.ozgur.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "club.ozgur"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "club.ozgur.demo"
                iconFile.set(project.file("src/main/resources/icon.icns"))
            }
        }
    }
}