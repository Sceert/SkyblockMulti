# Crear y verificar 24 anclas configurables en Y=64.
scoreboard players set #anchors sb3_const 0
function skyblock:slots/create_anchor with storage skyblock:slots s01
function skyblock:slots/create_anchor with storage skyblock:slots s02
function skyblock:slots/create_anchor with storage skyblock:slots s03
function skyblock:slots/create_anchor with storage skyblock:slots s04
function skyblock:slots/create_anchor with storage skyblock:slots s05
function skyblock:slots/create_anchor with storage skyblock:slots s06
function skyblock:slots/create_anchor with storage skyblock:slots s07
function skyblock:slots/create_anchor with storage skyblock:slots s08
function skyblock:slots/create_anchor with storage skyblock:slots s09
function skyblock:slots/create_anchor with storage skyblock:slots s10
function skyblock:slots/create_anchor with storage skyblock:slots s11
function skyblock:slots/create_anchor with storage skyblock:slots s12
function skyblock:slots/create_anchor with storage skyblock:slots s13
function skyblock:slots/create_anchor with storage skyblock:slots s14
function skyblock:slots/create_anchor with storage skyblock:slots s15
function skyblock:slots/create_anchor with storage skyblock:slots s16
function skyblock:slots/create_anchor with storage skyblock:slots s17
function skyblock:slots/create_anchor with storage skyblock:slots s18
function skyblock:slots/create_anchor with storage skyblock:slots s19
function skyblock:slots/create_anchor with storage skyblock:slots s20
function skyblock:slots/create_anchor with storage skyblock:slots s21
function skyblock:slots/create_anchor with storage skyblock:slots s22
function skyblock:slots/create_anchor with storage skyblock:slots s23
function skyblock:slots/create_anchor with storage skyblock:slots s24
execute if score #anchors sb3_const matches 24 in minecraft:overworld positioned 0 100 0 run summon minecraft:marker ~ ~ ~ {Tags:["skyblock_slots_ready_v2"]}
execute if score #anchors sb3_const matches 24 run function skyblock:slots/sync_occupancy
execute if score #anchors sb3_const matches 24 run function skyblock:slots/remove_forceload
execute if score #anchors sb3_const matches 24 run tellraw @a [{"text":"[SkyblockMulti] ","color":"aqua","bold":true},{"text":"Las 24 anclas están disponibles en Y=64.","color":"green"}]
execute unless score #anchors sb3_const matches 24 run scoreboard players set #slotgen sb3_const 40
