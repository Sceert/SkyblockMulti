# The Ascension Nexus - wrapper persistente.
# La estructura física se genera una sola vez por mundo.
execute in minecraft:overworld unless entity @e[type=minecraft:marker,tag=skyblock_nexus_built_v1,limit=1] run function skyblock:hub/build_once
