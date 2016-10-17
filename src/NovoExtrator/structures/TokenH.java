
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

//~--- JDK imports ------------------------------------------------------------
import java.io.Serializable;

/**
 *
 */
public class TokenH implements Serializable {

    private int count = 0;
    private String type = new String();

    public TokenH() {
    }

    public TokenH(String type) {
        this.type = type;
    }

    public TokenH(String type, int count) {
        this.type = type;
        this.count = count;
    }

    /**
     *
     * @param type will define the object's type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return the object's type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * increments the counter
     */
    public void incAttribute() {
        ++count;
    }

    /**
     *
     * @return the count of token of this type.
     */
    public int getAttributeCount() {
        return count;
    }

    public void setAttributeCount(int newcount) {
        this.count = newcount;
    }

    @Override
    public boolean equals(Object t) {
        if (t instanceof TokenH) {
            if (this.getType().trim().equalsIgnoreCase(((TokenH) t).getType().trim())) {
                return true;
            }

            // if (FileHandler.isNumber(t) && FileHandler.isNumber(this.)
        }

        return false;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
