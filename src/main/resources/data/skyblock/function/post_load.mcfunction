execute in minecraft:overworld run kill @e[type=minecraft:text_display,tag=skyblock_island_label]
# Configuración general y respaldo de posiciones.
# Valores seguros para el menú. Java los sobrescribe con config/skyblockmulti.json al iniciar el servidor.
execute unless score #capacity sb3_cfg matches 1.. run scoreboard players set #capacity sb3_cfg 24
execute unless score #enabled_count sb3_cfg matches 1.. run scoreboard players set #enabled_count sb3_cfg 11
execute unless score #distance sb3_const matches 1.. run scoreboard players set #distance sb3_const 2048
execute unless score #bonus_tier sb3_cfg matches 0..3 run scoreboard players set #bonus_tier sb3_cfg 2
execute unless score #adv_timer sb_adv_timer matches 0.. run scoreboard players set #adv_timer sb_adv_timer 0
execute unless score #adv_phase sb_adv_timer matches 0..1 run scoreboard players set #adv_phase sb_adv_timer 0
execute unless score #tree_oak sb3_cfg matches 0..1 run scoreboard players set #tree_oak sb3_cfg 1
execute unless score #tree_spruce sb3_cfg matches 0..1 run scoreboard players set #tree_spruce sb3_cfg 1
execute unless score #tree_birch sb3_cfg matches 0..1 run scoreboard players set #tree_birch sb3_cfg 1
execute unless score #tree_jungle sb3_cfg matches 0..1 run scoreboard players set #tree_jungle sb3_cfg 1
execute unless score #tree_acacia sb3_cfg matches 0..1 run scoreboard players set #tree_acacia sb3_cfg 1
execute unless score #tree_cherry sb3_cfg matches 0..1 run scoreboard players set #tree_cherry sb3_cfg 1
execute unless score #tree_mangrove sb3_cfg matches 0..1 run scoreboard players set #tree_mangrove sb3_cfg 1
execute unless score #tree_dark_oak sb3_cfg matches 0..1 run scoreboard players set #tree_dark_oak sb3_cfg 1
execute unless score #tree_pale_oak sb3_cfg matches 0..1 run scoreboard players set #tree_pale_oak sb3_cfg 1
execute unless score #tree_azalea sb3_cfg matches 0..1 run scoreboard players set #tree_azalea sb3_cfg 1
execute unless score #tree_flowering_azalea sb3_cfg matches 0..1 run scoreboard players set #tree_flowering_azalea sb3_cfg 1
tag @a[scores={sb3_state=1}] remove skyblock_menu_shown_v1
scoreboard players set #max sb3_const 24
scoreboard players set #claim sb3_const 0
execute unless data storage skyblock:slots s01.x run function skyblock:slots/default_storage
execute unless entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] run function skyblock:slots/forceload
execute unless entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] unless score #slotgen sb3_const matches 1.. run scoreboard players set #slotgen sb3_const 100
execute in minecraft:overworld run setworldspawn 0 101 0
execute in minecraft:overworld run gamerule respawn_radius 0
function skyblock:hub/build
execute in minecraft:overworld unless entity @e[type=minecraft:marker,tag=skyblock_system_v1,limit=1] positioned 0 100 0 run summon minecraft:marker ~ ~ ~ {Tags:["skyblock_system_v1"]}
execute if entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] run function skyblock:slots/forceload
execute if entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] run function skyblock:slots/sync_occupancy
execute if entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] run function skyblock:slots/remove_forceload
tellraw @a [{"text":"[SkyblockMulti] ","color":"aqua","bold":true},{"text":"SkyblockMulti 0.1.1-beta cargado.","color":"gray"}]

# Limpiar bloqueos residuales de versiones anteriores.
execute as @a[scores={sb3_state=2},tag=skyblock_selection_locked] run function skyblock:player/unlock_selection
