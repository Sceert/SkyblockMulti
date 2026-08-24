# Esperar hasta que el centro del Nether esté realmente cargado.
execute in minecraft:the_nether if loaded 0 64 0 run function skyblock:infernal_trial/build_structure
execute unless score #arena_version sb_infernal matches 6 run schedule function skyblock:infernal_trial/build_loaded 20t replace
