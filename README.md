# Aplicación Móvil para el Mundo del Rally

Esta aplicación móvil, desarrollada en Android Studio, ofrece a los aficionados del rally un punto de acceso integral para consultar eventos, noticias actualizadas y datos oficiales del mundial de Rally (clasificaciones y resultados). Además, permite a los usuarios guardar eventos favoritos, visualizarlos en un calendario personal y recibir notificaciones relacionadas, facilitando la interacción y el seguimiento de sus actividades deportivas.

---

## Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
  - [Clonación del Repositorio](#clonación-del-repositorio)
  - [Importar en Android Studio](#importar-en-android-studio)
  - [Sincronización y Configuración](#sincronización-y-configuración)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [Flujo y Funcionalidades](#flujo-y-funcionalidades)
- [Estructura de Ficheros](#estructura-de-ficheros)
- [Diseño de la Aplicación](#diseño-de-la-aplicación)
- [Autor](#autor)
- [Supervisora](#supervisora)
- [Licencia](#licencia)
- [Contacto](#contacto)

---

## Requisitos Previos 

Antes de proceder con la instalación y configuración de la aplicación, hay que asegurarse de contar con los siguientes elementos:

- **Git:** Herramienta esencial para clonar el repositorio.  
  [Descargar Git](https://git-scm.com/downloads)  
  ![Git Icon](https://img.shields.io/badge/-Git-black?style=flat-square&logo=git)

- **Java JDK:** Requerido para compilar la aplicación.  
  [Descargar Java JDK](https://www.oracle.com/java/technologies/javase-downloads.html)

- **Android Studio:** Entorno de desarrollo integrado para Android.  
  [Descargar Android Studio](https://developer.android.com/studio)  
  ![Android Studio Icon](https://img.shields.io/badge/-Android%20Studio-3DDC84?style=flat-square&logo=android)

- **Dispositivo Android o Emulador:** Para probar y ejecutar la aplicación.

---
## Instalación 

### Clonación del Repositorio {#clonación-del-repositorio}

Para comenzar, se deberá clonar el repositorio del proyecto en una máquina local. Posteriormente se debe abrir una terminal y ejecutar el siguiente comando:

```bash
git clone https://github.com/Ismaramos453/TFT_1_Desarrollo_de_una_Plataforma_Movil_para_Eventos_y_Comunidad_de_Rally.git
```
Luego, se deberá navegar al directorio raíz del proyecto:
```
cd ruta/al/proyecto/TFT-Mobile-App
```

### Importar en Android Studio
- **Abrir Android Studio.**
- **Seleccionar "Open an existing Android Studio project".**
- **Navegar hasta el directorio donde se clonó el repositorio y seleccionar la carpeta raíz del proyecto.**
- **Esperar a que Android Studio sincronice el proyecto y descargue las dependencias necesarias (esto se realiza automáticamente a través de Gradle).**

### Sincronización y Configuración
- **Gradle Sync:** Una vez importado el proyecto, Android Studio iniciará la sincronización con Gradle. Si esta acción no se realiza automáticamente, seleccione File > Sync Project with Gradle Files.

- **Configuración de Variables de Entorno:** El proyecto utiliza servicios en la nube (Firebase), por tanto hay que asegurarse de colocar el archivo de configuración google-services.json en el directorio app/ y revisar el archivo build.gradle para confirmar la integración.

---
## Ejecución del Proyecto 
#### Para compilar y ejecutar la aplicación:
- **Se deberá conectar un dispositivo Android físico o iniciar un emulador a través de Android Studio.**
- **Hacer clic en el botón Run (ícono de ▶) o seleccionar Run > Run 'app'.**
- **La aplicación se compilará y se desplegará en el dispositivo o emulador seleccionado.**

---
## Flujo y Funcionalidades
### El flujo operativo de la aplicación se estructura en las siguientes funcionalidades:

- **Registro e Inicio de Sesión:** Permite a los usuarios crear una cuenta e iniciar sesión para acceder a contenido personalizado.

- **Sistema de Noticias y Calendario de Eventos:** Proporciona información actualizada sobre eventos, noticias y resultados oficiales del mundial de Rally.

- **Gestión de Pilotos Favoritos y Guardado de Eventos:** Facilita la selección de pilotos favoritos y el marcado de eventos para su posterior consulta en un calendario personal.

- **Integración de Notificaciones y Recordatorios:** Los usuarios reciben notificaciones sobre eventos y cambios relevantes en la información.

- **Modo Oscuro y Apartado Multimedia:** Incluye una opción de modo oscuro para mejorar la experiencia visual, junto con una galería de imágenes y vídeos.

- **Foro Interno:** Permite la interacción entre usuarios, favoreciendo el intercambio de opiniones y experiencias relacionadas con el rally.

---
## Estructura de Ficheros 

La estructura de ficheros de la aplicación se organiza de la siguiente manera:

### Dentro del directorio principal de la app

**Ruta:** `TFT/app/src/main/java/com/example/tft`

- **data/**  
  - **api/:** Aquí se encuentran todos los archivos y ficheros relacionados con las llamadas a la API y el tratamiento de los datos obtenidos.
- **services/:** Este directorio contiene todos los servicios encargados de conectar, traer y cargar datos en la base de datos.
- **worker/:** Incluye el código relativo al sistema de notificaciones automáticas, que se activa cuando un usuario guarda un evento.

- **model/:** Aquí se encuentran los _dataclass_ y las estructuras de datos que representan:
  - Etapas
  - Foro
  - Pilotos
  - Preguntas y Reportes
  - Usuarios
  - Videos
  - Datos de WRC e información de la API
  - Cars
  - Ítems de la barra de navegación (se utiliza una _sealed class_)
  - News
  - Photos
  - RallyEvent

- **navigation/:** Este directorio se encarga del flujo y la navegación de la aplicación. Aquí se encuentran:
  - `AppNavigation`  
  - `AppScreens`

- **templates_App/:** Contiene componentes reutilizables que se utilizan de forma repetida en la aplicación, tales como:
  - `BackTopBar`
  - `DefaultTopBar`

- **ui/:** Este directorio agrupa todo lo relacionado con las _Screens_ (parte visual) y sus correspondientes _viewModels_. Cada Screen tiene su fichero composable y su lógica en el ViewModel asociado.

- **MainActivity:** Se encuentra al mismo nivel que los directorios anteriores y es el punto de entrada de la aplicación.

### Recursos (res)

Al mismo nivel que el directorio `java`, se encuentra el directorio de recursos:

**Ruta:** `TFT/app/src/main/res`

- **drawable/:** Contiene todas las imágenes e íconos utilizados en la aplicación.
- **mipmap/:** Aquí se ubican los distintos tamaños de iconos para la aplicación.
- **values/:** Incluye archivos XML para la definición de estilos, colores, cadenas de texto y otros recursos de configuración.
- **Otros directorios XML:**  Archivos XML relacionados con la configuración de layouts, temas, etc.

### Scripts de Gradle

Al nivel superior del proyecto, se encuentra el directorio de configuración de Gradle:

**Ruta:** `TFT/Gradle Scripts`

- Aquí se ubican todos los ficheros de configuración necesarios para la compilación y gestión del proyecto a través de Gradle.

---


---
## Diseño de la Aplicación 
El diseño gráfico y la propuesta de experiencia de usuario (UX/UI) han sido desarrollados en Figma, proporcionando una visión interactiva y detallada de la interfaz de la aplicación. Se recomienda consultar el siguiente enlace para apreciar el diseño visual:
https://www.figma.com/design/Bmh5LJqfA7YY1TPo1ZOAK7/Untitled?node-id=0-1&p=f&t=er3uaQAU5McFa6tE-0

---
## Autor 
- **Ismael Ramos Alonso**

---
## Supervisora 
- **María Dolores Afonso Suárez**

---
## Licencia 
Este software es de propiedad exclusiva y confidencial.
Se prohíbe terminantemente la reproducción o distribución de este archivo por cualquier medio sin la autorización correspondiente. Todos los derechos están reservados.

---
## Contacto 
Para consultas, sugerencias o más información, póngase en contacto a través de:
Correo electrónico: ismael.ramos103@alu.ulpgc.es
