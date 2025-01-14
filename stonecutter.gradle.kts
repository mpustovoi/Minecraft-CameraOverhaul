plugins {
	id("dev.kikugie.stonecutter")
}

stonecutter active "fabric-1.20.6" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("buildAllVersions", stonecutter.chiseled) {
	group = "build"
	ofTask("build")
}

stonecutter.configureEach {
	val mcVersion = project.property("mc.version").toString()
	swap("mc", mcVersion)

	val loader = project.property("loader.id").toString()
	val mcType = project.property("mc.type").toString()
	const("FABRIC_LOADER", loader == "fabric")
	const("FORGE_LOADER", loader == "forge")
	const("MC_RELEASE", mcType == "release")
	const("MC_BETA", mcType == "beta")
	const("MC_ALPHA", mcType == "alpha")
	const("false", false)
}
