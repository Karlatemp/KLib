plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "io.github.karlatemp"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    // SpigotMC
    maven(url = "https://hub.spigotmc.org/nexus/content/groups/public")
    jcenter()
}

tasks.withType(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    dependencies {
        exclude {
            when ("${it.moduleGroup}:${it.moduleName}") {
                "net.md-5:bungeecord-chat" -> true
                "org.spigotmc:spigot-api" -> true
                "commons-lang:commons-lang" -> true
                "org.yaml:snakeyaml" -> true
                "com.google.code.gson:gson" -> true
                "com.google.guava:guava" -> true

                else -> {
                    println("${it.moduleGroup} ${it.moduleName} ${it.moduleVersion}")
                    false
                }
            }
        }
    }
}

inline fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

dependencies {
    implementation("org.jetbrains:annotations:19.0.0")
    implementation(kotlin("stdlib-jdk8", "1.3.72"))
    implementation(kotlinx("coroutines-core", "1.3.4"))
    implementation(kotlinx("coroutines-io", "0.1.16"))
    implementation("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    implementation("org.apache.httpcomponents:httpclient:4.5.12")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

//kotlin {
//    sourceSets {
//        all {
//        }
//    }
//}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        kotlinOptions.freeCompilerArgs += "-XXLanguage:+PolymorphicSignature"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}