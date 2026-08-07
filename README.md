SkyblockMulti 1.0 — Treecs

REQUISITOS
- Minecraft Java 26.2
- Fabric Loader 0.19.3 o superior
- Fabric API para 26.2
- Mod Menu opcional para editar la configuración en el juego

CONFIGURACIÓN
El archivo config/skyblockmulti.json permite definir:
- islandDistance: separación entre islas (256–100000, múltiplos de 16).
- enabledTrees: especies visibles en el selector inicial.

ÁRBOLES DISPONIBLES
Roble, abeto, abedul, jungla, acacia, cerezo, manglar, roble oscuro, roble pálido, azalea y azalea florecida.
Todos utilizan retoño o propágulo vanilla. Roble oscuro y roble pálido requieren 4 retoños para crecer.

CAPACIDAD
La versión 1.0 reserva 24 islas para 24 jugadores. La distancia modifica la separación y la extensión del área, no el número de plazas.

IDIOMAS DEL SELECTOR
- El selector inicial usa componentes traducibles del cliente.
- Español: es_ar, es_cl, es_ec, es_es, es_mx, es_uy y es_ve.
- Inglés: en_us, en_gb, en_au, en_ca y en_nz.
- Cada jugador puede ver el menú en su propio idioma dentro del mismo servidor.
- Para ver las traducciones personalizadas, SkyblockMulti debe estar instalado también en el cliente.

ACTUALIZACIÓN DE MENÚ
- Selector de árboles con valores de respaldo para evitar menús vacíos.
- Configuración de Mod Menu en español o inglés según el idioma del juego.
- Sincronización del archivo config corregida para entornos Fabric de producción.

CORRECCIÓN DE RESPAWN
- La superficie de cada isla está en Y=64 y el punto de reaparición se fija en Y=65.
- Los jugadores de versiones anteriores son migrados automáticamente al cargar el mundo.
- Tras morir en cualquier dimensión, el mod verifica el respawn y devuelve al jugador a su isla.
- Si el bloque central fue eliminado, se restaura únicamente como soporte de emergencia.

SELECTOR CON COLORES Y OPCIÓN ALEATORIA
- Cada especie usa un color distintivo en el selector inicial.
- La opción Aleatorio/Random elige uniformemente entre las especies habilitadas.
- El servidor vuelve a validar la especie antes de crear la isla.

RESPAWN SEGURO
- La isla conserva una bedrock central visible y otra oculta debajo.
- El respawn busca un lugar libre dentro de un área 3x3 y no elimina bloques.
- Si toda el área está obstruida, aplica caída lenta sobre la isla.

COFRE BONUS CONFIGURABLE
- Puede activarse o desactivarse desde Mod Menu.
- Básico: cubo de agua y cubo de lava.
- Estándar: contenido clásico actual.
- Principiante: estándar más semillas, comida adicional y una cama.
- Los cambios se aplican a islas creadas después de reiniciar el mundo o servidor.

RESPAWN GARANTIZADO
- La bedrock central permanece visible y marca el punto de reaparición.
- Primero se busca un espacio libre en 5x5.
- Si todo está bloqueado, solo se destruyen los dos bloques encima de la bedrock central.
- Los bloques destruidos se convierten en drops; no se borran silenciosamente.
- La restauración se ejecuta una vez inmediatamente después de reaparecer.

AVISO EXPERIMENTAL
- El cliente confirma automáticamente la advertencia experimental cuando detecta el paquete interno de SkyblockMulti.
- No debería ocultar advertencias pertenecientes exclusivamente a otros paquetes.

NOTA DEL AVISO EXPERIMENTAL
- Mientras este mod esté instalado en el cliente, la pantalla estándar de confirmación experimental se acepta automáticamente.
- Esto también puede ocultar la misma advertencia al crear otros mundos experimentales.


CORRECCIONES DE ESTA COMPILACIÓN
- Una cama o ancla de reaparición válida tiene prioridad sobre la isla.
- SkyblockMulti solo recupera la isla si Minecraft envía al jugador al spawn mundial.
- /trigger sb_home ya no cambia el punto de la cama.
- El cofre inicial usa una única opción: Vacío, Básico, Estándar o Principiante.
- La configuración del cofre se aplica inmediatamente a futuras islas cuando hay un mundo abierto.
- La advertencia experimental se omite en la creación de mundos mientras el mod esté instalado.

SELECCIÓN DE DIFICULTAD POR JUGADOR
Después de elegir el árbol, cada jugador escoge Extremo, Difícil, Estándar o Fácil. Esta decisión define únicamente su cofre inicial.

SELECCIÓN DE DIFICULTAD POR JUGADOR
Después de elegir el árbol, cada jugador escoge Extremo, Difícil, Estándar o Fácil. Esta decisión define únicamente su cofre inicial.

SELECCIÓN DE DIFICULTAD POR JUGADOR
Después de elegir el árbol, cada jugador escoge Extremo, Difícil, Estándar o Fácil. Esta decisión define únicamente su cofre inicial.

SELECCIÓN OBLIGATORIA
---------------------
Los jugadores nuevos permanecen inmovilizados en el centro del HUB hasta escoger:
1. Un árbol habilitado o la opción Aleatorio.
2. Una dificultad: Extremo, Difícil, Estándar o Fácil.

Durante la selección no pueden caminar, saltar, caer ni romper bloques. La inmovilización
se elimina automáticamente después de que la isla se asigna correctamente.


PROGRESIÓN Y DIFICULTAD MÍNIMA
-------------------------------
La opción bonusChestMode se usa como dificultad mínima permitida: empty=Extremo, basic=Difícil, standard=Estándar, beginner=Fácil. El servidor valida la selección y oculta las opciones más fáciles. Se incluyen 58 avances iniciales y secretos, revisados cada 5 segundos cuando requieren inventario o contador.

NOMBRE DE ISLA
---------------
Se eliminó el rótulo flotante text_display de la isla. Los rótulos residuales de versiones anteriores se eliminan al cargar el mundo.

OPEN PARTIES AND CLAIMS
-----------------------
Se declara como mod sugerido, no obligatorio. La reclamación automática 3x3 y la vinculación de miembros requieren el módulo API compilado y una prueba con Open Parties and Claims 0.29.3; no se ejecutan silenciosamente en esta compilación para evitar errores cuando el mod externo no está instalado.


AMPLIACIÓN DE LOGROS 1.1.1
----------------------------
La progresión incluye 58 avances de bajo impacto, basados en inventario y eventos vanilla.
Se añadieron ramas de agricultura, animales, automatización, Nether, End y comercio.


LOGROS DE COMBATE AMPLIADOS
----------------------------
Se añadieron logros adicionales centrados en granjas de mobs y botines hostiles: muertes específicas
de zombi, esqueleto, creeper, araña, slime, bruja, enderman, blaze y ghast; además de botines
como hilo, carne podrida, ojo de araña, slime, flechas, polvo de blaze, lágrima de ghast y redstone.


RAMA DE COMBATE POR NIVELES
----------------------------
La rama de mobs se organiza en cuatro etapas:
1. Combate básico: primeras bajas de zombis, esqueletos, creepers y arañas.
2. Granja inicial: colección conjunta de carne podrida, huesos, hilo y pólvora.
3. Botines avanzados: slime, redstone, ojo de araña y perlas de Ender.
4. Cacería del Nether: blaze, polvo de blaze, ghast y lágrimas de ghast.

Los hitos de colección se comprueban junto con la revisión liviana de inventario cada cinco segundos.


OPTIMIZACIÓN 1.1.4
------------------
- Los 24 chunks de anclas dejan de permanecer forzados después de inicializarse.
- Cada isla se carga temporalmente solo al asignarla y se libera después.
- Los avances completados dejan de comprobarse.
- Las comprobaciones de inventario se dividen en dos grupos alternados; cada grupo se revisa cada 10 segundos.
- Los logros por matar criaturas continúan usando eventos vanilla inmediatos.
- Se eliminaron comandos duplicados y operaciones innecesarias ejecutadas cada tick.
- La detección del preset se guarda al cargar y evita consultar el bioma en cada tick.


HOTFIX 1.1.4.1
--------------
Se retiró el Mixin cliente usado para omitir la advertencia experimental. La clase del JAR anterior
fue compilada sin metadatos Mixin válidos y podía impedir el inicio del juego. La advertencia vanilla
puede volver a mostrarse, pero no afecta la creación ni el funcionamiento del mundo Skyblock.


HOTFIX 1.1.4.2
----------------
Corrige los botones de dificultad/cofre: ahora ejecutan correctamente el trigger sb_difficulty al hacer clic.


HOTFIX 1.1.4.3
--------------
Elimina el rótulo flotante de propietario de la isla y limpia automáticamente los text_display residuales.
No modifica todavía la lógica de dificultad mínima; se requiere revisar el registro de configuración aplicada.


RECETAS SKYBLOCK ADICIONALES
---------------------------
- Tierra: 4 carne podrida + 4 polvo de hueso + 1 cobblestone -> 1 tierra.
- Se eliminó la receta personalizada de tierra estéril.
- Micelio: 4 bloques de champiñón rojo + 4 bloques de champiñón marrón + 1 tierra -> 1 micelio.
- Fabricar el micelio mediante esta receta desbloquea un logro secreto.
