# Changelog

All notable Skyblock Multi milestones are documented here.

## [0.2.1-beta] — 2026-08-16

Ascension Nexus visual and progression revision.

### Added

- Optional JEI integration for discovering the four offerings and renewable lava catalyst recipes
- Narrative clue books for all four armor trials and four crafted offerings
- Cardinal armor displays that preserve completed equipment on Steve mannequins
- Open-topped transparent glass-pane reliquaries around completed armor guardians
- A fortress decoration guide with protected functional coordinates and a WorldEdit schematic workflow

### Changed

- Unified the public project name as Skyblock Multi across the mod metadata, documentation, translations and build artifacts
- Refined the imported Last Refuge dome, its lighting, wooden ribs, observation tube and central landscaping
- Expanded the lower fortress with eight dungeon approaches, renewable lava wells and a stronger End portal chamber
- Repositioned cardinal clue lecterns in front of their armor stands and aligned both with their outward direction
- Reworked clue writing to provide discoverable hints without directly listing exact offering recipes
- Prepared fortress generation so future hand-built decoration can remain separate from progression logic
- Simplified the first-join menu by removing redundant capacity, tree-renewability, random-selection and OpenPAC notices

### Fixed

- Corrected written-book data for Minecraft 26.2 lecterns
- Corrected visible line breaks in Spanish clue books
- Corrected cardinal armor-stand orientation
- Prevented completed armor sets from being dropped or duplicated during their guardian transformation

### Maintenance

- Removed temporary world-repair functions used during the development iteration
- Removed redundant per-tick visual maintenance and unused localization entries
- Verified datapack loading and the complete armor-seal transformation in a Minecraft 26.2 development server

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
- Dedicated creative inventory tab for Skyblock Multi recipes and progression items
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
