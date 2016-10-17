/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.filehandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Patterns {

    private LinkedList<LinkedList<String>> patterns;

    /**
     * Patterns' default constructor.
     */
    public Patterns() {
        patterns = new LinkedList();
    }

    /**
     * This method open the file where there is patterns, and save them in an
     * internal structure.
     *
     * @param filename File's name where there is patterns.
     */
    public void openFileAndLoadPatterns(String filename) {
        try {
            String[] vetPat;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(
                    new File(filename))));


            while (in.ready()) {
                LinkedList<String> l = new LinkedList();
                vetPat = (in.readLine()).split(" ");
                for (String s : vetPat) {
                    l.add(s);
                }
                patterns.add(l);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
