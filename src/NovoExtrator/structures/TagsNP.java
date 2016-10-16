
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

//~--- JDK imports ------------------------------------------------------------
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author cristiano
 */
public class TagsNP extends SuperClasse {

    private LinkedList<String> s = null;
    private String nome = "";

    public TagsNP(String nome) {
        this.nome = nome;
    }

    public TagsNP() {
        s = new LinkedList();
    }

    public void insert(String g) {
        s.add(g);
    }

    public LinkedList getTags() {
        return (LinkedList) s.clone();
    }

    public String printTags() {
        if (s.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String g : s) {
            sb.append(g + " ");
        }

        return sb.toString();
    }

    public boolean contains(Object o) {
        if (o instanceof String) {
            for (String str : this.s) {
                if (((String) o).equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }
        return false;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
