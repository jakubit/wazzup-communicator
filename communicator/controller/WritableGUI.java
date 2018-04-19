package proz.communicator.controller;

import proz.communicator.model.Message;

/**
 * Interfejs dla Kontrolera, zapewnia impelemntację funkcji write.
 */
public interface WritableGUI {
    /**
     * Metoda do wypisywania wiadomosci w Widoku.
     * @param m wiadomość do wyświetlenia.
     */
    void write(Message m);
}
