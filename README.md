# Alex's Caves — Features and Fixes for NeoForge 1.21.1

This is my feature and maintenance fork of [Raguto's unofficial NeoForge 1.21.1 port](https://github.com/Raguto/AlexsCaves-1.21.1) of [Alex's Caves](https://github.com/AlexModGuy/AlexsCaves). AlexModGuy and the original contributors created the mod; Raguto and the port contributors created the unofficial port this fork builds on. I am continuing that port with additional features, fixes and compatibility work.

This is an unofficial derivative. It is **not** the original mod's official release, and it is **not** a port with no gameplay changes.

## My changes

- Integrates **Alex's Caves Tweak (TACT)** into the same JAR, including its gameplay changes and integrations. TACT is credited separately under its MIT license.
- Repairs cave biome surface-rule handling and checks biome-locate results against the actual biome source.
- Fixes structure-search threading and repeated block scanning around magnetic entities.
- Repairs advancement-icon compatibility with the Citadel line used by this build.
- Fixes behavior such as harvested mussel clusters reappearing, alongside other maintenance changes on the existing port.

The `maintenance` branch contains focused repairs; the `pack` branch is the combined feature build with TACT and is the source of my published combined JARs. The branch name is an implementation detail, not a requirement to use a particular modpack.

## Downloads and requirements

Download the matching runtime JAR from [my releases](https://github.com/ashleywr/alexs-caves-neoforge-1.21.1/releases).

- Minecraft **1.21.1**, **NeoForge**, and **Java 21**.
- A matching **Citadel** build. This maintenance line was built and checked with Citadel **2.7.1** for NeoForge.

Use the chosen release's exact dependency requirements. Install the matching build on the server and clients, and keep only one Alex's Caves build installed.

**Do not add a separate TACT JAR alongside the combined build.** It already contains the `alexscaves` and `tact` mod IDs. The repository rename does not change those IDs or rename saved-world content.

## Building

Use Java 21 and the checked-in wrapper:

```powershell
.\gradlew.bat build -x deploy
```

Exclude `deploy` for an ordinary build: the checkout contains a local deployment task. The runtime JAR is written to `build/libs/`.

Successful compilation does not certify every gameplay path or modpack combination. For a fork-specific problem, use [my issue tracker](https://github.com/ashleywr/alexs-caves-neoforge-1.21.1/issues) with the exact release filename, dependency versions, logs and reproduction steps.

## Credits and license

Credit for the original mod, assets and design remains with AlexModGuy, Noonyeyz and the original contributors. Credit for the unofficial NeoForge port remains with Raguto and its contributors. TACT is by TelepathicGrunt and its contributors.

Alex's Caves remains under **GPL-3.0**. The integrated TACT work retains its **MIT** terms in [TACT-LICENSE.txt](TACT-LICENSE.txt). Existing copyright notices and asset credits remain in place. My additions do not imply endorsement by the original authors or earlier port maintainers. Builds are provided as is; maintenance is best effort.
