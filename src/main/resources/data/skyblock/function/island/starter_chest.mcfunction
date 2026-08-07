# Selección individual: 0 vacío, 1 básico, 2 estándar, 3 principiante.
execute if score @s sb_chest matches 1 run function skyblock:island/starter_chest/basic
execute if score @s sb_chest matches 2 run function skyblock:island/starter_chest/standard
execute if score @s sb_chest matches 3 run function skyblock:island/starter_chest/beginner
# Compatibilidad: si un jugador antiguo no tiene selección, usar la configuración global.
execute unless score @s sb_chest matches 0..3 if score #bonus_tier sb3_cfg matches 1 run function skyblock:island/starter_chest/basic
execute unless score @s sb_chest matches 0..3 if score #bonus_tier sb3_cfg matches 2 run function skyblock:island/starter_chest/standard
execute unless score @s sb_chest matches 0..3 if score #bonus_tier sb3_cfg matches 3 run function skyblock:island/starter_chest/beginner
