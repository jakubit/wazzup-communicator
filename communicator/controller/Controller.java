package proz.communicator.controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import proz.communicator.model.Connection;
import proz.communicator.model.Message;
import proz.communicator.model.Model;


import java.util.List;


/**
 * Klasa kontrolera. Stanowi połączenie między Widokiem a Modelem.
 * Implementuje interfejs WritableGUI
 */
public class Controller implements WritableGUI {

    /**
     * Obiekt model, przechowuje dane oraz udostepnia metody dostepu do danych.
     */
    private Model model = new Model(this);
    /**
     * Flaga oznaczająca czy opcja wysyłania wiadomości za pomocą klawisza Enter jest aktwyna.
     */
    private boolean sendWithEnterKey = false;


    //region FXML Variables
    /**
     * Pole tekstowe do wprowadzania numeru portu hosta, z którym chcemy się połączyć.
     */
    @FXML
    private TextField hostPortInput;

    /**
     * Pole tekstowe do wprowadzania adresu IP hosta, z którym chcemy się połączyć.
     */
    @FXML
    private TextField hostAddressInput;

    /**
     * Pole tekstowe do wprowadzania nazwy połączenia, które nawiązujemy.
     */
    @FXML
    private TextField connectionNameInput;

    /**
     * Pole tekstowe do wprowadzania numeru portu, na kótrym nasłuchujemy wiadomości.
     */
    @FXML
    private TextField portInput;

    /**
     * Pole tekstowe do wprowadzania wiadomości.
     */
    @FXML
    private TextArea messageInput;

    /**
     * Pole tekstowe do wyświetlania wiadomości.
     */
    @FXML
    private TextArea messageOutput;

    /**
     * Kolekcja otwartych kart wiadmości.
     */
    @FXML
    private TabPane tabPane;

    /**
     * Przycisk do włączania i wyłączania opcji wysyłania wiadomości przy pomocy klawisza Enter.
     */
    @FXML
    private ToggleButton sendWithEnterToggleButton;

    /**
     * Przycisk do wysyłania wiadomości.
     */
    @FXML
    private Button sendButton;

    /**
     * Przycisk do włączania nasłuchiwania wiadomości.
     */
    @FXML
    private Button listenButton;

    /**
     * Przycisk do zatrzymania nasłuchiwania wiadomości.
     */
    @FXML
    private Button stopButton;

    /**
     * Wskazuje na obecnie aktywna kartę wiadomości.
     */
    private Tab selectedTab;

    //endregion

    //region Button Actions

    /**
     * Wykonuje się przy kliknieciu przycisku Connect.
     * Pobiera dane połączenia i sprawdza ich poprawność.
     * Aktualizuje Model o nowe połączenie.
     * Otwiera nową kartę wiadomości w Widoku.
     */
    @FXML
    private void connectButtonClicked() {

        /*
        1. Get hostAddress, hostPort, connectionName
        2. Validate data: is empty? is port proper?
        3. Set model: add new connection, first check if there is an existing one (check it by connectionName)
        4. Open new tab with connection name
         */

        // AD 1.
        String hostAddress, hostPort, connectionName;

        hostAddress = hostAddressInput.getText();
        hostPort = hostPortInput.getText();
        connectionName = connectionNameInput.getText();

        // AD 2.
        if (!validateConnectionInputs(hostAddress, hostPort, connectionName)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Connection properties are incorrect!");
            alert.showAndWait();
            return;
        }

        // Inputs are ok!
        // AD 3. Check if connection already exists
        if (checkIfConnectionAlreadyExists(connectionName)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("This connection is already established!");
            alert.showAndWait();
            return;
        }

        // There is not connection like this, create a new one.
        // AD 3. Set model
        model.addNewConnection(hostAddress, Integer.parseInt(hostPort), connectionName);

        // AD 4. Open new tab with connection name
        Tab newTab = getTab(connectionName);
        if (newTab == null)
            openNewTab(connectionName);
        else
            enableInput();
    }

    /**
     * Wykonuje się przy kliknieciu przycisku Listen.
     * Pobiera numer portu z Widoku. Sprawdza jego poprawność.
     * Ustawia w Modelu numer portu serwera.
     * Każe Modelowi rozpocząć działanie serwera.
     * Deaktywuje przycisk Listen i akrywuje Stop.
     */
    @FXML
    private void listenButtonClicked() {

        /*
        1. Get port
        2. Validate in controller
        3. Set Model with port
        4. Start Listening: model.startServerListening()
        5. Update view: disable listen button and port input, enable stop button.
         */

        // AD 1. Get port
        String port = portInput.getText();

        // AD 2. Validate, is empty? port >= 2000? is an integer?
        if(!validatePortNumber(port)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Port number is incorrect!");
            alert.setContentText("Port number must be an integer, greater than 2000 or equal!");
            alert.showAndWait();
            return;
        }

        // Port number is ok.
        // AD 3. Set Model
        model.setServerPort(Integer.parseInt(port));

        // AD 4. Start listening
        model.startServerListening();

        // AD 5.
        portInput.setEditable(false);
        listenButton.setDisable(true);
        stopButton.setDisable(false);
    }

    /**
     * Wykonuje się przy kliknieciu przycisku Stop.
     * Każe Modelowi zakończyć nasłuchiwanie.
     * Deaktywuje przycisk Stop i aktywuje Listen.
     */
    @FXML
    private void stopButtonClicked() {

        model.stopServerListening();

        portInput.setEditable(true);
        listenButton.setDisable(false);
        stopButton.setDisable(true);
    }

    /**
     * Wykonuje się przy kliknieciu przycisku Send.
     * Pobiera wiadomosc z Widoku.
     * Pobiera z Modelu dane połączenia, w zależności od tego, która karta jest aktywna.
     * Każe Modelowi wysłać wiadomość do danego hosta.
     * Aktualizuje Widok o treść wysłanej wiadomości.
     * Czyści pole tekstowe do wprowadzania wiadomości.
     */
    @FXML
    private void sendButtonClicked() {

        /*
        0. Get message from input.
        1. Get connection settings from model, depending on which tab is active
        2. Tell model to send message to the host.
        3. Update proper textField.
        4. Clear input.
         */

        // AD 0. Get message.
        String message;
        message = messageInput.getText();
        if (message.isEmpty())
            return;

        // AD 1. Get settings
        String connectionName = selectedTab.getText();

        Connection connection = model.getConnection(connectionName);

        // AD 2.
        model.sendMessage(message, connection);

        // AD 3.
        printOutputMessage(message);

        // AD 4.
        messageInput.clear();
    }

    /**
     * Wykonuje się przy kliknieciu przycisku Send With Enter.
     * Ustawia flagę opcji wysyłania wiadomości za pomocą przycisku Enter.
     */
    @FXML
    private void sendWithEnterToggleButtonClicked() {
        if(sendWithEnterToggleButton.isSelected())
            sendWithEnterKey = true;
        else
            sendWithEnterKey = false;
    }

    /**
     * Wykonuje się przy przełączeniu karty wiadomości.
     * @param event zawiera informację, która karta jest teraz aktywna.
     */
    @FXML
    private void selectedTabChanged(Event event) {

        Tab tab = (Tab)event.getSource();

        if(tab.isSelected()) {
            selectedTab = tab;
            if (checkIfConnectionAlreadyExists(tab.getText()))
                enableInput();
            else
                disableInput();

            selectMessageOutput();
        }
    }

    /**
     * Wykonuje się przy kliknieciu klawisza, gdy aktywne jest pole wprowadzania wiadomości.
     * @param event zawiera informację, który klawisz został wciśniety.
     */
    @FXML
    private void enterKeyPressed(Event event) {

        if (sendWithEnterKey && (((KeyEvent)event).getCode() == KeyCode.ENTER))
        {
            messageInput.deleteText(messageInput.getLength() - 1 , messageInput.getLength());
            sendButtonClicked();
        }
    }

    //endregion

    //region Helpers

    /**
     * Otwiera w Widoku nową kartę.
     * @param name nazwa nowej karty.
     * @return zwraca nowo utworzoną kartę.
     */
    private Tab openNewTab(String name) {

        Tab newTab = new Tab(name);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMinWidth(0.0);
        anchorPane.setPrefHeight(0.0);
        anchorPane.setPrefHeight(180.0);
        anchorPane.setMinWidth(200.0);

        TextArea textArea = new TextArea();
        textArea.setLayoutX(12.0);
        textArea.setLayoutY(8.0);
        textArea.setPrefHeight(150.0);
        textArea.setPrefWidth(331.0);

        anchorPane.getChildren().add(textArea);

        newTab.setContent(anchorPane);

        newTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                selectedTabChanged(event);
            }
        });

        newTab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                deleteConnection( ((Tab)event.getSource()).getText() );
            }
        });

        tabPane.getTabs().add(newTab);

        return newTab;
    }

    /**
     * Wybiera pole tekstowe do wyświetlenia wiadomości, w zależności od tego, która karta jest aktywna.
     */
    private void selectMessageOutput(){

        AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
        messageOutput = (TextArea) anchorPane.getChildren().get(0);
    }

    /**
     * Wypisuje wiadomość w Widoku.
     * @param message wiadomość do wyświetlenia.
     */
    public void write(Message message) {

        String connectionName = message.getConnectionName();
        String messageText = message.getMessageText();

        TextArea output = getTextAreaFromTab(connectionName);

        if (output == null) {
            // There is not such tab. Create a new one.
            openNewTab(connectionName);
            getTextAreaFromTab(connectionName).appendText(connectionName + ": \t" + messageText + "\n");
        } else {
            // There is connection like this, tab also. Write message in the proper tab.
            output.appendText(connectionName + ": \t" + messageText + "\n");
        }
    }

    /**
     * Sprawdza czy podany String jest liczbą całkowitą.
     * @param s liczba do sprawdzenia.
     * @return true jesli zadany String jest liczba całkowitą, false w przeciwnym wypadku
     */
    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }

    /**
     * Walidator numeru port.
     * Podany string musi być liczbą całkowitą, większą bądź równą 2000.
     * @param port numer portu do walidacji.
     * @return true jesli numer portu spełnia wymagania, false w przeciwnym wypadku
     */
    private boolean validatePortNumber(String port) {

        // Is empty?
        if (port.isEmpty())
            return false;

        // Is an integer?
        if (!isInteger(port))
            return false;

        // Is greater than 2000? This restriction is because of the system requirements.
        int portInt = Integer.parseInt(port);
        if (portInt < 2000)
            return false;

        return true;
    }

    /**
     * Walidator danych hosta, z którym chcemy się połączyć.
     * @param hostAddress adres IP hosta
     * @param hostPort numer portu, na którym nasłuchuje host.
     * @param connectionName nazwa nowego połączenia.
     * @return zwraca true, gdy dane są poprawe i false w przeciwnym przypadku.
     */
    private boolean validateConnectionInputs(String hostAddress, String hostPort, String connectionName) {
        if (hostAddress.isEmpty() || hostPort.isEmpty() || connectionName.isEmpty())
            return false;

        if (!validatePortNumber(hostPort))
            return false;

        return true;
    }

    /**
     * Sprawdza czy połączenie, które chcemy nawiązać już nie istnieje.
     * @param connectionName nazwa połączenia
     * @return zwraca true, gdy połączenie o zadanej nazwie już istnieje i false w przeciwnym przypadku.
     */
    private boolean checkIfConnectionAlreadyExists(String connectionName) {
        List<Connection> connections = model.getConnections();

        for(Connection connection: connections) {
            if (connection.getConnectionName().equals(connectionName))
                return true;
        }

        return false;
    }

    /**
     * Przeszukje tabPane w celu znalezienia karty o zadanej nazwie.
     * @param name nazwa karty, której szukamy
     * @return znaleziona karta lub null, gdy nie ma karty o podanej nazwie
     */
    private Tab getTab(String name) {
        for (Tab tab: tabPane.getTabs()) {
            if (tab.getText().equals(name))
                return tab;
        }
        return null;
    }


    /**
     * Umożliwia użytkownikowi wprowadzenie tekstu wysyłanej wiadomości i naciśniecie przycisku Send.
     */
    private void enableInput() {
        sendButton.setDisable(false);
        messageInput.setDisable(false);
        messageInput.setText("");
    }

    /**
     * Uniemożliwia użytkownikowi wprowadzenie tekstu wysyłanej wiadomości i naciśniecie przycisku Send.
     */
    private void disableInput() {
        sendButton.setDisable(true);
        messageInput.setDisable(true);
        messageInput.setText("You are not connected...");
    }

    /**
     * Wyświetla wysyłaną wiadomość w karcie nadawcy.
     * @param message treść wiadomośći.
     */
    private void printOutputMessage(String message) {
        messageOutput.appendText("You: \t" + message + "\n");
    }

    /**
     * Szuka karty o zadanej nazwie i zwraca jej pole tekstowe.
     * @param name nazwa karty
     * @return zwraca karte o zdanej nazwie.
     */
    private TextArea getTextAreaFromTab(String name) {

        for (Tab tabIterator: tabPane.getTabs()) {
            if (tabIterator.getText().equals(name)) {
                AnchorPane anchorPane = (AnchorPane) tabIterator.getContent();
                TextArea textArea = (TextArea) anchorPane.getChildren().get(0);
                return textArea;
            }
        }
        return null;
    }

    /**
     * Mówi Modelowi żeby usunał połączenie o zadanej nazwie z listy połączeń.
     * @param name nazwa połączenia.
     */
    private void deleteConnection(String name) {
        Connection toDelete = model.getConnection(name);
        model.getConnections().remove(toDelete);
        if (tabPane.getTabs().size() == 0)
            disableInput();
    }
    //endregion
}
