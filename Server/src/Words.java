package Server.src;

import java.util.ArrayList;
import java.util.List;

public class Words {
    private List<String> words;

    public Words() {
        initWords();
    }

    public String getRandomWord(){
        return words.get(0);
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
