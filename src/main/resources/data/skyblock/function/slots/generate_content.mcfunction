# Crear y verificar las anclas de la geometría activa en Y=64.
# Modo 8: s01..s08. Modo 16: s01..s16. Modo 24: s01..s24 (8 interiores + 16 exteriores).
scoreboard players set #anchors sb3_const 0
function skyblock:slots/create_anchor with storage skyblock:slots s01
function skyblock:slots/create_anchor with storage skyblock:slots s02
function skyblock:slots/create_anchor with storage skyblock:slots s03
function skyblock:slots/create_anchor with storage skyblock:slots s04
function skyblock:slots/create_anchor with storage skyblock:slots s05
function skyblock:slots/create_anchor with storage skyblock:slots s06
function skyblock:slots/create_anchor with storage skyblock:slots s07
function skyblock:slots/create_anchor with storage skyblock:slots s08
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s09
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s10
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s11
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s12
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s13
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s14
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s15
execute if score #capacity sb3_cfg matches 16.. run function skyblock:slots/create_anchor with storage skyblock:slots s16
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s17
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s18
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s19
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s20
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s21
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s22
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s23
execute if score #capacity sb3_cfg matches 24 run function skyblock:slots/create_anchor with storage skyblock:slots s24

execute if score #anchors sb3_const = #capacity sb3_cfg in minecraft:overworld positioned 0 100 0 run summon minecraft:marker ~ ~ ~ {Tags:["skyblock_slots_ready_v2"]}
execute if score #anchors sb3_const = #capacity sb3_cfg run function skyblock:slots/sync_occupancy
execute if score #anchors sb3_const = #capacity sb3_cfg run function skyblock:slots/remove_forceload
execute if score #anchors sb3_const = #capacity sb3_cfg run tellraw @a [{"text":"[Skyblock Multi] ","color":"aqua","bold":true},{"translate":"skyblockmulti.system.anchors_ready","with":[{"score":{"name":"#capacity","objective":"sb3_cfg"}},{"score":{"name":"#distance","objective":"sb3_const"}}],"color":"green"}]
execute unless score #anchors sb3_const = #capacity sb3_cfg run scoreboard players set #slotgen sb3_const 40
