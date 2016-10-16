/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NovoExtrator.extrator;

import java.util.LinkedList;

/**
 * This class will contain informations about citations.
 * @author cristiano
 */
public class Citations {
    private LinkedList<DataCitation> citations;

    public class DataCitation{
        private int inicio, fim;

        public int getSize(){
            return fim - inicio;
        }
        
        public int getEnd() {
            return fim;
        }

        public void setEnd(int fim) {
            this.fim = fim;
        }

        public int getBegin() {
            return inicio;
        }

        public void setBegin(int inicio) {
            this.inicio = inicio;
        }

        public DataCitation(int begin, int end){
            inicio = begin;
            fim = end;
        }

        public DataCitation(){
            inicio = 0;
            fim = 0;
        }        
    }

    public Citations(){
        this.citations = new LinkedList();
    }

    public void insert(int begin, int end){
        this.citations.add(new DataCitation(begin, end));
    }

    public LinkedList<DataCitation> getCitations(){
        return this.citations;
    }

    public void fixDoubleCits(){
        DataCitation d1, d2;
        
        for (int i = 0; i < citations.size() -1; i++){
            for (int j = i + 1; j < citations.size(); j++){
                
                d1 = citations.get(i);
                d2 = citations.get(j);
                if (d1.getEnd() == d2.getEnd() && d1.getSize() <= d2.getSize()){
                    citations.remove(j);
                }else if (d1.getEnd() == d2.getEnd() && d1.getSize() >= d2.getSize()){
                    citations.remove(i);
                }
            }
        }
    }
}
