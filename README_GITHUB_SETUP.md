# SkyblockMulti - GitHub setup

This folder is prepared to be copied directly into the root of the cloned GitHub repository.

## First upload

1. Copy all files and folders from this package into the local `SkyblockMulti` repository folder.
2. When Windows asks whether to merge folders, choose **Yes**.
3. Open GitHub Desktop.
4. Review the changed files.
5. Commit with a message such as:
   `Import SkyblockMulti 0.1.1-beta source`
6. Click **Push origin**.
7. Open the repository on GitHub and select the **Actions** tab.
8. The `Build SkyblockMulti` workflow should start automatically.

## Important

The build workflow is configured for:
- Minecraft 26.2
- Fabric Loader 0.19.3
- Fabric API 0.156.0+26.2
- Java 25
- Gradle 9.1.0 on GitHub Actions

Open Parties and Claims integration is not yet implemented. The mod only declares it as an optional suggested dependency.
