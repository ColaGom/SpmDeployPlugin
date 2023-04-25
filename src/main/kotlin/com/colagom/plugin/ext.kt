package com.colagom.plugin

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

internal const val GROUP_NAME = "deploy"
internal const val TEMP_DIR = "deploy"

internal fun podPublishTaskName(buildType: NativeBuildType) =
    "podPublish${buildType.asString}XCFramework"

internal fun Project.tempDir() = file("$buildDir/$TEMP_DIR")
internal fun Project.frameworkPath(buildType: NativeBuildType) =
    "$buildDir/cocoapods/publish/${buildType.getName()}"

val NativeBuildType.asString get() = getName().capitalized()


internal fun swiftPackagePath(): String =
    "../Package.swift"

internal val Project.urlFile get() = file("$buildDir/url")
internal val Project.zipFile get() = file("${tempDir()}/archive.zip")

internal val Project.publishingExtension get() = extensions.getByType<PublishingExtension>()
internal val Project.deployExtension get() = extensions.getByType<SpmDeployPluginExtension>()
