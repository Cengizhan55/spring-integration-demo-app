package org.example;

import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 3456;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("BT Entegrasyon TCP server dinleniyor, port: " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept(); // Client bağlanana kadar bloklar
            System.out.println("Client bağlandı: " + clientSocket.getInetAddress());

            new Thread(() -> handleClient(clientSocket)).start(); // Her client için ayrı thread
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String line;
            while ((line = in.readLine()) != null) { // \r\n ile biten mesajları okur
                System.out.println("Gelen mesaj: " + line);
            }

            System.out.println("Bağlantı kapandı: " + socket.getInetAddress());

        } catch (IOException e) {
            System.err.println("Hata: " + e.getMessage());
        }
    }
}
