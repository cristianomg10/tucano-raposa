package pesquisagoogle;

import NovoExtrator.Desambiguacao.Desambiguacao;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import NovoExtrator.structures.TokenNP;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * Classe para fazer o processamento de uma página
 *
 * @author armando
 */
public class Processamento {

    // Tabela hash com tags html (Ex: <p>, <h1>)
    Hashtable<String, String> HashTagTable;
    // Tabela hash com as palavras a serem pesquisadas na página
    Hashtable<String, String> personalSearchTerms;
    // Tabela hash com as palavras usadas para pesquisar por referencias 
    // a paginas com citacao dentro de uma pagina
    Hashtable<String, String> citationReferenceTerms;
    Pesquisador pesquisador;

    public Processamento() {
        setPersonalSearchTerms();
        setCitationReferenceTerms();
    }

    /**
     * Construtor da classe processamento, recebe um objeto pesquisador
     *
     * @param pesq objeto pesquisador
     */
    public Processamento(Pesquisador pesq) {
        setPesquisador(pesq);
        setPersonalSearchTerms();
        setCitationReferenceTerms();
    }

    /**
     * Lê no máximo a quantidade de palavras passadas como parâmetro e as
     * retorna em uma string
     *
     * @param path caminho do arquivo
     * @param nWords quantidade de palavras
     * @return a quantidade de palavras do arquivo
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String readWords(String path, int nWords) throws FileNotFoundException, IOException { // não trata tags
        StringBuilder strb = new StringBuilder();
        BufferedReader input = new BufferedReader(new FileReader(path));
        String palavrasLinha[];
        String linha;
        int contador = 0;

        linha = input.readLine();
        while (linha != null && contador < nWords) {

            if (!linha.equals("")) {
                palavrasLinha = linha.split(" ");
                for (int i = 0; i < palavrasLinha.length && contador < nWords; i++) {
                    if (!HashTagTable.contains(palavrasLinha[i])) { // Insiro se não for uma tag.
                        strb.append(palavrasLinha[i]);
                        strb.append(" ");
                        contador++;
                    }
                }
                strb.append("\n");
            }
            linha = input.readLine();
        }
        return strb.toString();
    }

    /**
     * Lê uma página html e a retorna sem tags
     *
     * @param path caminho da página
     * @return uma string da página sem tags
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String removeTags(String path) throws FileNotFoundException, IOException {
        StringBuilder strb = new StringBuilder();
//        System.out.println("Entrou: " + path);
        BufferedReader input = new BufferedReader(new FileReader(path));
//        System.out.println("Saiu.");
        String string;
        char caractere;
        int contador = 0;
        System.out.println(strb);
        int temp;
        boolean lendoTag = false;
        boolean espacoFlag = false;
        while ((temp = input.read()) != -1) {
            caractere = (char) temp;
            if (caractere == '<') {
                lendoTag = true;
                strb.append(" ");
            } else if (caractere == '>') {
                lendoTag = false;
                strb.append(" ");
            } else if (caractere == '\n') {
                strb.append(caractere);
                contador++;
            } else {
                if (!lendoTag) {
                    strb.append(caractere);
                }
//                System.out.printf("%c", caractere);;
            }
//            if (!linha.equals("")) {
//                palavrasLinha = linha.split(" ");
//                for (int i = 0; i < palavrasLinha.length && contador < quantidade; i++) {
//                    if (!tabelaHashTags.contains(palavrasLinha[i])) { // Insiro se não for uma tag.
//                        strb.append(palavrasLinha[i]);
//                        strb.append(" ");
//                        contador++;
//                    }
//                }
//                strb.append("\n");
//            }
//            linha = input.readLine();
        }
        Files.writeFile(strb.toString(), "./Processamento/semtags.txt");
        return strb.toString();
    }

    /**
     * Lê uma página html e a retorna sem tags
     *
     * @param path
     * @return string da página sem tags
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String removeTags2(String path) throws FileNotFoundException, IOException {
        StringBuilder strb = new StringBuilder();
        BufferedReader input = new BufferedReader(new FileReader(path));
        String string;
        char caractere;
        int contador = 0;
        System.out.println(strb);
        int temp;
        boolean lendoTag = false;
        boolean tagAberta = false;
        boolean tagFechada = false;
        boolean espacoFlag = false;
        boolean lendoTexto = false;
        StringBuilder tagLida = new StringBuilder();
        while ((temp = input.read()) != -1) {
            caractere = (char) temp;
            if (caractere == '<') {
                lendoTag = true;
                strb.append(" ");
            } else if (caractere == '>') {
                lendoTag = false;
                strb.append(" ");
            } else if (caractere == '\n') {
                strb.append(caractere);
                contador++;
            } else {
                if (!lendoTag) {
                    strb.append(caractere);
                } else {
                    if (caractere == ' ') {
                        lendoTexto = false;
                        System.out.println(tagLida);
                        if (tagLida.toString().equals("script")) {
                            System.out.println("LENDO SCRIPT!!!");
                        }
                        tagLida = new StringBuilder();

                    } else if (lendoTexto) {
                        tagLida.append(caractere);
                        //System.out.println(tagLida + "\n");
                    } else {
                        lendoTexto = true;
                        tagLida.append(caractere);
                    }
                }
//                System.out.printf("%c", caractere);;
            }
//            if (!linha.equals("")) {
//                palavrasLinha = linha.split(" ");
//                for (int i = 0; i < palavrasLinha.length && contador < quantidade; i++) {
//                    if (!tabelaHashTags.contains(palavrasLinha[i])) { // Insiro se não for uma tag.
//                        strb.append(palavrasLinha[i]);
//                        strb.append(" ");
//                        contador++;
//                    }
//                }
//                strb.append("\n");
//            }
//            linha = input.readLine();
        }
        Files.writeFile(strb.toString(), "./Processamento/semtags.txt");
        return strb.toString();
    }

    // Lê uma página html e a retorna limpa, ou seja, sem tags e javascript
    public String cleanHtml(String htmlText) {

        Document doc = Jsoup.parse(htmlText);

        StringBuilder wholePage = new StringBuilder();

        Elements select = doc.select("body").select("*");
        List<TextNode> textNodes = select.get(0).textNodes();
        String textFromNode;

        for (int i = 0; i < select.size(); i++) {
            textNodes = select.get(i).textNodes();
            for (int j = 0; j < textNodes.size(); j++) {
                textFromNode = textNodes.get(j).text();
                wholePage.append(textFromNode);
                wholePage.append("\n");
            }
        }
        String withoutMultipleLineBreaks = wholePage.toString();
        withoutMultipleLineBreaks = withoutMultipleLineBreaks.replaceAll("(\\s*(\n)\\s*)+", "\n");
        return withoutMultipleLineBreaks;
//        for (Element el : doc.select("body").select("*")) {
//            for (TextNode node : el.textNodes()) {
////                System.out.println(node.text());
//                wholePage.append(node.text());
//                wholePage.append("\n");
//            }
//        }

    }

    // Lê uma página html e retorna os 1000 primeiros caracteres da página limpa
    public String cleanHtmlFirst1000Characters(String htmlText) {

        Document doc = Jsoup.parse(htmlText);

        int numberOfCharacters = 0;
        StringBuilder wholePage = new StringBuilder();

        Elements select = doc.select("body").select("*");
        List<TextNode> textNodes = select.get(0).textNodes();
        String textFromNode;

        for (int i = 0; i < select.size() && numberOfCharacters < 1000; i++) {
            textNodes = select.get(i).textNodes();
            for (int j = 0; j < textNodes.size(); j++) {
                textFromNode = textNodes.get(j).text();
                wholePage.append(textFromNode);
                wholePage.append("\n");
                numberOfCharacters += textFromNode.length();
            }
        }
        String withoutMultipleLineBreaks = wholePage.toString();
        withoutMultipleLineBreaks = withoutMultipleLineBreaks.replaceAll("(\\s*(\n)\\s*)+", "\n");
        return withoutMultipleLineBreaks;

    }

    /**
     * Recebe o link da página e checa se o nome do pesquisador contido na
     * classe está contido neste link
     *
     * @param name
     * @param link
     * @return
     */
    public boolean checkNameInLink(String name, String link) {
        link = link.toLowerCase();
        String nomes[] = name.split(" ");
        for (String nome : nomes) {
            nome = nome.toLowerCase();
            if (nome.length() > 2 && link.contains(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recebe o título da página e checa se o nome do pesquisador contido na
     * classe está contido neste título
     *
     * @param name
     * @param title
     * @return
     */
    public boolean checkNameInTitle(String name, String title) {
        title = title.toLowerCase();
        String nomes[] = name.split(" ");
        for (String nome : nomes) {
            nome = nome.toLowerCase();
            if (nome.length() > 2 && title.contains(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Define o pesquisador da classe
     *
     * @param pesq
     */
    public void setPesquisador(Pesquisador pesq) {
        this.pesquisador = pesq;
    }

    /**
     * Inicializa a tabela hash de tags
     */
    public void setHashTagTable() {
        try {
            HashTagTable = new Hashtable<String, String>();
            BufferedReader arquivoTagsHtml = new BufferedReader(new FileReader("./Arquivos/TagsHtml.txt"));
            String linha = arquivoTagsHtml.readLine();
            while (linha != null) {
                HashTagTable.put(linha, linha);
                linha = arquivoTagsHtml.readLine();
            }
            Enumeration tags = HashTagTable.elements();
            while (tags.hasMoreElements()) {
                String valor = (String) tags.nextElement();
                System.out.println(valor);
            }
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Processamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(Processamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Inicializa a tabela de termos usados na pesquisa de informação pessoal
     */
    public void setPersonalSearchTerms() {
        try {
            personalSearchTerms = new Hashtable<String, String>();
            BufferedReader arquivoSearchTermos = new BufferedReader(new FileReader("./Arquivos/PersonalSearchTermos.txt"));
            String linha = arquivoSearchTermos.readLine();
            while (linha != null) {
                personalSearchTerms.put(linha, linha);
                linha = arquivoSearchTermos.readLine();
            }
            Enumeration words = personalSearchTerms.elements();
            while (words.hasMoreElements()) {
                String valor = (String) words.nextElement();
                //System.out.println(valor);
            }
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Processamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(Processamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCitationReferenceTerms() {
        try {
            citationReferenceTerms = new Hashtable<String, String>();
            BufferedReader arquivoSearchTermos = new BufferedReader(new FileReader("./Arquivos/CitationReferenceTerms.txt"));
            String linha = arquivoSearchTermos.readLine();
            while (linha != null) {
                citationReferenceTerms.put(linha, linha);
                linha = arquivoSearchTermos.readLine();
            }
            Enumeration words = citationReferenceTerms.elements();
            while (words.hasMoreElements()) {
                String valor = (String) words.nextElement();
                //System.out.println(valor);
            }
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Processamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(Processamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Checa se a string passada como parâmetro é uma palavra de pesquisa para
     * informação pessoal
     *
     * @param palavra
     * @return
     */
    public boolean isSearchTerm(String palavra) {
        return personalSearchTerms.contains(palavra);
    }

    /**
     * Separa os caracteres de pontuação com espaço
     *
     * @param str a string a ter os caracteres de pontuação separados por espaço
     * @return a string com os caracteres de pontuação separados por espaço
     */
    public String separatePunctuation(String str) {
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
        return str;
    }

    /**
     * Subsitui os caracteres com acento na string por caracteres sem acento
     *
     * @param str a string a ter os caracteres com acentos substituídos por sem
     * acento
     * @return
     */
    public String removeAccents(String str) {
        str = str.replaceAll("[ÂÀÁÄÃ]", "A");
        str = str.replaceAll("[âãàáä]", "a");
        str = str.replaceAll("[ÊÈÉË]", "E");
        str = str.replaceAll("[êèéë]", "e");
        str = str.replaceAll("[ÎÍÌÏ]", "I");
        str = str.replaceAll("[îíìï]", "i");
        str = str.replaceAll("[ÔÕÒÓÖ]", "O");
        str = str.replaceAll("[ôõòóö]", "o");
        str = str.replaceAll("[ÛÙÚÜ]", "U");
        str = str.replaceAll("[ûúùü]", "u");
        str = str.replaceAll("Ç", "C");
        str = str.replaceAll("ç", "c");
        return str;
    }

    /**
     * Checa se a página passada como parâmetro é uma página pessoal
     *
     * @param page página para ser checada se é uma página de citações
     * @return true caso a página seja um página pessoal e false caso contrário
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean isCitationPage(WebPage page) throws FileNotFoundException, IOException {
        StringBuilder result = new StringBuilder();
        boolean containsPersonalInfo = containsPersonalInfo(page);
        boolean nameInLink = checkNameInLink(pesquisador.getName(), page.getLink());
        boolean nameInTitle = checkNameInTitle(pesquisador.getName(), page.getTitle());
        Desambiguacao desambiguacao = new Desambiguacao(page.getPath());
        float taxa = desambiguacao.rateCitationsFromPage(pesquisador.getName());
        String nome = pesquisador.getName().replace(" ", "");
        String path = "./Resultados/Processamento/" + nome + "/";
        System.out.println("");
        System.out.println("");
        System.out.println("##### isCitationPage #####");
        System.out.println(page.getTitle());
        System.out.println(page.getLink());
        System.out.println("containsPersonalInfo: " + containsPersonalInfo);
        System.out.println("nameInLink: " + nameInLink);
        System.out.println("nameInTitle:" + nameInTitle);
        System.out.println("taxa: " + taxa);
        if ((containsPersonalInfo && taxa > 0.7)) {
            System.out.println("Passou no critério 1.");
            File dir = new File(path);
            boolean directoryResult = dir.mkdir();
            Files.writeFile(page.getContent(), path + page.getTitle() + ".html");
            System.out.println("######################");
            return true;
        } else if ((taxa > 0.7) && (nameInLink || nameInTitle)) {
            System.out.println("Passou no critério 2.");
            File dir = new File(path);
            boolean directoryResult = dir.mkdir();
            Files.writeFile(page.getContent(), path + page.getTitle() + ".html");
            System.out.println("######################");

            return true;
        } else if (desambiguacao.isAuthorCitationsInExtraction(pesquisador) && (nameInLink || nameInTitle)) {
            System.out.println("Passou no critério 3.");
            File dir = new File(path);
            boolean directoryResult = dir.mkdir();
            Files.writeFile(page.getContent(), path + page.getTitle() + ".html");
            System.out.println("######################");
            return true;
        }

        System.out.println("######################");
        System.out.println("");
        System.out.println(result);
        return false;
    }

    /**
     * Checa se a página passada como parâmetro é uma página pessoal
     *
     * @param page página para ser checada se é uma página de citações
     * @return true caso a página seja um página pessoal e false caso contrário
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean isCitationPage2(WebPage page) throws FileNotFoundException, IOException {
//        System.out.println("iniciando metodo isCitationPage");
        StringBuilder result = new StringBuilder();
        boolean containsPersonalInfo = containsPersonalInfo(page);
        boolean nameInLink = checkNameInLink(pesquisador.getName(), page.getLink());
        boolean nameInTitle = checkNameInTitle(pesquisador.getName(), page.getTitle());
        Desambiguacao desambiguacao = new Desambiguacao(page.getPath());
        System.out.println(page.getPath());
        float taxa = desambiguacao.rateCitationsFromPage(pesquisador.getName());
        String nome = pesquisador.getName().replace(" ", "");
        String path = "./Resultados/Processamento/" + nome + "/";
        if ((containsPersonalInfo && nameInLink) || (containsPersonalInfo && nameInTitle)) {

            File dir = new File(path);
            boolean directoryResult = dir.mkdir();
            Files.writeFile(page.getContent(), path + page.getTitle() + ".html");
            return true;
        } else if (taxa > 0.7) {
            File dir = new File(path);
            boolean directoryResult = dir.mkdir();
            Files.writeFile(page.getContent(), path + page.getTitle() + ".html");
            return true;
        } else if (desambiguacao.isAuthorCitationsInExtraction(pesquisador)) {

            File dir = new File(path);
            boolean directoryResult = dir.mkdir();
            Files.writeFile(page.getContent(), path + page.getTitle() + ".html");
            return true;
        }
        System.out.println("");
        System.out.println(result);
        return false;
    }

    /**
     * Procura o nome (o nome é quebrado em seus nomes constituentes) na string
     * passada como parâmetro
     *
     * @param nome o nome a ser pesquisado
     * @param text a string que será pesquisada
     * @return true caso o nome seja encontrado, false caso contrário
     */
    public boolean searchName(String nome, String text) {
        String nomes[] = nome.split("\\s+");
//        for (String string : nomes) {
//            System.out.println(string);
//        }
        for (int i = 0; i < nomes.length; i++) {
            if (text.contains(nome));
            return true;
        }
        return false;
    }

    /**
     * Avalia se a página é uma página de currículo pessoal, se for retorna
     * true, caso contrário retorna false
     *
     * @param page O objeto WebPage da página
     * @return true se é uma página pessoal
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean containsPersonalInfo(WebPage page) throws FileNotFoundException, IOException {
//        String teste = "professor";
//        String a = searchTermos.get(teste);
//        if (searchTermos.contains(teste)) {
//            System.out.println("ACHOU!!!!!!!!");
//        } 

        String paginaLimpa = cleanHtmlFirst1000Characters(page.getPath());
        paginaLimpa = separatePunctuation(paginaLimpa);
        paginaLimpa = removeAccents(paginaLimpa);
        paginaLimpa = paginaLimpa.toLowerCase();
//        String lines[] = paginaSemTags.split(System.getProperty("line.separator"));
//        System.out.println(lines.length);
//        for (String string : lines) {
//            System.out.println(string);
//        }
        String temp;
        String palavras[];
        int counter = 0;
//        for (int i = 0; i < lines.length; i++) {
        palavras = paginaLimpa.split(" ");
        for (int j = 0; j < palavras.length; j++) {
            counter += palavras[j].length() + 1;
            //System.out.println(palavras[j]);
            temp = personalSearchTerms.get(palavras[j]);
            if (temp != null) {
                int before = ((counter - 100) >= 0) ? counter - 100 : 0;
                int after = ((counter + 100) <= paginaLimpa.length()) ? counter + 100 : paginaLimpa.length();
                if (searchName(pesquisador.getName(), paginaLimpa.substring(before, after))) {
                    return true;
                }

            }
        }
//        }
        return false;
    }

    public static boolean checkURL(String link) {
        try {

            URL url = new URL(link);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("HEAD");
            httpUrlConnection.connect();
            int code = httpUrlConnection.getResponseCode();
            if (code == 200) {
                String content = httpUrlConnection.getContentType();
//                System.out.println(content);
                if (content.contains("text/html")) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean checkURL2(String link) {
//        String urlRegex = "^(http|https|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
//        if (url.matches(urlRegex)) {
//            return true;
//        }
        try {

            URL url = new URL(link);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();
            int code = httpUrlConnection.getResponseCode();
            if (code == 200) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public void searchReferencesToCitationPage(WebPage page) throws IOException {
        String tempStr;
        WebPage tempWebPage;
        String stringOfPage = page.getContent();
        Document docLk = Jsoup.parse(stringOfPage);

        Elements links = docLk.select("a").select("*");
        for (Element el : links) {
            String linkHref = el.attr("href");
            String text = el.text();
            text = text.toLowerCase();

            tempStr = citationReferenceTerms.get(text);
            if (tempStr != null) { // && contains(".pdf)
                System.out.println(tempStr);
                System.out.println(linkHref);
                // texto com nome de publicacao, cria webpage a partir do link
                if (!linkHref.contains(".pdf") && checkURL(linkHref)) {
                    tempWebPage = new WebPage(linkHref);
                    String nome = pesquisador.getName().replace(" ", "");
                    String path = "./Resultados/PesquisaGoogle/" + nome + "/";
                    String pathToFileHtml = path + tempWebPage.getTitle() + ".html";
                    Files.writeFile(tempWebPage.getContent(), pathToFileHtml);
                    tempWebPage.setPath(pathToFileHtml);
                    isCitationPage(tempWebPage);
                    System.out.println(tempWebPage);
                }
            }

        }
    }
}
