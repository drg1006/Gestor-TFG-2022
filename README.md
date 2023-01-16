[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://gestor-tfg-2022.herokuapp.com)

# GII 22.24 Aplicación de gestión de TFGs

### Autor
David Renedo Gil

### Tutores
Álvar Arnaiz González y Ana Serrano Mamolar

### Resumen del proyecto
El propósito del proyecto es evolucionar la aplicación web de Gestor de TFG/TFM del grado de Ingeniería Informática de la Universidad de Burgos. Se llevarán a cabo tanto mejoras funcionales, como visuales. 
Se trata de una aplicación Java, con el uso de componentes gráficos del framework de aplicaciones web Vaaddin https://vaadin.com/home.  

El desarrollo del proyecto será una nueva distribución (fork) del trabajo fin de grado disponible en https://github.com/mjuez/Gestor-TFG-2021.

El proyecto esta en Vaadin 14 en la rama **master** y con pruebas a parte realizadas en la rama **tesbranch**.

### Página web
El proyecto está desplegado en https://gestor-tfg-2022.herokuapp.com/ .

### Instalación
* JDK --> https://www.oracle.com/es/java/technologies/javase-jdk11-downloads.html
* Apache Tomcat 9 --> https://tomcat.apache.org/download-90.cgi
* Apache Tomcat como Servidor --> https://vaadin.com/docs/v8/framework/installing/installing-server.html
* IDE Eclipse (Versión 2021-12 R) --> https://www.eclipse.org/downloads/packages/release/2021-12/r
* Plugin de Vaddin --> Instalar la extensión llamada "Vaadin Plugin for Eclipse" desde el "Eclipse Marketplace".

### Ejecución en local
Desde Eclipse:
* Instalar dependencias y compilar --> mvn install
* Instalar en modo producción (despliegue) --> mvn package -Pproduction
* Ejecutar con tomcat --> run on server 
Desde cmd:
* Mediante gestor de aplicaciones de Tomcat
