package NovoExtrator.filehandlers;

//~--- JDK imports ------------------------------------------------------------
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.LinkedList;
import NovoExtrator.structures.HashToken;

/**
 * Class Formats. Its functionality is to load formats of citations from file.
 *
 */
public class Formats {

    private LinkedList<LinkedList<String>> formats = new LinkedList();

    /**
     * This method insert a String into the formats list
     *
     * @param text String to be inserted
     */
    private void insert(String text) {
        String[] result = text.split(" ");
        LinkedList<String> formato = new LinkedList();

        for (int i = 0; i < result.length; ++i) {
            formato.add(result[i]);
        }

        formats.add(formato);
    }

    public void openFileAndLoadFormats(String filename) throws FileNotFoundException, IOException {
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        while (in.ready()) {
            insert(in.readLine());
        }
    }

    public LinkedList<LinkedList<String>> getFormats() {
        return (LinkedList<LinkedList<String>>) formats.clone();
    }

    public String printFormats() {
        StringBuilder sb = new StringBuilder();

        for (LinkedList<String> formato : formats) {
            for (String s : formato) {
                sb.append(s);
                sb.append(" ");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public int length() {
        return formats.size();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
