/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

import NovoExtrator.extrator.ListCreator;
import NovoExtrator.filehandlers.FileHandler;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 */
public class HashExternOperations {

    private HashToken hash;

    public HashExternOperations(HashToken theHash) {
        hash = theHash;
    }

    public String searchForTheMoreOften(String token) {
        LinkedList<TokenH> l = hash.get(token.trim());

        if (ListCreator.detectURL(token.trim()) != -1) {
            return "note";
        }
        ////system.out.println(token.trim() + "<<");
        if (token.trim().length() == 1
                && !FileHandler.isNumber(token.trim())
                && !ListCreator.isSymbol(token.trim())) {
            return ("author");
        }

        if (ListCreator.isSymbol(token.trim())) {
            return ("<symbol>");
        }

        if (l == null) {
            return "";
        }

        String s = "";
        int maior = -1;
        for (TokenH t : l) {
            if (t.getAttributeCount() > maior) {
                maior = t.getAttributeCount();
                s = t.getType();
            }
        }
        if (maior == -1) {
            return "";
        }
        return s;
    }

    public String searchForTheMoreOften(String token, String prox) {
        LinkedList<TokenH> l = hash.get(token.trim());

        if (ListCreator.detectURL(token.trim()) != -1) {
            return "note";
        }

        if (token.trim().length() == 1
                && !FileHandler.isNumber(token.trim())
                && !ListCreator.isSymbol(token.trim())
                && ListCreator.isSymbol(prox.trim())) {
            return ("author");
        }

        if (FileHandler.isNumber(token) && token.length() == 4
                && (token.substring(2).equals("19") || token.substring(2).equals("20"))) {
            return "date";
        }

        if (l == null) {
            return "";
        }

        String s = "";
        int maior = -1;
        for (TokenH t : l) {
            if (t.getAttributeCount() > maior) {
                maior = t.getAttributeCount();
                s = t.getType();
            }
        }
        if (maior == -1) {
            return "";
        }
        return s;
    }

    public int searchForFrequency(String token, String type) {
        token = token.trim();
        if (type == null) {
            return 0;
        }

        if (token.length() == 1 && !FileHandler.isNumber(token)
                && !ListCreator.isSymbol(token) && type.equalsIgnoreCase("author")) {
            return 1;
        }

        if (ListCreator.detectURL(token) != -1) {
            if (type.equalsIgnoreCase("note")) {
                return 1;
            }
        }


        LinkedList<TokenH> l = hash.get(token);
        if (l == null) {
            return 0;
        }

        for (TokenH t : l) {
            if (t.getType().equalsIgnoreCase(type)) {
                return t.getAttributeCount();
            }
        }

        return 0;

    }

    public int searchForFrequency(String token, String type, String prox) {
        token = token.trim();
        if (type == null) {
            return 0;
        }

        if (token.length() == 1 && !FileHandler.isNumber(token) && ListCreator.isSymbol(prox)
                && !ListCreator.isSymbol(token) && type.equalsIgnoreCase("author")) {
            return 1;
        }

        if (ListCreator.detectURL(token) != -1) {
            if (type.equalsIgnoreCase("note")) {
                return 1;
            }
        }


        LinkedList<TokenH> l = hash.get(token);
        if (l == null) {
            return 0;
        }

        for (TokenH t : l) {
            if (t.getType().equalsIgnoreCase(type)) {
                return t.getAttributeCount();
            }
        }

        return 0;

    }

    public int searchForFrequency1(String token, String type) {
        token = token.trim();
        if (type == null) {
            return 0;
        }

        if (token.length() == 1 && !FileHandler.isNumber(token)
                && !ListCreator.isSymbol(token) && type.equalsIgnoreCase("author")
                && hash.contains(token)) {

            LinkedList<TokenH> l = hash.get(token);
            if (l == null) {
                return 0;
            }

            for (TokenH t : l) {
                if (t.getType().equalsIgnoreCase("author")) {
                    return t.getAttributeCount();
                }
            }
        }


        if (ListCreator.detectURL(token) != -1) {
            if (type.equalsIgnoreCase("note")) {
                return 1;
            }
        }


        LinkedList<TokenH> l = hash.get(token);
        if (l == null) {
            return 0;
        }

        for (TokenH t : l) {
            if (t.getType().equalsIgnoreCase(type)) {
                return t.getAttributeCount();
            }
        }

        return 0;

    }
}
