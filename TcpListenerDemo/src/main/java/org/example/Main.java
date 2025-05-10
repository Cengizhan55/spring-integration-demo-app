package org.example;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        String serverIp = "127.0.0.1"; // TCP server IP
        int serverPort = 3456;        // TCP server port

        Socket socket = null;
        BufferedReader in = null;
        OutputStream out = null;

        try {
            socket = new Socket(serverIp, serverPort);  // Sunucuya bağlan
            System.out.println("TCP sunucuya bağlandı: " + serverIp + ":" + serverPort);

            // Gelen mesajları okuyacak input stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Mesaj gönderecek output stream
            out = socket.getOutputStream();
            out.write("HELLO\r\n".getBytes());  // Sunucuya ilk mesaj gönderebiliriz

            // Sunucudan gelen mesajları sürekli dinle
            String line;
            while (true) {
                if ((line = in.readLine()) != null) {
                    System.out.println("Gelen mesaj: " + line);
                }
            }

        } catch (Exception e) {
            System.err.println("TCP client hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Bağlantıyı kapatmak istemiyorsanız, socket'i burada kapatma.
            // socket.close(); // Bu satır bağlantının kapanmasına sebep olur.
        }
    }
}
