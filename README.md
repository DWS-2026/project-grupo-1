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
Aplicación web para una agencia de viajes que ofrece paquetes turísticos organizados (tours) a los destinos más raros y únicos del mundo (otros planetas, otrás épocas e incluso futuros apocalípticos), mostrando información de cada tour (precio por persona, duración, ubicación y características del alojamiento) y permitiendo a los usuarios reservar tours de manera online y por llamada, además de poder seleccionarlos para su posterior compra. Pertenece al sector turístico y facilita al usuario comparar destinos y tours de forma rápida, así como contactar con la agencia para gestionar su viaje.


### **Entidades**
Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. *Usuario*
2. *Tour*
3. *Reserva / Solicitud de Presupuesto*
4. *Guía* (Adventure, Beach, Nature, Camping, etc.)
5. *Carrito* (1 por destino)
6. "Notificación


**Relaciones entre entidades:**
- *Usuario - Reserva*: Un usuario puede crear múltiples reservas/solicitudes de presupuesto; cada reserva pertenece a un único usuario (1:N).
- *Destino - Reserva*: una reserva está asociada a uno o más destinos; un destino puede tener muchas reservas (1:N). Un tour se compone de 1 o más destinos (ej: si 2 destinos: destino1-hotel1, destino2-hotel2).
- *Destino - Categoría de Experiencia*: Un destino puede clasificarse en varias experiencias; cada experiencia agrupa múltiples destinos (NM).
- *Destino - Hotel*: Cada destino tiene exactamente un hotel asociado (1-1).
- *Usuario - Valoración: Un usuario puede dejar varias valoraciones sobre tours; cada valoración pertenece a un usuario y a un tour (1:N respecto a Usuario y 1:N respecto a Tour).

### **Permisos de los Usuarios**
Describir los permisos de cada tipo de usuario e indicar de qué entidades es dueño:

* *Usuario Anónimo*:  
  - Permisos:  
    - Visualizar listado de tours y destinos (catálogo público). 
    - Buscar y filtrar tours por destino, fechas y rango de precio.  
    - Ver detalles básicos del tour (precio por persona, duración, ubicación, características).  
    - Enviar formulario de contacto genérico o “Ask For A Quote” sin cuenta (según diseño). 
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
    - Gestión completa (CRUD) de Tours (precio, duración, imágenes, descripción, servicios, etc.).
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
📹 **[Enlace al vídeo en YouTube](https://youtu.be/GUYZgqYFe60)**
📹 **[Enlace al vídeo en Google Drive](https://drive.google.com/file/d/1pgBzeMeLv6O-XTHStwoZ2a43lTnHDl3A/view?usp=sharing)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**
![Diagrama de flujo fase 1.png](Diagrama%20de%20flujo%20fase%201.png)
> Descripción: El flujo de la aplicación comienza en la zona pública, donde el usuario puede acceder a la web desde cualquier página principal a través de la barra de navegación. Desde la navbar se puede ir libremente a la página de inicio, al listado de paquetes turísticos, a la sección de guías, a los servicios ofrecidos, a la página “sobre nosotros”, al formulario de contacto y al carrito de la compra. Esta navegación es bidireccional, permitiendo moverse entre estas secciones sin restricciones.
>
> Desde la página de paquetes, el usuario puede consultar más información sobre un tour concreto, lo que le lleva a la página de detalle del tour. En esta página se muestra la información completa del viaje seleccionado y desde ella se habilitan varias acciones clave. El usuario puede añadir una opinión sobre el tour, accediendo a la página de añadir reseña, desde la cual, una vez enviada la opinión, se regresa de nuevo a la página de detalles del tour. También es posible añadir el tour directamente al carrito de la compra.
>
>El carrito actúa como punto intermedio en el proceso de compra. Desde él, el usuario puede volver a consultar el detalle del tour, seguir explorando otros paquetes turísticos o continuar con el proceso de pago. Si decide pagar, el flujo conduce a la página de checkout, donde se finaliza la compra, y posteriormente a la página de factura, que confirma el pago realizado.
>
>En paralelo, el usuario puede acceder a la zona de autenticación desde la barra de navegación. Desde ahí puede iniciar sesión, registrarse como nuevo usuario o recuperar su contraseña en caso de olvido. Una vez que el inicio de sesión es correcto, el usuario accede a su perfil de cliente, desde donde gestiona su información personal. Además, desde la página de login existe la posibilidad de acceder al panel de administración si se trata de un usuario con permisos de administrador.
>
>El panel de administración comienza con el inicio de sesión del administrador y da acceso al dashboard principal, desde el cual se gestionan las diferentes áreas del sistema. El administrador puede administrar usuarios, consultar y editar sus perfiles, gestionar los tours disponibles (verlos, editarlos o añadir nuevos), y moderar las reseñas realizadas por los clientes. Desde la sección de reseñas, el administrador también puede acceder al detalle del tour asociado a cada opinión para tener una visión completa del contenido que se está gestionando.
>
>Finalmente, el sistema contempla el manejo de errores de navegación. Si el usuario introduce una URL que no corresponde a ninguna página existente, el flujo redirige a la página 404, informando de que la ruta solicitada no se encuentra disponible.

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Home**
![index.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Findex.png)

> "Página de inicio que muestra tanto los servicios que ofrecemos como la calidad de estos. Incluye una barra de navegación y acceso a registro/login para usuarios no autenticados."

#### **2. Tours disponibles / Packages**
![packages.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fpackages.png)

> "Página donde el usuario puede observar los distintos viajes que ofrecemos, así como su información más relevante resumida (destino principal, precio, duración, número de viajeros posibles, etc). La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **3. Descripción de un tour específico / Tour-details**
![tour details.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Ftour%20details.png)

> "Página en la que se muestran todos los detalles del producto seleccionado en tours. En esta página, específica para cada producto, podremos añadir el producto al carrito o reservarlo tanto online como por llamada telefónica, también podremos leer toda la información sobre el producto y las reseñas que han dejado otros usuarios sobre este. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **4. Carrito de compra / Carrito**
![carrito.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fcarrito.png)
> "Página en la que podremos observar todos los productos que hemos añadido al carrito. Nos permite volver a la página de estos productos para observar de nuevo sus detalles, volver a la página tours para seguir explorando el resto de productos y proceder al pago de los productos seleccionados. Podremos acceder a esta página desde cualquier página que tenga incluida barra de navegación. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **5. Zona de pago / Checkout**
![checkout.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fcheckout.png)
  > "Página en la que podremos proceder al pago de los productos seleccionados. En esta página se rellenarán los campos indicados (bancarios) para realizar el pago final de los productos."

#### **6. Factura / Invoice**
![invoice.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Finvoice.png)
> "Página en la que podremos observar el recibo de pago de la compra realizada. En esta página podremos observar todos los detalles de la compra: precio final, detalles de la agencia, quien ha realizado el pago y productos adquiridos (en este caso, packs de tours de viajes). También podremos volver a la página principal."

#### **7. Guías / Guides**
![guides.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fguides.png)
> "Página en la que podremos observar a nuestros guías más relevantes. Los usuarios pueden observar a nuestros guías más relevantes asi como su especialización, además, se incluyen sus redes sociales por si se les desea contactar o pedir recomendaciones. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **8. Sobre nosotros / About**
![about.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fabout.png)
> "Página en la que podremos observar información relevante a la empresa. Los usuarios pueden observar información sobre nosotros y las tareas principales de la empresa, además de las garantías incluidas en todos nuestros productos. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **9. Contacto / Contact**
![contact.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fcontact.png)
> "Página en la que se indicará como contactar con el servicio al cliente de la empresa. Los usuarios que tengan cualquier duda en lo relativo a los productos o hayan tenido cualquier problema técnico, nos podrán contactar rellenando los formularios indicados en esta página. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **10. Inicio de sesión / Login**
![login.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Flogin.png)
> "Página en la que se indicará como iniciar sesión en la web para poder acceder a todos los servicios ofrecidos. En caso de tener una cuenta registrada, se podrá navegar a una página para la creación de esta, también se podrá proceder a la recuperación de contraseña en caso de haberla olvidado o haber tenido cualquier problema relativo a esta. También se podrá acceder a la página de incio de sesión como administrador. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **11. Registro / Register**
![register.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fregister.png)
> "Página en la que se podrá proceder a la creación de una nueva cuenta. Una vez rellenados todos los campos requeridos y haber aceptado los términos y condiciones, se redirigirá al usuario a la página de ini  cio de sesión. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **12. Inicio de sesión para administrador / Admin-login**
![adminlogin.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fadminlogin.png)
> "Página en la que los administradores podrán iniciar sesión. Una vez rellenados los campos se podrá acceder al panel de administrador, también se ofrece la posibilidad de volver al inicio de sesión para usuarios comunes. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **13. Recuperación de contraseña / Forgot-password**
![password.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fpassword.png)

> "Página en la que podremos recuperar la contraseña en caso de haberla perdido o tener cualquier problema relativo a esta. Se nos pedirá introducir nuestro correo asociado a la cuenta, una vez hecho esto se nos enviará un mensaje a nuestro correo electrónico. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **14. Mi perfil / Profile**
![profile.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fprofile.png)

> "Página en la que se podrá observar la información de la cuenta con la que nos encontramos registrados. En todas las páginas de esta web (salvo checkout e invoice) se podrá acceder a través del botón "Mi perfil", ubicado en la dropbar "Opciones" situada en parte superior de estas. La barra de navegación y acceso a registro/login para usuarios no autenticados sigue disponible."

#### **15. Panel de control para administradores / Index**
![index_admin.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Findex_admin.png)

> "Panel de control para administradores desde donde se puede acceder al resto de funcionalidades."

#### **16. Perfil de administrador / Profile**
![profile_admin.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fprofile_admin.png)

> "En esta página cada administrador podrá acceder a información relativa a su cuenta, además de poder editarla."

#### **17. Usuarios / Users**
![users_admin.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fusers_admin.png)

> "Desde esta página los administradores podrán acceder al listado de todos los perfiles de usuario registrados de la web, así como acceder y editar los datos de estos."

#### **18. Tours disponibles / Tours**
![tours_admin.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Ftours_admin.png)

> "Desde esta página los administradores podrán gestionar todos los packs de tours disponibles en la web, pudiendo modificarlos y borrarlos."

#### **19. Reseñas / Reviews**
![reviews_admin.png](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Freviews_admin.png)
> "Desde esta página los administradores tendrán acceso a todas las reviews enviadas por los usuarios, pudiendo editarlas y borrarlas."

### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - Mario Ortiz Lopo**

# Tareas principales del proyecto

1. **Desarrollo de la interfaz de administración**
   - Implementación de funcionalidades para la edición de entidades: **tours**, **reviews** y **usuarios**.

2. **Estilizado de la interfaz**
   - Modificación del fichero `sb-admin-2.min.css` para dar estilo consistente a todas las pantallas.

3. **Pantallas de inicio de sesión**
   - Creación y estilizado de la pantalla de **login normal** y **login de administrador**.

4. **Configuración de navegación**
   - Implementación de la navegación en toda la interfaz de administración y parte de la interfaz de usuario.

5. **Gestión de imágenes**
   - Incorporación de imágenes en las pantallas según los requisitos visuales.

6. **Modularización de la interfaz**
   - Separación de secciones HTML en **componentes reutilizables**, almacenados en `sb_admin_2/components`.

7. **Traducción de contenidos**
   - Traducción al español de todos los ficheros de la interfaz de administración.

8. **Video-presentación de la práctica**
   - Grabación de un video que muestre el funcionamiento y desarrollo del proyecto.


| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Reducción de código repetido a través de creación de componentes](https://github.com/DWS-2026/project-grupo-1/commit/fba03fc)  | [sb_admin_2/components/sidebar.html](https://github.com/DWS-2026/project-grupo-1/blob/main/sb_admin_2/components/sidebar.html)   |
|2| [Utilización de los componentes](https://github.com/DWS-2026/project-grupo-1/commit/0ef97f5)  | [sb_admin_2/index.html](https://github.com/DWS-2026/project-grupo-1/blob/main/sb_admin_2/index.html)   |
|3| [Actualizada interfaz de administrador](https://github.com/DWS-2026/project-grupo-1/commit/6513f94)  | [html/login.html](https://github.com/DWS-2026/project-grupo-1/blob/main/html/login.html)   |
|4| [Añadida sección tours en panel de administración](https://github.com/DWS-2026/project-grupo-1/commit/f7be732)  | [sb_admin_2/charts.html](https://github.com/DWS-2026/project-grupo-1/blob/main/sb_admin_2/charts.html)   |
|5| [Actualizado el archivo css](https://github.com/DWS-2026/project-grupo-1/commit/ed2bef7)  | [html/admin-login](https://github.com/DWS-2026/project-grupo-1/blob/main/html/admin-login.html)   |

---

#### **Alumno 2 - Pablo Sánchez Martín**

Me he encargado de la organización inicial del repositorio (ramas y carpetas) y del diseño granular y reparto de tareas (mediante [trello](https://trello.com/invite/b/697f29309b83f21b92d02577/ATTI13ac19c938e84fc5479018bc1e789e18531EE990/dws-grupo-1)). Por otro lado, he configurando el entorno de trabajo en Git para parte del equipo (en sus dispositivos), introduciendo a algunos a la plataforma.

Mi foco principal ha sido la optimización y limpieza de la plantilla original, eliminando la dependencia de JavaScript (reduciéndolo a menos del 4%) para priorizar una implementación limpia basada en Bootstrap y HTML5, manteniendo scripts únicamente donde eran estrictamente necesarios:
- Dropdown 'Opciones' del navbar sólido.
- Directorio almacenando el panel de gestión admin: `/sb_admin_2`, ya que está más orientando a prácticas futuras. No obstante, nos hemos asegurado de que los requisitos de PoC de gestión de usuarios, reviews y tours sea estática.

Finalmente, he desarrollado plantillas desde cero para las páginas que no incluía la plantilla (`login`, `register`, `admin-login`, `profile`, `checkout`, `invoice`, `tour-details` y `packages`), estilizando algunas de ellas (`checkout`, `invoice`, `tour-details` y `packages`). También he asumido la responsabilidad de documentar el código en las carpetas `/html` y `/css`, diseñar la identidad gráfica (logos/favicons) y solucionar errores recurrentes en la navegación (en navbar y footer, errores tipográficos, etc.) introducidos durante el desarrollo.



| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Implementación de la estructura general de la web sin JS (limpieza de plantilla)](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624)  | [/html/index.html](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-c9f5db8ca708e7a00f0eb981560568a544afb798eaba34269168d6b432454438), [/html/contact.html](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-f93c9c8bb477b277c123ffb6e4873beb1bb27d074665981ba021d102a2761836), [/html/about.html](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-8dcf3fc487131a4a97fb09aeb2caa640f73721a717657f540c7a8b0af27f06e4), [/html/services.html](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-b3cdb78d46f6114635a059942cd1929ed7e12f45e5504f76a227ee2fb1654850), [/css/bootstrap.min.css](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-7c016081189f7a82e2bb0c47351cc3857fa33e62e2fb9f12158107a9809e31b5), [/css/style.css](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-1fc556f95754ee7e33d91044125c44bb9f750c99be4406756ffb27413adfcaf5), [template-credits.txt](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-eca02b1f0a6402ab40983d5958150c9cd4e3aa20cd2fbba24e9194ce63ce09b2) y multitud de imágenes en [/img](https://github.com/DWS-2026/project-grupo-1/commit/ef38f9760f3d98fc7dc184abaeb15b3ed71bb624#diff-295114c988f3d1a5037aba456693bd5ce8a023578939892b3bb556d8b92ce8b2)|
|2| [Implementación de la plantilla cruda de login (sin estilado temático)](https://github.com/DWS-2026/project-grupo-1/commit/6c2019f17fe04e66267b08f57ff6e4cda064dc1e)  | [/html/login.html](https://github.com/DWS-2026/project-grupo-1/commit/6c2019f17fe04e66267b08f57ff6e4cda064dc1e#diff-2bd5f6dbdc348f96a20f0d4a1a942687783b39c86907b8c9faf6671c66bc9582)   |
|3| [Implementación de la plantilla cruda de register (sin estilado temático)](https://github.com/DWS-2026/project-grupo-1/commit/6d7ef2ba05693c1cf212acde848244f19418f3bc)  | [/html/register.html](https://github.com/DWS-2026/project-grupo-1/commit/6d7ef2ba05693c1cf212acde848244f19418f3bc#diff-ec520d513723d69aac93fbe527b73b1093adcabe76c628934e5c42171fee1fbe)   |
|4| [Implementación de la plantilla base + estilizada de detalles de tour (le añadí la sección de reviews más tarde)](https://github.com/DWS-2026/project-grupo-1/commit/2928c62feb374cf49f3dd52a85c10d4582b56f55)  | [/html/tour-details.html](https://github.com/DWS-2026/project-grupo-1/commit/2928c62feb374cf49f3dd52a85c10d4582b56f55#diff-bc792cc27cc378666f815bce237a8e786be5b10ea0548ca1ababcd0c3403f291) y multitud de imágenes en [/img](https://github.com/DWS-2026/project-grupo-1/commit/2928c62feb374cf49f3dd52a85c10d4582b56f55#diff-b4d30a3ca61d2581aa7e39b0e0cf5669d3202f29a8336cb7de70bb09f57e6d42)   |
|5| [Adición de logo + nombre de empresa (diseño propio)](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a)  | [/html/about.html](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a#diff-8dcf3fc487131a4a97fb09aeb2caa640f73721a717657f540c7a8b0af27f06e4), [/html/contact.html](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a#diff-f93c9c8bb477b277c123ffb6e4873beb1bb27d074665981ba021d102a2761836), [/html/guides.html](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a#diff-f4a9948d0d64150be2d47a87a36f513bb5b7db80748817cd9adcf377615fc6b9), [/html/index.html](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a#diff-c9f5db8ca708e7a00f0eb981560568a544afb798eaba34269168d6b432454438), [/html/packages.html](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a#diff-6c37de3b954bc163769ad0f93b6f90d2e81a6ec49db8587a3ad81f251e2f92b3), [/html/profile.html](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a#diff-f0271321901161444964b89aa3750041d33ccfda3f0fe1963165e003463c3f6b) y [/img/logo_apex.png](https://github.com/DWS-2026/project-grupo-1/commit/f8efe0535dbe2e6dc55bd852a116cb90f356554a#diff-a09dc930a04d1f5b1d7c61bd6e94044f919b2fe19bb0d2c00f83d85071e05c3c)  |

---

#### **Alumno 3 – Javier Hernández Campano**

Me he encargado principalmente de la integración funcional y mejora del flujo de navegación de la aplicación, así como de la refactorización y corrección de errores en múltiples páginas del proyecto. Mi trabajo se ha centrado en asegurar una experiencia de usuario coherente, fluida y sin errores de navegación entre las distintas secciones de la web.

He integrado y unificado la navbar en todas las páginas, corrigiendo duplicidades, enlaces incorrectos y problemas de navegación. Además, he desarrollado y mejorado el flujo completo de compra, conectando correctamente el carrito con el proceso de checkout y la generación de la factura. También he creado e integrado la página de error 404 para gestionar rutas inexistentes.

Por otro lado, he realizado tareas de mantenimiento y limpieza del proyecto, reorganizando archivos obsoletos de la plantilla original, actualizando contenidos visuales (como imágenes de guías), corrigiendo textos y mejorando elementos de interfaz como botones y redirecciones. Finalmente, he documentado el funcionamiento general del proyecto mediante la integración del diagrama de flujo.

| Nº | Commits | Files |
|:--:|:--------|:------|
| 1  | [Refactor completo de la navbar e integración global](https://github.com/DWS-2026/project-grupo-1/commit/0b29350) | about.html, contact.html, guides.html, index.html, packages.html, profile.html, register.html, tour-details.html |
| 2 | [Cambio global del estilo: color principal a rojo](https://github.com/DWS-2026/project-grupo-1/commit/5d0de9f) | css/style.css, css/bootstrap.min.css, about.html, blog.html, contact.html, guides.html, index.html |
|3| [Activación del flujo de compra desde el carrito](https://github.com/DWS-2026/project-grupo-1/commit/0076964) | carrito.html |
| 4  | [Creación de la página de error 404](https://github.com/DWS-2026/project-grupo-1/commit/e1fdbcf) | 404.html |
| 5  | [Integración del diagrama de flujo y actualización del README](https://github.com/DWS-2026/project-grupo-1/commit/92a1de7) | README.md, Diagrama de flujo fase 1.png |


---

#### **Alumno 4 - Andrés Sánchez Nogales**

Me he encargado de traducir los distintos archivos de la página, así como de darles imágenes a estos. A su vez, completé aquellos archivos a los que les faltaba algún campo por cubrir. 

Primeramente me encargué de crear el carrito, el cual diseñé primero usando JavaScript, y una vez viendo que este no podía ser usado, diseñé otro (esta vez sin emplear JavaScript) empleando los botones requeridos en el archivo tour-details.html y luego creando y diseñando el archivo carrito.html. Eliminé alguna funcionalidad innecesaria en packages.html. 

También me encargué de solucionar diversos problemas que iban surgiendo a lo largo del diseño: botones o algunos dropdowns no funcionaban, redireccionamiento de botones erróneos, descolocamientos de textos o imágenes, etc.

Por último, redacté gran parte del README, añadiendo imágenes y descripciones de cada archivo de la web, siguiendo el flujo de funcionamiento.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Creación de un carrito para gestionar las compras(descartado)](https://github.com/DWS-2026/project-grupo-1/commit/3119a02e87639263196816fa396f108408459eef)  | [cart.js](https://github.com/DWS-2026/project-grupo-1/blob/main/html/carrito.html)   |
|2| [Nuevo carrito (sin js) y arreglos de reedireciones](https://github.com/DWS-2026/project-grupo-1/commit/2a5fac0010f3889b6bdc20b232353529cf1448e1)  | [carrito.html](https://github.com/DWS-2026/project-grupo-1/blob/main/html/carrito.html)   |
|3| [dashboard arreglada, imagenes packages ajustadas,  tour-details acabado](https://github.com/DWS-2026/project-grupo-1/commit/d0c307ff1b4d760c264121acc86d001926feaf09)  | [img](https://github.com/DWS-2026/project-grupo-1/tree/main/img)   |
|4| [arreglo invoice y carrito, decoración](https://github.com/DWS-2026/project-grupo-1/commit/f877cb3bbc59372740a4170b9efed9ac75692d97)  | [carrito.html](https://github.com/DWS-2026/project-grupo-1/blob/main/html/carrito.html)   |
|5| [Fotos de cada página añadidas + correcciones + descripciones](https://github.com/DWS-2026/project-grupo-1/commit/5278b92d5a436490f1e7707ac74d4648e65bd8e8)  | [README.md](https://github.com/DWS-2026/project-grupo-1/blob/main/README.md)   |

---

## 🛠 **Práctica 2: Web con HTML generado en servidor**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://youtu.be/NM7fwv_d4dY)**

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
   git clone https://github.com/DWS-2026/project-grupo-1.git
   cd project-grupo-1
   ```

2. **Instalar los requisitos previos**

   Antes de ejecutar la aplicación, es necesario tener instalado:

    - `Git`
    - `Java 21`
    - `Maven`
    - `MySQL`

   El proyecto está configurado como una aplicación Spring Boot con Maven y usa Java 21, Mustache, JPA, Spring Security y MySQL. Además, en la raíz del repositorio aparece `pom.xml`, por lo que la ejecución se realiza con Maven.

3. **Crear la base de datos**

   La aplicación está configurada para conectarse a una base de datos MySQL llamada `apex_project`. Por tanto, hay que crearla antes de arrancar la aplicación.

   ```sql
   CREATE DATABASE apex_project;
   ```

4. **Revisar la configuración de la base de datos**

   En el fichero `src/main/resources/application.properties` están definidos por defecto:

    - URL: `jdbc:mysql://localhost:3306/apex_project`
    - Usuario: `root`
    - Contraseña: `passwd`

   Si en tu equipo MySQL usa otras credenciales, debes modificar esas propiedades antes de ejecutar la aplicación.

5. **Compilar y ejecutar la aplicación**

   Una vez creada la base de datos y revisada la configuración, ejecuta:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   El proyecto incluye el plugin de Spring Boot para Maven y una clase principal `Application`, que es la que arranca la aplicación.

6. **Acceder a la aplicación**

   La aplicación está configurada para arrancar en el puerto `8443` y con `HTTPS`. Por tanto, una vez iniciada, se puede abrir en el navegador en:

   ```text
   https://localhost:8443
   ```

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin@apexexpeditions.com`, contraseña: `1234`
- **Usuario Registrado**: usuario: `luis@email.com`, contraseña: `1234`

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](src%2Fmain%2Fresources%2Fstatic%2Fimages%2Fdiagrama_BD.png)


> [El diagrama entidad-relación representa la estructura de la base de datos de la aplicación. En él se observan las entidades principales relacionadas con la gestión de usuarios, tours, reseñas, reservas, guías y notificaciones. Entre las relaciones más importantes se encuentran User 1:N Review y Tour 1:N Review, además de la relación entre usuarios y reservas, y entre reservas y tours. Cada entidad incluye sus atributos principales, como identificadores, fechas de creación, descripciones, valoraciones y estados de visibilidad.]

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/diagrama_BD .png)

> [Descripción opcional del diagrama y relaciones principales]

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - [Mario Ortiz Lopo]**

Me he encargado de la entidad `Tour` y de la entidad `Booking`, así como de la gestión del panel de administración y la integración de plantillas con Mustache. Resumido, mi trabajo ha abarcado las siguientes áreas:
 
* **Entidad Tour y panel de administración:** desarrollo completo del CRUD de tours desde el panel admin (`/admin/tours`, `/admin/tours/add`, `/admin/tours/edit`), incluyendo la gestión de imágenes asociadas a cada tour (subida y persistencia en base de datos), atributos extendidos del modelo y la lógica de inicialización de datos (`TourInitializer`).
* **Integración con Mustache:** refactorización completa de la interfaz de administrador para usar plantillas dinámicas con Mustache, incluyendo la extracción de componentes reutilizables (`sidebar`, `topbar`, `footer`, `logout-modal`) y la eliminación de código HTML repetido en todas las vistas admin.
* **Relación Tour–Reviews:** configuración de la relación entre las entidades `Tour` y `Review`, con su correspondiente lógica en servicios, repositorios y la vista de administración `tour-review.html`.
* **Paginación de tours:** implementación de paginación tanto en la vista pública de paquetes (`/packages`) como en la vista admin de tours, con los cambios necesarios en controladores, repositorio y servicio.
* **Interfaz de reservas:** añadida la vista de visualización de reservas del usuario (`/user/bookings`), con su modelo `Booking`, repositorio, servicio y enlace desde la barra de navegación.
* **Mejoras de UI/UX:** traducción completa de la interfaz de administrador al español, inclusión del favicon de admin en todas las vistas, adaptación dinámica de `packages.html` y `tour-details.html`, y perfil de administrador con topbar actualizado.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
| 1 | [Refactorización de interfaz admin (Mustache)](https://github.com/DWS-2026/project-grupo-1/commit/1c80e6ff1c871b427613cf0b712aad0055e92e94) | [sidebar.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/partials/sidebar.html), [topbar.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/partials/topbar.html), [tours.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/tours.html) |
| 2 | [Configurada la relación Tour–Reviews](https://github.com/DWS-2026/project-grupo-1/commit/471a4be9007657c810ddbaa7c8ce00d416407354) | [AdminWebController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/AdminWebController.java), [Tour.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/model/Tour.java), [Review.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/model/Review.java) |
| 3 | [Gestión de imágenes y persistencia](https://github.com/DWS-2026/project-grupo-1/commit/dfa03e986229e732a3fdf7981da45b08639e1bd9) | [TourInitializer.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/service/TourInitializer.java), [TourService.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/service/TourService.java), [tour-edit.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/tour-edit.html) |
| 4 | [Interfaz de visualización de Reservas](https://github.com/DWS-2026/project-grupo-1/commit/de01077217becbb87b367427aaa6a35bda351bb6) | [Booking.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/model/Booking.java), [BookingRepository.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/repository/BookingRepository.java), [user-bookings.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/user/user-bookings.html) |
| 5 | [Implementación de paginación de tours](https://github.com/DWS-2026/project-grupo-1/commit/e5fdd834a0127ca89d0dc95a2a78466e6ae00b10) | [TourRepository.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/repository/TourRepository.java), [WebController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/WebController.java), [packages.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/user/packages.html) |
---

#### **Alumno 2 - [Javier Hernández Campano]**


Mi principal responsabilidad dentro del proyecto ha sido el diseño, desarrollo e integración del sistema de reseñas de la aplicación. En concreto, me he encargado de definir y evolucionar la entidad Review y sus relaciones con User y Tour, así como de implementar la lógica necesaria para crear, editar, eliminar y visualizar reseñas tanto desde la parte de usuario como desde la parte administrativa.

Además, he trabajado en la integración completa de esta funcionalidad entre backend y frontend, desarrollando controladores, servicios, repositorios y vistas asociados a las reseñas. Entre las tareas realizadas destacan la creación de la página de detalle de tours con reseñas dinámicas, el formulario para añadir reseñas, la gestión de reseñas propias del usuario, la edición y eliminación de comentarios, la paginación de resultados y la visualización de valoraciones mediante estrellas.

Más allá del módulo de reseñas, también he participado en el desarrollo de la estructura general de la aplicación, especialmente en la creación y evolución de los controladores WebController y AdminWebController, que han permitido hacer funcional la navegación tanto en la zona de usuario como en la de administración. Asimismo, he colaborado en la construcción de la base de datos mediante la creación y definición de entidades principales del proyecto, en la adaptación de rutas y vistas HTML para integrarlas con Mustache, y en la configuración de distintos elementos necesarios para que la aplicación fuese navegable, coherente y operativa en su conjunto.
[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº | Commits                                                                                                                                                                                                                       | Files |
|:--:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------|
| 1  | [Implementación de la funcionalidad para añadir reseñas a los tours](https://github.com/DWS-2026/project-grupo-1/commit/8e0ff8b887c77885b8bf758180c2701965424917)                                                             | [ReviewController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/ReviewController.java)<br>[ReviewInitializer.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/service/ReviewInitializer.java)<br>[ReviewService.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/service/ReviewService.java)<br>[add-review.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/user/add-review.html) |
| 2  | [Implementación de un sistema dinámico de reseñas en la página de detalle de los tours](https://github.com/DWS-2026/project-grupo-1/commit/50227a3e9a30f67c6eafff12f79cc12474de6602)                                          | [WebController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/WebController.java)<br>[Review.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/model/Review.java)<br>[SecurityConfiguration.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/security/SecurityConfiguration.java)<br>[ReviewInitializer.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/service/ReviewInitializer.java)<br>[ReviewService.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/service/ReviewService.java)<br>[tour-details.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/user/tour-details.html) |
| 3  | [Implementación del sistema de gestión de reseñas del usuario](https://github.com/DWS-2026/project-grupo-1/commit/1babdeb3f1d7a645bb9c77d67ce9c8c340062761)                                                                   | [ReviewController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/ReviewController.java)<br>[WebController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/WebController.java)<br>[ReviewRepository.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/repository/ReviewRepository.java)<br>[ReviewService.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/service/ReviewService.java)<br>[navbar.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/partials/navbar.html)<br>[edit-review.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/user/edit-review.html)<br>[review-user.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/user/review-user.html) |
| 4  | [Creación del controlador AdminWebController en la zona de administración + login con credenciales + arreglo de errores y rutas](https://github.com/DWS-2026/project-grupo-1/commit/1579e7da3766b729827bf10f3d80b3d4298c82e2) | [AdminWebController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/AdminWebController.java)<br>[WebController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/WebController.java)<br>[application.properties](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/application.properties)<br>[footer.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/components/footer.html)<br>[logout-modal.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/components/logout-modal.html)<br>[sidebar.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/components/sidebar.html)<br>[topbar.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/components/topbar.html)<br>[admin_index.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/admin_index.html)<br>[index.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/admin/index.html)<br>[admin-login.html](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/resources/templates/user/admin-login.html) |
| 5 | [Creación del WebController y puesta en funcionamiento de la navegación web de usuario](https://github.com/DWS-2026/project-grupo-1/commit/db74943ff787e7d8683e44144a5d756c08d11081)                                          | [WebController.java](https://github.com/DWS-2026/project-grupo-1/blob/main/src/main/java/es/codeurjc/daw/library/controller/WebController.java) |
---



#### **Alumno 3 - [Pablo Sánchez Martín]**

Me he encargado de las entidades `User` y `Notification`, y de la implementación de Spring Security. Resumido, mi trabajo ha abarcado las siguientes áreas:

- Backend y vistas: desarrollo de la lógica y las interfaces de acceso (`/login`, `/register`, `/profile`, `/admin-login`), y de la sección del portal de administración para gestionar usuarios (`/admin/users`, `/admin/users/add`, `/admin/users/edit`, y los modales view y delete). También configuré los errores 403 y 404.

- Seguridad y control: implementación de cookies para mantener sesiones prolongadas, un sistema para bloquear el acceso a usuarios (atributo `boolean enabled` y modal `login-inactive`) y validación robusta de formularios (en los vinculados a mi entidad) tanto en frontend como backend.

- Interfaz de Usuario (UI/UX): diseño de ventanas modales para diversas acciones (`logout`, `login-inactive`, `terms-conditions` y `contact-wip`, que avisa de la falta de funcionalidad en `/contact`). Los modales de gestión de usuarios (view y delete) sirvieron como plantilla estándar para el resto del equipo.

- Sistema de notificaciones: creación de la entidad `Notification` para alertar automáticamente al administrador sobre la creación, edición o eliminación de existencias de cada entidad. Avisa tanto en caso de ser provocado por el propio usuario, o el administrador.

- Mejoras en navegación admin: integración de un menú dropdown de acceso rápido al panel de administración desde la web pública (sólo visible para admin). Por otro lado, en el portal admin, aporté dinamismo al topbar (con la foto y nombre del usuario actual), funcionalidad al menú para que admin pueda acceder a ver su perfil y cerrar sesión, y el diseño tanto del sidebar que enlaza a la gestión de cada entidad, como del landing page de admin (panel de control).


| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Implementación básica (y posteriormente transferida a un fichero distinto) de la inicialización de usuarios (tanto normales como admin) y generación de pfps default adaptadas al rol de cada usuario](https://github.com/DWS-2026/project-grupo-1/commit/7959f506b94dab726ae0ed31dda78e94ad92bef8)  | [DatabaseInitializer.java](https://github.com/DWS-2026/project-grupo-1/commit/7959f506b94dab726ae0ed31dda78e94ad92bef8#diff-27ca319e78faf0f9b138526aadb0392a04b27f4b21eb5efe9f239937dd3fef14)   |
|2| [Uso de CSRF y protección de contraseñas (bcrypt)](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499)  | [pom.xml](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-9c5fb3d1b7e3b0f54bc5c4182965c4fe1f9023d449017cece3005d3f90e8e4d8), [GlobalControllerAdvice.java](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-86ef4963b05c94bffe9778c30dd3f7155f7e4458027dbb71ff55600577978e08), [WebController.java](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-a486a472198187fdf67ccbae45968eb07fcd263c0158748771d579549ec72b91), [UserRepository.java](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-9759a4d6218c551e22ba60a440a50735559d0910777bd45277c5cbae6e28a2da), [RepositoryUserDetailsService.java](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-2361df66feacddd3d7644f0af82d9cb50e6687b2b3061d56ed6be649a0f6465d), [SecurityConfiguration.java](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-dc4755d7486bfbc5c0479dd942dab410201d81845b4939149c5d24cd5281f183), [DatabaseInitializer.java](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-27ca319e78faf0f9b138526aadb0392a04b27f4b21eb5efe9f239937dd3fef14), [application.properties](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-54eeffbae371fcd1398d4ca5e89a1b8118208b7bb2f8ddf55c1aa2f7d98ab136), [admin-login.html](https://github.com/DWS-2026/project-grupo-1/commit/2a3e757f1b5fc6d307161fabeee85cc30b9d6499#diff-ba8670837ec423cc5b6e9b7fea11b737a20e5c2040e78da298dd808757a1e65e)  |
|3| [Gestión de errores 403 y 404](https://github.com/DWS-2026/project-grupo-1/commit/6e98b1d7a05892e6b4bee8b76be7e99e3021c715)  | [pom.xml](https://github.com/DWS-2026/project-grupo-1/commit/6e98b1d7a05892e6b4bee8b76be7e99e3021c715#diff-9c5fb3d1b7e3b0f54bc5c4182965c4fe1f9023d449017cece3005d3f90e8e4d8), [CustomErrorController.java](https://github.com/DWS-2026/project-grupo-1/commit/6e98b1d7a05892e6b4bee8b76be7e99e3021c715#diff-faeb75e6d7f0aa105b123a10f93319a7a3545bd8ee51c60eb052bdf255021a9e), [403.html](https://github.com/DWS-2026/project-grupo-1/commit/6e98b1d7a05892e6b4bee8b76be7e99e3021c715#diff-947fd187fbf22f00c4fe881d2c633346fc5fee5f7f81f530ccbc43331925a2a9), [404.html](https://github.com/DWS-2026/project-grupo-1/commit/6e98b1d7a05892e6b4bee8b76be7e99e3021c715#diff-d790ae3f696efd84bbadcb218a2599008732ebf43c91eec917fd5ac30cca13eb)   |
|4| [Portal login funcional y opción de logout](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550)  | [GlobalControllerAdvice.java](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550#diff-86ef4963b05c94bffe9778c30dd3f7155f7e4458027dbb71ff55600577978e08), [WebController.java](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550#diff-a486a472198187fdf67ccbae45968eb07fcd263c0158748771d579549ec72b91), [SecurityConfiguration.java](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550#diff-dc4755d7486bfbc5c0479dd942dab410201d81845b4939149c5d24cd5281f183), [logout-modal.html](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550#diff-f5d6d374ac0597348228591cd0ab198434248c029b79142ce01e6b2f13ff220f), [navbar.html](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550#diff-f0323ba3441706a4018060c412cdc7fbb6205e83ebf16ff77fd6d2cd53d86657), [admin-login.html](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550#diff-ba8670837ec423cc5b6e9b7fea11b737a20e5c2040e78da298dd808757a1e65e), [login.html](https://github.com/DWS-2026/project-grupo-1/commit/3f6a0491ec1cb845b242b99fabf9a27df6cda550#diff-9f07546694da376e7301b28e339228f4a5c21891dcc9a537735b2cf0dec36782)   |
|5| [Validación backend en el formulario `/register`](https://github.com/DWS-2026/project-grupo-1/commit/ddd1feffcd3013a8756de2d399f27c2a5523124d)  | [WebController.java](https://github.com/DWS-2026/project-grupo-1/commit/ddd1feffcd3013a8756de2d399f27c2a5523124d#diff-a486a472198187fdf67ccbae45968eb07fcd263c0158748771d579549ec72b91), [UserRepository.java](https://github.com/DWS-2026/project-grupo-1/commit/ddd1feffcd3013a8756de2d399f27c2a5523124d#diff-9759a4d6218c551e22ba60a440a50735559d0910777bd45277c5cbae6e28a2da), [UserService.java](https://github.com/DWS-2026/project-grupo-1/commit/ddd1feffcd3013a8756de2d399f27c2a5523124d#diff-fc9c0748b93d18d6da425799c583b7069eabee943cf3aa00869902ea063caccf), [register.html](https://github.com/DWS-2026/project-grupo-1/commit/ddd1feffcd3013a8756de2d399f27c2a5523124d#diff-16be9745b1320d3207e53a324f3194e5d48299fc59e197fc5f94ddfeddba59d6)   |

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
