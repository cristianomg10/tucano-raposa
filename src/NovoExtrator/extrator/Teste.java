
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.extrator;

//~--- JDK imports ------------------------------------------------------------
import NovoExtrator.structures.SuperClasse;
import NovoExtrator.structures.TagsNP;
import NovoExtrator.structures.TokenNP;
import NovoExtrator.structures.HashToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.Charset;

import java.util.ArrayList;

/**
 *
 */
public class Teste {

    ArrayList<SuperClasse> a = new ArrayList<SuperClasse>();

    public void createList(HashToken ht, String fileName) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("UTF-8");
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, cs));

        /* while the file was not completely read. */
        while (in.ready()) {
            String str = fixTextCodification(in.readLine());
            String[] cTags = getTextSplitedWithTag(str);

            /* run thru the vector */
            for (int i = 0; i < cTags.length; i++) {
                if ((cTags[i] != null) && !cTags[i].equals("")) {
                    if (isTag(cTags[i])) {
                        TagsNP b = new TagsNP();

                        b.insert(cTags[i]);
                        a.add(b);
                    } else {
                        StringBuilder s = new StringBuilder();

                        str = cTags[i];

                        // String[] tokens = constructWords(str);
                        String[] tokens = this.separateTokens(str);

                        for (String t : tokens) {
                            if ((t != null) && !t.equals("")) {

                                // //system.out.printzln(t);
                                // if (t != null){
                                TokenNP c = new TokenNP(t, this.decideProbType(ht, t));

                                a.add(c);

                                // }
                            }
                        }
                    }
                }
            }
        }

        // this.fixList(wl);
    }

    public String fixTextCodification(String str) {
        str = str.replace("&ccedil;", "ç");
        str = str.replace("&eacute;", "é");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&aacute;", "á");
        str = str.replace("&euml;", "ë");
        str = str.replace("&ntilde;", "ñ");

        return str;
    }

    public static boolean isTag(String c) {
        if (c.contains("<") && c.contains(">")) {
            return true;
        }

        return false;
    }

    public String[] getTextSplitedWithTag(String s) {
        int counter = 0;
        int max = 10;
        String[] text = new String[max];
        boolean inicio = false,
                fim = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) != '<') && (inicio == false)) {
                if ((s.charAt(i) != ' ') && !inicio) {
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
            } else if ((s.charAt(i) == '<') && (inicio == false)) {
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
            } else if ((s.charAt(i) == '>') && (inicio == true)) {
                sb.append(s.charAt(i));
                fim = true;
            } else {
                sb.append(s.charAt(i));
            }

            if ((inicio == true) && (fim == true)) {
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

    public String[] separateTokens(String c) {
        c = c.replace(".", " . ");
        c = c.replace(",", " , ");
        c = c.replace("!", " ! ");
        c = c.replace(":", " : ");
        c = c.replace("-", " - ");
        c = c.replace("(", " ( ");
        c = c.replace(")", " ) ");
        c = c.replace("&", " & ");
        c = c.replace("*", " * ");
        c = c.replace("@", " @ ");

        String[] text = c.split(" ");

        for (String t : text) {
            t = t.trim();
        }

        return text;
    }

    public String decideProbType(HashToken ht, String t) {

        /*                if (ht.exists(t.toString().trim().toLowerCase())){
         TokenH tok = (TokenH) ht.get(t.toString().trim().toLowerCase());
         TokenH taux = tok;
         int lim = 0;

         if (!FileHandler.isNumber(t.toString().trim())){
         while (tok != null){
         if (tok.getAttributeCount() > lim){
         lim = tok.getAttributeCount();
         taux = tok;
         }
         //                    tok = tok.getNext();
         }
         ////system.out.println(t + ": " + taux.getType());
         return taux.getType();
         }else if (t.length() == 4 && (t.substring(0, 2).equals("19") || t.substring(0, 2).equals("20"))){
         return ("year");
         }else{
         if (FileHandler.isNumber(t.toString().trim()) && !t.toString().equals("&")){
         return("number");
         } else if (t.length() == 1 && ListCreator.isSymbol(t.toString())){
         return ("symbol");
         }
         else return("notype");
         }
         }*/
        return "";
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
