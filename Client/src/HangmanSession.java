package Client.src;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;

// HangmanSession represents a hangman game session
public class HangmanSession {
    private Socket client;
    private List<Character> word;
    private int errors;
    private boolean clientWon;
    private List<Character> usedLetters;
    private ObjectOutputStream clientOutStream;
    private ObjectInputStream clientInStream;
    private boolean gameInProgress;
    private Scanner sc;
    
    public HangmanSession(Socket client) {
        this.errors = 0;
        this.client = client;
        this.clientWon = false;
        this.gameInProgress = true;
        this.usedLetters = new ArrayList<Character>();
        this.sc = new Scanner(System.in);
    }
    
    public void startGame() {
        try {
            startDataStream();
            receiveWelcomeMessage();
            
            play();
            
            endGame();
        } catch(Exception e) {
            System.out.println("Falha ao executar o jogo: " + e.getMessage());
        }
    }
    
    // starts data stream for comunication with client
    private void startDataStream() throws IOException {
        clientOutStream = new ObjectOutputStream(client.getOutputStream());
        clientInStream = new ObjectInputStream(client.getInputStream());
    }

    // receives and computes welcome message from server.
    private void receiveWelcomeMessage() throws Exception {
        Message msg = receiveResponseMsgFromServer();
        word = msg.getWord();
        
        System.out.println(msg.getMessageText());
    }
    
    public void play() throws Exception {
        while(gameInProgress) {
            printHang();
            sendInputToServer();

            Message msg = receiveResponseMsgFromServer();
            System.out.println(msg.getMessageText());

            computeResponse(msg);
        }
    }
    
    // prints hang string.
    private void printHang() {
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
        "|                                     "+h+"\n" +
        "|                                    "+la+t+ra+"\n" +
        "|                                    "+ll+" "+rl+"\n" +
        "|                                         \n" +
        "|                                         \n" +
        "|                                         \n" +
        "|                                         \n";
        
        // define hang base
        String hangBase = "| ";
        for (int i = 0; i < word.size(); i++) {
            Character wordChar = word.get(i);

            if(wordChar == null) {
                hangBase = hangBase + "_ " ;
            } else if (wordChar == ' ') {    
                hangBase = hangBase + "  ";
            }  else {
                hangBase = hangBase + wordChar + " ";
            }
        }
        
        System.out.println(hangTop + hangBase + "\n letras já utilizadas: " + usedLetters.toString());
    }
    
    // closes conections, data streams and sends final messages.
    private void endGame() {
        try {
            printHang();
            printGoodbyeMessage();
            
            clientOutStream.close();
            clientInStream.close();
            client.close();
        } catch(Exception e) {
            System.out.println("Failed to close client or message stream");
        }
    }
    
    // returns a goodbye message given game outcome.
    private void printGoodbyeMessage() {
        String message = "voce perdeu...";
        if(clientWon) {
            message = "voce venceu!";
        }

        System.out.println("O jogo terminou e " + message + "\n você será desconectado agora, até mais!");
    }

    // sends the next inputed character to server.
    private void sendInputToServer() throws IOException {
        char inputChar = sc.next().charAt(0);

        if(usedLetters.contains(inputChar)){
            System.out.println("Voce já usou essa letra! tente denovo com outra.");
            sendInputToServer();
            return;
        }

        usedLetters.add(inputChar);
        clientOutStream.writeChar(inputChar);
        clientOutStream.flush();
    }

    // waits for a response from the server, and stores its content on a Message object for easier access
    private Message receiveResponseMsgFromServer() throws Exception {
        HashMap<String, Object> responseMsg = (HashMap<String, Object>)clientInStream.readUnshared();

        String messageText       = (String)responseMsg.get("messageText");
        List<Character> respWord = (List<Character>)responseMsg.get("word");
        boolean correctLetter    = (boolean)responseMsg.get("isCorrectLetter");

        Message msg = new Message(messageText, correctLetter, respWord);
        return msg;
    }

    // computes the response message received from the server.
    private void computeResponse(Message msg) {
        if(msg.isCorrectLetter()){
            word = msg.getWord();
            if(isWordComplete()) {
                clientWon = true;
                gameInProgress = false;
            }
        } else {
            errors++;
            if(errors > 5) {
                gameInProgress = false;
            }
        }
    }

    // checks the hang word is complete
    private boolean isWordComplete() {
        boolean wordComplete = true;

        for (int i = 0; i < word.size(); i++) {
            if(word.get(i) == null) {
                wordComplete = false;
                break;
            } 
        } 
        
        return wordComplete;
    }
}
