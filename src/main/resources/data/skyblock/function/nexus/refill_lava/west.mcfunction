data remove block -69 16 0 Items[{id:"skyblockmulti:lava_catalyst"}]
setblock -66 15 0 minecraft:lava[level=0]
particle minecraft:flame -65.5 16 0.5 1 1 1 0.05 50 force
playsound minecraft:item.firecharge.use master @a -66 16 0 1 0.7
