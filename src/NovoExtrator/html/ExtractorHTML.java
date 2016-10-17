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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.String;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import NovoExtrator.structures.HashExternOperations;
import NovoExtrator.structures.HashToken;
import NovoExtrator.structures.SuperClasse;
import NovoExtrator.structures.TokenNP;

/**
 *
 */
public class ExtractorHTML {

    public ExtractorHTML() {
    }

    public void openFileAndLoadHTML(String filename) throws FileNotFoundException {
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(is, cs);
        BufferedReader in = new BufferedReader(isr);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        ////system.out.println("<teste class='class1'>".replaceAll("<([^ ]+) [^>]*>", "<$1>"));
        //System.exit(0);

        Element e = new Element();
        HashToken ht = new HashToken();
        HashToken hsw = new HashToken();    /* Hash for Stopwords. */
        HashToken hci = new HashToken();    /* Hash for Cities and Countries. */
        HashToken hk = new HashToken();    /* Hash for Keywords. */
        FileHandler fh = new FileHandler();

        // former filename for hashdata = dadoshash_basebib2MEDIA1.txt
        // new name = hashdata.txt
        if (!ht.loadPreviousRecordedFileToHash("hashdata.txt")) {
            /**
             * Get data from hash
             */
            
            // The system is prepared to read data from bibtex files, 
            // by using the method addTokensFromBibTex

			fh.addTokensFromBibTex(ht, hsw, "citation-dataset.bib");
            //fh.addTokensFromBibTex(ht, hsw, "baseReferenciaBibTexMEDIA.txt");
            //system.out.println("Base .bib Loaded!");
            //
            //In our work, we used a XML file from DBLP.
            //If you want to do the same, just uncomment the 
            //1st and the 3rd line below, which call the method addTokensFromXML
            //fh.addTokensFromXML(ht, hsw, hci, "dblp.xml", "author");
            //system.out.println("Base author DBLP Loaded!");
            //fh.addTokensFromXML(ht, hsw, hci, "dblp.xml", "journal");
            //system.out.println("Base journal DBLP Loaded!");
            //system.out.println("Author's Base DBLP Loaded!");
        } else {
            /**
             * Hash already loaded
             */
            
            Main.CheatBase(ht);
            ht.remove("authors");
            ht.recordFileFromHash("hashdata.txt");
        }

        /*
         * Load hash with stopwords
         */
        fh.addStopWordsInTheHash(hsw, "stopwords.txt");


        /**
         * Load hash with cities and countries
         */
        File g = new File("location/");
        for (File s : g.listFiles()) {
            String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 4);
            if (!ext.equalsIgnoreCase(".txt")) {
                continue;
            }
            fh.addTokensFromTxt(ht, hsw, ht, s.getAbsolutePath());
        }

        /**
         * Load has with keywords
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
        g = new File("testes/TesteHTML/testeAleatorio/");

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
            texto = ListCreator.takeTagsOff(texto);
            texto = ListCreator.fixTextNumericCodification(texto);
            texto = ListCreator.fixTextCodification(texto);

            boolean b = false;
            StringBuilder sb = new StringBuilder();
            Element father = null;

            /**
             * Monta árvore DOM
             */
            for (int i = 0; i < texto.length(); i++) {
                if (texto.charAt(i) == '<' && !b) {
                    if (!sb.toString().equals("")) {
                        ////system.out.println(sb.toString());
                        ////system.out.println("\t" + sb.toString() + ":cotnent");
                        e.insertText(sb.toString());
                        e.setFather(father);
                        sb = new StringBuilder();
                    }
                    b = true;
                } else if ((texto.charAt(i) == '>') && b) {
                    e.insertTag(sb.toString());

                    ////system.out.println(sb.toString() + ":tag");
                    ////system.out.println(sb.toString());
                    b = false;
                    father = e;
                    sb = new StringBuilder();
                } else if ((texto.charAt(i) == '/') && (texto.charAt(i - 1) == '<')) {
                    e.closeTag();
                } else if (b) {
                    sb.append(texto.charAt(i));
                } else if (!b) {
                    sb.append(texto.charAt(i));
                }
            }

            FormatsCalculus p = new FormatsCalculus();
            LinkedList<LinkedList<String>> ls = new LinkedList();
            HashMap<String, Integer> hm = new HashMap();
            LinkedList<Integer> sizes = new LinkedList();

            /**
             * Extrai citações com lista de padrões
             */
            ExtractorHTML.extractToFindPattern(e, ht, hsw, hci, hk, p, nome, ls, hm, sizes, 0);
            int average = 0;
            if (sizes.size() > 0) {
                average = average(sizes);
            }

            Object[] nums = (sizes.toArray());
            Arrays.sort(nums);

            ////system.out.println(nums.length);

            //if (nums.length > 0){
            int min = 0;//(int) nums[0];
            int max = 0;//(int) nums[nums.length - 1];


            ls.clear();


            ExtractorHTML.extractGeneral(e, ht, hsw, hci, hk, p, ls, hm, average, min, max);
            ExtractorHTML.printCollection(ls, "testes/TesteHTML/testeAleatorio/Saída1/" + nome + "_saida.html", 0, 0);
            //}
            e.clear();

            /*Set<String> x = hm.keySet();
             for (String st : x){
             //system.out.println(st + " -> " + hm.get(st));
             }*/


            hm.clear();
            //System.exit(0);
        }
        System.err.println("FINALIZADO!");
    }

    public static int average(LinkedList<Integer> numbers) {
        int sum = 0;
        if (numbers.size() % 2 == 0) {
            return Math.round(
                    (numbers.get(Math.round(numbers.size() / 2))
                    + numbers.get(Math.round(numbers.size() / 2) - 1)) / 2);
        } else {
            return numbers.get(Math.round(numbers.size() / 2));
        }
    }

    public static boolean around(int size, int avg, int min, int max) {
        int num = Math.round(avg / 2) + 10; //Math.round((min + max) / 3);
        int sup = avg + /*Math.round(3 * avg / 4)*/ +num;
        int inf = (avg - num > 5 ? avg - num : 5);

        if (size > inf && size > 5 && size < sup) {
            return true;
        }
        return false;
    }

    public static void extractGeneral(Element e, HashToken ht, HashToken hsw, HashToken hci, HashToken hk, FormatsCalculus fc, LinkedList<LinkedList<String>> ls, HashMap<String, Integer> hm, int avg, int min, int max) {
        String[] t;

        ////system.out.println(ht.size());
        for (Element g : e.getL()) {
            /*for (int i = 0; i < tabs; i++){
             //system.out.print("\t");
             }*/
            if (g instanceof Textual) {
                if (hm.containsKey(g.getTag())) {

                    t = ((Textual) g).getTexto().split(" ");
                    LinkedList<String> l = new LinkedList();

                    ExtractorHTML.createBlocks(((Textual) g).getTexto(), ht, hsw, hci, hk, fc, ls, 0);

                    l.addAll(Arrays.asList(t));
                    //if (l.size() > 5){
                    LinkedList<LinkedList<String>> p = new LinkedList();
                    LinkedList<String> ps = new LinkedList();
                    for (String s : l) {
                        ps.add(s);
                    }

                    //ls.add(ps);
                    //}

                    /*int before = ls.size();
                     //ExtractorHTML.createBlocks(((Textual) g).getTexto(), ht, hsw, hci, hk, fc, ls, 1);
                     if (ls.size() != before){
                     //aumentou
                     //if (!around(ls.getLast().size(), avg, min, max)){
                     if (ls.getLast().size() <= 5) ls.removeLast();
                     //}
                     }*/

                    //ExtractorHTML.createBlocks(((Textual) g).getTexto(), ht, hsw, hci, hk, fc, ls);
                }
            } else if (g instanceof Elementual) {
                ////system.out.println(g.getTag());
                ////system.out.print("<" + ((Elementual)g).getName() + ">");
                ////system.out.println();
                extractGeneral(g, ht, hsw, hci, hk, fc, ls, hm, avg, min, max);
                //ExtractorHTML.extractToFindPattern(g, ht, hsw, hci, hk, fc, nome, ls, hm, tabs + 1);
            }
        }
    }

    public static void extractToFindPattern(Element e, HashToken ht, HashToken hsw, HashToken hci, HashToken hk, FormatsCalculus fc, String nome, LinkedList<LinkedList<String>> ls, HashMap<String, Integer> hm, LinkedList<Integer> sizes, int tabs) throws IOException {
        LinkedList<String> tags = new LinkedList();


        int size = ls.size();

        for (Element g : e.getL()) {
            /*for (int i = 0; i < tabs; i++){
             //system.out.print("\t");
             }*/
            if (g instanceof Textual) {

                ////system.out.println(((Textual) g).getTexto().replace("\n", ""));
                int tempS = ls.size();
                ExtractorHTML.createBlocks(((Textual) g).getTexto(), ht, hsw, hci, hk, fc, ls, 0);

                if (tempS != ls.size()) {
                    ////system.out.println(g.getTag());
                    sizes.add(ls.getLast().size());
                    if (hm.containsKey(g.getTag())) {
                        hm.put(g.getTag(), hm.get(g.getTag()) + 1);
                    } else {
                        hm.put(g.getTag(), 1);
                    }
                }

            } else if (g instanceof Elementual) {
                ////system.out.println(g.getTag());
                ////system.out.print("<" + ((Elementual)g).getName() + ">");
                ////system.out.println();
                ExtractorHTML.extractToFindPattern(g, ht, hsw, hci, hk, fc, nome, ls, hm, sizes, tabs + 1);
            }
        }

        //if (ls.size() > 0) //system.out.println(ls.size());
        boolean encontrou = false;
        for (LinkedList<String> st : ls) {
            for (String s : st) {
                if (ListCreator.isTextual(s) && encontrou == false) {
                    encontrou = true;
                }
                //if (encontrou) //system.out.print (s + " ");
            }
            encontrou = false;
            ////system.out.println();
        }



        /*if (ls.size() > 0) {
         for (LinkedList<String> s: ls){
         for (String s1: s){
         //system.out.print(s1 +  " ");
         }
         //system.out.println();
         }
         //system.out.println(tags);
         }*/


    }

    public static void createBlocks(String in, HashToken ht, HashToken hsw, HashToken hci, HashToken hk, FormatsCalculus fc, LinkedList<LinkedList<String>> ls, int mode) {

        ListCreator lc = new ListCreator();
        LinkedList<SuperClasse> wl = new LinkedList();
        String str = in.toLowerCase();

        /* Find out if exists some URL inner to String str */
        int x = ListCreator.detectURL(str);
        int s = 0;

        /* Declare helper variables */
        StringBuilder sb = new StringBuilder();
        LinkedList<String> v = new LinkedList();

        /* If x is not -1, so exists URL inner to String str */
        if (x != -1) {
            /* Gets the URL */
            for (int k = x; k < str.length(); ++k) {
                sb.append(str.charAt(k));
            }

            /* Split the URL to count how many pieces its division generates */
            String[] k = ((FileHandler.justIsolatePunctuationCharacters(
                    lc.fixTextCodification(
                    FileHandler.removeAcentos(
                    sb.toString())))).split(" "));

            /* Insert pieces into the list */
            for (String g : k) {
                if (!g.trim().equalsIgnoreCase("")) {
                    v.add(g);
                    s++;
                }
            }

        }

        /* Str become useful */
        str = FileHandler.justIsolatePunctuationCharacters(
                lc.fixTextCodification(
                FileHandler.removeAcentos(
                lc.fixTextCodification(
                str))));


        /* Split the string using spaces */
        String[] cTags = str.split("[\\s\t]");

        for (String o : cTags) {
            if (o.matches("([a-z][a-z]+)(\\d{2,4})")) {
                ////system.out.println(o);
                String troca = o.replaceAll("([a-z][a-z]+)(\\d{2,4})", "$1 $2");
                ////system.out.println(troca);
                str = str.replace(o, troca);
            }
        }

        cTags = str.split("[\\s\t]");


        /* Initialize helper lists */
        LinkedList<String> auxToken = new LinkedList();
        LinkedList<String> auxValue = new LinkedList();
        String before = "";

        ////system.out.println("--" + in);

        for (int i = 0; i < cTags.length; i++) {
            ////system.out.println(cTags[i]);
            if (!ListCreator.isSymbol(cTags[i]) && cTags[i].length() > 0) {
                // If cTags[i] is not symbol 
                if (v.size() > 0 && v.get(0).equalsIgnoreCase(cTags[i])) {
                    // Insert URL into the list 
                    auxToken.add(sb.toString());
                    auxValue.add("");
                    i += s + 1;
                    x = -1;

                    sb = new StringBuilder();
                    continue;
                } else {
                    auxToken.add(cTags[i]);
                    auxValue.add("");
                }
                // If cTags[i] is symbol 
            } else if ((auxValue.size() > 0) && ListCreator.isSymbol(cTags[i])) {
                /* Just print */
                /*for (int j = 0; j < auxToken.size(); j++){
                 //system.out.print(auxToken.get(j) + " ");
                 } */

                // Calculate block's probability  
//                        before = calculateBlockClass(ht, hsw, hci,
//                                auxToken, auxValue, fc, before, 0.5);
                // Verify if exists a keyword 
                boolean exist = false;
                String value = "";

                for (String a : auxToken) {
                    if (hk.exists(a)) {
                        exist = true;
                        value = hk.get(a).get(0).getType();
                    }
                }


                if (exist) {
                    //auxValue.set(0, value);

                    for (int index = 0; index < auxValue.size(); index++) {
                        auxValue.set(index, value);
                    }

                } else {
                    String a = lc.calculateBlockClass(ht, hsw,
                            hci, auxToken, auxValue, fc, "", 0.5);
                }

                // Just print 
                ////system.out.println(auxToken);
                ////system.out.println(auxValue);
                ////system.out.println(ht.size());
                //if ( auxValue.size() > 0) //system.out.println("(" + auxValue.get(0) + ")");

                // Insert all the block as a token 
                StringBuilder nsb = new StringBuilder();
                for (int j = 0; j < auxToken.size(); j++) {
                    nsb.append(auxToken.get(j)).append(" ");
                }

                wl.add(new TokenNP(nsb.toString(), auxValue.get(0)));

                // Insert the last symbol 
                if (!cTags[i].equalsIgnoreCase("")) {
                    wl.add(new TokenNP(cTags[i], "symbol"));
                }

                // Clear helper lists 
                auxToken.clear();
                auxValue.clear();
            } else if (!cTags[i].equalsIgnoreCase("")) {
                wl.add(new TokenNP(cTags[i], "symbol"));
            }
        }

        // Running here only at the end of the line. 
        if (auxToken.size() > 0) {
            /*for (int j = 0; j < auxToken.size(); j++){
             //system.out.print(auxToken.get(j) + " ");
             }*/

            boolean exist = false;
            String value = "";

            // Verify if exists a keyword 
            for (String a : auxToken) {
                if (hk.exists(a)) {
                    exist = true;
                    value = hk.get(a).get(0).getType();
                }
            }

            if (exist) {
                //auxValue.set(0, value);

                for (int index = 0; index < auxValue.size(); index++) {
                    auxValue.set(index, value);
                }

                ////system.out.println(auxToken + "\n" + auxValue);
            } else {
                before = calculateProbabilityUsingPreviousBlock(hk, ht, hsw, hci,
                        auxToken, auxValue, fc, before, 0.5);
            }

            // Insert all the block as a token 
            StringBuilder nsb = new StringBuilder();
            for (int j = 0; j < auxToken.size(); j++) {
                nsb.append(auxToken.get(j)).append(" ");
            }



            //before = calculateBlockClass(ht, hsw, hci, 
            //        auxToken, auxValue, fc, before);
            //if ( auxValue.size() > 0) //system.out.println("<" + auxValue.get(0) + ">");
        }

        if (auxToken.size() > 0) {
            String a = calculateProbabilityUsingPreviousBlock(hk, ht, hsw,
                    hci, auxToken, auxValue, fc, "", 0.5);


            // Just print 
            //if ( auxValue.size() > 0) //system.out.println("<" + auxValue.get(0) + ">");

            // Insert all the block as a token 
            StringBuilder nsb = new StringBuilder();
            for (int j = 0; j < auxToken.size(); j++) {
                nsb.append(auxToken.get(j)).append(" ");
            }

            wl.add(new TokenNP(nsb.toString(), auxValue.get(0)));
        }

        LinkedList<String> sn = new LinkedList();
        LinkedList<String> citation = new LinkedList();

        for (SuperClasse sc : wl) {
            TokenNP t = (TokenNP) sc;
            sn.add(t.getTag());
            citation.add(t.getToken());
        }

        if (mode != 0) {
            ////system.out.println("MODE1: " + lc.isValid(hsw, citation, sn, true));
            ////system.out.println(citation);
            ////system.out.println(sn);
            if (lc.verifyCitation(sn) && lc.isValid(hsw, citation, sn, true)) {
                ls.add(citation);
            }
        } else {
            ////system.out.println("MODE0: " + lc.isValidFlex(hsw, citation, sn, true));
            ////system.out.println(lc.verifyCitationFlex(sn) && lc.isValidFlex(hsw, citation, sn, true));
            ////system.out.println(sn);
            ////system.out.println(citation);
            ////system.out.println(sn);
            if (lc.verifyCitationFlex(sn) && lc.validWords(citation) /*&& lc.isValidFlex(hsw, citation, sn, true)*/) {
                ////system.out.println("Entrou o.O'");
                ls.add(citation);
            }
        }
    }

    public static void printCollection(LinkedList<LinkedList<String>> lista, String nome, int op, int showSize) throws IOException {
        File f = new File(nome);

        if (f.exists()) {
            f.delete();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true)));
        String conteudo = new String();

        conteudo = "<html><head></head><body style='font-family: Arial, Tahoma;'>";
        conteudo += "<div>Arquivo: " + nome + "</div><div>" + lista.size() + " Cita&ccedil;&otilde;es</div><table><tr style='background: gray;'><td>N&uacute;mero</td><td>Cita&ccedil;&atilde;o</td></tr>";

        String bg;
        int i = 0;

        for (LinkedList<String> p : lista) {
            if ((i + 1) % 2 == 0) {
                bg = "white";
            } else {
                bg = "#eee";
            }



            conteudo += "<tr><td style='background: " + bg + " '>" + (i + 1) + "</td><td style='background: " + bg
                    + " '>";

            for (int j = 0; j < p.size(); j++) {

                if (p.get(j).trim().equalsIgnoreCase("<barran>")) {
                    if (op == 1) {
                        conteudo += "&lt;barran&gt;";
                    }
                } else {
                    conteudo += (p.get(j) + " ");
                    if (op == 1) {
                        conteudo += ("&lt;" + p.get(j) + "&gt;");
                    }
                }
                //
            }


            if (showSize == 1) {
                conteudo += " Tamanho: " + (p.size()) + "</td></tr>";
            } else {
                conteudo += "</td></tr>";
            }
            i++;
        }

        conteudo += "</table></body></html>";
        //conteudo = ListCreator.codifyTextToHtml(conteudo);
        fw.write(conteudo);
        fw.close();
    }

    public static void printCollection(LinkedList<LinkedList<TokenNP>> lista, String nome, int showSize) throws IOException {
        File f = new File(nome);

        if (f.exists()) {
            f.delete();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true)));
        String conteudo = new String();

        conteudo = "<html><head></head><body style='font-family: Arial, Tahoma;'>";
        conteudo += "<div>Arquivo: " + nome + "</div><div>" + lista.size() + " Cita&ccedil;&otilde;es</div><table><tr style='background: gray;'><td>N&uacute;mero</td><td>Cita&ccedil;&atilde;o</td></tr>";

        String bg;
        int i = 0;

        for (LinkedList<TokenNP> p : lista) {
            if ((i + 1) % 2 == 0) {
                bg = "white";
            } else {
                bg = "#eee";
            }

            /* Conserta inicio com caracteres sujando a citação */
            /*int inicio = 0;
           
             String token = p.get(0).getToken();
             for (int k = 0; k < token.length(); k++){
             //system.out.println("Token: "+ token + " : char: " + token.charAt(k) + (token.charAt(k) == ' ' || ListCreator.isSymbol(String.valueOf(token.charAt(k))) || FileHandler.isNumber(String.valueOf(token.charAt(k)))));
             if (token.charAt(k) == ' ' || ListCreator.isSymbol(String.valueOf(token.charAt(k)))
             || FileHandler.isNumber(String.valueOf(token.charAt(k))))
             inicio++;
             else break;
             }

             p.get(0).setToken(token.substring(inicio));
             */

            int chars = 0;
            for (TokenNP t : p) {
                chars += t.getToken().length();
            }

            conteudo += "<tr><td style='background: " + bg + " '>" + (i + 1) + "</td><td style='background: " + bg
                    + " '>";

            for (int j = 0; j < p.size(); j++) {

                if (p.get(j).getToken().equalsIgnoreCase("<barran>")) {
                    /*if (op == 1) {
                     conteudo += "&lt;barran&gt;"; 
                     }*/
                } else {
                    conteudo += (p.get(j).getToken() + " ");
                    //conteudo += (p.get(j).getToken() + " (" + p.get(j).getTag() + ")");
                    //if (op == 1){conteudo += ("&lt;" + p.get(j) + "&gt;");}
                }
                //
            }


            if (showSize == 1) {
                conteudo += " Tamanho: " + (p.size()) + "<br>Media: " + (chars / p.size()) + "</td></tr>";
            } else {
                conteudo += "</td></tr>";
            }
            i++;
        }

        conteudo += "</table></body></html>";
        //conteudo = ListCreator.codifyTextToHtml(conteudo);
        fw.write(conteudo);
        fw.close();
    }

    public static String calculateProbabilityUsingPreviousBlock(HashToken hk, HashToken ht, HashToken hsw,
            HashToken hci, LinkedList<String> tokens, LinkedList<String> values,
            FormatsCalculus fc, String previous, Double threshold) {

        HashMap<String, Double> hm = new HashMap();         //Frequency
        HashMap<String, Integer> hc = new HashMap();        //Counting
        HashExternOperations heo = new HashExternOperations(ht);
        String[] types = {
            "author", "title", "booktitle", "journal", "volume",
            "number", "pages", "date", "location", "publisher",
            "institution", "editor", "note", "tech"
        };


        /* if the block is pages*/
        StringBuilder st = new StringBuilder();
        for (String q : tokens) {
            st.append(q).append(" ");
        }

        ////system.out.println(tokens);
        /*for (String q: tokens){
         ////system.out.println( q);
         if (hk.contains(q)){
         for (int i = 0; i < tokens.size(); i++){
         values.set(i, hk.get(q).get(0).getType());
         }
         return "";
         }
         }*/
        ////system.out.println("----------------------");

        if (st.toString().trim().matches("([0-9]+) - ([0-9]+)")) {
            values.set(0, "pages");
            return "";
        }

        /* if the token's just a token */
        if (tokens.size() == 1 && tokens.get(0).trim().length() > 1) {

            String s = heo.searchForTheMoreOften(tokens.get(0));

            if ((s.equalsIgnoreCase("author") && !ListCreator.isSymbol(previous))) {//previous.equalsIgnoreCase("."))){
                values.set(0, "");
                return "";
            }

            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));
            return s;

            /* if the token's just a letter */
        } else if (tokens.size() == 1 && tokens.get(0).trim().length() == 1) {

            String s = heo.searchForTheMoreOften(tokens.get(0));

            if (ListCreator.isSymbol(tokens.get(0).trim())) {
                return "";
            }
            if ((s.equalsIgnoreCase("author") && !previous.equalsIgnoreCase("."))) {
                values.set(0, "");
                return "";
            }
            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));

            return s;
        }

        /* Set all values to notype */
        for (int i = 0; i < values.size(); i++) {
            values.set(i, "notype");
        }


        for (int i = 0; i < values.size(); i++) {
            /* if the token's valid */
            if (((ht.exists(tokens.get(i).trim()))
                    || (heo.searchForFrequency(tokens.get(i).trim(), "author") > 0 && i == values.size() - 1)
                    || (heo.searchForFrequency(tokens.get(i).trim(), "note")) > 0)
                    && !ListCreator.isSimpleSymbol(tokens.get(i))) {
                values.set(i, "token");
                /* if the token is a stopword */
            } else if (hsw.exists(tokens.get(i))) {
                values.set(i, "");
            } else {
                values.set(i, "");
            }
        }

        ////system.out.println(tokens);
        ////system.out.println(values);

        int totalUseful = 0;
        /* Count valid tokens */
        for (String s : values) {
            if (s.equalsIgnoreCase("token")) {
                totalUseful++;
            }
        }

        if (totalUseful == 0) {
            return "";
        }

        /* Run thru the types */
        for (String t : types) {
            int actual = 0;
            int counter = 0;
            for (int i = 0; i < tokens.size(); i++) {
                String s = tokens.get(i);
                String v = values.get(i);
                if (v.equalsIgnoreCase("token")) {
                    if (heo.searchForFrequency(s, t) > 0) {
                        actual++;
                        counter += heo.searchForFrequency(s, t);
                    }
                } else {
                }
            }

            ////system.out.println("Actual= " + actual + ": Tipo= " + t);
            ////system.out.println();
            ////system.out.println(t +": " + Double.valueOf(actual) + "/" + Double.valueOf(totalUseful) + " = " + (Double.valueOf(actual)/Double.valueOf(totalUseful)) );
            hm.put(t, (Double.valueOf(actual) / Double.valueOf(totalUseful)));
            hc.put(t, counter);
        }

        Double maior = 0.0;
        String result = "";

        /* Runs through hash */
        Set<String> g = hm.keySet();
        for (String s : g) {
            if (hm.get(s) > maior) {
                maior = hm.get(s);
                result = s;
            }
        }

        ////system.out.println(1+ result + " " + maior);

        if (maior == 0.0) {
            return "";
        }

        if (Double.compare(hm.get(result), threshold) < 0) {
            for (int i = 0; i < values.size(); i++) {
                values.set(i, "");
            }
            return "";
        }

        ////system.out.println(2+ result + " " + maior);

        LinkedList<String> equal = new LinkedList();

        //Verify for draw
        int times = 0;
        for (String s : g) {
            if (Double.compare(hm.get(s), maior) == 0) {
                equal.add(s);
            }
        }

        int bigger = 0;

        if (equal.size() > 1) {
            Set<String> c = hc.keySet();
            for (String s : equal) {
                ////system.out.println(s + ": " + hc.get(s) );
                if (hc.get(s) > bigger) {
                    bigger = hc.get(s);
                    result = s;
                }
            }
            ////system.out.println("Entrou por empate: " + bigger + ">" + result);
        }

        ////system.out.println(result + " " + bigger);

        for (int i = 0; i < values.size(); i++) {
            values.set(i, result);
        }

        return result;

    }

    private static void method() {
        String html = "";
    }

    public static Element makeTree(String texto) {
        Element e = new Element();
        boolean b = false;
        StringBuilder sb = new StringBuilder();
        Element father = null;

        /**
         * Monta árvore DOM
         */
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == '<' && !b) {
                if (!sb.toString().equals("")) {
                    ////system.out.println(sb.toString());
                    ////system.out.println("\t" + sb.toString() + ":cotnent");
                    e.insertText(sb.toString());
                    e.setFather(father);
                    sb = new StringBuilder();
                }
                b = true;
            } else if ((texto.charAt(i) == '>') && b) {
                e.insertTag(sb.toString());

                ////system.out.println(sb.toString() + ":tag");
                ////system.out.println(sb.toString());
                b = false;
                father = e;
                sb = new StringBuilder();
            } else if ((texto.charAt(i) == '/') && (texto.charAt(i - 1) == '<')) {
                e.closeTag();
            } else if (b) {
                sb.append(texto.charAt(i));
            } else if (!b) {
                sb.append(texto.charAt(i));
            }
        }

        return e;
    }

    public static String getDeeper(Element e) {
        if (e instanceof Elementual) {
            for (Element x : e.getL()) {
                if (x instanceof Elementual) {
                    return getDeeper(x);
                }
            }
        }
        return e.getFather().getTag();
    }

    public static int getHeight(Element e, String texto, int height) {
        int res = 0;
        for (Element x : e.getL()) {
            if (x instanceof Elementual) {
                if (!x.getTag().equalsIgnoreCase(texto)) {
                    res = getHeight(e, texto, height + 1);
                }
            }
        }
        return (res == 0 ? height : res);
    }
}
