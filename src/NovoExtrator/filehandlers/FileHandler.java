
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.filehandlers;

//~--- JDK imports ------------------------------------------------------------
import NovoExtrator.structures.TokenH;
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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 *
 */
public class FileHandler {

    /**
     *
     * @param ht receives the hashtable that will receive the stopwords.
     * @param f is the file from which we will extract the stopwords.
     */
    public void addStopWordsInTheHash(HashToken ht, String f) throws IOException {
        Charset cs = Charset.forName("ISO-8859-1");
        InputStream is;
        File fi = new File(f);

        is = new FileInputStream(fi);

        BufferedReader br = new BufferedReader(new InputStreamReader(is, cs));

        while (br.ready()) {
            String str = br.readLine();
            TokenH t = new TokenH();

            t.setType("stopword");
            ht.put(str, t);
        }
    }

    /**
     *
     * @param s is the string that will be fixed.
     * @return the new string;
     */
    public static String isolatePunctuationCharacters(String s) {
        String aux = new String(),
                newChar = new String(" ");

        aux = s.replace("/", newChar);

        if ((aux.indexOf(".") != -1) && (aux.trim().length() > 2)) {
            aux = aux.replace(".", newChar);
        }

        aux = aux.replace("^", " ^ ");
        aux = aux.replace("!", " ! ");
        aux = aux.replace("@", " @ ");
        aux = aux.replace("{", " { ");
        aux = aux.replace("}", " } ");
        aux = aux.replace(".", " . ");
        aux = aux.replace(";", " ; ");
        aux = aux.replace("(", " ( ");
        aux = aux.replace(")", " ) ");
        aux = aux.replace(",", " , ");
        aux = aux.replace(":", " : ");
        aux = aux.replace("\"", " \" ");
        aux = aux.replace("``", " '' ");
        aux = aux.replace("´´", " '' ");
        aux = aux.replace("%", " % ");
        aux = aux.replace("=", " = ");
        //aux = aux.replace(">", " > ");
        //aux = aux.replace("<", " < ");
        aux = aux.replace("-", " - ");
        aux = aux.replace("?", " ? ");
        aux = aux.replace("--", newChar);
        aux = aux.replace("&", " & ");
        aux = aux.replace("]", " ] ");
        aux = aux.replace("[", " [ ");
        aux = aux.replaceAll("[^\u0000-\u007F]", " ");
        aux = aux.replaceAll("['<>\\|/]", "");

        return aux;
    }

    public static String justIsolatePunctuationCharacters(String s) {
        String aux = new String(),
                newChar = new String(" ");

        aux = s.replace("/", " / ");
        aux = aux.replace("--", " - ");
        aux = aux.replace("–", " - ");
        aux = aux.replace("^", " ^ ");
        aux = aux.replace("!", " ! ");
        aux = aux.replace("@", " @ ");
        aux = aux.replace("{", " { ");
        aux = aux.replace("}", " } ");
        aux = aux.replace(".", " . ");
        aux = aux.replace(";", " ; ");
        aux = aux.replace("(", " ( ");
        aux = aux.replace(")", " ) ");
        aux = aux.replace(",", " , ");
        aux = aux.replace(":", " : ");

        aux = aux.replace("\"", " . ");
        aux = aux.replace("“", " . ");
        aux = aux.replace("”", " . ");
        aux = aux.replace("", " . ");
        aux = aux.replace("", " . ");
        aux = aux.replace("``", " . ");
        aux = aux.replace("´´", " . ");
        aux = aux.replace("%", " % ");
        aux = aux.replace("=", " = ");
        aux = aux.replace(">", " > ");
        aux = aux.replace("<", " < ");
        aux = aux.replace("-", " - ");
        aux = aux.replace("?", " ? ");
        aux = aux.replace("&", " & ");
        aux = aux.replace("]", " ] ");
        aux = aux.replace("[", " [ ");
        aux = aux.replace("-", " - ");
        aux = aux.replace("–", " - ");
        aux = aux.replace("—", " - ");
        aux = aux.replace("", " - ");
        aux = aux.replace("", " ' ");
        aux = aux.replace("", " - ");
        aux = aux.replace("'", " ' ");
        aux = aux.replaceAll("[\t]+", "\t");
        aux = aux.replaceAll("[\t]", " . ");

        //aux = aux.replaceAll("(['<>\\|/])", " $1 ");

        return aux;
    }

    public static boolean isDash(char c) {
        switch (c) {
            case '-':
            case '–':
            case '—':
            case '':
                return true;
        }
        return false;
    }

    public static String convertsBibTexCharsToPlainText(String s) {
        String aux = new String();

        aux = s.replace("\\c{c}", "ç");

        for (char a = 'a'; a <= 'z'; a++) {
            aux = aux.replace("{" + a + "}", String.valueOf(a));
        }

        aux = aux.replace("\\_", "_");
        aux = aux.replace("\\user{}", "~");
        aux = aux.replace("{\\user}", "~");
        aux = aux.replace("\\user", "~");

        aux = aux.replace("{\\'a}", "á");
        aux = aux.replace("{\\'e}", "é");
        aux = aux.replace("{\\'i}", "í");
        aux = aux.replace("{\\'o}", "ó");
        aux = aux.replace("{\\'u}", "ú");
        aux = aux.replace("{\\'{a}}", "á");
        aux = aux.replace("{\\'{e}}", "é");
        aux = aux.replace("{\\'{i}}", "í");
        aux = aux.replace("{\\'{o}}", "ó");
        aux = aux.replace("{\\'{u}}", "ú");
        aux = aux.replace("\\'a", "á");
        aux = aux.replace("\\'e", "é");
        aux = aux.replace("\\'i", "í");
        aux = aux.replace("\\'o", "ó");
        aux = aux.replace("\\'u", "ú");
        aux = aux.replace("\\'{a}", "á");
        aux = aux.replace("\\'{e}", "é");
        aux = aux.replace("\\'{i}", "í");
        aux = aux.replace("\\'{o}", "ó");
        aux = aux.replace("\\'{u}", "ú");

        aux = aux.replace("{\\^a}", "â");
        aux = aux.replace("{\\^e}", "ê");
        aux = aux.replace("{\\^i}", "î");
        aux = aux.replace("{\\^o}", "ô");
        aux = aux.replace("{\\^u}", "û");
        aux = aux.replace("{\\^{a}}", "â");
        aux = aux.replace("{\\^{e}}", "ê");
        aux = aux.replace("{\\^{i}}", "î");
        aux = aux.replace("{\\^{o}}", "ô");
        aux = aux.replace("{\\^{u}}", "û");
        aux = aux.replace("\\^a", "â");
        aux = aux.replace("\\^e", "ê");
        aux = aux.replace("\\^i", "î");
        aux = aux.replace("\\^o", "ô");
        aux = aux.replace("\\^u", "û");
        aux = aux.replace("\\^{a}", "â");
        aux = aux.replace("\\^{e}", "ê");
        aux = aux.replace("\\^{i}", "î");
        aux = aux.replace("\\^{o}", "ô");
        aux = aux.replace("\\^{u}", "û");

        aux = aux.replace("{\\\"a}", "ä");
        aux = aux.replace("{\\\"e}", "ë");
        aux = aux.replace("{\\\"i}", "ï");
        aux = aux.replace("{\\\"o}", "ö");
        aux = aux.replace("{\\\"u}", "ü");
        aux = aux.replace("{\\\"{a}}", "ä");
        aux = aux.replace("{\\\"{e}}", "ë");
        aux = aux.replace("{\\\"{i}}", "ï");
        aux = aux.replace("{\\\"{o}}", "ö");
        aux = aux.replace("{\\\"{u}}", "ü");
        aux = aux.replace("\\\"a", "ä");
        aux = aux.replace("\\\"e", "ë");
        aux = aux.replace("\\\"i", "ï");
        aux = aux.replace("\\\"o", "ö");
        aux = aux.replace("\\\"u", "ü");
        aux = aux.replace("\\\"{a}", "ä");
        aux = aux.replace("\\\"{e}", "ë");
        aux = aux.replace("\\\"{i}", "ï");
        aux = aux.replace("\\\"{o}", "ö");
        aux = aux.replace("\\\"{u}", "ü");

        aux = aux.replace("{\\`a}", "à");
        aux = aux.replace("{\\`e}", "è");
        aux = aux.replace("{\\`i}", "ì");
        aux = aux.replace("{\\`o}", "ò");
        aux = aux.replace("{\\`u}", "ù");
        aux = aux.replace("{\\`{a}}", "à");
        aux = aux.replace("{\\`{e}}", "è");
        aux = aux.replace("{\\`{i}}", "ì");
        aux = aux.replace("{\\`{o}}", "ò");
        aux = aux.replace("{\\`{u}}", "ù");
        aux = aux.replace("\\`a", "à");
        aux = aux.replace("\\`e", "è");
        aux = aux.replace("\\`i", "ì");
        aux = aux.replace("\\`o", "ò");
        aux = aux.replace("\\`u", "ù");
        aux = aux.replace("\\`{a}", "à");
        aux = aux.replace("\\`{e}", "è");
        aux = aux.replace("\\`{i}", "ì");
        aux = aux.replace("\\`{o}", "ò");
        aux = aux.replace("\\`{u}", "ù");

        aux = aux.replace("{\\~a}", "ã");
        aux = aux.replace("{\\~e}", "ẽ");
        aux = aux.replace("{\\~i}", "ĩ");
        aux = aux.replace("{\\~o}", "õ");
        aux = aux.replace("{\\~u}", "ũ");
        aux = aux.replace("{\\~n}", "ñ");
        aux = aux.replace("{\\~{a}}", "ã");
        aux = aux.replace("{\\~{e}}", "ẽ");
        aux = aux.replace("{\\~{i}}", "ĩ");
        aux = aux.replace("{\\~{o}}", "õ");
        aux = aux.replace("{\\~{u}}", "ũ");
        aux = aux.replace("{\\~{n}}", "ñ");
        aux = aux.replace("\\~a", "ã");
        aux = aux.replace("\\~e", "ẽ");
        aux = aux.replace("\\~i", "ĩ");
        aux = aux.replace("\\~o", "õ");
        aux = aux.replace("\\~u", "ũ");
        aux = aux.replace("\\~n", "ñ");
        aux = aux.replace("\\~{a}", "ã");
        aux = aux.replace("\\~{e}", "ẽ");
        aux = aux.replace("\\~{i}", "ĩ");
        aux = aux.replace("\\~{o}", "õ");
        aux = aux.replace("\\~{u}", "ũ");
        aux = aux.replace("\\~{n}", "ñ");

        aux = aux.replace("{\\ss}", "ß");
        aux = aux.replace("\\ss", "ß");


        return aux;
    }

    /**
     *
     * @param s is the string that will be fixed.
     * @return the new string;
     */
    public static String removeCharacters(String s) {
        String aux = new String(),
                newChar = " ";

        aux = s.replace("/", newChar);

        if ((aux.indexOf(".") != -1) && (aux.trim().length() > 2)) {
            aux = aux.replace(".", newChar);
        }

        aux = aux.replace("^", newChar);
        aux = aux.replace("!", newChar);
        aux = aux.replace("@", newChar);
        aux = aux.replace("[", newChar);
        aux = aux.replace("]", newChar);
        aux = aux.replace("{", newChar);
        aux = aux.replace("}", newChar);
        aux = aux.replace(".", newChar);
        aux = aux.replace(";", newChar);
        aux = aux.replace("(", newChar);
        aux = aux.replace(")", newChar);
        aux = aux.replace(",", newChar);
        aux = aux.replace(":", newChar);
        aux = aux.replace("\"", newChar);
        aux = aux.replace("=", newChar);
        aux = aux.replace(">", newChar);
        aux = aux.replace("<", newChar);
        aux = aux.replace("-", newChar);
        aux = aux.replace("?", newChar);
        aux = aux.replace("--", newChar);
        aux = aux.replace("&", newChar);
        aux = aux.replace("\\", newChar);
        aux = aux.replace("$", newChar);
        aux = aux.replace("`", newChar);
        aux = aux.replace("´", newChar);
        aux = aux.replace("_", newChar);
        aux = aux.replace("~", newChar);
        aux = aux.replace("#", newChar);
        aux = aux.replace("*", newChar);
        aux = aux.replace("+", newChar);
        aux = aux.replace("¶", newChar);
        aux = aux.replace("«", newChar);
        aux = aux.replace("%", newChar);
        aux = aux.replace("'", newChar);
        aux = aux.replace("|", newChar);
        aux = aux.replace("-", newChar);
        aux = aux.replace("–", newChar);
        aux = aux.replace("—", newChar);
        //aux = aux.replaceAll("[^0-9a-z]", newChar);

        return aux;
    }

    /**
     *
     * @param s receives a string
     * @return true if is a number.
     */
    public static boolean isNumber(String s) {
        return s.matches("[0-9]+");
    }

    public static String removeAcentos(String s) {

        String passa = s;

        passa = passa.replaceAll("[ÂÀÁÄÃ]", "A");
        passa = passa.replaceAll("[âãàáä]", "a");
        passa = passa.replaceAll("[ÊÈÉË]", "E");
        passa = passa.replaceAll("[êèéë]", "e");
        passa = passa.replaceAll("[ÎÍÌÏ]", "I");
        passa = passa.replaceAll("[îíìï]", "i");
        passa = passa.replaceAll("[ÔÕÒÓÖ]", "O");
        passa = passa.replaceAll("[ôõòóö]", "o");
        passa = passa.replaceAll("[ÛÙÚÜ]", "U");
        passa = passa.replaceAll("[ûúùü]", "u");
        passa = passa.replaceAll("Ç", "C");
        passa = passa.replaceAll("ç", "c");
        passa = passa.replaceAll("[ýÿ]", "y");
        passa = passa.replaceAll("Ý", "Y");
        passa = passa.replaceAll("ñ", "n");
        passa = passa.replaceAll("Ñ", "N");

        return passa;

    }

    public static String fixUTF8Codification(String str) {
        str = str.replace("&ccedil;", "ç");
        str = str.replace("&eacute;", "é");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&aacute;", "á");
        str = str.replace("&atilde;", "ã");
        str = str.replace("&auml;", "ä");
        str = str.replace("&euml;", "ë");
        str = str.replace("&iuml;", "ï");
        str = str.replace("&ouml;", "ö");
        str = str.replace("&uuml;", "ü");

        str = str.replace("&agrave;", "à");

        str = str.replace("&uacute;", "ú");
        str = str.replace("&euml;", "ë");
        str = str.replace("&ntilde;", "ñ");
        str = str.replace("&quot;", "\"");
        str = str.replace("&apos;", "'");
        str = str.replace("&amp;", "&");
        str = str.replace("&lt;", "<");
        str = str.replace("&gt;", ">");


        str = str.replace("&acirc;", "â");
        str = str.replace("&ecirc;", "ê");
        str = str.replace("&ocirc;", "ô");
        return str;
    }

    /**
     * Insert on hash
     *
     * @param text
     * @param h
     * @param value
     */
    private void insert(String[] text, HashToken h, String value) {
        for (String v : text) {
            if (!v.trim().equalsIgnoreCase("")) {
                h.put(FileHandler.removeAcentos(FileHandler.removeCharacters(v.trim())), new TokenH(value, 1));
            }
        }
    }

    private void insert(String[] text, HashToken h, HashToken hsw, HashToken orig) {

        if (text.length > 1) {
            for (String v : text) {
                if (!v.trim().equalsIgnoreCase("")) {
                    //if (!orig.exists(v)){ 
                    h.put(FileHandler.removeAcentos(FileHandler.removeCharacters(v.trim())).trim(), new TokenH("location", 10000));
                    //}
                }
            }
        } else if (text.length == 1) {
            if (!text[0].trim().equalsIgnoreCase("")) {
                //if (!orig.exists(text[0])){
                h.put(FileHandler.removeAcentos(FileHandler.removeCharacters(text[0].trim())).trim(), new TokenH("location", 10000));
                //}
            }
        }
    }

    public static String toRoman(int i) {
        StringBuilder sb = new StringBuilder();

        int j = i;
        while (j != 0) {
            ////system.out.println(j);
            if (j >= 1000) {
                sb.append("M");
                j -= 1000;
            } else if (j >= 900) {
                sb.append("CM");
                j -= 900;
            } else if (j >= 500) {
                sb.append("D");
                j -= 500;
            } else if (j >= 400) {
                sb.append("CD");
                j -= 400;
            } else if (j >= 100) {
                sb.append("C");
                j -= 100;
            } else if (j >= 90) {
                sb.append("XC");
                j -= 90;
            } else if (j >= 50) {
                sb.append("L");
                j -= 50;
            } else if (j >= 40) {
                sb.append("XL");
                j -= 40;
            } else if (j >= 10) {
                sb.append("X");
                j -= 10;
            } else if (j >= 9) {
                sb.append("IX");
                j -= 9;
            } else if (j >= 5) {
                sb.append("V");
                j -= 5;
            } else if (j >= 4) {
                sb.append("IV");
                j -= 4;
            } else if (j >= 1) {
                sb.append("I");
                j -= 1;
            }
        }
        return sb.toString();
    }

    public void addTokensFromTxt(HashToken hci, HashToken hsw, HashToken h, String filename) throws FileNotFoundException, IOException {
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        while (in.ready()) {
            String v = in.readLine();
            String[] s = v.split(" ");
            ////system.out.println(v);
            insert(s, hci, hsw, h);
        }
    }

    public void addTokensFromTxt(HashToken hk, String filename) throws FileNotFoundException, IOException {
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String value = filename.toLowerCase().trim().replace(".txt", "");
        String[] strip = value.split("/");
        value = strip[strip.length - 1];

        while (in.ready()) {
            String[] s = in.readLine().split(" ");
            ////system.out.println(s.length);
            insert(s, hk, value);
        }
    }

    public void addTokensFromBibTex(HashToken ht, HashToken hsw, String f) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("ISO-8859-1");
        File file = new File(f);
        InputStream is = new FileInputStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, cs));
        String str, type = "";
        int contador = 0;


        while (in.ready()) {
            str = in.readLine().toLowerCase();

            // verifica se nao existe @ na linha e se nao tem só um caracter (fim de bloco)
            if ((str.trim().indexOf("@") == -1) && (str.trim().length() >= 2)) {

                // divide a string pelo =
                String[] strs = str.split("=");

                if (strs.length == 1 && (str.equals("}"))) {
                    continue;
                }

                // elimina os espaços e caracteres invalidos
                String aux = "";

                if (strs.length > 1) {
                    aux = strs[1].trim();
                } else {
                    aux = strs[0].trim();
                }

                String regex1 = "^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z0-9]{2,3}(\\:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*$";
                String regex2 = "^(((ht|f)tp(s?))\\://)?(www.|[a-zA-Z].)[a-zA-Z0-9\\-\\.]+\\.{2,4}(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&amp;%\\$#\\=~_\\-]+))*$";
                String regex3 = "^[a-zA-Z0-9\\-\\.]+\\.[a-z0-9]{2}$";


                // separa por espaços
                aux = convertsBibTexCharsToPlainText(aux);
                aux = aux.replace("{", " ");
                aux = aux.replace("}", " ");
                aux = aux.replace("\"", " ");
                aux = aux.replace("(", " ");
                aux = aux.replace(")", " ");
                aux = aux.replace("\\", "/");
                aux = aux.replace("|", " ");
                aux = aux.replace("$", " ");
                aux = aux.replace("<", " ");
                aux = aux.replace(">", " ");

                String[] token1 = aux.split(" ");
                String url = "";

                for (int i = 0; i < token1.length; ++i) {
                    if (token1[i].length() <= 1) {
                        continue;
                    }

                    url = "";
                    if (token1[i].matches(regex1) || token1[i].matches(regex2) || token1[i].matches(regex3)) {
                        url = token1[i];
                        token1[i] = token1[i].replaceAll(regex1, "HTTPURL");
                        token1[i] = token1[i].replaceAll(regex2, "HTTPURL");
                        token1[i] = token1[i].replaceAll(regex3, "HTTPURL");
                    }//else if ((token1[i].contains("www") || token1[i].contains("http"))){
                    //   //system.out.println(token1[i]);
                    //}
                    ////system.out.println(token1[i]);
                    token1[i] = removeCharacters(token1[i].trim());
                    ////system.out.println(token1[i]);
                    token1[i] = removeAcentos(token1[i]);
                    ////system.out.println(token1[i]);
                    for (String s : token1[i].split("[\\s\\t]")) {
                        if (!s.equals("")) {


                            if (strs.length > 1) {
                                type = strs[0].toLowerCase().trim();
                                if (!this.tipoAceito(type) && !this.tipoData(type)
                                        && !this.tipoLocation(type)) {
                                    continue;
                                }
                            }
//                            TokenH t = new TokenH();
//                            t.setType(type);


                            ////system.out.println("<?" + tipo + "?>");
                            if (!hsw.exists(s.trim())) {
                                if ((!this.tipoAceito(type) && !this.tipoData(type)
                                        && !this.tipoLocation(type))
                                        || s.trim().length() == 1) {
                                    continue;
                                }
                                //if (s.equalsIgnoreCase("HTTPURL"))

                                if (tipoData(type.trim())) {
                                    type = "date";
                                }
                                if (type.trim().equalsIgnoreCase("notes")) {
                                    type = "note";
                                }
                                if (tipoLocation(type.trim())) {
                                    type = "location";
                                }
                                //if (type.equalsIgnoreCase("year")) //system.out.println(tipoData(type));
                                TokenH t = new TokenH();
                                t.setType(type);

                                //se der errado, apagar esse if todo
                                if (s.matches("([a-z]+)(\\d{2,4})")) {
                                    ////system.out.println(s);
                                    String troca = s.replaceAll("([a-z]+)(\\d{2,4})", "$1 $2");
                                    ////system.out.println(troca);
                                    s = s.replace(s, troca);
                                    for (String o : s.split(" ")) {
                                        if (o.length() > 1) {
                                            ht.put(o.toLowerCase().trim(), t);
                                            contador++;
                                        }
                                    }

                                }

                                ////system.out.println(s.toLowerCase());
                                ht.put(s.toLowerCase().trim(), t);
                                contador++;
                            }

                        }
                    }
                }
            }
        }

        //system.out.println(contador);
        in.close();
    }

    public void addTokensFromXML(HashToken ht, HashToken hsw, String f) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("UTF-8");//UTF8
        File file = new File(f);
        ////system.out.println(file.getAbsolutePath());
        //System.exit(0);
        InputStream is = new FileInputStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, cs));
        String str;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbConteudo = new StringBuilder();

        while (in.ready()) {
            str = in.readLine().trim();


            for (int i = 1; i < str.length(); i++) {
                if (str.charAt(i) != '>') {
                    sb.append(str.charAt(i));
                } else {
                    break;
                }
            }

            for (int i = sb.toString().length() + 1; i < str.length(); i++) {
                if (str.charAt(i) != '<') {
                    sbConteudo.append(str.charAt(i));
                } else {
                    break;
                }
            }

            // elimina os espaços e caracteres invalidos
            String aux = sbConteudo.toString().trim();

            aux = fixUTF8Codification(aux);
            aux = isolatePunctuationCharacters(aux);
            aux = removeAcentos(aux);
            //aux = aux.replaceAll("\\d*", "");

            ////system.out.println(aux.trim());
            // separa por espaços
            String[] token = aux.trim().split(" ");

            for (int i = 0; i < token.length; i++) {
                if (token[i].length() == 1 || token[i].equalsIgnoreCase("")) {
                    continue;
                }

                ////system.out.println(sb.toString() + ": " + token1[i]);

                if (!token[i].trim().equals("-")) {
                    TokenH t = new TokenH();
                    String tipo = sb.toString().trim();

                    t.setType(tipo);


                    if (tipoData(tipo)) {
                        t.setType("date");
                    }
                    if (tipoInstitution(tipo)) {
                        t.setType("institution");
                    }

                    if (!hsw.exists(token[i])) {
                        ////system.out.println(tipo + " : " + !tipoNaoAceito(tipo));
                        if (tipoAceito(tipo)) {
                            t.incAttribute();
                            ht.put(token[i].toLowerCase().trim(), t);
                        }
                        ////system.out.println(">"+t.getType()+"<"+token1[i].toLowerCase().trim());
                    }
                }
            }

            sb = new StringBuilder();
            sbConteudo = new StringBuilder();
        }

        in.close();
    }

    public void addTokensFromXML(HashToken ht, HashToken hsw, String f, String specific) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("UTF-8");//UTF8
        File file = new File(f);
        ////system.out.println(file.getAbsolutePath());
        //System.exit(0);
        InputStream is = new FileInputStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, cs));
        String str;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbConteudo = new StringBuilder();

        while (in.ready()) {
            str = in.readLine().trim();


            for (int i = 1; i < str.length(); i++) {
                if (str.charAt(i) != '>') {
                    sb.append(str.charAt(i));
                } else {
                    break;
                }
            }

            for (int i = sb.toString().length() + 1; i < str.length(); i++) {
                if (str.charAt(i) != '<') {
                    sbConteudo.append(str.charAt(i));
                } else {
                    break;
                }
            }

            // elimina os espaços e caracteres invalidos
            String aux = sbConteudo.toString().trim();

            aux = fixUTF8Codification(aux);
            aux = isolatePunctuationCharacters(aux);
            aux = removeAcentos(aux);
            //aux = aux.replaceAll("\\d*", "");

            ////system.out.println(aux.trim());
            // separa por espaços
            String[] token = aux.trim().split(" ");

            for (int i = 0; i < token.length; i++) {
                if (token[i].length() == 1 || token[i].equalsIgnoreCase("")) {
                    continue;
                }

                ////system.out.println(sb.toString() + ": " + token1[i]);

                if (!token[i].trim().equals("-")) {
                    TokenH t = new TokenH();
                    String tipo = sb.toString().trim();

                    t.setType(tipo);
//rnote, topic, organization, address
//                    if ((tipo.equalsIgnoreCase("journal")) || (tipo.equalsIgnoreCase("booktitle"))
//                            || (tipo.equalsIgnoreCase("school"))) {
//                        t.setType("journal");
//                    } else if ((tipo.equalsIgnoreCase("organization")) || (tipo.equalsIgnoreCase("publisher"))) {
//                        t.setType("publisher");
//                    }
//
//                    if (!tipo.equalsIgnoreCase("author") &&!tipo.equalsIgnoreCase("title")
//                            &&!tipo.equalsIgnoreCase("pages") &&!tipo.equalsIgnoreCase("year")
//                            &&!tipo.equalsIgnoreCase("journal") &&!tipo.equalsIgnoreCase("publisher")) {
//                        break;
//                    }


//                    if (!isNumber(token1[i].trim()) &&!hsw.exists(token1[i])) {
//                        ht.put(token1[i].toLowerCase().trim(), t);
//                        //system.out.println(">"+token1[i].toLowerCase().trim()+"<");
//                    }

                    if (tipoData(tipo)) {
                        t.setType("date");
                    }
                    if (tipoInstitution(tipo)) {
                        t.setType("institution");
                    }

                    if (!hsw.exists(token[i])) {
                        ////system.out.println(tipo + " : " + !tipoNaoAceito(tipo));
                        if (tipoAceito(tipo) && tipo.equalsIgnoreCase(specific)) {
                            t.incAttribute();
                            ht.put(token[i].toLowerCase().trim(), t);
                        }
                        ////system.out.println(">"+t.getType()+"<"+token1[i].toLowerCase().trim());
                    }
                }
            }

            sb = new StringBuilder();
            sbConteudo = new StringBuilder();
        }

        in.close();
    }

    public void addTokensFromXML(HashToken ht, HashToken hsw, HashToken hci, String f, String specific) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("UTF-8");//UTF8
        File file = new File(f);
        ////system.out.println(file.getAbsolutePath());
        //System.exit(0);
        InputStream is = new FileInputStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, cs));
        String str;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbConteudo = new StringBuilder();

        while (in.ready()) {
            str = in.readLine().trim();


            for (int i = 1; i < str.length(); i++) {
                if (str.charAt(i) != '>') {
                    sb.append(str.charAt(i));
                } else {
                    break;
                }
            }

            for (int i = sb.toString().length() + 1; i < str.length(); i++) {
                if (str.charAt(i) != '<') {
                    sbConteudo.append(str.charAt(i));
                } else {
                    break;
                }
            }

            // elimina os espaços e caracteres invalidos
            String aux = sbConteudo.toString().trim();

            aux = fixUTF8Codification(aux);
            aux = isolatePunctuationCharacters(aux);
            aux = removeAcentos(aux);
            //aux = aux.replaceAll("\\d*", "");

            ////system.out.println(aux.trim());
            // separa por espaços
            String[] token = aux.trim().split(" ");

            for (int i = 0; i < token.length; i++) {
                if (token[i].length() == 1 || token[i].equalsIgnoreCase("")) {
                    continue;
                }

                ////system.out.println(sb.toString() + ": " + token1[i]);

                if (!token[i].trim().equals("-")) {
                    TokenH t = new TokenH();
                    String tipo = sb.toString().trim();

                    t.setType(tipo);

                    if (tipoData(tipo)) {
                        t.setType("date");
                    }
                    if (tipoInstitution(tipo)) {
                        t.setType("institution");
                    }

                    if (!hsw.exists(token[i]) && !hci.exists(token[i])) {
                        ////system.out.println(tipo + " : " + !tipoNaoAceito(tipo));
                        if (tipoAceito(tipo) && tipo.equalsIgnoreCase(specific)) {
                            t.incAttribute();
                            ht.put(token[i].toLowerCase().trim(), t);
                        }
                        ////system.out.println(">"+t.getType()+"<"+token1[i].toLowerCase().trim());
                    }
                }
            }

            sb = new StringBuilder();
            sbConteudo = new StringBuilder();
        }

        in.close();
    }

    private boolean tipoNaoAceito(String t) {
        if (t.equalsIgnoreCase("url") || t.equalsIgnoreCase("doi") || t.equalsIgnoreCase("ee") || t.equalsIgnoreCase("crossref")
                || t.equalsIgnoreCase("rtnote") || t.equalsIgnoreCase("topic") || t.equalsIgnoreCase("organization")
                || t.equalsIgnoreCase("address") || t.equalsIgnoreCase("note")) {
            return true;
        }
        if (((t.split(" ")).length) > 1) {
            return true;
        }
        return false;
    }

    public boolean tipoAceito(String t) {
        if (t.equalsIgnoreCase("author")
                || t.equalsIgnoreCase("title")
                || t.equalsIgnoreCase("booktitle")
                || t.equalsIgnoreCase("journal")
                || t.equalsIgnoreCase("volume")
                || t.equalsIgnoreCase("number")
                || t.equalsIgnoreCase("pages")
                || t.equalsIgnoreCase("date")
                || t.equalsIgnoreCase("location")
                || t.equalsIgnoreCase("publisher")
                || t.equalsIgnoreCase("institution")
                || t.equalsIgnoreCase("editor")
                || t.equalsIgnoreCase("note")
                || t.equalsIgnoreCase("tech")) {
            return true;
        }
        return false;
    }

    public boolean tipoLocation(String t) {
        if (t.equalsIgnoreCase("address")) {
            return true;
        }
        return false;
    }

    private boolean tipoData(String t) {
        t = t.trim();
        if (t.equalsIgnoreCase("year") || t.equalsIgnoreCase("month") || t.equalsIgnoreCase("date")) {
            return true;
        }
        return false;
    }

    private boolean tipoInstitution(String str) {
        if (str.equalsIgnoreCase("school")) {
            return true;
        }
        return false;
    }

    public void saveHashAsTxt(HashToken ht, String f) throws FileNotFoundException, IOException {
        Charset cs = Charset.forName("ISO-8859-1");
        File file = new File(f);
        OutputStream is = new FileOutputStream(file);
        BufferedWriter in = new BufferedWriter(new OutputStreamWriter(is, cs));
        StringBuilder sb = new StringBuilder();

        Enumeration i = ht.getTable().keys();
        int j = 0;

        while (i.hasMoreElements()) {
            String s = (String) i.nextElement();
            sb.append(s).append(": ");

            for (TokenH th : ht.get(s)) {
                sb.append(th.getType()).append("(").append(th.getAttributeCount()).append(") -> ");
            }
            sb.append("\n");

            j++;
        }

        ////system.out.println("SB: " + sb.toString() + "j" + j);
        in.write(sb.toString());
        in.close();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
