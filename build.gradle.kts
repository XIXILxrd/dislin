import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

group = "dev.rukhlovar"

plugins {
    kotlin("jvm") version "2.2.20"
    application
}

application {
    mainClass = "dev.rukhlovar.MainKt"
}

dependencies {
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

private fun readProperty(key: String, propertiesFile: File? = File(rootProject.rootDir, "local.properties")): String? {
    if (propertiesFile == null) {
        return null
    }

    val properties = Properties().apply {
        propertiesFile.inputStream().use { fis -> load(fis) }
    }

    return properties.getProperty(key)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    val token = readProperty("TOKEN") ?: System.getenv("TOKEN")
    val port = readProperty("SERVER_PORT") ?: System.getenv("SERVER_PORT")
    val address = readProperty("SERVER_ADDRESS") ?: System.getenv("SERVER_ADDRESS")
    val password = readProperty("SERVER_PASSWORD") ?: System.getenv("SERVER_PASSWORD")

    systemProperties("bot.token" to token)
    systemProperties("bot.address" to "$address:$port")
    systemProperties("bot.pass" to password)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}
