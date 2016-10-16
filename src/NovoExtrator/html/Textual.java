/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.html;

/**
 *
 * @author cristiano
 */
public class Textual extends Element{

    public Textual(String texto) {
        this.texto = texto;
    }
    private String texto;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
