scoreboard players set @s sb_home 0
execute store result storage skyblock:runtime respawn.x int 1 run scoreboard players get @s sb3_x
execute store result storage skyblock:runtime respawn.z int 1 run scoreboard players get @s sb3_z
function skyblock:player/home_position with storage skyblock:runtime respawn
