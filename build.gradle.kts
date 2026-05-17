import java.util.*

private fun readProperty(key: String, propertiesFile: File = File(rootProject.rootDir, "local.properties")): String {
    val properties = Properties().apply {
        propertiesFile.inputStream().use { fis -> load(fis) }
    }

    return properties.getProperty(key)
}

plugins {
    kotlin("jvm") version "2.2.20"
}

group = "dev.rukhlovar"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.kord.core)
    implementation(libs.kord.voice)
    implementation(libs.kord.core.voice)
    implementation(libs.kord.lavakord)
    implementation(libs.kord.rest)
    implementation(libs.lavakord)
    implementation(libs.lavasrc)
    implementation(libs.lavasrc.protocol)
    implementation(libs.kotlinx.coroutines)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    systemProperties("bot.token" to readProperty("TOKEN"))
    systemProperties("bot.address" to readProperty("ADDRESS"))
    systemProperties("bot.pass" to readProperty("PASS"))
}