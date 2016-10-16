package pesquisagoogle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Classe para realizar pesquisas no google usando a API Custom Search fornecida
 * pelo google.
 *
 * @author armando
 */
public class PesquisaGoogle {

  public static String toUrlEnconding(String str) {
    str = str.replaceAll(" ", "%20");
    str = str.replaceAll("!", "%21");
    str = str.replaceAll("\"", "%22");
    str = str.replaceAll("#", "%23");
    str = str.replaceAll("$", "%24");
//        str = str.replaceAll("%", "%25");
    str = str.replaceAll("&", "%26");
    str = str.replaceAll("'", "%27");
    str = str.replaceAll("\\(", "%28");
    str = str.replaceAll("\\)", "%29");
    str = str.replaceAll("\\*", "%2A");
//        str = str.replaceAll("\\+", "%2B");
    str = str.replaceAll(",", "%2C");
    str = str.replaceAll("-", "%2D");
    str = str.replaceAll("\\.", "%2E");
    str = str.replaceAll("/", "%2F");
    str = str.replaceAll(":", "%3A");
    str = str.replaceAll(";", "%3B");
    str = str.replaceAll("<", "%3C");
    str = str.replaceAll("=", "%3D");
    str = str.replaceAll(">", "%3E");
    str = str.replaceAll("\\?", "%3F");
    str = str.replaceAll("@", "%40");
    str = str.replaceAll("\\[", "%5B");
    str = str.replaceAll("\\\\", "%5C");
    str = str.replaceAll("]", "%5D");
    str = str.replaceAll("\\^", "%5E");
    str = str.replaceAll("_", "%5F");
    str = str.replaceAll("`", "%60");
    str = str.replaceAll("\\{", "%7B");
    str = str.replaceAll("\\|", "%7C");
    str = str.replaceAll("}", "%7D");
    str = str.replaceAll("~", "%7E");
    str = str.replaceAll("‚", "%82");
    str = str.replaceAll("à", "%E0");
    str = str.replaceAll("á", "%E1");
    str = str.replaceAll("â", "%E2");
    str = str.replaceAll("ã", "%E3");
    str = str.replaceAll("ä", "%E4");
    str = str.replaceAll("å", "%E5");
    str = str.replaceAll("ç", "%E7");
    str = str.replaceAll("è", "%E8");
    str = str.replaceAll("é", "%E9");
    str = str.replaceAll("ê", "%EA");
    str = str.replaceAll("ë", "%EB");
    str = str.replaceAll("ì", "%EC");
    str = str.replaceAll("í", "%ED");
    str = str.replaceAll("î", "%EE");
    str = str.replaceAll("ï", "%EF");
    str = str.replaceAll("ð", "%F0");
    str = str.replaceAll("ñ", "%F1");
    str = str.replaceAll("ò", "%F2");
    str = str.replaceAll("ó", "%F3");
    str = str.replaceAll("ô", "%F4");
    str = str.replaceAll("õ", "%F5");
    str = str.replaceAll("ö", "%F6");
    str = str.replaceAll("ù", "%F9");
    str = str.replaceAll("ú", "%FA");
    str = str.replaceAll("û", "%FB");
    str = str.replaceAll("ü", "%FC");
    return str;
  }

  public static String removeAccents(String str) {
    str = str.replaceAll("[ÂÀÁÄÃ]", "A");
    str = str.replaceAll("[âãàáä]", "a");
    str = str.replaceAll("[ÊÈÉË]", "E");
    str = str.replaceAll("[êèéë]", "e");
    str = str.replaceAll("[ÎÍÌÏ]", "I");
    str = str.replaceAll("[îíìï]", "i");
    str = str.replaceAll("[ÔÕÒÓÖ]", "O");
    str = str.replaceAll("[ôõòóö]", "o");
    str = str.replaceAll("[ÛÙÚÜ]", "U");
    str = str.replaceAll("[ûúùü]", "u");
    str = str.replaceAll("Ç", "C");
    str = str.replaceAll("ç", "c");
    return str;
  }

  public static boolean checkURL(String link) {
    try {

      URL url = new URL(link);
      HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
      httpUrlConnection.setRequestMethod("HEAD");
      httpUrlConnection.connect();
      int code = httpUrlConnection.getResponseCode();
      if (code == 200) {
        String content = httpUrlConnection.getContentType();
//                System.out.println(content);
        if (content.contains("text/html")) {
          return true;
        }
      }
      return false;
    } catch (Exception ex) {
      return false;
    }
  }

  public static String getCharset(String url) {
    try {
      String charset = "";
      Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
      charset = doc.outputSettings().charset().toString();
      return charset;
    } catch (IOException ex) {
      return "";
    }
  }

  /**
   * Método para realizar uma pesquisa à partir da query passada como parâmetro,
   * processar o resultado da pesquisa e então salvá-la no local passado como
   * parâmetro em um arquivo .txt, nesse processamento são salvas as seguintes
   * informações das páginas do resultado: título da página, snippet da página,
   * link da página e o local onde o arquivo .html da página foi salvo.
   *
   * @param query Query para ser feita a pesquisa.
   * @param pageIndex Índice da página da pesquisa, é incrementado de 10 para
   * avançar a página, ou seja, 1 página 1, 11 página 2.
   * @param path O local onde o resultado analisado da pesquisa é salvo.
   * @param keyID ArrayList contendo a key (posicao 0) e a ID (posicao 1) da
   * GoogleSearchAPI
   * @throws IOException
   */
  public static void parsedSearch(String query, int pageIndex, String path, ArrayList<String> keyID) throws IOException {

    String queryHtmlEnconding = removeAccents(query);
    String key = keyID.get(0);
    String engineID = keyID.get(1);

    String startResult = "&start=" + pageIndex;
    String formattedURL;
    formattedURL = "https://www.googleapis.com/customsearch/v1?key=" + key + "&cx=" + engineID + "&q=" + queryHtmlEnconding + startResult;

    URL url = new URL(formattedURL);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

    ArrayList<WebPage> pages = parseBufferedReader(br, path, query);

    connection.disconnect(); // Não posso fechar a conexão antes de usar o BufferedReader

    StringBuilder str = new StringBuilder();
    String pathToFileHtml;

    for (WebPage pg : pages) {

      str.append(pg.getTitle());
      str.append("\n");
      str.append(pg.getLink());
      str.append("\n");
      str.append(pg.getDescription());
      str.append("\n");
      // Nome do caminho do arquivo da página a ser salvo.
      pathToFileHtml = path + pg.getTitle() + ".html";
      str.append(pathToFileHtml);
      pg.setPath(pathToFileHtml);
      str.append("\n");
      str.append("\n");

    }

    Files.writeFile(str.toString(), path + query + "--ParsedSearch.txt");

    savePages(pages);

  }

  /**
   * Método para processar o BufferedReader (resultado JSON) retornado pela
   * pesquisa à API e retornar as informações das páginas contidas no resultado
   * em objetos WebPage, o arquivo JSON da pesquisa também é salvo em um arquivo
   * .json.
   *
   * @param br O BufferedReader que contém o resultado da pesquisa em formato
   * JSON.
   * @param path O caminho onde JSON será salvo como um arquivo .txt.
   * @param query A query usada na pesquisa.
   * @return Um ArrayList contendo os objetos WebPage das páginas contidas no
   * resultado da pesquisa.
   * @throws IOException
   */
  public static ArrayList<WebPage> parseBufferedReader(BufferedReader br, String path, String query) throws IOException {
    String output;
    String title = new String();
    String link = new String();
    String snippet = new String();
    StringBuilder resultadoTodo = new StringBuilder();
    boolean flagItems = false;
    boolean flagMetatags = false;
    ArrayList<WebPage> pages = new ArrayList<WebPage>();

    while ((output = br.readLine()) != null) {
      resultadoTodo.append(output + "\n ");
      if (flagItems && !flagMetatags && output.contains("\"title\": \"") && output.contains("\",")) {
        title = output.substring(output.indexOf("\"title\": \"") + ("\"title\": \"").length(), output.indexOf("\","));
      } else if (flagItems && !flagMetatags && output.contains("\"link\": \"")) {
        link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(), output.indexOf("\","));
      } else if (flagItems && !flagMetatags && output.contains("\"snippet\": \"")) {
        snippet = output.substring(output.indexOf("\"snippet\": \"") + ("\"snippet\": \"").length(), output.indexOf("\","));
        WebPage page = new WebPage(title, link, snippet);
        pages.add(page);
      } else if (output.contains("\"metatags\": [")) {
        flagMetatags = true;
      } else if (flagItems && output.contains("},")) {
        flagMetatags = false;
      } else if (output.contains("\"items\": [")) {
        flagItems = true;
      }
    }
    Files.writeFile(resultadoTodo.toString(), path + "--" + query + "--ResultadoBruto.json");
    return pages;
  }

  /**
   * Salva o resultado da pesquisa como retornado pela API Custom Search (JSON)
   * sem fazer nenhum processamento.
   *
   * @param query A query a ser usada na pesquisa.
   * @param pageIndex Índice da página da pesquisa, é incrementado de 10 para
   * avançar a página, ou seja, 1 página 1, 11 página 2.
   * @param path Local onde será salvo o resultado da pesquisa.
   * @throws IOException
   */
  public static void saveSearch(String query, int pageIndex, String path) throws IOException {
    String key = "AIzaSyC7iDOdUDDC8bbcTLzp1VMVwBW9sbZQaS0";
    String engineID = "017213267132652376375:g5ftzanm1sk";
    String startResult = "&start=" + pageIndex;
    String formattedURL;
    formattedURL = "https://www.googleapis.com/customsearch/v1?key=" + key + "&cx=" + engineID + "&q=" + query + startResult;
    URL url = new URL(formattedURL);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

    String output;
    String archive = new String();
    while ((output = br.readLine()) != null) {
      archive = archive + output + "\n";
    }

    Files.writeFile(archive, path + "--ResultadoBruto.json");

    connection.disconnect(); // Não posso fechar a conexão antes de usar o BufferedReader

  }

  /**
   * Método para pesquisar um pesquisador.
   *
   * @param pesq O pesquisador que será pesquisado.
   * @param keyID ArrayList contendo a key (posicao 0) e a ID (posicao 1) da
   * GoogleSearchAPI
   */
  public static void searchPesquisador(Pesquisador pesq, ArrayList<String> keyID) {
    ArrayList<String> queries = pesq.geraQueriesPadrao();
    String nome = pesq.getName().replace(" ", "");
    String path = "./Resultados/PesquisaGoogle/" + nome + "/";
    File dir = new File(path);
    boolean directoryResult = dir.mkdir(); // directoryResult diz se o diretório já existe ou não, por ora não é usado

    for (String query : queries) {
      try {
        parsedSearch(query, 1, path, keyID);
      } catch (IOException ex) {
      }
    }

  }

  /**
   * Salva o arquivo .html das páginas contidas nos objetos WebPage dentro do
   * ArrayList, o local onde as páginas são salvas está contido no campo path
   * das páginas.
   *
   * @param pages ArrayList que contém as páginas,
   * @throws IOException
   */
  public static void savePages(ArrayList<WebPage> pages) throws IOException {

    URL page;

    for (WebPage pg : pages) {
      if (checkURL(pg.getLink())) {
        savePage(pg.getLink(), pg.getPath());
      }
//            if (pg.getLink().contains(".pdf")) {
//                //path = "./Resultados/PDF" + pg.getTitle() + ".html";
//                //lePdf(path, pg.getLink());
//            } else {
//                savePage(pg.getLink(), pg.getPath());
//            }
    }

  }

  /**
   * Método para salvar uma página à partir da url passada como parâmetro.
   *
   * @param url A url da página que terá seu arquivo .html salvo.
   * @param path Local onde será salvo o arquvio .html da página,
   * @throws IOException
   */
  public static void savePage(String url, String path) throws IOException {

    URL page = new URL(url);

    URLConnection connection = page.openConnection();
    HttpURLConnection conn = (HttpURLConnection) connection;
    conn.addRequestProperty("User-Agent", "Mozilla/21.0");

    String charset = getCharset(url);
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
    StringBuilder str = new StringBuilder();
    String inputLine;

    while ((inputLine = in.readLine()) != null) {
      str.append(inputLine);
      str.append("\n");
    }

    in.close();
    if (!Files.checkIfFileExists(path)) {
      Files.writeFile(str.toString(), path);
    }

  }
}
