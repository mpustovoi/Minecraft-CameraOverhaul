// Copyright 2020-2025 Mirsario & Contributors.
// Released under the GNU General Public License 3.0
// See LICENSE.md for details.

plugins {
	id("fabric-loom") version "1.8-SNAPSHOT"
}

base {
	group = property("maven_group")!!
	version = "v${property("mod.version")}-${property("loader.id")}+mc${property("mc.displayed_range")}"
	archivesName.set(property("archives_base_name").toString())
}

repositories {
	maven("https://maven.shedaniel.me")
	maven("https://maven.terraformersmc.com/releases")
}

val mcVersion = property("mc.version").toString()
val clothConfigVersion = property("mods.clothconfig.ref").toString()
val clothConfigMajor: Int = if (clothConfigVersion != "[VERSIONED]") clothConfigVersion.split(".")[0].toInt() else 0

// To change any versions see the gradle.properties files under root and "/versions/*/"
dependencies {
	minecraft("com.mojang:minecraft:${mcVersion}")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

	// Common libraries
	implementation("io.hotmoka:toml4j:0.7.3") { include(this) }
	if (stonecutter.eval(mcVersion, "<1.19.3")) {
		implementation("org.joml:joml:1.10.5") { include(this) }
	}

	// Cloth Config
	modApi("me.shedaniel.cloth:${if (clothConfigMajor <= 2) "config-2" else "cloth-config-fabric"}:${clothConfigVersion}") {
		// Prevent preparing two loader versions in cache. Not needed.
		exclude(group = "net.fabricmc")
		exclude(group = "net.fabricmc.fabric-api")
	}
	// ModMenu API, to add the Config Screen to it
	modImplementation("com.terraformersmc:modmenu:${property("mods.modmenu.ref")}")
}

val javaVersion = if (stonecutter.eval(mcVersion, ">=1.20.6")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
tasks.withType<JavaCompile> {
	sourceCompatibility = javaVersion.toString()
	targetCompatibility = javaVersion.toString()
}

tasks.processResources {
	filesMatching("fabric.mod.json") {
		expand(mapOf(
			"mod_version" to project.property("mod.version"),
			"mc_version_range" to project.property("mc.version_range"),
			"mods_clothconfig_range" to project.property("mods.clothconfig.range"),
			"mods_modmenu_range" to project.property("mods.modmenu.range")
		))
	}
}

loom {
	runConfigs.all {
		ideConfigGenerated(true) // Run configurations are not created for subprojects by default
		runDir("../../run") // Use a shared run folder and create separate worlds
	}
}

// Copy produced jars into /out/
val copyJars = tasks.register<Copy>("copyJars") {
	from(tasks.getByName("remapJar"))
	into("../../out/")
}
tasks.getByName("build").finalizedBy(copyJars)