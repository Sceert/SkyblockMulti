# Guardia liviana: solo ejecuta SkyblockMulti cuando el preset fue detectado al cargar.
execute if score #active sb3_const matches 1 run function skyblock:tick_active
