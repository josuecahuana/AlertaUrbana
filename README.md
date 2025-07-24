## Integrantes
- Cahuana Aguilar, Josué Mathías Miguel
- Huaman Apaza, Nelson Aluyis
- Arteaga Peña, Carlos Fabián

## Funcionalidad

Esta aplicación permite a los usuarios **crear reportes geolocalizados** de manera sencilla e intuitiva. Entre sus principales funcionalidades se encuentran:

- **Creación de reportes** con título, descripción y ubicación.
- **Selección de ubicación en mapa interactivo** utilizando la API de Google Maps.
- **Visualización de reportes en lista**, con opción de búsqueda por texto.
- **Vista detallada de cada reporte**, mostrando información completa.
- **Sincronización con base de datos remota (Supabase)** y almacenamiento local (Room).
- **Autenticación de usuarios** mediante Supabase Auth.
- **Actualización en tiempo real** de reportes utilizando el modelo **pub/sub (Realtime)** de Supabase.

---

## Arquitectura

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)**, promoviendo una separación clara de responsabilidades y facilitando la mantenibilidad y escalabilidad del código.

### Estructura general:
- `ui/`: Composables y pantallas de interfaz de usuario.
- `viewmodel/`: Manejo de estado de cada pantalla utilizando `ViewModel` y `StateFlow`.
- `domain/`: Modelos de negocio puros y lógica de dominio.
- `data/`: Repositorios, modelos DTO y configuración de Room y Supabase.

### Tecnologías utilizadas:
- **Jetpack Compose** para el diseño de UI declarativa.
- **Room** para persistencia de datos local.
- **Supabase** para autenticación, base de datos remota y eventos en tiempo real.
- **Google Maps API** para selección de ubicación en un mapa.
- **Moshi** para serialización y deserialización de datos JSON.

## Cómo usar

1. **Crear una cuenta:**
   - Si no tienes una cuenta, selecciona la opción de registro.
   - Ingresa un correo electrónico válido y una contraseña segura.

2. **Verificar el correo:**
   - Revisa tu bandeja de entrada y haz clic en el enlace de verificación enviado por Supabase.
   - Es necesario completar este paso para poder iniciar sesión correctamente.

3. **Iniciar sesión:**
   - Una vez verificado el correo, regresa a la aplicación.
   - Inicia sesión con tu correo y contraseña registrados.

>  Nota: Solo los usuarios con correo verificado pueden acceder a la funcionalidad principal de la aplicación.
