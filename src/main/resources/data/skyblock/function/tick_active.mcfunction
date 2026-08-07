
# Respaldo de inicialización.
execute in minecraft:overworld unless entity @e[type=minecraft:marker,tag=skyblock_system_v1,limit=1] run function skyblock:bootstrap
# Garantizar el HUB.
execute in minecraft:overworld unless block 0 100 0 minecraft:bedrock run function skyblock:hub/build
# Preparar las 24 anclas después de que sus chunks estén cargados.
execute unless entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] if score #slotgen sb3_const matches 1.. run scoreboard players remove #slotgen sb3_const 1
execute unless entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] if score #slotgen sb3_const matches 0 run function skyblock:slots/generate_all
# Registrar jugadores.
execute as @a[tag=!skyblock_registered_v1] run function skyblock:player/first_join
# Recuperar una selección interrumpida (estado 3 no debe persistir entre ticks).
execute as @a[scores={sb3_state=3}] run function skyblock:player/recover_claim
# Corregir automáticamente el punto de reaparición de jugadores asignados por versiones anteriores.
execute as @a[scores={sb3_state=2},tag=!skyblock_respawn_v2] run function skyblock:player/migrate_respawn
# Restauración robusta después de morir.
# deathCount identifica cada muerte; time_since_death confirma que el jugador ya reapareció.
execute as @a[scores={sb3_state=2}] unless score @s sb_deaths = @s sb_last_deaths run function skyblock:player/death_detected
execute as @a[tag=skyblock_respawn_pending,scores={sb3_state=2,sb_since_death=1..}] run function skyblock:player/respawn_decide
# Inmovilizar obligatoriamente durante la selección inicial.
execute as @a[scores={sb3_state=1}] run function skyblock:player/freeze_selection
execute as @a[scores={sb3_state=3}] run function skyblock:player/freeze_selection
execute as @a[scores={sb3_state=4}] run function skyblock:player/freeze_selection
# Habilitar triggers.
scoreboard players enable @a[scores={sb3_state=1}] sb_tree
scoreboard players enable @a[scores={sb3_state=1}] sb_menu
scoreboard players enable @a[scores={sb3_state=4}] sb_difficulty
scoreboard players enable @a[scores={sb3_state=2}] sb_home
scoreboard players enable @a[tag=skyblock_registered_v1] sb_hub
scoreboard players enable @a[tag=skyblock_registered_v1] sb_info
# Mostrar selector solo cuando las anclas estén listas.
execute if entity @e[type=minecraft:marker,tag=skyblock_slots_ready_v2,limit=1] as @a[scores={sb3_state=1},tag=!skyblock_menu_shown_v1] run function skyblock:player/menu
# Procesar botones y comandos.
execute as @a[scores={sb_menu=1..,sb3_state=1}] run function skyblock:player/menu
execute as @a[scores={sb_tree=1..12,sb3_state=1}] run function skyblock:player/select
execute as @a[scores={sb_difficulty=1..4,sb3_state=4}] run function skyblock:player/difficulty_select
execute as @a[scores={sb_home=1..,sb3_state=2}] run function skyblock:player/home
execute as @a[scores={sb_hub=1..}] run function skyblock:player/hub
execute as @a[scores={sb_info=1..}] run function skyblock:player/info
# Limpiar valores inválidos de selección.
scoreboard players set @a[scores={sb_tree=13..}] sb_tree 0
scoreboard players set @a[scores={sb_difficulty=5..}] sb_difficulty 0

# Progreso de logros: una mitad cada 100 ticks; cada categoría se revisa cada 10 segundos.
scoreboard players add #adv_timer sb_adv_timer 1
execute if score #adv_timer sb_adv_timer matches 100.. if score #adv_phase sb_adv_timer matches 0 run function skyblock:advancements/check_group_a
execute if score #adv_timer sb_adv_timer matches 100.. if score #adv_phase sb_adv_timer matches 1 run function skyblock:advancements/check_group_b
execute if score #adv_timer sb_adv_timer matches 100.. run scoreboard players add #adv_phase sb_adv_timer 1
execute if score #adv_phase sb_adv_timer matches 2.. run scoreboard players set #adv_phase sb_adv_timer 0
execute if score #adv_timer sb_adv_timer matches 100.. run scoreboard players set #adv_timer sb_adv_timer 0
scoreboard players add @a[scores={sb3_state=1}] sb_choice_time 1
scoreboard players add @a[scores={sb3_state=4}] sb_choice_time 1
scoreboard players set @a[scores={sb3_state=2}] sb_choice_time 0
