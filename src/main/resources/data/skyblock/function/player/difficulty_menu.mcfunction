scoreboard players set @s sb_difficulty 0
scoreboard players enable @s sb_difficulty
tellraw @s {"text":""}
tellraw @s {"translate":"skyblockmulti.difficulty.title","color":"gold","bold":true}
tellraw @s {"translate":"skyblockmulti.difficulty.instruction","color":"white"}
function skyblock:player/difficulty/lines
