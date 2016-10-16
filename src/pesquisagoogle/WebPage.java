package pesquisagoogle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Classe usada para representar uma página web.
 *
 * @author armando
 */
public class WebPage {

    private String title; // Título da página
    private String link; // Link da página
    private String description; // Description da página
    private String content; // Conteúdo da página (texto do arquivo .html)
    private String path; // Caminho do arquivo .html da página

    /**
     * Construtor padrão.
     */
    public WebPage() {
    }

    /**
     * Constrói um objeto WebPage à partir do título da página.
     *
     * @param url O título da página.
     */
    public WebPage(String url) {
        try {
            Document internetPage = Jsoup.connect(url).userAgent("Mozilla").get();
            this.setTitle(internetPage.title());
            this.setLink(url);
            String descript = "";
            Elements m2 = internetPage.select("meta");
            for (Element meta : m2) {
                if (meta.attr("name").equals("description")) {
                    description = meta.attr("content");
                }
            }
            this.setDescription(descript);
            String cont = internetPage.html();
            this.setContent(cont);
        } catch (IOException ex) {
            // Tratamento de excecao
        }

    }

    /**
     * Constrói um objeto WebPage à partir do título, link e description da
     * página.
     *
     * @param title O título da página.
     * @param link O link da página.
     * @param description O description da página.
     */
    public WebPage(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    /**
     * Constrói um objeto WebPage à partir do título, link, description e path
     * da página.
     *
     * @param title O título da página.
     * @param link O link da página.
     * @param description O description da página.
     * @param path O local onde foi salvo o arquivo .html da página.
     */
    public WebPage(String title, String link, String description, String path) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.path = path;
    }

    /**
     * Método para retornar o título do objeto página.
     *
     * @return O título da página.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Método para retornar o título da página à partir do conteúdo html dela.
     *
     * @return O título da página.
     */
    public String getTitleFromHTML() {
        String title = "";
        if (content != null) {
            Document docLk = Jsoup.parse(content);
            Elements links = docLk.select("head").select("*");
            for (Element el : links) {
                if (el.nodeName().equals("title")) {
                    title = el.text();
//                    System.out.println(title);
                }
            }
        }
        return title;
    }

    /**
     * Método para definir o título da página.
     *
     * @param title O título da página.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Método para retornar o link da pálgina.
     *
     * @return O link da página.
     */
    public String getLink() {
        return link;
    }

    /**
     * Método para definir o link da página.
     *
     * @param link O link da página.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Método para retornar o description da página.
     *
     * @return O description da página.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método para definir o description da página.
     *
     * @param description O description da página.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Método para retornar o conteúdo da página.
     *
     * @return O conteúdo da página.
     */
    public String getContent() {
        return content;
    }

    /**
     * Método para definir o conteúdo da página.
     *
     * @param conteudo O conteúdo da página.
     */
    public void setContent(String conteudo) {
        this.content = conteudo;
    }

    /**
     * Método para definir o conteúdo da página à partir do arquivo .html salvo
     * no local armazenado na variável path.
     *
     * @throws FileNotFoundException
     */
    public void setContentFromPath() throws FileNotFoundException {
        if (Files.fileSizeInMB(path) < 2) {
            this.content = Files.stringFromFile(this.getPath());
        } else {
            this.content = null;
        }
    }

    /**
     * Método para definir o conteúdo da página à partir da url salva na
     * variável link.
     *
     * @throws FileNotFoundException
     * @throws MalformedURLException
     * @throws IOException
     */
    public void setContentFromURL() throws FileNotFoundException, MalformedURLException, IOException {
        URL page = new URL(link);

        URLConnection connection = page.openConnection();
        HttpURLConnection conn = (HttpURLConnection) connection;
        conn.addRequestProperty("User-Agent", "Mozilla/21.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder str = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            str.append(inputLine);
            str.append("\n");
        }

        in.close();
        this.content = str.toString();
    }

    /**
     * Retorna o local onde o arquivo está ou será salvo o arquivo .html.
     *
     * @return O local onde o arquivo .html está salvo ou será salvo.
     */
    public String getPath() {
        return path;
    }

    /**
     * Define o local onde o arquivo está ou será salvo o arquivo .html.
     *
     * @param path O local onde o arquivo será salvo ou está salvo.
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return title + "\n" + link + "\n" + description + "\n";
    }
}
