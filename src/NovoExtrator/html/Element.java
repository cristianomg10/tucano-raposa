/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.html;

import java.util.LinkedList;

/**
 * 
 */
public class Element {

    private Element father = null;
    private Element currentTag = this;
    private String tag = null;
    private LinkedList<Element> l = new LinkedList();

    public LinkedList<Element> getL() {
        return (LinkedList<Element>) l.clone();
    }

    public String getTag() {
        return tag;
    }

    public Element getFather() {
        return father;
    }

    public void setTag(String t) {
        tag = t;
    }

    public void setFather(Element father) {
        this.father = father;
    }

    public void insertText(String text) {
        if (currentTag == this) {
            Textual t = new Textual(text);
            t.setTag(this.tag);
            l.add(t);
        } else {
            currentTag.insertText(text);
        }
    }

    public void insertTag(String tag) {
        Elementual e = new Elementual(tag);
        e.setFather(this);
        e.setTag(tag);
        currentTag = e;
        l.add(e);
        /*
         Element e = new Element();
         e.setTag(tag);
         e.setFather(this);
         currentTag = e;
         l.add(e);*/
    }

    public void closeTag() {
        currentTag = currentTag.getFather();
    }

    public void clear() {
        l.clear();
    }
}
