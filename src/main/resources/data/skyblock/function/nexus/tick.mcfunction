# Refill an empty well only after receiving one crafted Lava Catalyst.
execute in minecraft:overworld unless block 0 15 -66 minecraft:lava if items block 0 16 -69 container.* minecraft:fire_charge[minecraft:custom_data~{skyblockmulti:{lava_catalyst:1b}}] run function skyblock:nexus/refill_lava/north
execute in minecraft:overworld unless block 66 15 0 minecraft:lava if items block 69 16 0 container.* minecraft:fire_charge[minecraft:custom_data~{skyblockmulti:{lava_catalyst:1b}}] run function skyblock:nexus/refill_lava/east
execute in minecraft:overworld unless block 0 15 66 minecraft:lava if items block 0 16 69 container.* minecraft:fire_charge[minecraft:custom_data~{skyblockmulti:{lava_catalyst:1b}}] run function skyblock:nexus/refill_lava/south
execute in minecraft:overworld unless block -66 15 0 minecraft:lava if items block -69 16 0 container.* minecraft:fire_charge[minecraft:custom_data~{skyblockmulti:{lava_catalyst:1b}}] run function skyblock:nexus/refill_lava/west

# Cardinal armor seals use actual armor stands.
execute in minecraft:overworld as @e[type=minecraft:armor_stand,tag=skyblock_nexus_seal_leather,limit=1] unless score #seal_leather sb_nexus matches 1 if items entity @s armor.head minecraft:leather_helmet if items entity @s armor.chest minecraft:leather_chestplate if items entity @s armor.legs minecraft:leather_leggings if items entity @s armor.feet minecraft:leather_boots run function skyblock:nexus/complete/leather
execute in minecraft:overworld as @e[type=minecraft:armor_stand,tag=skyblock_nexus_seal_gold,limit=1] unless score #seal_gold sb_nexus matches 1 if items entity @s armor.head minecraft:golden_helmet if items entity @s armor.chest minecraft:golden_chestplate if items entity @s armor.legs minecraft:golden_leggings if items entity @s armor.feet minecraft:golden_boots run function skyblock:nexus/complete/gold
execute in minecraft:overworld as @e[type=minecraft:armor_stand,tag=skyblock_nexus_seal_iron,limit=1] unless score #seal_iron sb_nexus matches 1 if items entity @s armor.head minecraft:iron_helmet if items entity @s armor.chest minecraft:iron_chestplate if items entity @s armor.legs minecraft:iron_leggings if items entity @s armor.feet minecraft:iron_boots run function skyblock:nexus/complete/iron
execute in minecraft:overworld as @e[type=minecraft:armor_stand,tag=skyblock_nexus_seal_diamond,limit=1] unless score #seal_diamond sb_nexus matches 1 if items entity @s armor.head minecraft:diamond_helmet if items entity @s armor.chest minecraft:diamond_chestplate if items entity @s armor.legs minecraft:diamond_leggings if items entity @s armor.feet minecraft:diamond_boots run function skyblock:nexus/complete/diamond

# Intercardinal crafted offerings.
execute in minecraft:overworld unless score #seal_earth sb_nexus matches 1 if items block 18 16 -18 container.* minecraft:echo_shard[minecraft:custom_data~{skyblockmulti:{offering:"earth"}}] run function skyblock:nexus/complete/earth
execute in minecraft:overworld unless score #seal_trees sb_nexus matches 1 if items block 18 16 18 container.* minecraft:echo_shard[minecraft:custom_data~{skyblockmulti:{offering:"trees"}}] run function skyblock:nexus/complete/trees
execute in minecraft:overworld unless score #seal_metals sb_nexus matches 1 if items block -18 16 18 container.* minecraft:echo_shard[minecraft:custom_data~{skyblockmulti:{offering:"metals"}}] run function skyblock:nexus/complete/metals
execute in minecraft:overworld unless score #seal_war sb_nexus matches 1 if items block -18 16 -18 container.* minecraft:echo_shard[minecraft:custom_data~{skyblockmulti:{offering:"war"}}] run function skyblock:nexus/complete/war

# Recalculate global completion without relying on a player being online.
scoreboard players set #seal_total sb_nexus 0
execute if score #seal_leather sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_gold sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_iron sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_diamond sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_earth sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_trees sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_metals sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_war sb_nexus matches 1 run scoreboard players add #seal_total sb_nexus 1
execute if score #seal_total sb_nexus matches 8 unless score #portal_open sb_nexus matches 1 run function skyblock:nexus/open_portal_access
