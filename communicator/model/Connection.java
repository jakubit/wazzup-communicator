package proz.communicator.model;

/**
 * Klasa reprezntująca połączenie hosta z serwerem.
 */
public class Connection {

    /**
     * Adress IP hosta.
     */
    private String hostAddress;
    /**
     * Numer portu hosta.
     */
    private int hostPort;
    /**
     * Nazwa połączenia.
     */
    private String connectionName;

    /**
     * Konstruktor klasy.
     * @param hostAddress adres IP hosta.
     * @param hostPort numer portu hosta.
     * @param connectionName nazwa połączenia.
     */
    public Connection(String hostAddress, int hostPort, String connectionName) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.connectionName = connectionName;
    }

    /**
     * Getter numeru portu hosta.
     * @return numer portu host.
     */
    public int getHostPort() {
        return hostPort;
    }

    /**
     * Getter nazwy połączenia.
     * @return nazwa połączenia.
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * Getter adresu IP hosta.
     * @return adres IP hosta.
     */
    public String getHostAddress() {
        return hostAddress;
    }
}
