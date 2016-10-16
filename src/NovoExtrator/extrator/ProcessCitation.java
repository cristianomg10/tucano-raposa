/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.extrator;

import NovoExtrator.structures.SuperClasse;
import NovoExtrator.filehandlers.Formats;
import NovoExtrator.filehandlers.FileHandler;
import NovoExtrator.structures.TagsNP;
import NovoExtrator.structures.TokenNP;
import NovoExtrator.structures.HashToken;
import NovoExtrator.extrator.Citations.DataCitation;
import NovoExtrator.filehandlers.FormatsCalculus;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe com objetivo principal de extrair as citações.
 *
 * @author cristiano
 *
 */
public class ProcessCitation {

    private LinkedList<LinkedList<SuperClasse>> cits;

    /**
     * This method returns the amount of extracted citations.
     *
     * @return the amount of citations, type int.
     */
    public int getCitCount() {
        return cits.size();
    }

    private boolean equality(String a, String b) {
        if (a.equalsIgnoreCase("") && a.equalsIgnoreCase("")) {
            return true;
        }
        if (a.equalsIgnoreCase("") || a.equalsIgnoreCase("")) {
            return false;
        }

        if (a.charAt(0) == '<' && a.length() > 3 && b.length() > 3) {
            if (((String) a.subSequence(0, 3)).equalsIgnoreCase(((String) b.subSequence(0, 3)))) {
                return true;
            } else {
                return false;
            }
        } else if (a.equalsIgnoreCase("<barran>") && b.equalsIgnoreCase("<barran>")) {
            return true;
        } else if (!a.equalsIgnoreCase("<barran>") && b.equalsIgnoreCase("<barran>")
                || a.equalsIgnoreCase("<barran>") && !b.equalsIgnoreCase("<barran>")) {
            return false;
        } else if (FileHandler.isNumber(a) && FileHandler.isNumber(b)) {
            return true;
        } else if ((FileHandler.isNumber(a) && b.equals("%d")) || (a.equals("%d") && FileHandler.isNumber(b))) {
            return true;
        } else if (a.equalsIgnoreCase(b)) {
            return true;
        } else if (((!FileHandler.isNumber(a) && b.equals("%s")) || (a.equals("%s") && !FileHandler.isNumber(b)))) {
            return true;
        }
        return false;
    }

    private void insere(LinkedList<TokenNP> ls, TokenNP s) {
        if (FileHandler.isNumber(s.getToken())) {
            TokenNP t = new TokenNP("%d", "number");
            ls.add(t);
        } else if (ListCreator.isSymbol(s.getToken())) {
            TokenNP t = new TokenNP(s.getToken(), "symbol");
            ls.add(t);
        } else if (s.getToken().equals("<barran>")) {
            TokenNP t = new TokenNP(s.getToken(), "symbol");
            ls.add(t);
        } else {
            TokenNP t = new TokenNP("%s", "string");
            ls.add(t);
        }
    }

    private void insereSC(LinkedList<SuperClasse> ls, TokenNP s) {
        if (FileHandler.isNumber(s.getToken()) && !s.getTag().equalsIgnoreCase("year")) {
            TokenNP t = new TokenNP("%d", "number");
            ls.add(t);
        } else if (ListCreator.isSymbol(s.getToken())) {
            TokenNP t = new TokenNP(s.getToken(), "symbol");
            ls.add(t);
        } else if (s.getToken().equals("<barran>")) {
            TokenNP t = new TokenNP(s.getToken(), "symbol");
            ls.add(t);
        } else {
            TokenNP t = new TokenNP("%s", "string");
            ls.add(t);
        }
    }

    private void insere(LinkedList<SuperClasse> ls, String s) {
        if (ls.peekLast() != null && ls.peekLast() instanceof TagsNP) {
            TagsNP t = (TagsNP) ls.peekLast();
            t.insert(s);
        } else {
            TagsNP t = new TagsNP();
            t.insert(s);
            ls.add(t);
        }
    }

    private LinkedList<TokenNP> getIntersection(LinkedList<LinkedList<SuperClasse>> s, int expansion) {
        LinkedList<TokenNP> common = new LinkedList();
        boolean test = false;

        saitudo:
        for (int i = 0; i < s.size(); i++) {
            LinkedList<SuperClasse> ss = s.get(i);


            for (int j = 0; j < expansion; j++) {

                SuperClasse s1 = ss.get(j);
                if (test) {
                    test = !test;
                    j--;
                }


                if (s1 instanceof TokenNP) {
                    ////system.out.println(((TokenNP) s1).getToken().equals(""));
                    if (((TokenNP) s1).getToken().equals("")) {
                        test = true;
                        continue;
                    }
                    ////system.out.println(((TokenNP) s1).getToken() + "<<<<<");
                    this.insere(common, (TokenNP) s1);
                } else {
                    int ultimo = (((TagsNP) s1).getTags().size() - 1);
                    LinkedList lista = ((TagsNP) s1).getTags();
                    for (int k = j; k < expansion; k++) {
                        this.insere(common, new TokenNP(lista.get(ultimo - k).toString(), ""));
                    }
                    break saitudo;
                }
            }
            break;
        }

//        //system.out.println("Impressao Common " + common.size());
//        for (SuperClasse v: common){
//            if (v instanceof TokenNP){
//                //system.out.println(((TokenNP)v).getToken() + " : " + ((TokenNP)v).getTag());
//            }
//        }
//        //system.out.println("TAMANHO: " + common.size());
        return common;
    }

    private String getIntersection(String a, String b) {
        int c = a.indexOf(b);
        String d = a.substring(0, c);

        return d;
    }

    private LinkedList<TokenNP> analyzeCommonList(LinkedList<TokenNP> initial) {
        LinkedList<TokenNP> n = (LinkedList<TokenNP>) initial.clone();
        if (initial.size() <= 0) {
            return n;
        }
        if (initial.get(0).getToken().equalsIgnoreCase("%s") || initial.get(0).getToken().equalsIgnoreCase(".")) {
            n.remove();
        } else if (initial.get(initial.size() - 1).getToken().equalsIgnoreCase("%s")) {
            n.removeLast();
        }

        return n;
    }

    private LinkedList<LinkedList<TokenNP>> getListVariations(LinkedList<TokenNP> common) {
        LinkedList<LinkedList<TokenNP>> l = new LinkedList();
        for (int i = 0; i < common.size() - 2; i++) {
            LinkedList<TokenNP> com = new LinkedList();

            for (int j = i; j < common.size(); j++) {
                com.add(common.get(j));
            }

            l.add(com);
        }

        return l;
    }

    private LinkedList<LinkedList<SuperClasse>> getListVariationsHTML(LinkedList<TokenNP> common) {
        LinkedList<LinkedList<SuperClasse>> l = new LinkedList();
        for (int i = 0; i < common.size() - 2; i++) {
            LinkedList<SuperClasse> com = new LinkedList();

            for (int j = i; j < common.size(); j++) {
                com.add(common.get(j));
            }

            l.add(com);
        }

        return l;
    }

    private LinkedList<TokenNP> getIntersectionTest(LinkedList<LinkedList<SuperClasse>> s, int expansion) {
        LinkedList<TokenNP> common = new LinkedList();
        LinkedList<LinkedList<TokenNP>> commons = new LinkedList();
        LinkedList<Integer> commonsQty = new LinkedList();
        LinkedList<TokenNP> lista;

        boolean test = false;

        for (LinkedList<SuperClasse> sc : s) {
            lista = new LinkedList();
            int i = 0;
            for (SuperClasse sd : sc) {
                if (sd instanceof TokenNP) {
                    insere(lista, (TokenNP) sd);
                } else {
                    insere(lista, new TokenNP("<tag>", "<tag>"));
                }
                i++;
                if (i == expansion) {
                    break;
                }
            }

            //system.out.println("I:" + i);
            test = false;
            if (commons.size() == 0) {
                commons.add(lista);
                commonsQty.add(1);
            } else {
                fora:
                for (i = 0; i < commons.size(); i++) {

                    int j = 0;


                    for (int k = 0; k < lista.size(); k++) {
                        ////system.out.println(k + " Valor k");
                        ////system.out.println(lista.get(k).getToken() + " ==> " + commons.get(i).get(j).getToken());
                        ////system.out.println("LISTA: " + Main.printLists(lista));
                        if (j == commons.get(i).size()) {
                            break;
                        }
                        if (this.equality(lista.get(k).getToken(), commons.get(i).get(j).getToken())) {
                            test = true;
                            j++;
                            if (j == expansion) {
                                ////system.out.println("COMMONS.GET(" + i + "): " + Main.printLists(commons.get(i)));
                                commonsQty.set(i, commonsQty.get(i) + 1);
                                ////system.out.println("Incrementou");
                                //i = 0;
                                break fora;
                            }
                        } else {
                            test = false;

                            i++;
                            k = -1;
                            j = 0;
                            ////system.out.println("Verificar proximo");
                            if (i == commons.size()) {
                                break;
                            }
                            continue;
                            //break fora;
                        }
                    }
                    if (!test) {
                        commons.add(lista);
                        commonsQty.add(1);
                        ////system.out.println("Adicionou");
                    }
                }

            }

        }



        int maior = 0;
        int contador = 0;
        for (int i = 0; i < commonsQty.size(); i++) {
            ////system.out.println(commonsQty.get(i) + " >= " + maior);
            if (commonsQty.get(i) >= commonsQty.get(maior)) {
                maior = i;
                ////system.out.println(maior + "m" + i);
            }
        }
        ////system.out.println("Maior: " + maior + "Qtde: " + commonsQty.size());

        if (commonsQty.size() > 0) {
            common = commons.get(maior);
        } else {
            common = new LinkedList();
        }
        // //system.out.println(commons.size());

        // //system.out.println(Main.printLikeTree(commons));
        // System.exit(0);

//        //system.out.println("Impressao Common " + common.size());
//        for (SuperClasse v: common){
//            if (v instanceof TokenNP){
//                //system.out.println(((TokenNP)v).getToken() + " : " + ((TokenNP)v).getTag());
//            }
//        }
        // //system.out.println("TAMANHO: " + common.size() + " // Maior: " + maior);

//        for (int i = 0; i < commons.size(); i++){
//            //system.out.println("Commons(" + i + "): " + Main.printLists(commons.get(i)));
//            //system.out.println("CommonsQty(" + i + "): " +commonsQty.get(i));
//        }
//
        ////system.out.println("E agora?" + Main.printLikeTree(commons));
        common = analyzeCommon(common);
        //system.out.println(Main.printLists(common));

        return common;
    }

    private LinkedList<TokenNP> analyzeCommon(LinkedList<TokenNP> c) {
        int numberCounter = 0;
        int firstNumberPosition = -1;
        for (int i = 0; i < c.size(); i++) {
            TokenNP t = (TokenNP) c.get(i);

            if (t.getToken().equalsIgnoreCase("%d")) {
                numberCounter++;
                if (firstNumberPosition == -1) {
                    firstNumberPosition = i + 1;
                }
            }
        }

        ////system.out.println("1" + Main.printLists(c));

        if (numberCounter > 0) {
            for (int i = 0; i < c.size(); i++) {
                TokenNP t = (TokenNP) c.get(i);
                if (t.getToken().equalsIgnoreCase("%s")) {
                    c.remove(i);
                    continue;
                }
                if (t.getToken().equalsIgnoreCase("[")) {
                    break;
                }
                if (t.getToken().equalsIgnoreCase("(")) {
                    break;
                }
                break;
            }
        }

        ////system.out.println("2" + Main.printLists(c));
        if (numberCounter > 1) {

            for (int i = 0; i < firstNumberPosition - 1; i++) {
                c.remove(i);
            }
        }

        ////system.out.println("3" + Main.printLists(c));
        ////system.out.println(firstNumberPosition);

        boolean next = false;
        for (int i = 0; i < c.size(); i++) {
            TokenNP t = (TokenNP) c.get(i);
            if ((!(t.getToken().equalsIgnoreCase("%d")) || t.getToken().equals("<barran>"))) {
                t.setTag("out");
                continue;
            }
            if (next) {
                t.setTag("in"); // inner to citation
                continue;
            }
            t.setTag("out"); //out of the citation
            if (t.getToken().equalsIgnoreCase("%s") && !next) {
                t.setTag("in");
                next = true;

            }
        }

        //system.out.println(1 + Main.printLists(c));

        if (c.size() > 4) {
            if (c.get(1).getToken().equalsIgnoreCase("-")
                    && c.get(0).getToken().equalsIgnoreCase("%d")
                    && c.get(2).getToken().equalsIgnoreCase("%d")) {
                c.remove();
                c.remove();//.setTag("in");
                c.remove();//setTag("in");                
            }
        }

        if (c.size() > 4) {
            if (c.get(0).getToken().equalsIgnoreCase("%d")
                    && c.get(1).getToken().equalsIgnoreCase(".")
                    && c.get(2).getToken().equalsIgnoreCase("<barran>")) {
                c.remove(0);
            }
        }


        return c;
    }

    private LinkedList<SuperClasse> getIntersectionHTML(LinkedList<LinkedList<SuperClasse>> s, int expansion) {
        LinkedList<SuperClasse> common = new LinkedList();
        LinkedList<LinkedList<SuperClasse>> commons = new LinkedList();
        LinkedList<Integer> commonsQty = new LinkedList();
        LinkedList<SuperClasse> lista;

        boolean test = false;
        boolean lastTag = false;

        for (LinkedList<SuperClasse> sc : s) {
            lista = new LinkedList();
            int i = 0;
            for (SuperClasse sd : sc) {
                if (sd instanceof TokenNP) {
                    insereSC(lista, (TokenNP) sd);
                } else if (sd instanceof TagsNP) {
                    for (int j = (((TagsNP) sd).getTags().size() - 1); j > ((TagsNP) sd).getTags().size() - (expansion + 1); j--) {
                        insere(lista, (String) (((TagsNP) sd).getTags().get(j)));
                    }
                    break;
                }
                i++;
                if (i == expansion) {
                    break;
                }
            }

            //system.out.println(Main.printList(lista));
            System.exit(0);

            test = false;
            if (commons.size() == 0) {
                commons.add(lista);
                commonsQty.add(1);
            } else {
                fora:
                for (i = 0; i < commons.size(); i++) {

                    int j = 0;


                    for (int k = 0; k < lista.size(); k++) {
                        ////system.out.println(k + " Valor k");
                        ////system.out.println(lista.get(k).getToken() + " ==> " + commons.get(i).get(j).getToken());
                        ////system.out.println("LISTA: " + Main.printLists(lista));

                        if (lista.get(k) instanceof TokenNP && commons.get(i).get(j) instanceof TokenNP) {
                            if (this.equality(((TokenNP) lista.get(k)).getToken(), ((TokenNP) commons.get(i).get(j)).getToken())) {
                                test = true;
                                j++;
                                if (j == expansion) {
                                    //system.out.println("COMMONS.GET(" + i + "): " + Main.printList(commons.get(i)));
                                    commonsQty.set(i, commonsQty.get(i) + 1);
                                    //system.out.println("Incrementou");
                                    //i = 0;
                                    break fora;
                                }
                            } else {
                                test = false;

                                i++;
                                k = -1;
                                j = 0;
                                //system.out.println("Verificar proximo");
                                if (i == commons.size()) {
                                    break;
                                }
                                continue;
                                //break fora;
                            }
                        } else if (lista.get(k) instanceof TagsNP) {
                            //Arrumar isso aqui! Falta só isso.
                            for (LinkedList<SuperClasse> com : commons) {
                                for (SuperClasse c1 : com) {
                                    if (c1 instanceof TagsNP) {
                                        //system.out.println("eNTROU");
                                        String s1 = this.convertsToStrings(((TagsNP) c1).getTags());
                                        String s2 = this.convertsToStrings(((TagsNP) lista.get(k)).getTags());

                                        String r = s1.replace(s2, "");
                                        String r1 = s2.replace(s1, "");
                                        if (!r.equalsIgnoreCase(s1) || !r1.equalsIgnoreCase(s2)) {
                                            break fora;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!test) {
                        commons.add(lista);
                        commonsQty.add(1);
                        //system.out.println("Adicionou");
                    }
                }

            }
        }



        int maior = 0;
        int contador = 0;
        for (int i = 0; i < commonsQty.size(); i++) {
            //system.out.println(commonsQty.get(i) + " >= " + maior);
            if (commonsQty.get(i) >= commonsQty.get(maior)) {
                maior = i;
                //system.out.println(maior + "m" + i);
            }
        }
        ////system.out.println("Maior: " + maior + "Qtde: " + commonsQty.size());

        if (commonsQty.size() > 0) {
            common = commons.get(maior);
        } else {
            common = new LinkedList();
        }
        // //system.out.println(commons.size());

        // //system.out.println(Main.printLikeTree(commons));
        // System.exit(0);

//        //system.out.println("Impressao Common " + common.size());
//        for (SuperClasse v: common){
//            if (v instanceof TokenNP){
//                //system.out.println(((TokenNP)v).getToken() + " : " + ((TokenNP)v).getTag());
//            }
//        }
        ////system.out.println("TAMANHO: " + common.size() + " // Maior: " + maior);

        for (int i = 0; i < commons.size(); i++) {
            //system.out.println("Commons(" + i + "): " + Main.printList(commons.get(i)));
            //system.out.println("CommonsQty(" + i + "): " + commonsQty.get(i));
        }

        //system.out.println(Main.printList(common));

        return common;
    }

    private void fixDoubledCitations(LinkedList<LinkedList<SuperClasse>> s) {
        String str1;
        String str2;

        for (int i = 0; i < s.size() - 1; i++) {
            LinkedList<SuperClasse> l = s.get(i);

            for (int j = i + 1; j < s.size(); j++) {
                boolean equal = false;

                LinkedList<SuperClasse> l1 = s.get(j);
                LinkedList<SuperClasse> menor;
                if (s.get(i).size() >= s.get(j).size()) {
                    menor = s.get(j);
                } else {
                    menor = s.get(i);
                }

                for (int k = 0; k < menor.size(); k++) {
                    ////system.out.println (i + " " + j + " " + k + " " + s.get(i).size() + " " + s.get(j).size());
                    if (s.get(i).get(k) instanceof TokenNP && s.get(j).get(k) instanceof TokenNP) {
                        str1 = ((TokenNP) s.get(i).get(k)).getToken();
                        str2 = ((TokenNP) s.get(j).get(k)).getToken();

                        if (!str1.equalsIgnoreCase(str2)) {
                            equal = false;
                            break;
                        } else {
                            equal = true;
                        }
                    }
                }

                if (equal) {
                    s.remove(menor);
                }
            }
        }
    }

    private boolean verifyEquality(String a, String b) {
        if ((a.equalsIgnoreCase("journal") && b.equalsIgnoreCase("booktitle"))
                || (b.equalsIgnoreCase("journal") && a.equalsIgnoreCase("booktitle"))) {
            return true;
        } else {
            return equality(a, b);
        }

    }

    public LinkedList<LinkedList<SuperClasse>> catchCitations(LinkedList<SuperClasse> global, Formats formats) {
        LinkedList<LinkedList<String>> ls = formats.getFormats();
        LinkedList<LinkedList<SuperClasse>> cit = new LinkedList();


        //for each linkedlist de ls
        for (LinkedList<String> f : ls) {

            int i = 0; //reset the index of linkedlist
            int j = 0;
            LinkedList<SuperClasse> s = new LinkedList(); //create a new linkedlist, to be the citation

            //run the global linkedlist
            while (j < global.size()) {

//                 if (s.size() > 0){
//                    for (SuperClasse sc: s){
//                        if (sc instanceof SuperClasse) //system.out.print(((TokenNP)sc).getToken() + " ");
//                    }
//                    //system.out.println();
//                 }
                ////system.out.println(i + " " + j + " " + cit.size());
                //This line below verifies if the item is a TokenNP
                if (global.get(j) instanceof TokenNP) {

                    TokenNP t = (TokenNP) global.get(j);
                    if (t.getToken().equalsIgnoreCase("<barran>")) {
                        continue; //novo
                    }
                    String tag = t.getTag();

                    //if the format at position i isn't equal to t's tag AND
                    //the list s doesn't have items
                    if (!f.get(i).equalsIgnoreCase(tag) && i == 0) {
                        //runs the global list until the current element is different to tag
                        boolean entrou = false;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    entrou = true;
                                    break;
                                }
                            }
                        }

                        if (!entrou) {
                            j++;
                        }
                        //if the format at position i isn't equal to t's tag,
                        //although the list s doesn't have items, yet.

                    } else if (!verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (!f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i = 0;
                        s = new LinkedList();
                        j++;

                        //if the format at position i is equal to t's tag, AND
                        //the list s is empty, yet.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == 0){
                        i++;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }

                        //if format f at position i is equal the t's tag AND i equals the size of list f
                        //This means that is completed the format, AND s contains a citation.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == (f.size() - 1)) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == (f.size() - 1)){
                        i = 0;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    cit.add(s);
                                    s = new LinkedList();
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                            if (k == global.size() - 1 && s.size() > 0) {
                                cit.add(s);
                            }
                        }
                    } else if (verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i++;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }
                    }
                } else {
                    j++;
                }

            }
        }

        //this.verifyCitations1();
        //this.verifyCitations();
        // this.utilizeTags();
        this.fixDoubledCitations(cit);
        this.cits = cit;
        this.validateCitations();

        return cit;
    }

    public LinkedList<LinkedList<SuperClasse>> catchCitations(LinkedList<SuperClasse> global, Formats formats, Citations c, int expansion) {
        LinkedList<LinkedList<String>> ls = formats.getFormats();
        LinkedList<LinkedList<SuperClasse>> cit = new LinkedList();


        //for each linkedlist de ls
        for (LinkedList<String> f : ls) {

            int i = 0; //reset the index of linkedlist
            int j = 0;
            int inicio = 0;
            int fim = 0;
            LinkedList<SuperClasse> s = new LinkedList(); //create a new linkedlist, to be the citation

            //run the global linkedlist
            while (j < global.size()) {

//                 if (s.size() > 0){
//                    for (SuperClasse sc: s){
//                        if (sc instanceof SuperClasse) //system.out.print(((TokenNP)sc).getToken() + " ");
//                    }
//                    //system.out.println();
//                 }
                ////system.out.println(i + " " + j + " " + cit.size());
                //This line below verifies if the item is a TokenNP
                if (global.get(j) instanceof TokenNP) {

                    TokenNP t = (TokenNP) global.get(j);
                    String tag = t.getTag();

                    //if the format at position i isn't equal to t's tag AND
                    //the list s doesn't have items
                    if (!f.get(i).equalsIgnoreCase(tag) && i == 0) {
                        //runs the global list until the current element is different to tag
                        boolean entrou = false;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    entrou = true;
                                    break;
                                }
                            }
                        }
                        inicio = 0;
                        if (!entrou) {
                            j++;
                        }
                        //if the format at position i isn't equal to t's tag,
                        //although the list s doesn't have items, yet.

                    } else if (!verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (!f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i = 0;
                        inicio = 0;
                        s = new LinkedList();
                        j++;

                        //if the format at position i is equal to t's tag, AND
                        //the list s is empty, yet.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == 0){
                        inicio = j;
                        i++;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }

                        //if format f at position i is equal the t's tag AND i equals the size of list f
                        //This means that is completed the format, AND s contains a citation.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == (f.size() - 1)) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == (f.size() - 1)){
                        i = 0;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    int begin = inicio - expansion > 0 ? inicio - expansion : 0;
                                    int end = (k + expansion) < global.size() ? k + expansion : global.size();
//
//
//
                                    ////system.out.println(end + "<-> " + k + " <-> " + fim);

                                    for (int a = inicio - 1; a >= begin; a--) {
                                        s.add(0, global.get(a));
//                                        //system.out.println("Passou aqui");
                                    }
                                    for (int a = k; a < end; a++) {
                                        s.add(global.get(a));
//                                        //system.out.println("Passou aqui 2");
                                    }
                                    //c.insert(inicio, k);
                                    cit.add(s);

                                    //    inicio = 0;
                                    //    fim = 0;

                                    s = new LinkedList();
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));

                            }

                        }
                        inicio = 0;
                        fim = 0;

                    } else if (verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i++;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }
                    }
                } else {
                    j++;
                }
                ////system.out.println(j);

            }
        }




        //this.verifyCitations1();
        //this.verifyCitations();
        // this.utilizeTags();

        this.cits = cit;
        //this.getCitationWithMarks(this.getIntersection(cit, expansion), global);


        //this.getCitationWithMarksPointer(this.getIntersectionTest(cit, expansion), global, c);
        this.getCitationWithMarksPointerTemp(getListVariations(this.getIntersectionTest(cit, expansion)), global, c);


        this.fixDoubledCitations(cit);
        //this.validateCitations();
        //this.returnCommon(expansion);
        ////system.out.println(Main.printLists(this.getIntersectionTest(cit, expansion)));
        return cit;
    }

    private String getLastOfTagElement(TagsNP t) {
        return (String) t.getTags().getLast();
    }

    private String generateEndTag(String t) {
        if (t.contains("<") && t.contains(">")) {
            int v = t.indexOf(" ");

            String aux;
            if (v > 0) {
                aux = t.substring(0, v);
            } else {
                aux = t;
            }

            if (t.length() > 2 && !t.subSequence(0, 2).equals("</")) {
                aux = aux.replace("<", "</");
            }

            if (aux.charAt(aux.length() - 1) != '>') {
                aux = aux.trim() + ">";
            }
            return aux;
        }
        return "";
    }

    public LinkedList<LinkedList<SuperClasse>> catchCitationsHTMLTemp(LinkedList<SuperClasse> global, Formats formats, Citations c, int expansion) {
        LinkedList<LinkedList<String>> ls = formats.getFormats();
        LinkedList<LinkedList<SuperClasse>> cit = new LinkedList();
        LinkedList<String> tokens = new LinkedList();


        //for each linkedlist de ls
        for (LinkedList<String> f : ls) {

            int i = 0; //reset the index of linkedlist
            int j = 0;
            int inicio = 0;
            int fim = 0;
            LinkedList<SuperClasse> s = new LinkedList(); //create a new linkedlist, to be the citation

            //run the global linkedlist
            while (j < global.size()) {

//                 if (s.size() > 0){
//                    for (SuperClasse sc: s){
//                        if (sc instanceof SuperClasse) //system.out.print(((TokenNP)sc).getToken() + " ");
//                    }
//                    //system.out.println();
//                 }
                ////system.out.println(i + " " + j + " " + cit.size());
                //This line below verifies if the item is a TokenNP
                if (global.get(j) instanceof TokenNP) {

                    TokenNP t = (TokenNP) global.get(j);
                    String tag = t.getTag();

                    //if the format at position i isn't equal to t's tag AND
                    //the list s doesn't have items
                    if (!f.get(i).equalsIgnoreCase(tag) && i == 0) {
                        //runs the global list until the current element is different to tag
                        boolean entrou = false;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    entrou = true;
                                    break;
                                }
                            }
                        }
                        inicio = 0;
                        if (!entrou) {
                            j++;
                        }
                        //if the format at position i isn't equal to t's tag,
                        //although the list s doesn't have items, yet.

                    } else if (!verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (!f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i = 0;
                        inicio = 0;
                        s = new LinkedList();
                        j++;

                        //if the format at position i is equal to t's tag, AND
                        //the list s is empty, yet.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == 0){
                        inicio = j;
                        i++;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }

                        //if format f at position i is equal the t's tag AND i equals the size of list f
                        //This means that is completed the format, AND s contains a citation.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == (f.size() - 1)) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == (f.size() - 1)){
                        i = 0;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    int begin = inicio - expansion > 0 ? inicio - expansion : 0;
                                    int end = (k + expansion) < global.size() ? k + expansion : global.size();
//
//
//
                                    ////system.out.println(end + "<-> " + k + " <-> " + fim);


                                    int comeco = 0;
                                    String firstTag = "";
                                    for (int a = inicio - 1;; a--) {
                                        if (global.get(a) instanceof TokenNP) {
                                            s.add(0, global.get(a));
                                            comeco++;
                                            if (comeco == expansion + 1) {
                                                break;
                                            }
                                        } else {
                                            firstTag = this.getLastOfTagElement((TagsNP) global.get(a));
                                            tokens.add(tag);
                                            break;
                                        }
//                                        
                                    }



                                    comeco = 0;
                                    for (int a = k; a < end; a++) {
                                        if (global.get(a) instanceof TokenNP) {
                                            s.add(global.get(a));
                                            comeco++;
                                            if (comeco == expansion + 1) {
                                                break;
                                            }
                                        } else {
                                            TagsNP tp = (TagsNP) global.get(a);
                                            if (tp.getTags().contains(this.generateEndTag(firstTag))) {
                                                break;
                                            }
                                        }
//                                        //system.out.println("Passou aqui 2");
                                    }

//                                    int comeco = 0;
//                                    for (int a = inicio - 1; ; a--){
//                                        if (global.get(a) instanceof TokenNP) {
//                                            s.add(0, global.get(a));
//                                            comeco++;
//                                            if (comeco == expansion +1) break;
//                                        }
////                                        //system.out.println("Passou aqui");
//                                    }

//                                    comeco = 0;
//                                    for (int a = k; a < end; a++){
//                                        if (global.get(a) instanceof TokenNP) {
//                                            s.add(global.get(a));
//                                            comeco++;
//                                            if (comeco == expansion +1) break;
//                                        }
////                                        //system.out.println("Passou aqui 2");
//                                    }
                                    //c.insert(inicio, k);
                                    cit.add(s);

                                    //    inicio = 0;
                                    //    fim = 0;

                                    s = new LinkedList();
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));

                            }

                        }
                        inicio = 0;
                        fim = 0;

                    } else if (verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i++;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }
                    }
                } else {
                    j++;
                }

            }
        }




        //this.verifyCitations1();
        //this.verifyCitations();
        // this.utilizeTags();

        this.cits = cit;
        //this.getCitationWithMarks(this.getIntersection(cit, expansion), global);


        //this.getCitationWithMarksHTML(this.getIntersectionHTML(cit, expansion), global, c);
        ////system.out.println(expansion);
        ////system.out.println(Main.printLikeTreeSC(cit));
        ////system.out.println(Main.printList(global));
        //System.exit(0);
        this.getCitationWithMarksHTML(getListVariationsHTML(this.getIntersectionTest(cit, expansion)), global, c);


        this.fixDoubledCitations(cit);
        //this.validateCitations();
        //this.returnCommon(expansion);
        ////system.out.println(Main.printLists(this.getIntersectionTest(cit, expansion)));
        return cit;
    }

    public LinkedList<LinkedList<SuperClasse>> catchCitationsHTML(LinkedList<SuperClasse> global, Formats formats, int expansion) {
        LinkedList<LinkedList<String>> ls = formats.getFormats();
        LinkedList<LinkedList<SuperClasse>> cit = new LinkedList();


        //for each linkedlist de ls
        for (LinkedList<String> f : ls) {

            int i = 0; //reset the index of linkedlist
            int j = 0;
            int inicio = 0;
            int fim = 0;
            LinkedList<SuperClasse> s = new LinkedList(); //create a new linkedlist, to be the citation

            //run the global linkedlist
            while (j < global.size()) {

                if (s.size() > 0) {
                    for (SuperClasse sc : s) {
                        if (sc instanceof SuperClasse) {
                            //system.out.print(((TokenNP) sc).getToken() + " ");
                        }
                    }
                    //system.out.println();
                }
                //system.out.println(i + " " + j + " " + cit.size());
                //This line below verifies if the item is a TokenNP
                if (global.get(j) instanceof TokenNP) {

                    TokenNP t = (TokenNP) global.get(j);
                    String tag = t.getTag();

                    //if the format at position i isn't equal to t's tag AND
                    //the list s doesn't have items
                    if (!f.get(i).equalsIgnoreCase(tag) && i == 0) {
                        //runs the global list until the current element is different to tag
                        boolean entrou = false;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    entrou = true;
                                    break;
                                }
                            }
                        }
                        inicio = 0;
                        if (!entrou) {
                            j++;
                        }
                        //if the format at position i isn't equal to t's tag,
                        //although the list s doesn't have items, yet.

                    } else if (!verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (!f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i = 0;
                        inicio = 0;
                        s = new LinkedList();
                        j++;

                        //if the format at position i is equal to t's tag, AND
                        //the list s is empty, yet.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == 0){
                        inicio = j;
                        i++;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }

                        //if format f at position i is equal the t's tag AND i equals the size of list f
                        //This means that is completed the format, AND s contains a citation.
                    } else if (verifyEquality(f.get(i), t.getTag()) && i == (f.size() - 1)) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i == (f.size() - 1)){
                        i = 0;

                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    int begin = inicio - expansion > 0 ? inicio - expansion : 0;
                                    int end;// = (k + 2*(expansion)) < global.size()  ? k + (expansion) : global.size();
//
//
//
                                    ////system.out.println(end + "<-> " + k + " <-> " + fim);

                                    for (int a = inicio - 1; a >= begin; a--) {
                                        s.add(0, global.get(a));
//                                        //system.out.println("Passou aqui");
                                    }
                                    int a = k;
                                    while (a < global.size()) {
                                        if (global.get(a) instanceof TokenNP) {
                                            s.add(global.get(a));
////system.out.println("Passou aqui 2");
                                        } else if (global.get(a) instanceof TagsNP) {
                                            break;
                                        }
                                        a++;

                                    }
                                    cit.add(s);

                                    //    inicio = 0;
                                    //    fim = 0;

                                    s = new LinkedList();
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));

                            }

                        }
                        inicio = 0;
                        fim = 0;

                    } else if (verifyEquality(f.get(i), t.getTag()) && i != 0) {
                        //}else if (f.get(i).equalsIgnoreCase(t.getTag()) && i != 0){
                        i++;
                        for (int k = j; k < global.size(); k++) {
                            if (global.get(k) instanceof TokenNP) {
                                if (!verifyEquality(((TokenNP) global.get(k)).getTag(), tag)) {
                                    //if (!((TokenNP) global.get(k)).getTag().equalsIgnoreCase(tag)){
                                    j = k;
                                    break;
                                }
                                s.add(global.get(k));
                            }
                        }
                    }
                } else {
                    j++;
                }

            }
        }




        //this.verifyCitations1();
        //this.verifyCitations();
        // this.utilizeTags();

        this.cits = cit;
//        this.getCitationWithMarks(this.getIntersection(cit, expansion), global);
        ////system.out.println(Main.printList(this.getIntersectionHTML(cit, expansion)));
        //System.exit(0);
        //system.out.println(Main.printLikeTreeSC(cits));

        //this.getCitationWithMarksHTML(this.getIntersectionHTML(cit, expansion), global);

        //this.fixDoubledCitations(cit);
        //this.validateCitations();
        //this.returnCommon(expansion);
        ////system.out.println(Main.printLists(this.getIntersectionTest(cit, expansion)));
        return cit;
    }

    private void validateCitations() {
        for (int i = 0; i < cits.size(); i++) {
            if (!validateTitle(cits.get(i))) {
                cits.remove(i);
            }
        }
    }

    private boolean validateTitle(LinkedList<SuperClasse> s) {
        int countSymbols = 0;
        for (SuperClasse v : s) {
            if (v instanceof TokenNP) {
                TokenNP t = (TokenNP) v;
                ////system.out.println(t.getTag() + "<>" + ListCreator.isSymbol(t.getToken()));
                if (t.getTag().equalsIgnoreCase("title") && ListCreator.isSymbol(t.getToken())) {
                    countSymbols++;
                    if (countSymbols == 4) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void substitute(LinkedList<TokenNP> common, LinkedList<SuperClasse> sc) {
        boolean completo;
        int inicio;

        for (int i = 0; i < sc.size(); i++) {
            inicio = i;
            completo = true;
            for (int j = 0; j < common.size(); j++) {
                if (sc.get(i) instanceof TokenNP && common.get(j) instanceof TokenNP) {
                    TokenNP s1 = (TokenNP) sc.get(i);
                    TokenNP s2 = (TokenNP) common.get(j);
                    if (equality(s1.getToken(), s2.getToken())) {
                        completo = true;
                        i++;
                    } else {
                        completo = false;
                        break;
                    }
                }
            }
            if (completo) {
                int l = 0;
                for (int k = inicio; k < inicio + common.size(); k++, l++) {
                    ((TokenNP) sc.get(k)).setToken(common.get(l).getToken());
                }
            }
        }
    }

    private String convertsToStrings(LinkedList<String> sc) {
        StringBuilder sb = new StringBuilder();

        for (String s : sc) {
            sb.append(" " + s);
        }

        return sb.toString();
    }

    private String convertsToString(LinkedList<SuperClasse> sc) {
        StringBuilder sb = new StringBuilder();

        for (SuperClasse s : sc) {
            if (s instanceof TokenNP) {
                sb.append(" " + ((TokenNP) s).getToken());
            } else if (s instanceof TagsNP) {
                for (String g : (LinkedList<String>) ((TagsNP) s).getTags()) {
                    sb.append(" " + g);
                }
            }
        }

        return sb.toString();
    }

    private String convertToSymbol(String s) {
        if (s.equals("<barran>")) {
            return "<barran>";
        }
        char symb = s.charAt(0);

        switch (symb) {
            case '[':
                return "\\[" + s.replace(String.valueOf(symb), "");
            case '%':
                return "\\%" + s.replace(String.valueOf(symb), "");
            case ']':
                return "\\]" + s.replace(String.valueOf(symb), "");
            case '(':
                return "\\(" + s.replace(String.valueOf(symb), "");
            case ')':
                return "\\)" + s.replace(String.valueOf(symb), "");
            case '.':
                return "\\." + s.replace(String.valueOf(symb), "");
            case '-':
                return "\\-" + s.replace(String.valueOf(symb), "");
        }

        return "";
    }

    private String convertsToStringT(LinkedList<TokenNP> sc) {
        StringBuilder sb = new StringBuilder();

        for (SuperClasse s : sc) {
            if (s instanceof TokenNP) {
                if (ListCreator.isSymbol(((TokenNP) s).getToken())) {
                    sb.append(" " + this.convertToSymbol(((TokenNP) s).getToken()));
                } else {
                    sb.append(" " + ((TokenNP) s).getToken());
                }
            }
        }

        return sb.toString();
    }

    private void getCitationWithMarks(LinkedList<TokenNP> common, LinkedList<SuperClasse> sc) {
        int i = 0;

        //this.substitute(common, sc);
        String s = this.convertsToString(sc);
        //String result[] = s.split(this.convertsToStringT(common));

        if (common.size() > 0) {
            LinkedList<TokenNP> n = this.analyzeCommonList(common);
            String a = this.convertsToStringT(common);
            String b = this.convertsToStringT(n);

            String c = this.getIntersection(a, b);

            String divisor = (this.convertsToStringT(common)).replace("\\%d", "[0-9]{0,3}");
            //system.out.println("common: " + Main.printLists(common));
            String result[] = s.split(divisor);

            //system.out.println(divisor);

            //        //system.out.println("AAAAAAAAAAAAAAAA: " + divisor);
            //        for (String sv: result){
            //            //system.out.println(sv);
            //        }

            //        //system.out.println("Saida" + this.convertsToStringT(common));

            //        //system.out.println(Main.printList(sc));
            //System.exit(0);

            //cits = null;
            //system.out.println("CRISTIANO");
            //system.out.println(Main.printLikeTreeSC(cits));

            cits.clear();
            for (String g : result) {
                try {
                    LinkedList<SuperClasse> l = ListCreator.createList(g.replace("<barran>", "") + c.replace("\\", ""));
                    if (l.size() > 0 && g.replace("<barran>", "").trim().length() > 0) {
                        cits.add(l);
                    }
                    //system.out.println("OK");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ProcessCitation.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ProcessCitation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            fixTheFirst(cits.get(0), common);
        }
    }

    private void changesToZero(LinkedList<LinkedList<TokenNP>> commons, LinkedList<Integer> li) {
        //system.out.println("Zerou");
        for (LinkedList<TokenNP> common : commons) {
            li.add(0);
        }
    }

    //---------------------------------0---------------------------------------//
    private void getCitationWithMarksPointerTemp(LinkedList<LinkedList<TokenNP>> commons, LinkedList<SuperClasse> sc, Citations c) {

        int i = 0;
        int inicio = 0, fim = 0;
        int j = 0;

        TokenNP t;
        LinkedList<LinkedList<TokenNP>> com = (LinkedList<LinkedList<TokenNP>>) commons.clone();
        LinkedList<TokenNP> common;
        LinkedList<Integer> common1n = new LinkedList();

        //system.out.println(Main.printLikeTree(com));
        this.changesToZero(com, common1n);

        while (i < sc.size()) {
            if (sc.get(i) instanceof TokenNP) {
                t = (TokenNP) sc.get(i);

                for (int k = 0; k < com.size(); k++) {
                    common = com.get(k);
                    j = common1n.get(k);

                    //system.out.println("Lista: " + k + " | Item: " + j + ":" + common.get(j).getToken() + " =??= " + t.getToken() + "<" + t.getTag() + ">: " + equality(common.get(j).getToken(), t.getToken()));

                    if (equality(common.get(j).getToken(), t.getToken())) {
                        if (j == common.size() - 1) {

                            int co = 0;

                            for (int m = common.size() - 1; m >= 0; m--) {
                                if (common.get(m).getTag().equalsIgnoreCase("in")) {
                                    co++;
                                } else {
                                    break;
                                }
                            }

                            //if (co == 0){co = -j+1;}

                            if (inicio < i - (j + co)) {
                                //system.out.println("Inseriu com inicio: " + inicio + " e fim: " + (i - j - co));
                                c.insert(inicio, i - (j + co));
                            }
                            inicio = i - co + 1;
                            //j = 0;
                            changesToZero(com, common1n);
                            //continue;
                        } else {
                            int v = ++j;
                            common1n.set(k, v);
                        }
                    } else {
                        //i -= j-1;
                        //i++;

                        common1n.set(k, 0);
                        //if (com.size() == 0) com = (LinkedList<LinkedList<TokenNP>>) commons.clone();
                        //else com.remove(k);

                    }
                }

                i++;
            } else {
                i++;
            }
        }

        if (sc.size() - 1 > inicio) {
            c.insert(inicio, sc.size() - 1);
        }
        this.fixItOneByOne(sc, c);
        ////system.out.println("II:" + i);


    }

    private void getCitationWithMarksPointer(LinkedList<TokenNP> common, LinkedList<SuperClasse> sc, Citations c) {
        if (common.size() > 0) {
            int i = 0;
            int inicio = 0, fim = 0;
            int j = 0, k = 0;
            TokenNP t;

            while (i < sc.size()) {
                t = (TokenNP) sc.get(i);

                //system.out.println(j + ":" + common.get(j).getToken() + " =??= " + t.getToken() + "<" + t.getTag() + ">: " + equality(common.get(j).getToken(), t.getToken()));
                if (equality(common.get(j).getToken(), t.getToken())) {
                    if (j == common.size() - 1) {
                        ////system.out.println("começou");
                        int co = 0;

                        for (int m = common.size() - 1; m >= 0; m--) {
                            if (common.get(m).getTag().equalsIgnoreCase("in")) {
                                co++;
                            } else {
                                break;
                            }
                        }

                        if (inicio < i - (j + co)) {
                            c.insert(inicio, i - (j + co));
                        }
                        inicio = i - co + 1;
                        j = 0;
                    } else {
                        j++;
                    }
                } else {
                    i -= j;
                    j = 0;
                }

                i++;
            }

            if (sc.size() - 1 > inicio) {
                c.insert(inicio, sc.size() - 1);
            }

            ////system.out.println("II:" + i);
        }
        //this.fixItOneByOne(sc, c, common);
    }
//-----------------------------------------------0---------------------------------------------------//

    private void cleanCitations(LinkedList<SuperClasse> sc, Citations c, int index, int begin, int end) {
        int atAuthor = 0;
        int countAuthor = 0;
        int slashAuthor = 0;
        for (int i = begin; i <= end; ++i) {
            if (i >= sc.size()) {
                break;
            }
            if (sc.get(i) instanceof TokenNP) {
                TokenNP t = (TokenNP) sc.get(i);
                if (t.getTag().equalsIgnoreCase("author") || t.getTag().equalsIgnoreCase("title")) {
                    if (t.getToken().trim().equalsIgnoreCase("/")) {
                        slashAuthor++;
                    } else {
                        countAuthor++;
                    }
                }
                if (t.getToken().trim().equalsIgnoreCase("@")) {
                    atAuthor++;
                }
            }
        }

        ////system.out.println("Slashes: " + slashAuthor);
        if (slashAuthor > 0 || atAuthor > 0) {
            c.getCitations().remove(index);
            return;
        }

        if (countAuthor <= 3) {
            c.getCitations().remove(index);
            return;
        }


        boolean author = false, title = false, year = false, other = false;
        for (int j = c.getCitations().get(index).getBegin(); j <= c.getCitations().get(index).getEnd(); ++j) {
            if (j >= sc.size()) {
                break;
            }
            SuperClasse t = sc.get(j);
            if (t instanceof TokenNP) {
                TokenNP tp = (TokenNP) t;
                ////system.out.println(tp.getTag());
                ////system.out.println(author + " " + title + " " + year + " " + other + " ");
                if (tp.getTag().equalsIgnoreCase("author") && !author) {
                    author = true;
                } else if (tp.getTag().equalsIgnoreCase("title") && !title) {
                    title = true;
                } else if (tp.getTag().equalsIgnoreCase("date") && !year) {
                    year = true;
                } else if (!tp.getTag().equalsIgnoreCase("") && !other) {
                    other = true;
                }
            }
        }

        ////system.out.println(author + " " + title + " " + year + " " + other + " ");
//        if (!(author && title && year))// && other))
//        {
//            c.getCitations().remove(index);
//            return;
//        }

    }

    private void fixItOneByOne(LinkedList<SuperClasse> sc, Citations c) {

        for (int j = 0; j < c.getCitations().size(); j++) {
            //if () return;

            DataCitation d = c.getCitations().get(j);

            ////system.out.println(c.getCitations().size() + " Inicio: " + d.getBegin() + " | Fim: " + d.getEnd());
            int tamanho = c.getCitations().size();
            this.cleanCitations(sc, c, j, d.getBegin(), d.getEnd());
            if (tamanho != c.getCitations().size()) {
                j = -1;
            }
        }

        c.fixDoubleCits();
    }

    private String getMoreOften(LinkedList<String> ls) {
        Hashtable<String, Integer> help = new Hashtable();

        for (String s : ls) {
            if (help.contains(s)) {
                Integer i = help.get(s);
                i++;
            } else {
                help.put(s, 1);
            }
        }

        Enumeration e = help.keys();

        int maior = -1;
        return "";
//        while(e.hasMoreElements()){
//             if (maior < e.nextElement())
//        }
    }

    private void getCitationWithMarksHTMLTag(String common, LinkedList<SuperClasse> sc, Citations c) {
    }

    private void getCitationWithMarksHTML(LinkedList<LinkedList<SuperClasse>> commons, LinkedList<SuperClasse> sc, Citations c) {
        int i = 0;
        int inicio = 0, fim = 0;
        int j = 0;

        TokenNP t;
        LinkedList<LinkedList<TokenNP>> com = (LinkedList<LinkedList<TokenNP>>) commons.clone();
        LinkedList<TokenNP> common;
        LinkedList<Integer> common1n = new LinkedList();

        //system.out.println(Main.printLikeTree(com));
        //system.out.println("getCitationWithMarksHTML");


        this.changesToZero(com, common1n);

        while (i < sc.size()) {
            if (sc.get(i) instanceof TokenNP) {
                t = (TokenNP) sc.get(i);

                for (int k = 0; k < com.size(); k++) {
                    common = com.get(k);
                    j = common1n.get(k);

                    //system.out.println("Lista: " + k + " | Item: " + j + ":" + common.get(j).getToken() + " =??= " + t.getToken() + "<" + t.getTag() + ">: " + equality(common.get(j).getToken(), t.getToken()));

                    if (equality(common.get(j).getToken(), t.getToken())) {
                        if (j == common.size() - 1) {

                            int co = 0;

                            for (int m = common.size() - 1; m >= 0; m--) {
                                if (common.get(m).getTag().equalsIgnoreCase("in")) {
                                    co++;
                                } else {
                                    break;
                                }
                            }

                            if (inicio < i - (j + co)) {
                                c.insert(inicio, i - (j + co));
                            }
                            inicio = i - co + 1;
                            //j = 0;
                            changesToZero(com, common1n);
                            //continue;
                        } else {
                            int v = ++j;
                            common1n.set(k, v);
                        }
                    } else {
                        //i -= j-1;
                        //i++;

                        common1n.set(k, 0);
                        //if (com.size() == 0) com = (LinkedList<LinkedList<TokenNP>>) commons.clone();
                        //else com.remove(k);

                    }
                }

                i++;
            } else {
                i++;
            }
        }

        if (sc.size() - 1 > inicio) {
            c.insert(inicio, sc.size() - 1);
        }
        this.fixItOneByOne(sc, c);

    }

    public void reVerifyTheCitations(ListCreator l, HashToken h, FormatsCalculus fc) {
        for (int j = 0; j < cits.size(); j++) {
            LinkedList<SuperClasse> k = cits.get(j);
            for (int i = 0; i < k.size(); i++) {
                SuperClasse s = k.get(i);

                if (s instanceof TokenNP) {
                    TokenNP t = (TokenNP) s;
                    //system.out.println("> " + t.getToken() + ">" + t.getTag());
                    t.setTag(l.decideProbType(h, t.getToken()));
                    //system.out.println("< " + t.getToken() + ">" + t.getTag());
                    //l.clusterFieldsTest(k);
                    l.fixList(k, fc);
                    l.refixList(k);

                }
            }
            if (!this.canBeACitation(k)) {
                cits.remove(k);
                j--;
            }
            //system.out.println("::::" + Main.printList(k));
        }
    }

    private boolean isOnlyTitle(LinkedList<SuperClasse> l) {

        TokenNP t;

        for (SuperClasse s : l) {
            if (s instanceof TokenNP) {
                t = (TokenNP) s;
                if ((t.getTag().equalsIgnoreCase("title") || t.getTag().equalsIgnoreCase("symbol") || t.getTag().equalsIgnoreCase("notype"))) {
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean containsUnaceptableSymbols(LinkedList<SuperClasse> l) {
        TokenNP t;

        for (SuperClasse s : l) {
            if (s instanceof TokenNP) {
                t = (TokenNP) s;
                if ((t.getToken().equalsIgnoreCase("="))
                        || (t.getToken().equalsIgnoreCase("["))
                        || (t.getToken().equalsIgnoreCase("]"))
                        || (t.getToken().equalsIgnoreCase("©"))
                        || (t.getToken().equalsIgnoreCase(">"))
                        || (t.getToken().equalsIgnoreCase("<"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canBeACitation(LinkedList<SuperClasse> l) {

        if (containsUnaceptableSymbols(l)) {
            return false;
        }
        if (isOnlyTitle(l)) {
            return false;
        }
        return true;
    }

    private void fixTheFirst(LinkedList<SuperClasse> f, LinkedList<TokenNP> common) {
        if (common.size() == 0) {
            return;
        }
        if (common.get(0).getToken().equals("<barran>")) {
            common.remove(0);
        }

        int count = 0;
        for (int i = 0; i < common.size(); i++) {
            if (i < common.size() && common.size() > 0 && i < f.size() && f.size() > 0
                    && equality(((TokenNP) f.get(i)).getToken(), common.get(i).getToken())) {
                count++;
            }
        }

        for (int i = 0; i < count; i++) {
            ////system.out.println("FIXTHEFIRST " + i + " - " + ((TokenNP) f.get(0)).getToken() + " - " + common.get(i).getToken());
            f.remove(0);
        }

    }
}