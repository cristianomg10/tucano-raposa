package NovoExtrator.Desambiguacao;

import NovoExtrator.html.ExtractorHTML;
import NovoExtrator.html.ExtractorHTMLList;
import NovoExtrator.structures.TokenNP;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pesquisagoogle.Files;
import pesquisagoogle.Pesquisador;

/**
 *
 * @author armando
 */
public class Desambiguacao {

    LinkedList<LinkedList<TokenNP>> citations;

    public Desambiguacao(String path) {
        setCitations(path);
    }

    // Caminho para a página .html dentro de algum diretório
    public LinkedList<LinkedList<TokenNP>> getCitations() {
        return citations;
    }

    public final void setCitations(String path) {
        try {
            int indiceUltimaBarra = path.lastIndexOf("/");
            String nome = path.substring(indiceUltimaBarra);
//            System.out.println("Desambiguacao.setCitations(String): " + path);
            LinkedList<LinkedList<TokenNP>> cts = ExtractorHTMLList.getCitations(path);
//            ExtractorHTML.printCollection(cts, "./Resultados/Extracao/" + nome + "_saida.html", 0);
            citations = cts;
        } catch (FileNotFoundException ex) {
            // Excecao
            citations = null;
        } catch (IOException ex) {
            // Excecao
            citations = null;
        }
    }

//    Retorna quantas vezes o nome acontece nas citações contidas na lista
    public static int numeroDeOcorrencias(LinkedList<LinkedList<TokenNP>> citacoes, String nomeCompleto) {
        int count = 0;
        nomeCompleto = nomeCompleto.toLowerCase();
        String nomes[] = nomeCompleto.split(" ");
        for (String string : nomes) {
//            System.out.println(string);
        }
        for (LinkedList<TokenNP> linkedList : citacoes) {
            for (TokenNP tokenNP : linkedList) {
                String token = tokenNP.getToken();
                for (int i = 0; i < nomes.length; i++) {
//                    System.out.println(token);
                    if (token.contains(nomes[i]) && nomes[i].length() > 2) {
                        count++;
                        i += nomes.length;// saio do loop pois encontrei o nome
                    }
                }
            }
        }
        return count;
    }

    public static int numberOfCitations(LinkedList<LinkedList<TokenNP>> citations) {
        LinkedList<TokenNP> linkedList;
        String wholeExtractedCitation = "";
        int count = 0;
        for (int i = 0; i < citations.size(); i++) {
            linkedList = citations.get(i);
            for (TokenNP tk : linkedList) {
                wholeExtractedCitation = wholeExtractedCitation + tk.getToken() + " ";
            }
            if (wholeExtractedCitation.length() > 80) {
//                System.out.println("");
//                System.out.println(wholeExtractedCitation);
//                System.out.println("");
                count++;
            }
            wholeExtractedCitation = "";
        }
        return count;
    }

    public Desambiguacao() {
    }

    public int size() {
        return citations.size();
    }

    public float rateCitationsFromPage(String name) {
        LinkedList<LinkedList<TokenNP>> citas = getCitations();
        if (citas != null) {
            int n = numeroDeOcorrencias(citas, name);
//            System.out.println("Encontrado " + n + " em " + citas.size() + " citacoes");
            float result = (float) n / numberOfCitations(citas);
            return result;
        }
        return 0;
    }

    public static String separatePunctuation(String str) {
        str = str.replace("\"", " \" ");
        str = str.replace("“", " “ ");
        str = str.replace("”", " ” ");
        str = str.replace("'", " ' ");
        str = str.replace(".", " . ");
        str = str.replace(",", " , ");
        str = str.replace("!", " ! ");
        str = str.replace(";", " ; ");
        str = str.replace(":", " : ");
        str = str.replace("-", " - ");
        str = str.replace("(", " ( ");
        str = str.replace(")", " ) ");
        str = str.replace("[", " [ ");
        str = str.replace("]", " ] ");
        str = str.replace("&", " & ");
        str = str.replace("*", " * ");
        str = str.replace("@", " @ ");
        str = str.replace("\\", " \\ ");
        str = str.replace("/", " / ");
        str = str.replace("$", " $ ");
        str = str.replace("#", " # ");
        return str;
    }

    public boolean isAuthorCitationsInExtraction(Pesquisador pesquisador) {

        if (citations != null) {
            ArrayList<String> authorPublications = pesquisador.getPublications();
            int numberOfFoundWords = 0;
            int currentWordIndex = 0;
            int wordGreaterThanTreeCounter = 0;
            int trueCounter = 0;
            String splittedPublication[];
            String splittedExtractedCitation[];
            String tempString;
            String wholeExtractedCitation = "";
            float tempRate;
            boolean rates[] = new boolean[authorPublications.size()];

            // a quantidade de publicacoes do pesquisador controla o loop
            LinkedList<TokenNP> linkedList;
            TokenNP tokenNP;

            for (int n = 0; n < authorPublications.size(); n++) {

                tempString = separatePunctuation(authorPublications.get(n));
                tempString = tempString.toLowerCase();

                splittedPublication = tempString.split("\\s+");

//                for (int i = 0; i < splittedPublication.length; i++) {
//                    if(splittedPublication[i].length()>3){
//                        System.out.printf("%s ", splittedPublication[i]);
//                    }
//                }
//                System.out.println("");

                for (int i = 0; i < citations.size(); i++) {

                    linkedList = citations.get(i);
                    for (TokenNP tk : linkedList) {
                        wholeExtractedCitation = wholeExtractedCitation + tk.getToken() + " ";
                    }
                    wholeExtractedCitation = wholeExtractedCitation.toLowerCase();
                    splittedExtractedCitation = wholeExtractedCitation.split("\\s+");

//                    for (int j = 0; j < splittedExtractedCitation.length; j++) {
//                        if (splittedExtractedCitation[j].length() > 3) {
//                            System.out.printf("%s ", splittedExtractedCitation[j]);
//                        }
//                    }
//                    System.out.println("");
//                System.out.println("#######");

                    for (int k = 0; k < splittedPublication.length; k++) {

                        if (splittedPublication[k].length() > 3) {

//                            if (splittedPublication[k].equals("2008") && i == 72) {
//                                System.out.println("k: " + k);
//                                System.out.println("currentWordIndex: " + currentWordIndex);
//                            }

                            //enquanto a palavra da citacao nao for maior do que 3
                            while ((currentWordIndex < (splittedExtractedCitation.length - 1))
                                    && (!(splittedExtractedCitation[currentWordIndex].length() > 3)
                                    || !(tempString.contains(splittedExtractedCitation[currentWordIndex])))) {
                                currentWordIndex++;
                            }
                            if (currentWordIndex < splittedExtractedCitation.length && (tempString.contains(splittedExtractedCitation[currentWordIndex]))) {
                                int index1 = tempString.indexOf(splittedExtractedCitation[currentWordIndex]);
                                int returnedIndex = tempString.indexOf(' ', index1);
                                int index2 = (returnedIndex >= 0) ? returnedIndex : tempString.length();
                                String containerString = tempString.substring(index1, index2);

                                if (!containerString.equals(splittedExtractedCitation[currentWordIndex])) {
                                    currentWordIndex++;
                                }
                            }
                            if ((currentWordIndex < (splittedExtractedCitation.length - 1))
                                    && splittedExtractedCitation[currentWordIndex].contains(splittedPublication[k])) {
//                            System.out.println("casamento: " + splittedPublication[k] + " & " + splittedExtractedCitation[currentWordIndex]);
                                currentWordIndex++;
                                numberOfFoundWords++;
                            }
                        }
                    }

                    for (int j = 0; j < splittedPublication.length; j++) {
                        if (splittedPublication[j].length() > 3) {
                            wordGreaterThanTreeCounter++;
                        }
                    }
//                    if (numberOfFoundWords > 0) {
//                        System.out.println("@@@@@@@@@@@@@");
//                        for (int q = 0; q < splittedPublication.length; q++) {
//                            if (splittedPublication[q].length() > 3) {
//                                System.out.printf("\"%d %s \"", q, splittedPublication[q]);
//                            }
//                        }
//                        System.out.println("");
//                        for (int r = 0; r < splittedExtractedCitation.length; r++) {
//                            if (splittedExtractedCitation[r].length() > 3) {
//                                System.out.printf("\"%d %s \"", r, splittedExtractedCitation[r]);
//                            }
//                        }
//                        System.out.println("");
//                        System.out.println(numberOfFoundWords);
//                        System.out.println(wordGreaterThanTreeCounter);
//                        System.out.println("Tamanho da entrada: " + splittedPublication.length);
//                        System.out.println("Tamanho da extraida: " + splittedExtractedCitation.length);
//                        System.out.println(i);
//                        System.out.println("@@@@@@@@@@@@@");
//                    }
                    tempRate = (float) numberOfFoundWords / wordGreaterThanTreeCounter;
//                System.out.println(tempRate);
                    if (tempRate >= 0.6) { // 80% das palavras da publicacao tem de estar na citacao
                        // vou para proxima publicacao
                        rates[n] = true;
                        i += citations.size(); // saio do loop pois encontrei a citacao
                    }

                    wholeExtractedCitation = "";
                    currentWordIndex = 0;
                    numberOfFoundWords = 0;
                    wordGreaterThanTreeCounter = 0;
//                System.out.println("#######");
                }

            }
            for (int i = 0; i < rates.length; i++) {
                if (rates[i]) {
//                    System.out.println(i);
                    trueCounter++;
                }
            }
            tempRate = (float) trueCounter / rates.length;
//            System.out.println(tempRate);
            if (tempRate > 0.7) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }

    public static void main(String[] args) {
        ArrayList<Pesquisador> pesquisadores = Files.leArquivoEntrada("Entrada1.xml");
//        for (int i = 0; i < pesquisadores.size(); i++) {
//            System.out.println(i);
//            System.out.println(pesquisadores.get(i));
//            System.out.println("");
//            System.out.println("****************************");
//        }

//        String teste = "“Approximate”";
//        System.out.println(teste);
//        System.exit(0);
        System.out.println(Files.fileSizeInMB("./PaginasParaTeste/William T. Freeman - - VideoLectures.NET.html"));
//        System.exit(0);

        String path = "./PaginasParaTeste/William T. Freeman - - VideoLectures.NET.html";
        Desambiguacao desambiguacao = new Desambiguacao(path);
        LinkedList<LinkedList<TokenNP>> citas = desambiguacao.getCitations();

        System.out.println(citas.size());

        if (desambiguacao.isAuthorCitationsInExtraction(pesquisadores.get(0))) {
            System.out.println("A FUNCAO FUNCIONA!!");
        }

        int n = numeroDeOcorrencias(citas, pesquisadores.get(0).getName());
        System.out.println("Encontrado " + n + " em " + citas.size() + " citacoes");
//        System.out.println(numberOfCitations(citas));
    }
}
