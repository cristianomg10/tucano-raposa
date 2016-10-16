package pesquisagoogle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe para lidar com arquivos.
 *
 * @author Armando
 */
public class Files {

    public static int fileSizeInMB(String path) {
        File file = new File(path);
        return (int) (file.length() / (1024 * 1024));
    }

    /**
     * Recebe o caminho de um arquivo e retorna este arquivo em uma String.
     *
     * @param path Local onde está o arquivo.
     * @return
     */
    public static String stringFromFile(String path) {
        StringBuilder stringFile = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String tempLine = br.readLine();

            while (tempLine != null) {
                stringFile.append(tempLine);
                stringFile.append("\n");
                tempLine = br.readLine();
            }
        } catch (FileNotFoundException ex) {
//            System.out.println("Erro ao ler arquivo: " + path);
        } catch (IOException ex) {
//            System.out.println("erro.\n");
        }

        return stringFile.toString();

    }

    /**
     * Salva a string em um arquivo txt.
     *
     * @param archive A string a ser salva.
     * @param path O local onde a string será salva.
     * @throws IOException
     */
    public static void writeFile(String stringFile, String path) throws IOException {
        if (path.length() > 254) {
            path = path.substring(0, 254);
        }
        System.out.println(path);
        FileWriter fw = new FileWriter(new File(path));
        fw.write(stringFile);
        fw.close();
    }

    /**
     * Lê o arquivo no formato Parsed Search passado no caminho path gerando um
     * ArrayList de objetos WebPage das páginas contidas no arquivo
     *
     * @param path O local onde se encontra o arquivo.
     * @return
     */
    public static ArrayList<WebPage> readParsedSearchFile(String path) {
        WebPage page = null;
        String title = null;
        String link = null;
        String snippet = null;
        String htmlPath = null;
        ArrayList<WebPage> pages = new ArrayList<WebPage>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int contadorLinha = 1;

            while (line != null) {
                if (contadorLinha == 1) {
                    title = line;
                } else if (contadorLinha == 2) {
                    link = line;
                } else if (contadorLinha == 3) {
                    snippet = line;
                } else if (contadorLinha == 4) {
                    htmlPath = line;
                } else {
                    contadorLinha = 0;
                    page = new WebPage(title, link, snippet, htmlPath);
                    pages.add(page);
                }
                line = br.readLine();
                contadorLinha++;
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado.\n");
        } catch (IOException ex) {
            System.out.println("erro.\n");
        }

        return pages;
    }

    /**
     * Lê o arquivo de entrada padrão dos pesquisadores e retorna os
     * pesquisadores em um ArrayLis.
     *
     * @return Os pesquisadores contidos no arquivo padrão.
     */
    public static ArrayList<Pesquisador> leArquivoEntrada(String path) {

//        String path = "./Arquivos/Entrada/" + fileName;

        Pesquisador pesq = null;
        String nome;
        String publicacao;
        ArrayList<Pesquisador> Pesquisadores = new ArrayList<Pesquisador>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if (line.contains("<Nome>")) {
                    nome = line.substring(line.indexOf("<Nome>") + ("<Nome> ").length(), line.indexOf(" </Nome>"));
                    pesq = new Pesquisador(nome);
                } else if (line.contains("<Pub>")) {
                    publicacao = line.substring(line.indexOf("<Pub>") + ("<Pub> ").length(), line.indexOf(" </Pub>"));
                    pesq.getPublications().add(publicacao);
                } else if (line.contains("</Pesquisador>")) {
                    Pesquisadores.add(pesq);
                }
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }

        } catch (FileNotFoundException ex) {
            System.out.println("erro! arquivo nao encontrado.");
        } catch (IOException ex) {
            System.out.println("erro!");
        } catch (StringIndexOutOfBoundsException ex) {
            System.out.println("formato invalido.");
        }

        return Pesquisadores;
    }

    public static boolean checkIfFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}
