
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author cristiano
 */
public class HashToken extends Hashtable implements Serializable {

    private Hashtable<String, LinkedList<TokenH>> hash = new Hashtable();

    public HashToken() {
        hash.clear();
    }

    @Override
    public int size() {
        return hash.size();
    }

    @Override
    public boolean isEmpty() {
        return hash.isEmpty();
    }

    /**
     * @param tok
     * @return true if exists in HashTable.
     */
    public boolean exists(String tok) {
        if (hash.containsKey(tok.toLowerCase())) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param tok
     * @return the tokenlist founded in hashtable, with your features.
     */
    public LinkedList<TokenH> get(String tok) {
        tok = tok.toLowerCase();

        return (LinkedList<TokenH>) hash.get(tok);
    }

    /**
     *
     * @param tok is the token that will be added
     * @param t, this is, the token object (containing the type) will be added
     * to hashtable
     */
    public void put(String tok, TokenH t) {
        tok = tok.toLowerCase();

        // se já existe na hash
        if (hash.containsKey(tok)) {
            LinkedList<TokenH> aux = (LinkedList<TokenH>) hash.get(tok);

            boolean b = false;
            for (TokenH to : aux) {
                if (to.getType().equalsIgnoreCase(t.getType())) {
                    b = true;
                    if (t.getAttributeCount() != 0) {
                        to.setAttributeCount(to.getAttributeCount() + t.getAttributeCount());
                    } else {
                        to.incAttribute();
                    }
                    break;
                }
            }

            if (!b) {
                TokenH newT = new TokenH(t.getType());
                if (t.getAttributeCount() != 0) {
                    newT.setAttributeCount(t.getAttributeCount());
                } else {
                    newT.incAttribute();
                }
                aux.add(newT);
            }
        } else {    // se nao existe ainda na hash
            LinkedList<TokenH> r = new LinkedList<TokenH>();

            if (t.getAttributeCount() != 0) {
                t.setAttributeCount(t.getAttributeCount());
            } else {
                t.incAttribute();
            }
            r.add(t);
            hash.put(tok, r);
        }
    }

    /**
     * This method create a String containing all the keys of current hash.
     *
     * @return a String object containing all the keys of this current hash.
     */
    public String printData() {
        StringBuilder sb = new StringBuilder();
        LinkedList<String> al = new LinkedList<String>(hash.keySet());

        for (String a : al) {
            sb.append(a + " ");
            for (Iterator<TokenH> it = hash.get(a).iterator(); it.hasNext();) {
                TokenH l = it.next();
                sb.append("-> " + l.getType() + " " + l.getAttributeCount());
            }
            sb.append("\n");

        }

        sb.append("Total: " + al.size() + "\n");

        return sb.toString();
    }

    public boolean loadPreviousRecordedFileToHash(String fileName) {
        try {
            FileInputStream fIn;
            File arquivo = new File(fileName);

            ////system.out.println(arquivo.getAbsolutePath());
            //System.exit(0);
            if (!arquivo.exists()) {
//                System.out.println("entrou no if");
                arquivo.createNewFile();
                return false;
            }
//            System.out.println("nao entrou no if");
            fIn = new FileInputStream(arquivo);

            ObjectInputStream objIn = new ObjectInputStream(fIn);
//            System.out.println("depois");
            this.hash = (Hashtable<String, LinkedList<TokenH>>) objIn.readObject(); // AQUI SAI EXCEÇÃO
//            System.out.println("depois depois");
            ////system.out.println("Vish");
            return true;
        } catch (ClassNotFoundException e) {
        } catch (IOException ex1) {
            try {
//                System.out.println("EXCEÇÃO");
                File arquivo = new File(fileName);
                arquivo.createNewFile();
                return false;
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
            return false;


        }
        return false;

    }

    public boolean recordFileFromHash(String fileName) {
        File arquivo = new File(fileName);

        try {
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }

            FileOutputStream fOut = new FileOutputStream(arquivo);
            ObjectOutputStream objOut;


            objOut = new ObjectOutputStream(fOut);
            objOut.writeObject(this.hash);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public Hashtable getTable() {
        return this.hash;
    }

    public void removeTag(String tag) {
        Set<String> s = this.hash.keySet();
        LinkedList<String> sl = new LinkedList();
        sl.addAll(s);

        for (int i = 0; i < sl.size(); i++) {
            String z = sl.get(i);
            LinkedList<TokenH> l = this.hash.get(z);

            for (int j = 0; j < l.size(); j++) {
                ////system.out.println(l.get(j).getType() + " " + tag + l.get(j).getType().equalsIgnoreCase(tag));
                if (l.get(j).getType().equalsIgnoreCase(tag)) {
                    l.remove(j);
                }
            }

            if (l.size() == 0) {
                this.hash.remove(z);
            } else {
                this.hash.put(z, l);
            }
        }
    }

    public void remove(String token) {
        this.hash.remove(token);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
