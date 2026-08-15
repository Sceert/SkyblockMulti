# Changelog

All notable SkyblockMulti milestones are documented here.

## [0.2.0-beta] — 2026-08-15

World Foundation beta.

### Added

- The Ascension Nexus, including the new Last Refuge glass-dome HUB and the fortress below it
- Structure-based Nexus generation with a versioned, once-per-world build marker
- Native Nexus protection that remains available without Open Parties and Claims
- Regional explosion protection while preserving normal explosions outside the Nexus
- Configurable initial End Portal Eyes: 0%, 25%, 50% or 75%, initialized once per world
- Eight world-global seals: four armor trials and four craftable offerings
- Permanent progression state, colored beacon signals and four-way portal-chamber access after completing all seals
- Renewable fortress lava wells powered by a craftable Lava Catalyst
- In-world clues for the armor trials, offerings and lava wells
- Dedicated creative inventory tab for SkyblockMulti recipes and progression items
- Imported custom HUB/dome structures and reproducible conversion/generation tools
- New English and Spanish text for every supported locale

### Changed

- Redesigned the Mod Menu layout for additional configuration controls
- Expanded and darkened the fortress and End portal chamber
- Improved HUB lighting, including reliable activation of the central copper-bulb ring
- Reworked offering receptacles to become waxed copper grates when completed

### Fixed

- Survival players can interact with intended Nexus progression blocks without gaining general build access
- TNT and other explosions no longer destroy the protected central terrain
- End portal initialization no longer re-randomizes or removes eyes placed later by players
- Portal access stairs now open from all four cardinal directions with a wider entrance

### Maintenance

- Version metadata is now intended to use `gradle.properties` as the single version source.

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
