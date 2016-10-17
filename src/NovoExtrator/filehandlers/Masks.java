
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.filehandlers;

//~--- JDK imports ------------------------------------------------------------
import NovoExtrator.extrator.ContentList;
import NovoExtrator.structures.SuperClasse;
import NovoExtrator.extrator.WordsList;
import NovoExtrator.structures.TokenNP;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 */
public class Masks {

    private int counter = 0;
    private int max = 10;
    private WordsList[] wl = new WordsList[max];
    private LinkedList<LinkedList<TokenNP>> l = new LinkedList();
    private ContentList[] cl = new ContentList[max];

    public ContentList[] getInfoList() {
        return cl;
    }

    /**
     * This method returns the list of masks, type WordsList.
     *
     * @return the list of masks.
     */
    /*public WordsList[] getMasksList() {
     return wl;
     }*/

    /*public int getCount() {
     return counter;
     }*/
    public String printMasksWithRange() {
        StringBuilder crAux = new StringBuilder();

        for (int i = 0; i < counter; ++i) {
            ContentList c = wl[i].getBegin();

            while (c != wl[i].getEnd().getNext()) {
                crAux.append(c.getToken() + "[" + c.getProbType() + "](" + c.getRange() + ")");
                c = c.getNext();
            }

            crAux.append("\n");
        }

        return crAux.toString();
    }

    /**
     *
     * @param textOrPattern receives a text or a pattern to comparation with
     * text2
     * @param text2
     * @return true if textOrPattern equals ignoring case to text2, or if the
     * pattern of textOrPattern is compatible with text2
     */
    private boolean verifyComp(String textOrPattern, String text2) {
        if (textOrPattern.equalsIgnoreCase("%d")) {
            if (FileHandler.isNumber(text2)) {
                return true;
            }
        } else {
            if (textOrPattern.equalsIgnoreCase(text2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method makes the preprocessing of the masks loaded from file. This
     * preprocessing method is such as the preprocessing of the
     * Boyer-Moore-Horspool algorithm.
     */
    private void preProcessMasks() {
        for (int i = 0; i < counter; ++i) {
            ContentList aux = wl[i].getBegin();

            /* pré processamento(preenche todos com 1) */
            while (aux != null) {
                aux.setRange(1);
                aux = aux.getNext();
            }

            /*
             *  segundo pré-processamento()
             * pré-processa do ultimo ao segundo
             */
            aux = wl[i].getEnd();

            while (aux != wl[i].getBegin()) {
                ContentList aux1 = aux.getPrevious();
                int k = 1;

                while (aux1 != null) {
                    if (aux.getToken().equalsIgnoreCase(aux1.getToken())) {
                        aux.setRange(k);

                        break;
                    }

                    ++k;
                    aux1 = aux1.getPrevious();
                }

                if (aux1 == null) {
                    aux.setRange(1);
                }

                aux = aux.getPrevious();
            }

            /* preprocessamento do primeiro */
            aux = wl[i].getBegin();

            ContentList aux1 = aux.getNext();
            int c = 1;

            while (aux1 != null) {
                if (aux.getToken().equalsIgnoreCase(aux1.getToken())) {
                    aux.setRange(c);

                    break;
                }

                c++;
                aux1 = aux1.getNext();
            }

            /* Preprocessamento OK */
        }
    }

    private void propagationMask(ContentList begin, ContentList end, WordsList mask) {

        /*
         * /p = propaga
         * /np = não propaga
         */
        ContentList aux = begin;

        /*
         * propaga pra trás
         */
        while (aux != null) {
            if (!aux.getToken().equalsIgnoreCase(":") && (!aux.getToken().equalsIgnoreCase("."))
                    && (!FileHandler.isNumber(aux.getToken()))) {
                aux.setProbType(mask.getBegin().getProbType());
            } else {
                break;
            }

            aux = aux.getPrevious();
        }

        /*
         * propaga pra frente
         */
        aux = end;

        boolean ps = false;

        while ((aux != null) && (ps == false)) {
            aux.setProbType(mask.getEnd().getProbType());

            if (FileHandler.isNumber(aux.getToken()) || aux.getToken().equalsIgnoreCase(":")
                    || (aux.getToken().equalsIgnoreCase("."))) {
                ps = true;
            }

            aux = aux.getNext();
        }
    }

    public void lookAndSubstituteMasksBrutus(WordsList g) {
        for (int i = 0; i < counter; i++) {
            ContentList w = g.getBegin();
            ContentList m = wl[i].getBegin();

            while (w != null) {
                if ((m != null) && m.getProbType().equalsIgnoreCase(w.getProbType())) {
                }
            }
        }
    }

    public void lookAndSubstituteMasks(LinkedList<SuperClasse> g) {
        ////system.out.println(g.size());

        /* percorrendo a lista de lista de máscaras */
        for (int i = 0; i < l.size(); i++) {
            LinkedList<TokenNP> ll = l.get(i);
            LinkedList<TokenNP> aux = new LinkedList();

            /* percorre a lista global */
            int n = 0;
            SuperClasse sc;

            for (int j = 0; j < g.size(); j++) {
                sc = g.get(j);

                // //system.out.println("i: " + i + " j: " + j);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) g.get(j);

                    if (verifyComp(ll.get(n).getToken(), t.getToken())) {
                        aux.add(t);
                        n++;

                        if (aux.size() == ll.size()) {
                            for (int m = 0; m < aux.size(); m++) {
                                ////system.out.println(aux.get(m).getToken() + " <-> " + ll.get(m).getTag());
                                aux.get(m).setTag(ll.get(m).getTag());
                            }

                            n = 0;
                            aux.clear();
                        }
                    } else {
                        if (aux.size() > 0) {
                            j -= n;
                        }

                        n = 0;
                        aux.clear();
                    }
                }
            }
        }
    }

    public void openFileAndLoadMasks(String fileName) throws FileNotFoundException, IOException {
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        LinkedList<String> fb = new LinkedList();
        LinkedList<String> sb = new LinkedList();

        while (in.ready()) {
            String str = in.readLine();
            String[] s = str.split("<=>");

            if (s.length == 2) {
                String[] firstBit = s[0].trim().split(" ");
                String[] secondBit = s[1].trim().split(" ");

                fb.addAll(Arrays.asList(firstBit));
                sb.addAll(Arrays.asList(secondBit));

                LinkedList<TokenNP> ll = new LinkedList();

                for (int j = 0; j < fb.size(); ++j) {
                    TokenNP t = new TokenNP(fb.get(j), sb.get(j));

                    ll.add(t);
                }

                l.add(ll);
                fb.clear();
                sb.clear();
            }
        }
    }

    public String printMasks() {
        StringBuilder str = new StringBuilder();

        for (LinkedList<TokenNP> ll : l) {
            for (TokenNP t : ll) {
                str.append(" " + t.getToken() + " " + t.getTag() + " ->");
            }

            str.append("\n");
        }

        return str.toString();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
