# Buscar aleatoriamente un slot libre en el rango 09..24, con envoltura circular.
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..9 if score #09 sb3_used matches 0 run function skyblock:slots/claim/09
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..10 if score #10 sb3_used matches 0 run function skyblock:slots/claim/10
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..11 if score #11 sb3_used matches 0 run function skyblock:slots/claim/11
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..12 if score #12 sb3_used matches 0 run function skyblock:slots/claim/12
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..13 if score #13 sb3_used matches 0 run function skyblock:slots/claim/13
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..14 if score #14 sb3_used matches 0 run function skyblock:slots/claim/14
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..15 if score #15 sb3_used matches 0 run function skyblock:slots/claim/15
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..16 if score #16 sb3_used matches 0 run function skyblock:slots/claim/16
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..17 if score #17 sb3_used matches 0 run function skyblock:slots/claim/17
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..18 if score #18 sb3_used matches 0 run function skyblock:slots/claim/18
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..19 if score #19 sb3_used matches 0 run function skyblock:slots/claim/19
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..20 if score #20 sb3_used matches 0 run function skyblock:slots/claim/20
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..21 if score #21 sb3_used matches 0 run function skyblock:slots/claim/21
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..22 if score #22 sb3_used matches 0 run function skyblock:slots/claim/22
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..23 if score #23 sb3_used matches 0 run function skyblock:slots/claim/23
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches ..24 if score #24 sb3_used matches 0 run function skyblock:slots/claim/24

# Envolver al comienzo del rango si no había huecos hacia adelante.
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 10.. if score #09 sb3_used matches 0 run function skyblock:slots/claim/09
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 11.. if score #10 sb3_used matches 0 run function skyblock:slots/claim/10
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 12.. if score #11 sb3_used matches 0 run function skyblock:slots/claim/11
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 13.. if score #12 sb3_used matches 0 run function skyblock:slots/claim/12
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 14.. if score #13 sb3_used matches 0 run function skyblock:slots/claim/13
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 15.. if score #14 sb3_used matches 0 run function skyblock:slots/claim/14
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 16.. if score #15 sb3_used matches 0 run function skyblock:slots/claim/15
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 17.. if score #16 sb3_used matches 0 run function skyblock:slots/claim/16
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 18.. if score #17 sb3_used matches 0 run function skyblock:slots/claim/17
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 19.. if score #18 sb3_used matches 0 run function skyblock:slots/claim/18
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 20.. if score #19 sb3_used matches 0 run function skyblock:slots/claim/19
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 21.. if score #20 sb3_used matches 0 run function skyblock:slots/claim/20
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 22.. if score #21 sb3_used matches 0 run function skyblock:slots/claim/21
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 23.. if score #22 sb3_used matches 0 run function skyblock:slots/claim/22
execute if score #claim sb3_const matches 0 if score #random_slot sb3_const matches 24.. if score #23 sb3_used matches 0 run function skyblock:slots/claim/23
