package Server.src;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// HangmanSession represents a hangman game session
public class HangmanSession {
    private Socket client;
    private String word;
    private int errors;
    private List<Character> wordLetters;
    private boolean gameInProgress;
    private ObjectOutputStream clientOutStream;
    private ObjectInputStream clientInStream;
    
    public HangmanSession(Socket client) {
        this.errors = 0;
        this.gameInProgress = true;
        this.client = client;
        getRandomWord();
        initWordLetters();
    }
    
    // gets a random word and stores its letters on 'wordLetters' array to send to client
    private void getRandomWord() {
        Words wordMemory = new Words();

        word = wordMemory.getRandomWord();
    }

    // init word letters array to send to client, only filling slots corresponding a ' ' charater. other slots will be filled with null
    private void initWordLetters() {
        wordLetters = new ArrayList<Character>();

        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);

            if(c == ' ') {
                wordLetters.add(c);
            } else {
                wordLetters.add(null);
            }
        }
    }
    
    public void startGame() {
        try {
            startDataStream();
            sendWelcomeMessage();
            
            play();
            
            endGame();
        } catch(Exception e) {
            System.out.println("Failed to execute game with message: " + e.getMessage());
        }
    }
    
    // starts data stream for comunication with client
    private void startDataStream() throws IOException {
        clientOutStream = new ObjectOutputStream(client.getOutputStream());
        clientInStream = new ObjectInputStream(client.getInputStream());
    }
    
    // send initial welcome message
    private void sendWelcomeMessage() throws IOException {
        String messageText = "Bem vindo ao jogo da forca! \n foi escolhida uma palavra aleatória, tente adivinhar qual palavra é! \n você tem 6 chances. \n\n";

        sendMessage(newMessage(messageText));
    }
    
    public void play() throws IOException {
        while(gameInProgress) {
            char inputedChar = Character.toLowerCase(clientInStream.readChar());
            System.out.println("Client input: " + inputedChar);

            HashMap<String,Object> msg = verifyChar(inputedChar);

            sendMessage(msg);
            verifyGameEnd();
        }
    }

    // verifies client inputed character and returns a message hashmap for a response.
    private HashMap<String, Object> verifyChar(char inputedChar) throws IOException {
        String messageText = "";
        boolean correctLetter = false;
        
        if(word.indexOf(inputedChar) >= 0 ) {
            messageText = "Boa! a palavra contém a letra "+ inputedChar+".";
            updateWordLetters(inputedChar);
            correctLetter = true;
        } else {
            messageText = "A palavra não contém a letra "+ inputedChar+" :( .";
            errors++;
        }

        return newMessage(messageText, correctLetter);
    }

    // update wordLetters array, filling slots with the inputed letter
    private void updateWordLetters(char inputedChar) {
        for (int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == inputedChar) {
                wordLetters.set(i, inputedChar);
            }
        }
    }

    // verifies if the game has endede
    private void verifyGameEnd() {
        // verifies if the client lost
        if(errors > 5) {
            gameInProgress = false;
            return;
        }

        // verifies if the client won
        boolean wordComplete = true;
        for (int i = 0; i < wordLetters.size(); i++) {
            if(wordLetters.get(i) == null) {
                wordComplete = false;
                break;
            } 
        }

        gameInProgress = !wordComplete;
    }

    // closes conections and data streams.
    private void endGame() {
        try {
            clientOutStream.close();
            clientInStream.close();
            client.close();
        } catch(Exception e) {
            System.out.println("Failed to close client or message stream");
        }
    }
    
    // creates a hashmap with information to pass through the data stream
    private HashMap<String, Object> newMessage(String messageText) {
        return newMessage(messageText, false);
    }
 
    // creates a hashmap with information to pass through the data stream
    private HashMap<String, Object> newMessage(String messageText, boolean isCorrectLetter) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("messageText", messageText);
        message.put("word", wordLetters);
        message.put("isCorrectLetter", isCorrectLetter);

        return message;
    }

    // sends a message hashmap to client via data stream.
    private void sendMessage(HashMap<String, Object> message) throws IOException {
        clientOutStream.writeUnshared(message);
        clientOutStream.flush();
        clientOutStream.reset();
    }
}
