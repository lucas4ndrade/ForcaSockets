package Server.src;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;

// HangmanSession represents a hangman game session
public class HangmanSession {
    private Socket client;
    private String word;
    private int errors;
    private boolean clientWon;
    private List<Character> usedLetters;
    private List<Character> correctLetters;
    private List<Character> wordLetters;
    private ObjectOutputStream clientOutStream;
    private ObjectInputStream clientInStream;
    private boolean gameInProgress;
    
    public HangmanSession(Socket client) {
        this.errors = 0;
        this.client = client;
        this.clientWon = false;
        this.gameInProgress = true;
        this.usedLetters = new ArrayList<Character>();
        this.correctLetters = new ArrayList<Character>();
        this.wordLetters = new ArrayList<Character>();
        getRandomWord();
    }
    
    // gets a random word and stores its letters on 'wordLetters' array
    private void getRandomWord() {
        this.word = "amo voce";
        for (int i = 0; i < this.word.length(); i++) {
            char c = this.word.charAt(i);
            if(c != ' ' && !this.wordLetters.contains(c)) {
                this.wordLetters.add(c);
            }
        }
    }
    
    public void startGame() {
        try {
            startDataStream();
            sendWelcome();
            
            play();
            
            endGame();
        } catch(Exception e) {
            System.out.println("Failed to execute game with message: " + e.getMessage());
        }
    }
    
    // starts data stream for comunication with client
    private void startDataStream() throws IOException {
        this.clientOutStream = new ObjectOutputStream(client.getOutputStream());
        this.clientInStream = new ObjectInputStream(client.getInputStream());
    }
    
    private void sendWelcome() throws IOException {
        sendMessage("Bem vindo ao jogo da forca! \n foi escolhida uma palavra aleatória, tente adivinhar qual palavra é! \n você tem 6 chances. \n\n" + getHang());
    }
    
    public void play() throws IOException {
        while(this.gameInProgress) {
            char inputedChar = Character.toLowerCase(clientInStream.readChar());
            System.out.println("Client input: " + inputedChar);

            String maybeGoodbyeMessage = "";
            String responseMessage = verifyChar(inputedChar);
            boolean gameEnded = verifyGameEnd();
            if(gameEnded) {
                gameInProgress = false;
                maybeGoodbyeMessage = getGoodbyeMessage();
            }


            sendMessage(responseMessage + "\n\n" + getHang() + maybeGoodbyeMessage);
        }
    }

    // verifies client inputed character.
    private String verifyChar(char inputedChar) throws IOException {
        String message = "";
        if(this.usedLetters.contains(inputedChar)) {
            message = "Voce já usou essa letra! tente denovo com outra.";
            return message;
        } 
        
        if(this.word.indexOf(inputedChar) >= 0 ) {
            message = "Boa! a palavra contém a letra "+ inputedChar+".";
            this.correctLetters.add(inputedChar);
        } else {
            message = "A palavra não contém a letra "+ inputedChar+" :( .";
            this.errors++;
        }
        this.usedLetters.add(inputedChar);
        return message;
    }
    
    // gets hang string.
    private String getHang() {
        // define charater limbs
        String h = " ";
        if(errors > 0) {
            h = "O";
        }
        String la = " ";
        if(errors > 1) {
            la = "/";
        }
        String t = " ";
        if(errors > 2) {
            t = "|";
        }
        String ra = " ";
        if(errors > 3) {
            ra = "\\";
        }
        String ll = " ";
        if(errors > 4) {
            ll = "/";
        }
        String rl = " ";
        if(errors > 5) {
            rl = "\\";
        }
        String hangTop = 
        "+-------------------------------------- \n" +
        "|                                     | \n"  +
        "|                                     "+h +"\n" +
        "|                                   "+la+t+ra+"\n" +
        "|                                   "+ll+" "+rl+"\n" +
        "|                                         \n" +
        "|                                         \n" +
        "|                                         \n" +
        "|                                         \n";
        
        String hangBase = "| ";
        for (int i = 0; i < this.word.length(); i++) {
            char wordChar = this.word.charAt(i);

            if(wordChar == ' ') {    
                hangBase = hangBase + "  ";
            }  else if(correctLetters.contains(wordChar)) {
                hangBase = hangBase + wordChar + " ";
            } else {
                hangBase = hangBase + "_ " ;
            }
        }
        
        return hangTop + hangBase + "\n letras já utilizadas: " + this.usedLetters.toString() + "\n";
        
    }
    
    // checks if the game ended and computes its possible outcome.
    private boolean verifyGameEnd() {
        if(this.errors >= 6) {
            return true;
        } else if (this.correctLetters.size() == this.wordLetters.size()) {
            this.clientWon = true;
            return true;
        }
        return false;
    }

    // returns a goodbye message given game outcome.
    private String getGoodbyeMessage() {
        String message = "voce perdeu...";
        if(clientWon) {
            message = "voce venceu!";
        }

        return "O jogo terminou e " + message + "\n você será desconectado agora, até mais!\n\n Aperte CTRL+C para sair do programa.";
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
    
    // sends a string message to client via data stream.
    private void sendMessage(String message) throws IOException {
        clientOutStream.writeUTF(message);
        clientOutStream.flush();
    }
}
