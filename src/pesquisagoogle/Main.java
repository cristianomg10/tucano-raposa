package pesquisagoogle;

import NovoExtrator.Desambiguacao.Desambiguacao;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author armando
 */
public class Main {

  public static void main(String[] args) throws IOException {

    if (args.length != 2) {
      System.out.println("Input in wrong format. Example: ./projectname input.xml keyid.txt");
    }

    ArrayList<String> keyID = new ArrayList<>();
    for (String string : Files.stringFromFile(args[1]).split("\n")) {
      keyID.add(string);
    }

    boolean isCitationPage = false;
    File folder;
    File[] listOfFiles;

    ArrayList<Pesquisador> pesquisadores = Files.leArquivoEntrada(args[0]);
    ArrayList<WebPage> paginas;
    System.out.println(pesquisadores.get(0));
    String nome;
    Processamento processamento = new Processamento();
    for (Pesquisador pesquisador : pesquisadores) {

      //Search the researcher in the web using GoogleSearch API
      PesquisaGoogle.searchPesquisador(pesquisador, keyID);
      // Load the pages (title, snippet, link) returned by the search
      paginas = new ArrayList<>();
      nome = pesquisador.getName().replace(" ", "");
      folder = new File("./Resultados/PesquisaGoogle/" + nome + "/");
      listOfFiles = folder.listFiles();
      processamento.setPesquisador(pesquisador);
      for (File file : listOfFiles) {
        if (file.getName().contains("ParsedSearch")) {
          paginas = Files.readParsedSearchFile(file.getPath());
          for (WebPage webPage : paginas) {
            webPage.setContentFromPath();
          }
          for (WebPage webPage : paginas) {
            if ((webPage.getLink().contains(".pdf")) || webPage.getLink().contains(".pptx")
                    || (webPage.getContent() == null) || (webPage.getContent() == "")) {
              continue;
            } else {
              isCitationPage = processamento.isCitationPage(webPage);
            }
          }
        }
      }

    }

  }

}
