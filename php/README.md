# Alethana Books — versión PHP

Tienda de libros online. Esta es la migración a **PHP 8 + MySQL (PDO)** de la aplicación de escritorio JavaFX
que se encuentra en `src/main/java`. Usa **la misma base de datos** (`database/alethana_books.sql`),
con sus triggers y restricciones.

## Requisitos

- XAMPP (o cualquier servidor con **PHP 8.1 o superior** y la extensión `pdo_mysql`)
- MySQL / MariaDB

## Instalación

1. **Base de datos**: encender MySQL en XAMPP e importar el script desde la raíz del repositorio:
   ```bash
   mysql -u root < database/alethana_books.sql
   ```
   (o en phpMyAdmin: *Importar* → `database/alethana_books.sql`).
2. **Configuración**: los datos de conexión están en `php/config/config.php`
   (por defecto `root` sin contraseña en `localhost:3306`, como XAMPP).
3. **Ejecutar**, con cualquiera de estas dos opciones:
   - **Servidor integrado de PHP** (recomendado):
     ```bash
     C:\xampp\php\php.exe -S localhost:8000 -t php/public
     ```
     y abrir <http://localhost:8000/login.php>
   - **Apache de XAMPP**: copiar el repositorio dentro de `C:\xampp\htdocs\` y abrir
     <http://localhost/Alethana_Books/php/>

## Usuarios de prueba

| Rol           | Correo               | Contraseña                                   |
|---------------|----------------------|----------------------------------------------|
| Administrador | `admin@alethana.com` | la registrada en la BD (si no existe ningún admin se crea con `admin123`) |
| Cliente       | cualquier correo     | al iniciar sesión con un correo nuevo, la cuenta se registra sola |

Códigos de descuento: `CYBER20`, `PROMO10`, `LECTOR15`, `NAVIDAD25`, `BIENVENIDA5`.

## Funcionalidades

- Inicio de sesión y registro automático de clientes; acceso de administrador.
- Catálogo con categorías, búsqueda, banner y novedad.
- Recomendados aleatorios, últimos 10 añadidos, libros importados y biblioteca digital filtrable por formato.
- Carrito en sesión, códigos promocionales y 10% automático en e-books.
- Pasarela de pago simulada: Tarjeta, PSE y Efecty, con validaciones y un 90% de aprobación.
- Mi cuenta: datos de envío, historial de compras y descarga de los e-books comprados.
- Opiniones de clientes con calificación de 1 a 5 estrellas.
- Panel de administración: agregar, editar y eliminar libros (con subida de portada y archivo digital) y ver los pedidos.

## Estructura

```
php/
├── bootstrap.php          # autoload de clases, sesión y manejo de errores
├── config/config.php      # conexión a la base de datos
├── src/
│   ├── Modelo/            # Libro, LibroFisico, LibroDigital, Usuario, Carrito, Venta...
│   ├── Interfaces/        # Descargable, Validable
│   ├── Factory/           # LibroFactory          (patrón Factory Method)
│   ├── Strategy/          # Descuento*            (patrón Strategy)
│   ├── Persistence/       # ConexionBD + repositorios con PDO (consultas preparadas)
│   ├── Service/           # CatalogoService, UsuarioService, CarritoService
│   └── Util/              # Sesion y funciones de ayuda para las vistas
├── templates/             # encabezado, pie y componentes (tarjetas de libros)
├── descargables/          # archivos de e-books (fuera de public/: solo se bajan con descargar.php)
└── public/                # raíz web: una página .php por pantalla, css/, js/, imagenes/
```

### Equivalencias con la versión Java

| JavaFX (FXML + Controller)                 | PHP                          |
|--------------------------------------------|------------------------------|
| `Login.fxml` / `LoginController`           | `public/login.php`           |
| `alethana-books.fxml` / `InicioController` | `public/index.php`           |
| `header.fxml` / `HeaderController`         | `templates/header.php`       |
| `librosRe.fxml`                            | `public/recomendados.php`    |
| `UltimosAnadidos.fxml`                     | `public/ultimos.php`         |
| `LibrosImportados.fxml`                    | `public/importados.php`      |
| `LibrosDigitales.fxml`                     | `public/digitales.php`       |
| `Opiniones.fxml`                           | `public/opiniones.php`       |
| `Carrito.fxml`                             | `public/carrito.php`         |
| `Pago.fxml`                                | `public/pago.php`            |
| `Cuenta.fxml`                              | `public/cuenta.php`          |
| `admin.fxml` / `AdminController`           | `public/admin.php`           |
| `SesionActual`                             | `src/Util/Sesion.php` (`$_SESSION`) |
| `ImagenUtil` / `TarjetaUtil`               | `templates/componentes.php`  |

## Seguridad

- Todas las consultas usan **sentencias preparadas** de PDO, que evitan la inyección SQL.
- Todo el texto que se muestra pasa por `htmlspecialchars` (función `e()`), lo que evita ataques XSS.
- Los formularios POST llevan un **token CSRF**.
- Las contraseñas se comparan con su hash SHA-256 usando `hash_equals`. Es el mismo formato que usa la versión Java, por eso los usuarios existentes siguen funcionando.
- Solo se aceptan ciertas extensiones en las subidas de archivos. Los e-books se guardan fuera de la carpeta pública y únicamente los descarga quien los compró.
