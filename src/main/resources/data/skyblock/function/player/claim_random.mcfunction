# Buscar desde un punto aleatorio del anillo y continuar en sentido circular hasta encontrar un slot libre.
# Esto evita que Jugador 1=Slot 1, Jugador 2=Slot 2, etc., sin usar recursión.

# Fase 1: desde #random_slot hasta el final del anillo.
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..1 if score #capacity sb3_cfg matches 1.. if score #01 sb3_used matches 0 run function skyblock:slots/claim/01
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..2 if score #capacity sb3_cfg matches 2.. if score #02 sb3_used matches 0 run function skyblock:slots/claim/02
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..3 if score #capacity sb3_cfg matches 3.. if score #03 sb3_used matches 0 run function skyblock:slots/claim/03
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..4 if score #capacity sb3_cfg matches 4.. if score #04 sb3_used matches 0 run function skyblock:slots/claim/04
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..5 if score #capacity sb3_cfg matches 5.. if score #05 sb3_used matches 0 run function skyblock:slots/claim/05
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..6 if score #capacity sb3_cfg matches 6.. if score #06 sb3_used matches 0 run function skyblock:slots/claim/06
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..7 if score #capacity sb3_cfg matches 7.. if score #07 sb3_used matches 0 run function skyblock:slots/claim/07
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..8 if score #capacity sb3_cfg matches 8.. if score #08 sb3_used matches 0 run function skyblock:slots/claim/08
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..9 if score #capacity sb3_cfg matches 9.. if score #09 sb3_used matches 0 run function skyblock:slots/claim/09
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..10 if score #capacity sb3_cfg matches 10.. if score #10 sb3_used matches 0 run function skyblock:slots/claim/10
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..11 if score #capacity sb3_cfg matches 11.. if score #11 sb3_used matches 0 run function skyblock:slots/claim/11
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..12 if score #capacity sb3_cfg matches 12.. if score #12 sb3_used matches 0 run function skyblock:slots/claim/12
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..13 if score #capacity sb3_cfg matches 13.. if score #13 sb3_used matches 0 run function skyblock:slots/claim/13
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..14 if score #capacity sb3_cfg matches 14.. if score #14 sb3_used matches 0 run function skyblock:slots/claim/14
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..15 if score #capacity sb3_cfg matches 15.. if score #15 sb3_used matches 0 run function skyblock:slots/claim/15
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..16 if score #capacity sb3_cfg matches 16.. if score #16 sb3_used matches 0 run function skyblock:slots/claim/16

# Fase 2: si no había espacio hacia adelante, envolver al inicio del anillo.
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 2.. if score #capacity sb3_cfg matches 1.. if score #01 sb3_used matches 0 run function skyblock:slots/claim/01
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 3.. if score #capacity sb3_cfg matches 2.. if score #02 sb3_used matches 0 run function skyblock:slots/claim/02
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 4.. if score #capacity sb3_cfg matches 3.. if score #03 sb3_used matches 0 run function skyblock:slots/claim/03
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 5.. if score #capacity sb3_cfg matches 4.. if score #04 sb3_used matches 0 run function skyblock:slots/claim/04
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 6.. if score #capacity sb3_cfg matches 5.. if score #05 sb3_used matches 0 run function skyblock:slots/claim/05
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 7.. if score #capacity sb3_cfg matches 6.. if score #06 sb3_used matches 0 run function skyblock:slots/claim/06
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 8.. if score #capacity sb3_cfg matches 7.. if score #07 sb3_used matches 0 run function skyblock:slots/claim/07
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 9.. if score #capacity sb3_cfg matches 8.. if score #08 sb3_used matches 0 run function skyblock:slots/claim/08
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 10.. if score #capacity sb3_cfg matches 9.. if score #09 sb3_used matches 0 run function skyblock:slots/claim/09
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 11.. if score #capacity sb3_cfg matches 10.. if score #10 sb3_used matches 0 run function skyblock:slots/claim/10
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 12.. if score #capacity sb3_cfg matches 11.. if score #11 sb3_used matches 0 run function skyblock:slots/claim/11
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 13.. if score #capacity sb3_cfg matches 12.. if score #12 sb3_used matches 0 run function skyblock:slots/claim/12
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 14.. if score #capacity sb3_cfg matches 13.. if score #13 sb3_used matches 0 run function skyblock:slots/claim/13
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 15.. if score #capacity sb3_cfg matches 14.. if score #14 sb3_used matches 0 run function skyblock:slots/claim/14
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 16.. if score #capacity sb3_cfg matches 15.. if score #15 sb3_used matches 0 run function skyblock:slots/claim/15
