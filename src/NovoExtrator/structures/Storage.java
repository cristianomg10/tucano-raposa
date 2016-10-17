/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

import java.util.LinkedList;

/**
 *
 */
public class Storage {

    LinkedList<PCitation> storage;

    public Storage() {
        storage = new LinkedList();

    }

    public LinkedList<PCitation> getStorage() {
        return storage;
    }

    public void insertIntoStorage(int begin, int end, String filename) {
        PCitation p = new PCitation(begin, end, filename);
        storage.add(p);
    }

    public LinkedList<Integer> calculateDistance() {

        LinkedList<Integer> dist = new LinkedList();



        restart:
        for (int i = 1, j = 0; i < storage.size() - 1; i++, j++) {
            //System.out.println(storage.get(i).getBegin() - storage.get(i-1).getEnd());
            //if (storage.get(i).getBegin() - storage.get(i - 1).getEnd() > 100){
            dist.add(storage.get(i).getBegin() - storage.get(i - 1).getEnd());
            //storage.remove(i - 1);
            //continue restart;

            //}
        }

        return dist;
    }

    public void calculateDistance(Storage closer, Storage nonCloser, String filename) {
        for (int i = 1, j = 0; i < storage.size(); i++, j++) {
            int diff = storage.get(i).getBegin() - storage.get(i - 1).getEnd();
            if (diff < 10 && (storage.get(i - 1).getEnd() > storage.get(i - 1).getBegin())) {
                closer.insertIntoStorage(storage.get(i - 1).getBegin(), storage.get(i - 1).getEnd(), filename);
            } else {
                nonCloser.insertIntoStorage(storage.get(i - 1).getBegin(), storage.get(i - 1).getEnd(), filename);
            }
        }

        int i = storage.size() - 1;

        if (i > 1) {
            if (storage.get(i).getBegin() - storage.get(i - 1).getEnd() < 10) {
                closer.insertIntoStorage(storage.get(i).getBegin(), storage.get(i).getEnd(), filename);
            } else {
                nonCloser.insertIntoStorage(storage.get(i).getBegin(), storage.get(i).getEnd(), filename);
            }
        }

    }
}
