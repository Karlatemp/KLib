buildscript {
    repositories {
        maven("https://gitee.com/Karlatemp/Karlatemp-repo/raw/master/")
    }
    dependencies.classpath("io.github.karlatemp:Java8Converter:1.0.3")
}

io.github.karlatemp.java8converter.Java8Converter().apply(project)

tasks.named("java8converter", io.github.karlatemp.java8converter.ConverterTask::class.java).configure {
    dependsOn("shadowJar")
    setup {
        filter {
            name.startsWith("io/github/karlatemp/klib/")
        }
        resource {
            name.endsWith(".yml")
        }
        "version" property project.version
    }
}

plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
//     id("io.github.karlatemp.Java8Converter") version "1.0.0-Alpha"
}


group = "io.github.karlatemp"
version = "1.2.1-Alpha"

repositories {
    mavenLocal()
    mavenCentral()
    // SpigotMC
    maven(url = "https://hub.spigotmc.org/nexus/content/groups/public")
    jcenter()
}

tasks.withType(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    this.archiveClassifier.convention("").set("")
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

tasks.named("jar").get().enabled = false

@Suppress("NOTHING_TO_INLINE")
inline fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

dependencies {
    implementation("org.ow2.asm:asm-commons:8.0.1")
    implementation("org.ow2.asm:asm-tree:8.0.1")
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

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}

tasks {
    compileJava {
        with(options) {
            this.compilerArgs.addAll(listOf("--add-exports", "java.base/jdk.internal.reflect=ALL-UNNAMED"))
            this.compilerArgs.addAll(listOf("--add-exports", "java.base/jdk.internal.misc=ALL-UNNAMED"))
        }
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        kotlinOptions.freeCompilerArgs += "-XXLanguage:+PolymorphicSignature"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}