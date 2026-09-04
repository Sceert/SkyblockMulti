# Changelog

## 0.2.3-beta — Infernal Trials and Datapack API

This beta checkpoint adds the first shared combat activity to Skyblock Multi while preserving the existing Ascension Nexus and island progression.

### Added

- A persistent Infernal Coliseum in the void Nether.
- Six craftable Infernal Cores and secret advanced tiers VII–X.
- Progressive waves that scale with trial tier, selected island difficulty and nearby participants.
- Two phases for tiers VII–IX, with a dedicated third champion phase at tier X.
- Raid-style remaining-enemy display, start countdowns, cooldown feedback and arena lore.
- Personal reward delivery with a reliquary fallback for returning participants.
- Controlled Ancient Debris rewards, golden apple bonuses and renewable Nether materials.
- Temporary natural-spawn suppression inside the coliseum during active trials.
- A validated datapack API for custom Infernal Trials, phases, enemies, equipment,
  scaling, timing, arena hooks and custom catalyst items.
- Distinct 2D artifact sprites for the four Nexus offerings and six Infernal Cores.

### Improved

- Infernal enemies are distributed between the arena floor and upper galleries.
- Third-floor spawn positions now align with the walkable floor.
- Trial cleanup removes unrelated mobs, dropped items and every Magma Cube division.
- Split Magma Cubes remain part of the active wave and enemy counter.
- Infernal Cores are fire resistant and require a nearby living player to activate the altar.
- The arena automatically resets abandoned encounters and delays natural-spawn restoration after victory.
- The coliseum template includes the author's missing second-level floor segment.

### Fixed

- Prevented cores from being consumed by the altar while it is on cooldown.
- Prevented death-dropped cores from starting a trial without a nearby living player.
- Removed the countdown's redundant zero before the trial-start announcement.
- Corrected upper-floor mobs taking fall damage immediately after spawning.
- Removed obsolete procedural coliseum builders now replaced by the maintained NBT template.

### Known beta scope

- Additional Infernal Trial definitions and temporary arena transformations remain
  extensibility features for datapack authors rather than built-in progression.
