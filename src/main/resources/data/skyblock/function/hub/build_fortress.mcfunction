# ============================================================
# FORTALEZA INFERIOR - prototipo estructural v1
# Huella exterior aproximada 65x65, Y=12..36.
# ============================================================

# Plataforma/fundación.
execute in minecraft:overworld run fill -32 12 -32 32 12 32 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -31 13 -31 31 13 31 minecraft:stone_bricks

# Muros exteriores.
execute in minecraft:overworld run fill -32 13 -32 -30 34 32 minecraft:deepslate_bricks
execute in minecraft:overworld run fill 30 13 -32 32 34 32 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -29 13 -32 29 34 -30 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -29 13 30 29 34 32 minecraft:deepslate_bricks

# Techo exterior y gran lucernario central.
execute in minecraft:overworld run fill -32 35 -32 32 35 32 minecraft:stone_bricks
execute in minecraft:overworld run fill -13 35 -13 13 35 13 minecraft:glass
execute in minecraft:overworld run fill -11 35 -11 11 35 11 minecraft:tinted_glass

# Cámara ceremonial central 35x35.
execute in minecraft:overworld run fill -18 14 -18 18 14 18 minecraft:polished_deepslate
execute in minecraft:overworld run fill -18 15 -18 -17 27 18 minecraft:stone_bricks
execute in minecraft:overworld run fill 17 15 -18 18 27 18 minecraft:stone_bricks
execute in minecraft:overworld run fill -16 15 -18 16 27 -17 minecraft:stone_bricks
execute in minecraft:overworld run fill -16 15 17 16 27 18 minecraft:stone_bricks

# Abrir cuatro accesos de la cámara; el sur conduce a la futura sala del portal.
execute in minecraft:overworld run fill -2 15 -18 2 20 -17 minecraft:air
execute in minecraft:overworld run fill -2 15 17 2 20 18 minecraft:air
execute in minecraft:overworld run fill -18 15 -2 -17 20 2 minecraft:air
execute in minecraft:overworld run fill 17 15 -2 18 20 2 minecraft:air

# Ocho pedestales ceremoniales (4 cardinales + 4 intercardinales).
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

# Corredores laterales sellados para futuras dungeons.
execute in minecraft:overworld run fill -29 14 -4 -19 20 4 minecraft:air
execute in minecraft:overworld run fill 19 14 -4 29 20 4 minecraft:air
execute in minecraft:overworld run fill -4 14 -29 4 20 -19 minecraft:air
execute in minecraft:overworld run fill -4 14 19 4 20 29 minecraft:air
execute in minecraft:overworld run fill -30 14 -3 -30 20 3 minecraft:reinforced_deepslate
execute in minecraft:overworld run fill 30 14 -3 30 20 3 minecraft:reinforced_deepslate
execute in minecraft:overworld run fill -3 14 -30 3 20 -30 minecraft:reinforced_deepslate
# Acceso sur queda sellado hacia la sala del Portal del End.
execute in minecraft:overworld run fill -3 14 30 3 20 30 minecraft:reinforced_deepslate

# Sala del Portal del End, al sur y ligeramente más baja.
execute in minecraft:overworld run fill -12 8 20 12 8 30 minecraft:deepslate_bricks
execute in minecraft:overworld run fill -12 9 20 -11 19 30 minecraft:stone_bricks
execute in minecraft:overworld run fill 11 9 20 12 19 30 minecraft:stone_bricks
execute in minecraft:overworld run fill -10 9 20 10 19 21 minecraft:stone_bricks
execute in minecraft:overworld run fill -10 9 29 10 19 30 minecraft:stone_bricks
execute in minecraft:overworld run fill -12 20 20 12 20 30 minecraft:stone_bricks

# Portal estándar de 12 marcos, inicialmente 0 ojos.
execute in minecraft:overworld run fill -1 9 23 1 9 23 minecraft:end_portal_frame[facing=south,eye=false]
execute in minecraft:overworld run fill -1 9 27 1 9 27 minecraft:end_portal_frame[facing=north,eye=false]
execute in minecraft:overworld run fill -2 9 24 -2 9 26 minecraft:end_portal_frame[facing=east,eye=false]
execute in minecraft:overworld run fill 2 9 24 2 9 26 minecraft:end_portal_frame[facing=west,eye=false]
execute in minecraft:overworld run fill -1 9 24 1 9 26 minecraft:air

# Barrera ceremonial entre cámara y sala del portal.
execute in minecraft:overworld run fill -4 14 18 4 22 21 minecraft:reinforced_deepslate
execute in minecraft:overworld run fill -2 16 18 2 20 18 minecraft:iron_bars

# Iluminación de la cámara para que sea visible desde el HUB.
execute in minecraft:overworld run setblock 0 16 0 minecraft:sea_lantern
execute in minecraft:overworld run setblock 14 16 14 minecraft:sea_lantern
execute in minecraft:overworld run setblock -14 16 14 minecraft:sea_lantern
execute in minecraft:overworld run setblock 14 16 -14 minecraft:sea_lantern
execute in minecraft:overworld run setblock -14 16 -14 minecraft:sea_lantern

# Detalles atmosféricos básicos.
execute in minecraft:overworld run fill -25 14 -25 -23 14 -23 minecraft:lava
execute in minecraft:overworld run fill 23 14 -25 25 14 -23 minecraft:lava
execute in minecraft:overworld run fill -25 14 23 -23 14 25 minecraft:lava
execute in minecraft:overworld run fill 23 14 23 25 14 25 minecraft:lava
