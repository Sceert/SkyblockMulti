# Buscar aleatoriamente un slot libre en el rango 01..08, con envoltura circular.
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..1 if score #01 sb3_used matches 0 run function skyblock:slots/claim/01
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..2 if score #02 sb3_used matches 0 run function skyblock:slots/claim/02
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..3 if score #03 sb3_used matches 0 run function skyblock:slots/claim/03
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..4 if score #04 sb3_used matches 0 run function skyblock:slots/claim/04
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..5 if score #05 sb3_used matches 0 run function skyblock:slots/claim/05
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..6 if score #06 sb3_used matches 0 run function skyblock:slots/claim/06
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..7 if score #07 sb3_used matches 0 run function skyblock:slots/claim/07
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..8 if score #08 sb3_used matches 0 run function skyblock:slots/claim/08

# Envolver al comienzo del rango si no había huecos hacia adelante.
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 2.. if score #01 sb3_used matches 0 run function skyblock:slots/claim/01
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 3.. if score #02 sb3_used matches 0 run function skyblock:slots/claim/02
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 4.. if score #03 sb3_used matches 0 run function skyblock:slots/claim/03
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 5.. if score #04 sb3_used matches 0 run function skyblock:slots/claim/04
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 6.. if score #05 sb3_used matches 0 run function skyblock:slots/claim/05
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 7.. if score #06 sb3_used matches 0 run function skyblock:slots/claim/06
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 8.. if score #07 sb3_used matches 0 run function skyblock:slots/claim/07
