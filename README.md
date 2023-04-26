# SpmDeployPlugin

Gradle plugin for Kotlin multiplatform

# Features

1. Archive & zip `xcframework` for deploying
2. Deploy archive to Github packages repository
3. Generate `Pacakge.swift` file for SPM(Swift Pacakge Manager)

# Getting Start
### 1. Add plugin to module `build.gradle`
```kotlin
id("io.github.colagom.deploy") version "1.0.0"
```

### 2. Setup publish to github packages repo
```
...
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("[input your repository url]")
            credentials { [setup credentials, must required write package permission] } 
        }
    }
}
```

### 3. Setup plugin
```
spmDeploy {
    frameworkName.set("Shared")
    buildType.set(NativeBuildType.DEBUG)
    publicationName.set("SunflowerShared")
}
```
