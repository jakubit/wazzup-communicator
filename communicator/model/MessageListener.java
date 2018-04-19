package proz.communicator.model;

import javafx.application.Platform;
import proz.communicator.controller.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Klasa serwera nasłuchującego przychodzacych wiadomośći.
 */
public class MessageListener extends Thread {

    /**
     * Numer portu serwera.
     */
    private int port;

    /**
     * Socket serwera.
     */
    private ServerSocket serverSocket;

    /**
     * Flaga mówiąca o tym czy serwer ma nadal nasłuchiwać wiadomości.
     */
    private boolean listenFlag = true;

    /**
     * Wskazanie na kontroler programu.
     */
    private Controller controller;

    /**
     * Konstrukotr klasy.
     * @param controller kontroler serwera
     * @param port numer portu, na którym ma słuchać serwer.
     */
    public MessageListener(Controller controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    /**
     * Rozpoczyna nasłuchiwananie wiadomości.
     */
    @Override
    public void run() {
        Socket clientSocket;

        try {
            serverSocket = new ServerSocket(port);
            while (listenFlag) {

                clientSocket = serverSocket.accept();
                if (clientSocket != null) {

                    // Host has just connected to the server.
                    try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {

                        try {
                            final Message message = (Message) inputStream.readObject();   // Received Message object.
                            // Tell controller to update view - write message.
                            Platform.runLater(() -> controller.write(message));

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException ee) {
                            ee.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    /**
     * Setter dla flagi nasłuchiwania. Flaga ustawiona na false wyłącza nasłuchiwanie serwera.
     * @param bool wartość flagi.
     */
    public void setListenFlag(boolean bool) {
        listenFlag = bool;
    }


    /**
     * Zatrzumuje działanie serwera, zamyka jego socket.
     */
    public void stopListening() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
