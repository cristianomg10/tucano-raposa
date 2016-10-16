
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.extrator;

//~--- JDK imports ------------------------------------------------------------
import java.io.File;

/**
 * This class is a implementation of a linked list, with my own updates.
 *
 * @author cristiano
 */
public class WordsList {

    private ContentList begin = null,
            end = null;
    private ContentList beginT = null,
            endT = null;
    private int count = 0;
    private String ultimo;

    /**
     * This method retorns the begin of a current list.
     *
     * @returns list's begin.
     */
    public ContentList getBegin() {
        return this.begin;
    }

    /**
     * This method return the list of objects, in a String format.
     *
     * @return a String containing the list print.
     */
    public String printList() {
        String aux = new String();
        ContentList b = begin,
                e = end;

        if (!(b == null) && !(e == null)) {
            while (b != e.getNext()) {
                aux += b.getToken() + "(" + b.getProbType() + ") \n";
                b = b.getNext();
            }
        }

        return aux;
    }

    public String printListWithTag() {
        String aux = new String();
        ContentList b = beginT,
                e = end;

        if ((ultimo != null) && ultimo.equalsIgnoreCase("tag")) {
            e = endT;
        }

        if (b == null) {
            b = begin;
        }

        if (!(b == null) && !(e == null)) {
            while (b != e.getNextWithTag()) {
                aux += b.getToken() + "(" + b.getProbType() + ") \n";
                b = b.getNextWithTag();
            }
        }

        return aux;
    }

    public String printInListFormat() {
        String aux = new String();
        ContentList b = begin,
                e = end;

        if (!(b == null) && !(e == null)) {
            while (b != e.getNext()) {
                aux += b.getToken() + "(" + b.getProbType() + ") ";
                b = b.getNext();
            }
        }

        return aux;
    }

    public void setBegin(ContentList begin) {
        this.begin = begin;
    }

    public void setEnd(ContentList end) {
        this.end = end;
    }

    public int getSize() {
        if ((count == 0) && (begin == null)) {
            return this.count;
        } else {
            count = 0;

            ContentList aux = this.getBegin();

            while (aux != this.getEnd().getNext()) {
                count++;
                aux = aux.getNext();
            }
        }

        return count;
    }

    /**
     *
     * @param index receives the index of our list, thinking in this list like a
     * array.
     * @return the object with data if exists. If it isnt exists, returns null.
     */
    public ContentList getData(int index) {
        if (index == 0) {
            return getBegin();
        }

        if ((index == getSize()) || (index == getSize() - 1)) {
            return getEnd();
        }

        if (index > getSize()) {
            return null;
        }

        int c = 0;
        ContentList aux = getBegin();
        ContentList fim = ((getEnd() == null)
                ? getEnd()
                : getEnd().getNext());

        while (aux != fim) {
            if (c == index) {
                return aux;
            }

            ++c;
            aux = aux.getNext();
        }

        return null;
    }

    public ContentList getEnd() {
        return this.end;
    }

    public void insertWord(String word, String ptype) {
        ContentList c = new ContentList();

        c.setToken(word);
        c.setProbType(ptype);

        boolean eTag = ListCreator.isTag(word);

        count++;

        if (eTag && (beginT == null) && (begin == null)) {
            beginT = c;
            endT = c;
            ultimo = "tag";
        } else if (!eTag && (beginT == null) && (begin == null)) {
            begin = c;
            end = c;
            ultimo = "notag";
        } else if (eTag && (beginT != null) && (begin == null)) {
            c.setPreviousWithTag(endT);
            endT.setNextWithTag(c);
            endT = c;
            ultimo = "tag";
        } else if (!eTag && (beginT != null) && (begin == null)) {
            begin = c;
            ultimo = "notag";
            endT.setNextWithTag(c);
            end = c;
        } else if (!eTag && (beginT == null) && (begin != null)) {
            end.setNext(c);
            c.setPrevious(begin);
            end = c;
            ultimo = "notag";
        } else if (eTag && (beginT != null) && (begin != null) && ultimo.equalsIgnoreCase("notag")) {

            // //system.out.println("aqui");
            end.setNextWithTag(c);
            c.setPreviousWithTag(end);
            ultimo = "tag";
            endT = c;
        } else if (eTag && (beginT != null) && (begin != null) && ultimo.equalsIgnoreCase("tag")) {
            endT.setNextWithTag(c);
            c.setPreviousWithTag(endT);
            endT = c;
            ultimo = "tag";
        } else if (!eTag && (beginT != null) && (begin != null) && ultimo.equalsIgnoreCase("notag")) {
            end.setNext(c);
            c.setPrevious(end);
            end = c;
            ultimo = "notag";
        } else if (!eTag && (beginT != null) && (begin != null) && ultimo.equalsIgnoreCase("tag")) {
            endT.setNextWithTag(c);
            end.setNext(c);
            c.setPreviousWithTag(endT);
            c.setPrevious(end);
            end = c;
            ultimo = "notag";
        }
    }

    public void insertWord1(String word, String ptype) {

        // Cria um novo nó
        ContentList wd = new ContentList();

        // seta o token
        wd.setToken(word);
        wd.setProbType(ptype);
        count++;

        if (begin == null) {    // Lista vazia

            // seta o inicio e o fim da lista pro nó criado
            if (!ptype.equalsIgnoreCase("tag")) {
                begin = wd;
                end = wd;
            } else {
                beginT = wd;
                endT = wd;
            }
        } else {

            // seta o prox do ultimo pra wd
            if (!ptype.equalsIgnoreCase("tag")) {
                if (!end.getProbType().equalsIgnoreCase("tag")) {
                    end.setNext(wd);

                    // seta o anterior de wd pra end
                    wd.setPrevious(end);
                    wd.setNext(null);

                    if (end.getNextWithTag() != end.getNext()) {
                        ContentList aux = end.getPrevious();

                        while ((aux != wd) && aux.getProbType().equalsIgnoreCase("tag")) {
                            aux = aux.getNext();
                        }

                        wd.setPreviousWithTag(aux);
                        aux.setNextWithTag(end);
                    }

                    // end passa a ser wd
                    end = wd;
                }
            } else {

                // //system.out.println("Inseriu Tag" + wd.getToken());
                if (!end.getProbType().equalsIgnoreCase("tag")) {
                    end.setNextWithTag(wd);
                    wd.setPreviousWithTag(end);
                } else {
                    endT.setNextWithTag(wd);
                    wd.setNextWithTag(endT);
                }
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
