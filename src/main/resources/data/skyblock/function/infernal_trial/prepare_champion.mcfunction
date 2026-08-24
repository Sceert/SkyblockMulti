scoreboard players set #trial_state sb_infernal 3
scoreboard players set #champion_countdown sb_infernal 160
execute as @a if score @s sb_infernal_run = #run_id sb_infernal run function skyblock:infernal_trial/reward/phase_bonus
execute in minecraft:the_nether positioned 0 64 0 run tellraw @a[distance=..64] [{translate:"skyblockmulti.infernal_trial.phase_complete",color:"gold",bold:true},{text:" 1/2"}]
execute if score #trial_level sb_infernal matches 10 in minecraft:the_nether positioned 0 64 0 run tellraw @a[distance=..64] {translate:"skyblockmulti.infernal_trial.champion_warning",color:"dark_red",bold:true}
execute in minecraft:the_nether positioned 0 64 0 run playsound minecraft:entity.warden.sonic_charge master @a[distance=..64] ~ ~ ~ 1.0 0.7
