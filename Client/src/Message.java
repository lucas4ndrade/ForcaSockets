package Client.src;

import java.util.List;

// Message represents a message received from server
public class Message {
    private String messageText;
    private boolean correctLetter;
    private List<Character> word;

    public Message(String messageText, boolean correctLetter, List<Character> word) {
        this.messageText  = messageText;
        this.correctLetter = correctLetter;
        this.word = word;
    }

    public String getMessageText() {
        return messageText;
    }

    public List<Character> getWord() {
        return word;
    }

    public boolean isCorrectLetter() {
        return correctLetter;
    }
    
}
