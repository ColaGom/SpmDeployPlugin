package com.colagom.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset


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

internal fun Project.podPublishTask(extension: SpmDeployPluginExtension): TaskProvider<Task> {
    return tasks.named(podPublishTaskName(extension.buildType.get()))
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