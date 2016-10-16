
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.extrator;

//~--- JDK imports ------------------------------------------------------------
import NovoExtrator.structures.SuperClasse;
import NovoExtrator.filehandlers.Formats;
import NovoExtrator.filehandlers.FileHandler;
import NovoExtrator.filehandlers.Masks;
import NovoExtrator.filehandlers.Patterns;
import NovoExtrator.structures.TagsNP;
import NovoExtrator.structures.TokenH;
import NovoExtrator.structures.TokenNP;
import NovoExtrator.structures.HashToken;
import NovoExtrator.extrator.Citations.DataCitation;
import NovoExtrator.filehandlers.FormatsCalculus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import NovoExtrator.structures.Rules;
import NovoExtrator.structures.Storage;
import NovoExtrator.structures.PCitation;
//import testeaudio.TesteAudio;

/**
 * /media/7498DDE398DDA3C0/Documents and Settings/Cristiano/Meus
 * documentos/NetBeansProjects
 *
 * @author cristiano
 * @version
 *
 */
public class Main1 {

    public static void TestTags() {
        ListCreator lc = new ListCreator();
        String s = "Cristiano : Mesquita <html><body><a href=''>Testando</a> Deu? </body><html> Teste";
        String[] sq = lc.getTextSplitedWithTag(s);

        for (String q : sq) {
            if ((q != null) && !q.equals("")) {
                //system.out.println(q + "\n");
            }
        }
    }

    public static void CheatBase1(HashToken ht) {
        TokenH t = new TokenH();
        t.setType("author");

        ht.put("meeta", t);
        ht.put("gupta", t);
        ht.put("jude", t);
        ht.put("rivers", t);
        ht.put("pradip", t);
        ht.put("bose", t);
        ht.put("gu", t);
        ht.put("yeon", t);
        ht.put("wei", t);
        ht.put("david", t);
        ht.put("brooks", t);
        ht.put("kristen", t);
        ht.put("lovin", t);
        ht.put("benjamin", t);
        ht.put("lee", t);
        ht.put("xiaoyao", t);
        ht.put("liang", t);

        t = new TokenH();
        t.setType("title");
        ht.put("design", t);
        ht.put("for", t);
        ht.put("pvt", t);
        ht.put("variations", t);
        ht.put("with", t);
        ht.put("local", t);
        ht.put("recovery", t);
        ht.put("and", t);
        ht.put("fine", t);
        ht.put("grained", t);
        ht.put("adaptation", t);
        ht.put("variations", t);
        ht.put("tribeca", t);
        ht.put("empirical", t);
        ht.put("performance", t);
        ht.put("models", t);
        ht.put("3t1d", t);
        ht.put("memories", t);
        ht.put("test", t);
        ht.put("strategies", t);

        ht.put("post", t);
        ht.put("fabrication", t);
        ht.put("tuning", t);

        t = new TokenH();
        t.setType("journal");

        ht.put("international", t);
        ht.put("microarchitectural", t);
        ht.put("on", t);
        ht.put("conference", t);
        ht.put("symposium", t);
        ht.put("computer", t);
        ht.put("design", t);
        ht.put("42nd", t);
        ht.put("27th", t);
    }

    public static void CheatBase(HashToken ht) {
        TokenH t = new TokenH();
//        
//       
//        t.setType("journal");
//        ht.put("Ec", t);
//        ht.put("EuroPLoP", t);
//        ht.put("webnet", t);
//        ht.put("UCS", t);
//        ht.put("hicsS", t);
//        ht.put("sigir", t);
//        ht.put("forum", t);
//        ht.put("science", t);
//        ht.put("Comput", t);
//        ht.put("calisce", t);
//        ht.put("hmm", t);
//        ht.put("teleteaching", t);
//        ht.put("ita", t);
//        ht.put("hypertext", t);
//        ht.put("theoretical", t);
//        ht.put("science", t);
//        ht.put("jasist", t);
//        ht.put("xacta", t);
//        ht.put("sociedade", t);
//        ht.put("brasileira", t);
//        ht.put("computacao", t);
//        
//        t = new TokenH();
//        t.setType("author");
//        ht.put("Duval", t);
//        ht.put("Olivié", t);
//        ht.put("Kris", t);
//        ht.put("peruzza", t);
//        ht.put("melchiori", t);
//        ht.put("greghi", t);

        t = new TokenH();
        t.setType("pages");

        ht.put("pages", new TokenH("pages", 10000));
        ht.put("pp", new TokenH("pages", 10000));


        t = new TokenH();
        t.setType("tech");
        t.setAttributeCount(10000);
        ht.put("technical", new TokenH("tech", 10000));
        ht.put("report", new TokenH("tech", 10000));

        t = new TokenH();
        t.setType("booktitle");
        ht.put("in", new TokenH("booktitle", 10000));

        t = new TokenH();
        t.setType("date");
        t.setAttributeCount(10000);
        ht.put("november", t);
        ht.put("september", t);
        ht.put("october", t);
        ht.put("june", t);
        ht.put("july", t);
        ht.put("august", t);
        ht.put("march", t);
        ht.put("april", t);
        ht.put("february", t);
        ht.put("january", t);
        ht.put("december", t);
        ht.put("may", t);

        ht.put("novembro", t);
        ht.put("setembro", t);
        ht.put("outubro", t);
        ht.put("junho", t);
        ht.put("julho", t);
        ht.put("agosto", t);
        ht.put("marco", t);
        ht.put("abril", t);
        ht.put("fevereiro", t);
        ht.put("janeiro", t);
        ht.put("dezembro", t);
        ht.put("maio", t);


        t = new TokenH();
        t.setType("journal");
        t.setAttributeCount(10000);

        String suffix = "";
        String n;
        for (int j = 1; j < 99; j++) {
            n = String.valueOf(j);
            if (n.charAt(n.length() - 1) == '1') {
                suffix = "st";
            } else if (n.charAt(n.length() - 1) == '2') {
                suffix = "nd";
            } else if (n.charAt(n.length() - 1) == '3') {
                suffix = "rd";
            } else {
                suffix = "th";
            }

            ht.put(j + suffix, t);

            if (FileHandler.toRoman(j).length() > 1) {
                ht.put(FileHandler.toRoman(j), t);
            }
        }

        ht.put("symposium", t);
        ht.put("microarchitecture", t);
        ht.put("international", t);
        //ht.put("trec",new TokenH("journal", 2));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // TODO code application logic here
        boolean load = false;
        HashToken ht = new HashToken();
        HashToken hsw = new HashToken();    /* Hash for Stopwords. */
        HashToken hci = new HashToken();    /* Hash for Cities and Countries. */
        HashToken hk = new HashToken();    /* Hash for Keywords. */

        FileHandler fh = new FileHandler();
        ListCreator lc = new ListCreator();
        Masks m = new Masks();
        Formats f = new Formats();
        ProcessCitation pc = new ProcessCitation();
        Citations c = new Citations();
        LinkedList<LinkedList<SuperClasse>> cits = null;
        Patterns p = new Patterns();
        Storage st = new Storage();

        fh.addStopWordsInTheHash(hsw, "stopwords.txt");
        ht.remove("lu");


        ////system.out.println(hsw.printData());
//            String p = "<td>Teste 25. <barran></td>";
//            String reg = "\\. <barran>";
//            String[] g = p.split(reg);
//
//            //system.out.println(reg);
//
//            for (String t: g) //system.out.println(t);
//
//            ////system.out.println(g);
//            System.exit(0);

//            String p = "<td>Teste 25</td>";
//            String g = p.replaceAll("<td>([^<]+) ([\\d]+)</td>", "<i>$2-$1</i>");
//
//            //system.out.println(g);
//            System.exit(0);



        //corrigeCities("americanstates.txt");
//        //system.out.println(FileHandler.toRoman(2012));
////        System.exit(0);
        if (!ht.loadPreviousRecordedFileToHash("dadoshash_basebib2MEDIA1.txt")) {
            ////system.out.println("Achou hash");
            //fh.addTokensFromBibTex(ht, hsw, "baseReferenciaBibTex.txt");
            fh.addTokensFromBibTex(ht, hsw, "baseReferenciaBibTexMEDIA.txt");
            //system.out.println("Base .bib Loaded!");
            fh.addTokensFromXML(ht, hsw, hci, "dblp.xml", "author");
            //system.out.println("Base author DBLP Loaded!");
            fh.addTokensFromXML(ht, hsw, hci, "dblp.xml", "journal");
            //system.out.println("Base journal DBLP Loaded!");
            //fh.addTokensFromXML(ht, hsw, hci, "dblp.xml", "booktitle");
            //system.out.println("Author's Base DBLP Loaded!");

//            fh.addTokensFromBibTex(ht, hsw, "zbase.txt");
//            fh.addTokensFromBibTex(ht, hsw, "zbase1.txt");
//            fh.addTokensFromXML(ht, hsw, "dblp.xml");
//            fh.addTokensFromXML(ht, hsw, "ZivaniOG85.xml");
//            fh.addTokensFromXML(ht, hsw, "LimaEMFC04.xml");
//            fh.addTokensFromXML(ht, hsw, "CardinaelsDO06.xml");
//            //system.out.println("Leu o XML\n");
            load = true;
        } else {
            //fh.addTokensFromBibTex(ht, hsw, "teste.txt");
            //system.out.println(ht.size());
            CheatBase(ht);
            ////system.out.println(ht.printData());
        }
//        ht.remove("hu");
//        ht.remove("mesquita");
//        ht.remove("goncalves");
//        load = true;

//        for (char pro = 'a'; pro <= 'z'; pro++ ){
//            //system.out.println(pro);
//            for (int ipro = 0; ipro < 500; ipro++){
//
//                String t = String.valueOf(pro) + (int)ipro;
//                ht.remove(t);
//
//            }
//        }
//        load = true;
//        fh.addTokensFromBibTex(ht, hsw, "bibteste.txt");
        //fh.addTokensFromBibTex(ht, hsw, "baseReferenciaBibTex.txt");
        ////system.out.println(ht.size());
//        
        //System.exit(0);
        // 
        // }

        //Main.CheatBase1(ht);

        try {
            if (load) {
                File g = new File("location/");
                for (File s : g.listFiles()) {
                    String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 4);

                    if (!ext.equalsIgnoreCase(".txt")) {
                        continue;
                    }

                    //system.out.println(s.getAbsolutePath());
                    fh.addTokensFromTxt(ht, hsw, ht, s.getAbsolutePath());
                }


            }

            File g = new File("keywords/");
            for (File s : g.listFiles()) {
                String ext = s.getAbsolutePath().substring(s.getAbsolutePath().length() - 4);

                if (!ext.equalsIgnoreCase(".txt")) {
                    continue;
                }

                //system.out.println(s.getAbsolutePath());
                fh.addTokensFromTxt(hk, s.getAbsolutePath());
            }

            //fh.addTokensFromXML(ht, hsw, hci, "dblp.xml", "author");
            //load = true;
            ////system.out.println(s.getAbsolutePath());
            //if (s.)
            //String nomearq = s.getName();




//            LinkedList<TokenH> t = ht.get("uk");
//            for (TokenH tt: t){
//                //system.out.println(tt.getType() + ": " + tt.getAttributeCount() + " ocorrências.");
//            }
//
//            System.exit(0);
            // for (int i = 0; i < 2; i++){

            //String nomearq = "testes/anapaula.txt";
            //String nomearq = "ahmed.html";
            //String nomearq = "doc0_0.html";
            //String nomearq = "doc0_2.html";
            //String nomearq = "docd.txt";
            //String nomearq = "art3.txt";
            //String nomearq = "testes/testescolchete.txt"; //teste OK size 3
            //String nomearq = "testes/testeponto.txt"; //teste OK size 3
            //String nomearq = "testes/ahmed.txt"; //teste OK size 3 no
            //String nomearq = "testes/testeblocos.txt";
            //String nomearq = "testes/testesparenteses.txt"; //teste OK size 3
            //String nomearq = "testes/testesbarran1.txt"; //teste OK size 1
            //String nomearq = "testes/testesbarran.txt"; //com 3, nao pega a ultima página.
            //com 2, parte algumas citações no meio.
            //String nomearq = "testes/testedblp.txt"; //com tamanho 2, OK
            //com tamanho 3, perde a ultima pagina

            //String nomearq = "testes/dap.txt";

            //String nomearq = "testes/tales.txt";
            //String nomearq = "testes/newlattesnotag.txt";
            //String nomearq = "testes/latteshtml.txt";
            //String nomearq = "testes/lattestxt.txt";
            //String nomearq = "testes/lnovo.txt";

//            for (File s: g.listFiles()){
//                if (s.getAbsolutePath().indexOf(".txt") == -1) continue;
            ////system.out.println(s.getAbsolutePath());
            //if (s.)
            //String nomearq = s.getName();

            //String nomearq = "testes/newlattesdenilson.txt";
            //String nomearq = "testes/newlatteshtml.txt";
            //String nomearq = "testes/dblp1.txt";
            //String nomearq = "testes/cverikdemaine.txt";
            //String nomearq = "testes/erikdemaine.txt";            
            //String nomearq = "testes/art29.txt";
            //String nomearq = "testes/sample2.txt";
            //String nomearq = "testes/art3.txt";
            //String nomearq = "testes/art29.txt";
            //String nomearq = "testes/art3.txt";
            //String nomearq = "testes/art17i.txt";
            // String nomearq = "testes/art42.txt";  
            //String nomearq = "testes/cv18.txt";                  
            //String nomearq = "testes/dblp5.txt";                  
            //String nomearq = "testes/testedblp1.txt";      

            //String nomearq = "testes/ahmed.txt";      
            //String nomearq = "testes/denilson.txt";
            //String nomearq = "testes/art31.txt";
            //String nomearq = "testes/similar.html";
            //String nomearq = "testes/similar.txt";
            //String nomearq = "testes/aaaesmindblp.txt";
            //String nomearq = "testes/aaaesmindblpnotags.txt";

            f.openFileAndLoadFormats("formats.txt");
            p.openFileAndLoadPatterns("patterns.txt");
            m.openFileAndLoadMasks("masks.txt");

            FormatsCalculus fc = new FormatsCalculus();
            fc.loadStructureWithFormatsAndCalculate(f);


            //fh.addTokensFromBibTex(ht, hsw, "baseReferenciaBibTex.txt");
            fh.saveHashAsTxt(ht, "hashaux.txt");
            ////system.out.println(ht.size());
            if (load) {
                //system.out.println(ht.recordFileFromHash("dadoshash_basebib2MEDIA1.txt"));
            }
            ////system.out.println(ht.printData());

            /**
             * *************************************************
             */
            g = new File("testes/Oficial/");
            /**
             * *************************************************
             */
            for (final File s : g.listFiles()) {
                st = new Storage();
                /**
                 * *************************************************
                 */
                if (s.isFile() && s.getName().substring(s.getName().length() - 3).equalsIgnoreCase("txt")) {
                    /**
                     * *************************************************
                     */
                    String nomearq = s.getAbsolutePath();
                    String filename = s.getName();
                    lc.createBlockList(ht, hsw, hci, hk, nomearq, fc);

                    LinkedList<SuperClasse> wl = lc.getList();

                    LinkedList<String> l = lc.getCitations(st, lc.getList(), hsw, filename);


                    //                for (int i = 0; i < l.size(); i++){
                    //                    String o = l.get(i);
                    //                    //system.out.println(i + " >> " + o);
                    //                }

                    ////system.out.println(lc.printList());

                    //lc.createList(ht, hsw, hci, nomearq, fc);


                    ////system.out.println("Tamanho da hasha:" + ht.size());

                    //            LinkedList<TokenH> l = ht.get("olivie");
                    //            for (TokenH a: l){
                    //                //system.out.println("olivie: " + a.getType() + "::" + a.getAttributeCount());
                    //            }
                    ////system.out.println("Aqui 4");

                    /**
                     * *************************************************
                     */
                    //String nomeSaida = nomearq.replace("testes/", "testes/Oficial/");
                    /**
                     * **************************************
                     */
                    String nomeSaida = nomearq.replace("testes/Oficial/",
                            "testes/Oficial/saida1/") + "_saida.html";

                    //String nomeSaida = (nomearq.replace("testes/", "testes/saida/") + "_saida.html");
                    ////system.out.println(nomeSaida);
                    //System.exit(0);
                    //generateStringFile(nomeSaida, l);

                    if (load) {
                        //system.out.println(ht.recordFileFromHash("dadoshash_basebib2MEDIA1.txt"));
                    }

                    //LinkedList<Integer> dists = st.calculateDistance();
                    ////system.out.println(dists);

                    Storage in = new Storage(), out = new Storage();

                    Rules r = new Rules(wl);
                    r.applyRules(st);
                    //dists = st.calculateDistance();
                    //r.recognizeBlocks(st, dists);
                    st.calculateDistance(in, out, filename);

                    printCollection(in, nomeSaida, wl, 0, 0);
                    /**
                     * *************************************************
                     */
                }
                /**
                 * *************************************************
                 */
            }

            ////system.out.println(Main.printList(wl));
            System.exit(0);
            //lc.refixList(lc.getList());
            ////system.out.println("Aqui 5");
            //cits = pc.catchCitations(lc.getList(), f, c, 5);
            //generateFile(nomeSaida, lc.getList(), c, 0, 0);
            //System.exit(0);

            // arrumar a extração de citações
            m.lookAndSubstituteMasks(lc.getList());

            //lc.refixList(lc.getList());
            ////system.out.println(Main.printList(lc.getList()));
            //System.exit(0);
            //cits = pc.catchCitations(lc.getList(), f);
            ////system.out.println(lc.getType());
            //Dependending on the type, the list receives a kind of treatment.
            //String nomeSaida = (nomearq.replace("testes/", "testes/saida/") + "_saida.html");

            if (lc.getType().equalsIgnoreCase("txt")) {
                cits = pc.catchCitations(lc.getList(), f, c, 5);
                //Statistics s = new Statistics();
                ////system.out.println(s.getModa(c));
                //generateFile(nomeSaida, lc.getList(), c, 0, 0);
                //system.out.println(Main1.printCits(cits));
            } else if (lc.getType().equalsIgnoreCase("html")) {
                cits = pc.catchCitationsHTMLTemp(lc.getList(), f, c, 5);
                //generateFile(nomeSaida, cits);
            }
//            }
            //if (load)    
            ////system.out.println(ht.recordFileFromHash("dadoshash_basebib2.txt"));
            // if (i==0) System.exit(0);
            ////system.out.println(ht.printData());
            ////system.out.println(hci.printData());
            ////system.out.println(ht.size());
            ////system.out.println(lc.printList());
            //if (cits != null) //system.out.println(Main.printLikeTreeSC(cits));
            ////system.out.println(lc.getType());
            // }
            ////system.out.println(printList(pc.ge));

//            for (DataCitation dc: c.getCitations()){
//                //system.out.println("Início: " + dc.getInicio() + " | Fim: " + dc.getFim());
//                for (int i = dc.getInicio(); i <= dc.getFim(); i++){
//                    if (lc.getList().get(i) instanceof TokenNP){
//                        TokenNP tp = (TokenNP) lc.getList().get(i) ;
//                        //system.out.print(tp.getToken() + " ");
//                    }
//                }
//                //system.out.println();
//            }


            ////system.out.println(hci.printData());

        } catch (IOException ex) {
            System.err.println(ex);
            //system.out.println("Não foi possível abrir o arquivo ");
            ex.printStackTrace();
        }

        //finish(); //:)

    }

    private static void finish() {
        //TesteAudio ta = new TesteAudio();
        //ta.testePlay("preview.mp3");
    }

    private static String printCits(LinkedList<LinkedList<SuperClasse>> sc) {
        StringBuilder sb = new StringBuilder("IMPRESSÃO\n");
        int i = 0;

        for (LinkedList<SuperClasse> s : sc) {
            i++;
            sb.append(i);

            for (SuperClasse s1 : s) {
                if (s1 instanceof TagsNP) {
                    sb.append("\n--->Tags<---\n").append(((TagsNP) s1).printTags()).append("\n" + "--->/Tags<---\n");
                } else if (s1 instanceof TokenNP) {
                    sb.append(((TokenNP) s1).getToken()).append("<").append(((TokenNP) s1).getTag()).append("> ");
                }
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public static String printLikeTree(LinkedList<LinkedList<TokenNP>> s) {
        StringBuilder sb = new StringBuilder();
        ////system.out.println(s.size());
        int i = 0;
        for (LinkedList<TokenNP> sk : s) {
            sb.append("\tItem ").append(i).append(" \n");
            for (TokenNP s1 : sk) {
                sb.append("\t\t").append(((TokenNP) s1).getToken()).append("<").append(((TokenNP) s1).getTag()).append("> \n");
            }
            i++;
        }

        return sb.toString();
    }

    public static String printLikeTreeSC(LinkedList<LinkedList<SuperClasse>> s) {
        StringBuilder sb = new StringBuilder();
        ////system.out.println(s.size());
        int i = 0;
        for (LinkedList<SuperClasse> sk : s) {
            sb.append("\tItem " + i + " \n");
            for (SuperClasse s1 : sk) {
                if (s1 instanceof TagsNP) {
                    sb.append("--->Tags<---\n" + ((TagsNP) s1).printTags() + "--->/Tags<---");
                } else if (s1 instanceof TokenNP) {
                    sb.append(((TokenNP) s1).getToken() + "<" + ((TokenNP) s1).getTag() + "> ");
                }
            }
            i++;
        }

        return sb.toString();
    }

    public static String printList(LinkedList<SuperClasse> s) {
        StringBuilder sb = new StringBuilder();
        ////system.out.println(s.size());
        for (SuperClasse s1 : s) {
            if (s1 instanceof TagsNP) {
                sb.append("--->Tags<---\n" + ((TagsNP) s1).printTags() + "\n" + "--->/Tags<---\n");
            } else if (s1 instanceof TokenNP) {
                sb.append(((TokenNP) s1).getToken() + "<" + ((TokenNP) s1).getTag() + "> \n");
            }
        }

        return sb.toString();
    }

    public static void printCollection(Storage st, String nome, LinkedList<SuperClasse> wl, int op, int showSize) throws IOException {
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
        conteudo += "<div>Arquivo: " + nome + "</div><div>" + st.getStorage().size() + " Cita&ccedil;&otilde;es</div><table><tr style='background: gray;'><td>N&uacute;mero</td><td>Cita&ccedil;&atilde;o</td></tr>";

        String bg;
        int i = 0;

        for (PCitation p : st.getStorage()) {
            if ((i + 1) % 2 == 0) {
                bg = "white";
            } else {
                bg = "#eee";
            }



            conteudo += "<tr><td style='background: " + bg + " '>" + (i + 1) + "</td><td style='background: " + bg
                    + " '>";

            for (int j = p.getBegin(); j <= p.getEnd() && p.getBegin() > 0; j++) {
                SuperClasse l = wl.get(j);
                if (l instanceof TokenNP) {
                    ////system.out.println("\"" + ((TokenNP)l).getToken() + "\"");
                    if (((TokenNP) l).getToken().trim().equalsIgnoreCase("<barran>")) {
                        if (op == 1) {
                            conteudo += "&lt;barran&gt;";
                        }
                    } else {
                        conteudo += (((TokenNP) l).getToken() + " ");
                        if (op == 1) {
                            conteudo += ("&lt;" + ((TokenNP) l).getTag() + "&gt;");
                        }
                    }
                    //
                }
            }


            if (showSize == 1) {
                conteudo += " Tamanho: " + (p.getEnd() - p.getBegin()) + "</td></tr>";
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

    public static String printLists(LinkedList<TokenNP> s) {
        StringBuilder sb = new StringBuilder();
        ////system.out.println(s.size());
        for (TokenNP s1 : s) {
            sb.append(s1.getToken() + "<" + s1.getTag() + "> \n");
        }

        return sb.toString();
    }

    public static void generateFile(String nome, LinkedList<LinkedList<SuperClasse>> ls) throws IOException {
        File f = new File(nome);

        if (f.exists()) {
            f.delete();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "ISO8859-1"));
        String conteudo = new String();

        conteudo = "<html><head></head><body style='font-family: Arial, Tahoma;'>";
        conteudo += "<div>Arquivo: " + nome + "</div><div>" + ls.size() + " Cita&ccedil;&otilde;es</div><table><tr style='background: gray;'><td>Número</td><td>Cita&ccedil;&atilde;o</td></tr>";

        String bg;

        for (int i = 0; i < ls.size(); i++) {
            LinkedList<SuperClasse> l = ls.get(i);

            if ((i + 1) % 2 == 0) {
                bg = "white";
            } else {
                bg = "#eee";
            }

            conteudo += "<tr><td style='background: " + bg + " '>" + (i + 1) + "</td><td style='background: " + bg
                    + " '>";

            for (SuperClasse s : l) {
                if (s instanceof TokenNP) {
                    conteudo += ListCreator.codifyTextToHtml(((TokenNP) s).getToken() + " ");
                }
            }

            conteudo += "</td></tr>";
        }

        conteudo += "</table></body></html>";
        fw.write(conteudo);
        fw.close();
    }

    public static void generateStringFile(String nome, LinkedList<String> ls) throws IOException {
        File f = new File(nome);

        if (f.exists()) {
            f.delete();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "ISO8859-1"));
        StringBuilder conteudo = new StringBuilder();

        conteudo.append("<html><head></head><body style='font-family: Arial, Tahoma;'>");
        conteudo.append("<div>Arquivo: ").append(nome).append("</div><div>").append(ls.size()).append(" Cita&ccedil;&otilde;es</div><table><tr style='background: gray;'><td>Número</td><td>Cita&ccedil;&atilde;o</td></tr>");

        String bg;
        //system.out.println(ls.size());
        for (int i = 0; i < ls.size(); i++) {
            String l = ls.get(i);

            if ((i + 1) % 2 == 0) {
                bg = "white";
            } else {
                bg = "#eee";
            }

            conteudo.append("<tr><td style='background: ").append(bg).append(" '>").append(i + 1).append("</td>" + "<td style='background: ").append(bg).append(" '>").append(l).append("</td></tr>");
        }

        conteudo.append("</table></body></html>");

        fw.write(conteudo.toString());
        fw.close();
    }

    public static void generateFile(String nome, LinkedList<SuperClasse> ls, Citations c, int op, int showSize) throws IOException {
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
        conteudo += "<div>Arquivo: " + nome + "</div><div>" + c.getCitations().size() + " Cita&ccedil;&otilde;es</div><table><tr style='background: gray;'><td>N&uacute;mero</td><td>Cita&ccedil;&atilde;o</td></tr>";

        String bg;
        int i = 0;

        for (DataCitation dc : c.getCitations()) {
            if ((i + 1) % 2 == 0) {
                bg = "white";
            } else {
                bg = "#eee";
            }



            conteudo += "<tr><td style='background: " + bg + " '>" + (i + 1) + "</td><td style='background: " + bg
                    + " '>";

            for (int j = dc.getBegin(); j <= dc.getEnd(); j++) {
                SuperClasse l = ls.get(j);
                if (l instanceof TokenNP) {
                    ////system.out.println("\"" + ((TokenNP)l).getToken() + "\"");
                    if (((TokenNP) l).getToken().trim().equalsIgnoreCase("<barran>")) {
                        if (op == 1) {
                            conteudo += "&lt;barran&gt;";
                        }
                    } else {
                        conteudo += (((TokenNP) l).getToken() + " ");
                        if (op == 1) {
                            conteudo += ("&lt;" + ((TokenNP) l).getTag() + "&gt;");
                        }
                    }
                    //
                }
            }


            if (showSize == 1) {
                conteudo += " Tamanho: " + (dc.getEnd() - dc.getBegin()) + "</td></tr>";
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

    private static void corrigeCities(String filename) {
        try {
            File f = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(f));

            String in;
            StringBuilder sb = new StringBuilder();

            while (br.ready()) {
                in = br.readLine();
                in = in.replaceAll("\\(.*\\)", "");
                //system.out.println(in);
                if (!in.trim().equals("")) {
                    sb.append(in.trim() + "\n");
                }
            }

            br.close();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
            bw.write(sb.toString());
            bw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generateFileT(String nome, LinkedList<LinkedList<TokenNP>> ls) throws IOException {
        File f = new File(nome);

        if (f.exists()) {
            f.delete();
        }

        if (!f.exists()) {
            f.createNewFile();
        }

        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "ISO8859-1"));
        String conteudo = new String();

        conteudo = "<html><head></head><body style='font-family: Arial, Tahoma;'>";
        conteudo += "<div>Arquivo: " + nome + "</div><div>" + ls.size() + " Cita&ccedil;&otilde;es</div><table><tr style='background: gray;'><td>Número</td><td>Cita&ccedil;&atilde;o</td></tr>";

        String bg;

        for (int i = 0; i < ls.size(); i++) {
            //system.out.println(i);
            LinkedList<TokenNP> l = ls.get(i);

            if ((i + 1) % 2 == 0) {
                bg = "white";
            } else {
                bg = "#eee";
            }

            conteudo += "<tr><td style='background: " + bg + " '>" + (i + 1) + "</td><td style='background: " + bg
                    + " '>";

            for (TokenNP s : l) {
                conteudo += ListCreator.codifyTextToHtml(s.getToken() + " ");
            }

            conteudo += "</td></tr>";
        }

        conteudo += "</table></body></html>";
        fw.write(conteudo);
        fw.close();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com