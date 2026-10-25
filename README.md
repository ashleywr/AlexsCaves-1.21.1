# Alex's Caves - 1.21.1 Port

This is a port of [Alex's Caves](https://github.com/AlexModGuy/AlexsCaves) to Minecraft 1.21.1.

**Original Author:** [AlexModGuy](https://github.com/AlexModGuy)  
**License:** GPL-3.0

## Changes

- Updated code to work with Minecraft 1.21.1 API changes
- Ported from Forge to NeoForge
- Fixed deprecated methods and API calls
- No gameplay or feature changes

All credit goes to the original author. This is purely a version compatibility update.

<!-- ashley-release-information -->

## Ashley's builds

This is an unofficial maintenance fork. Original authors retain ownership and credit. See the [upstream project](https://github.com/Raguto/AlexsCaves-1.21.1) for the original mod. Local changes do not imply upstream endorsement.

Licensing: **GPL-3.0**. Existing license files, copyright notices, attribution and any separate asset terms remain in force. This fork does not relicense upstream work.

Provided **AS IS**, without warranty of any kind. Maintenance, updates and support are best effort; compatibility with every modpack is not guaranteed.

### Downloads and installation

Download a runtime `.jar` from [GitHub Releases](https://github.com/ashleywr/AlexsCaves-1.21.1/releases). Use the Minecraft version and loader named in that release and install its required dependencies. Do not install `-sources`, `-dev`, `-slim`, `-common` or test JARs as the mod.

Close Minecraft, back up your world, and place the matching runtime JAR in the instance's `mods/` directory. Replace older versions of this same mod rather than loading duplicates. For content mods, install matching versions on the server and its clients unless the release explicitly says client-only or server-only.

### Building and reporting problems

Use Java 21 and the checked-in Gradle wrapper:

```powershell
.\gradlew.bat build
```

Build outputs are under `build/libs/` or each loader subproject's `build/libs/`. Some projects require the local development dependencies described above; those dependencies are not included in the download.

For a problem report, include the exact release filename, Minecraft and loader versions, relevant mod versions, `logs/latest.log`, and a crash report if one was written. Remove private information before sharing logs. Remove the JAR to uninstall; worlds containing this mod's blocks, entities or world generation may require restoring a backup.
