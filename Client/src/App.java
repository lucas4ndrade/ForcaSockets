package Client.src;

import java.net.*;

public class App {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost",8080);
            HangmanSession gameSession = new HangmanSession(client);
            gameSession.startGame();
        } catch(Exception e) { 
            System.out.println("Erro: " + e.getMessage());
        } finally {
            System.out.println("Aperte CTRL+C para sair do programa.");
        }
    }
    
}
