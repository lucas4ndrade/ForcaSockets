package Client.src;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost",8080);
            ObjectOutputStream clientOutStream = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream clientInStream = new ObjectInputStream(client.getInputStream());
            Scanner sc = new Scanner(System.in);

            while(true) {
                String serverMsg = clientInStream.readUTF();
                System.out.println(serverMsg);
                
                char inputChar = sc.next().charAt(0);
                clientOutStream.writeChar(inputChar);
                clientOutStream.flush();
            }
        }
        catch(Exception e) { 
            System.out.println("Erro: " + e.getMessage());
        }
    }
    
}
