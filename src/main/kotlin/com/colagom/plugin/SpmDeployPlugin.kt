package com.colagom.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType


class SpmDeployPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val extension = project.extensions.create<SpmDeployPluginExtension>("spmDeploy")
        extension.buildType.convention(NativeBuildType.DEBUG)
        extension.frameworkName.convention("Shared")
        extension.publicationName.convention("SunflowerKMM")

        afterEvaluate {
            val zipTask = zipTask(extension)
            val publishTask = podPublishTask(extension)
            val spmTask = generatePackageFileTask(extension)

            configPublishingTask(spmTask)

            spmTask.configure {
                dependsOn(zipTask)
            }

            zipTask.configure {
                dependsOn(publishTask)
            }
        }
    }
}


internal fun Project.zipTask(extension: SpmDeployPluginExtension) =
    tasks.register<Zip>("zip") {
        group = GROUP_NAME

        val zipFile = zipFile
        val from = frameworkPath(extension.buildType.get())

        exclude {
            it.file.extension == "podspec"
        }

        from(from)

        destinationDirectory.set(zipFile.parentFile)
        archiveFileName.set(zipFile.name)
    }


interface SpmDeployPluginExtension {
    val buildType: Property<NativeBuildType>
    val frameworkName: Property<String>
    val publicationName: Property<String>
}