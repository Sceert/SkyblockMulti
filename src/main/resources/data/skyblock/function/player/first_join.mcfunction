tag @s add skyblock_registered_v1
scoreboard players set @s sb3_state 1
scoreboard players set @s sb3_slot 0
scoreboard players set @s sb3_x 0
scoreboard players set @s sb3_z 0
scoreboard players set @s sb_tree 0
scoreboard players set @s sb_difficulty 0
scoreboard players set @s sb_chest -1
scoreboard players set @s sb_menu 0
scoreboard players set @s sb_home 0
scoreboard players set @s sb_hub 0
scoreboard players set @s sb_info 0
scoreboard players add @s sb_deaths 0
scoreboard players add @s sb_since_death 0
scoreboard players set @s sb_choice_time 0
scoreboard players set @s sb_last_deaths 0
scoreboard players set @s sb_respawn_delay 0
tag @s remove skyblock_respawn_pending
execute in minecraft:overworld run teleport @s 0.5 101 0.5
execute in minecraft:overworld run spawnpoint @s 0 101 0
tellraw @s [{"text":"[Skyblock] ","color":"aqua","bold":true},{"translate":"skyblockmulti.welcome","color":"yellow"}]
