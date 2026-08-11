# SkyblockMulti

Configurable multiplayer Skyblock for **Minecraft Java 26.2** on **Fabric**, focused on multiplayer-safe island allocation, player-selectable difficulty, custom progression, optional party integration, and a growing world/lore layer.

> **Latest published checkpoint:** `v0.1.1-beta`  
> **Current development:** `0.2.0-beta` on `feature/world-foundation`

## Status

SkyblockMulti is currently in beta development.

`v0.1.1-beta` closes the multiplayer/OpenPAC foundation phase. Development of `0.2.0-beta` begins the world-foundation phase: the HUB, protected introductory refuge, fortress/End progression, and broader world design.

## Requirements

- Minecraft Java **26.2**
- Fabric Loader **0.19.3+**
- Fabric API for Minecraft 26.2
- Java **25**
- Mod Menu — optional, recommended for in-game configuration
- Open Parties and Claims — optional, for shared-party island integration

OpenPAC integration has been tested with **Open Parties and Claims 0.29.3**.

## Main features

- Configurable multiplayer Skyblock with **8, 16 or 24 island capacity**
- Circular island layouts around a shared central HUB
- Random free-slot assignment
- In 24-island worlds:
  - 8-slot inner ring
  - 16-slot outer ring
  - Easy mode prioritizes inner slots
  - Extreme / Hard / Standard prioritize outer slots
  - automatic overflow when the preferred ring is full
- 11 selectable tree starts plus a Random option
- Four player difficulty modes:
  - Extreme
  - Hard
  - Standard
  - Easy
- Configurable minimum difficulty
- Custom starting chests tied to difficulty
- Custom recipes and Skyblock-oriented progression
- Custom advancement tree, including secret advancements
- Initial lore book
- Secret End advancement for preserving the original lore book
- Optional OpenPAC party integration
- English and Spanish client localization

## Multiplayer and OpenPAC

SkyblockMulti can run without Open Parties and Claims.

When OpenPAC is installed:

- Party members can share the party owner's active island.
- A member's personal island is preserved while playing with another party.
- Leaving or being removed from a party returns the player to their own island if they already have one.
- A player who joined a party before creating a personal island is treated as a new player if they later leave that party:
  - inventory is reset
  - armor/offhand are reset
  - XP is reset
  - Ender Chest is reset
  - the player returns to the HUB
  - the initial island-selection cycle begins again
- Bed/respawn points are reset when party ownership context changes, preventing players from respawning on islands they no longer belong to.

## Island geometry

The world uses a shared HUB at the center of the Overworld and distributes islands around it.

### 8 islands

One circular ring.

Available HUB radius options:

`512 · 1024 · 2048 · 4096 · 8192`

### 16 islands

One circular ring.

Available HUB radius options:

`1024 · 2048 · 4096 · 8192 · 16384`

### 24 islands

Two circular rings:

- inner ring: 8 islands
- outer ring: 16 islands

The configured value represents the **outer radius**. The inner ring automatically uses half that radius.

Available outer-radius options:

`2048 · 4096 · 8192 · 16384`

World geometry becomes persistent after the first island is assigned, preventing capacity/radius changes from moving existing islands.

## Tree starts

Available initial tree types:

- Oak
- Spruce
- Birch
- Jungle
- Acacia
- Cherry
- Mangrove
- Dark Oak
- Pale Oak
- Azalea
- Flowering Azalea
- Random

Random only selects from tree types enabled in the world configuration.

## Difficulty

Each player chooses their own starting difficulty after selecting a tree.

| Difficulty | Starting resources |
|---|---|
| Extreme | Empty chest |
| Hard | Water + lava |
| Standard | Standard Skyblock starter resources |
| Easy | Additional starter resources and bed |

The server validates the selection. Difficulty options that are below the configured minimum remain visible but unavailable.

When OpenPAC is installed, a separate minimum difficulty can be configured for players who restart after leaving a party without ever owning an island.

## Lore and progression

New players receive the **Chronicle of the Last Refuge**, an initial written book that introduces the world and its background.

The book is intentionally worth preserving: reaching the End while still carrying the original SkyblockMulti lore book unlocks a secret advancement.

The custom advancement tree is designed both as progression tracking and as a guide for Skyblock gameplay.

## Current world structure

### Overworld

- Void world
- Shared central HUB
- Skyblock islands distributed around the HUB

### Nether

- Void-based Skyblock progression

### End

- Vanilla End generation remains enabled
- Central island, dragon fight, gateways, outer islands, End Cities and ships remain part of progression

## 0.2.0-beta — World Foundation

The `0.2.0-beta` development phase is focused on world identity and progression.

The following items are **planned/in development and should not be considered implemented until their corresponding commits are completed**:

- The **Last Refuge**, a protected glass-dome introductory HUB
- A walkable natural environment inside the dome
- Mod-level HUB protection against destructive player/environment interactions
- A transparent central observation floor
- A protected fortress beneath the refuge
- A stronghold-inspired End portal room
- Configurable initial End Portal Eyes:
  - `0%` = 0/12 eyes
  - `25%` = 3/12 eyes
  - `75%` = 9/12 eyes
  - never 100%
- No direct passage from the introductory dome to the fortress
- Future access to the fortress through lateral progression/dungeon content
- Expanded world lore and exploration progression

## Configuration

SkyblockMulti stores its configuration in:

```text
config/skyblockmulti.json
```

The in-game configuration screen can control, depending on installed optional mods:

- island capacity
- HUB/island radius
- enabled tree starts
- minimum starting difficulty
- minimum difficulty after leaving an OpenPAC party without a personal island

Capacity and radius become locked per world after the first island is assigned.

## Localization

SkyblockMulti includes localized UI files for:

English:
- `en_us`
- `en_gb`
- `en_au`
- `en_ca`
- `en_nz`

Spanish:
- `es_es`
- `es_419`
- `es_cl`
- `es_mx`
- `es_ar`
- `es_ec`
- `es_uy`
- `es_ve`

Each client can display translated menus in its own language on the same multiplayer server.

## Installation

1. Install Minecraft Java 26.2.
2. Install Fabric Loader 0.19.3 or newer.
3. Install Fabric API.
4. Place the SkyblockMulti `.jar` in the Minecraft `mods` folder.
5. Optionally install Mod Menu.
6. Optionally install Open Parties and Claims for party integration.
7. Create a world using the SkyblockMulti world preset.

For multiplayer, installing SkyblockMulti on both server/host and clients is recommended so players receive the intended translated UI.

## Development branches

- `main` — published/checkpoint code
- `feature/world-foundation` — current `0.2.0-beta` development
- older feature branches may remain temporarily for historical/reference purposes

## License

Copyright © Treecs. All rights reserved.

This repository is currently distributed under an **All Rights Reserved** license. Do not redistribute, modify, or republish the project without permission from the author.

## Author

**Treecs**
