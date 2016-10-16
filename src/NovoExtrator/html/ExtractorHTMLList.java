/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.html;

import NovoExtrator.extrator.ListCreator;
import NovoExtrator.extrator.Main;
import NovoExtrator.filehandlers.FileHandler;
import NovoExtrator.filehandlers.FormatsCalculus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import NovoExtrator.structures.HashToken;
import NovoExtrator.structures.PCitation;
import NovoExtrator.structures.Storage;
import NovoExtrator.structures.TokenH;
import NovoExtrator.structures.TokenNP;
import javax.sound.midi.Soundbank;

/**
 *
 * @author cristiano
 */
public class ExtractorHTMLList {

    public void openFileAndLoadHTML(String filename) throws FileNotFoundException {
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(is, cs);
        BufferedReader in = new BufferedReader(isr);
    }

    /**
     * Método utilizado apenas uma vez para extração dos dados dos XML contendo
     * coleção de conferencias e journals
     *
     * @param ht
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void print(HashToken ht) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("ISO8859-1");
        for (int i = 0; i < 2; i++) {
            File f = null;

            if (i == 0) {
                f = new File("conference-collection.xml");
            } else if (i == 1) {
                f = new File("journal-collection.xml");
            }


            InputStream is = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(is, cs);
            BufferedReader in = new BufferedReader(isr);

            while (in.ready()) {
                String v = in.readLine();
                String r = "";
                boolean changeR = false;
                if (v.matches(".*<title>([^<]*)</title>.*")) {

                    r = (v.replaceAll(".*<title>([^<]*)</title>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<title-abrev>([^<]*)</title-abrev>.*")) {
                    r = (v.replaceAll(".*<title-abrev>([^<]*)</title-abrev>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<title-formerly>([^<]*)</title-formerly>.*")) {
                    r = (v.replaceAll(".*<title-formerly>([^<]*)</title-formerly>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<title-merge>([^<]*)</title-merge>.*")) {
                    r = (v.replaceAll(".*<title-merge>([^<]*)</title-merge>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<merge-of-title>([^<]*)</merge-of-title>.*")) {
                    r = (v.replaceAll(".*<merge-of-title>([^<]*)</merge-of-title>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<acronym>([^<]*)</acronym>.*")) {
                    r = (v.replaceAll(".*<acronym>([^<]*)</acronym>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<acronym-formerly>([^<]*)</acronym-formerly>.*")) {
                    r = (v.replaceAll(".*<acronym-formerly>([^<]*)</acronym-formerly>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<merge-of-acronym>([^<]*)</merge-of-acronym>.*")) {
                    r = (v.replaceAll(".*<merge-of-acronym>([^<]*)</merge-of-acronym>.*", "$1"));
                    changeR = true;
                } else if (v.matches(".*<publisher>([^<]*)</publisher>.*")) {
                    String p = (v.replaceAll(".*<publisher>([^<]*)</publisher>.*", "$1"));
                    String[] values = p.split("[ \n\\.]");
                    for (String va : values) {
                        ////system.out.println(va + "pub");
                        String valor = FileHandler.removeCharacters(va).trim();
                        ht.put(valor, new TokenH("publisher", 10000));
                    }
                }

                if (changeR) {
                    String[] values = r.split("[ \n\\.]");
                    for (String va : values) {
                        ////system.out.println(va + "booktitle");
                        String valor = FileHandler.removeCharacters(va).trim();
                        if (i == 0) {
                            ht.put(valor, new TokenH("booktitle", 10000));
                        } else if (i == 1) {
                            ht.put(valor, new TokenH("journal", 10000));
                        }
                    }
                }
            }
        }
    }

    /**
     * MÉTODO PRINCIPAL!
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {


        Element e = new Element();
        HashToken ht = new HashToken();
        HashToken hsw = new HashToken();    /* Hash for Stopwords. */
        HashToken hci = new HashToken();    /* Hash for Cities and Countries. */
        HashToken hk = new HashToken();    /* Hash for Keywords. */
        FileHandler fh = new FileHandler();
        ListCreator lc = new ListCreator();

        FormatsCalculus fc = new FormatsCalculus();


        if (!ht.loadPreviousRecordedFileToHash("/home/armando/NetBeansProjects/Pesquisa/Arquivos/Extrator/dadoshash_basebib2MEDIA1.txt")) {
            //system.out.println("Não carregou o arquivo");
            /**
             * Obtem dados para Hash
             */
            fh.addTokensFromBibTex(ht, hsw, "/home/armando/NetBeansProjects/Pesquisa/Arquivos/Extrator/baseReferenciaBibTexMEDIA.txt");
            //system.out.println("Base .bib Loaded!");
            fh.addTokensFromXML(ht, hsw, hci, "/home/armando/NetBeansProjects/Pesquisa/Arquivos/Extrator/dblp.xml", "author");
            //system.out.println("Base author DBLP Loaded!");
            fh.addTokensFromXML(ht, hsw, hci, "/home/armando/NetBeansProjects/Pesquisa/Arquivos/Extrator/dblp.xml", "journal");
            //system.out.println("Base journal DBLP Loaded!");
            //system.out.println("Author's Base DBLP Loaded!");
        } else {
            /**
             * Hash já foi carregada
             */
            //system.out.println(ht.size());
            Main.CheatBase(ht);
            ht.remove("authors");
            ht.recordFileFromHash("/home/armando/NetBeansProjects/Pesquisa/Arquivos/Extrator/dadoshash_basebib2MEDIA1.txt");
        }
        ht.recordFileFromHash("/home/armando/NetBeansProjects/Pesquisa/Arquivos/Extrator/dadoshash_basebib2MEDIA1.txt");
        fh.saveHashAsTxt(ht, "hashaux.txt");
        /*
         * Cria hash com stopwords
         */
        fh.addStopWordsInTheHash(hsw, "/home/armando/NetBeansProjects/Pesquisa/Arquivos/Extrator/stopwords.txt");


        /**
         * Carrega hash de cidades e países
         */
        File g = new File("location/");
        File[] fs = (g.listFiles());
        Arrays.sort(fs);

        for (File s : g.listFiles()) {
            String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 5);
            if (!ext.equalsIgnoreCase(".data")) {
                continue;
            }
            fh.addTokensFromTxt(ht, hsw, ht, s.getAbsolutePath());
        }


        /**
         * Carrega hash com palavras chaves
         */
        g = new File("keywords/");

        for (File s : g.listFiles()) {
            String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 4);

            if (!ext.equalsIgnoreCase(".txt")) {
                continue;
            }
            fh.addTokensFromTxt(hk, s.getAbsolutePath());
        }

        /**
         * Abre pasta com páginas Web a serem extraídos as citações
         */
        //g = new File("testes/TesteHTML/testeUOT/");
        g = new File("testes/TesteHTML/testeUOT/");
        for (File s : g.listFiles()) {

            String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 4);
            String nome = s.getName();
            //system.out.println(nome);

            /**
             * Se página nao tem extensão HTML, então é ignorado.
             */
            if (!ext.equalsIgnoreCase("html")) {
                continue;
            }

            //system.out.println(s.getAbsolutePath()); // LINHA ADICIONADA!!!!

            Charset cs = Charset.forName("ISO8859-1");
            File f = new File(s.getAbsolutePath());
            InputStream is = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(is, cs);
            BufferedReader in = new BufferedReader(isr);

            StringBuilder text = new StringBuilder();

            while (in.ready()) {
                text.append(" ").append(in.readLine());
            }

            String texto = text.toString();

            /**
             * Extrai tags de estilo e decodifica texto html caso necessário
             */
            texto = ListCreator.decideWhichFuncTakeTagsOff(texto);
            texto = ListCreator.fixTextNumericCodification(texto);
            texto = ListCreator.fixTextCodification(texto);
            texto = texto.replaceAll(" +", " ");
            texto = texto.replaceAll("\\.+", ".");

            texto = texto.replaceAll("([^\\d\\w]+)([0-9]{4})", "$1 $2");
            texto = texto.replaceAll("([a-zA-Z])([0-9]+)", "$1 $2");

            boolean b = false;
            StringBuilder sb = new StringBuilder();
            Element father = null;

            LinkedList<TokenNP> t = new LinkedList();


            /**
             * Monta Lista DOM
             */
            for (int i = 0; i < texto.length(); i++) {
                if (texto.charAt(i) == '<' && !b) {
                    if (!sb.toString().trim().equals("")) {


                        String[] palavras =
                                FileHandler.justIsolatePunctuationCharacters(
                                sb.toString().toLowerCase()).split("[ ]");

                        StringBuilder temp = new StringBuilder();
                        LinkedList<String> auxTemp = new LinkedList();
                        LinkedList<String> auxValue = new LinkedList();

                        boolean pdelimitador_anterior = false;
                        String pdelimitador = "";

                        for (String p : palavras) {
                            if (p.equalsIgnoreCase("")) {
                                continue; //se esta vazio
                            }
                            if (p.matches("[ ]+")) {            //se tem mais de ume spaço
                                temp.append(" ");
                                continue;
                            }


                            if (ListCreator.isPDelimiter(p.trim()) /*|| FileHandler.isNumber(p.trim())*/) {


                                if (auxTemp.size() > 0) {
                                    lc.calculateBlockClass(hk, ht, hsw, hci, auxTemp, auxValue, fc, 0.5);

                                    t.add(new TokenNP(temp.toString().trim(), auxValue.get(0)));
                                }

                                t.add(new TokenNP(p.trim(), "p-delimiter"));
                                temp = new StringBuilder();
                                auxTemp.clear();
                                auxValue.clear();
                                pdelimitador_anterior = true;
                                pdelimitador = p;
                                continue;
                            }



                            auxTemp.add(p.trim() + (" "));
                            auxValue.add("");
                            temp.append(p.trim()).append(" ");
                            ////system.out.println(p);
                        }

                        if (temp.toString().length() > 0) {
                            lc.calculateBlockClass(hk, ht, hsw, hci, auxTemp, auxValue, fc, 0.5);
                            ////system.out.println(temp.toString().trim() + " >> " + auxValue.get(0));
                            t.add(new TokenNP(temp.toString().trim(), auxValue.get(0)));
                        }

                        sb = new StringBuilder();
                    }
                    b = true;
                } else if ((texto.charAt(i) == '>') && b) {
                    t.add(new TokenNP("<" + sb.toString().trim() + ">", "tag"));

                    b = false;
                    father = e;
                    sb = new StringBuilder();
                } else if (b) {
                    sb.append(texto.charAt(i));
                } else if (!b) {
                    sb.append(texto.charAt(i));
                }
            }

            LinkedList<TokenNP> novalista = normalization(t);

            HashMap<String, Integer> anterior = new HashMap();
            HashMap<String, Integer> posterior = new HashMap();

            LinkedList<LinkedList<TokenNP>> tok = ExtractorHTMLList.getCitations(novalista, anterior, posterior);


            tok = ExtractorHTMLList.getCitations_Last(novalista, anterior, posterior);

            ExtractorHTML.printCollection(tok, "testes/TesteHTML/testeUOT/Saída1/" + nome + "_saida.html", 0);

        }
    }

    public static LinkedList<LinkedList<TokenNP>> getCitations(String path) throws FileNotFoundException, IOException {

        Element e = new Element();
        HashToken ht = new HashToken();
        HashToken hsw = new HashToken();    /* Hash for Stopwords. */
        HashToken hci = new HashToken();    /* Hash for Cities and Countries. */
        HashToken hk = new HashToken();    /* Hash for Keywords. */
        FileHandler fh = new FileHandler();
        ListCreator lc = new ListCreator();

        FormatsCalculus fc = new FormatsCalculus();
        if (!ht.loadPreviousRecordedFileToHash("./Arquivos/Extrator/dadoshash_basebib2MEDIA1.txt")) {
            //system.out.println("Não carregou o arquivo");
            /**
             * Obtem dados para Hash
             */
            fh.addTokensFromBibTex(ht, hsw, "./Arquivos/Extrator/baseReferenciaBibTexMEDIA.txt");
            //system.out.println("Base .bib Loaded!");
            fh.addTokensFromXML(ht, hsw, hci, "./Arquivos/Extrator/dblp.xml", "author");
            //system.out.println("Base author DBLP Loaded!");
            fh.addTokensFromXML(ht, hsw, hci, "./Arquivos/Extrator/dblp.xml", "journal");
            //system.out.println("Base journal DBLP Loaded!");
            //system.out.println("Author's Base DBLP Loaded!");
        } else {
            /**
             * Hash já foi carregada
             */
            //system.out.println(ht.size());
            Main.CheatBase(ht);
            ht.remove("authors");
            ht.recordFileFromHash("./Arquivos/Extrator/dadoshash_basebib2MEDIA1.txt");
        }
        ht.recordFileFromHash("./Arquivos/Extrator/dadoshash_basebib2MEDIA1.txt");
        fh.saveHashAsTxt(ht, "./Arquivos/Extrator/hashaux.txt");
        /*
         * Cria hash com stopwords
         */
        fh.addStopWordsInTheHash(hsw, "./Arquivos/Extrator/stopwords.txt");


        /**
         * Carrega hash de cidades e países
         */
        File g = new File("./Arquivos/Extrator/location/");
        File[] fs = (g.listFiles());
        Arrays.sort(fs);

        for (File s : g.listFiles()) {
            String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 5);
            if (!ext.equalsIgnoreCase(".data")) {
                continue;
            }
            fh.addTokensFromTxt(ht, hsw, ht, s.getAbsolutePath());
        }


        /**
         * Carrega hash com palavras chaves
         */
        g = new File("./Arquivos/Extrator/keywords/");

        for (File s : g.listFiles()) {
            String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 4);

            if (!ext.equalsIgnoreCase(".txt")) {
                continue;
            }
            fh.addTokensFromTxt(hk, s.getAbsolutePath());
        }

        /**
         * Abre pasta com páginas Web a serem extraídos as citações
         */
        //g = new File("testes/TesteHTML/testeUOT/");
        g = new File(path); // ABRO O DIRETÓRIO PASSADO COMO PARÂMETRO!!!!!


        String ext = path.substring(path.length() - 4);

        /**
         * Se página nao tem extensão HTML, então é ignorado.
         */
        if (!ext.equalsIgnoreCase("html")) {
            return null;
        }

        //system.out.println(path); // LINHA ADICIONADA!!!!

        Charset cs = Charset.forName("UTF-8");
        File f = new File(path);
        InputStream is = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(is, cs);
        BufferedReader in = new BufferedReader(isr);

        StringBuilder text = new StringBuilder();

        while (in.ready()) {
            text.append(" ").append(in.readLine());
        }

        String texto = text.toString();

        /**
         * Extrai tags de estilo e decodifica texto html caso necessário
         */
        texto = ListCreator.decideWhichFuncTakeTagsOff(texto);
        texto = ListCreator.fixTextNumericCodification(texto);
        texto = ListCreator.fixTextCodification(texto);
        
//        System.out.println("imprimindo o html:");
//        System.out.println(texto);
//        
        texto = texto.replaceAll(" +", " ");
        texto = texto.replaceAll("\\.+", ".");

        texto = texto.replaceAll("([^\\d\\w]+)([0-9]{4})", "$1 $2");
        texto = texto.replaceAll("([a-zA-Z])([0-9]+)", "$1 $2");

        boolean b = false;
        StringBuilder sb = new StringBuilder();
        Element father = null;

        LinkedList<TokenNP> t = new LinkedList();


        /**
         * Monta Lista DOM
         */
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == '<' && !b) {
                if (!sb.toString().trim().equals("")) {


                    String[] palavras =
                            FileHandler.justIsolatePunctuationCharacters(
                            sb.toString().toLowerCase()).split("[ ]");

                    StringBuilder temp = new StringBuilder();
                    LinkedList<String> auxTemp = new LinkedList();
                    LinkedList<String> auxValue = new LinkedList();

                    boolean pdelimitador_anterior = false;
                    String pdelimitador = "";

                    for (String p : palavras) {
                        if (p.equalsIgnoreCase("")) {
                            continue; //se esta vazio
                        }
                        if (p.matches("[ ]+")) {            //se tem mais de ume spaço
                            temp.append(" ");
                            continue;
                        }


                        if (ListCreator.isPDelimiter(p.trim()) /*|| FileHandler.isNumber(p.trim())*/) {


                            if (auxTemp.size() > 0) {
                                lc.calculateBlockClass(hk, ht, hsw, hci, auxTemp, auxValue, fc, 0.5);

                                t.add(new TokenNP(temp.toString().trim(), auxValue.get(0)));
                            }

                            t.add(new TokenNP(p.trim(), "p-delimiter"));
                            temp = new StringBuilder();
                            auxTemp.clear();
                            auxValue.clear();
                            pdelimitador_anterior = true;
                            pdelimitador = p;
                            continue;
                        }



                        auxTemp.add(p.trim() + (" "));
                        auxValue.add("");
                        temp.append(p.trim()).append(" ");
                        ////system.out.println(p);
                    }

                    if (temp.toString().length() > 0) {
                        lc.calculateBlockClass(hk, ht, hsw, hci, auxTemp, auxValue, fc, 0.5);
                        ////system.out.println(temp.toString().trim() + " >> " + auxValue.get(0));
                        t.add(new TokenNP(temp.toString().trim(), auxValue.get(0)));
                    }

                    sb = new StringBuilder();
                }
                b = true;
            } else if ((texto.charAt(i) == '>') && b) {
                t.add(new TokenNP("<" + sb.toString().trim() + ">", "tag"));

                b = false;
                father = e;
                sb = new StringBuilder();
            } else if (b) {
                sb.append(texto.charAt(i));
            } else if (!b) {
                sb.append(texto.charAt(i));
            }
        }

        LinkedList<TokenNP> novalista = normalization(t);

        HashMap<String, Integer> anterior = new HashMap();
        HashMap<String, Integer> posterior = new HashMap();

        LinkedList<LinkedList<TokenNP>> tok = ExtractorHTMLList.getCitations(novalista, anterior, posterior);


        tok = ExtractorHTMLList.getCitations_Last(novalista, anterior, posterior);

        return tok;

    }

    /**
     * Método que agrupa os símbolos aos tokens anteriores.
     *
     * @param original
     * @return
     */
    public static LinkedList<TokenNP> normalization(LinkedList<TokenNP> original) {
        LinkedList<TokenNP> newOne = new LinkedList();
        LinkedList<TokenNP> aux = new LinkedList();
        String lastOne = null;
        int position = -1;

        restart:
        for (int i = 0; i < original.size(); ++i) {
            TokenNP t = original.get(i);


            if (t.getTag().equalsIgnoreCase("tag")) {
                lastOne = "tag";
                //position = -1;
                newOne.add(t);
            } else if (t.getTag().equalsIgnoreCase("p-delimiter")) {
                newOne.add(t);
            } else if (t.getTag().equalsIgnoreCase("symbol")) {
                newOne.add(t);
            } else {
                //position = i;
                //if (original.get(i).getTag().equalsIgnoreCase(lastOne) /*&& !lastOne.equalsIgnoreCase("tag")*/){
                TokenNP grupo = new TokenNP("", lastOne);

                /*for (int j = position; j <= i; j++){
                 aux.add(original.get(j));
                 }*/
                lastOne = t.getTag();
                for (int k = i; k < original.size(); k++) {
                    if (original.get(k).getTag().equalsIgnoreCase(lastOne)
                            || original.get(k).getTag().equalsIgnoreCase("symbol")
                            || original.get(k).getTag().equalsIgnoreCase("p-delimiter")
                            || original.get(k).getTag().equalsIgnoreCase(" ")) {
                        aux.add(original.get(k));
                    } else {
                        String aux1 = new String();

                        for (int j = 0; j < aux.size(); j++) {
                            aux1 = aux1 + " " + aux.get(j).getToken();
                        }

                        newOne.add(new TokenNP(aux1, lastOne));

                        i = k - 1;
                        break;
                    }
                }



                aux.clear();

                lastOne = null;

            }
        }

        return newOne;
    }

    /**
     * Método que obtem as citações e procura o padrão entre tags anteriores e
     * tags posterioes à potencial citação
     *
     * @param l
     * @param anterior
     * @param posterior
     * @return
     */
    public static LinkedList<LinkedList<TokenNP>> getCitations(LinkedList<TokenNP> l, HashMap<String, Integer> anterior, HashMap<String, Integer> posterior) {
        ListCreator lc = new ListCreator();
        LinkedList<String> types = new LinkedList();
        LinkedList<String> tokens = new LinkedList();
        LinkedList<TokenNP> p_list = new LinkedList();

        LinkedList<LinkedList<TokenNP>> saida = new LinkedList();
        Storage st = new Storage();

        boolean flag = false;
        for (int i = 0; i < l.size(); i++) {
            TokenNP t = l.get(i);
            ////system.out.println("Tag atual: "+ t.getToken() + "<" + t.getTag() + ">");
            ////system.out.println(t.getToken() + "|" + t.getTag() + flag);

            if (!t.getTag().equalsIgnoreCase("tag")) {
                p_list.add(t);

                tokens.add(t.getToken());
                types.add(t.getTag());

                flag = true;
                ////system.out.println("1");
            } else if (t.getTag().equalsIgnoreCase("tag") && flag) {
                ////system.out.println(tokens + ">>" + types);

                if (lc.verifyCitation(types, tokens)) {
                    /* Insere tags anteriores */

                    if (anterior.containsKey(l.get(i - types.size() - 1).getToken())) {
                        anterior.put(
                                l.get(i - types.size() - 1).getToken(),
                                anterior.get(l.get(i - types.size() - 1).getToken()) + 1);
                    } else {
                        anterior.put(l.get(i - types.size() - 1).getToken(), 1);
                    }

                    // Insere tags posteriores 
                    if (posterior.containsKey(l.get(i).getToken())) {
                        posterior.put(
                                l.get(i).getToken(),
                                posterior.get(l.get(i).getToken()) + 1);
                    } else {
                        posterior.put(l.get(i).getToken(), 1);
                    }

                    st.insertIntoStorage(i - types.size(), i - 1, null);


                }

                flag = false;

                tokens.clear();
                types.clear();
                p_list.clear();

                ////system.out.println("2");
            }
        }

        //System.exit(0);

        LinkedList<TokenNP> lista = new LinkedList();

        for (PCitation p : st.getStorage()) {

            for (int i = p.getBegin(); i <= p.getEnd(); i++) {
                lista.add(l.get(i));
            }
            if (lista.size() > 0) {

                for (int k = 0; k < lista.size(); k++) {
                    if (lista.get(k).getTag().equalsIgnoreCase("tag")) {
                        lista.remove(k);
                    }
                }

                saida.add(lista);
                ////system.out.println(">>>>>>>>>" + Main.printLists(lista));
                //System.exit(0);
                lista = new LinkedList();
            }
        }


        return saida;
    }

    /**
     * Método que extrai as citações ignorando a premissa de que as citações
     * precisam estar entre tags.
     *
     * @param l
     * @param anterior
     * @param posterior
     * @return
     */
    public static LinkedList<LinkedList<TokenNP>> getCitationsIgnoringTags(LinkedList<TokenNP> l, HashMap<String, Integer> anterior, HashMap<String, Integer> posterior) {
        ListCreator lc = new ListCreator();
        LinkedList<String> types = new LinkedList();
        LinkedList<String> tokens = new LinkedList();
        LinkedList<TokenNP> p_list = new LinkedList();
        Storage st = new Storage();
        LinkedList<LinkedList<TokenNP>> saida = new LinkedList();
        boolean tag = false, iniciou = false;
        int piece = 0, pivot = 0, inicio = 0, fim = 0;

        for (int i = 0; i < l.size(); ++i) {
            TokenNP t = l.get(i);
            tag = (t.getTag().equalsIgnoreCase("tag"));

            if (tag && !iniciou) {
                iniciou = true;
                inicio = i;
                piece = 1;
            } else if (!tag && iniciou) {
                p_list.add(t);
                types.add(t.getTag());
                tokens.add(t.getToken());
            } else if (tag && iniciou) {
                if (lc.verifyCitation(types, tokens)) {
                    fim = i;

                    int firstTag = -1;
                    int endTag = -1;

                    for (int k = i - types.size() + 1; k >= 0; k--) {
                        ////system.out.println(k + " " + l.get(k).getTag());
                        if (l.get(k).getTag().trim().equalsIgnoreCase("tag")) {
                            firstTag = k;
                            break;
                        }
                    }

                    for (int k = i - 1; k < l.size(); k++) {
                        if (l.get(k).getTag().trim().equalsIgnoreCase("tag")) {
                            endTag = k;
                            break;
                        }
                    }
                    //system.out.println("POTENCIAL CITAÇÃO");
                    //system.out.println(tokens);

                    if (anterior.containsKey(l.get(firstTag).getToken())) {
                        anterior.put(
                                l.get(firstTag).getToken(),
                                anterior.get(l.get(firstTag).getToken()) + 1);
                    } else {
                        anterior.put(l.get(firstTag).getToken(), 1);
                    }

                    // Insere tags posteriores 
                    if (posterior.containsKey(l.get(endTag).getToken())) {
                        posterior.put(
                                l.get(endTag).getToken(),
                                posterior.get(l.get(endTag).getToken()) + 1);
                    } else {
                        posterior.put(l.get(endTag).getToken(), 1);
                    }

                    st.insertIntoStorage(firstTag, endTag, null);

                    p_list = new LinkedList();
                    types = new LinkedList();
                    tokens = new LinkedList();
                    i = inicio + 1;
                    iniciou = false;
                } else if (lc.containsMininum(types)) {
                    //system.out.println("CONTEM MINIMO");
                    //system.out.println(tokens);
                    //system.out.println(types);
                    //system.out.println(piece);
                    piece++;
                    if (piece == 2) {
                        pivot = i;
                    } else if (piece == 6) {
                        iniciou = false;
                        i = pivot;
                        p_list = new LinkedList();
                        types = new LinkedList();
                        tokens = new LinkedList();
                    }
                }
            }
        }

        LinkedList<TokenNP> lista = new LinkedList();

        for (PCitation p : st.getStorage()) {

            for (int i = p.getBegin(); i <= p.getEnd(); i++) {
                lista.add(l.get(i));
            }
            if (lista.size() > 0) {

                for (int k = 0; k < lista.size(); k++) {
                    if (lista.get(k).getTag().equalsIgnoreCase("tag")) {
                        lista.remove(k);
                    }
                }

                saida.add(lista);
                ////system.out.println(">>>>>>>>>" + Main.printLists(lista));
                //System.exit(0);
                lista = new LinkedList();
            }
        }

        return saida;
    }

    /**
     * Último passo da extração. À partir dos padrões detectados, reverifica o
     * arquivo, principalmente entre as tags mais frequentes.
     *
     * @param l
     * @param anterior
     * @param posterior
     * @return
     */
    public static LinkedList<LinkedList<TokenNP>> getCitations_Last(LinkedList<TokenNP> l, HashMap<String, Integer> anterior, HashMap<String, Integer> posterior) {
        /* Variaveis */
        ListCreator lc = new ListCreator();
        LinkedList<String> types = new LinkedList();
        LinkedList<String> tokens = new LinkedList();
        LinkedList<TokenNP> p_list = new LinkedList();

        LinkedList<LinkedList<TokenNP>> saida = new LinkedList();


        /**
         * Pega os mais frequentes *
         */
        Set<String> ant = anterior.keySet();
        Set<String> pos = posterior.keySet();

        LinkedList<String> ant_l = new LinkedList();
        LinkedList<String> pos_l = new LinkedList();

        String ant_m = "";
        String pos_m = "";

        int ant_q = 0;
        int pos_q = 0;

        ////system.out.println("Posteriores");
        for (String v : pos) {
            //system.out.println(v + ": " + posterior.get(v));
            if (posterior.get(v) > pos_q) {
                pos_m = v;
                pos_q = posterior.get(v);
            }
        }

        /* 3.5 valor arbitrario */
        for (String v : pos) {
            if (pos_q < (3.5 * posterior.get(v))) {
                pos_l.add(v);
            }
        }


        ////system.out.println("Anteriores");
        for (String v : ant) {
            //system.out.println(v + ": " + anterior.get(v));
            if (anterior.get(v) > ant_q) {
                ant_m = v;
                ant_q = anterior.get(v);
            }
        }

        for (String v : ant) {
            if (ant_q < (3.5 * anterior.get(v))) {
                ant_l.add(v);
            }
        }

        /* Busca pelos padroes */
        //system.out.println("ANTERIOR ESCOLHIDO: " + ant_m);
        //system.out.println("POSTERIOR ESCOLHIDO:" + pos_m);
        boolean open = false;

        for (TokenNP t : l) {
            /* Se tag de inicio e fim forem diferentes */
            if (!ant_m.equalsIgnoreCase(pos_m)) {
                //if (ant_l.contains(t.getToken()) && !open){
                if (t.getToken().equalsIgnoreCase(ant_m) && !open) {
                    ////system.out.println("Começou");
                    open = true;
                } else if (t.getToken().equalsIgnoreCase(ant_m) && open) {
                    //}else if(ant_l.contains(t.getToken()) && open){
                    ////system.out.println("Começou falso e recomeçou");
                    types.clear();
                    tokens.clear();
                    p_list = new LinkedList();
                    open = true;
                    //}else if (pos_l.contains(t.getToken()) && !open){
                } else if (t.getToken().equalsIgnoreCase(pos_m) && !open) {
                    ////system.out.println("Terminou sem começar");
                    //}else if (pos_l.contains(t.getToken()) && open){                    
                } else if (t.getToken().equalsIgnoreCase(pos_m) && open) {
                    ////system.out.println("Fechou");
                    ////system.out.println(getPerc("author", types) + " " +tokens);
                    ////system.out.println(types);
                    ////system.out.println(getPerc("author", types) + " " + tokens);
                    if (lc.verifyCitationFlex(types, tokens)) {
                        //if (lc.verifyCitationFlex(types)){
                        saida.add(p_list);
                        types.clear();
                        tokens.clear();
                        p_list = new LinkedList();
                        open = false;
                    }
                } else if (open && !t.getTag().equalsIgnoreCase("tag")) {
                    ////system.out.println("Adicionando...");    
                    p_list.add(t);
                    types.add(t.getTag());
                    tokens.add(t.getToken());
                }
            } else {
                /* Se tags de inicio e fim forem iguais */
                //if (ant_l.contains(t.getToken()) && !open){
                if (t.getToken().equalsIgnoreCase(ant_m) && !open) {
                    ////system.out.println("Começou");
                    open = true;
                    //}else if(ant_l.contains(t.getToken()) && open){
                } else if (t.getToken().equalsIgnoreCase(ant_m) && open) {
                    ////system.out.println("Fechou");
                    ////system.out.println(getPerc("author", types) + " " + tokens);
                    //if (lc.verifyCitationFlex(types)){
                    if (lc.verifyCitationFlex(types, tokens)) {
                        saida.add(p_list);
                        types.clear();
                        tokens.clear();
                        p_list = new LinkedList();
                        open = false;
                    } else {
                        types.clear();
                        tokens.clear();
                        p_list = new LinkedList();
                    }
                } else if (open && !t.getTag().equalsIgnoreCase("tag")) {
                    ////system.out.println("Adicionando...");    
                    p_list.add(t);
                    types.add(t.getTag());
                    tokens.add(t.getToken());
                }

            }
        }

        ////system.out.println("TESTE" + Main.printLikeTree(saida));
        //System.exit(0);        
        return saida;
    }

    /**
     * Método que monta uma lista a partir do arquivo passado pelo parâmetro
     * filename
     *
     * @param filename
     * @param lc
     * @param ht
     * @param hsw
     * @param hci
     * @param fc
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static LinkedList<TokenNP> mountList(String filename, ListCreator lc, HashToken ht, HashToken hsw, HashToken hci, FormatsCalculus fc) throws FileNotFoundException, IOException {
        boolean b = false;
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(is, cs);
        BufferedReader in = new BufferedReader(isr);

        StringBuilder text = new StringBuilder();

        while (in.ready()) {
            text.append(" ").append(in.readLine());
        }

        String texto = text.toString();

        /**
         * Extrai tags de estilo e decodifica texto html caso necessário
         */
        texto = ListCreator.decideWhichFuncTakeTagsOff(texto);
        texto = ListCreator.fixTextNumericCodification(texto);
        texto = ListCreator.fixTextCodification(texto);


        StringBuilder sb = new StringBuilder();
        LinkedList<TokenNP> t = new LinkedList();

        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == '<' && !b) {
                if (!sb.toString().trim().equals("")) {


                    String[] palavras =
                            FileHandler.justIsolatePunctuationCharacters(
                            sb.toString().toLowerCase()).split("[ ]");

                    StringBuilder temp = new StringBuilder();
                    LinkedList<String> auxTemp = new LinkedList();
                    LinkedList<String> auxValue = new LinkedList();

                    ////system.out.println(sb.toString());
                    ////system.out.println(palavras.length);

                    for (String p : palavras) {
                        ////system.out.println(p);
                        if (p.equalsIgnoreCase("")) {
                            continue; //se esta vazio
                        }
                        if (p.matches("[ ]+")) {            //se tem mais de ume spaço
                            temp.append(" ");
                            continue;
                        }

                        if (ListCreator.isPDelimiter(p.trim())) {


                            if (auxTemp.size() > 0) {
                                lc.calculateBlockClass(ht, hsw, hci, auxTemp, auxValue, fc, texto, 0.5);
                                ////system.out.println(auxTemp.size() + " " + auxValue.size());
                                ////system.out.println(auxTemp.size() + auxTemp.get(0) + " " + auxValue.get(0));
                                t.add(new TokenNP(temp.toString().trim(), auxValue.get(0)));
                            }

                            t.add(new TokenNP(p.trim(), "symbol")); //p-delimiter
                            temp = new StringBuilder();
                            auxTemp.clear();
                            auxValue.clear();
                            continue;
                        }
                        auxTemp.add(p.trim() + (" "));
                        auxValue.add(" ");
                        temp.append(p.trim()).append(" ");
                        ////system.out.println(p);
                    }

                    if (temp.toString().length() > 0) {
                        lc.calculateBlockClass(ht, hsw, hci, auxTemp, auxValue, fc, texto, 0.5);
                        t.add(new TokenNP(temp.toString().trim(), auxValue.get(0)));
                    }

                    sb = new StringBuilder();
                }
                b = true;
            } else if ((texto.charAt(i) == '>') && b) {
                t.add(new TokenNP("<" + sb.toString().trim() + ">", "symbol")); //tag

                ////system.out.println(sb.toString() + ":tag");
                ////system.out.println("<" + sb.toString().trim() + ">");
                b = false;
                sb = new StringBuilder();
                /*}else if ((texto.charAt(i) == '/') && (texto.charAt(i - 1) == '<')){
                 //e.closeTag();
                 */            } else if (b) {
                sb.append(texto.charAt(i));
            } else if (!b) {
                sb.append(texto.charAt(i));
            }
        }

        return t;
    }
}
