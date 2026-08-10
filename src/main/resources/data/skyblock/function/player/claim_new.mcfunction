# Asignación aleatoria de una posición libre dentro del anillo configurado.
# La capacidad válida es 8 o 16. El número de slot deja de depender del orden de ingreso.
scoreboard players set #claim sb3_const 0
scoreboard players set #random_slot sb3_const 1
execute if score #capacity sb3_cfg matches 8 run execute store result score #random_slot sb3_const run random value 1..8
execute if score #capacity sb3_cfg matches 16 run execute store result score #random_slot sb3_const run random value 1..16
function skyblock:player/claim_random
execute if score #claim sb3_const matches 0 run function skyblock:player/server_full
