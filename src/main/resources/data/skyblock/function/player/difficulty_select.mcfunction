scoreboard players set @s sb_chest -1
execute if score @s sb_difficulty matches 1 run scoreboard players set @s sb_chest 0
execute if score @s sb_difficulty matches 2 run scoreboard players set @s sb_chest 1
execute if score @s sb_difficulty matches 3 run scoreboard players set @s sb_chest 2
execute if score @s sb_difficulty matches 4 run scoreboard players set @s sb_chest 3
# Validación real en servidor: nunca aceptar una opción más fácil que el mínimo configurado.
scoreboard players set #difficulty_allowed sb3_const 0
execute if score @s sb_chest matches 0..3 if score @s sb_chest <= #bonus_tier sb3_cfg run scoreboard players set #difficulty_allowed sb3_const 1
execute if score #difficulty_allowed sb3_const matches 0 run function skyblock:player/difficulty_blocked
execute if score #difficulty_allowed sb3_const matches 1 run advancement grant @s only skyblockmulti:progress/choose_difficulty
execute if score #difficulty_allowed sb3_const matches 1 if score @s sb_chest matches 0 run advancement grant @s only skyblockmulti:challenge/extreme_start
execute if score #difficulty_allowed sb3_const matches 1 if score @s sb_chest matches 1 run advancement grant @s only skyblockmulti:challenge/hard_start
execute if score #difficulty_allowed sb3_const matches 1 if score @s sb_chest matches 2 run advancement grant @s only skyblockmulti:progress/standard_start
execute if score #difficulty_allowed sb3_const matches 1 if score @s sb_chest matches 3 run advancement grant @s only skyblockmulti:progress/easy_start
execute if score #difficulty_allowed sb3_const matches 1 run scoreboard players set @s sb3_state 3
execute if score #difficulty_allowed sb3_const matches 1 unless entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] run function skyblock:player/not_ready
execute if score #difficulty_allowed sb3_const matches 1 if entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] run function skyblock:slots/sync_occupancy
execute if score #difficulty_allowed sb3_const matches 1 if entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] run function skyblock:player/claim_new
execute if score #difficulty_allowed sb3_const matches 1 run scoreboard players set @s sb_difficulty 0
