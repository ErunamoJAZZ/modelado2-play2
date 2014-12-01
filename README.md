Proyecto, Taller de modelado 2
==============================

Proyecto de **Taller de Modelado 2**, materia del posgrado en Ingeniería de 
Sistemas de la Universidad Nacional de Colombia, Sede Medellín.

En el curso se analizaron diferentes frameworks web, y con cada uno se creó una 
aplicación que permitiera subir fotos, dejando que el usuario creara albumes 
para ellas, y si se quería, poder hacer las fotos privadas.

El objetivo era ver cómo era la aproximación de cada uno de los frameworks al
solucinar el mismo problema.



  
Cómo construir esto
===================

Se debe tener el JDK7 instalado, y se debe de instalar el _activator_ que se 
descarga desde https://playframework.com/download (aunque el proyecto tiene ya
una copia de esto).

el activator tiene una serie de comandos, se pueden consultar en la documentación
(hay unos muy interesantes como el `dist` o `console`), pero los más interesantes son:

* `update`: descarga automáticamente todas las dependencias. 
* `compile`: compila el proyecto.
* `run`: ejecuta en modo developer, y deja oyendo en http://localhost:9000/ , y
  cuando un archivo es cambiado, compila automáticamente. 
* `clean`: limpia los archivos compilados.

El sistema usa una base de datos SQLite. Pero se puede usar cualquier otra que 
sea soportda por Slick.



Información sobre las aplicaciones en Play Framework
====================================================

Este framework es muy similar a todos los basados en _Ruby on Rails_, su anatomía
puede verse en la documentación oficial: https://playframework.com/documentation/2.3.x/Anatomy

Lo interesante de usar este framework sobre otros, es que internamente maneja 
las respuestas con el modelo de actores (usando Akka, un framework para programación
concurrente), por lo que tiene un grado muy alto de resistencia a la una gran 
cantidad de peticiones, sin "caerse". 

De entre los Frameworks para usar con java (o Scala en este caso), este es quizas 
el más nuevo, y el que se aleja más de la forma "tradicional" de estos, para 
proyectos nuevos, y en los que preocupe mucho la concurrencia, y el procesamiento
en paralelo, este puede ser la mejor elección.

Las dependencias son manejadas de forma automática por **sbt**, 
que a su vez usa los repositorios de
*Maven*, y esto se puede encontrar en el archivo `build.sbt`.


Plantillas
----------

La info sobre el sistema de plantillas se puede leer en 
https://playframework.com/documentation/2.3.x/ScalaTemplates

En *main.scala.html* está la plantilla base, de esta heredan las demás, 
también se tiene el *nav_component.scala.html* que es como un componente para
la navegación (no se llama en todos, pero era solo un ejemplo).

A lo mejor hay una mejor manera de ordenar esto, pero no soy un bueno con la parte
de frontend, pero en fin...

Algo importante de notar, es que las plantillas no tienen acceso a atributos que
no se le pasen, sea explicita, o implicitamente. (lo cual concuerda con la idea
de "programación funcional" que se usa en Scala...). 

En los formularios, se hizo uso de un helper llamado **@CSRF.formField**, 
para evitar adulteraciones.


Modelo
------

Slick 2.1 es el ORM usado en este proyecto... realmente se puede usar cualquiera
que sea compatible con Java, pero Slick está especialemente optimizado para 
Scala. Lo más interesante es que permite usar la sintaxis que se usa para 
recorrer colecciones como las listas (una sintaxis muy comun en Scala), para 
hacer las consultas SQL.. lo cual hace que luzcan muy similares.

También hice un DAO, que aunque no es obligatorio, es una buena manera de mantener
centralizadas las sesiones de conexión.

Los datos de conexión se pueden encontrar en `conf/application.conf`, ahí además hay 
otras cosas como la linea que escucha slick para contruir automáticamente las 
tablas cuando las clases que las mapean se modifican, etc.


Controllers
-----------

Son los controladores de la aplicación... un objeto trae dentro las acciones.
Dentro de cada acción, se puede revizar si ya inició sesión o no... esto podría 
también enmascararse haciendo una "composición" de Acciones 
(que no es más que un currying de estas funciones) 
https://playframework.com/documentation/2.3.x/ScalaActionsComposition

Hay un objeto especial llamado **Global**, el cual es el que inicia de primero 
en la aplicación. Lo puse de ejemplo ya que se puede hacer cualquier tipo de 
procedimiento de inicialización ahí... este objeto puede llamarse de cualquier 
forma, pero se debe normbrar su lugar, en `conf/application.conf`.

En los controladores que reciben datos de formularios, se metió una acción
especial llamada **CSRFCheck**, donde se checkea que no se haya adulterado.

 

LICENCE
=======

Todos los derechos reservados <C. Daniel Sanchez R.>. 
* Se prohibe el uso de este código con fines comerciales.
* Se permite usarlo para cualquier fin personal, educativo, etc.
* Se permite la modificación del código.
* Modificaciones de más del 50% en la lógica del programa, no son 
  cobijadas por esta licencia.

  