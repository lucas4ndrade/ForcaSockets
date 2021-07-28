package Server.src;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

// HangmanSession represents a hangman game session
public class HangmanSession {
    private Socket client;
    private String word;
    private int errors;
    private boolean clientWon;
    
    public HangmanSession(Socket client) {
        this.client = client;
        this.word = getRandomWord();
        this.errors = 0;
        this.clientWon = false;
    }
    
    private String getRandomWord() {
        return "palavra daora";
    }
    
    public void startGame() {
        try {
            ObjectOutputStream clientOutput = new ObjectOutputStream(client.getOutputStream());
            clientOutput.flush();
            clientOutput.writeChars("O jogo começou! a sua palavra é: " + this.word);
            clientOutput.close();

            while(this.errors != 6 && !clientWon) {
                ObjectInputStream clientInput = new ObjectInputStream(client.getInputStream());
                clientInput.readUTF();
            }
            
            endGame();
            client.close();
        } catch(Exception e) {
            System.out.println("Failed to execute game with message: " + e.getMessage());
        }
    }
    
    private void endGame() {
        try {
            String message = "";
            if(clientWon) {
                message = "voce venceu!";
            } else {
                message = "voce perdeu...";
            }

            ObjectOutputStream clientOutput = new ObjectOutputStream(client.getOutputStream());
            clientOutput.flush();
            clientOutput.writeChars("O jogo terminou! " + message);
            clientOutput.close();
        } catch(Exception e) {
            System.out.println("Failed to send finishing message to client");
        }
    }
    
}
