package proz.communicator.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Klasa służąca do nawiązywania połączenia z serwerem hosta i wysyłania mu wiadomości.
 * Dziedziczy po klasie Thread, aby wiadomości mogły być wysyłane w odzielnych wątkach.
 */
public class MessageTransmitter extends Thread {

    /**
     * Adres IP hosta.
     */
    private String hostAddress;
    /**
     * Wiadomość do wysłania.
     */
    private Message message;
    /**
     * Numer portu hosta.
     */
    private int port;


    /**
     * Konstruktor klasy.
     * @param message wiadomość do wysłania
     * @param hostAddress adres IP hosta
     * @param port numer portu hosta
     */
    public MessageTransmitter(Message message, String hostAddress, int port) {
        this.message = message;
        this.hostAddress = hostAddress;
        this.port = port;
    }

    /**
     * Wysyła wiadomość do hosta.
     */
    @Override
    public void run() {
        try {
            Socket socket = new Socket(hostAddress, port);

            try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                outputStream.writeObject(message);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
