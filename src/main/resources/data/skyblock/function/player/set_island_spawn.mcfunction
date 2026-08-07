# Guarda un punto de reaparición válido: la superficie está en Y=64 y los pies en Y=65.
execute store result storage skyblock:runtime respawn.x int 1 run scoreboard players get @s sb3_x
execute store result storage skyblock:runtime respawn.z int 1 run scoreboard players get @s sb3_z
function skyblock:player/set_island_spawn_position with storage skyblock:runtime respawn
