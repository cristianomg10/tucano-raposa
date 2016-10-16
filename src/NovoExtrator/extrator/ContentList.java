
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package NovoExtrator.extrator;

/**
 *
 * @author cristiano
 */
public class ContentList {
    private ContentList ant     = null;
    private ContentList antTag  = null;
    private String      pType   = "";
    private ContentList prox    = null;
    private ContentList proxTag = null;    /* This field is used only whether the document is a HTML doc. */
    private String      token   = "";
    private int         range;             /* This field is used only in pattern search process */

    public ContentList getPreviousWithTag() {
        if (antTag != null) {
            return antTag;
        } else {
            return ant;
        }
    }

    public void setPreviousWithTag(ContentList prevTag) {
        this.antTag     = prevTag;
        prevTag.proxTag = this;
    }

    public ContentList getNextWithTag() {
        if (proxTag != null) {
            return proxTag;
        } else {
            return prox;
        }
    }

    public void setNextWithTag(ContentList nextTag) {
        this.proxTag   = nextTag;
        nextTag.antTag = this;
    }

    /**
     * This method set a new value for the range.
     * @param newRange is a new range of the object.
     */
    public void setRange(int newRange) {
        this.range = newRange;
    }

    /**
     * This method retrieve the object's range.
     * @return the current range.
     */
    public int getRange() {
        return this.range;
    }

    /**
     * This method set a new value for the token.
     * @param token is a new value for token.
     */
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setProbType(String pType) {
        this.pType = pType;
    }

    public String getProbType() {
        return this.pType;
    }

    public void setNext(ContentList next) {
        this.prox = next;
    }

    public ContentList getNext() {
        return this.prox;
    }

    public void setPrevious(ContentList prev) {
        this.ant = prev;
    }

    public ContentList getPrevious() {
        return this.ant;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
