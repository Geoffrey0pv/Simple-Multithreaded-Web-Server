# Servidor Web Multihilos en Java

Este proyecto es un **servidor web multihilos** desarrollado en Java. Es capaz de recibir solicitudes HTTP, procesarlas en hilos separados y servir archivos HTML e imágenes desde la carpeta `resources`. Soporta los métodos `GET` y maneja errores como `404 Not Found` cuando el archivo solicitado no existe.

## 🚀 Características
- Manejo de múltiples conexiones simultáneamente usando hilos.
- Servir archivos HTML, imágenes (`.jpg`, `.gif`) y otros tipos de contenido.
- Manejo de errores con respuestas HTTP adecuadas (`404 Not Found`, `405 Method Not Allowed`).
- Configuración sencilla utilizando Maven.

## ⚙️ Configuración y Ejecución
### **1️⃣ Requisitos Previos**
Asegúrate de tener instalado:
- **Java 11 o superior** ([Descargar JDK](https://adoptium.net/))
- **Apache Maven** ([Descargar Maven](https://maven.apache.org/download.cgi))

### **2️⃣ Clonar el Repositorio**
```sh
git clone https://github.com/tu-usuario/servidor-web-java.git
cd servidor-web-java
```

### **3️⃣ Compilar el Proyecto**
Ejecuta el siguiente comando para compilar el código:
```sh
mvn clean package
```

### **4️⃣ Ejecutar el Servidor**
```sh
java -cp target/classes org.example.ServidorWeb
```
Si el servidor inicia correctamente, verás el mensaje:
```
Servidor iniciado en el puerto 6780
```

### **5️⃣ Probar el Servidor**
Abre tu navegador y accede a los recursos:
```
http://localhost:6780/index.html
http://localhost:6780/rayo.jpg
http://localhost:6780/woody.gif
```
Si `index.html` está en `resources`, se mostrará en el navegador.

Sí la ruta solamente es `\` se lanzará un error de recurso no encontrado.

## 🛠️ Posibles Errores y Soluciones
### **❌ Error: Puerto en uso**
Si ves `java.net.BindException: Address already in use: bind`, significa que el puerto `6780` ya está ocupado. Para solucionarlo:
- Cambia el puerto en `ServidorWeb.java`.
- Verifica qué procesos están usando el puerto:
  ```sh
  netstat -ano | findstr :6780  # En Windows
  lsof -i :6780                 # En Linux/Mac
  ```

### **❌ Error: Archivo no encontrado (`404 Not Found`)**
- Asegúrate de que los archivos solicitados estén en `src/main/resources`.
- Verifica los logs del servidor para ver qué archivo está buscando.

👨‍💻 Desarrollado por [Geoffrey Pasaje](https://github.com/geoffrey0pv)

