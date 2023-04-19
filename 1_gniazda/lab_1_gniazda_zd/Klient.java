package lab_1_gniazda_zd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;

public class Klient {
    public static void main(String[] args) throws IOException {
        System.out.println("CLIENT READY!");
        String hostName = "localhost";
        int portNumber = 12345;
        Socket socketTCP = null;
        DatagramSocket socketInUDP = null;
        DatagramSocket socketOutUDP = null;

        try {
            // create socket
            socketTCP = new Socket(hostName, portNumber);
//            socketInUDP = new DatagramSocket(portNumber);
//            socketOutUDP = new DatagramSocket(portNumber);

            // in & out streams
            PrintWriter toServer = new PrintWriter(socketTCP.getOutputStream(), true);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            new Thread(() -> {
                while (true) {
                    try {
                        // send msg
                        toServer.println(input.readLine());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while(true) {
                // read response
                System.out.println(fromServer.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socketTCP != null){
                socketTCP.close();
            }
        }
    }
}
