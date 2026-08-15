item replace entity @s armor.head with minecraft:air
item replace entity @s armor.chest with minecraft:air
item replace entity @s armor.legs with minecraft:air
item replace entity @s armor.feet with minecraft:air
kill @s
setblock -25 16 0 minecraft:waxed_copper_grate
scoreboard players set #seal_diamond sb_nexus 1
particle minecraft:happy_villager -24.5 17 0.5 0.8 1.0 0.8 0.1 40 force
playsound minecraft:block.beacon.activate master @a -25 16 0 0.8 1.1
