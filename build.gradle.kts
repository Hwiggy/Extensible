plugins {
    kotlin("jvm") version "1.5.31"
    id("maven-publish")
    id("java-library")
}

group = "me.hwiggy"
version = "1.4.4"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib", "1.5.31"))
}

publishing {
    repositories {
        mavenLocal()
        when (project.findProperty("deploy") ?: "local") {
            "local" -> return@repositories
            "remote" -> maven {
                if (project.version.toString().endsWith("-SNAPSHOT")) {
                    setUrl("https://nexus.mcdevs.us/repository/mcdevs-snapshots/")
                    mavenContent { snapshotsOnly() }
                } else {
                    setUrl("https://nexus.mcdevs.us/repository/mcdevs-releases/")
                    mavenContent { releasesOnly() }
                }
                credentials {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
    publications {
        create<MavenPublication>("assembly") {
            from(components["java"])
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
