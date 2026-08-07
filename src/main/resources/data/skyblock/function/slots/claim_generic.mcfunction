# Macro de asignación para una posición definida en storage skyblock:slots.
$execute in minecraft:overworld unless block $(x) 64 $(z) minecraft:bedrock run scoreboard players set #$(key) sb3_used 1
$execute in minecraft:overworld if block $(x) 64 $(z) minecraft:bedrock unless block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) run function skyblock:island/base_from_anchor
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) run function skyblock:island/starter_chest
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 1 run function skyblock:island/tree/oak
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 2 run function skyblock:island/tree/spruce
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 3 run function skyblock:island/tree/birch
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 4 run function skyblock:island/tree/jungle
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 5 run function skyblock:island/tree/acacia
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 6 run function skyblock:island/tree/cherry
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 7 run function skyblock:island/tree/mangrove
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 8 run function skyblock:island/tree/dark_oak
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 9 run function skyblock:island/tree/pale_oak
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 10 run function skyblock:island/tree/azalea
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if score @s sb_tree matches 11 run function skyblock:island/tree/flowering_azalea
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if block ~1 ~ ~ minecraft:grass_block run scoreboard players set #$(key) sb3_used 1
$execute in minecraft:overworld if block $(x) 63 $(z) minecraft:bedrock positioned $(x) 64 $(z) if block ~1 ~ ~ minecraft:grass_block run scoreboard players set #claim sb3_const 1
$execute if score #claim sb3_const matches 1 run scoreboard players set @s sb3_slot $(slot)
$execute if score #claim sb3_const matches 1 run scoreboard players set @s sb3_x $(x)
$execute if score #claim sb3_const matches 1 run scoreboard players set @s sb3_z $(z)
$execute if score #claim sb3_const matches 1 in minecraft:overworld run spawnpoint @s $(x) 65 $(z)
$execute if score #claim sb3_const matches 1 in minecraft:overworld positioned $(x) 65 $(z) run teleport @s ~0.5 ~ ~0.5
$execute if score #claim sb3_const matches 1 in minecraft:overworld run forceload remove $(x) $(z)
execute if score #claim sb3_const matches 1 run scoreboard players add @s sb_deaths 0
execute if score #claim sb3_const matches 1 run scoreboard players operation @s sb_last_deaths = @s sb_deaths
execute if score #claim sb3_const matches 1 run scoreboard players set @s sb_respawn_delay 0
execute if score #claim sb3_const matches 1 run tag @s add skyblock_respawn_v2
tag @s add skyblock_respawn_handled
execute if score #claim sb3_const matches 1 run tag @s remove skyblock_respawn_pending
execute if score #claim sb3_const matches 1 run scoreboard players set @s sb_tree 0
execute if score #claim sb3_const matches 1 run scoreboard players set @s sb_difficulty 0
execute if score #claim sb3_const matches 1 run scoreboard players set @s sb_menu 0
execute if score #claim sb3_const matches 1 run tag @s remove skyblock_menu_shown_v1
execute if score #claim sb3_const matches 1 run scoreboard players set @s sb3_state 2
execute if score #claim sb3_const matches 1 run function skyblock:player/unlock_selection
$execute if score #claim sb3_const matches 1 in minecraft:overworld positioned $(x) 67 $(z) run kill @e[type=minecraft:text_display,tag=skyblock_island_label,distance=..2]
execute if score #claim sb3_const matches 1 run advancement grant @s only skyblockmulti:progress/first_island
execute if score #claim sb3_const matches 1 run tellraw @s [{"text":"[SkyblockMulti] ","color":"aqua","bold":true},{"text":"Tu ancla fue sustituida por una isla en Y=64.","color":"green"}]
execute if score #claim sb3_const matches 1 run tellraw @s [{"text":"Usa ","color":"gray"},{"text":"/trigger sb_hub","color":"yellow"},{"text":" para ir al HUB, ","color":"gray"},{"text":"/trigger sb_home","color":"yellow"},{"text":" para volver y ","color":"gray"},{"text":"/trigger sb_info","color":"yellow"},{"text":" para consultar tu asignación.","color":"gray"}]
