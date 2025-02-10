package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public final class ServidorWeb {
    public static void main(String argv[]) throws Exception {
        // Establece el número de puerto
        int puerto = 6780;

        // Crea el socket de servidor para escuchar conexiones
        ServerSocket serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor iniciado en el puerto " + puerto);

        // Procesando las solicitudes HTTP en un ciclo infinito
        while (true) {
            // Escuchando solicitudes de conexión TCP
            Socket socket = serverSocket.accept(); // Espera una conexión
            System.out.println("Nueva conexión aceptada desde: " + socket.getInetAddress());

            // Crea un nuevo hilo para manejar la solicitud
            SolicitudHttp solicitud = new SolicitudHttp(socket);
            Thread hilo = new Thread(solicitud);
            hilo.start(); // Inicia el hilo
        }
    }
}

final class SolicitudHttp implements Runnable {
    private Socket socket;

    public SolicitudHttp(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String linea = reader.readLine();
            if (linea == null || linea.isEmpty()) {
                socket.close();
                return;
            }

            StringTokenizer partesLinea = new StringTokenizer(linea);
            String metodo = partesLinea.nextToken(); // Método HTTP (ej. GET)
            String recurso = partesLinea.nextToken(); // Recurso solicitado (ej. /index.html)

            if (!metodo.equals("GET")) {
                enviarError(os, 405, "Método no permitido");
                return;
            }

//            if (recurso.equals("/")) {
//                recurso = "/index.html"; // Página por defecto
//            }

            String nombreArchivo = recurso.substring(1);
            File file = new File(getClass().getClassLoader().getResource(nombreArchivo).toURI());

            String lineaDeEstado;
            String lineaHeader;

            if (file.exists() && !file.isDirectory()) {
                InputStream archivoStream = new FileInputStream(file);
                lineaDeEstado = "HTTP/1.0 200 OK\r\n";
                lineaHeader = "Content-Type: " + contentType(nombreArchivo) + "\r\n\r\n";

                os.write(lineaDeEstado.getBytes());
                os.write(lineaHeader.getBytes());
                enviarBytes(archivoStream, os);
                archivoStream.close();
            } else {
                lineaDeEstado = "HTTP/1.0 404 Not Found\r\n";
                lineaHeader = "Content-Type: text/html\r\n\r\n";

                os.write(lineaDeEstado.getBytes());
                os.write(lineaHeader.getBytes());
                os.write("<html><body><h1>404 - Archivo no encontrado</h1></body></html>".getBytes());
            }

            os.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarBytes(InputStream fis, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
        os.flush();
        fis.close();
    }

    private static void enviarError(OutputStream os, int codigo, String mensaje) throws IOException {
        PrintWriter writer = new PrintWriter(os, true);
        writer.println("HTTP/1.0 " + codigo + " " + mensaje);
        writer.println("Content-Type: text/html\r\n");
        writer.println("\r\n");
        writer.println("<html><body><h1>" + codigo + " " + mensaje + "</h1></body></html>");
        writer.flush();
    }

    private static String contentType(String nombreArchivo) {
        if (nombreArchivo.endsWith(".htm") || nombreArchivo.endsWith(".html")) {
            return "text/html";
        }
        if (nombreArchivo.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if (nombreArchivo.endsWith(".gif")) {
            return "image/gif";
        }
        if (nombreArchivo.endsWith(".css")) {
            return "text/css";
        }
        if (nombreArchivo.endsWith(".js")) {
            return "application/javascript";
        }
        return "application/octet-stream";
    }
}
