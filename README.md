# appEstandar
Proyecto que contine el arquetipo para la generación de aplicaciones estandar.

# La aplicación contiene:
El archivo POM con todas las dependencias necesarias.

Las entidades Rol, Usuario y AdminEntidad con sus respectivos EJB de acceso a datos.

La Unidad de Persistencia con su correspondiente JDBC Resource.

Los archivos de propiedades Bundle y ValidationMessages.

El cliente del AccesoAppWebService con todas las clases importadas.

El package util con todas las clases utilitarias (incluyendo el filter para la autenticación y contrlo de sesión).

El package mb con los Mb para Rol y Usuario (este con todo el proceso para la gestión de usuarios completa) y el MbLogin con el todo el proceso de gestión del inicio de sesión del usuario.

Las plantillas para todas las vistas con los recursos necesarios.

La vista inicial y la de acceso restringido.

La administración de Usuarios y Roles.

El archivo de configuración JSF (faces-config.xml).

El archivo de despliegue de la aplicación (web.xml) con la configuración para el filtrado.

# Instrucciónes para la generación de una aplicación
1) Descargar el proyecto.

2) Ir a /appEstandar/target/generated-sources/archetype, desde ahí abrir una ventana de línea de comandos y ejecutar "mvn install". Esto instalará el arquetipo en el respositorio local dejándolo accesible a Maven para la creación de la aplicación.

3) Desde el IDE, ir a Nuevo Proyecto, Maven/Proyect from Archetype. Seleccionar "appEstandar-archetype" del repositorio local.

4) Nombrar el proyecto, Group ID, Versión y Package para la aplicación. Terminar.

5) Actualizar el vínculo con el servicio AccesoAppWebService.

6) Actualizar el package del servicio en el MbUsuario.

7) Actualizar lo que corresponda en el Bundle.

8) Implementar la funcionalidad planificada.
