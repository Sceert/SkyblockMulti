
# HUB permanente en el Overworld vacío.
execute in minecraft:overworld run forceload add -1 -1 1 1
execute in minecraft:overworld run fill -12 90 -12 12 110 12 minecraft:air
execute in minecraft:overworld run fill -8 -64 0 8 -50 18 minecraft:air
execute in minecraft:overworld run fill -1 100 -1 1 100 1 minecraft:bedrock
# Portal del End en la altura mínima práctica: soporte Y=-64 y marcos Y=-63.
execute in minecraft:overworld run fill -4 -64 3 4 -64 14 minecraft:bedrock
execute in minecraft:overworld run fill -1 -63 8 1 -63 8 minecraft:end_portal_frame[facing=south,eye=false]
execute in minecraft:overworld run fill -1 -63 12 1 -63 12 minecraft:end_portal_frame[facing=north,eye=false]
execute in minecraft:overworld run fill -2 -63 9 -2 -63 11 minecraft:end_portal_frame[facing=east,eye=false]
execute in minecraft:overworld run fill 2 -63 9 2 -63 11 minecraft:end_portal_frame[facing=west,eye=false]
execute in minecraft:overworld run fill -1 -63 9 1 -63 11 minecraft:air
