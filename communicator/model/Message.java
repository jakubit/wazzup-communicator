package proz.communicator.model;

import java.io.Serializable;


/**
 * Klasa reprezntująca wiadomość. Implementuje interfejs Serializable, aby możliwa była serializacja obiektu przed wysłaniem.
 */
public class Message implements Serializable {

    /**
     * treść wiadomości.
     */
    private final String messageText;

    /**
     * Nazwa połączenia, do którego skierowana ma być wiadomość.
     */
    private final String connectionName;

    /**
     * Konstruktor klasy.
     * @param messageText treść wiadomości.
     * @param connectionName nazwa połączenia.
     */
    public Message(String messageText, String connectionName) {
        this.messageText = messageText;
        this.connectionName = connectionName;
    }

    /**
     * Getter nazwy połączenia.
     * @return nazwa połączenia
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * Getter treści wiadomości.
     * @return treść wiadomości.
     */
    public String getMessageText() {
        return messageText;
    }
}
