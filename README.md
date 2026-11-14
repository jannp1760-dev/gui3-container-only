# GUI 3 – Container Screens Only

Minecraft Java **1.21.11** · Fabric · Client-side

This repository contains the source archive `gui3-source.zip` and a GitHub Actions workflow that builds the mod automatically.

## Features
- Configurable scaling for inventory and container screens
- Separate switches for player inventory, storage containers, workstations and other screens
- X/Y position offsets
- Settings saved to `.minecraft/config/gui3container.json`
- The in-game HUD and hotbar are not globally rescaled

## Build
GitHub Actions runs the build on pushes to `main` or via manual workflow dispatch. The output JAR is uploaded as an Actions artifact named `gui3-container-only-1.1.0`.

Open the repository's **Actions** tab to inspect the build and download the artifact after a successful run.
