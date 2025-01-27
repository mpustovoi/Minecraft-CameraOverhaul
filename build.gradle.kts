// Copyright 2020-2025 Mirsario & Contributors.
// Released under the GNU General Public License 3.0
// See LICENSE.md for details.

plugins {
	id("dev.architectury.loom")
	id("architectury-plugin")
	id("com.github.johnrengelman.shadow")
}

// Utilities.
fun Project.required(p: String) = property(p).toString()
fun Project.optional(p: String) = findProperty(p)?.toString()

repositories {
	// NeoForge
	maven("https://maven.neoforged.net/releases")
	// Libraries
	maven("https://maven.shedaniel.me") // ClothConfig
	maven("https://maven.terraformersmc.com") // ModMenu
	maven("https://maven.nucleoid.xyz") // Placeholder API (ModMenu dependency)
}

// Set architectury platforms.
architectury.common(stonecutter.tree.branches.mapNotNull {
	if (stonecutter.current.project in it) it.project.optional("loom.platform") else null
})

val minecraft = stonecutter.current.version
val loader = loom.platform.get().name.lowercase()
val mcType = required("mc.type")
val mcVersion = required("mc.version")
val isFabric = loader == "fabric"
val isForge = loader == "forge"
val isNeoForge = loader == "neoforge"
val isForgeLike = isForge || isNeoForge
val shadowLibs = isForge && stonecutter.eval(mcVersion, "<1.19")

base {
	group = required("maven_group")
	version = "v${required("mod.version")}-${loader}+mc[${required("mc.displayed_range")}]"
	archivesName.set(required("archives_base_name"))
}

// Configure Java.
java {
	val java = if (stonecutter.eval(mcVersion, ">=1.20.5")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	sourceCompatibility = java
	targetCompatibility = java
}

// Setup preprocessor.
stonecutter {
	const("FABRIC", isFabric)
	const("FORGE", isForge)
	const("NEOFORGE", isNeoForge)
	const("FORGE_LIKE", isForgeLike)

	const("MC_RELEASE", mcType == "release")
	const("MC_BETA", mcType == "beta")
	const("MC_ALPHA", mcType == "alpha")
	const("false", false)
}

// Prepare Shadow to inline libraries right into our JAR on legacy Forge.
val shade: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
	isTransitive = false
}
if (shadowLibs) {
	tasks.shadowJar {
		configurations = listOf(shade)
		archiveClassifier = "dev-shadow"
		minimize()
	}
	tasks.remapJar {
		input = tasks.shadowJar.get().archiveFile
		archiveClassifier = null
		dependsOn(tasks.shadowJar)
	}
}

// To change any versions see the gradle.properties files under root and "/versions/*/"
dependencies {
	minecraft("com.mojang:minecraft:${mcVersion}")
	mappings(loom.officialMojangMappings())

	// Common libraries
	implementation("io.hotmoka:toml4j:0.7.3") { if (shadowLibs) shade(this) else include(this) }
	if (stonecutter.eval(mcVersion, "<1.19.3")) {
		implementation("org.joml:joml:1.10.5") { if (shadowLibs) shade(this) else include(this) }
	}

	// Cloth Config
	val clothConfigVersion: String = required("mods.clothconfig.ref").toString()
	val clothConfigMajor: Int = if (clothConfigVersion != "[VERSIONED]") clothConfigVersion.split(".")[0].toInt() else 0
	modApi("me.shedaniel.cloth:${if (clothConfigMajor <= 2) "config-2" else "cloth-config-${loader}"}:${clothConfigVersion}") {
		// Prevent preparing two loader versions in cache. Not needed.
		exclude(group = "net.fabricmc")
		exclude(group = "net.fabricmc.fabric-api")
	}

	if (loader == "fabric") {
		modImplementation("net.fabricmc:fabric-loader:${required("deps.fabric_loader")}")

		// ModMenu API
		modImplementation("com.terraformersmc:modmenu:${required("mods.modmenu.ref")}")
	}
	// Note: String invocation means that the function resolution is delayed to the buildscript's runtime.
	if (loader == "forge") {
		"forge"("net.minecraftforge:forge:${required("deps.forge_loader")}")
	}
	if (loader == "neoforge") {
		"neoForge"("net.neoforged:neoforge:${required("deps.neoforge_loader")}")
	}
}

loom {
	//accessWidenerPath = rootProject.file("src/main/resources/${required("mod.id")}.accesswidener")

	decompilers {
		get("vineflower").apply { // Adds names to lambdas - useful for mixins
			options.put("mark-corresponding-synthetics", "1")
		}
	}
	if (loader == "forge") {
		forge.mixinConfigs("${required("mod.id")}.mixins.json")
	}
}

tasks.processResources {
	fun plainList(str: String) = str.lines().joinToString(", ") { it.trim() }
	fun fancyList(str: String) = str.lines().joinToString("\n") { "- ${it.trim()}" }
	fun jsonList(str: String) = str.lines().joinToString(", ") { "\"${it.trim()}\"" }

	val version = "${required("mod.version")}-${loader}+mc.${required("mc.displayed_range")}"
    var properties = mapOf(
		"mod_id" to required("mod.id"),
		"mod_name" to required("mod.name"),
		"mod_description" to required("mod.description"),
		"mod_description_esc" to required("mod.description").replace("\n", "\\n"),
		"mod_version" to version,
		"mod_authors" to plainList(required("mod.authors")),
		"mod_authors_list" to fancyList(required("mod.authors")),
		"mod_authors_jarray" to jsonList(required("mod.authors")),
		"mod_contributors" to plainList(required("mod.contributors")),
		"mod_contributors_list" to fancyList(required("mod.contributors")),
		"mod_contributors_jarray" to jsonList(required("mod.contributors")),
		"mod_forgeupdatecheckurl" to required("mod.forgeupdatecheckurl"),
		"mc_version_range" to required("mc.version_range"),
		// Contact (Mod)
		"contact_homepage" to required("contact.homepage"),
		"contact_sources" to required("contact.sources"),
		"contact_issues" to required("contact.issues"),
		"contact_modrinth" to optional("contact.modrinth"),
		"contact_curseforge" to optional("contact.curseforge"),
		// Contact (Author)
		"contact_email" to required("contact.email"),
		"contact_discord" to optional("contact.discord"),
		"contact_patreon" to optional("contact.patreon"),
		"contact_youtube" to optional("contact.youtube"),
		// Libraries
		"mods_clothconfig_range" to required("mods.clothconfig.range"),
	)
	if (isFabric) properties = properties.plus(mapOf(
		"mods_modmenu_range" to required("mods.modmenu.range"),
	))

	fun expandLoaderFile(include: Boolean, pattern: String) = filesMatching(pattern) { if (!include) exclude() else expand(properties) }

	expandLoaderFile(isFabric, "fabric.mod.json")
	expandLoaderFile(isForge, "META-INF/mods.toml")
	expandLoaderFile(isNeoForge, "META-INF/neoforge.mods.toml")
	expandLoaderFile(isForgeLike, "pack.mcmeta")

	inputs.properties(properties)
}

// Copy produced jars into /out/
val copyJars = tasks.register<Copy>("copyJars") {
	from(tasks.getByName("remapJar"))
	into("../../out/")
}
tasks.getByName("build").finalizedBy(copyJars)

tasks.build {
	group = "hidden"
	description = "Run 'buildAllVersions' instead!"
}