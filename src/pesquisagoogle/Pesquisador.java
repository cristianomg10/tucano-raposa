package pesquisagoogle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe que representa um pesquisador, contém o nome e as publicações do
 * mesmo.
 *
 * @author Armando
 */
public class Pesquisador {

    private String nome;
    private ArrayList<String> Publicacoes;

    /**
     * Construtor padrão.
     */
    public Pesquisador() {
    }

    /**
     * Cria um objeto pesquisador à partir do seu nome.
     *
     * @param nome Nome do pesquisador.
     */
    public Pesquisador(String nome) {
        this.nome = nome;
        Publicacoes = new ArrayList<String>();
    }

    /**
     * Retorna o nome do pesquisador.
     *
     * @return O nome do pesquisador.
     */
    public String getName() {
        return nome;
    }

    /**
     * Define o nome do pesquisador.
     *
     * @param nome O nome do pesquisador.
     */
    public void setName(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna as publicações do pesquisador.
     *
     * @return As publicações do pesquisador.
     */
    public ArrayList<String> getPublications() {
        return Publicacoes;
    }

    /**
     * Define as publicações do pesquisador.
     *
     * @param Publicacoes ArrayList contendo as publicações do pesquisador.
     */
    public void setPublicacoes(ArrayList<String> Publicacoes) {
        this.Publicacoes = Publicacoes;
    }

    /**
     * Imprime um objeto pesquisador.
     */
    public void imprime() {
        System.out.printf("%s\n", nome);
        for (String pub : Publicacoes) {
            System.out.printf("%s\n", pub);
        }
    }

    /**
     * Método que gera a combinação de nomes de um pesquisador, e retorna em um
     * ArrayList, Ex: Armando Honorio, retornará: [0] Armando, [1] Honorio, [2]
     * Armando Honorio
     *
     * @return ArrayList contendo as combinações do nome do pesquisador.
     */
    public ArrayList<String> geraNomes() {
        String namesArray[] = this.nome.split(" ");
        ArrayList<String> concatenacoes = new ArrayList<>();
        conjuntoPotencia(namesArray, namesArray.length, concatenacoes);
        return concatenacoes;
    }

    /**
     * Gera o conjunto potência de vet, o elemento vazio não é incluído.
     *
     * @param vet Array com os nomes separados do pesquisador usado pra gerar o
     * conjunto potência.
     * @param n Variável para controlar a recursão.
     * @param resultado ArrayList para guardar o resultado, isto é, o conjunto
     * potência.
     */
    public void conjuntoPotencia(String vet[], int n, ArrayList<String> resultado) {
        if (n == 1) {
            resultado.add(vet[0]);
            return;
        }
        conjuntoPotencia(vet, n - 1, resultado);
        // não posso adicionar em uma Collection enquanto faço um loop nela.
        ArrayList<String> temp = new ArrayList<>();
        int tam = resultado.size();
        for (int i = 0; i <= tam; i++) {
            if (i != tam) {
                temp.add(resultado.get(i) + "+" + vet[n - 1]);
            } else {
                temp.add(vet[n - 1]);
            }
        }
        resultado.addAll(temp);
    }

    /**
     * Gera todas as queries padrão do pesquisador, ou seja: nome do pesquisador
     * + nome da publicação.
     *
     * @return ArrayList contendo as queries do pesquisador em lower case.
     */
    public ArrayList<String> geraQueriesPadrao() {
        String nm = this.nome.replaceAll(" ", "+");
        ArrayList<String> resultado = new ArrayList<>();
        String query;
        for (String pub : this.Publicacoes) {
            pub = "+" + pub.replaceAll(" ", "+");
            query = nm + pub;
            query = query.toLowerCase();
            resultado.add(query);
        }
        return resultado;
    }

    /**
     * Gera todas as queries possíveis, ou seja, todas as combinações de nomes +
     * nome da publicação.
     *
     * @return ArrayList contendo todas as queries possíveis.
     */
    public ArrayList<String> geraTodasQueries() {
        ArrayList<String> nomes = geraNomes();
        ArrayList<String> resultado = new ArrayList<>();;
        for (String nm : nomes) {
            for (String pub : this.Publicacoes) {
                pub = "+" + pub.replaceAll(" ", "+");
                resultado.add(nm + pub);
            }
        }
        return resultado;
    }

    public String toString() {
        String retorno = "";
        retorno = retorno + nome + "\n";
        for (String pub : Publicacoes) {
            retorno = retorno + pub + "\n";
        }
        return retorno;
    }
}
