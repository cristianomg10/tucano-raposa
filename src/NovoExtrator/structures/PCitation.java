/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

/**
 *
 * @author cristiano
 */
public class PCitation {

    private int begin;
    private int end;
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public PCitation(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public PCitation(int begin, int end, String file) {
        this.begin = begin;
        this.end = end;
        this.file = file;
    }

    public PCitation() {
        this.begin = 0;
        this.end = 0;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}