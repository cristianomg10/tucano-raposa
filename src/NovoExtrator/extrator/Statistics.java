/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.extrator;

import NovoExtrator.structures.SuperClasse;
import NovoExtrator.extrator.Citations.DataCitation;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 */
public class Statistics {
    
    public class Quicksort  {
	private int[] numbers;
	private int number;

	public void sort(int[] values) {
		// Check for empty or null array
		if (values ==null || values.length==0){
			return;
		}
		this.numbers = values;
		number = values.length;
		quicksort(0, number - 1);
	}

	private void quicksort(int low, int high) {
		int i = low, j = high;
		// Get the pivot element from the middle of the list
		int pivot = numbers[low + (high-low)/2];

		// Divide into two lists
		while (i <= j) {
			// If the current value from the left list is smaller then the pivot
			// element then get the next element from the left list
			while (numbers[i] < pivot) {
				i++;
			}
			// If the current value from the right list is larger then the pivot
			// element then get the next element from the right list
			while (numbers[j] > pivot) {
				j--;
			}

			// If we have found a values in the left list which is larger then
			// the pivot element and if we have found a value in the right list
			// which is smaller then the pivot element then we exchange the
			// values.
			// As we are done we can increase i and j
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}
		// Recursion
		if (low < j)
			quicksort(low, j);
		if (i < high)
			quicksort(i, high);
	}

	private void exchange(int i, int j) {
		int temp = numbers[i];
		numbers[i] = numbers[j];
		numbers[j] = temp;
	}
    } 
    
    private int[] ordena(int[] a){
        Quicksort q = new Quicksort();
        q.sort(a);
        return q.numbers;
    }
    
    public int getModa(Citations c){
        if (c.getCitations().size() == 0) return 0;
        int[] values = new int[c.getCitations().size()];
        int i = 0;
        
        for (DataCitation dc: c.getCitations()){
            values[i] = dc.getEnd() - dc.getBegin();
            i++;
        }
        
        values = ordena(values);
        return values[(int)(values.length / 2)];
        
    }
    
    public void normaliza(LinkedList<SuperClasse> wl, Citations c, int percentSup, int percentInf, int nIdent, int moda){
    
        DataCitation dc;
        if (c.getCitations().size() == 0) return;

        for (int i = 0; i < c.getCitations().size(); i++){
            dc = c.getCitations().get(i);
            int notype = 0;
            
//            if (notype > ((dc.getFim() - dc.getInicio()) * (nIdent/100))){
//                c.getCitations().remove(i);
//            }
//            else
            //{
                if ((dc.getEnd() - dc.getBegin()) > (moda * percentSup/100)) {
                    c.getCitations().remove(i);
                    continue;
                }
                if ((dc.getEnd() - dc.getBegin()) < (moda * percentInf/100)) {
                    c.getCitations().remove(i);
                    continue;
                }
                
            //}
            
//            for (int j = dc.getInicio(); j < dc.getFim(); j++){
//                if (wl.get(j) instanceof TokenNP && 
//                        (((TokenNP) wl.get(j)).getTag().equals("") || 
//                        ((TokenNP) wl.get(j)).getTag().equals("notype"))){
//                    notype++;
//                }
//                
//
//            }
        } 
    }
    
}
