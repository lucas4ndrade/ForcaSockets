package Server.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Words represents a word memory database
public class Words {
    private List<String> words;

    public Words() {
        initWords();
    }

    public String getRandomWord(){
        Random rand = new Random();
        int randomIndex = rand.nextInt(words.size());

        return words.get(randomIndex);
    }

    private void initWords() {
        this.words = new ArrayList<String>();
        this.words.add("batatinha");
        this.words.add("carro de corrida");
        this.words.add("redes de computadores");
        this.words.add("computaçao distribuida");
        this.words.add("olimpiadas");
        this.words.add("jogo da forca");
        this.words.add("programacao com sockets");
        this.words.add("foco força e cafe");
        this.words.add("programaçao com java");
        this.words.add("golang e demais");
    }
}
