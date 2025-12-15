# ISO2-Lab

## Enlaces

Enlace del protyecto: [Github](https://github.com/pabloAlvarez8/ISO2-Lab "Proyecto")

* Ver [Documentación del proyecto](https://docs.google.com/document/d/1WYdRqaKpLokZI2UAyOUoG7wVif8BfM0WYjm3txiifJ8/edit?tab=t.0 "Documentación")
* Ver [Kanban del proyecto](https://trello.com/b/7v8fO0r8/mi-tablero-de-trello "Kanban")

## Tabla de contenidos
1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Metodología de Trabajo](#metodología-de-trabajo)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Instalación y Ejecución](#instalación-y-ejecución)
5. [Documentación](#documentación)
6. [Equipo de Desarrollo](#equipo-de-desarrollo)

---

## Descripción del Proyecto

**Inmobiliarias Paqui** es una plataforma funcional que conecta a anfitriones con visitantes. El sistema permite buscar, filtrar y reservar alojamientos, además de ofrecer herramientas de gestión para propiedades y usuarios.

**Funcionalidades principales:**
* **Gestión de Usuarios:** Registro, inicio de sesión seguro y roles (Propietario/Inquilino).
* **Búsqueda Avanzada:** Filtrado de alojamientos por ubicación, precio y características.
* **Reservas:** Sistema de solicitud, confirmación y gestión de pagos simulados.
* **Seguridad:** Protección de datos mediante Spring Security.

---

## Metodología de Trabajo

El desarrollo se ha regido por una metodología **Ágil Híbrida (Scrum + Kanban)**, permitiendo un desarrollo incremental e iterativo con entregas continuas.

* **Organización:** Sprints semanales y bisemanales.
* **Seguimiento:** Tablero Kanban en Trello para visualizar el flujo de trabajo (Pendiente, En progreso, En revisión, Completado).
* **Control de Versiones:** Uso de ramas (`main`, `develop`, `feature`, `fix`) y Pull Requests.

---

## Stack Tecnológico

El proyecto utiliza una arquitectura **MVC (Modelo-Vista-Controlador)** construida con:

* **Backend:** Java, Spring Boot, Spring Data JPA.
* **Frontend:** Thymeleaf, HTML5, CSS, JavaScript.
* **Seguridad:** Spring Security.
* **Base de Datos:** MySQL (Gestión con MySQL Workbench).
* **Herramientas:** Visual Studio Code, Maven, GitHub.

---

## Instalación y Ejecución

### Pre-requisitos
Asegúrate de tener instalado:
* Java JDK 17+
* MySQL Server

### Pasos
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/pabloAlvarez8/ISO2-Lab.git](https://github.com/pabloAlvarez8/ISO2-Lab.git)
    cd ISO2-Lab
    ```

2.  **Configurar Base de Datos:**
    Crea una base de datos en MySQL y ajusta el archivo `src/main/resources/application.properties` con tus credenciales:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/tu_base_de_datos
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    ```

3.  **Ejecutar la aplicación:**
    ```bash
    ./mvnw spring-boot:run
    ```
    La web estará disponible en: `http://localhost:8080`

---

## Documentación

Para profundizar en los requisitos, historias de usuario y diagramas UML, consulta los siguientes enlaces oficiales:

*  **Documentación Técnica Completa:** [Google Docs](https://docs.google.com/document/d/1WYdRqaKpLokZI2UAyOUoG7wVif8BfM0WYjm3txiifJ8/edit?tab=t.0)
*  **Kanban del Proyecto:** [Tablero Trello](https://trello.com/b/7v8fO0r8/mi-tablero-de-trello)

---

## Equipo de Desarrollo

| Nombre | Rol Principal | Responsabilidades Clave |
| :--- | :--- | :--- |
| **Pablo Álvarez** | CEO & Dev Team | Lógica de backend, arquitectura y BD. |
| **Daniel López** | Scrum Master | Supervisión de metodología y soporte técnico. |
| **Daira Lucía García**| Scrum Master | Diseño frontend, maquetación y UX/UI. |
| **Stephanie Ortiz** | Scrum Master | Testing, calidad de software (QA) y revisión visual. |

> Proyecto realizado para la asignatura de Ingeniería del Software II (UCLM).