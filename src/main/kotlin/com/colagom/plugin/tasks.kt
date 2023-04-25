package com.colagom.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset

internal fun Project.zipTask(extension: SpmDeployPluginExtension) =
    tasks.register<Zip>("zip") {
        group = GROUP_NAME

        val zipFile = zipFile
        val from = "${frameworkPath()}/${extension.buildType.get().name}"

        from(from)

        destinationDirectory.set(zipFile.parentFile)
        archiveFileName.set(zipFile.name)
    }


internal fun Project.generatePackageFileTask(extension: SpmDeployPluginExtension) =
    tasks.register("spm") {
        group = GROUP_NAME
        val frameworkName = extension.frameworkName.get()

        doLast(Action {
            val swiftFile = file(swiftPackagePath())
            swiftFile.writeText("")
            val checksum = generateSpmChecksum(zipFile)
            val url = urlFile.readText()

            swiftFile.delete()
            swiftFile.writeText(
                generateSwiftPackageText(
                    frameworkName,
                    url,
                    checksum
                )
            )
        })
    }

internal fun Project.createXCFrameworkTask(extension: SpmDeployPluginExtension): TaskProvider<XCFrameworkTask> {
    val buildType = extension.buildType.get()
    val kotlinExtension = kotlinExtension

    return tasks.register<XCFrameworkTask>("${buildType.name.toLowerCase()}XCFramework") {
        group = GROUP_NAME
        kotlinExtension.appleTargets.forEach {
            from(it.binaries.getFramework(buildType))
        }
        outputDir = file(frameworkPath())
        this.buildType = buildType
        baseName = extension.frameworkName
    }
}

private fun Project.generateSpmChecksum(zipFile: File): String {
    val os = ByteArrayOutputStream()

    exec {
        commandLine(
            "swift",
            "package",
            "compute-checksum",
            zipFile.path
        )
        standardOutput = os
    }

    return os.toByteArray().toString(Charset.defaultCharset()).trim()
}

private fun generateSwiftPackageText(
    frameworkName: String,
    frameworkUri: String,
    checksum: String
): String {
    //from https://developer.apple.com/documentation/packagedescription/package
    return """
        // swift-tools-version:5.3
        import PackageDescription

        let package = Package(
            name: "$frameworkName",
            platforms: [
                .iOS(.v13)
            ],
            products: [
                .library(
                    name: "$frameworkName",
                    targets: ["$frameworkName"])
            ],
            dependencies: [],
            targets: [
                .binaryTarget(
                    name: "$frameworkName",
                    url: "$frameworkUri",
                    checksum: "$checksum"
                )
            ]
        )
    """.trimIndent()
}