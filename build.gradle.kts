plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.8.0"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.0"
}

group = "com.colagom.plugin"
version = "1.0.0"

repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://github.com/ColaGom/SpmDeployPlugin")
        credentials {
            username = project.findProperty("github_username")?.toString() ?: System.getenv("github_username")
            password = project.findProperty("github_password")?.toString() ?: System.getenv("github_password")
        }
    }

    mavenCentral()
}

pluginBundle {
    website = "https://github.com/ColaGom/SpmDeployPlugin"
    vcsUrl = "https://github.com/ColaGom/SpmDeployPlugin.git"

    tags = listOf("kmm", "kotlin multiplatform", "spm")
}

gradlePlugin {
    plugins {
        create("SpmDeployPlugin") {
            id = "com.colagom.plugin.deploy"
            implementationClass = "com.colagom.plugin.SpmDeployPlugin"
            displayName = "SpmDeployPlugin"
            description = "Deploy Kotlin Multiplatform Framework to SPM"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("compiler-embeddable"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}