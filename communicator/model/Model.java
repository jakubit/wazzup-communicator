package proz.communicator.model;

import proz.communicator.controller.Controller;

import java.util.ArrayList;
import java.util.List;


/**
 * Klasa Modelu w programie. Zawiera logikę programu.
 */
public class Model {

    /**
     * Serwer nasłuchujący wiadomości.
     */
    private MessageListener messageListener;

    /**
     * Obiekt służący do nawiązywania połączenia i wysyłania wiadomości.
     */
    private MessageTransmitter messageTransmitter;

    /**
     * Lista nawiązanych połączeń.
     */
    private List<Connection> connections;

    /**
     * Numer portu serwera nasłuchującego wiadomości.
     */
    private int serverPort;

    /**
     * Wskazanie na kontroler programu.
     */
    private Controller controller;

    /**
     * Konstruktor klasy Model.
     * @param controller kontroler programu sterujący modelem.
     */
    public Model(Controller controller) {
        this.controller = controller;
        connections = new ArrayList<>();
    }

    /**
     * Rozpoczyna działanie serwera nasłuchującego wiadomości.
     */
    public void startServerListening() {
        messageListener = new MessageListener(controller, serverPort);
        messageListener.start();
    }

    /**
     * Kończy działanie serwera nasłuchującego wiadomości.
     */
    public void stopServerListening() {

        messageListener.setListenFlag(false);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messageListener.stopListening();

        try {
            messageListener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messageListener = null;
    }

    /**
     * Setter numeru portu serwera.
     * @param port numer portu serwera.
     */
    public void setServerPort(int port) {
        serverPort = port;
    }

    /**
     * Getter listy nawiązanych połączeń.
     * @return lista połączeń.
     */
    public List<Connection> getConnections() {
        return connections;
    }


    /**
     * Dodaje nowe połączenie do listy połączeń.
     * @param hostAddress adres IP hosta.
     * @param hostPort numer portu hosta.
     * @param connectionName nazwa połączenia.
     */
    public void addNewConnection(String hostAddress, int hostPort, String connectionName) {
        connections.add(new Connection(hostAddress, hostPort, connectionName));
    }


    /**
     * Zwraca połączanie o zadanej nazwie z listy nawiązanych połączeń.
     * @param connectionName nazwa połączenia.
     * @return połączenie lub null, gdy nie ma połączenia o zadanej nazwie.
     */
    public Connection getConnection(String connectionName) {
        for(Connection connection : connections) {
            if (connection.getConnectionName().equals(connectionName))
                return connection;
        }
        return null;
    }

    /**
     * Tworzy obiekt klasy Message z zadanymi argumentami.
     * Tworzy obiekt klasy MessageTransmiter i z jego pomocą wysysła obiekt klasy Message.
     * @param message treść wiadomości.
     * @param connection połączenie, do którego zostanie skierowana wiadomość.
     */
    public void sendMessage(String message, Connection connection) {
        String hostAddress = connection.getHostAddress();
        String connectionName = connection.getConnectionName();
        int port = connection.getHostPort();

        Message messageToSend = new Message(message, connectionName);
        messageTransmitter = new MessageTransmitter(messageToSend, hostAddress, port);
        messageTransmitter.start();
    }
}
