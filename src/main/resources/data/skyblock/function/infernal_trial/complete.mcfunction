scoreboard players set #trial_active sb_infernal 0
scoreboard players set #trial_state sb_infernal 4
scoreboard players set #spawn_lock sb_infernal 600
scoreboard players set #cooldown sb_infernal 1200
scoreboard players set #cooldown_display_tick sb_infernal 0
scoreboard players set #wave_remaining sb_infernal 0
bossbar set skyblock:infernal_trial value 0
bossbar set skyblock:infernal_trial visible false
bossbar set skyblock:infernal_trial players
execute in minecraft:the_nether run setblock 0 65 0 minecraft:fire
execute in minecraft:the_nether run particle minecraft:soul_fire_flame 0 65 0 3 1 3 0.05 80 force
execute in minecraft:the_nether run playsound minecraft:ui.toast.challenge_complete master @a[distance=..48] 0 64 0 1.0 1.0
execute in minecraft:the_nether run tellraw @a[distance=..48] {translate:"skyblockmulti.infernal_trial.completed",color:"aqua"}
execute in minecraft:the_nether run setblock 0 64 5 minecraft:chest[facing=south,type=single,waterlogged=false]
execute in minecraft:the_nether run data merge block 0 64 5 {CustomName:{translate:"skyblockmulti.infernal_trial.reliquary",color:"gold",bold:true}}
execute in minecraft:the_nether positioned 0 64 5 run particle minecraft:trial_spawner_detection ~0.5 ~1 ~0.5 1 1 1 0.02 80 force
execute in minecraft:the_nether positioned 0 64 5 run tellraw @a[distance=..64] {translate:"skyblockmulti.infernal_trial.reliquary_ready",color:"gold",bold:true}
# Los participantes conectados reciben inmediatamente su lote personal. El
# Relicario permanece como respaldo para quien vuelva a conectarse más tarde.
execute as @a if score @s sb_infernal_run = #run_id sb_infernal run function skyblock:infernal_trial/reward/completion_bonus
execute as @a if score @s sb_infernal_run = #run_id sb_infernal unless score @s sb_infernal_claim = #run_id sb_infernal run function skyblock:infernal_trial/reward/grant
