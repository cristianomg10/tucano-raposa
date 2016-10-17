
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

/**
 *
 */
public class TokenNP extends SuperClasse {

    private String tag;
    private String token;

    public TokenNP(String token, String tag) {
        this.token = token;
        this.tag = tag;
    }

    public String getToken() {
        return token.trim();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
