scoreboard players set @s sb_home 0
execute store result storage skyblockmulti:runtime respawn.x int 1 run scoreboard players get @s sb_active_x
execute store result storage skyblockmulti:runtime respawn.z int 1 run scoreboard players get @s sb_active_z
function skyblockmulti:player/home_position with storage skyblockmulti:runtime respawn
