# Advancement Tracker

A Minecraft Fabric mod that allows for detailed advancement progress tracking for the following advancements:

- Adventuring Time (Discover all biomes)
- Hot Tourist Destination (Discover all Nether biomes)
- A Complete Catalogue (Tame all cat variants)
- The Whole Pack (Tame all wolf variants)
- When The Squad Hops Into Town (Use a lead on all frog variants)
- A Balanced Diet (Eat one of every food item)
- Two By Two (Breed every animal)
- Monsters Hunter (Kill every mob)

## Features

- Detailed tracking of advancement requirements with real-time progress updates
- Easy-to-use UI to view your detailed progress, accessible via keybind (default: "J") 
- **Real-time Updates**: Progress is tracked and updated as you play
- **Server-Client Synchronization**: Works seamlessly in multiplayer environments
- **Lightweight**: Minimal performance impact while providing comprehensive tracking


## Download
- If you just want the mod itself and you don't care about the source code, just download the jar file for the corresponding Minecraft version from the releases folder

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/)
2. Install [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. Download the latest release of this mod from the releases page or releases folder
4. Place the mod file in your `mods` folder

## Building

To build the mod yourself:

1. Clone this repository
2. Run `./gradlew clean build`
3. The built mod will be in `build/libs/`

## Testing

### Development Testing

1. Run the mod in development environment:
   ```bash
   ./gradlew runClient
   ```

      **Note**: If you encounter mixin errors during startup, check that:
      - All package names in your mixin configuration files match your actual package structure
      - Mixin classes exist in the specified locations
      - The mod ID in `fabric.mod.json` matches your mixin configuration


## Compatibility

- Minecraft: 1.21.8
- Fabric Loader: 0.16.9+
- Fabric API: 0.110.0+1.21.8

## License

This mod is licensed under the MIT License. See [LICENSE](LICENSE) for details.
