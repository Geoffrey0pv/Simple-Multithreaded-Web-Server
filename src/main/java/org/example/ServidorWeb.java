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
    final static String CRLF = "\r\n";
    private Socket socket;

    // Constructor
    public SolicitudHttp(Socket socket) {
        this.socket = socket;
    }

    // Implementa el método run() de la interface Runnable.
    public void run() {
        try {
            proceseSolicitud();
        } catch (Exception e) {
            System.out.println("Error procesando la solicitud: " + e.getMessage());
        }
    }

    private void proceseSolicitud() throws Exception {
        // Referencia al stream de salida del socket.
        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);

        // Referencia y filtros para el stream de entrada.
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // Recoge y muestra las líneas de header.
        String linea;
        StringTokenizer tokenizer;
        String nombre = "";

        while ((linea = reader.readLine()) != null && !linea.isEmpty()) {

            tokenizer = new StringTokenizer(linea, CRLF);
            String method = tokenizer.nextToken();

            if (method.equals("GET")) {
                nombre = "." + tokenizer.nextToken();
                System.out.println(nombre);
                break;
            }
        }

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(nombre);

        if (inputStream != null) {
            File file = new File(ClassLoader.getSystemResource(nombre).toURI());
            long fileSize = file.length();
            var outRequest = new BufferedOutputStream(socket.getOutputStream());
            enviarString("HTTP/1.0 200 OK" + CRLF, outRequest);
            enviarString("Content-Type: text/html" + CRLF, outRequest);
            enviarString("Content-Length: " + fileSize + CRLF, outRequest);
            enviarString(CRLF, outRequest);
            enviarBytes(inputStream, out);

            // Cierra los streams y el socket.
            writer.close();
            reader.close();
            socket.close();

        }
    }

    private static void enviarString(String line, OutputStream os) throws Exception {
        os.write(line.getBytes(StandardCharsets.UTF_8));
    }
    private static void enviarBytes(InputStream fis, OutputStream os) throws Exception {
        // Construye un buffer de 1KB para guardar los bytes cuando van hacia el socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copia el archivo solicitado hacia el output stream del socket.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
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
        return "application/octet-stream";
    }
}