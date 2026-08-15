data remove block -69 16 0 Items[{id:"minecraft:fire_charge",components:{"minecraft:custom_data":{skyblockmulti:{lava_catalyst:1b}}}}]
setblock -66 15 0 minecraft:lava[level=0]
particle minecraft:flame -65.5 16 0.5 1 1 1 0.05 50 force
playsound minecraft:item.firecharge.use master @a -66 16 0 1 0.7
