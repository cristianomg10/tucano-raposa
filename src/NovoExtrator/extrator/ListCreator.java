/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.extrator;

import NovoExtrator.structures.SuperClasse;
import NovoExtrator.filehandlers.Formats;
import NovoExtrator.filehandlers.FileHandler;
import NovoExtrator.filehandlers.FormatsCalculus;
import NovoExtrator.html.Element;
import NovoExtrator.html.ExtractorHTML;
import NovoExtrator.html.ExtractorHTMLList;
import NovoExtrator.structures.TagsNP;
import NovoExtrator.structures.TokenH;
import NovoExtrator.structures.TokenNP;
import NovoExtrator.structures.HashToken;
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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import NovoExtrator.structures.HashExternOperations;
import NovoExtrator.structures.Storage;

/**
 * This class creates the list, fixing it using heuristics.
 *
 * @author cristiano
 */
public class ListCreator {

    private LinkedList<SuperClasse> wl;
    private String type = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Constructor of the Class, without parameters.
     */
    public ListCreator() {
        wl = new LinkedList();
    }

    /**
     * Methods that returns a String containing the content of the list.
     *
     * @return String containing the content of the list.
     */
    public String printList() {
        StringBuilder sb = new StringBuilder();

        for (SuperClasse s : wl) {
            if (s instanceof TagsNP) {
                sb.append("--->Tags<---\n" + ((TagsNP) s).printTags() + "\n" + "--->/Tags<---\n");
            } else if (s instanceof TokenNP) {
                sb.append(((TokenNP) s).getToken() + "->" + ((TokenNP) s).getTag() + "\n");
            }
        }

        return sb.toString();
    }

    private void deduceParenthesis(LinkedList<SuperClasse> al) {
        if (al.size() == 0) {
            return;
        }
        int auxiliar = -1;
        String tipo = "";
        boolean controle = false;
        for (int i = 0; i < al.size(); ++i) {
            SuperClasse s = al.get(i);
            if (s instanceof TokenNP) {
                if (((TokenNP) s).getToken().equalsIgnoreCase("(") && !controle) {
                    auxiliar = i;
                    controle = true;
                } else if (controle && ((TokenNP) s).getToken().equalsIgnoreCase(")")) {
                    for (int j = auxiliar; j <= i; ++j) {
                        if (al.get(j) instanceof TokenNP) {
                            ((TokenNP) al.get(j)).setTag(((TokenNP) al.get(auxiliar + 1)).getTag());
                        }
                    }
                    controle = false;
                }



            }
        }
    }

    /**
     * Methods that deduces author, using heuristics.
     *
     * @param al The list containing the text.
     */
    private void deduceAuthor(LinkedList<SuperClasse> al) {
        //se o proximo for '.', e se aux tiver apenas uma letra, entao é autor
        if (al.size() == 0) {
            return;
        }
        SuperClasse auxiliar = (al != null ? al.get(0) : null);
        boolean controle = false;
        for (int i = 0; i < al.size(); ++i) {
            SuperClasse s = al.get(i);
            if (s instanceof TokenNP) {
                if (((TokenNP) s).getToken().length() == 1
                        && Character.isLetter(((TokenNP) s).getToken().charAt(0))
                        && i < al.size() - 1
                        && al.get(i + 1) instanceof TokenNP
                        && ((TokenNP) al.get(i + 1)).getToken().equals(".")) {

                    ((TokenNP) s).setTag("author");
                    ((TokenNP) al.get(i + 1)).setTag("author");
                }
            }
        }
    }

    /**
     *
     * @param wl receives the words list to fix and incorporate symbols
     */
    /* esse método nao vai ser usado, pois utiliza mascaras e ja tem metodos pra re
     * solver esse problema.
     */
    private void incorporateSymbols(LinkedList<SuperClasse> al) {
        SuperClasse auxiliar = (al != null && al.size() > 0 ? al.get(0) : null);
        if (auxiliar == null) {
            return;
        }

        boolean controle = false;

        LinkedList<TokenNP> l = new LinkedList();
        String tipo = new String();

        for (int i = 0; i < al.size(); i++) {
            SuperClasse a = al.get(i);

            if (a instanceof TokenNP) {
                TokenNP t = (TokenNP) a;

                if ((isSymbol(t.getToken()) && t.getToken().equalsIgnoreCase("-"))) {
                    int k = i - 1;
                    String sg = "";

                    while (k > 0) {
                        if (wl.get(k) instanceof TokenNP) {
                            sg = ((TokenNP) wl.get(k)).getTag();
                            break;
                        }
                        k--;
                    }

                    t.setTag(sg);
                }

                if (t.getToken().equals(".") || t.getToken().equals("!") || t.getToken().equals(":") || t.getToken().equals(",") || t.getToken().equals(";")) {
                    TokenNP k = (TokenNP) al.get(i);
                    for (int j = i - 1; j > 0; j--) {
                        if (al.get(j) instanceof TokenNP) {
                            k = (TokenNP) al.get(j);
                            break;
                        }
                    }
                    t.setTag(k.getTag());
                }

                if (t.getToken().equals("(")) {
                    TokenNP k = (TokenNP) al.get(i);
                    for (int j = i - 1; j > al.size(); j++) {
                        if (al.get(j) instanceof TokenNP) {
                            k = (TokenNP) al.get(j);
                            break;
                        }
                    }
                    t.setTag(k.getTag());
                }
            }
        }
    }

    /**
     * Verifica se a String s é um dos delimitadores que ficarão fora do bloco
     *
     * @param s
     * @return
     */
    private boolean isBlockOut(String s) {
        if (/*(FileHandler.isNumber(s))
                 || */(s.equalsIgnoreCase("["))
                || (s.equalsIgnoreCase("(")))// || (s.equalsIgnoreCase("\\n")))
        {
            return true;
        }
        return false;
    }

    /**
     * Verifica se a String s é um dos delimitadores que ficarão dentro e no fim
     * do bloco
     *
     * @param s
     * @return
     */
    private boolean isBlockInEnd(String s) {
        if ((s.equalsIgnoreCase(".")) || (s.equalsIgnoreCase(":")) || (s.equalsIgnoreCase("]"))
                || (s.equalsIgnoreCase(")")) || (s.equalsIgnoreCase(",")) || (s.equalsIgnoreCase(";"))) {
            return true;
        }
        return false;
    }

    public void clusterFieldsTest(LinkedList<SuperClasse> wl, FormatsCalculus fc) {
        LinkedList<SuperClasse> lista = new LinkedList();

        String before = "";
        int whereLeave = 0;

        //procura pela lista algum item que tenha tipo prédefinido
        int i = 0;
        int inicio = 0;
        while (i < wl.size()) {
            SuperClasse s = wl.get(i);

            whereLeave = 0;
            if (s instanceof TokenNP) {
                //se for titulo ou author ou publisher ou journal

                //cria uma lista pra carregar o possível bloco de um único tipo
                lista.clear();

                //segue à frente até achar um possivel delimitador.
                inicio = i;
                if (i == 0) {
                    inicio = 0;
                    for (int a = i; a >= 0; a--) {
                        if (wl.get(a) instanceof TokenNP) {
                            TokenNP t1 = (TokenNP) wl.get(a);

                            if (this.isBlockInEnd(t1.getToken())) {
                                inicio = a + 1;
                                break;
                            } else if (this.isBlockOut(t1.getToken())) {
                                inicio = a;
                                break;
                            }
                        }
                    }
                }

                for (int a = inicio; a < wl.size() - 1; a++) {
                    if (wl.get(a) instanceof TokenNP) {
                        TokenNP t1 = (TokenNP) wl.get(a);
                        TokenNP t2 = (TokenNP) wl.get(a + 1);

                        if (this.isBlockInEnd(t1.getToken()) && !this.isBlockInEnd(t2.getToken())) {
                            whereLeave = a + 1;
                            lista.add(t1);
                            break;
                        } else if (this.isBlockOut(t1.getToken())) {
                            whereLeave = a;// - 1;
                            break;
                        }
                        lista.add(t1);
                    }
                }

                //System.out.println("inicio: " + inicio + " WhereLeave: " + whereLeave);
//
//                ///////////////////
//                System.out.println("ANTES");
                for (SuperClasse k : lista) {
                    if (k instanceof TokenNP) {
//                        System.out.println(((TokenNP) k).getToken() + " => " + ((TokenNP) k).getTag());
                    }
                }
//                ///////////////////

                before = probGroupType(lista, fc, before);

//               System.out.println("\nDEPOIS");
                for (SuperClasse k : lista) {
                    if (k instanceof TokenNP) {
//                        System.out.println(((TokenNP) k).getToken() + " => " + ((TokenNP) k).getTag());
                    }
                }
                System.out.println();
//                System.out.println();
                /////////
            }
            if (whereLeave > i) {
                i = whereLeave;
            } else if (whereLeave <= i) {
                i++;
            }
        }
        //System.exit(0);
    }

    private void clusterFields(LinkedList<SuperClasse> wl, FormatsCalculus fc) {
        LinkedList<SuperClasse> lista = new LinkedList();
        int whereLeave = 0;
        //procura pela lista algum item que tenha tipo prédefinido
        int i = 0;
        int inicio = 0;
        String before = "";

        while (i < wl.size()) {
            SuperClasse s = wl.get(i);

            whereLeave = 0;
            if (s instanceof TokenNP) {
                //se for titulo ou author ou publisher ou journal

                //cria uma lista pra carregar o possível bloco de um único tipo
                lista.clear();

                //segue à frente até achar um possivel delimitador.
                inicio = i;
                if (i == 0) {
                    inicio = 0;
                    for (int a = i; a >= 0; a--) {
                        if (wl.get(a) instanceof TokenNP) {
                            TokenNP t1 = (TokenNP) wl.get(a);

                            if (this.isBlockInEnd(t1.getToken())) {
                                inicio = a + 1;
                                break;
                            } else if (this.isBlockOut(t1.getToken())) {
                                inicio = a;
                                break;
                            }
                        }
                    }
                }

                for (int a = inicio; a < wl.size() - 1; a++) {
                    if (wl.get(a) instanceof TokenNP && wl.get(a + 1) instanceof TokenNP) {
                        TokenNP t1 = (TokenNP) wl.get(a);
                        TokenNP t2 = (TokenNP) wl.get(a + 1);

                        if (this.isBlockInEnd(t1.getToken()) && !this.isBlockInEnd(t2.getToken())) {
                            whereLeave = a + 1;
                            lista.add(t1);
                            break;
                        } else if (this.isBlockOut(t1.getToken())) {
                            whereLeave = a;// - 1;
                            break;
                        }
                        lista.add(t1);
                    }
                }

                //System.out.println("inicio: " + inicio + " WhereLeave: " + whereLeave);
//
//                ///////////////////
//                System.out.println("ANTES");
//                for (SuperClasse k: lista){
//                    if (k instanceof TokenNP) System.out.println(((TokenNP)k).getToken() + " => " + ((TokenNP)k).getTag());
//                }
//                ///////////////////

                before = probGroupType(lista, fc, before);

//               System.out.println("\nDEPOIS");
//                for (SuperClasse k: lista){
//                    if (k instanceof TokenNP) System.out.println(((TokenNP)k).getToken() + " => " + ((TokenNP)k).getTag());
//                }
//                System.out.println();
//                System.out.println();
                /////////
            }
            if (whereLeave > i) {
                i = whereLeave;
            } else if (whereLeave <= i) {
                i++;
            }
        }
        //System.exit(0);
    }

    private void fixAuthor(LinkedList<SuperClasse> wl) {
        int j = 0;
        int inicial = 0;

        for (int i = 0; i < wl.size(); i++) {
            if (wl.get(i) instanceof TokenNP) {
                TokenNP t = (TokenNP) wl.get(i);
                if (t.getTag().equalsIgnoreCase("author") && j == 0) {
                    j = 1;
                    continue;
                } else if (t.getToken().equalsIgnoreCase(".") && j == 1) {
                    j = 2;
                    inicial = i + 1;
                    continue;
                } else if (t.getToken().equalsIgnoreCase("and") && j == 2) {
                    j = 3;
                }

                if (j == 3) {
                    int k = 0;
                    while (inicial + k < wl.size()) {
                        if (wl.get(inicial + k) instanceof TokenNP) {
                            if (!isPDelimiter(((TokenNP) wl.get(inicial + k)).getToken())) {
                                break;
                            }
                            ((TokenNP) wl.get(inicial + k)).setTag("author");
                        }
                        k++;
                    }
                }
            } else {
                j = 0;
            }
        }
    }

    /**
     * It fixes the "in:"
     *
     * @param wl
     */
    private void fixIn(LinkedList<SuperClasse> wl) {
        int j = 0;
        int inicial = 0;

        for (int i = 0; i < wl.size(); i++) {
            if (wl.get(i) instanceof TokenNP) {
                TokenNP t = (TokenNP) wl.get(i);
                if (t.getToken().equalsIgnoreCase("in") && j == 0) {
                    j = 1;
                    inicial = i;
                    continue;
                } else if (t.getToken().equalsIgnoreCase(":") && j == 1) {
                    j = 2;
                    t.setTag("journal");
                    ((TokenNP) wl.get(inicial)).setTag("journal");
                    j = 0;
                }


            } else {
                j = 0;
            }
        }
    }

    private String probGroupType(LinkedList<SuperClasse> w, FormatsCalculus fc, String before) {
        int contador = 0;
        int max = 10;
        boolean flag = false;
        TokenH[] t = new TokenH[max];

        //percorre a lista w, e verifica se nao é null

        //System.out.println(Main.printList(w) + "\n");

        for (SuperClasse s : w) {
            if (s instanceof TokenNP) {
                TokenNP aux = (TokenNP) s;
                //percorre o vetor t, pra ver se tipo já existe
                //se existe, retorna true
                for (int i = 0; i < contador; ++i) {
                    if (t[i] != null) {
                        if (t[i].getType().equalsIgnoreCase(aux.getTag())) {
                            t[i].incAttribute();
                            flag = true;
                            break;
                        }
                    }
                }

                if (!flag) {  //quer dizer que não encontrou tipo
                    if (!aux.getTag().equalsIgnoreCase("")
                            && !aux.getTag().equalsIgnoreCase("notype")
                            && !aux.getTag().equalsIgnoreCase("symbol")
                            && !aux.getTag().equalsIgnoreCase("stopword")
                            && !FileHandler.isNumber(aux.getToken())) {

                        if (contador >= max) { //se nao couber, cria outro vetor
                            TokenH[] tAux = new TokenH[max * 2];

                            System.arraycopy(t, 0, tAux, 0, t.length);

                            t = tAux;
                            tAux = null;
                            max *= 2;
                        }

                        t[contador] = new TokenH();
                        t[contador].setType(aux.getTag());
                        ++contador;
                        flag = false;
                    }
                }
            }
        }

        int maior = 0;
        String tipoFreq = "";

        /* Look for the most frequent type*/
        LinkedList<String> lstring = new LinkedList();

        for (int i = 0; i < contador; ++i) {
            if (t[i].getAttributeCount() >= maior) {
                tipoFreq = t[i].getType();
                maior = t[i].getAttributeCount();
            }
        }

        /* Count how many types has the same element's number as the most frequent type. */
        for (int i = 0; i < contador; ++i) {
            if (t[i].getAttributeCount() == maior) {
                lstring.add(type);
            }
        }

        /* if exists more than an only type, then look for the most frequent, 
         * according with the previous block */

        if (lstring.size() > 1) {
            boolean ran = false;
            for (String s : lstring) {
//                System.out.println(before);
                if (s.equalsIgnoreCase(fc.getTheMostProbable(before)) || before.equalsIgnoreCase(s)) {
                    tipoFreq = s;
                    ran = true;
                    break;
                }
            }
            if (ran == false) {
                for (SuperClasse y : w) {
                    if (y instanceof TokenNP) {
                        ((TokenNP) y).setTag(tipoFreq);
                    }
                }
                return tipoFreq;
            }
        } else {
            for (SuperClasse y : w) {
                if (y instanceof TokenNP) {
                    ((TokenNP) y).setTag(tipoFreq);
                }
            }
        }
//        
//        System.out.println(tipoFreq);
        return tipoFreq;

    }

    private void deduceNumber(LinkedList<SuperClasse> wl) {
        TokenNP t;

        for (int i = 0; i < wl.size(); i++) {
            SuperClasse s = wl.get(i);
            if (s instanceof TokenNP) {
                t = (TokenNP) s;
                t.setToken(FileHandler.removeAcentos(t.getToken()));

                /*if (t.getTag().equalsIgnoreCase("")){
                 if (FileHandler.isNumber(t.getToken())){
                 t.setTag("number");
                 }
                 */

                if (FileHandler.isNumber(t.getToken()) && t.getToken().length() == 4
                        && (t.getToken().substring(0, 2).equals("19") || t.getToken().substring(0, 2).equals("20"))
                        && (Integer.parseInt(t.getToken()) < (new Date()).getYear() + 1900)) {
                    t.setTag("year");
//                    System.out.println(t.getToken() + "<<");
                }

                //if (t.getToken().equalsIgnoreCase("-") && wl.get(i + 1) instanceof TokenNP) System.out.println(((TokenNP)wl.get(i + 1)).getToken());

                if ((isSymbol(t.getToken()) && i > 0 && i < (wl.size() - 1) && wl.get(i - 1) instanceof TokenNP && wl.get(i + 1) instanceof TokenNP
                        && FileHandler.isNumber(((TokenNP) wl.get(i - 1)).getTag()) && FileHandler.isNumber(((TokenNP) wl.get(i + 1)).getTag()) //    && !isPDelimiter(((TokenNP) wl.get(i - 1)).getToken())){ //linha nova
                        )) {
                    //System.out.println(((TokenNP) wl.get(i-1)).getToken() + ((TokenNP) wl.get(i)).getToken() + ((TokenNP) wl.get(i+1)).getToken());
                    //System.out.println(((TokenNP) wl.get(i-1)).getToken() + FileHandler.isNumber(((TokenNP) wl.get(i-1)).getTag()));
                    ((TokenNP) wl.get(i - 1)).setTag("pages");
                    ((TokenNP) wl.get(i)).setTag("pages");
                    ((TokenNP) wl.get(i + 1)).setTag("pages");
                }

                if (FileHandler.isNumber(t.getToken()) && i > 0 && (wl.get(i - 1)) instanceof TokenNP && !FileHandler.isNumber(((TokenNP) wl.get(i - 1)).getToken())
                        && !isSymbol(((TokenNP) wl.get(i - 1)).getToken()) && !((TokenNP) wl.get(i)).getTag().equalsIgnoreCase("pages") && !((TokenNP) wl.get(i - 1)).getToken().equalsIgnoreCase("<barran>")) //    && !isPDelimiter(((TokenNP) wl.get(i - 1)).getToken())){ //linha nova
                {
                    //System.out.println("Journal :@");
                    ((TokenNP) wl.get(i - 1)).setTag("journal");
                    ((TokenNP) wl.get(i)).setTag("journal");
                }

                if (i > 0 && (i < wl.size() - 1) && (wl.get(i - 1)) instanceof TokenNP && (wl.get(i + 1)) instanceof TokenNP
                        && ((TokenNP) wl.get(i - 1)).getToken().equals("(")
                        && ((TokenNP) wl.get(i + 1)).getToken().equals(")")) //    && !isPDelimiter(((TokenNP) wl.get(i - 1)).getToken())){ //linha nova
                {
                    //System.out.println("Journal :@");
                    ((TokenNP) wl.get(i + 1)).setTag(((TokenNP) wl.get(i)).getTag());
                    ((TokenNP) wl.get(i - 1)).setTag(((TokenNP) wl.get(i)).getTag());
                }
                //}
            }
        }
    }

    public void fixList(LinkedList<SuperClasse> wl, FormatsCalculus fc) {
        deduceAuthor(wl);
        deduceNumber(wl);
        //clusterFields(wl);

        //System.out.println(this.getType());
        if (this.getType().equals("txt")) {
            clusterFieldsTest(wl, fc);
        } else {
            clusterFields(wl, fc);
        }

        //deduceNumber(wl);
        //incorporateSymbols(wl);
    }

    public void refixList(LinkedList<SuperClasse> wl) {
        //deduceAuthor(wl);
        deduceNumber(wl);
        //clusterFields(wl);
        incorporateSymbols(wl);
        fixAuthor(wl);
        fixIn(wl);
        deduceParenthesis(wl);
    }

    public String[] getTextSplitedWithTag(String s) {
        int counter = 0;
        int max = 10;
        String[] text = new String[max];
        boolean inicio = false, fim = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '<' && inicio == false) {
                if (s.charAt(i) != ' ' && !inicio && s.charAt(i) != '\t' && s.charAt(i) != '­') {
                    sb.append(s.charAt(i));
                } else {
                    if (counter == max) {
                        String[] textAux = new String[max * 2];
                        System.arraycopy(text, 0, textAux, 0, text.length);

                        text = textAux;
                        textAux = null;
                        max *= 2;
                    }
                    text[counter] = sb.toString().toLowerCase();

                    counter++;
                    sb = null;
                    sb = new StringBuilder();

                }

            } else if (s.charAt(i) == '<' && inicio == false) {
                if (sb.toString().equalsIgnoreCase("")) {
                    sb.append(s.charAt(i));
                    inicio = true;
                } else {
                    if (counter == max) {
                        String[] textAux = new String[max * 2];
                        System.arraycopy(text, 0, textAux, 0, text.length);

                        text = textAux;
                        textAux = null;
                        max *= 2;
                    }
                    text[counter] = sb.toString().toLowerCase();

                    counter++;
                    sb = new StringBuilder();
                    sb.append(s.charAt(i));

                    inicio = true;
                }
            } else if (s.charAt(i) == '>' && inicio == true) {
                sb.append(s.charAt(i));
                fim = true;
            } else {
                sb.append(s.charAt(i));
            }

            if (inicio == true && fim == true) {
                if (counter == max) {
                    String[] textAux = new String[max * 2];
                    System.arraycopy(text, 0, textAux, 0, text.length);

                    text = textAux;
                    textAux = null;
                    max *= 2;
                }
                text[counter] = sb.toString().toLowerCase();
                counter++;

                sb = null;
                sb = new StringBuilder();

                inicio = false;
                fim = false;
            }
        }
        if (!sb.toString().equals("")) {
            if (counter == max) {
                String[] textAux = new String[max * 2];
                System.arraycopy(text, 0, textAux, 0, text.length);

                text = textAux;
                textAux = null;
                max *= 2;
            }
            text[counter] = sb.toString().toLowerCase();
            counter++;

        }
        return text;
    }

    public static String fixTextCodification(String str) {
        str = str.replace("&ccedil;", "ç");
        str = str.replace("&eacute;", "é");
        str = str.replace("&nbsp;", " ");

        str = str.replace("&uacute;", "ú");
        str = str.replace("&quote;", "\"");

        str = str.replace("&ntilde;", "ñ");
        str = str.replace("&quot;", "\"");
        str = str.replace("&apos;", "'");
        str = str.replace("&amp;", "&");
        str = str.replace("&lt;", "<");
        str = str.replace("&gt;", ">");
        str = str.replace("–", "-");

        str = str.replace("–", "-");

        str = str.replace("&acirc;", "â");
        str = str.replace("&aacute;", "á");
        str = str.replace("&atilde;", "ã");
        str = str.replace("&agrave;", "à");
        str = str.replace("&auml;", "ä");

        str = str.replace("&euml;", "ë");
        str = str.replace("&ecirc;", "ê");
        str = str.replace("&eacute;", "é");
        str = str.replace("&etilde;", "ẽ");
        str = str.replace("&egrave;", "è");

        str = str.replace("&iuml;", "ï");
        str = str.replace("&icirc;", "î");
        str = str.replace("&iacute;", "í");
        str = str.replace("&itilde;", "ĩ");
        str = str.replace("&igrave;", "ì");

        str = str.replace("&ouml;", "ö");
        str = str.replace("&ocirc;", "ô");
        str = str.replace("&oacute;", "ó");
        str = str.replace("&otilde;", "õ");
        str = str.replace("&ograve;", "ò");

        str = str.replace("&uuml;", "ë");
        str = str.replace("&ucirc;", "ê");
        str = str.replace("&uacute;", "é");
        str = str.replace("&utilde;", "ẽ");
        str = str.replace("&ugrave;", "è");

        str = str.replace("–", "-");


        return str;
    }

    public static String fixTextNumericCodification(String texto) {
        texto = texto.replace("&#65;", "A");
        texto = texto.replace("&#97;", "a");
        texto = texto.replace("&#192;", "À");
        texto = texto.replace("&#224;", "à");
        texto = texto.replace("&#193;", "Á");
        texto = texto.replace("&#225;", "á");
        texto = texto.replace("&#194;", "Â");
        texto = texto.replace("&#226;", "â");
        texto = texto.replace("&#195;", "Ã");
        texto = texto.replace("&#227;", "ã");
        texto = texto.replace("&#196;", "Ä");
        texto = texto.replace("&#228;", "ä");
        texto = texto.replace("&#197;", "Å");
        texto = texto.replace("&#229;", "å");
        texto = texto.replace("&#256;", "Ā");
        texto = texto.replace("&#257;", "ā");
        texto = texto.replace("&#258;", "Ă");
        texto = texto.replace("&#259;", "ă");
        texto = texto.replace("&#260;", "Ą");
        texto = texto.replace("&#261;", "ą");
        texto = texto.replace("&#478;", "Ǟ");
        texto = texto.replace("&#479;", "ǟ");
        texto = texto.replace("&#506;", "Ǻ");
        texto = texto.replace("&#507;", "ǻ");
        texto = texto.replace("&#198;", "Æ");
        texto = texto.replace("&#230;", "æ");
        texto = texto.replace("&#508;", "Ǽ");
        texto = texto.replace("&#509;", "ǽ");
        texto = texto.replace("&#66;", "B");
        texto = texto.replace("&#98;", "b");
        texto = texto.replace("&#7682;", "Ḃ");
        texto = texto.replace("&#7683;", "ḃ");
        texto = texto.replace("&#67;", "C");
        texto = texto.replace("&#99;", "c");
        texto = texto.replace("&#262;", "Ć");
        texto = texto.replace("&#263;", "ć");
        texto = texto.replace("&#199;", "Ç");
        texto = texto.replace("&#231;", "ç");
        texto = texto.replace("&#268;", "Č");
        texto = texto.replace("&#269;", "č");
        texto = texto.replace("&#264;", "Ĉ");
        texto = texto.replace("&#265;", "ĉ");
        texto = texto.replace("&#266;", "Ċ");
        texto = texto.replace("&#267;", "ċ");
        texto = texto.replace("&#68;", "D");
        texto = texto.replace("&#100;", "d");
        texto = texto.replace("&#7696;", "Ḑ");
        texto = texto.replace("&#7697;", "ḑ");
        texto = texto.replace("&#270;", "Ď");
        texto = texto.replace("&#271;", "ď");
        texto = texto.replace("&#7690;", "Ḋ");
        texto = texto.replace("&#7691;", "ḋ");
        texto = texto.replace("&#272;", "Đ");
        texto = texto.replace("&#273;", "đ");
        texto = texto.replace("&#208;", "Ð");
        texto = texto.replace("&#240;", "ð");
        texto = texto.replace("&#499;", "ǳ");
        texto = texto.replace("&#454;", "ǆ");
        texto = texto.replace("&#69;", "E");
        texto = texto.replace("&#101;", "e");
        texto = texto.replace("&#200;", "È");
        texto = texto.replace("&#232;", "è");
        texto = texto.replace("&#201;", "É");
        texto = texto.replace("&#233;", "é");
        texto = texto.replace("&#282;", "Ě");
        texto = texto.replace("&#283;", "ě");
        texto = texto.replace("&#202;", "Ê");
        texto = texto.replace("&#234;", "ê");
        texto = texto.replace("&#203;", "Ë");
        texto = texto.replace("&#235;", "ë");
        texto = texto.replace("&#274;", "Ē");
        texto = texto.replace("&#275;", "ē");
        texto = texto.replace("&#276;", "Ĕ");
        texto = texto.replace("&#277;", "ĕ");
        texto = texto.replace("&#280;", "Ę");
        texto = texto.replace("&#281;", "ę");
        texto = texto.replace("&#278;", "Ė");
        texto = texto.replace("&#279;", "ė");
        texto = texto.replace("&#439;", "Ʒ");
        texto = texto.replace("&#658;", "ʒ");
        texto = texto.replace("&#494;", "Ǯ");
        texto = texto.replace("&#495;", "ǯ");
        texto = texto.replace("&#70;", "F");
        texto = texto.replace("&#102;", "f");
        texto = texto.replace("&#7710;", "Ḟ");
        texto = texto.replace("&#7711;", "ḟ");
        texto = texto.replace("&#402;", "ƒ");
        texto = texto.replace("&#64256;", "ﬀ");
        texto = texto.replace("&#64257;", "ﬁ");
        texto = texto.replace("&#64258;", "ﬂ");
        texto = texto.replace("&#64259;", "ﬃ");
        texto = texto.replace("&#64260;", "ﬄ");
        texto = texto.replace("&#64261;", "ﬅ");
        texto = texto.replace("&#71;", "G");
        texto = texto.replace("&#103;", "g");
        texto = texto.replace("&#500;", "Ǵ");
        texto = texto.replace("&#501;", "ǵ");
        texto = texto.replace("&#290;", "Ģ");
        texto = texto.replace("&#291;", "ģ");
        texto = texto.replace("&#486;", "Ǧ");
        texto = texto.replace("&#487;", "ǧ");
        texto = texto.replace("&#284;", "Ĝ");
        texto = texto.replace("&#285;", "ĝ");
        texto = texto.replace("&#286;", "Ğ");
        texto = texto.replace("&#287;", "ğ");
        texto = texto.replace("&#288;", "Ġ");
        texto = texto.replace("&#289;", "ġ");
        texto = texto.replace("&#484;", "Ǥ");
        texto = texto.replace("&#485;", "ǥ");
        texto = texto.replace("&#72;", "H");
        texto = texto.replace("&#104;", "h");
        texto = texto.replace("&#292;", "Ĥ");
        texto = texto.replace("&#293;", "ĥ");
        texto = texto.replace("&#294;", "Ħ");
        texto = texto.replace("&#295;", "ħ");
        texto = texto.replace("&#73;", "I");
        texto = texto.replace("&#105;", "i");
        texto = texto.replace("&#204;", "Ì");
        texto = texto.replace("&#236;", "ì");
        texto = texto.replace("&#205;", "Í");
        texto = texto.replace("&#237;", "í");
        texto = texto.replace("&#206;", "Î");
        texto = texto.replace("&#238;", "î");
        texto = texto.replace("&#296;", "Ĩ");
        texto = texto.replace("&#297;", "ĩ");
        texto = texto.replace("&#207;", "Ï");
        texto = texto.replace("&#239;", "ï");
        texto = texto.replace("&#298;", "Ī");
        texto = texto.replace("&#299;", "ī");
        texto = texto.replace("&#300;", "Ĭ");
        texto = texto.replace("&#301;", "ĭ");
        texto = texto.replace("&#302;", "Į");
        texto = texto.replace("&#303;", "į");
        texto = texto.replace("&#304;", "İ");
        texto = texto.replace("&#305;", "ı");
        texto = texto.replace("&#306;", "Ĳ");
        texto = texto.replace("&#307;", "ĳ");
        texto = texto.replace("&#74;", "J");
        texto = texto.replace("&#106;", "j");
        texto = texto.replace("&#308;", "Ĵ");
        texto = texto.replace("&#309;", "ĵ");
        texto = texto.replace("&#75;", "K");
        texto = texto.replace("&#107;", "k");
        texto = texto.replace("&#7728;", "Ḱ");
        texto = texto.replace("&#7729;", "ḱ");
        texto = texto.replace("&#310;", "Ķ");
        texto = texto.replace("&#311;", "ķ");
        texto = texto.replace("&#488;", "Ǩ");
        texto = texto.replace("&#489;", "ǩ");
        texto = texto.replace("&#312;", "ĸ");
        texto = texto.replace("&#76;", "L");
        texto = texto.replace("&#108;", "l");
        texto = texto.replace("&#313;", "Ĺ");
        texto = texto.replace("&#314;", "ĺ");
        texto = texto.replace("&#315;", "Ļ");
        texto = texto.replace("&#316;", "ļ");
        texto = texto.replace("&#317;", "Ľ");
        texto = texto.replace("&#318;", "ľ");
        texto = texto.replace("&#319;", "Ŀ");
        texto = texto.replace("&#320;", "ŀ");
        texto = texto.replace("&#321;", "Ł");
        texto = texto.replace("&#322;", "ł");
        texto = texto.replace("&#457;", "ǉ");
        texto = texto.replace("&#77;", "M");
        texto = texto.replace("&#109;", "m");
        texto = texto.replace("&#7744;", "Ṁ");
        texto = texto.replace("&#7745;", "ṁ");
        texto = texto.replace("&#78;", "N");
        texto = texto.replace("&#110;", "n");
        texto = texto.replace("&#323;", "Ń");
        texto = texto.replace("&#324;", "ń");
        texto = texto.replace("&#325;", "Ņ");
        texto = texto.replace("&#326;", "ņ");
        texto = texto.replace("&#327;", "Ň");
        texto = texto.replace("&#328;", "ň");
        texto = texto.replace("&#209;", "Ñ");
        texto = texto.replace("&#241;", "ñ");
        texto = texto.replace("&#329;", "ŉ");
        texto = texto.replace("&#330;", "Ŋ");
        texto = texto.replace("&#331;", "ŋ");
        texto = texto.replace("&#460;", "ǌ");
        texto = texto.replace("&#79;", "O");
        texto = texto.replace("&#111;", "o");
        texto = texto.replace("&#210;", "Ò");
        texto = texto.replace("&#242;", "ò");
        texto = texto.replace("&#211;", "Ó");
        texto = texto.replace("&#243;", "ó");
        texto = texto.replace("&#212;", "Ô");
        texto = texto.replace("&#244;", "ô");
        texto = texto.replace("&#213;", "Õ");
        texto = texto.replace("&#245;", "õ");
        texto = texto.replace("&#214;", "Ö");
        texto = texto.replace("&#246;", "ö");
        texto = texto.replace("&#332;", "Ō");
        texto = texto.replace("&#333;", "ō");
        texto = texto.replace("&#334;", "Ŏ");
        texto = texto.replace("&#335;", "ŏ");
        texto = texto.replace("&#216;", "Ø");
        texto = texto.replace("&#248;", "ø");
        texto = texto.replace("&#336;", "Ő");
        texto = texto.replace("&#337;", "ő");
        texto = texto.replace("&#510;", "Ǿ");
        texto = texto.replace("&#511;", "ǿ");
        texto = texto.replace("&#338;", "Œ");
        texto = texto.replace("&#339;", "œ");
        texto = texto.replace("&#80;", "P");
        texto = texto.replace("&#112;", "p");
        texto = texto.replace("&#7766;", "Ṗ");
        texto = texto.replace("&#7767;", "ṗ");
        texto = texto.replace("&#81;", "Q");
        texto = texto.replace("&#113;", "q");
        texto = texto.replace("&#82;", "R");
        texto = texto.replace("&#114;", "r");
        texto = texto.replace("&#340;", "Ŕ");
        texto = texto.replace("&#341;", "ŕ");
        texto = texto.replace("&#342;", "Ŗ");
        texto = texto.replace("&#343;", "ŗ");
        texto = texto.replace("&#344;", "Ř");
        texto = texto.replace("&#345;", "ř");
        texto = texto.replace("&#636;", "ɼ");
        texto = texto.replace("&#83;", "S");
        texto = texto.replace("&#115;", "s");
        texto = texto.replace("&#346;", "Ś");
        texto = texto.replace("&#347;", "ś");
        texto = texto.replace("&#350;", "Ş");
        texto = texto.replace("&#351;", "ş");
        texto = texto.replace("&#352;", "Š");
        texto = texto.replace("&#353;", "š");
        texto = texto.replace("&#348;", "Ŝ");
        texto = texto.replace("&#349;", "ŝ");
        texto = texto.replace("&#7776;", "Ṡ");
        texto = texto.replace("&#7777;", "ṡ");
        texto = texto.replace("&#383;", "ſ");
        texto = texto.replace("&#223;", "ß");
        texto = texto.replace("&#84;", "T");
        texto = texto.replace("&#116;", "t");
        texto = texto.replace("&#354;", "Ţ");
        texto = texto.replace("&#355;", "ţ");
        texto = texto.replace("&#356;", "Ť");
        texto = texto.replace("&#357;", "ť");
        texto = texto.replace("&#7786;", "Ṫ");
        texto = texto.replace("&#7787;", "ṫ");
        texto = texto.replace("&#358;", "Ŧ");
        texto = texto.replace("&#359;", "ŧ");
        texto = texto.replace("&#222;", "Þ");
        texto = texto.replace("&#254;", "þ");
        texto = texto.replace("&#85;", "U");
        texto = texto.replace("&#117;", "u");
        texto = texto.replace("&#217;", "Ù");
        texto = texto.replace("&#249;", "ù");
        texto = texto.replace("&#218;", "Ú");
        texto = texto.replace("&#250;", "ú");
        texto = texto.replace("&#219;", "Û");
        texto = texto.replace("&#251;", "û");
        texto = texto.replace("&#360;", "Ũ");
        texto = texto.replace("&#361;", "ũ");
        texto = texto.replace("&#220;", "Ü");
        texto = texto.replace("&#252;", "ü");
        texto = texto.replace("&#366;", "Ů");
        texto = texto.replace("&#367;", "ů");
        texto = texto.replace("&#362;", "Ū");
        texto = texto.replace("&#363;", "ū");
        texto = texto.replace("&#364;", "Ŭ");
        texto = texto.replace("&#365;", "ŭ");
        texto = texto.replace("&#370;", "Ų");
        texto = texto.replace("&#371;", "ų");
        texto = texto.replace("&#368;", "Ű");
        texto = texto.replace("&#369;", "ű");
        texto = texto.replace("&#86;", "V");
        texto = texto.replace("&#118;", "v");
        texto = texto.replace("&#87;", "W");
        texto = texto.replace("&#119;", "w");
        texto = texto.replace("&#7808;", "Ẁ");
        texto = texto.replace("&#7809;", "ẁ");
        texto = texto.replace("&#7810;", "Ẃ");
        texto = texto.replace("&#7811;", "ẃ");
        texto = texto.replace("&#372;", "Ŵ");
        texto = texto.replace("&#373;", "ŵ");
        texto = texto.replace("&#7812;", "Ẅ");
        texto = texto.replace("&#7813;", "ẅ");
        texto = texto.replace("&#88;", "X");
        texto = texto.replace("&#120;", "x");
        texto = texto.replace("&#89;", "Y");
        texto = texto.replace("&#121;", "y");
        texto = texto.replace("&#7922;", "Ỳ");
        texto = texto.replace("&#7923;", "ỳ");
        texto = texto.replace("&#221;", "Ý");
        texto = texto.replace("&#253;", "ý");
        texto = texto.replace("&#374;", "Ŷ");
        texto = texto.replace("&#375;", "ŷ");
        texto = texto.replace("&#159;", "Ÿ");
        texto = texto.replace("&#255;", "ÿ");
        texto = texto.replace("&#90;", "Z");
        texto = texto.replace("&#122;", "z");
        texto = texto.replace("&#377;", "Ź");
        texto = texto.replace("&#378;", "ź");
        texto = texto.replace("&#381;", "Ž");
        texto = texto.replace("&#382;", "ž");
        texto = texto.replace("&#379;", "Ż");
        texto = texto.replace("&#380;", "ż");
        return texto;
    }

    public static String codifyTextToHtml(String str) {
        str = str.replace("ç", "&ccedil;");

        str = str.replace("á", "&aacute;");
        str = str.replace("â", "&acirc;");
        str = str.replace("à", "&agrave;");
        str = str.replace("ã", "&atilde;");
        str = str.replace("ä", "&auml;");

        str = str.replace("é", "&eacute;");
        str = str.replace("ê", "&ecirc;");
        str = str.replace("è", "&egrave;");
        str = str.replace("ẽ", "&etilde;");
        str = str.replace("ë", "&euml;");

        str = str.replace("í", "&iacute;");
        str = str.replace("î", "&icirc;");
        str = str.replace("ì", "&igrave;");
        str = str.replace("ĩ", "&itilde;");
        str = str.replace("ï", "&iuml;");

        str = str.replace("ó", "&oacute;");
        str = str.replace("ô", "&ocirc;");
        str = str.replace("ò", "&ograve;");
        str = str.replace("õ", "&otilde;");
        str = str.replace("ö", "&ouml;");

        str = str.replace("ú", "&uacute;");
        str = str.replace("û", "&ucirc;");
        str = str.replace("ù", "&ugrave;");
        str = str.replace("ũ", "&utilde;");
        str = str.replace("ü", "&uuml;");

        str = str.replace("é", "&eacute;");
        str = str.replace("ê", "&ecirc;");
        str = str.replace("ë", "&euml;");


        str = str.replace("í", "&iacute;");

        str = str.replace("ó", "&oacute;");
        str = str.replace("õ", "&otilde;");
        str = str.replace("ò", "&ograve;");

        str = str.replace("ú", "&uacute;");


        str = str.replace("ñ", "&ntilde;");

        return str;
    }

    public static boolean isYear(String c) {
        if (c.matches("[0-9]{4,4}") && !(Integer.parseInt(c) > 2015) && Integer.parseInt(c) > 1950) {
            return true;
        }
        return false;
    }

    public static boolean isTag(String c) {
        if (c.contains("<") && c.contains(">")) {
            return true;
        }
        return false;
    }

    public static boolean isSimpleSymbol(String c) {
        if (c.length() == 0) {
            return false;
        }
        switch (c.charAt(0)) {
            case '-':
            case ':':
            case '=':
            case ',':
            case '!':
            case '@':
            case '#':
            case '$':
            case '%':
            case '*':
            case '(':
            case ')':
            case '+':
            case '_':
            case '?':
            case '{':
            case '}':
            case '[':
            case ']':
            case '|':
            case '.':
                return true;
            default:
                return false;

        }
    }

    public static boolean isSymbol(String c) {
        if (c.length() == 0) {
            return false;
        }
        switch (c.charAt(0)) {
            //case '-':
            case ':':
            case '=':
            case ',':
            case '!':
            //case '@':
            case '#':
            case '$':
            case '%':
            case '*':
            case '(':
            case ')':
            case '+':
            case '_':
            case '?':
            case '{':
            case '}':
            case '[':
            case ']':
            case ';':
            case '\t':
            case '|':
            case '.':
                return true;
            default:
                return false;

        }
    }

    public static boolean isTextual(String c) {
        if (c.matches("[0-9]+")) {
            return false;
        }
        if (isSymbol(c)) {
            return false;
        }
        return true;
    }

    public static boolean isPDelimiter(String c) {
        if (c.length() == 0) {
            return false;
        }
        switch (c.charAt(0)) {
            case ':':
            case '(':
            case ')':
            case ',':
            case '.':
                return true;
            default:
                return false;

        }
    }

    public String[] separateTokens(String c) {
        c = c.replace(".", " . ");
        c = c.replace(",", " , ");
        c = c.replace("!", " ! ");
        c = c.replace(":", " : ");
        c = c.replace("-", " - ");
        c = c.replace("(", " ( ");
        c = c.replace(")", " ) ");
        c = c.replace("[", " [ ");
        c = c.replace("]", " ] ");
        c = c.replace("&", " & ");
        c = c.replace("*", " * ");
        c = c.replace("@", " @ ");

        String[] text = c.split(" ");

        for (String t : text) {
            t = t.trim();
        }

        return text;
    }

    public static String splitSymbols(String c) {
        c = c.replace(".", " . ");
        c = c.replace(",", " , ");
        c = c.replace("!", " ! ");
        c = c.replace(":", " : ");
        c = c.replace("-", " - ");
        c = c.replace("(", " ( ");
        c = c.replace(")", " ) ");
        c = c.replace("[", " [ ");
        c = c.replace("]", " ] ");
        c = c.replace("&", " & ");
        c = c.replace("*", " * ");
        c = c.replace("@", " @ ");

        return c;
    }

    public String decideProbType(HashToken ht, String t) {

        String nt = FileHandler.removeAcentos(t.toString().trim().toLowerCase());

        if (ht.exists(nt)) {
            if (!FileHandler.isNumber(nt)) {
                String probType = new String();
                LinkedList<TokenH> l = ht.get(nt);
                int maior = 0;

                for (TokenH s : l) {
                    if (s.getAttributeCount() > maior) {
                        probType = s.getType();
                        maior = s.getAttributeCount();
                    }
                }
                //System.out.println(">" + nt + "<" + probType);
                return probType;

            }
        } else if (nt.length() == 4 && (nt.substring(0, 2).equals("19") || nt.substring(0, 2).equals("20"))) {
            return "date"; //("year");
        } else {
            if (FileHandler.isNumber(nt.trim()) && !nt.equals("&")) {
                return ("number");
            } else if (nt.length() == 1 && ListCreator.isSymbol(nt.toString())) {
                return ("symbol");
            } else {
                return ("notype");
            }
        }
        return "notype";
    }

    public String[] constructWords(String str) {
        StringBuilder s = new StringBuilder();
        int count = 0;
        int max = 10;
        String[] ans = new String[max];

        for (int j = 0; j < str.length(); ++j) {
            //Separa as palavras
            if (Character.isLetterOrDigit(str.charAt(j))) {
                //if the character is letter or digit, this is inserted on StringBuilder s    
                s.append(str.charAt(j));
            } else {
                //if the character is a symbol, the flow runs here.
                if (!s.toString().equals("")) {
                    if (count >= max - 1) {
                        String[] aux = new String[max * 2];
                        System.arraycopy(ans, 0, aux, 0, ans.length);

                        ans = aux;
                        max *= 2;
                    }
                    ans[count] = s.toString();
                    count++;
                    s = new StringBuilder();
                    s.append(str.charAt(j));

                    if (!Character.isSpace(str.charAt(0))) {
                        ans[count] = s.toString();
                        count++;
                        s = new StringBuilder();
                    }
                }
            }
        }
        return ans;
    }

    public void createList(HashToken ht, HashToken hsw, HashToken hci, String fileName, FormatsCalculus fc) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        //BufferedReader in = new BufferedReader (new InputStreamReader(is, cs));
        InputStreamReader isr = new InputStreamReader(is, cs);

//        if (!isr.getEncoding().equalsIgnoreCase("UTF8")){
//            String encoding = isr.getEncoding();
//            isr.close();
//            cs = Charset.forName(encoding);
//            isr = new InputStreamReader(is, cs);
//        }
//
//        System.out.println(isr.getEncoding());

        BufferedReader in = new BufferedReader(isr);

        wl.clear();
        BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("saida.txt"), "UTF-8"));

        // while the file was not completely read.
        while (in.ready()) {
            String str = fixTextCodification(FileHandler.removeAcentos(fixTextCodification(in.readLine())));//fixTextCodification(in.readLine());
            fw.write(str);

            if (str.matches(".*?<[^>]*>.*?") && getType().equals("")) {
                this.setType("html");
            } else if (!str.matches(".*?<[^>]*>.*?") && getType().equals("")) {
                this.setType("txt");
            }

            //System.out.println(getType());

            String[] cTags = getTextSplitedWithTag(str);
            boolean ref = false;

            // run thru the vector
            for (int i = 0; i < cTags.length; i++) {
                if (cTags[i] != null && !cTags[i].equals("")) {
                    if (isTag(cTags[i])) {
                        //if the last is tag, the tags is insert together this one. Else, create a new element.
                        if (!(wl.peekLast() instanceof TagsNP)) {
                            TagsNP t = new TagsNP();
                            t.insert(cTags[i]);
                            wl.add(t);
                        } else if (wl.size() > 0 && (wl.peekLast() instanceof TagsNP)) {
                            TagsNP t = (TagsNP) wl.peekLast();
                            t.insert(cTags[i]);
                        }
                    } else {
                        str = cTags[i];
                        String[] tokens = this.separateTokens(str);

                        for (String t : tokens) {

                            if (t != null && !t.equals("")) {

                                //System.out.println("Token " + t + " Prob Type:" + this.decideProbType(ht, t));
                                //System.out.println("t:" + t);
                                if (!hci.exists(t)) {
                                    TokenNP p = new TokenNP(t, this.decideProbType(ht, t));
                                    wl.add(p);
                                } else {
                                    //System.out.println(t + "->location");
                                    TokenNP p = new TokenNP(t, "location");
                                    wl.add(p);
                                }

                            }
                        }
                    }
                }
            }
            if (getType().equals("txt")) {
                wl.add(new TokenNP("<barran>", "symbol"));
            }
        }

        fw.close();



        this.fixList(wl, fc);


    }

    /**
     * Verifying if some URL exists inner to str.
     *
     * @param str to be verified.
     * @return the position for the url's start. -1 if it's not found.
     */
    public static int detectURL(String str) {
        boolean s = str.matches(
                "^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\+&%\\$#\\=~])*$");

        int value = str.indexOf("http");
        if (s) {
            if (value == -1) {
                value = str.indexOf("ftp");
            }
        }

        return value;
    }

    public void createBlockList(HashToken ht, HashToken hsw, HashToken hci, HashToken hk, String fileName, FormatsCalculus fc) throws FileNotFoundException, IOException {

        /* Pre-process to open file and load into structure */
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(is, cs);
        BufferedReader in = new BufferedReader(isr);
        wl.clear();

        /* Open file to write */
        BufferedWriter fw = new BufferedWriter(
                new OutputStreamWriter(
                new FileOutputStream("saida.txt"), "UTF-8"));

        String before = "";
        boolean ref = false;

        /* While the file was not completely read */
        while (in.ready()) {
            String str = in.readLine().toLowerCase().replaceAll("<.*?>", "");

            /* If wants to catch from "references" */
//                if (str.trim().contains("references") && str.trim().length() <= 15){
//                    ref = true;
//                    continue;
//                }
//                
//                if (!ref) continue;

            /* Print line */
            //System.out.println();
            //System.out.println(str);
            //System.out.println();

            /* Find out if exists some URL inner to String str */
            int x = detectURL(str);
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
                String st = sb.toString().replaceAll("<.*?>", "");

                String[] k = ((FileHandler.justIsolatePunctuationCharacters(
                        fixTextCodification(
                        FileHandler.removeAcentos(
                        st//sb.toString()
                        )))).split(" "));

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
                    fixTextCodification(
                    FileHandler.removeAcentos(
                    fixTextCodification(
                    str))));

            fw.write(str);
            this.setType("txt");

            /* Split the string using spaces */
            String[] cTags = str.split("[\\s\t]");

            for (String o : cTags) {
                if (o.matches("([a-z][a-z]+)(\\d{2,4})")) {
                    //System.out.println(o);
                    String troca = o.replaceAll("([a-z][a-z]+)(\\d{2,4})", "$1 $2");
                    //System.out.println(troca);
                    str = str.replace(o, troca);
                }
            }

            cTags = str.split("[\\s\t]");


            /* Initialize helper lists */
            LinkedList<String> auxToken = new LinkedList();
            LinkedList<String> auxValue = new LinkedList();

            /* Runs through the array cTags */
            for (int i = 0; i < cTags.length; i++) {
                //System.out.println(cTags[i]);
                if (!isSymbol(cTags[i]) && cTags[i].length() > 0) {
                    /* If cTags[i] is not symbol */
                    if (v.size() > 0 && v.get(0).equalsIgnoreCase(cTags[i])) {
                        /* Insert URL into the list */
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
                    /* If cTags[i] is symbol */
                } else if ((auxValue.size() > 0) && isSymbol(cTags[i])) {
                    /* Just print */
                    /*for (int j = 0; j < auxToken.size(); j++){
//                     System.out.print(auxToken.get(j) + " ");
                     } */

                    /* Calculate block's probability */
//                        before = calculateBlockClass(ht, hsw, hci,
//                                auxToken, auxValue, fc, before, 0.5);
                        /* Verify if exists a keyword */
                    boolean exist = false;
                    String value = "";

                    for (String a : auxToken) {
                        if (hk.exists(a)) {
                            exist = true;
                            value = hk.get(a).get(0).getType();
                        }
                    }


                    if (exist) {
                        auxValue.set(0, value);
                    } else {
                        before = calculateBlockClass(ht, hsw, hci,
                                auxToken, auxValue, fc, cTags[i], 0.5);
                    }

                    /* Just print */
//                    if ( auxValue.size() > 0) System.out.println("<" + auxValue.get(0) + ">");

                    /* Insert all the block as a token */
                    StringBuilder nsb = new StringBuilder();
                    for (int j = 0; j < auxToken.size(); j++) {
                        nsb.append(auxToken.get(j)).append(" ");
                    }

                    wl.add(new TokenNP(nsb.toString(), auxValue.get(0)));

                    /* Insert the last symbol */
                    if (!cTags[i].equalsIgnoreCase("")) {
                        wl.add(new TokenNP(cTags[i], "symbol"));
                    }

                    /* Clear helper lists */
                    auxToken.clear();
                    auxValue.clear();
                } else if (!cTags[i].equalsIgnoreCase("")) {
                    wl.add(new TokenNP(cTags[i], "symbol"));
                }
            }

            /* Running here only at the end of the line. */
            if (auxToken.size() > 0) {
                /*for (int j = 0; j < auxToken.size(); j++){
//                 System.out.print(auxToken.get(j) + " ");
                 }*/

                boolean exist = false;
                String value = "";

                /* Verify if exists a keyword */
                for (String a : auxToken) {
                    if (hk.exists(a)) {
                        exist = true;
                        value = hk.get(a).get(0).getType();
                    }
                }

                if (exist) {
                    auxValue.set(0, value);
                } else {
                    before = calculateBlockClass(ht, hsw, hci,
                            auxToken, auxValue, fc, before, 0.5);
                }

                /* Insert all the block as a token */
                StringBuilder nsb = new StringBuilder();
                for (int j = 0; j < auxToken.size(); j++) {
                    nsb.append(auxToken.get(j)).append(" ");
                }

                wl.add(new TokenNP(nsb.toString(), auxValue.get(0)));

                //before = calculateBlockClass(ht, hsw, hci, 
                //        auxToken, auxValue, fc, before);
//                if ( auxValue.size() > 0) System.out.println("<" + auxValue.get(0) + ">");

            }
            wl.add(new TokenNP("<barran>", "symbol"));

        }

        fw.close();
        //this.preProcess(wl);
        //this.fixList(wl, fc);
        //System.exit(0);

    }

    private static boolean flexEqualType(String str1, String str2) {
//        if (str1.equalsIgnoreCase(str2)) return true;
//        if (str1.equalsIgnoreCase("booktitle") && str2.equalsIgnoreCase("title")) return true; 
//        if (str1.equalsIgnoreCase("title") && str2.equalsIgnoreCase("booktitle")) return true;  
//        if (str1.equalsIgnoreCase("journal") && str2.equalsIgnoreCase("title")) return true; 
//        if (str1.equalsIgnoreCase("title") && str2.equalsIgnoreCase("journal")) return true;         
//        if (str1.equalsIgnoreCase("booktitle") && str2.equalsIgnoreCase("journal")) return true;
//        if (str1.equalsIgnoreCase("journal") && str2.equalsIgnoreCase("booktitle")) return true;
//        if (str1.equalsIgnoreCase("institution") && str2.equalsIgnoreCase("publisher")) return true;
//        if (str1.equalsIgnoreCase("publisher") && str2.equalsIgnoreCase("institution")) return true;        
//        return false;

        String[] array = {"title", "booktitle", "journal", "pages", "note", "location", "publisher", "institution", "date"};
        LinkedList<String> lista = new LinkedList();

        lista.addAll(Arrays.asList(array));
        //if (str1.equalsIgnoreCase(str2) && str2.equalsIgnoreCase("date")) return false;
        if (!str1.equalsIgnoreCase("") && !str2.equalsIgnoreCase("")) {

            //System.out.println(str1.trim().charAt(str1.trim().length()-1));
            if (str1.equalsIgnoreCase("author") | str2.equalsIgnoreCase("author")) {
                return false;
            }
            if (str1.equalsIgnoreCase(str2)) {
                return true;
            }
            if (lista.contains(str2) && lista.contains(str1)) {
                return true;
            }

        } else {
            return false;
        }
        return false;
    }

    private static boolean equalType(String str1, String str2) {
        if (str1.equalsIgnoreCase(str2)) {
            return true;
        }
        if (str1.equalsIgnoreCase("booktitle") && str2.equalsIgnoreCase("journal")) {
            return true;
        }
        if (str1.equalsIgnoreCase("journal") && str2.equalsIgnoreCase("booktitle")) {
            return true;
        }
        if (str1.equalsIgnoreCase("institution") && str2.equalsIgnoreCase("publisher")) {
            return true;
        }
        if (str1.equalsIgnoreCase("publisher") && str2.equalsIgnoreCase("institution")) {
            return true;
        }
        return false;
    }

    private LinkedList<String> cleanDouble(LinkedList<String> st) {

        LinkedList<String> s = (LinkedList<String>) st.clone();

        label:
        {
            int t = s.size();
            for (int i = 0; i < s.size() - 1; i++) {
                for (int j = i + 1; j < s.size(); j++) {

                    //System.out.println("Comparando " + s.get(j) + "\n\t com " + s.get(i) + "\n\t resultado: " + s.get(j).indexOf(s.get(i)));

                    if (s.get(j).indexOf(s.get(i)) != -1) {
                        s.remove(i);
                        i = -1;
                        break;
                    } else if (s.get(i).indexOf(s.get(j)) != -1) {
                        s.remove(j);
                        i = -1;
                        break;
                    }
                }
            }
        }

        return s;
    }

    public LinkedList<String> getCitations(Formats f, LinkedList<SuperClasse> wl) {
        LinkedList<LinkedList<String>> formatos = f.getFormats();
        LinkedList<Integer> n = new LinkedList();           //posicao
        LinkedList<Integer> indicador = new LinkedList();   //indicador do começo da citação atual
        LinkedList<String> anteriores = new LinkedList();

        LinkedList<String> resultados = new LinkedList();


        for (int i = 0; i < formatos.size(); i++) {
            n.add(0);
            indicador.add(-1);
            anteriores.add("");
        }

        //runs through list
        for (int i = 0; i < wl.size(); i++) {

            //if it's a symbol
            if (wl.get(i) instanceof TokenNP && (((TokenNP) wl.get(i)).getTag().equals("symbol"))) {
                continue;
            }

            //runs formats list
            for (int j = 0; j < formatos.size(); j++) {
                TokenNP t = ((TokenNP) wl.get(i));

                String tipoAnterior = anteriores.get(j);
                String tipoAtual = formatos.get(j).get(n.get(j));

                //System.out.println(t.getToken() + ">" + t.getTag() + " = " + 
                //        tipoAtual + " " + tipoAnterior + " " +
                //        equalType(t.getTag(), tipoAnterior));

                if (equalType(t.getTag(), tipoAnterior)) {
                    continue;
                }

                if (equalType(t.getTag(), tipoAtual)) {

                    if (indicador.get(j) == -1) {
                        indicador.set(j, i);
                    }

                    n.set(j, n.get(j) + 1);

                    if (n.get(j) == formatos.get(j).size()) {

                        StringBuilder sb = new StringBuilder();

                        int m = 0;
                        TokenNP tn;// = formatos.get(j).getLast();

                        for (m = i; m < wl.size(); m++) {
                            tn = (TokenNP) wl.get(m);
//                            System.out.println("Comparação: " + tn.getTag() + " " + tipoAtual);
                            if (equalType(tn.getTag(), "symbol")) {
                                continue;
                            }
                            if (!equalType(tn.getTag(), tipoAtual)) {
                                //i = m - 1;
                                break;
                            }
                        }

                        for (int k = indicador.get(j); k < m; k++) {
                            String b = ((TokenNP) wl.get(k)).getToken();
                            if (!b.equalsIgnoreCase("<barran>")) {
                                sb.append(b).append(" ");
                            }
                        }

                        resultados.add(sb.toString());

//                        System.out.println(indicador.get(j) + " Fechou! \\o/" + i);


                        sb = new StringBuilder();
                        n.set(j, 0);
                        indicador.set(j, -1);


                    }
                    anteriores.set(j, tipoAtual);
                } else {
                    n.set(j, 0);
                    indicador.set(j, -1);
                    anteriores.set(j, "");
                }
            }
        }

        resultados = this.cleanDouble(resultados);
        return resultados;
    }

    /**
     * Just verify if contains minimum fields
     *
     * @param s
     * @return
     */
    private boolean containsOptionalFields(LinkedList<String> s) {
        //String  [] optional   = {"journal", "booktitle", "publisher", "note", 
        //                        "pages", "tech", "editor", "volume", "number"};
        //Integer [] optionalI  = {        0,           0,           0,      0,
        //                              0,      0,        0,        0,        0};
        String[] optional = {"journal", "booktitle", "publisher", "pages",
            "institution", "note", "location", "title"};
        Integer[] optionalI = {0, 0, 0, 0,
            0, 0, 0, 0};
        for (String si : s) {
            for (int i = 0; i < optional.length; i++) {
                String v = optional[i];

                if (si.equalsIgnoreCase(v)) {
                    optionalI[i]++;
                }
            }
        }

        int counter = 0;
        for (Integer j : optionalI) {
            if (j > 0) {
                counter++;
            }
        }

        if (counter > 0) {
            return true;
        }
        return false;
    }

    private boolean containsOptionalFieldsFlex(LinkedList<String> s) {
        //String  [] optional   = {"journal", "booktitle", "publisher", "note", 
        //                        "pages", "tech", "editor", "volume", "number"};
        //Integer [] optionalI  = {        0,           0,           0,      0,
        //                              0,      0,        0,        0,        0};
        String[] optional = {"journal", "booktitle", "publisher",
            "title"};
        Integer[] optionalI = {0, 0, 0,
            0};
        for (String si : s) {
            for (int i = 0; i < optional.length; i++) {
                String v = optional[i];

                if (si.equalsIgnoreCase(v)) {
                    optionalI[i]++;
                }
            }
        }

        int counter = 0;
        for (Integer j : optionalI) {
            if (j > 0) {
                counter++;
            }
        }

        if (counter > 0) {
            return true;
        }
        return false;
    }

    /**
     * Just verify if contains minimum fields
     *
     * @param s
     * @return
     */
    private boolean containsObligatoryFields(LinkedList<String> s) {
        String[] obligatory = {"author", "date"/*, "title"*/};
        Integer[] obligatoryI = {0, 0/*,       0*/};

        for (String si : s) {
            for (int i = 0; i < obligatory.length; i++) {
                String v = obligatory[i];
                //System.out.println("V -->" + v);
                //System.out.println(v.matches("([\\w])([0-9]+)"));
                if (v.matches("([\\w])([0-9]+)")) {
                    obligatoryI[1]++;
                    continue;
                }

                if (si.equalsIgnoreCase(v)) {
                    obligatoryI[i]++;
                }
            }
        }

        int counter = 0;
        for (Integer j : obligatoryI) {
            if (j > 0) {
                counter++;
            }

        }

        if ((counter == obligatoryI.length /*|| counter == obligatoryI.length - 1*/)
                && obligatoryI[0] > 0) {
            return true;
        }

        return false;
    }

    private boolean containsObligatoryFields(LinkedList<String> s, LinkedList<String> f) {
        String[] obligatory = {"author", "date"/*, "title"*/};
        Integer[] obligatoryI = {0, 0/*,       0*/};

        for (int i = 0; i < s.size(); ++i) {
            String si = s.get(i);
            String vi = f.get(i);
            for (int j = 0; j < obligatory.length; j++) {
                String v = obligatory[j];
                //System.out.println("Vi -->" + vi);
                //System.out.println(vi.matches(".*?(\\w)([0-9]+).*?"));
                if (vi.matches(".*?(\\w)([0-9]+).*?")) {
                    obligatoryI[1]++;
                    continue;
                }

                if (si.equalsIgnoreCase(v)) {
                    obligatoryI[j]++;
                }
            }
        }

        int counter = 0;
        for (Integer j : obligatoryI) {
            if (j > 0) {
                counter++;
            }

        }

        if ((counter == obligatoryI.length /*|| counter == obligatoryI.length - 1*/)
                && obligatoryI[0] > 0) {
            return true;
        }

        return false;
    }

    public boolean validWords(LinkedList<String> s) {
        int countNum = 0;
        int advisor = 0;

        for (String v : s) {
            if (v.contains("advisor")) {
                advisor++;
            }
            if (FileHandler.isNumber(v)) {
                countNum++;
            }

            //if (v.contains("phd")) return false;
            //if (v.contains("degree")) return false;
        }

        //if (countNum >= s.size() * 0.2) return false;
        if (/*countNum == 0 && */advisor == 0) {
            return true;
        }
        return false;
    }

    private boolean containsObligatoryFieldsFlex(LinkedList<String> s) {
        String[] obligatory = {"author"/*,"date", "pages" , "title" */};
        Integer[] obligatoryI = {0/*,  0  , 0,    0 */};

        for (String si : s) {
            for (int i = 0; i < obligatory.length; i++) {
                String v = obligatory[i];

                if (si.equalsIgnoreCase(v)) {
                    obligatoryI[i]++;
                }
            }
        }

        int counter = 0;
        for (Integer j : obligatoryI) {
            if (j > 0) {
                counter++;
            }

        }

        if (counter == obligatoryI.length) {
            return true;
        }

        return false;
    }

    /**
     * Just verify if contains minimum fields for starting a new Citation.
     *
     * @param s
     * @return
     */
    private boolean minimumAtFirstLine(LinkedList<String> s) {
        String[] minimum = {"author", "editor"};
        Integer[] minimumI = {0, 0};

        for (String si : s) {
            for (int i = 0; i < minimum.length; i++) {
                String v = minimum[i];

                if (ListCreator.equalType(si, v)) {
                    minimumI[i]++;
                }
            }
        }

        int counter = 0;
        for (Integer j : minimumI) {
            if (j > 0) {
                counter++;
            }
        }

        if (counter == minimumI.length || counter == minimumI.length - 1) {
            return true;
        }
        return false;
    }

    /**
     * Just verify if the track can be a citation.
     *
     * @return true if yes, false if not.
     */
    public boolean verifyCitation(LinkedList<String> s, LinkedList<String> f) {

        //System.out.println(s + " \n Obrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        if (containsObligatoryFields(s, f) && containsOptionalFields(s) && verifyCitationSize(f)) {
            return true;
        }

        //System.out.println("\nObrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        return false;
    }

    /**
     * Just verify if the track can be a citation.
     *
     * @return true if yes, false if not.
     */
    public boolean verifyCitation(LinkedList<String> s) {

        //System.out.println(s + " \n Obrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        if (containsObligatoryFields(s) && containsOptionalFields(s)) {
            return true;
        }

        //System.out.println("\nObrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        return false;
    }

    public boolean verifyCitationSize(LinkedList<String> fields) {
        int soma = 0;
        for (String s : fields) {
            soma += s.length();
        }
        //System.out.println(soma);
        //System.out.println(fields.size());
        //System.out.println(">" + soma/fields.size());
        //System.out.println((soma/fields.size()) >= 10.0);

        if ((soma / fields.size()) >= 10.0) {
            return true;
        }
        return false;
    }

    public boolean containsDoubleDate(LinkedList<String> f) {
        StringBuilder s = new StringBuilder();
        for (String v : f) {
            s.append(v).append(" ");
        }

        String[] splitted = s.toString().split("[ \t\n]");
        HashMap<String, Integer> anos = new HashMap();
        String anterior = "";

        for (String v : splitted) {
            if (ListCreator.isYear(anterior) && v.equalsIgnoreCase("-")) {
                anos.put(anterior, anos.get(anterior) - 1);
                if (anos.get(anterior) == 0) {
                    anos.remove(anterior);
                }
            }

            if (ListCreator.isYear(v) && !anterior.equalsIgnoreCase("-")) {
                if (anos.containsKey(v)) {
                    anos.put(v, anos.get(v) + 1);
                } else {
                    anos.put(v, 1);
                }
            }
            anterior = v;
        }

//        System.out.println(anos.keySet() + " " + anos.size());
        if (anos.size() > 1) {
            return true;
        }
        return false;
    }

    public boolean containsManyParenthesis(LinkedList<String> f) {
        StringBuilder s = new StringBuilder();
        for (String v : f) {
            s.append(v).append(" ");
        }

        String[] splitted = s.toString().split("[ \t\n]");
        int open = 0;
        int close = 0;

        for (String v : splitted) {
            if (v.equalsIgnoreCase("(")) {
                open++;
            } else if (v.equalsIgnoreCase(")")) {
                close++;
            }
        }

        if (open != close) {
            return true;
        }
        if (open >= 3 || close >= 3) {
            return true;
        }
        return false;
    }

    public boolean verifyHeuristics(LinkedList<String> f) {
        //if (this.containsDoubleDate(f)) return false;
        //if (this.containsManyParenthesis(f)) return false;
        return true;
    }

    /**
     * Just verify if the track can be a citation.
     *
     * @return true if yes, false if not.
     */
    public boolean verifyCitationFlex(LinkedList<String> s, LinkedList<String> f) {
        //System.out.println(s + "->" + s.size());
        //System.out.println(s + " \n Obrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        if (containsObligatoryFieldsFlex(s) && verifyHeuristics(f) /*&& verifyCitationSize(f) */ && this.containsOptionalFieldsFlex(s) && s.size() >= 2 /* && s.size() < 40*/) {
            return true;
        }

        //System.out.println("\nObrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        return false;
    }

    /**
     * Just verify if the track can be a citation.
     *
     * @return true if yes, false if not.
     */
    public boolean verifyCitationFlex(LinkedList<String> s) {
        //System.out.println(s + "->" + s.size());
        //System.out.println(s + " \n Obrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        if (containsObligatoryFieldsFlex(s) /* */ && verifyHeuristics(s) && this.containsOptionalFieldsFlex(s) && s.size() >= 2 /* && s.size() < 40*/) {
            return true;
        }

        //System.out.println("\nObrigatorio: " + containsObligatoryFields(s) 
        //       + "\n Opcional: " + containsOptionalFields(s));
        return false;
    }

    private String beginning(LinkedList<LinkedList<String>> s) {
        LinkedList<LinkedList<String>> local = (LinkedList<LinkedList<String>>) s.clone();
        LinkedList<String> answer = new LinkedList();


        for (int i = 0; i < local.size() - 1; ++i) {
            for (int j = 0; j < local.size(); ++j) {
                if (local.get(i).get(j).equalsIgnoreCase("symbol")) {
                    local.remove(i);
                }
            }
        }

        int notEqual = 0;
        for (int i = 0; i < local.size() - 1; ++i) {
            for (int j = i + 1; j < local.size(); ++j) {
                if (!local.get(i).get(0).equalsIgnoreCase(local.get(j).get(0))) {
                    notEqual++;
                }
            }
        }

        if (notEqual == 0) {
            return local.get(0).get(0);
        }

        return "";
    }

    public int getDistance(String controle, LinkedList<String> temp) {
        String s = null;
        int distance = 0;

        for (int i = temp.size() - 1; i >= 0; i--) {
            s = temp.get(i);
            if (s.equalsIgnoreCase(controle)) {
                break;
            }
            distance++;
        }

        if (distance == 0) {
            return -1;
        }
        return distance;
    }

    public boolean alreadyContainsDateAndFitOneMore(LinkedList<String> temp,
            LinkedList<String> tokens, String next, String nextT) {
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).equalsIgnoreCase("date") && next.equalsIgnoreCase("date")) {
                if (FileHandler.isNumber(tokens.get(i)) && FileHandler.isNumber(nextT)) {
                    return false;
                } else if (FileHandler.isNumber(tokens.get(i)) && !FileHandler.isNumber(nextT)) {
                    return true;
                } else if (!FileHandler.isNumber(tokens.get(i)) && FileHandler.isNumber(nextT)) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Verifica se contém os tipos necessários pra ser considerado mínimo.
     *
     * @param types lista de tipos
     * @return
     */
    public boolean containsMininum(LinkedList<String> types) {
        int author = 0;
        int title = 0;

        for (String s : types) {
            switch (s) {
                case "author":
                    author++;
                    break;
                case "title":
                    title++;
                    break;
            }
        }

        if (author > 0 || title > 0) {
            return true;
        }
        return false;
    }

    public boolean verifyIfCanBeContinued(int i, LinkedList<SuperClasse> wl, LinkedList<String> temp, LinkedList<String> tokens, HashToken hsw) {

        String control = "", lastOne = "", last = "";
        String n1 = "", n2 = "";
        boolean heuristicsNumbers = false;
        boolean sigla = false;
        int aux = 0;

        for (int k = i + 1; k < wl.size(); k++) {
            if (!((TokenNP) wl.get(k)).getTag().equalsIgnoreCase("symbol")) {
                control = ((TokenNP) wl.get(k)).getTag();
                break;
            }
        }

        for (int k = temp.size() - 1; k > 0; --k) {
            if (!temp.get(k).equalsIgnoreCase("symbol")) {
                lastOne = (temp.get(k));
                aux = k;
                break;
            }
        }

        last = tokens.get(tokens.size() - 1);
        int tamanho = tokens.size() - 1;


        if (tamanho > 3 && ListCreator.isSymbol(tokens.get(tamanho))
                && ListCreator.isSymbol(tokens.get(tamanho - 2))
                && ((!FileHandler.isNumber(tokens.get(tamanho - 1))
                && tokens.get(tamanho - 1).length() == 1)
                || tokens.get(tamanho - 1).equalsIgnoreCase("eds"))) {
            sigla = true;
        }

        /*System.out.println(tokens.get(tamanho) + " "
         + tokens.get(tamanho - 2) + " "
         + tokens.get(tamanho-1) + " "
         + sigla);*/

        //System.out.println("Control: " + control + " | LastOne: " + lastOne);

        /////Ver aqui!!!!
        for (int k = i + 1; k < wl.size(); k++) {
            if (!((TokenNP) wl.get(k)).getToken().equalsIgnoreCase("<barran>")) {
                n1 = ((TokenNP) wl.get(k)).getToken();
                break;
            }
        }

        for (int k = tokens.size() - 1; k > 0; --k) {
            if (!tokens.get(k).equalsIgnoreCase("<barran>")) {
                n2 = (tokens.get(k));
                String[] vet_tok = n2.split(" ");
                n2 = vet_tok[vet_tok.length - 1];
                break;
            }
        }

        if (FileHandler.isNumber(n1)
                && FileHandler.isNumber(n2)) {
            heuristicsNumbers = true;
        }
        //System.out.println(n1 + "<>" + n2 );



        String[] token = last.split("\\s");
        String lastToken = token[token.length - 1];
        //System.out.println(">" + lastToken + " = " + hsw.exists(lastToken));
        if (hsw.exists(lastToken)) {
            return true;
        }
        if (n1.trim().equals(":") || n1.trim().equals("-")) {
            return true;
        }
        if (heuristicsNumbers) {
            return false;
        }
        if (tokens.get(aux).trim().length() == 0) {
            return false;
        }
        if (last.equalsIgnoreCase(",") || lastToken.equalsIgnoreCase("&")) {
            return true;
        }
        if (FileHandler.isDash(tokens.get(aux).trim().charAt(
                tokens.get(aux).trim().length() - 1))) {
            return true;
        }

        //before
        //new V
        //if (hsw.exists(n1)) return true;


//        if (this.alreadyContainsDateAndFitOneMore(temp, tokens, lastOne, last))    {
//            return false;
//        }  
        if (ListCreator.flexEqualType(lastOne, control)) {
            return true;
        }

        /*if (getDistance(control, tokens) > 0){
         return false;
         } */

        if (sigla == true) {
            return true;
        }
        return false;
    }

    public void preProcess(LinkedList<SuperClasse> wl) {
        String previousType = "";
        Integer previousPos = -1;

        restart:
        for (int i = 0; i < wl.size() - 1; i++) {
            TokenNP t = (TokenNP) wl.get(i);

            if (t.getTag().equalsIgnoreCase("symbol")) {
                continue;
            }
//            System.out.println(t.getTag() + " ? " + previousType);
            if (!previousType.equalsIgnoreCase("") && equalType(t.getTag(), previousType)) {
                StringBuilder sb = new StringBuilder();
                for (int k = previousPos + 1; k <= i; k++) {
                    sb.append(((TokenNP) wl.get(k)).getToken()).append(" ");
                }

                ((TokenNP) wl.get(previousPos)).setToken(((TokenNP) wl.get(previousPos)).getToken()
                        + " " + sb.toString());


                for (int k = 0; k < i - (previousPos); k++) {
                    wl.remove(i);
                }

                i--;
                //break restart;
            }
            previousPos = i;
            previousType = t.getTag();
        }
//        System.out.println("\nSaída\n");
//        System.out.println(Main.printList(wl));
        //System.exit(0);
    }

    private void cleanLists(LinkedList<String>... listas) {
        for (LinkedList<String> s : listas) {
            s.clear();
        }
    }

    public boolean isValid(HashToken hsw, LinkedList<String> cit, LinkedList<String> tag, boolean html) {
        int validTokens = 0;
        int moreThan1Letter = 0;

        for (String c : cit) {
            if (c.length() > 1 && !FileHandler.isNumber(c) && !c.equalsIgnoreCase("<barran>")
                    && !isSymbol(c)/*&& !hsw.contains(c)*/) {
                validTokens++;
            }
        }

        for (int i = 0; i < cit.size(); i++) {
            String c = cit.get(i);
            String t = tag.get(i);
            if ((t.equalsIgnoreCase("author") || t.equalsIgnoreCase("editor")) && c.length() > 1) {
                moreThan1Letter++;
            }

        }

        if (html) {
            if (validTokens >= 5 && moreThan1Letter > 0) {
                return true;
            }
        } else {
            if (validTokens >= 5 && validTokens < 20 && moreThan1Letter > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidFlex(HashToken hsw, LinkedList<String> cit, LinkedList<String> tag, boolean html) {
        int validTokens = 0;
        StringBuilder sb = new StringBuilder();
        int moreThan1Letter = 0;

        for (String c : cit) {
            sb.append(c);
            if (c.length() > 1 && !FileHandler.isNumber(c) && !c.equalsIgnoreCase("<barran>")
                    && !isSymbol(c)/*&& !hsw.contains(c)*/) {
                validTokens++;
            }
        }

        if (html) {
            if (validTokens >= 5) {
                return true;
            }
        } else {
            if (validTokens >= 5 && validTokens < 20) {
                return true;
            }
        }
        if (cit.size() == 0 || sb.toString().trim().equals("")) {
            return false;
        }

        return false;
    }

    /**
     * Function test
     *
     * @param f
     * @param hsw Stopwords' HashToken
     * @param wl
     * @return
     */
    public LinkedList<String> getCitations(Storage st, LinkedList<SuperClasse> wl, HashToken hsw, String filename) {
        LinkedList<String> resultados = new LinkedList();
        LinkedList<LinkedList<String>> listRes = new LinkedList();
        LinkedList<String> temp = new LinkedList();
        LinkedList<String> tempToken = new LinkedList();
        LinkedList<String> linha = new LinkedList();

        String ini = "";
        boolean verified = false;

        int marker = 0;
        boolean opened = false;
        boolean li = false;
        int lineCounter = 0;
        SuperClasse s;

        /* Runs the list */
        for (int i = 0; i < wl.size(); i++) {
            s = wl.get(i);

            /* If s is an object TokenNP, then */
            if (s instanceof TokenNP) {
                TokenNP t = (TokenNP) s;

                if (t.getToken().equalsIgnoreCase("<barran>")) {
                    linha.add(t.getToken());
                }

                /* if, during running the list, a "<barran>" is found */
                if (t.getToken().equalsIgnoreCase("<barran>") && !opened) {
//                    System.out.println("BARRAN");
                    //System.out.println("Achou <barran>, e vai começar uma linha 1");

                    opened = true;
                    marker = i;

                    if (!this.minimumAtFirstLine(temp) && lineCounter == 1) {
                        temp.clear();
                        tempToken.clear();
                    } else {
                        //System.out.println("> " + this.verifyCitation(temp) 
                        //        + " \n> " + tempToken + " \n> " + temp);

                        if (this.verifyCitation(temp)) {
                            //System.out.println(tempToken);

                            if (!this.verifyIfCanBeContinued(i, wl, temp, tempToken, hsw) && linha.size() > 0) {
                                //System.out.println("Verify1");
                                //if (isValid(hsw, tempToken)){
                                StringBuilder sb = new StringBuilder();
                                for (String k : tempToken) {
                                    sb.append(k).append(" ");
                                }

                                //if (tempToken.size() > 1){
                                resultados.add(sb.toString());
                                //System.out.println("Saída: " + tempToken);

                                int init = i - temp.size();

                                for (int k = init; k >= 0; --k) {
                                    if (((TokenNP) wl.get(k)).getToken().equalsIgnoreCase("<barran>")) {
                                        init = k;
                                        break;
                                    }
                                }

                                st.insertIntoStorage(i - temp.size() - 1, i, filename);

                                listRes.add(temp);

                                if (resultados.size() == 3) {
                                    ini = this.beginning(listRes);
                                }
                                //}

                                cleanLists(temp, tempToken, linha);
                                lineCounter = 0;
                                //}
                            } else {
                                continue;
                            }

                        }
                    }

                    continue;
                } else if (t.getToken().equalsIgnoreCase("<barran>") && opened) {
                    //System.out.println("BARRAN");
                    //System.out.println("Achou <barran>, e vai começar uma linha 2");
                    //System.out.println(">>" + verifyCitation(temp) + " \n\t" + temp + "\n\t" + tempToken);
                    //System.out.println(linha);

                    if (!verifyCitation(temp)) {
                        //System.out.println("Não configura citação");

                        lineCounter++;
                        if (lineCounter == 1 && !this.minimumAtFirstLine(temp)) {
                            //System.out.println("Primeira condição");
                            marker = i;

                            cleanLists(temp, tempToken, linha);
                            lineCounter = 0;
                            //opened = false;
                        }

                        if (lineCounter == 2) {
                            //System.out.println("Terceira condição");
                            marker = i;
                        }
                        if (lineCounter == 6 || lineCounter == 7) {
                            //System.out.println("Quarta condição");

                            cleanLists(temp, tempToken, linha);

                            lineCounter = 0;
                            i = marker;
                            //opened = false;
                        }
                    } else if (verifyCitation(temp)) {
                        //System.out.println(tempToken);
                        if (lineCounter < 6) {
                            //System.out.println("Verify2");
                            if (this.verifyIfCanBeContinued(i, wl, temp, tempToken, hsw) && tempToken.size() > 0) {
                                continue;
                            }
                        } else {
                            continue;
                        }


                        opened = false;

                        if (isValid(hsw, tempToken, temp, false)) {
                            //System.out.println("Configura citação");
                            StringBuilder sb = new StringBuilder();
                            for (String k : tempToken) {
                                sb.append(k).append(" ");
                            }


                            int init = i - temp.size();

                            for (int k = init; k >= 0; --k) {
                                if (((TokenNP) wl.get(k)).getToken().equalsIgnoreCase("<barran>")) {
                                    init = k;
                                    break;
                                }
                            }

                            st.insertIntoStorage(init, i - 1, filename);
                            //System.out.println("Saída: " + tempToken + " " 
                            //        + tempToken.size() + " " + ((TokenNP) wl.get(i)).getToken());

                            resultados.add(sb.toString());
                            listRes.add(temp);
                        }

                        if (resultados.size() == 3) {
                            ini = this.beginning(listRes);
                        }

                        cleanLists(temp, tempToken, linha);

                        lineCounter = 0;
                    }
                    continue;
                } else {

//                    boolean contain = false;
//                    
//                    for (String g: linha){
//                        if (equalType(g, t.getTag())){
//                            contain = true;
//                            
//                        }
//                    }

                    temp.add(t.getTag());
                    tempToken.add(t.getToken());
                    //System.out.println("\n" + verifyCitation(temp) + " \n\t" + temp + "\n\t" + tempToken);
                }
            }
        }

        return resultados;
    }

    /**
     * Function test
     *
     * @param f
     * @param wl
     * @return
     */
    public LinkedList<String> getCitationsBackup(LinkedList<SuperClasse> wl) {
        LinkedList<String> resultados = new LinkedList();
        LinkedList<LinkedList<String>> listRes = new LinkedList();
        LinkedList<String> temp = new LinkedList();
        LinkedList<String> tempToken = new LinkedList();
        LinkedList<String> linha = new LinkedList();

        String ini = "";
        boolean verified = false;

        int marker = 0;
        boolean opened = false;
        int lineCounter = 0;
        SuperClasse s;


        for (int i = 0; i < wl.size(); i++) {
            s = wl.get(i);
            if (s instanceof TokenNP) {
                TokenNP t = (TokenNP) s;
                if (t.getToken().equalsIgnoreCase("<barran>") && !opened) {
//                    System.out.println("Achou <barran>, e vai começar uma linha 1");


                    opened = true;
                    marker = i;

                    if (!this.minimumAtFirstLine(temp)) {
                        temp.clear();
                        tempToken.clear();
                    } else {
                        if (this.verifyCitation(temp)) {

//                         if (!this.verifyIfCanBeContinued(i, wl, temp, tempToken, hsw)){
                            StringBuilder sb = new StringBuilder();
                            for (String k : tempToken) {
                                sb.append(k).append(" ");
                            }


                            resultados.add(sb.toString());
                            listRes.add(temp);


                            if (resultados.size() == 3) {
                                ini = this.beginning(listRes);
                            }

                            temp.clear();
                            tempToken.clear();
                            lineCounter = 0;
                        } else {
                            continue;
                        }

                        //}
                    }

                    continue;
                } else if (t.getToken().equalsIgnoreCase("<barran>") && opened) {
//                    System.out.println("Achou <barran>, e vai começar uma linha 2");
//                    System.out.println(verifyCitation(temp) + " \n\t" + temp + "\n\t" + tempToken);

                    if (!verifyCitation(temp)) {
//                        System.out.println("Não configura citação");

                        lineCounter++;
                        if (lineCounter == 1 && !this.minimumAtFirstLine(temp)) {
//                            System.out.println("Primeira condição");
                            marker = i;
                            temp.clear();
                            tempToken.clear();
                            lineCounter = 0;
                            opened = false;
                        }

                        if (lineCounter == 2) {
//                            System.out.println("Terceira condição");
                            marker = i;
                        }
                        if (lineCounter == 4) {
//                            System.out.println("Quarta condição");
                            temp.clear();
                            tempToken.clear();
                            lineCounter = 0;
                            i = marker;
                            opened = false;
                        }
                    } else {

                        if (lineCounter < 4) {
                            //if (this.verifyIfCanBeContinued(i, wl, temp, tempToken)){
                            //    continue;
                            //}
                        }

//                        System.out.println("Configura citação");
                        opened = false;

                        StringBuilder sb = new StringBuilder();
                        for (String k : tempToken) {
                            sb.append(k).append(" ");
                        }


                        resultados.add(sb.toString());
                        listRes.add(temp);


                        if (resultados.size() == 3) {
                            ini = this.beginning(listRes);
                        }

                        temp.clear();
                        tempToken.clear();
                        lineCounter = 0;
                    }
                    continue;
                } else {
                    temp.add(t.getTag());
                    tempToken.add(t.getToken());
//                    System.out.println(verifyCitation(temp) + " \n\t" + temp + "\n\t" + tempToken);
                }
            }
        }

        return resultados;
    }

    private void calculateProbability(HashToken ht, HashToken hsw, HashToken hci, LinkedList<String> tokens, LinkedList<String> values) {

        HashMap<String, Double> hm = new HashMap(); //Frequency
        HashMap<String, Integer> hc = new HashMap(); //Counting
        HashExternOperations heo = new HashExternOperations(ht);
        String[] types = {
            "author", "title", "booktitle", "journal", "volume",
            "number", "pages", "date", "location", "publisher",
            "institution", "editor", "note", "tech"
        };


        if (tokens.size() == 1 && tokens.get(0).length() > 1) {
            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));
            return;
        }


        for (int i = 0; i < values.size(); i++) {
            values.set(i, "notype");
        }

        for (int i = 0; i < values.size(); i++) {
//            System.out.println(tokens.get(i) + "> " + (heo.searchForFrequency(tokens.get(i), "author") > 0));
            if (ht.exists(tokens.get(i))
                    || (heo.searchForFrequency(tokens.get(i), "author") > 0 && i == values.size() - 1)) {
                values.set(i, "token");
            } else if (hsw.exists(tokens.get(i))) {
                values.set(i, "");
            } else {
                values.set(i, "");
            }
        }

        int totalUseful = 0;
        for (String s : values) {
            if (s.equalsIgnoreCase("token") || s.equalsIgnoreCase("location")) {
                //System.out.println("OK");
                totalUseful++;
            }
        }

        if (totalUseful == 0) {
            return;
        }

        for (String t : types) {
            int actual = 0;
            int counter = 0;
            for (int i = 0; i < tokens.size(); i++) {
                String s = tokens.get(i);
                String v = values.get(i);
                if (v.equalsIgnoreCase("token")) {
                    //System.out.println(s + ">>" + v);
                    if (heo.searchForFrequency(s, t) > 0) {
                        actual++;
                        counter += heo.searchForFrequency(s, t);
                    }
                } else {
                }
            }

            //System.out.println("Actual= " + actual + ": Tipo= " + t);
            //System.out.println();
            //System.out.println(t +": " + Double.valueOf(actual) + "/" + Double.valueOf(totalUseful) + " = " + (Double.valueOf(actual)/Double.valueOf(totalUseful)) );
            hm.put(t, (Double.valueOf(actual) / Double.valueOf(totalUseful)));
            hc.put(t, counter);
        }

        Double maior = 0.0;
        String result = "";

        Set<String> g = hm.keySet();
        for (String s : g) {

            //System.out.println(s + ": " + hm.get(s) + ": " + totalUseful);
            if (hm.get(s) > maior) {
                maior = hm.get(s);
                result = s;
            }
        }

        //Verify for draw
        int times = 0;
        for (String s : g) {
            if (Double.compare(hm.get(s), maior) == 0) {
                times++;
            }
        }

        int bigger = 0;

        if (times > 1) {
            Set<String> c = hc.keySet();
            for (String s : c) {
                if (hc.get(s) > bigger) {
                    bigger = hc.get(s);
                    result = s;
                }
            }
            //System.out.println("Entrou por empate: " + maior + ">" + result);
        }

        for (int i = 0; i < values.size(); i++) {
            values.set(i, result);
        }

    }

    public String calculateBlockClass(HashToken ht, HashToken hsw,
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

        if (st.toString().trim().matches("([0-9]+) - ([0-9]+)")) {
            values.set(0, "pages");
            return "pages";
        }

        /*MUDEI AQUI 8/4 - 14:21*/

        if (st.toString().trim().replaceAll("[ ]+", " ").matches("([^ ]+) ([0-9]{2})")) {
//            System.out.println("book" + st.toString().trim().replaceAll("[ ]+", " "));
            values.set(0, "booktitle");
            return "booktitle";
        }

        /* if the token's just a token */
        if (tokens.size() == 1 && tokens.get(0).trim().length() > 1) {

            String s = heo.searchForTheMoreOften(tokens.get(0).trim());

            if ((s.equalsIgnoreCase("author") && !isSymbol(previous))) {//previous.equalsIgnoreCase("."))){
                values.set(0, "author");
                return "author";
            }

            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));
            return s;

            /* if the token's just a letter */
        } else if (tokens.size() == 1 && tokens.get(0).trim().length() == 1) {

            String s = heo.searchForTheMoreOften(tokens.get(0));
            //System.out.println(s + ">" + tokens.get(0).trim() + "<");
            if (isSymbol(tokens.get(0).trim())) {
                return "";
            }
            if ((s.equalsIgnoreCase("author") && !previous.equalsIgnoreCase("."))) {
                values.set(0, "author");
                return "author";
            }
            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));

            return s;
            /* two tokens, like : bystrof dw */
        } else if (tokens.size() == 2 && tokens.get(0).trim().length() > 3
                && tokens.get(tokens.size() - 1).trim().length() > 3) {

            String s = heo.searchForTheMoreOften(tokens.get(0));

            if (isSymbol(tokens.get(0).trim())) {
                return "";
            }
            if ((s.equalsIgnoreCase("author") && !previous.equalsIgnoreCase("."))) {
                values.set(0, "author");
                return "author";
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
                    && !isSimpleSymbol(tokens.get(i))) {
                values.set(i, "token");
                /* if the token is a stopword */
            } else if (hsw.exists(tokens.get(i))) {
                values.set(i, "");
            } else {
                values.set(i, "");
            }
        }

        //System.out.println(tokens);
        //System.out.println(values);

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

            //System.out.println("Actual= " + actual + ": Tipo= " + t);
            //System.out.println();
            //System.out.println(t +": " + Double.valueOf(actual) + "/" + Double.valueOf(totalUseful) + " = " + (Double.valueOf(actual)/Double.valueOf(totalUseful)) );
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

        //System.out.println(1+ result + " " + maior);

        if (maior == 0.0) {
            return "";
        }

        if (Double.compare(hm.get(result), threshold) < 0) {
            for (int i = 0; i < values.size(); i++) {
                values.set(i, "");
            }
            return "";
        }

        //System.out.println(2+ result + " " + maior);

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
                //System.out.println(s + ": " + hc.get(s) );
                if (hc.get(s) > bigger) {
                    bigger = hc.get(s);
                    result = s;
                }
            }
            //System.out.println("Entrou por empate: " + bigger + ">" + result);
        }

        //System.out.println(result + " " + bigger);

        for (int i = 0; i < values.size(); i++) {
            values.set(i, result);
        }

        return result;

    }

    /**
     * Calculate the block's class using the data bases and the previous
     * classifications found on it.
     *
     * @param hk Hashtable of keywords
     * @param ht Hashtable of tokens
     * @param hsw Hashtable of stopwords
     * @param hci Hashtable of cities
     * @param tokens Block's tokens
     * @param values Block's tokens' values
     * @param fc
     * @param threshold
     * @return
     */
    public String calculateBlockClass(HashToken hk, HashToken ht, HashToken hsw,
            HashToken hci, LinkedList<String> tokens, LinkedList<String> values,
            FormatsCalculus fc, Double threshold) {

        HashMap<String, Double> hm = new HashMap();         //Frequency
        HashMap<String, Integer> hc = new HashMap();        //Counting
        HashExternOperations heo = new HashExternOperations(ht);
        String[] types = {
            "author", "title", "booktitle", "journal", "volume",
            "number", "pages", "date", "location", "publisher",
            "institution", "editor", "note", "tech"
        };

        for (String q : tokens) {
            if (hk.contains(q)) {
                String classe = hk.get(q).get(0).getType();
                values.set(0, classe);
                return classe;
            }
        }

        /* if the block is pages*/
        StringBuilder st = new StringBuilder();
        for (String q : tokens) {
            st.append(q).append(" ");
        }

        if (st.toString().trim().matches("([0-9]+) - ([0-9]+)")) {
            values.set(0, "pages");
            return "pages";
        }

        /*MUDEI AQUI 8/4 - 14:21*/

        if (st.toString().trim().replaceAll("[ ]+", " ").matches("([^ ]+) ([0-9]{2})")) {
            //System.out.println("book" + st.toString().trim().replaceAll("[ ]+", " "));
            values.set(0, "booktitle");
            return "booktitle";
        }

        /* if the token's just a token */
        if (tokens.size() == 1 && tokens.get(0).trim().length() > 1) {

            String s = heo.searchForTheMoreOften(tokens.get(0).trim());

            if ((s.equalsIgnoreCase("author") /*&& !isSymbol(previous)*/)) {//previous.equalsIgnoreCase("."))){
                values.set(0, "author");
                return "author";
            }

            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));
            return s;

            /* if the token's just a letter */
        } else if (tokens.size() == 1 && tokens.get(0).trim().length() == 1) {

            String s = heo.searchForTheMoreOften(tokens.get(0));
            //System.out.println(s + ">" + tokens.get(0).trim() + "<");
            if (isSymbol(tokens.get(0).trim())) {
                return "";
            }
            if ((s.equalsIgnoreCase("author") /*&& !previous.equalsIgnoreCase(".")*/)) {
                values.set(0, "author");
                return "author";
            }
            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));

            return s;
            /* two tokens, like : bystrof dw */
        } else if (tokens.size() == 2 && tokens.get(0).trim().length() > 3
                && tokens.get(tokens.size() - 1).trim().length() > 3) {

            String s = heo.searchForTheMoreOften(tokens.get(0));

            if (isSymbol(tokens.get(0).trim())) {
                return "";
            }
            if ((s.equalsIgnoreCase("author") /*&& !previous.equalsIgnoreCase(".")*/)) {
                values.set(0, "author");
                return "author";
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
                    && !isSimpleSymbol(tokens.get(i))) {
                values.set(i, "token");
                /* if the token is a stopword */
            } else if (hsw.exists(tokens.get(i))) {
                values.set(i, "");
            } else {
                values.set(i, "");
            }
        }

        //System.out.println(tokens);
        //System.out.println(values);

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

            //System.out.println("Actual= " + actual + ": Tipo= " + t);
            //System.out.println();
            //System.out.println(t +": " + Double.valueOf(actual) + "/" + Double.valueOf(totalUseful) + " = " + (Double.valueOf(actual)/Double.valueOf(totalUseful)) );
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

        //System.out.println(1+ result + " " + maior);

        if (maior == 0.0) {
            return "";
        }

        if (Double.compare(hm.get(result), threshold) < 0) {
            for (int i = 0; i < values.size(); i++) {
                values.set(i, "");
            }
            return "";
        }

        //System.out.println(2+ result + " " + maior);

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
                //System.out.println(s + ": " + hc.get(s) );
                if (hc.get(s) > bigger) {
                    bigger = hc.get(s);
                    result = s;
                }
            }
            //System.out.println("Entrou por empate: " + bigger + ">" + result);
        }

        //System.out.println(result + " " + bigger);

        for (int i = 0; i < values.size(); i++) {
            values.set(i, result);
        }

        return result;

    }

    private String calculateProbabilityUsingPreviousAndProxBlock(HashToken ht, HashToken hsw,
            HashToken hci, LinkedList<String> tokens, LinkedList<String> values,
            FormatsCalculus fc, String previous, String prox) {

        HashMap<String, Double> hm = new HashMap();         //Frequency
        HashMap<String, Integer> hc = new HashMap();        //Counting
        HashExternOperations heo = new HashExternOperations(ht);
        String[] types = {
            "author", "title", "booktitle", "journal", "volume",
            "number", "pages", "date", "location", "publisher",
            "institution", "editor", "note", "tech"
        };


        if (tokens.size() == 1 && tokens.get(0).length() > 1) {
            //values.set(0, );

            String s = heo.searchForTheMoreOften(tokens.get(0));
            String f = fc.getTheMostProbable(previous);
            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));

            //System.out.println("S: " + s + "(" + heo.searchForFrequency(tokens.get(0), s) 
            //        + ") | F: " + f + "(" + heo.searchForFrequency(tokens.get(0), f)
            //       + ") | previous: " + previous);

            return s;
        }


        for (int i = 0; i < values.size(); i++) {
            values.set(i, "notype");
        }

        //System.out.println(ListCreator.detectURL("http://www.cifraclub.com.br/") + " " + heo.searchForFrequency("http://www.cifraclub.com.br/", "note"));

        for (int i = 0; i < values.size(); i++) {
            //System.out.println(tokens.get(i) + "> " + (heo.searchForFrequency(tokens.get(i), "author") > 0));
            if ((ht.exists(tokens.get(i).trim()))
                    || (heo.searchForFrequency(tokens.get(i).trim(), "author") > 0 && i == values.size() - 1)
                    || (heo.searchForFrequency(tokens.get(i).trim(), "note")) > 0) {
                values.set(i, "token");
            } else if (hsw.exists(tokens.get(i))) {
                values.set(i, "notype");
            } else {
                values.set(i, "notype");
            }
            //System.out.println(">" + tokens.get(i).trim() + " > " + values.get(i) + " > " + ht.exists(tokens.get(i).trim()));
        }

        int totalUseful = 0;
        for (String s : values) {
            if (s.equalsIgnoreCase("token")) {
                totalUseful++;
            }
        }

        if (totalUseful == 0) {
            return "";
        }

        for (String t : types) {
            int actual = 0;
            int counter = 0;
            for (int i = 0; i < tokens.size(); i++) {
                String s = tokens.get(i);
                String v = values.get(i);
                if (v.equalsIgnoreCase("token")) {
                    //System.out.println(s + ">>" + v);
                    if (heo.searchForFrequency(s, t) > 0) {
                        actual++;
                        counter += heo.searchForFrequency(s, t);
                    }
                } else {
                }
            }

            //System.out.println("Actual= " + actual + ": Tipo= " + t);
            //System.out.println();
            //System.out.println(t +": " + Double.valueOf(actual) + "/" + Double.valueOf(totalUseful) + " = " + (Double.valueOf(actual)/Double.valueOf(totalUseful)) );
            hm.put(t, (Double.valueOf(actual) / Double.valueOf(totalUseful)));
            hc.put(t, counter);
        }

        Double maior = 0.0;
        String result = "";

        Set<String> g = hm.keySet();
        for (String s : g) {

            //System.out.println(s + ": " + hm.get(s) + ": " + totalUseful);
            if (hm.get(s) > maior) {
                maior = hm.get(s);
                result = s;
            }
        }

        if (Double.compare(hm.get(result), 0.0) < 0.5) {
            for (int i = 0; i < values.size(); i++) {
                values.set(i, "");
            }
            return "";
        }

        //Verify for draw
        int times = 0;
        for (String s : g) {
            if (Double.compare(hm.get(s), maior) == 0) {
                times++;
            }
        }

        int bigger = 0;

        if (times > 1) {
            Set<String> c = hc.keySet();
            for (String s : c) {
                if (hc.get(s) > bigger) {
                    bigger = hc.get(s);
                    result = s;
                }
            }
            //System.out.println("Entrou por empate: " + maior + ">" + result);
        }

        for (int i = 0; i < values.size(); i++) {
            values.set(i, result);
        }

        return result;

    }

    private String printPartOfList(int begin, int end, LinkedList<SuperClasse> wl) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i <= end; i++) {
            sb.append(((TokenNP) wl.get(i)).getToken()).append(" ");
        }

        return sb.toString();
    }

    public static LinkedList<SuperClasse> createList(String entrada) throws FileNotFoundException, IOException {
        LinkedList<SuperClasse> ls = new LinkedList<>();
        // while the file was not completely read.
        String g[] = entrada.split(" ");

        for (String in : g) {
            TokenNP t = new TokenNP(in, "symbol");
            if (!t.getToken().equalsIgnoreCase("")) {
                ls.add(t);
            }
        }

        return ls;

    }

    /**
     * This method performs a search for possible types that not exists in
     * database.
     *
     * @param global receives a list of tokens, loaded from a file.
     * @param formats is the formats loaded from file, compiled in a object
     * Format.
     */
    public void verifyNoTypedTypes(LinkedList<SuperClasse> global, Formats formats) {
        LinkedList<LinkedList<String>> format;
        ContentList aux, f;                               //aux percorre a lista, format o formato

        format = formats.getFormats();

        /* percorre a lista de formatos */
        for (LinkedList<String> l : format) {
            for (String s : l) {
                for (SuperClasse sc : global) {
                    if (((TokenNP) sc).getTag().equalsIgnoreCase(s)) {
                    }
                }
            }
        }
    }

    public LinkedList<SuperClasse> getList() {
        return wl;
    }

    public static String[] getTags(String texto) {
        LinkedList<String> out = new LinkedList();
        StringBuilder sb = new StringBuilder();
        Boolean b = false;

        for (char i : texto.toCharArray()) {
            if (i == '>') {
                out.add(sb.toString());
                sb = new StringBuilder();
                b = false;
            }
            if (b) {
                sb.append(i);
            }
            if (i == '<') {
                b = true;
            }
        }

        String[] tags = new String[out.size()];
        int i = 0;
        for (String s : out) {
            tags[i++] = s;
        }

        return tags;
    }

    public static int contaPalavras(String palavra, String texto) {
        int quant = 0;
        String[] arrayString = texto.replace(">", " ").replace("<", " ").split(" ");

        for (int i = 0; i < arrayString.length; i++) {
            //System.out.println(arrayString[i]);
            if (arrayString[i].matches(palavra)) {
                quant++;
            }

        }

        return quant;
    }

    public static LinkedList<String> containsOneInside(String[] array, String texto) {
        LinkedList<String> ll = new LinkedList();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (i != j) {
                    if ((texto.indexOf("<" + array[i] + ">") != -1) && (texto.indexOf("<" + array[j] + ">") != -1)) {

                        String v = texto.substring(texto.indexOf("<" + array[i] + ">"));
                        String w = texto.substring(texto.indexOf("<" + array[j] + ">"));

                        //System.out.println(array[j] + "\n" + v + "\n" + w);

                        int v1 = contaPalavras(array[j], v);
                        int w1 = contaPalavras(array[i], w);

                        //System.out.println(v1 + "-" + w1);
                        if (v1 > w1) {
                            ll.add(array[j]);
                        } else if (v1 < w1) {
                            ll.add(array[i]);
                        }
                    }
                }
            }
        }

        return ll;
    }

    public static String getDeeperTag(String texto, String tag1, String tag2) {
        Element e = ExtractorHTML.makeTree(texto);
        int hTag1 = ExtractorHTML.getHeight(e, tag1, 0);
        int hTag2 = ExtractorHTML.getHeight(e, tag2, 0);
//        System.out.println(hTag1 + " - " + hTag2);
        return "";
    }

    public static String decideWhichFuncTakeTagsOff(String texto) {
        texto = texto.toLowerCase();

        //getDeeperTag(texto, "p", "div");
        //String[] str = getTags(texto);//new String[]{"div", "li", "tr", "td", "p"};
//        System.out.println("L->" + containsOneInside(str, texto));
        //System.exit(0);

        String tex1 = texto.toLowerCase();

        //Verificar

        /*
         int div = contaPalavras("<div.*?>", tex1);
         int p = contaPalavras("<p.*?>", tex1);
//         System.out.println(div + " " + p);
         */
        texto = simplifyTags(texto);
        //return ListCreator.letJustTheWanted(texto);
        /*if (p < div){
         return takeTagsOff_P(texto);
         }
         * 
         */
        return takeTagsOff(texto);
    }

    public static String simplifyTags(String texto) {

        LinkedList<Integer> occ = new LinkedList();
        for (int index = texto.indexOf("<li>");
                index >= 0;
                index = texto.indexOf("<li>", index + 1)) {
            occ.add(index);
        }

        //for (int i)
        //System.out.println(texto);

        //texto = texto.replaceAll(">([ ].+)<", "><");
        texto = texto.replaceAll("<tr [^>]*>", "<tr>");
        texto = texto.replaceAll("<li [^>]*>", "<li>");
        texto = texto.replaceAll("<p [^>]*>", "<p>");
        texto = texto.replaceAll("<div [^>]*>", "<div>");
        texto = texto.replaceAll("<ul [^>]*>", "<ul>");
        //texto = texto.replaceAll("<h([0-9]) [^>]*>", "<h$1>");
        //System.out.println(texto);
        //System.exit(0);
        return texto;
    }

    public static String takeTagsOff(String texto) {
        texto = texto.toLowerCase();
        texto = texto.replace("\n", "");
        texto = texto.replaceAll("<a.*?>", "");
        texto = texto.replaceAll("</a.*?>", "");
        texto = texto.replace("iÂ¿Â½", "");
        //texto = texto.replaceAll("<p>", "");
        //texto = texto.replaceAll("</p>", "");
        //texto = texto.replaceAll("<A.*?>", "");
        //texto = texto.replaceAll("</A.*?>", "");
        //texto = texto.replaceAll("<b.*?>", ".");
        //texto = texto.replaceAll("</b\\s.*?>", ".");
        texto = texto.replaceAll("<b>", ".");
        texto = texto.replaceAll("</b>", ".");
        //texto = texto.replaceAll("<B>", "");
        //texto = texto.replaceAll("</B>", "");
        texto = texto.replaceAll("<b .*?>", ".");
        texto = texto.replaceAll("</b>", ".");
        //texto = texto.replaceAll("<B .*?>", "");
        //texto = texto.replaceAll("</B>", "");
        texto = texto.replaceAll("<u>", ".");
        texto = texto.replaceAll("</u>", ".");
        //texto = texto.replaceAll("<U>", "");
        //texto = texto.replaceAll("</U>", "");
        texto = texto.replaceAll("<i>", "");
        texto = texto.replaceAll("<i.*?>", "");
        texto = texto.replaceAll("</i>", "");
        //texto = texto.replaceAll("<I.*?>", "");
        //texto = texto.replaceAll("</I>", "");
        texto = texto.replaceAll("<sup.*?>", "");
        texto = texto.replaceAll("</sup.*?>", "");
        //texto = texto.replaceAll("<SUP.*?>", "");
        //texto = texto.replaceAll("</SUP.*?>", "");
        //texto = texto.replaceAll("<CITE.*?>", "");
        //texto = texto.replaceAll("</CITE.*?>", "");
        texto = texto.replaceAll("<cite.*?>", "");
        texto = texto.replaceAll("</cite.*?>", "");

        texto = texto.replaceAll("<dd.*?>", "<dd>");
        //texto = texto.replaceAll("</dd>", "");
        texto = texto.replaceAll("<dt.*?>", "<dt>");
        //texto = texto.replaceAll("</dt>", "");

        texto = texto.replaceAll("<strong.*?>", "");
        texto = texto.replaceAll("</strong.*?>", "");
        texto = texto.replaceAll("<font.*?>", "");
        texto = texto.replaceAll("</font.*?>", "");

        texto = texto.replaceAll("<em.*?>", "");
        texto = texto.replaceAll("</em.*?>", "");
        texto = texto.replaceAll("<span.*?>", "");
        texto = texto.replaceAll("</span>", "");
        //texto = texto.replaceAll("<SPAN.*?>", "");
        //texto = texto.replaceAll("</SPAN.*?>", "");
        texto = texto.replaceAll("<img.*?>", "");
        texto = texto.replaceAll("<link.*?>", "");
        //texto = texto.replaceAll("\n[\\t\\s].+<br.*?>[\\t\\s].+\n", "<br>");
        texto = texto.replaceAll("<br.*?>", ".");
        texto = texto.replaceAll("<hr.*?>", "");
        //texto = texto.replaceAll("<HR.*?>", "");
        //texto = texto.replaceAll("<BR.*?>", ".");
        texto = texto.replaceAll("'", "");
        texto = texto.replaceAll("<script.*?>.*?</script>", "");
        texto = texto.replaceAll("<style.*?>.*?</style>", "");
        texto = texto.replaceAll("<!--.*?-->", "");
        texto = texto.replaceAll("<!\\-\\-([^\\-]*\\-)*\\->", "");
        texto = texto.replaceAll("<meta.*?>", "");
        //texto = texto.replaceAll("<META.*?>", "");
        texto = texto.replaceAll("<pre.*?>.*?</pre>", "");
        //texto = texto.replaceAll("</PRE>", "");   

        //
        texto = texto.replaceAll("</td>", "");
        //texto = texto.replaceAll("</TD>", "");  
        texto = texto.replaceAll("<td.*?>", "");
        //texto = texto.replaceAll("<TD.*?>", "");  
        texto = texto.replaceAll("<p.*?>", "<p>");
        /*
         texto = texto.replaceAll("</small>", "");
         texto = texto.replaceAll("</SMALL>", "");  
         texto = texto.replaceAll("<small.*?>", "");
         texto = texto.replaceAll("<SMALL.*?>", "");*/
        texto = texto.replaceAll("<code.*?>.*?</code>", "");



        return texto;
    }

    public static String takeTagsOff_P(String texto) {
        texto = texto.toLowerCase();
        texto = texto.replace("\n", "");
        texto = texto.replaceAll("<a.*?>", "");
        texto = texto.replaceAll("</a.*?>", "");
        texto = texto.replace("iÂ¿Â½", "");
        //texto = texto.replaceAll("<p>", "");
        //texto = texto.replaceAll("</p>", "");
        //texto = texto.replaceAll("<A.*?>", "");
        //texto = texto.replaceAll("</A.*?>", "");
        //texto = texto.replaceAll("<b.*?>", ".");
        //texto = texto.replaceAll("</b\\s.*?>", ".");
        texto = texto.replaceAll("<b>", ".");
        texto = texto.replaceAll("</b>", ".");
        //texto = texto.replaceAll("<B>", "");
        //texto = texto.replaceAll("</B>", "");
        texto = texto.replaceAll("<b .*?>", ".");
        texto = texto.replaceAll("</b>", ".");
        //texto = texto.replaceAll("<B .*?>", "");
        //texto = texto.replaceAll("</B>", "");
        texto = texto.replaceAll("<u>", ".");
        texto = texto.replaceAll("</u>", ".");
        //texto = texto.replaceAll("<U>", "");
        //texto = texto.replaceAll("</U>", "");
        texto = texto.replaceAll("<i>", "");
        texto = texto.replaceAll("<i.*?>", "");
        texto = texto.replaceAll("</i>", "");
        //texto = texto.replaceAll("<I.*?>", "");
        //texto = texto.replaceAll("</I>", "");
        texto = texto.replaceAll("<sup.*?>", "");
        texto = texto.replaceAll("</sup.*?>", "");
        //texto = texto.replaceAll("<SUP.*?>", "");
        //texto = texto.replaceAll("</SUP.*?>", "");
        //texto = texto.replaceAll("<CITE.*?>", "");
        //texto = texto.replaceAll("</CITE.*?>", "");
        texto = texto.replaceAll("<cite.*?>", "");
        texto = texto.replaceAll("</cite.*?>", "");

        texto = texto.replaceAll("<dd.*?>", "<dd>");
        //texto = texto.replaceAll("</dd>", "");
        //texto = texto.replaceAll("<dt.*?>", "");
        //texto = texto.replaceAll("</dt>", "");

        texto = texto.replaceAll("<strong.*?>", "");
        texto = texto.replaceAll("</strong.*?>", "");
        texto = texto.replaceAll("<font.*?>", "");
        texto = texto.replaceAll("</font.*?>", "");

        texto = texto.replaceAll("<em.*?>", "");
        texto = texto.replaceAll("</em.*?>", "");
        texto = texto.replaceAll("<span.*?>", "");
        texto = texto.replaceAll("</span.*?>", "");
        //texto = texto.replaceAll("<SPAN.*?>", "");
        //texto = texto.replaceAll("</SPAN.*?>", "");
        texto = texto.replaceAll("<img.*?>", "");
        texto = texto.replaceAll("<link.*?>", "");
        texto = texto.replaceAll("<br.*?>", ".");
        texto = texto.replaceAll("<hr.*?>", "");
        //texto = texto.replaceAll("<HR.*?>", "");
        //texto = texto.replaceAll("<BR.*?>", ".");
        texto = texto.replaceAll("'", "");
        texto = texto.replaceAll("<script.*?>.*?</script>", "");
        texto = texto.replaceAll("<style.*?>.*?</style>", "");
        texto = texto.replaceAll("<!--.*?-->", "");
        texto = texto.replaceAll("<!\\-\\-([^\\-]*\\-)*\\->", "");
        texto = texto.replaceAll("<meta.*?>", "");
        //texto = texto.replaceAll("<META.*?>", "");
        texto = texto.replaceAll("<pre.*?>.*?</pre>", "");
        /*texto = texto.replaceAll("<PRE.*?>", "");
         texto = texto.replaceAll("</pre>", "");*/
        //texto = texto.replaceAll("</PRE>", "");        
        texto = texto.replaceAll("<p.*?>", "");
        texto = texto.replaceAll("</p>", "");


        texto = texto.replaceAll("<code.*?>.*?</code>", "");
        //texto = texto.replaceAll("<CODE.*?>.*?</CODE>", "");

        //
        texto = texto.replaceAll("</td>", "");
        //texto = texto.replaceAll("</TD>", "");  
        texto = texto.replaceAll("<td.*?>", "");
        //texto = texto.replaceAll("<TD.*?>", "");  



        /*texto = texto.replaceAll("</small>", "");
         texto = texto.replaceAll("</SMALL>", "");  
         texto = texto.replaceAll("<small.*?>", "");
         texto = texto.replaceAll("<SMALL.*?>", "");*/
        return texto;
    }

    public static String letJustTheWanted(String texto) {
        texto = texto.toLowerCase();

        texto = texto.replaceAll("<[^(tr|/tr|div|/div|li|/li)]+>", "");

        return texto;
    }

    public void _createBlockList(HashToken ht, HashToken hsw, HashToken hci, HashToken hk, String fileName, FormatsCalculus fc) throws FileNotFoundException, IOException {

        /* Pre-process to open file and load into structure */
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(is, cs);
        BufferedReader in = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();

        wl.clear();

        /* Open file to write */
        BufferedWriter fw = new BufferedWriter(
                new OutputStreamWriter(
                new FileOutputStream("saida.txt"), "UTF-8"));

        String before = "";
        boolean ref = false;

        /* While the file was not completely read */
        while (in.ready()) {
            sb.append(in.readLine()).append("\n").append("<barran>");
        }

//        System.out.println(sb.toString());
        LinkedList<TokenNP> token = ExtractorHTMLList.mountList(fileName, this, ht, hsw, hci, fc);

        LinkedList<SuperClasse> newWl = new LinkedList();

        for (TokenNP t : token) {
            if (ListCreator.isPDelimiter(t.getToken()) || t.getTag().equalsIgnoreCase("tag")) {
                newWl.add(
                        new TagsNP(t.getTag()));
            } else {
                newWl.add(t);
            }
        }

        wl = newWl;

        fw.close();
        //this.preProcess(wl);
        //this.fixList(wl, fc);
        //System.exit(0);

    }

    public LinkedList<String> _getCitations(Storage st, LinkedList<SuperClasse> wl, HashToken hsw, String filename) {
        LinkedList<String> resultados = new LinkedList();
        LinkedList<LinkedList<String>> listRes = new LinkedList();
        LinkedList<String> temp = new LinkedList();
        LinkedList<String> tempToken = new LinkedList();
        LinkedList<String> linha = new LinkedList();

        String ini = "";
        boolean verified = false;

        int marker = 0;
        boolean opened = false;
        boolean li = false;
        int lineCounter = 0;
        SuperClasse s;

        /* Runs the list */
        for (int i = 0; i < wl.size(); i++) {
            s = wl.get(i);

            /* If s is an object TokenNP, then */
            if (s instanceof TokenNP) {
                TokenNP t = (TokenNP) s;

                if (t.getToken().equalsIgnoreCase("<barran>")) {
                    linha.add(t.getToken());
                }

                /* if, during running the list, a "<barran>" is found */
                if (t.getToken().equalsIgnoreCase("<barran>") && !opened) {
//                    System.out.println("BARRAN");
                    //System.out.println("Achou <barran>, e vai começar uma linha 1");

                    opened = true;
                    marker = i;

                    if (!this.minimumAtFirstLine(temp) && lineCounter == 1) {
                        temp.clear();
                        tempToken.clear();
                    } else {
                        //System.out.println("> " + this.verifyCitation(temp) 
                        //        + " \n> " + tempToken + " \n> " + temp);

                        if (this.verifyCitation(temp)) {
                            //System.out.println(tempToken);

                            if (!this.verifyIfCanBeContinued(i, wl, temp, tempToken, hsw) && linha.size() > 0) {
                                //System.out.println("Verify1");
                                //if (isValid(hsw, tempToken)){
                                StringBuilder sb = new StringBuilder();
                                for (String k : tempToken) {
                                    sb.append(k).append(" ");
                                }

                                //if (tempToken.size() > 1){
                                resultados.add(sb.toString());
                                //System.out.println("Saída: " + tempToken);

                                int init = i - temp.size();

                                for (int k = init; k >= 0; --k) {
                                    if (((TokenNP) wl.get(k)).getToken().equalsIgnoreCase("<barran>")) {
                                        init = k;
                                        break;
                                    }
                                }

                                st.insertIntoStorage(i - temp.size() - 1, i, filename);

                                listRes.add(temp);

                                if (resultados.size() == 3) {
                                    ini = this.beginning(listRes);
                                }
                                //}

                                cleanLists(temp, tempToken, linha);
                                lineCounter = 0;
                                //}
                            } else {
                                continue;
                            }

                        }
                    }

                    continue;
                } else if ((t.getToken().equalsIgnoreCase("<barran>") || t.getTag().equalsIgnoreCase("tag")) && opened) {
                    //System.out.println("BARRAN");
                    //System.out.println("Achou <barran>, e vai começar uma linha 2");
                    //System.out.println(">>" + verifyCitation(temp) + " \n\t" + temp + "\n\t" + tempToken);
                    //System.out.println(linha);

                    if (!verifyCitation(temp)) {
                        //System.out.println("Não configura citação");

                        lineCounter++;
                        if (lineCounter == 1 && !this.minimumAtFirstLine(temp)) {
                            //System.out.println("Primeira condição");
                            marker = i;

                            cleanLists(temp, tempToken, linha);
                            lineCounter = 0;
                            //opened = false;
                        }

                        if (lineCounter == 2) {
                            //System.out.println("Terceira condição");
                            marker = i;
                        }
                        if (lineCounter == 6 || lineCounter == 7) {
                            //System.out.println("Quarta condição");

                            cleanLists(temp, tempToken, linha);

                            lineCounter = 0;
                            i = marker;
                            //opened = false;
                        }
                    } else if (verifyCitation(temp)) {
                        //System.out.println(tempToken);
                        if (lineCounter < 6) {
                            //System.out.println("Verify2");
                            if (this.verifyIfCanBeContinued(i, wl, temp, tempToken, hsw) && tempToken.size() > 0) {
                                continue;
                            }
                        } else {
                            continue;
                        }


                        opened = false;

                        if (isValid(hsw, tempToken, temp, false)) {
                            //System.out.println("Configura citação");
                            StringBuilder sb = new StringBuilder();
                            for (String k : tempToken) {
                                sb.append(k).append(" ");
                            }


                            int init = i - temp.size();

                            for (int k = init; k >= 0; --k) {
                                if (((TokenNP) wl.get(k)).getToken().equalsIgnoreCase("<barran>")) {
                                    init = k;
                                    break;
                                }
                            }

                            st.insertIntoStorage(init, i - 1, filename);
                            //System.out.println("Saída: " + tempToken + " " 
                            //        + tempToken.size() + " " + ((TokenNP) wl.get(i)).getToken());

                            resultados.add(sb.toString());
                            listRes.add(temp);
                        }

                        if (resultados.size() == 3) {
                            ini = this.beginning(listRes);
                        }

                        cleanLists(temp, tempToken, linha);

                        lineCounter = 0;
                    }
                    continue;
                } else {

//                    boolean contain = false;
//                    
//                    for (String g: linha){
//                        if (equalType(g, t.getTag())){
//                            contain = true;
//                            
//                        }
//                    }

                    temp.add(t.getTag());
                    tempToken.add(t.getToken());
                    //System.out.println("\n" + verifyCitation(temp) + " \n\t" + temp + "\n\t" + tempToken);
                }
            }
        }

        return resultados;
    }
}
