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

private fun readProperty(key: String, propertiesFile: File = File(rootProject.rootDir, "local.properties")): String {
    val properties = Properties().apply {
        propertiesFile.inputStream().use { fis -> load(fis) }
    }

    return properties.getProperty(key)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    systemProperties("bot.token" to readProperty("TOKEN"))
    systemProperties("bot.address" to readProperty("ADDRESS"))
    systemProperties("bot.pass" to readProperty("PASS"))
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
