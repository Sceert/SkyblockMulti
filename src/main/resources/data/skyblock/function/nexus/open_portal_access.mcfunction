# Persist first so this transition can never be consumed twice.
scoreboard players set #portal_open sb_nexus 1

# Tear open four monumental gates in the ceremonial rotunda.
fill -4 15 -20 4 23 -16 minecraft:air
fill -4 15 16 4 23 20 minecraft:air
fill 16 15 -4 20 23 4 minecraft:air
fill -20 15 -4 -16 23 4 minecraft:air

# Clear four broad descents through the sealed floor layers.
fill -3 6 -18 3 18 -10 minecraft:air
fill -3 6 10 3 18 18 minecraft:air
fill 10 6 -3 18 18 3 minecraft:air
fill -18 6 -3 -10 18 3 minecraft:air

# Open the actual portal chamber wall; the old one-block breach was inside this ring.
fill -3 5 -12 3 11 -9 minecraft:air
fill -3 5 9 3 11 12 minecraft:air
fill 9 5 -3 12 11 3 minecraft:air
fill -12 5 -3 -9 11 3 minecraft:air

# Seal the underside and join every descent to the chamber's final floor.
fill -3 4 -12 3 4 -6 minecraft:reinforced_deepslate
fill -3 4 6 3 4 12 minecraft:reinforced_deepslate
fill 6 4 -3 12 4 3 minecraft:reinforced_deepslate
fill -12 4 -3 -6 4 3 minecraft:reinforced_deepslate
fill -3 5 -9 3 5 -6 minecraft:polished_deepslate
fill -3 5 6 3 5 9 minecraft:polished_deepslate
fill 6 5 -3 9 5 3 minecraft:polished_deepslate
fill -9 5 -3 -6 5 3 minecraft:polished_deepslate

# North and south stairs.
fill -3 14 -18 3 14 -18 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 13 -17 3 13 -17 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 12 -16 3 12 -16 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 11 -15 3 11 -15 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 10 -14 3 10 -14 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 9 -13 3 9 -13 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 8 -12 3 8 -12 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 7 -11 3 7 -11 minecraft:polished_blackstone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]
fill -3 6 -10 3 6 -10 minecraft:polished_blackstone_bricks
fill -3 14 18 3 14 18 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 13 17 3 13 17 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 12 16 3 12 16 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 11 15 3 11 15 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 10 14 3 10 14 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 9 13 3 9 13 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 8 12 3 8 12 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 7 11 3 7 11 minecraft:polished_blackstone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]
fill -3 6 10 3 6 10 minecraft:polished_blackstone_bricks

# East and west stairs.
fill 18 14 -3 18 14 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 17 13 -3 17 13 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 16 12 -3 16 12 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 15 11 -3 15 11 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 14 10 -3 14 10 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 13 9 -3 13 9 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 12 8 -3 12 8 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 11 7 -3 11 7 3 minecraft:polished_blackstone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]
fill 10 6 -3 10 6 3 minecraft:polished_blackstone_bricks
fill -18 14 -3 -18 14 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -17 13 -3 -17 13 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -16 12 -3 -16 12 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -15 11 -3 -15 11 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -14 10 -3 -14 10 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -13 9 -3 -13 9 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -12 8 -3 -12 8 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -11 7 -3 -11 7 3 minecraft:polished_blackstone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]
fill -10 6 -3 -10 6 3 minecraft:polished_blackstone_bricks

# Dark guard rails and soul lanterns frame every threshold.
fill -4 7 -17 -4 16 -10 minecraft:polished_blackstone_brick_wall
fill 4 7 -17 4 16 -10 minecraft:polished_blackstone_brick_wall
fill -4 7 10 -4 16 17 minecraft:polished_blackstone_brick_wall
fill 4 7 10 4 16 17 minecraft:polished_blackstone_brick_wall
fill 10 7 -4 17 16 -4 minecraft:polished_blackstone_brick_wall
fill 10 7 4 17 16 4 minecraft:polished_blackstone_brick_wall
fill -17 7 -4 -10 16 -4 minecraft:polished_blackstone_brick_wall
fill -17 7 4 -10 16 4 minecraft:polished_blackstone_brick_wall
setblock -4 17 -17 minecraft:soul_lantern
setblock 4 17 -17 minecraft:soul_lantern
setblock -4 17 17 minecraft:soul_lantern
setblock 4 17 17 minecraft:soul_lantern
setblock 17 17 -4 minecraft:soul_lantern
setblock 17 17 4 minecraft:soul_lantern
setblock -17 17 -4 minecraft:soul_lantern
setblock -17 17 4 minecraft:soul_lantern

# The eight inner seals awaken together as coloured ceremonial beacons.
fill 2 15 -9 4 15 -7 minecraft:netherite_block
fill 7 15 -4 9 15 -2 minecraft:netherite_block
fill 7 15 2 9 15 4 minecraft:netherite_block
fill 2 15 7 4 15 9 minecraft:netherite_block
fill -4 15 7 -2 15 9 minecraft:netherite_block
fill -9 15 2 -7 15 4 minecraft:netherite_block
fill -9 15 -4 -7 15 -2 minecraft:netherite_block
fill -4 15 -9 -2 15 -7 minecraft:netherite_block
setblock 3 16 -8 minecraft:beacon
setblock 8 16 -3 minecraft:beacon
setblock 8 16 3 minecraft:beacon
setblock 3 16 8 minecraft:beacon
setblock -3 16 8 minecraft:beacon
setblock -8 16 3 minecraft:beacon
setblock -8 16 -3 minecraft:beacon
setblock -3 16 -8 minecraft:beacon
setblock 3 17 -8 minecraft:brown_stained_glass
setblock 8 17 -3 minecraft:yellow_stained_glass
setblock 8 17 3 minecraft:white_stained_glass
setblock 3 17 8 minecraft:light_blue_stained_glass
setblock -3 17 8 minecraft:lime_stained_glass
setblock -8 17 3 minecraft:green_stained_glass
setblock -8 17 -3 minecraft:orange_stained_glass
setblock -3 17 -8 minecraft:red_stained_glass
# Tinted glass blocks vanilla beacon beams; replace only these eight roof cells.
setblock 3 35 -8 minecraft:brown_stained_glass
setblock 8 35 -3 minecraft:yellow_stained_glass
setblock 8 35 3 minecraft:white_stained_glass
setblock 3 35 8 minecraft:light_blue_stained_glass
setblock -3 35 8 minecraft:lime_stained_glass
setblock -8 35 3 minecraft:green_stained_glass
setblock -8 35 -3 minecraft:orange_stained_glass
setblock -3 35 -8 minecraft:red_stained_glass

particle minecraft:reverse_portal 0 16 0 18 5 18 0.15 1000 force
playsound minecraft:block.end_portal.spawn master @a 0 14 0 2.0 0.7
title @a title {"translate":"skyblockmulti.nexus.portal_open.title","color":"light_purple","bold":true}
title @a subtitle {"translate":"skyblockmulti.nexus.portal_open.subtitle","color":"gray"}
