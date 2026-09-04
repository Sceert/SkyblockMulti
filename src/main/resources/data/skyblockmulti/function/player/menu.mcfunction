# El catálogo y la selección se muestran exclusivamente en la pantalla visual del mod.
# Se conservan los objetivos para que el flujo histórico de asignación siga funcionando.
scoreboard players set @s sb_menu 0
scoreboard players enable @s sb_tree
tag @s add skyblock_menu_shown_v1
tellraw @s {"text":"","extra":[{"translate":"skyblockmulti.tree_screen.reopen","color":"aqua","bold":true,"click_event":{"action":"run_command","command":"skyblockmulti trees"},"hover_event":{"action":"show_text","value":{"translate":"skyblockmulti.tree_screen.reopen_hover"}}}]}
