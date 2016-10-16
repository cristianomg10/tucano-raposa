/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.filehandlers;

import NovoExtrator.filehandlers.FormatsCalculus.AuxStructure.EachOne;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * This set of classes exists to help the algorithm to deduces the right type of
 * blocks. The parent class (FormatsCalculus) just involves the class
 * AuxStructure, which in its turn, involves the class EachOne.
 *
 * FormatsCalculus is all the structure that contains the simple probability of
 * a block of tokens to be of a specific type.
 *
 * AuxStructure is the structure that contains all the next types of block for
 * each type.
 *
 * EachOne is the structure that contains data about the types in relation to
 * the type in question, such as current type, counter of occurrences and
 * probability.
 *
 * @author cristiano
 */
public class FormatsCalculus {

    private HashMap<String, AuxStructure> structure = new HashMap();

    public class AuxStructure {

        private int counter = 0;
        private LinkedList<EachOne> eO = new LinkedList();

        public class EachOne {

            private int counter = 0;
            private Double probability = 0.0;
            private String type = "";

            /**
             * Increments the element's counter.
             */
            public void incCounter() {
                counter++;
            }

            /**
             *
             * @return the element's counter.
             */
            public int getCounter() {
                return counter;
            }

            /**
             *
             * @return the element's type.
             */
            public String getType() {
                return type;
            }

            /**
             *
             * @return the element's probability.
             */
            public Double getProbability() {
                return probability;
            }

            /**
             * Set the element's probability to the probability param's value.
             *
             * @param probability is the new probability value.
             */
            public void setProbability(Double probability) {
                this.probability = probability;
            }

            /**
             * Constructor.
             *
             * @param t is the element's type.
             */
            public EachOne(String t) {
                type = t;
                counter = 1;
            }
        }

        public void add(String sequence) {
            for (EachOne e : eO) {
                if (e.getType().equalsIgnoreCase(sequence)) {
                    e.incCounter();
                    return;
                }
            }
            eO.add(new EachOne(sequence));
        }

        public void calculateProb() {
            int count = 0;

            for (EachOne e : eO) {
                count += e.getCounter();
            }

            for (EachOne e : eO) {
                e.setProbability(Double.valueOf(Double.valueOf(e.getCounter()) / Double.valueOf(count)));
            }
        }

        public AuxStructure(String sequence) {
            eO.add(new EachOne(sequence));
        }

        public LinkedList<EachOne> getStructure() {
            return this.eO;
        }
    }

    public String getTheMostProbable(String original) {
        Double max = 0.0;
        String success = "";

        AuxStructure as = structure.get(original);
        if (as == null) {
            return null;
        }

        for (EachOne e : as.getStructure()) {
            if (max < e.getProbability()) {
                max = e.getProbability();
                success = e.getType();
            }
        }

        return success;
    }

    private void insertOnHash(String first, String sequence) {
        if (!structure.containsKey(first)) {
            structure.put(first, new AuxStructure(sequence));
        } else {
            AuxStructure x = structure.get(first);
            x.add(sequence);
        }
    }

    public void loadStructureWithFormatsAndCalculate(Formats f) {
        for (LinkedList<String> ll : f.getFormats()) {
            for (int i = 0; i < ll.size() - 1; i++) {
                insertOnHash(ll.get(i), ll.get(i + 1));
            }
        }

        Set<String> s = this.structure.keySet();
        for (String ss : s) {
            AuxStructure x = structure.get(ss);
            x.calculateProb();
        }
    }

    public String printHash() {
        Set<String> s = this.structure.keySet();
        StringBuilder sb = new StringBuilder();


        for (String ss : s) {
            AuxStructure x = structure.get(ss);
            sb.append(ss).append("\n");

            for (EachOne e : x.getStructure()) {
                sb.append("\t").append(e.getType()).append("(").append(e.getProbability()).append(")").append("\n");
            }
        }

        return sb.toString();
    }

    public HashMap<String, AuxStructure> getMap() {
        return structure;
    }
}
