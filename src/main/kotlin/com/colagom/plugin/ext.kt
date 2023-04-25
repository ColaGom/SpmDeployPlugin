package com.colagom.plugin

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

internal const val GROUP_NAME = "deploy"
internal const val TEMP_DIR = "spm-deploy"

internal fun Project.tempDirPath() = "$buildDir/$TEMP_DIR"
internal fun Project.tempDir() = file(tempDirPath())
internal fun Project.frameworkPath() =
    "${tempDirPath()}/framework"

val NativeBuildType.asString get() = getName().capitalized()

internal fun swiftPackagePath(): String =
    "../Package.swift"

internal val Project.urlFile get() = file("$buildDir/url")
internal val Project.zipFile get() = file("${tempDir()}/archive.zip")

internal val Project.publishingExtension get() = extensions.getByType<PublishingExtension>()
internal val Project.deployExtension get() = extensions.getByType<SpmDeployPluginExtension>()
internal val Project.kotlinExtension get() = extensions.getByType<KotlinMultiplatformExtension>()
internal val KotlinMultiplatformExtension.appleTargets
    get() = targets.withType(KotlinNativeTarget::class.java).filter { it.konanTarget.family.isAppleFamily }
