# Mantener al jugador inmovilizado en el HUB hasta completar árbol y dificultad.
execute in minecraft:overworld run teleport @s 0.5 101 0.5
effect give @s minecraft:slowness 2 255 true
effect give @s minecraft:jump_boost 2 128 true
effect give @s minecraft:mining_fatigue 2 255 true
effect give @s minecraft:resistance 2 255 true
effect give @s minecraft:slow_falling 2 0 true
tag @s add skyblock_selection_locked
