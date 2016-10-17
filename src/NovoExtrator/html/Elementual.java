/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.html;

/**
 *
 */
public class Elementual extends Element{
    private Element element;
    private String name;

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Elementual(String name){
        this.name = name;
    }
}
