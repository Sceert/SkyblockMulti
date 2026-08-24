scoreboard players set #trial_state sb_infernal 2
scoreboard players set #champion_spawned sb_infernal 1
scoreboard players set #phase sb_infernal 2
function skyblock:infernal_trial/spawn_phase_2
execute if score #trial_level sb_infernal matches 10 in minecraft:the_nether run summon minecraft:piglin_brute 0 76 -18 {Tags:["skyblock_infernal_enemy","skyblock_infernal_champion"],PersistenceRequired:1b,IsImmuneToZombification:1b}
execute in minecraft:the_nether run effect give @e[type=minecraft:piglin_brute,tag=skyblock_infernal_champion,limit=1] minecraft:resistance 4 4 true
execute in minecraft:the_nether run effect give @e[type=minecraft:piglin_brute,tag=skyblock_infernal_champion,limit=1] minecraft:slow_falling 4 0 true
execute if score #trial_level sb_infernal matches 10 in minecraft:the_nether positioned 0 76 -18 run particle minecraft:soul_fire_flame ~ ~ ~ 2 2 2 0.08 160 force
execute in minecraft:the_nether positioned 0 64 0 run playsound minecraft:entity.piglin_brute.angry master @a[distance=..64] ~ ~ ~ 2.0 0.6
execute in minecraft:the_nether store result score #wave_total sb_infernal run execute if entity @e[tag=skyblock_infernal_enemy]
execute store result bossbar skyblock:infernal_trial max run scoreboard players get #wave_total sb_infernal
