pluginManagement {
	repositories {
		// Shared
		mavenCentral()
		gradlePluginPortal()
		// Stonecutter
		maven("https://maven.kikugie.dev/snapshots")
		// Fabric
		maven("https://maven.fabricmc.net")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.4.5" //"0.5-beta.2"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	shared {
		vers("fabric-1.14.4", "1.14.4")
		vers("fabric-1.15", "1.15")
		vers("fabric-1.17", "1.17")
		vers("fabric-1.19", "1.19")
		vers("fabric-1.19.3", "1.19.3")
		vers("fabric-1.20.6", "1.20.6")
		vcsVersion = "fabric-1.20.6"
	}

	create(rootProject)
}