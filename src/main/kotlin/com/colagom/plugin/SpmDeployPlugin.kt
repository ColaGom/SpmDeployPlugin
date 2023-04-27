package com.colagom.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType


class SpmDeployPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val extension = project.extensions.create<SpmDeployPluginExtension>("spmDeploy")
        extension.buildType.convention(NativeBuildType.DEBUG)
        extension.frameworkName.convention("Shared")
        extension.publicationName.convention("SunflowerKMM")

        afterEvaluate {
            val createXCFrameworkTask = createXCFrameworkTask(extension)
            val zipTask = zipTask(extension)
            val generatePackageFileTask = generatePackageFileTask(extension)

            configPublishingTask(generatePackageFileTask)

            generatePackageFileTask.configure {
                dependsOn(zipTask)
            }

            zipTask.configure {
                dependsOn(createXCFrameworkTask)
            }
        }
    }
}

interface SpmDeployPluginExtension {
    val buildType: Property<NativeBuildType>
    val frameworkName: Property<String>
    val publicationName: Property<String>
}