# Teletransporte a casa sin alterar el punto de cama o ancla vanilla.
$execute in minecraft:overworld run setblock $(x) 63 $(z) minecraft:bedrock
$execute in minecraft:overworld run setblock $(x) 64 $(z) minecraft:bedrock
scoreboard players set #respawn_safe sb3_const 0
function skyblock:player/respawn_find_safe with storage skyblock:runtime respawn
execute if score #respawn_safe sb3_const matches 0 run function skyblock:player/home_emergency_clear with storage skyblock:runtime respawn
