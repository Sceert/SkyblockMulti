# Changelog

All notable SkyblockMulti milestones are documented here.

## [0.2.0-beta] — In development

World Foundation phase.

### Development focus

- New Last Refuge / HUB design
- Protected glass dome
- Fortress below the HUB
- End portal structural progression
- Initial End Portal Eye configuration: 0%, 25%, 75%
- World/lore integration
- Future lateral dungeon access to the fortress

### Maintenance

- Version metadata is now intended to use `gradle.properties` as the single version source.

> Features listed under this version are development targets and should only be moved into "Added" once implemented and tested.

## [0.1.1-beta] — Multiplayer foundation checkpoint

### Added

- Optional Open Parties and Claims integration
- Shared party-owner islands
- Personal `OWN` island and current `ACTIVE` island separation
- Party join/leave lifecycle handling
- Full reset flow for party members that never owned a personal island
- Bed/respawn handling across party transitions
- Circular 8/16/24 island layouts
- Two-ring 24-island layout
- Difficulty-based inner/outer slot priority
- Persistent per-world geometry lock
- Initial lore book
- Secret End advancement tied to preserving the official lore book
- `/skyblockmulti debug` diagnostic snapshot
- Expanded English and Spanish localization

### Fixed

- Repeated party-member teleportation
- Incorrect island reassignment after leaving a party
- Party members with personal islands now return to their original island
- Party-context bed spawn points are invalidated correctly
- Secret lore-book End advancement detection

### Tested

- Minecraft Java 26.2
- Fabric Loader 0.19.3
- Java 25
- Open Parties and Claims 0.29.3
