# Floating Island (Fabric 1.21)

## Requirements
- Minecraft 1.21
- Java 21
- Fabric Loader and Fabric API
- Install this mod on both the client and the server

## Controls
- J: Toggle island on/off
- K: Switch between 16x16 and 32x32 (rebuilds the island if enabled)

The server checks permission level 2 before changing blocks.

## Build
On Windows:
```powershell
gradle wrapper --gradle-version 8.8
.\gradlew.bat build
```

On Linux/macOS:
```bash
gradle wrapper --gradle-version 8.8
./gradlew build
```

Output: `build/libs/floating-island-1.0.0.jar`

## Restoration warning
The original block states are kept in server memory. Turn the island off before restarting the server. The backup is not persisted across restarts. Changes made by other players inside the island area while it is active may be overwritten when restoring.
