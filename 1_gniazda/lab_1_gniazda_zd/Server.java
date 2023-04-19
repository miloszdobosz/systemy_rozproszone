package lab_1_gniazda_zd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Server {
    InetAddress address = InetAddress.getLoopbackAddress();
    static int portNumber = 12345;
    ArrayList<String> messagesTCP = new ArrayList<>();
    ArrayList<DatagramPacket> messagesUDP = new ArrayList<>();

    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server::manageTCP).start();
//        new Thread(server::manageUDP).start();
        System.out.println("SERVER READY!");
    }

    void manageTCP() {
        ServerSocket serverSocketTCP = null;

        try {
            // create socket
            serverSocketTCP = new ServerSocket(portNumber);

            // manage TCP
            while (true) {

                // accept client
                Socket clientSocket = serverSocketTCP.accept();

                // communication from client
                new Thread(() -> readFromClientTCP(clientSocket)).start();

                // communication to client
                new Thread(() -> writeToClientTCP(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocketTCP != null) {
                try {
                    serverSocketTCP.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void manageUDP() {
        DatagramSocket serverSocketUDP = null;

        try {
            // create socket
            serverSocketUDP = new DatagramSocket(portNumber);

            // manage UDP
            while (true) {

//                // accept client
//                Socket clientSocket = serverSocketUDP.connect();

//                // communication from client
//                new Thread(() -> readFromUDP(serverSocketUDP)).start();
//
//                // communication to client
//                new Thread(() -> writeToClientUDP(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocketUDP != null) {
                serverSocketUDP.close();
            }
        }
    }

    void readFromClientTCP(Socket clientSocket) {
        int id = clientSocket.getPort();
        System.out.println("TCP client connected from port " + id);

        try {
            // in stream
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                // read msg, send response
                String message = id + ": " + fromClient.readLine();
                System.out.println(message);
                messagesTCP.add(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeToClientTCP(Socket clientSocket) {
        try {
            // out stream
            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);

            int atMessage = 0;

            while (true) {
                while (atMessage < messagesTCP.size()) {
                    toClient.println(messagesTCP.get(atMessage));
                    atMessage++;
                }
                sleep(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void readFromUDP(DatagramSocket socket) {
        try {
            while(true) {
                // MEMORY LEAK!
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                messagesUDP.add(receivePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeToClientUDP(DatagramSocket clientSocket) {
        try {
            int id = clientSocket.getPort();
            int atMessage = 0;

            while (true) {
                while (atMessage < messagesUDP.size()) {
                    DatagramPacket message = messagesUDP.get(atMessage);
                    int port = message.getPort();
                    if (port == id) continue;

                    byte[] data = message.getData();
                    clientSocket.send(new DatagramPacket(data, data.length, address, port));

                    atMessage++;
                }
                sleep(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}