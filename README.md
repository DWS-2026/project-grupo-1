# Apex Expeditions

## 👥 Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:--- |:--- |:--- |
| Mario Ortiz Lopo | m.ortizl.2023@alumnos.urjc.es | marioortiz-6 |
| Pablo Sánchez Martín | p.sanchezm.2023@alumnos.urjc.es | psmURJC |
| Javier Hérnandez Campano | j.hernandezca.2023@alumnos.urjc.es | javier0004 |
| Andrés Sánchez Nogales | a.sanchezn.2023@alumnos.urjc.es | Andresshme |

--- 

## 🎭 **Preparación: Definición del Proyecto**

### **Descripción del Tema**
Aplicación web para una *agencia* de viajes que ofrece paquetes turísticos organizados (tours) a distintos destinos, mostrando información de cada tour (precio por persona, duración, ubicación y características del alojamiento) y permitiendo a los usuarios buscar, filtrar y solicitar presupuestos online. Pertenece al sector turístico y facilita al usuario comparar destinos y tours de forma rápida, así como contactar con la agencia para gestionar su viaje. [themewagon.github](https://themewagon.github.io/pacific/destination.html)

### **Entidades**
Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. *Usuario*
2. *Destino*
3. *Reserva / Solicitud de Presupuesto*
4. *Categoría de Experiencia* (Adventure, Beach, Nature, Camping, etc.) [themewagon.github](https://themewagon.github.io/pacific/destination.html)
5. *Hotel* (1 por destino)


**Relaciones entre entidades:**
- *Usuario - Reserva*: Un usuario puede crear múltiples reservas/solicitudes de presupuesto; cada reserva pertenece a un único usuario (1:N).
- *Destino - Reserva*: una reserva está asociada a uno o más destinos; un destino puede tener muchas reservas (1:N). Un tour se compone de 1 o más destinos (ej: si 2 destinos: destino1-hotel1, destino2-hotel2).
- *Destino - Categoría de Experiencia*: Un destino puede clasificarse en varias experiencias; cada experiencia agrupa múltiples destinos (NM).
- *Destino - Hotel*: Cada destino tiene exactamente un hotel asociado (1-1).
- *Usuario - Valoración (opcional)*: Un usuario puede dejar varias valoraciones sobre tours; cada valoración pertenece a un usuario y a un tour (1:N respecto a Usuario y 1:N respecto a Tour).

### **Permisos de los Usuarios**
Describir los permisos de cada tipo de usuario e indicar de qué entidades es dueño:

* *Usuario Anónimo*:  
  - Permisos:  
    - Visualizar listado de tours y destinos (catálogo público). [themewagon.github](https://themewagon.github.io/pacific/destination.html)
    - Buscar y filtrar tours por destino, fechas y rango de precio.  
    - Ver detalles básicos del tour (precio por persona, duración, ubicación, características).  
    - Enviar formulario de contacto genérico o “Ask For A Quote” sin cuenta (según diseño). [themewagon.github](https://themewagon.github.io/pacific/destination.html)
  - No es dueño de ninguna entidad.

* *Usuario Registrado*:  
  - Permisos:  
    - Todo lo del usuario anónimo.  
    - Gestionar su perfil (datos personales, preferencias de viaje).  
    - Crear y gestionar sus propias reservas/solicitudes de presupuesto de tours.  
    - Consultar el historial de reservas y estado (pendiente, confirmada, cancelada).  
    - Crear valoraciones/comentarios sobre tours (si se implementa módulo de opiniones).  
  - Es dueño de:  
    - Su Perfil de Usuario.  
    - Sus propias Reservas/Solicitudes de Presupuesto.  
    - Sus propias Valoraciones/Comentarios.

* *Administrador*:  
  - Permisos:  
    - Gestión completa (CRUD) de Tours (precio, duración, imágenes, descripción, servicios, etc.). [themewagon.github](https://themewagon.github.io/pacific/destination.html)
    - Gestión de Hoteles (alta, baja, modificación; un hotel por destino).
    - Gestión de Categorías de Experiencia (Adventure, Beach, Nature, etc.).
    - Gestión de Usuarios (activar/desactivar cuentas, ver datos básicos y reservas).
    - Gestión y revisión de Reservas/Solicitudes (cambio de estado, anulación, confirmación).
    - Moderación de valoraciones/comentarios (aprobar, eliminar).
    - Visualización de estadísticas (número de reservas por destino, destinos más populares, ingresos estimados).
  - Es dueño de:  
    - Destinos, Categorías y Hoteles.  
    - Puede gestionar (no “poseer”) todas las Reservas y Usuarios.

---

## 🛠 **Práctica 1: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**
Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación](images/navigation-diagram.png)

> [Descripción opcional del flujo de navegación: Ej: "El usuario puede acceder desde la página principal a todas las secciones mediante el menú de navegación. Los usuarios anónimos solo tienen acceso a las páginas públicas, mientras que los registrados pueden acceder a su perfil y panel de usuario."]

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Home**
![Página Principal](images/home-page.png)

> "Página de inicio que muestra tantos los servicios que ofrecemos como la calidad de estos. Incluye una barra de navegación y acceso a registro/login para usuarios no autenticados."

#### **2. Tours disponibles / Packages**
![Página Principal](images/home-page.png)

> "Página donde el usuario puede observar los distintos viajes que ofrecemos, asi como su información más relevante resumida (destino principal, precio, duración, número de viajeros posibles, etc). La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **3. Descripción de un tour específico / tour-details**
![Página Principal](images/home-page.png)

> "Página en la que se muestran todos los detalles del producto seleccionado en tours. En esta página, específica para cada producto, podremos añadir el producto al carrito o reservarlo tanto online como por llamada telefónica, también podremos leer la toda la información sobre el producto y las reseñas que han dejado otros usuarios sobre este. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **4. Carrito de compra / carrito**
![Página Principal](images/home-page.png)

> "Página en la que podremos observar todos los productos que hemos añadido al carrito. Nos permite volver a la página de estos productos para observar de nuevo sus detalles, volver a la página tours para seguir explorando el resto de productos y proceder al pago de los productos seleccionados. Podremos acceder a esta página desde cualquier página que tenga incluida barra de navegación. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **5. Zona de pago / checkout**
![Página Principal](images/home-page.png)

  > "Página en la que podremos proceder al pago de los prodcutos seleccionados. En esta página se rellenarán los campos indicados (bancarios) para realizar el pago final de los productos."

#### **6. Factura / invoice**
![Página Principal](images/home-page.png)

> "Página en la que podremos observar el recibo de pago de la compra realizada. En esta página podremos observar todos los detalles de la compra: precio final, detalles de la agencia, quien ha realizado el pago y productos adquiridos (en este caso, packs de tours de viajes). También podremos volver a la página principal."

#### **7. Guías / guides**
![Página Principal](images/home-page.png)

> "Página en la que podremos observar a nuestros guías más relevantes. Los usuarios pueden observar a nuestros guías más relevantes asi como su especialización, además, se incluyen sus redes sociales por si se les desea contactar o pedir recomendaciones. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **8. Sobre nosotros / about**
![Página Principal](images/home-page.png)

> "Página en la que podremos observar información relevante a la empresa. Los usuarios pueden observar información sobre nosotros y las tareas principales de la empresa, además de las garantías incluidas en todos nuestros productos. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **9. Contacto / contact**
![Página Principal](images/home-page.png)

> "Página en la que se indicará como contactar con el servicio al cliente de la empresa. Los usuarios que tengan cualquier duda en lo relativo a los productos o hayan tenido cualquier problema técnico, nos podrán contactar rellenando los formularios indicados en esta página. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **10. Inicio de sesión / login**
![Página Principal](images/home-page.png)

> "Página en la que se indicará como iniciar sesión en la web para poder acceder a todos los servicios ofrecidos. En caso de tener una cuenta registrada, se podrá navegar a una página para la creación de esta, también se podrá proceder a la recuperación de contraseña en caso de haberla olvidado o haber tenido cualquier problema relativo a esta. También se podrá acceder a la págino de incio de sesión como administrador. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **11. Registro / register**
![Página Principal](images/home-page.png)

> "Página en la que se podrá proceder a la creación de una nueva cuenta. Una vez rellenados todos los campos requeridos y haber aceptados los términos y condiciones, se redirigirá añ usuario a la página de incio de sesión. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **12. Inicio de sesión para administrador / admin-login**
![Página Principal](images/home-page.png)

> "Página en la que los administradores podrán iniciar sesión. Una vez rellenados los campos se podrá acceder al panel de administrador, también se ofrece la posibilidad de volver al incio de sesión para usuarios comunes. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **13. Recuperación de contraseña / forgot-password**
![Página Principal](images/home-page.png)

> "Página en la que podremos recuperar la contraseña en caso de haberla perdido o tener cualquier problema relativo a esta. Se nos pedirá introducir nuestro correo asociado a la cuenta, una vez hecho esto se nos enviare un mensaje a nuestro correo electrónico. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **14. Mi perfil / profile**
![Página Principal](images/home-page.png)

> "Página en la que se podrá observar la información de la cuenta con la que nos encontramos registrados. En todas las páginas de esta web (salvo chekout e invoice) se podrá acceder a través del boton "Mi perfil", ubicado en la dropbar "Opciones" situada en parte superior de estas. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **14. Error 404 / 404**
![Página Principal](images/home-page.png)

> "Página a la que será redirigido el usuario en caso de acceder a una página que no existe o a la que el servidor no puede acceder. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **AQUÍ AÑADIR EL RESTO DE PÁGINAS**

### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 2: Web con HTML generado en servidor**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

Solo si ha cambiado.

#### **Capturas de Pantalla Actualizadas**

Solo si han cambiado.

### **Instrucciones de Ejecución**

#### **Requisitos Previos**
- **Java**: versión 21 o superior
- **Maven**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio

#### **Pasos para ejecutar la aplicación**

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

2. **AQUÍ INDICAR LO SIGUIENTES PASOS**

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin`, contraseña: `admin`
- **Usuario Registrado**: usuario: `user`, contraseña: `user`

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](images/database-diagram.png)

> [Descripción opcional: Ej: "El diagrama muestra las 4 entidades principales: Usuario, Producto, Pedido y Categoría, con sus respectivos atributos y relaciones 1:N y N:M."]

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/classes-diagram.png)

> [Descripción opcional del diagrama y relaciones principales]

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 3: Incorporación de una API REST a la aplicación web, análisis de vulnerabilidades y contramedidas**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**
📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**
📖 **[Documentación API REST (HTML)](https://raw.githack.com/[usuario]/[repositorio]/main/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](images/complete-classes-diagram.png)

#### **Credenciales de Usuarios de Ejemplo**

| Rol | Usuario | Contraseña |
|:---|:---|:---|
| Administrador | admin | admin123 |
| Usuario Registrado | user1 | user123 |
| Usuario Registrado | user2 | user123 |

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |
