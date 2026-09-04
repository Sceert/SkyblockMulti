# Crear y verificar las anclas de la geometría activa en Y=64.
# Modo 8: s01..s08. Modo 16: s01..s16. Modo 24: s01..s24 (8 interiores + 16 exteriores).
scoreboard players set #anchors sb3_const 0
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s01
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s02
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s03
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s04
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s05
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s06
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s07
function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s08
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s09
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s10
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s11
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s12
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s13
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s14
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s15
execute if score #capacity sb3_cfg matches 16.. run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s16
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s17
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s18
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s19
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s20
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s21
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s22
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s23
execute if score #capacity sb3_cfg matches 24 run function skyblockmulti:slots/create_anchor with storage skyblockmulti:slots s24

execute if score #anchors sb3_const = #capacity sb3_cfg in minecraft:overworld positioned 0 100 0 run summon minecraft:marker ~ ~ ~ {Tags:["skyblock_slots_ready_v2"]}
execute if score #anchors sb3_const = #capacity sb3_cfg run scoreboard players set #slots_built sb3_const 1
execute if score #anchors sb3_const = #capacity sb3_cfg run function skyblockmulti:slots/sync_occupancy
execute if score #anchors sb3_const = #capacity sb3_cfg run function skyblockmulti:slots/remove_forceload
execute unless score #anchors sb3_const = #capacity sb3_cfg run scoreboard players set #slotgen sb3_const 40
