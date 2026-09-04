# Asignación aleatoria de una posición libre según la geometría configurada.
# 8/16: un solo anillo.
# 24: 8 posiciones interiores + 16 exteriores.
# En modo 24, Fácil (sb_chest=3) prioriza el anillo interior; las demás dificultades
# priorizan el exterior. Si el anillo preferido está lleno, se usa el otro para
# mantener la capacidad real de 24 islas.
scoreboard players set #claim sb3_const 0
scoreboard players set #random_slot sb3_const 1

# Modo 8.
execute if score #capacity sb3_cfg matches 8 run execute store result score #random_slot sb3_const run random value 1..8
execute if score #capacity sb3_cfg matches 8 run function skyblockmulti:player/claim_random

# Modo 16.
execute if score #capacity sb3_cfg matches 16 run execute store result score #random_slot sb3_const run random value 1..16
execute if score #capacity sb3_cfg matches 16 run function skyblockmulti:player/claim_random

# Modo 24: Fácil -> interior primero.
execute if score #capacity sb3_cfg matches 24 if score @s sb_chest matches 3 run execute store result score #random_slot sb3_const run random value 1..8
execute if score #capacity sb3_cfg matches 24 if score @s sb_chest matches 3 run function skyblockmulti:player/claim_random_inner8
execute if score #capacity sb3_cfg matches 24 if score @s sb_chest matches 3 if score #claim sb3_const matches 0 run execute store result score #random_slot sb3_const run random value 9..24
execute if score #capacity sb3_cfg matches 24 if score @s sb_chest matches 3 if score #claim sb3_const matches 0 run function skyblockmulti:player/claim_random_outer16

# Modo 24: Extremo/Difícil/Estándar -> exterior primero.
execute if score #capacity sb3_cfg matches 24 unless score @s sb_chest matches 3 run execute store result score #random_slot sb3_const run random value 9..24
execute if score #capacity sb3_cfg matches 24 unless score @s sb_chest matches 3 run function skyblockmulti:player/claim_random_outer16
execute if score #capacity sb3_cfg matches 24 unless score @s sb_chest matches 3 if score #claim sb3_const matches 0 run execute store result score #random_slot sb3_const run random value 1..8
execute if score #capacity sb3_cfg matches 24 unless score @s sb_chest matches 3 if score #claim sb3_const matches 0 run function skyblockmulti:player/claim_random_inner8

# La primera asignación fija la geometría de ESTE mundo de forma persistente.
execute if score #claim sb3_const matches 1 run scoreboard players set #geometry_locked sb3_cfg 1
execute if score #claim sb3_const matches 1 run scoreboard players operation #world_capacity sb3_cfg = #capacity sb3_cfg
execute if score #claim sb3_const matches 1 run scoreboard players operation #world_radius sb3_const = #distance sb3_const

execute if score #claim sb3_const matches 0 run function skyblockmulti:player/server_full
