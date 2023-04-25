package com.colagom.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip

private const val ARTIFACT_ID = "shared"

fun Project.configPublishingTask(spmTask: TaskProvider<Task>) {
    val publishingExt = publishingExtension
    val deployExt = deployExtension

    val version = version.toString()
    val publicationName = deployExt.publicationName.get()

    publishingExt.publications.create(
        publicationName,
        MavenPublication::class.java
    ) {
        this.version = version
        val archiveProvider =
            tasks.named("zip", Zip::class.java)
                .flatMap { it.archiveFile }

        artifact(archiveProvider) {
            extension = "zip"
        }
        artifactId = ARTIFACT_ID
    }
    val repositoryUri = publishingExt.repositories.filterIsInstance<MavenArtifactRepository>()
        .firstOrNull()?.url?.toString() ?: ""
    val groupInUri = group.toString().replace(".", "/")

    urlFile.writeText("$repositoryUri/$groupInUri/${ARTIFACT_ID}/$version/${ARTIFACT_ID}-$version.zip")

    val publishTaskName =
        "publish${publicationName}PublicationToGitHubPackagesRepository"

    tasks.named(publishTaskName).configure {
        dependsOn(spmTask)
    }
}