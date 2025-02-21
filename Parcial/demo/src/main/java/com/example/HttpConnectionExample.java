package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class HttpConnectionExample {

    private static final int PORT = 36000; // Puerto en el que el servidor escuchará las conexiones

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor HTTP en puerto " + PORT + "...");
            boolean escucha = true;
            while (escucha) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String requestLine = in.readLine();
                    if (requestLine == null)
                        continue;
                    System.out.println("Solicitud recibida: " + requestLine);

                    if (requestLine.startsWith("GET /compreflex?comando=")) {
                        String query = requestLine.split(" ")[1];
                        System.out.println(query);
                        String comando = URLDecoder.decode(query.split("=")[1], "UTF-8");
                        Object result = ejecutarComando(comando);

                        // Enviar respuesta al cliente
                        out.println("HTTP/1.1 200 OK");
                        out.println("Content-Type: text/plain");
                        out.println();
                        out.println("Resultado: " + result);
                    } else {
                        // Respuesta para solicitudes no reconocidas
                        out.println("HTTP/1.1 400 Bad Request");
                        out.println("Content-Type: text/plain");
                        out.println();
                        out.println("Solicitud no reconocida");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object ejecutarComando(String comando) {
        try {
            String[] partes = comando.split(",");
            String clase = partes[0].substring(partes[0].indexOf('(') + 1);
            String metodoNombre = partes[1];
            List<Class<?>> paramTypes = new ArrayList<>();
            List<Object> paramValues = new ArrayList<>();

            for (int i = 2; i < partes.length; i += 2) {
                String tipo = partes[i];
                String valor = partes[i + 1];
                switch (tipo) {
                    case "int":
                        paramTypes.add(int.class);
                        paramValues.add(Integer.parseInt(valor));
                        break;
                    // Agregar más tipos según sea necesario
                }
            }

            Class<?> clazz = Class.forName(clase);
            Method method = clazz.getMethod(metodoNombre, paramTypes.toArray(new Class[0]));
            return method.invoke(null, paramValues.toArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al ejecutar el comando";
        }
    }

    public static void bubbleSort(int arr[]) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // Intercambiar arr[j] y arr[j+1]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
}