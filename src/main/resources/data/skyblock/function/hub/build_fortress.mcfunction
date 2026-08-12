# ============================================================
# THE ASCENSION NEXUS - FORTALEZA v2
# Cámara ceremonial centrada bajo el HUB.
# Portal del End directamente bajo el eje 0,0 y visible desde arriba.
# ============================================================

# Fundación exterior.
execute in minecraft:overworld run fill -32 12 -32 32 12 32 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -31 13 -31 31 13 31 minecraft:stone_bricks

# Muros exteriores.
execute in minecraft:overworld run fill -32 13 -32 -30 34 32 minecraft:deepslate_bricks
execute in minecraft:overworld run fill 30 13 -32 32 34 32 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -29 13 -32 29 34 -30 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -29 13 30 29 34 32 minecraft:deepslate_bricks

# Techo con lucernario totalmente transparente sobre la cámara.
execute in minecraft:overworld run fill -32 35 -32 32 35 32 minecraft:stone_bricks
execute in minecraft:overworld run fill -14 35 -14 14 35 14 minecraft:glass

# Cámara ceremonial central 35x35.
execute in minecraft:overworld run fill -18 14 -18 18 14 18 minecraft:polished_deepslate
execute in minecraft:overworld run fill -18 15 -18 -17 27 18 minecraft:stone_bricks
execute in minecraft:overworld run fill 17 15 -18 18 27 18 minecraft:stone_bricks
execute in minecraft:overworld run fill -16 15 -18 16 27 -17 minecraft:stone_bricks
execute in minecraft:overworld run fill -16 15 17 16 27 18 minecraft:stone_bricks

# Accesos cardinales de la cámara.
execute in minecraft:overworld run fill -2 15 -18 2 20 -17 minecraft:air
execute in minecraft:overworld run fill -2 15 17 2 20 18 minecraft:air
execute in minecraft:overworld run fill -18 15 -2 -17 20 2 minecraft:air
execute in minecraft:overworld run fill 17 15 -2 18 20 2 minecraft:air

# Ocho pedestales ceremoniales.
execute in minecraft:overworld run fill -1 15 -13 1 15 -11 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock 0 16 -12 minecraft:polished_blackstone
execute in minecraft:overworld run fill 11 15 -1 13 15 1 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock 12 16 0 minecraft:polished_blackstone
execute in minecraft:overworld run fill -1 15 11 1 15 13 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock 0 16 12 minecraft:polished_blackstone
execute in minecraft:overworld run fill -13 15 -1 -11 15 1 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock -12 16 0 minecraft:polished_blackstone
execute in minecraft:overworld run fill 7 15 -9 9 15 -7 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock 8 16 -8 minecraft:polished_blackstone
execute in minecraft:overworld run fill 7 15 7 9 15 9 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock 8 16 8 minecraft:polished_blackstone
execute in minecraft:overworld run fill -9 15 7 -7 15 9 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock -8 16 8 minecraft:polished_blackstone
execute in minecraft:overworld run fill -9 15 -9 -7 15 -7 minecraft:chiseled_stone_bricks
execute in minecraft:overworld run setblock -8 16 -8 minecraft:polished_blackstone

# Ventana ceremonial central en el piso de la cámara.
# Permite ver el Portal del End, pero NO es un acceso.
execute in minecraft:overworld run fill -6 14 -6 6 14 6 minecraft:glass

# Cámara sellada del Portal del End bajo el centro.
execute in minecraft:overworld run fill -10 4 -10 10 4 10 minecraft:reinforced_deepslate
execute in minecraft:overworld run fill -10 5 -10 -9 13 10 minecraft:deepslate_bricks
execute in minecraft:overworld run fill 9 5 -10 10 13 10 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -8 5 -10 8 13 -9 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -8 5 9 8 13 10 minecraft:deepslate_bricks

# Portal estándar de 12 marcos, 0 ojos, centrado exactamente bajo el HUB.
execute in minecraft:overworld run fill -1 6 -2 1 6 -2 minecraft:end_portal_frame[facing=south,eye=false]
execute in minecraft:overworld run fill -1 6 2 1 6 2 minecraft:end_portal_frame[facing=north,eye=false]
execute in minecraft:overworld run fill -2 6 -1 -2 6 1 minecraft:end_portal_frame[facing=east,eye=false]
execute in minecraft:overworld run fill 2 6 -1 2 6 1 minecraft:end_portal_frame[facing=west,eye=false]
execute in minecraft:overworld run fill -1 6 -1 1 6 1 minecraft:air

# Luz alrededor del portal, evitando una lámpara en el eje central.
execute in minecraft:overworld run setblock 7 5 7 minecraft:sea_lantern
execute in minecraft:overworld run setblock -7 5 7 minecraft:sea_lantern
execute in minecraft:overworld run setblock 7 5 -7 minecraft:sea_lantern
execute in minecraft:overworld run setblock -7 5 -7 minecraft:sea_lantern

# Corredores laterales sellados para futuras dungeons.
execute in minecraft:overworld run fill -29 14 -4 -19 20 4 minecraft:air
execute in minecraft:overworld run fill 19 14 -4 29 20 4 minecraft:air
execute in minecraft:overworld run fill -4 14 -29 4 20 -19 minecraft:air
execute in minecraft:overworld run fill -4 14 19 4 20 29 minecraft:air
execute in minecraft:overworld run fill -30 14 -3 -30 20 3 minecraft:reinforced_deepslate
execute in minecraft:overworld run fill 30 14 -3 30 20 3 minecraft:reinforced_deepslate
execute in minecraft:overworld run fill -3 14 -30 3 20 -30 minecraft:reinforced_deepslate
execute in minecraft:overworld run fill -3 14 30 3 20 30 minecraft:reinforced_deepslate

# Iluminación periférica de la cámara ceremonial.
execute in minecraft:overworld run setblock 14 16 14 minecraft:sea_lantern
execute in minecraft:overworld run setblock -14 16 14 minecraft:sea_lantern
execute in minecraft:overworld run setblock 14 16 -14 minecraft:sea_lantern
execute in minecraft:overworld run setblock -14 16 -14 minecraft:sea_lantern

# Detalles atmosféricos provisionales.
execute in minecraft:overworld run fill -25 14 -25 -23 14 -23 minecraft:lava
execute in minecraft:overworld run fill 23 14 -25 25 14 -23 minecraft:lava
execute in minecraft:overworld run fill -25 14 23 -23 14 25 minecraft:lava
execute in minecraft:overworld run fill 23 14 23 25 14 25 minecraft:lava
