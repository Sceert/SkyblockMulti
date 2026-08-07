scoreboard players set @s sb_info 0
tellraw @s [{"text":"=== SKYBLOCKMULTI ===","color":"aqua","bold":true}]
tellraw @s [{"text":"Estado: ","color":"gray"},{"score":{"name":"@s","objective":"sb3_state"},"color":"white"},{"text":" | Isla: ","color":"gray"},{"score":{"name":"@s","objective":"sb3_slot"},"color":"white"}]
tellraw @s [{"text":"Centro X: ","color":"gray"},{"score":{"name":"@s","objective":"sb3_x"},"color":"white"},{"text":" | Z: ","color":"gray"},{"score":{"name":"@s","objective":"sb3_z"},"color":"white"}]
tellraw @s [{"text":"Distancia configurada: ","color":"gray"},{"score":{"name":"#distance","objective":"sb3_const"},"color":"white"},{"text":" bloques","color":"gray"}]
tellraw @s [{"text":"Capacidad máxima: ","color":"gray"},{"score":{"name":"#capacity","objective":"sb3_cfg"},"color":"white"},{"text":" jugadores | Árboles habilitados: ","color":"gray"},{"score":{"name":"#enabled_count","objective":"sb3_cfg"},"color":"white"}]
tellraw @s [{"text":"Comandos: ","color":"gray"},{"text":"/trigger sb_hub","color":"yellow"},{"text":" · ","color":"dark_gray"},{"text":"/trigger sb_home","color":"yellow"},{"text":" · ","color":"dark_gray"},{"text":"/trigger sb_info","color":"yellow"}]
