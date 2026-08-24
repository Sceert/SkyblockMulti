# Cinco segundos después del aviso: limpiar el recinto y cerrar el spawn natural.
scoreboard players set #spawn_lock sb_infernal -1
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:item,distance=..64]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:piglin,distance=..64,tag=!skyblock_infernal_enemy]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:piglin_brute,distance=..64,tag=!skyblock_infernal_enemy]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:zombified_piglin,distance=..64,tag=!skyblock_infernal_enemy]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:wither_skeleton,distance=..64,tag=!skyblock_infernal_enemy]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:blaze,distance=..64,tag=!skyblock_infernal_enemy]
# Se eliminan todos, incluidos los pequeños que hayan heredado la etiqueta de
# una prueba anterior. Las dos pasadas posteriores capturan sus divisiones.
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:magma_cube,distance=..64]
schedule function skyblock:infernal_trial/cleanup_magma_cubes 1t replace
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:ghast,distance=..64,tag=!skyblock_infernal_enemy]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:enderman,distance=..64,tag=!skyblock_infernal_enemy]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:hoglin,distance=..64,tag=!skyblock_infernal_enemy]
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:zoglin,distance=..64,tag=!skyblock_infernal_enemy]
# Algunos drops se crean al terminar el tick de muerte. Limpiar ahora y repetir
# dos ticks después evita que sobrevivan objetos de los mobs retirados.
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:item,distance=..64]
schedule function skyblock:infernal_trial/cleanup_drops 2t replace
execute in minecraft:the_nether positioned 0 64 0 run tellraw @a[distance=..64] {translate:"skyblockmulti.infernal_trial.arena_sealed",color:"red",bold:true}
