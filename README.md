![Img](https://i.imgur.com/H3UrLFP.png)

## ❗ What's this?!
**CameraOverhaul** a clientside Minecraft mod that attempts to improve overall satisfaction of the game through the introduction of various camera rotations, to emphasize on the player's movement and improve visual feedback. Strafing and turning around will tilt the camera sideways. Jumping, falling, and moving forward/backwards will affect the camera's pitch.

### Info:
- **Available** at [Modrinth](https://modrinth.com/mod/cameraoverhaul) & [CurseForge](https://www.curseforge.com/minecraft/mc-mods/cameraoverhaul);
- **Made** for [Fabric](https://fabricmc.net) & [Quilt](https://quiltmc.org);
- **Requires** [Cloth Config API](https://modrinth.com/mod/cloth-config);
- **Supports** Minecraft versions from `1.14` to `1.21` and up, releases are version-agnostic;
- Works best at high framerates - [Sodium](https://modrinth.com/mod/sodium), [Starlight](https://modrinth.com/mod/starlight), [Lithium](https://modrinth.com/mod/lithium), and other [optimization mods](https://modrinth.com/mods?f=categories%3A%27optimization%27) are **recommended**.

## ⚙️ Can I turn off X and only keep Y?
You can use [Mod Menu](https://modrinth.com/mod/modmenu) (or an alternative of your choosing) to configure the mod in-game.

You can also manually find the configuration file with intensity scaling for all features at<br/>
`.minecraft/config/cameraoverhaul.json`.

Set any factor to `0.0` to turn its feature off.<br/>
Negative numbers are also usually legal, allowing inverting effects, if that's your thing.

## 📖 License
Released under the [GNU General Public License 3.0](https://github.com/Mirsario/Minecraft-CameraOverhaul/blob/dev/LICENSE.md).
<br/>
Copyright (c) 2020-2024 Mirsario & Contributors.

## ❤️ Contributing
This project uses the [Stonecutter](https://stonecutter.kikugie.dev) comment-macro preprocessor for multi-version support.
- The IntelliJ [Stonecutter Dev](https://plugins.jetbrains.com/plugin/25044-stonecutter-dev) plugin is highly recommended. Manually install it in zip form if you see complaints about your IDE version not being supported.
- After getting the repository's source code, use `gradlew tasks` to list available Gradle tasks.
- Use the `buildAllVersions` Gradle task to properly build all targets of the mod.
- Use tasks under the `Stonecutter` group to "checkout" specific versions, running the preprocessor on the source code and updating all comment blocks.

Thank you to the following contributors for helping improve the mod for everyone!

<a href="https://github.com/Mirsario/Minecraft-CameraOverhaul/graphs/contributors">
	<img src="https://contrib.rocks/image?repo=Mirsario/Minecraft-CameraOverhaul&max=900&columns=20" />
</a>
