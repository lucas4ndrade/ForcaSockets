package Server.src;

import java.net.*;

public class App {
    public static void main(String[] args) {
        try {
            // Initiates server on port 8080
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Servidor ouvindo a porta 8080");

            // blocks execution and awaits for client to connect
            Socket client = server.accept();
            System.out.println("Cliente conectado: " + client.getInetAddress().getHostAddress());

            HangmanSession gameSession = new HangmanSession(client);
            gameSession.startGame();
            server.close();
            
        }
        catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        finally {
            System.out.println("Execução terminada");
        }
    }
}
