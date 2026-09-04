# Segunda pasada: elimina las divisiones creadas por la limpieza inicial.
execute in minecraft:the_nether positioned 0 64 0 run kill @e[type=minecraft:magma_cube,distance=..64]
schedule function skyblockmulti:infernal_trial/cleanup_magma_cubes_final 1t replace
