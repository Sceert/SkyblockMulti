scoreboard players set @s sb_menu 0
scoreboard players enable @s sb_tree
tag @s add skyblock_menu_shown_v1
tellraw @s {"text":""}
tellraw @s {"translate":"skyblockmulti.menu.title","color":"aqua","bold":true}
tellraw @s {"translate":"skyblockmulti.menu.instruction","color":"white"}
execute if score #tree_oak sb3_cfg matches 1 run function skyblock:player/menu/tree/oak
execute if score #tree_spruce sb3_cfg matches 1 run function skyblock:player/menu/tree/spruce
execute if score #tree_birch sb3_cfg matches 1 run function skyblock:player/menu/tree/birch
execute if score #tree_jungle sb3_cfg matches 1 run function skyblock:player/menu/tree/jungle
execute if score #tree_acacia sb3_cfg matches 1 run function skyblock:player/menu/tree/acacia
execute if score #tree_cherry sb3_cfg matches 1 run function skyblock:player/menu/tree/cherry
execute if score #tree_mangrove sb3_cfg matches 1 run function skyblock:player/menu/tree/mangrove
execute if score #tree_dark_oak sb3_cfg matches 1 run function skyblock:player/menu/tree/dark_oak
execute if score #tree_pale_oak sb3_cfg matches 1 run function skyblock:player/menu/tree/pale_oak
execute if score #tree_azalea sb3_cfg matches 1 run function skyblock:player/menu/tree/azalea
execute if score #tree_flowering_azalea sb3_cfg matches 1 run function skyblock:player/menu/tree/flowering_azalea
execute if score #enabled_count sb3_cfg matches 1.. run function skyblock:player/menu/tree/random
tellraw @s {"translate":"skyblockmulti.menu.capacity","with":[{"score":{"name":"#capacity","objective":"sb3_cfg"}}],"color":"gray"}
tellraw @s {"translate":"skyblockmulti.menu.footer","color":"gray","italic":true}
tellraw @s {"translate":"skyblockmulti.menu.random_note","color":"dark_gray","italic":true}
