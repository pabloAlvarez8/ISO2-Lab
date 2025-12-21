# 🏠 Inmobiliarias Paqui

**Inmobiliarias Paqui** es una aplicación web para la gestión y alquiler vacacional de alojamientos, inspirada en plataformas colaborativas y adaptada a un entorno académico. Su objetivo principal es proporcionar una plataforma funcional que permita a los usuarios buscar, filtrar y reservar alojamientos, facilitando a anfitriones y visitantes la gestión de propiedades y reservas.

Este proyecto ha sido desarrollado como parte de la asignatura **Ingeniería del Software II** (Grado en Ingeniería Informática), aplicando metodologías ágiles, control de versiones y patrones de diseño profesional.

---

## 👥 Equipo de Desarrollo

El proyecto ha sido llevado a cabo por el siguiente equipo, asumiendo roles rotativos de Scrum Master y Development Team:

| Integrante | Rol Principal | Responsabilidades Clave |
| --- | --- | --- |
| **Daniel López Rodríguez** | CEO / Scrum Master / Dev Team | Supervisión de metodología Scrum, soporte técnico y revisión de código.
| **Daira Lucía García Méndez** | Scrum Master / Dev Team | Diseño de interfaz (Frontend), maquetación y pruebas de usabilidad.
| **Pablo Álvarez Peinado** | Scrum Master / Dev Team | Lógica de negocio (Backend), arquitectura técnica, integración y QA.
| **Stephanie Ortiz Ovelar** | Scrum Master / Dev Team | Gestión de tareas, desarrollo y documentación.


---

## ⚙️ Metodología de Trabajo

Para el desarrollo se ha seguido una **metodología ágil híbrida** combinando **Scrum y Kanban**.

* **Ciclo de Vida:** Incremental e iterativo, organizado en **Sprints** (semanales y bisemanales).


* **Gestión de Tareas:** Se utilizó un tablero Kanban (Trello) con estados.


* **Control de Versiones:** Estrategia de ramas en GitHub (`main`, `development`, `feature/*`, etc.) gestionada mediante Pull Requests.

---

## 🚀 Funcionalidades Principales

El sistema integra perfiles de **Propietario** e **Inquilino** en una única cuenta de usuario para mayor eficiencia.

### 👤 Gestión de Usuarios

* **Registro y Autenticación:** Registro seguro (cifrado de contraseñas) y Login.


* **Perfil:** Modificación de datos personales y roles.


### 🏡 Gestión de Alojamientos (Anfitriones)

* **Publicación:** Alta de propiedades con detalles (ubicación, precio, fotos, servicios).

* **Gestión:** Edición y eliminación de propiedades; configuración de reservas (inmediata o bajo petición).


### 🔍 Búsqueda y Reservas (Visitantes)

* **Buscador Avanzado:** Filtrado por destino, fechas y número de personas.

* **Reservas:** Flujo completo de reserva. Si requiere confirmación, el anfitrión puede aceptar o rechazar.

* **Pagos:** Simulación de pasarela de pago (Tarjeta/PayPal).

* **Valoraciones:** Sistema de reseñas y puntuación (1-5 estrellas) tras la estancia.


---


## 🛠️ Stack Tecnológico

El proyecto sigue una arquitectura **por funcionalidades**.

* **Backend:** Java, Spring Boot (Web, Data JPA, Security).

* **Frontend:** HTML5, CSS3, Thymeleaf (Motor de plantillas), JavaScript.

* **Base de Datos:**
  * *Desarrollo:* Apache Derby (Embebida).
  * *Diseño:* MySQL con Hibernate/JPA.

* **Herramientas de Calidad:**
  * **SonarQube:** Análisis estático de código para detectar *Code Smells* y vulnerabilidades.

  * **Maven Surefire:** Automatización de informes de pruebas.

---

## 🧪 Estrategia de Testing

Se ha implementado un plan de pruebas exhaustivo para asegurar la calidad del software.

### 1. Pruebas Estáticas (Calidad del Código)

Realizadas con **SonarQube** para evaluar métricas de *Reliability*, *Security* y *Maintainability*. Se han refactorizado aspectos como:

* Eliminación de inyección de campos (`@Autowired`) en favor de inyección por constructor.

* Manejo correcto de excepciones y eliminación de `Thread.sleep()` en tests.

* Reducción de la Deuda Técnica.

### 2. Pruebas Dinámicas (Unitarias e Integración)

* **Frameworks:** JUnit 5 + Mockito.

* **Enfoque:** Caja Blanca. Uso de **Mocks** para aislar la lógica de negocio de la base de datos.

* **Cobertura:** Verificación de controladores, servicios y ciclo de vida de entidades JPA.

---

## 🔧 Mantenimiento y Mejoras Futuras

El proyecto cuenta con un plan de mantenimiento clasificado en cuatro categorías: *Correctivo, Preventivo, Perfectivo y Adaptativo*.

Líneas de trabajo futuro (Roadmap):

* [ ] Mejora visual: Centrar alojamientos en el Index y refinar el CSS global.
* [ ] Funcionalidad: Añadir más filtros avanzados en la búsqueda.
* [ ] UX: Optimización del footer y visualización de detalles del alojamiento.

---

## ⚙️ Instalación y Ejecución

### Prerrequisitos

* Java JDK 17+
* Maven

### Pasos

1. **Clonar el repositorio:**
```bash
git clone https://github.com/pabloAlvarez8/ISO2-Lab.git
cd ISO2-Lab/negocio

```


2. **Ejecutar Tests (y generar reporte Surefire):**
```bash
./mvnw test

# Los reportes se generarán en negocio/target/surefire-report
```


3. **Iniciar la aplicación:**
```bash
./mvnw spring-boot:run

```

4. **Acceder a la aplicación desde:**
```bash
http://localhost:8080
```