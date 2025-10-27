# 🎵 SISMUS — Sistema de Música UNAM DGTIC

**SISMUS** es una aplicación web tipo *Spotify* desarrollada como parte del **Diplomado en Java con Spring Boot de la UNAM DGTIC (2025)**.  
Permite gestionar canciones, artistas, géneros y playlists personalizadas, así como generar reportes en **PDF** y **Excel** sobre la actividad del sistema.

---

## 🧠 Descripción general

El sistema está construido con **Spring Boot 3**, **Thymeleaf**, **Spring Security**, **Bootstrap 5**, y **JPA (Hibernate)**.  
Incluye autenticación de usuarios, manejo de roles, descargas, y un módulo de administración para generar reportes avanzados.

### Funcionalidades principales
- 👥 Autenticación, registro y control de acceso por roles (usuario / admin).
- 🎧 Gestión de canciones, artistas y géneros musicales.
- 📁 Creación, edición y eliminación de playlists.
- 💾 Descarga de canciones individuales o playlists completas en formato ZIP.
- ⭐ Visualización del Top 10 de canciones más descargadas.
- 📊 Generación de reportes PDF y Excel: canciones, listas, actividad, artistas y géneros.
- ⚙️ Interfaz web responsiva con Bootstrap 5 y fragmentos Thymeleaf.

---

## 🧩 Estructura del proyecto

```

sismus/
├── src/
│ ├── main/
│ │ ├── java/mx/dgtic/unam/sismus/
│ │ │ ├── auth.controller/ ← Controladores de autenticación (login, registro)
│ │ │ ├── config/ ← Configuración general (seguridad, beans, etc.)
│ │ │ ├── controller/ ← Controladores MVC y REST principales
│ │ │ ├── dto/ ← Clases DTO (Data Transfer Objects)
│ │ │ ├── error/ ← Manejo de errores específicos
│ │ │ ├── exception/ ← Excepciones personalizadas y manejo global
│ │ │ ├── mapper/ ← Conversión DTO ↔ Entidad con MapStruct o manual
│ │ │ ├── model/ ← Entidades JPA (tablas del sistema)
│ │ │ ├── repository/ ← Repositorios JPA y consultas personalizadas
│ │ │ ├── security/ ← Configuración y filtros de Spring Security
│ │ │ ├── service/ ← Lógica de negocio (servicios de dominio)
│ │ │ └── SismusApplication.java ← Clase principal del proyecto (Spring Boot)
│ │ │
│ │ ├── resources/
│ │ │ ├── static/ ← Archivos estáticos (frontend)
│ │ │ │ ├── audios/ ← Archivos MP3 y recursos multimedia
│ │ │ │ ├── bootstrap/ ← Librería Bootstrap local
│ │ │ │ ├── css/ ← Hojas de estilo personalizadas
│ │ │ │ ├── iconos/ ← Íconos y recursos gráficos
│ │ │ │ ├── images/ ← Imágenes del sitio
│ │ │ │ ├── js/ ← Scripts JavaScript (interacción dinámica)
│ │ │ │ └── tema/ ← Paletas, estilos o configuraciones visuales
│ │ │ │
│ │ │ ├── templates/ ← Vistas Thymeleaf (HTML dinámico)
│ │ │ │ ├── admin/ ← Panel de administración
│ │ │ │ ├── auth/ ← Pantallas de login y autenticación
│ │ │ │ ├── cancion/ ← Listado, búsqueda y gestión de canciones
│ │ │ │ ├── error/ ← Páginas de error personalizadas
│ │ │ │ ├── favoritos/ ← Top 10 canciones más descargadas
│ │ │ │ ├── layout/ ← Plantilla base y layout principal
│ │ │ │ ├── playlist/ ← Gestión de playlists del usuario
│ │ │ │ ├── reporte/ ← Vistas de generación de reportes
│ │ │ │ └── usuario/ ← Perfil de usuario
│ │ │ │
│ │ │ └── application.properties ← Configuración de Spring Boot
│ │ │
│ └── test/ ← Pruebas unitarias y de integración 
│
├── pom.xml ← Archivo Maven (dependencias y build)
└── README.md ← Documentación del proyecto

```
---

## ⚙️ Tecnologías utilizadas

| Capa | Tecnología |
|------|-------------|
| Backend | Java 17 / Spring Boot 3 |
| Web | Thymeleaf + Bootstrap 5 + jQuery |
| Seguridad | Spring Security (login personalizado) |
| Base de datos | MariaDB (JPA Hibernate) |
| Reportes | Apache POI (Excel) / OpenPDF (PDF) |
| IDE | IntelliJ IDEA |
| Gestión de dependencias | Maven |
| Logging | SLF4J / Logback |

---

## 🚀 Cómo ejecutar el proyecto en IntelliJ IDEA

### 1️⃣ Importar el proyecto
1. Abre **IntelliJ IDEA**.
2. Ve a **File → Open…**
3. Selecciona la carpeta raíz del proyecto (`sismus/`).
4. IntelliJ detectará automáticamente el proyecto **Maven**.

> 💡 Si te pregunta el SDK, elige **Java 17 o superior**.

---

### 2️⃣ Configurar base de datos

🧩 Inicializar la base de datos

El proyecto incluye un script SQL completo llamado:

`src/main/resources/db/scripts/sismus.sql`

Abre una terminal o consola de comandos y ejecuta:

`mysql -u root -p < src/main/resources/db/scripts/sismus.sql`

Este comando ejecuta el script completo, creando la base de datos y poblando todos los datos de ejemplo.
Asegúrate de que el usuario y contraseña de MariaDB coincidan con los configurados en application.properties

Edita el archivo `src/main/resources/application.properties`:

```properties
# Puerto y nombre de la app
server.port=8080
spring.application.name=sismus

# Configuración de base de datos local
spring.datasource.url=jdbc:mariadb://localhost:3306/sismus
spring.datasource.username=root
spring.datasource.password=dgtic123
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuración Thymeleaf
spring.thymeleaf.cache=false

💡 Cambia el usuario y contraseña según tu configuración local.

```

📋 Datos iniciales

El script ya incluye varios usuarios de prueba con roles y datos precargados:

| 👤 **Usuario** | 📧 **Correo**        | 🧩 **Rol**   | 🔑 **Contraseña (bcrypt)** |
| -------------- | -------------------- | ------------ | -------------------------- |
| `luismgg`      | `luis@example.com`   | ADMIN / USER | (encriptada)               |
| `anamlo`       | `ana@example.com`    | ADMIN / USER | (encriptada)               |
| `carlitos`     | `carlos@example.com` | USER         | (encriptada)               |
| `pedropz`      | `pedropz@gmail.com`  | USER         | (encriptada)               |
| `pepito123`    | `pepito@gmail.com`   | USER         | (encriptada)               |
| `pepe1`        | `pepe@example.com`   | ADMIN / USER | (encriptada)               |
🔐 Puedes iniciar sesión con luismgg o anamlo para acceder al panel de administración.

---

### 3️⃣ Ejecutar el proyecto

- Usa el atajo: **Shift + F10**
- O desde el menú: **Run → Run ‘SismusApplication’**

Si todo está correcto, deberías ver algo como:


**Tomcat started on port(s): 8080 (http) with context path '/'**

**Started SismusApplication in 6.749 seconds**


---

### 4️⃣ Abrir en el navegador

➡️ [http://localhost:8080](http://localhost:8080)

Rutas principales:

## 🧭 Navegación del sistema

| Módulo | URL | Descripción                                                                         |
|--------|-----|-------------------------------------------------------------------------------------|
| 🏠 **Inicio** | `/` | Página principal del sistema (canciones disponibles, búsqueda, navegación general). |
| 👤 **Perfil** | `/usuario/perfil` | Información del usuario autenticado y sus datos personales.                         |
| 🎵 **Canciones** | `/canciones` | Catálogo de canciones disponibles para descargar o agregar a playlists.             |
| 📁 **Playlists** | `/playlists` | Gestión de listas personales del usuario (crear, editar, eliminar, descargar).      |
| ⭐ **Favoritos / Top 10** | `/favoritos/top10` | Muestra las canciones más descargadas del sistema por cada usuario.                 |
| ⚙️ **Administrar Canciones** | `/admin/canciones` | CRUD de canciones disponibles (solo para administradores).                          |
| 🎤 **Administrar Artistas** | `/admin/artistas` | Gestión de artistas registrados.                                                    |
| 🎼 **Administrar Géneros** | `/admin/generos` | Gestión de géneros musicales.                                                       |
| 👥 **Administrar Usuarios** | `/admin/usuarios` | Administración de cuentas de usuario (activación, eliminación, roles).              |
| 📊 **Ver Reportes** | `/admin/reportes` | Generación de reportes en PDF o Excel sobre actividad, canciones y listas.          |


---

## 📊 Reportes disponibles

| Reporte | PDF | Excel |
|----------|------|-------|
| 🎵 Canciones registradas | `/admin/reportes/pdf/canciones` | `/admin/reportes/excel/canciones` |
| 📚 Listas por usuario | `/admin/reportes/pdf/listas` | `/admin/reportes/excel/listas` |
| 👤 Actividad de usuarios | `/admin/reportes/pdf/actividad` | `/admin/reportes/excel/actividad` |
| 🧑‍🎤 Top artistas más descargados | `/admin/reportes/pdf/artistas` | `/admin/reportes/excel/artistas` |
| 🎧 Top géneros más descargados | `/admin/reportes/pdf/generos` | `/admin/reportes/excel/generos` |

---

## 🧠 Detalles técnicos importantes

- **Control global de excepciones:**  
  Implementado con `@RestControllerAdvice` (`GlobalRestExceptionHandler`) para devolver errores JSON uniformes y legibles.

- **Seguridad personalizada:**  
  Integración completa con Spring Security, login propio, y control de rutas por rol (`admin` / `usuario`).

- **Servicios transaccionales:**  
  Lógica encapsulada en clases `@Service` con `@Transactional(readOnly = true)` donde aplica.

- **AJAX dinámico:**  
  Uso de jQuery para agregar canciones a playlists, manejar descargas y realizar búsquedas sin recargar la página.

- **Reportes exportables:**  
  Generación dinámica de documentos PDF (OpenPDF) y hojas de cálculo Excel (Apache POI).

- **Diseño responsivo:**  
  Bootstrap 5 con modo oscuro, tarjetas animadas y modales contextuales reutilizables.

---

## 🧑‍💻 Autor

**Luis Manuel González Gutiérrez**  
Desarrollador Java — UNAM DGTIC  
📍 México  
💬 Intereses: desarrollo backend, bases de datos e inteligencia artificial.

---

## 🧾 Licencia

Proyecto desarrollado con fines educativos para el **Diplomado en Java — UNAM DGTIC 2025**.  
Puedes modificarlo y reutilizarlo libremente con fines académicos o personales.

---

> 🧩 Proyecto completo y modular, con arquitectura limpia, seguridad sólida y una interfaz moderna inspirada en plataformas de streaming.  
> **SISMUS: la música organizada con estilo y buen código. 🎶**

