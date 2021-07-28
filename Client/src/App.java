package Client.src;

import java.io.ObjectInputStream;
import java.net.*;

public class App {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("cliente",8080);
            /*ObjectInputStream entrada = new ObjectInputStream(client.getInputStream());

            Date data_atual = (Date)entrada.readObject();
            JOptionPane.showMessageDialog(null,"Data recebida do servidor:" + data_atual.toString());
            entrada.close();
            System.out.println("Conexão encerrada");*/
        }
        catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    
}
