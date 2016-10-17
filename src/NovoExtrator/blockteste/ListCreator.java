/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NovoExtrator.blockteste;

import NovoExtrator.extrator.ContentList;
import NovoExtrator.structures.SuperClasse;
import NovoExtrator.filehandlers.Formats;
import NovoExtrator.filehandlers.FileHandler;
import NovoExtrator.filehandlers.FormatsCalculus;
import NovoExtrator.filehandlers.FormatsCalculus.AuxStructure;
import NovoExtrator.structures.TagsNP;
import NovoExtrator.structures.TokenH;
import NovoExtrator.structures.TokenNP;
import NovoExtrator.structures.HashToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import NovoExtrator.structures.HashExternOperations;

/**
 * This class creates the list, fixing it using heuristics.
 */
public class ListCreator {
    private LinkedList<SuperClasse> wl;
    private String type = "";

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    /**
     * Constructor of the Class, without parameters.
     */
    public ListCreator(){
        wl = new LinkedList();
    }

    /**
     * Methods that returns a String containing the content of the list.
     * @return String containing the content of the list.
     */
    public String printList(){
        StringBuilder sb = new StringBuilder();
        
        for (SuperClasse s: wl){
            if (s instanceof TagsNP){
                sb.append("--->Tags<---\n" + ((TagsNP)s).printTags() + "\n" + "--->/Tags<---\n");
            }else if(s instanceof TokenNP){
                sb.append(((TokenNP)s).getToken() + "->" + ((TokenNP)s).getTag() + "\n");
            }
        }

        return sb.toString();
    }
    
    private void deduceParenthesis(LinkedList<SuperClasse> al){
               if (al.size() == 0) return;
        int auxiliar = -1;
        String tipo = "";
        boolean controle = false;
        for(int i= 0; i < al.size(); ++i){
           SuperClasse s = al.get(i);
           if (s instanceof TokenNP){
               if (((TokenNP)s).getToken().equalsIgnoreCase("(") && !controle){
                   auxiliar = i;
                   controle = true;
               }else if (controle && ((TokenNP)s).getToken().equalsIgnoreCase(")")){
                   for (int j = auxiliar; j <= i; ++j){
                       if (al.get(j) instanceof TokenNP){
                           ((TokenNP) al.get(j)).setTag(((TokenNP) al.get(auxiliar + 1)).getTag());
                       }
                   }
                   controle = false;
               }
               
               

           }
       }
    }

    /**
     * Methods that deduces author, using heuristics.
     * @param al The list containing the text.
     */
    
    private void deduceAuthor(LinkedList<SuperClasse> al){
        //se o proximo for '.', e se aux tiver apenas uma letra, entao é autor
        if (al.size() == 0) return;
        SuperClasse auxiliar = (al != null ? al.get(0) : null);
        boolean controle = false;
        for(int i= 0; i < al.size(); ++i){
           SuperClasse s = al.get(i);
           if (s instanceof TokenNP){
//               if (((TokenNP)s).getToken().length() == 1 &&
//                       Character.isLetter(((TokenNP)s).getToken().charAt(0)) && !controle){
//                   auxiliar = s;
//                   controle = true;
//               }else{controle=false;}
//               
//               if (controle && ((TokenNP)s).getToken().equalsIgnoreCase(".")){
//                   ((TokenNP) auxiliar).setTag("author");
//                   ((TokenNP) s)       .setTag("author");
//                   controle = false;
//               }
               if (((TokenNP)s).getToken().length() == 1 
                       && Character.isLetter(((TokenNP)s).getToken().charAt(0)) 
                       && i < al.size() - 1
                       && al.get(i + 1) instanceof TokenNP
                       && ((TokenNP) al.get(i+1)).getToken().equals(".")){
                   
                   ((TokenNP) s)          .setTag("author");
                   ((TokenNP) al.get(i+1)).setTag("author");
               }
           }
        }
    }

    /**
     *
     * @param wl receives the words list to fix and incorporate symbols
     */
    /* esse método nao vai ser usado, pois utiliza mascaras e ja tem metodos pra re
     * solver esse problema.
     */
    private void incorporateSymbols(LinkedList<SuperClasse> al){
        SuperClasse auxiliar = (al != null && al.size() > 0 ? al.get(0) : null);
        if (auxiliar == null) return;

        boolean controle = false;

        LinkedList<TokenNP> l = new LinkedList();
        String tipo = new String();

        for(int i=0; i < al.size(); i++){
            SuperClasse a = al.get(i);

            if (a instanceof TokenNP){
                TokenNP t = (TokenNP) a;
                
                //cancelei aqui pra baixo pq as mascaras fazem esse servico
                /*if (t.getTag().equals("symbol")){
                    if(!controle) {
                        if (t.getToken().equals("(")){
                           controle = true;
                           l.add(t);
                        }
                        
                    }else{
                        if (t.getToken().equals(")")){
                            l.add(t);

                            for (TokenNP g: l){
                                g.setTag(tipo);
                            }

                            //System.out.println("Fechou parenteses");

                        }else{
                            tipo = t.getTag();
                        }\
                    }
                }*/
                //System.out.println(i + "  " + t.getToken() + " " + (((TokenNP) al.get(i)).getTag()));
                if ((isSymbol(t.getToken()) && t.getToken().equalsIgnoreCase("-"))

                    //    && !isPDelimiter(((TokenNP) wl.get(i - 1)).getToken())){ //linha nova
                    ){
                    //System.out.println(((TokenNP) wl.get(i-1)).getToken() + ((TokenNP) wl.get(i)).getToken() + ((TokenNP) wl.get(i+1)).getToken());
                    //System.out.println(((TokenNP) wl.get(i-1)).getToken() + FileHandler.isNumber(((TokenNP) wl.get(i-1)).getTag()));
                    int k = i - 1;
                    String sg = "";
                    
                    //System.out.println("Símbolo: " + t.getToken());

                    while (k > 0){
                        if (wl.get(k) instanceof TokenNP){
                            sg = ((TokenNP) wl.get(k)).getTag();
                            break;
                        }
                        k--;
                    }
                    
                    t.setTag(sg);
                }

                if (t.getToken().equals(".") || t.getToken().equals("!") || t.getToken().equals(":") || t.getToken().equals(",") || t.getToken().equals(";")){
                   TokenNP k = (TokenNP) al.get(i) ;
                   for (int j = i - 1; j>0; j--){
                       if (al.get(j) instanceof TokenNP) {
                           k = (TokenNP) al.get(j);
                           break;
                       }
                   }
                   t.setTag(k.getTag());
                }
                
                if (t.getToken().equals("(")){
                   TokenNP k = (TokenNP) al.get(i) ;
                   for (int j = i - 1; j>al.size(); j++){
                       if (al.get(j) instanceof TokenNP) {
                           k = (TokenNP) al.get(j);
                           break;
                       }
                   }
                   t.setTag(k.getTag());
                }
            }
        }
    }

    /**
     * Verifica se a String s é um dos delimitadores que ficarão fora do bloco
     * @param s
     * @return
     */
    private boolean isBlockOut(String s){
        if (/*(FileHandler.isNumber(s))
                            || */ (s.equalsIgnoreCase("["))
                            || (s.equalsIgnoreCase("(")))// || (s.equalsIgnoreCase("\\n")))
            return true;
        return false;
    }

    /**
     * Verifica se a String s é um dos delimitadores que ficarão dentro e no fim do bloco
     * @param s
     * @return
     */
    private boolean isBlockInEnd(String s){
        if ((s.equalsIgnoreCase(".")) || (s.equalsIgnoreCase(":")) ||  (s.equalsIgnoreCase("]")) ||  
                (s.equalsIgnoreCase(")"))  || (s.equalsIgnoreCase(",")) || (s.equalsIgnoreCase(";")))
            // || (s.equalsIgnoreCase("<barran>")))
            return true;
        return false;
    }

    public void clusterFieldsTest(LinkedList<SuperClasse> wl, FormatsCalculus fc){
        LinkedList<SuperClasse> lista = new LinkedList();
        
        String before = "";
        int whereLeave = 0;
        
        //procura pela lista algum item que tenha tipo prédefinido
        int i = 0;
        int inicio = 0;
        while (i < wl.size()){
            SuperClasse s = wl.get(i);

            whereLeave = 0;
            if (s instanceof TokenNP){
                //se for titulo ou author ou publisher ou journal

               //cria uma lista pra carregar o possível bloco de um único tipo
                lista.clear();

                //segue à frente até achar um possivel delimitador.
                inicio = i;
                if (i==0){
                    inicio = 0;
                    for (int a=i; a>=0;a--){
                        if (wl.get(a) instanceof TokenNP){
                            TokenNP t1 = (TokenNP)wl.get(a);

                            if (this.isBlockInEnd(t1.getToken())){
                                inicio = a + 1;
                                break;
                            }else if (this.isBlockOut(t1.getToken())){
                                inicio = a;
                                break;
                            }
                        }
                    }
                }
                
                for (int a=inicio; a < wl.size() - 1; a++){
                    if (wl.get(a) instanceof TokenNP){
                        TokenNP t1 = (TokenNP)wl.get(a);
                        TokenNP t2 = (TokenNP)wl.get(a+1);

                        if (this.isBlockInEnd(t1.getToken()) && !this.isBlockInEnd(t2.getToken())){
                            whereLeave = a + 1;
                            lista.add(t1);
                            break;
                        }else if (this.isBlockOut(t1.getToken())){
                            whereLeave = a;// - 1;
                            break;
                        }
                        lista.add(t1);
                    }
                }

                //System.out.println("inicio: " + inicio + " WhereLeave: " + whereLeave);
//
//                ///////////////////
//                System.out.println("ANTES");
                for (SuperClasse k: lista){
                    if (k instanceof TokenNP) System.out.println(((TokenNP)k).getToken() + " => " + ((TokenNP)k).getTag());
                }
//                ///////////////////

                before = probGroupType(lista, fc, before);

//               System.out.println("\nDEPOIS");
                for (SuperClasse k: lista){
                    if (k instanceof TokenNP) System.out.println(((TokenNP)k).getToken() + " => " + ((TokenNP)k).getTag());
                }
//                System.out.println();
//                System.out.println();
 /////////
            }
            if (whereLeave > i)
                i = whereLeave;
            else if (whereLeave <= i)
                i++;
        }
        //System.exit(0);
    }

    private void clusterFields(LinkedList<SuperClasse> wl, FormatsCalculus fc){
          LinkedList<SuperClasse> lista = new LinkedList();
        int whereLeave = 0;
        //procura pela lista algum item que tenha tipo prédefinido
        int i = 0;
        int inicio = 0;
        String before = "";
        
         while (i < wl.size()){
            SuperClasse s = wl.get(i);

            whereLeave = 0;
            if (s instanceof TokenNP){
                //se for titulo ou author ou publisher ou journal

               //cria uma lista pra carregar o possível bloco de um único tipo
                lista.clear();

                //segue à frente até achar um possivel delimitador.
                inicio = i;
                if (i==0){
                    inicio = 0;
                    for (int a=i; a>=0;a--){
                        if (wl.get(a) instanceof TokenNP){
                            TokenNP t1 = (TokenNP)wl.get(a);

                            if (this.isBlockInEnd(t1.getToken())){
                                inicio = a + 1;
                                break;
                            }else if (this.isBlockOut(t1.getToken())){
                                inicio = a;
                                break;
                            }
                        }
                    }
                }
                
                for (int a=inicio; a < wl.size() - 1; a++){
                    if (wl.get(a) instanceof TokenNP && wl.get(a+1) instanceof TokenNP){
                        TokenNP t1 = (TokenNP)wl.get(a);
                        TokenNP t2 = (TokenNP)wl.get(a+1);

                        if (this.isBlockInEnd(t1.getToken()) && !this.isBlockInEnd(t2.getToken())){
                            whereLeave = a + 1;
                            lista.add(t1);
                            break;
                        }else if (this.isBlockOut(t1.getToken())){
                            whereLeave = a;// - 1;
                            break;
                        }
                        lista.add(t1);
                    }
                }

                //System.out.println("inicio: " + inicio + " WhereLeave: " + whereLeave);
//
//                ///////////////////
//                System.out.println("ANTES");
//                for (SuperClasse k: lista){
//                    if (k instanceof TokenNP) System.out.println(((TokenNP)k).getToken() + " => " + ((TokenNP)k).getTag());
//                }
//                ///////////////////
                
                before = probGroupType(lista, fc, before);

//               System.out.println("\nDEPOIS");
//                for (SuperClasse k: lista){
//                    if (k instanceof TokenNP) System.out.println(((TokenNP)k).getToken() + " => " + ((TokenNP)k).getTag());
//                }
//                System.out.println();
//                System.out.println();
 /////////
            }
            if (whereLeave > i)
                i = whereLeave;
            else if (whereLeave <= i)
                i++;
        }
        //System.exit(0);
    }

    private void fixAuthor(LinkedList<SuperClasse> wl){
        int j = 0;
        int inicial = 0;
        
        for (int i = 0; i < wl.size(); i++){
            if (wl.get(i) instanceof TokenNP){
                TokenNP t = (TokenNP) wl.get(i);
                if (t.getTag().equalsIgnoreCase("author") && j == 0){
                    j = 1;
                    continue;
                }else if (t.getToken().equalsIgnoreCase(".") && j == 1){
                    j = 2;
                    inicial = i + 1;
                    continue;
                }else if (t.getToken().equalsIgnoreCase("and") && j == 2){
                    j = 3;
                }
                
                if (j == 3){
                    int k = 0;
                    while (inicial + k < wl.size()){
                        if (wl.get(inicial+k) instanceof TokenNP){
                            if (!isPDelimiter(((TokenNP) wl.get(inicial+k)).getToken())) break;
                            ((TokenNP) wl.get(inicial + k)).setTag("author");
                        }       
                        k++;
                    }
                }
            }else j = 0;
        }
    }
    
    /**
     * It fixes the "in:"
     * @param wl 
     */
    private void fixIn(LinkedList<SuperClasse> wl){
        int j = 0;
        int inicial = 0;
        
        for (int i = 0; i < wl.size(); i++){
            if (wl.get(i) instanceof TokenNP){
                TokenNP t = (TokenNP) wl.get(i);
                if (t.getToken().equalsIgnoreCase("in") && j == 0){
                    j = 1;
                    inicial = i;
                    continue;
                }else if (t.getToken().equalsIgnoreCase(":") && j == 1){
                    j = 2;
                    t.setTag("journal");
                    ((TokenNP) wl.get(inicial)).setTag("journal");
                    j=0;
                }
                
                
            }else j=0;
        }
    }
    
    private String probGroupType (LinkedList<SuperClasse> w, FormatsCalculus fc, String before){
        int contador = 0;
        int max = 10;
        boolean flag = false;
        TokenH[] t = new TokenH[max];

        //percorre a lista w, e verifica se nao é null

        //System.out.println(Main.printList(w) + "\n");

        for(SuperClasse s: w){
            if (s instanceof TokenNP){
                TokenNP aux = (TokenNP) s;
                //percorre o vetor t, pra ver se tipo já existe
                //se existe, retorna true
                for (int i = 0; i < contador; ++i){
                    if (t[i] != null){
                        if (t[i].getType().equalsIgnoreCase(aux.getTag())){
                            t[i].incAttribute();
                            flag = true;
                            break;
                        }
                    }
                }

                if (!flag){  //quer dizer que não encontrou tipo
                     if (!aux.getTag().equalsIgnoreCase("")
                        && !aux.getTag().equalsIgnoreCase("notype")
                        && !aux.getTag().equalsIgnoreCase("symbol")
                        && !aux.getTag().equalsIgnoreCase("stopword")
                        && !FileHandler.isNumber(aux.getToken())){

                        if (contador >= max){ //se nao couber, cria outro vetor
                            TokenH[] tAux = new TokenH[max * 2];

                            System.arraycopy(t, 0, tAux, 0, t.length);

                            t = tAux;
                            tAux = null;
                            max *= 2;
                        }

                        t[contador] = new TokenH();
                        t[contador].setType(aux.getTag());
                        ++contador;
                        flag = false;
                    }
                }
            }
        }

        int maior = 0;
        String tipoFreq = "";

        /* Look for the most frequent type*/
        LinkedList<String> lstring = new LinkedList();
        
        for (int i = 0; i < contador; ++i){
            if (t[i].getAttributeCount() >= maior){
                tipoFreq = t[i].getType();
                maior = t[i].getAttributeCount();
            }
        }
        
        /* Count how many types has the same element's number as the most frequent type. */
        for (int i = 0; i < contador; ++i){
            if (t[i].getAttributeCount() == maior){
                lstring.add(type);
            }
        }
        
        /* if exists more than an only type, then look for the most frequent, 
         * according with the previous block */
        
        if (lstring.size() > 1){
            boolean ran = false;
            for (String s: lstring){
                System.out.println(before);
                if (s.equalsIgnoreCase(fc.getTheMostProbable(before)) || before.equalsIgnoreCase(s)){
                    tipoFreq = s;
                    ran = true;
                    break;
                }
            }
            if (ran == false){
                for(SuperClasse y: w){
                    if (y instanceof TokenNP){
                        ((TokenNP)y).setTag(tipoFreq);
                    }
                }
                return tipoFreq;
            }
        }else{
            for(SuperClasse y: w){
                if (y instanceof TokenNP){
                    ((TokenNP)y).setTag(tipoFreq);
                }
            }
        }
//        
        System.out.println(tipoFreq);
        return tipoFreq;

    }

    private void deduceNumber(LinkedList<SuperClasse> wl){
        TokenNP t;

        for(int i = 0; i < wl.size(); i++){
            SuperClasse s = wl.get(i);
            if (s instanceof TokenNP){
                t = (TokenNP) s;
                t.setToken(FileHandler.removeAcentos(t.getToken()));
                
                /*if (t.getTag().equalsIgnoreCase("")){
                    if (FileHandler.isNumber(t.getToken())){
                        t.setTag("number");
                    }
                */
                
                if (FileHandler.isNumber(t.getToken()) && t.getToken().length() == 4
                        && (t.getToken().substring(0, 2).equals("19") || t.getToken().substring(0, 2).equals("20")) &&
                        (Integer.parseInt(t.getToken()) < (new Date()).getYear() + 1900)){
                    t.setTag("year");
                    //System.out.println(t.getToken() + "<<");
                }

                //if (t.getToken().equalsIgnoreCase("-") && wl.get(i + 1) instanceof TokenNP) System.out.println(((TokenNP)wl.get(i + 1)).getToken());

                if ((isSymbol(t.getToken()) && i > 0 && i < (wl.size() -1) && wl.get(i - 1) instanceof TokenNP && wl.get(i + 1) instanceof TokenNP
                        && FileHandler.isNumber(((TokenNP) wl.get(i - 1)).getTag()) && FileHandler.isNumber(((TokenNP) wl.get(i + 1)).getTag())
                        
                    //    && !isPDelimiter(((TokenNP) wl.get(i - 1)).getToken())){ //linha nova
                    )){
                    //System.out.println(((TokenNP) wl.get(i-1)).getToken() + ((TokenNP) wl.get(i)).getToken() + ((TokenNP) wl.get(i+1)).getToken());
                    //System.out.println(((TokenNP) wl.get(i-1)).getToken() + FileHandler.isNumber(((TokenNP) wl.get(i-1)).getTag()));
                    ((TokenNP) wl.get(i-1)).setTag("pages");
                    ((TokenNP) wl.get(i)).setTag("pages");
                    ((TokenNP) wl.get(i+1)).setTag("pages");
                }

                if (FileHandler.isNumber(t.getToken()) && i > 0 && (wl.get(i - 1)) instanceof TokenNP && !FileHandler.isNumber(((TokenNP)wl.get(i-1)).getToken())
                     && !isSymbol(((TokenNP)wl.get(i-1)).getToken()) && !((TokenNP)wl.get(i)).getTag().equalsIgnoreCase("pages") && !((TokenNP) wl.get(i-1)).getToken().equalsIgnoreCase("<barran>"))
                    //    && !isPDelimiter(((TokenNP) wl.get(i - 1)).getToken())){ //linha nova
                    {
                    //System.out.println("Journal :@");
                    ((TokenNP) wl.get(i-1)).setTag("journal");
                    ((TokenNP) wl.get(i)).setTag("journal");
                }

                if (i > 0 && (i < wl.size() - 1) && (wl.get(i - 1)) instanceof TokenNP && (wl.get(i + 1)) instanceof TokenNP 
                     && ((TokenNP)wl.get(i-1)).getToken().equals("(")
                     && ((TokenNP) wl.get(i+1)).getToken().equals(")"))
                    //    && !isPDelimiter(((TokenNP) wl.get(i - 1)).getToken())){ //linha nova
                    {
                    //System.out.println("Journal :@");
                    ((TokenNP) wl.get(i+1)).setTag(((TokenNP) wl.get(i)).getTag());
                    ((TokenNP) wl.get(i-1)).setTag(((TokenNP) wl.get(i)).getTag());
                }
                //}
            }
        }
    }

    public void fixList(LinkedList<SuperClasse> wl, FormatsCalculus fc){
        deduceAuthor(wl);
        deduceNumber(wl);
        //clusterFields(wl);
        
        //System.out.println(this.getType());
        if (this.getType().equals("txt")){
            clusterFieldsTest(wl, fc);
        }else{
            clusterFields(wl, fc);
        }
       
        //deduceNumber(wl);
        //incorporateSymbols(wl);
    }

    public void refixList(LinkedList<SuperClasse> wl){
        //deduceAuthor(wl);
        deduceNumber(wl);
        //clusterFields(wl);
        incorporateSymbols(wl);
        fixAuthor(wl);
        fixIn(wl);
        deduceParenthesis(wl);
    }

    public String[] getTextSplitedWithTag(String s){
        int counter = 0;
        int max = 10;
        String[] text = new String[max];
        boolean inicio = false, fim = false;
        StringBuilder sb = new StringBuilder();
                
        for (int i = 0; i < s.length(); i++){
            if (s.charAt(i) != '<' && inicio == false){
                if (s.charAt(i) != ' ' && !inicio && s.charAt(i) != '\t'  && s.charAt(i) != '­'){
                    sb.append(s.charAt(i));
                }else{
                  if (counter == max){
                        String[] textAux = new String[max * 2];
                        System.arraycopy(text, 0, textAux, 0, text.length);

                        text = textAux;
                        textAux = null;
                        max *= 2;
                    }
                    text[counter] = sb.toString().toLowerCase();

                    counter++;
                    sb = null;
                    sb = new StringBuilder();
                    
                }

            }else if (s.charAt(i) == '<' && inicio == false){
                if (sb.toString().equalsIgnoreCase("")){
                    sb.append(s.charAt(i));
                    inicio = true;
                }else{
                    if (counter == max){
                        String[] textAux = new String[max * 2];
                        System.arraycopy(text, 0, textAux, 0, text.length);

                        text = textAux;
                        textAux = null;
                        max *= 2;
                    }
                    text[counter] = sb.toString().toLowerCase();
                    
                    counter++;
                    sb = new StringBuilder();
                    sb.append(s.charAt(i));

                    inicio = true;
                }
            }else if (s.charAt(i) == '>' && inicio == true){ 
                sb.append(s.charAt(i));
                fim = true;
            }else{
                sb.append(s.charAt(i));
            }

            if (inicio == true && fim == true){
                if (counter == max){
                    String[] textAux = new String[max * 2];
                    System.arraycopy(text, 0, textAux, 0, text.length);

                    text = textAux;
                    textAux = null;
                    max *= 2;
                }
                text[counter] = sb.toString().toLowerCase();
                counter++;

                sb = null;
                sb = new StringBuilder();

                inicio = false;
                fim    = false;
            }
        }
        if (!sb.toString().equals("")){
                if (counter == max){
                    String[] textAux = new String[max * 2];
                    System.arraycopy(text, 0, textAux, 0, text.length);

                    text = textAux;
                    textAux = null;
                    max *= 2;
                }
                text[counter] = sb.toString().toLowerCase();
                counter++;

        }
        return text;
    }

    public String fixTextCodification(String str){
        str = str.replace("&ccedil;", "ç");
        str = str.replace("&eacute;", "é");
        str = str.replace("&nbsp;", " ");

        str = str.replace("&uacute;", "ú");

        str = str.replace("&ntilde;", "ñ");
        str = str.replace("&quot;", "\"");
        str = str.replace("&apos;", "'");
        str = str.replace("&amp;", "&");
        str = str.replace("&lt;", "<");
        str = str.replace("&gt;", ">");
        str = str.replace("–", "-");
        
        str = str.replace("–", "-");

        str = str.replace("&acirc;", "â");
        str = str.replace("&aacute;", "á");
        str = str.replace("&atilde;", "ã");
        str = str.replace("&agrave;", "à");
        str = str.replace("&auml;", "ä");
        
        str = str.replace("&euml;", "ë");
        str = str.replace("&ecirc;", "ê");
        str = str.replace("&eacute;", "é");
        str = str.replace("&etilde;", "ẽ");
        str = str.replace("&egrave;", "è");    

        str = str.replace("&iuml;", "ï");
        str = str.replace("&icirc;", "î");
        str = str.replace("&iacute;", "í");
        str = str.replace("&itilde;", "ĩ");
        str = str.replace("&igrave;", "ì"); 
        
        str = str.replace("&ouml;", "ö");
        str = str.replace("&ocirc;", "ô");
        str = str.replace("&oacute;", "ó");
        str = str.replace("&otilde;", "õ");
        str = str.replace("&ograve;", "ò"); 
        
        str = str.replace("&uuml;", "ë");
        str = str.replace("&ucirc;", "ê");
        str = str.replace("&uacute;", "é");
        str = str.replace("&utilde;", "ẽ");
        str = str.replace("&ugrave;", "è");         
                

        return str;
    }

    public static String codifyTextToHtml(String str){
        str = str.replace("ç","&ccedil;");

        str = str.replace("á", "&aacute;");
        str = str.replace("â", "&acirc;");
        str = str.replace("à", "&agrave;");
        str = str.replace("ã", "&atilde;");
        str = str.replace("ä", "&auml;");

        str = str.replace("é", "&eacute;");
        str = str.replace("ê", "&ecirc;");
        str = str.replace("è", "&egrave;");
        str = str.replace("ẽ", "&etilde;");
        str = str.replace("ë", "&euml;");        

        str = str.replace("í", "&iacute;");
        str = str.replace("î", "&icirc;");
        str = str.replace("ì", "&igrave;");
        str = str.replace("ĩ", "&itilde;");
        str = str.replace("ï", "&iuml;");        
        
        str = str.replace("ó", "&oacute;");
        str = str.replace("ô", "&ocirc;");
        str = str.replace("ò", "&ograve;");
        str = str.replace("õ", "&otilde;");
        str = str.replace("ö", "&ouml;");        
        
        str = str.replace("ú", "&uacute;");
        str = str.replace("û", "&ucirc;");
        str = str.replace("ù", "&ugrave;");
        str = str.replace("ũ", "&utilde;");
        str = str.replace("ü", "&uuml;");        
        
        str = str.replace("é", "&eacute;");
        str = str.replace("ê", "&ecirc;");
        str = str.replace("ë", "&euml;");


        str = str.replace("í", "&iacute;");

        str = str.replace("ó", "&oacute;");
        str = str.replace("õ", "&otilde;");
        str = str.replace("ò", "&ograve;");

        str = str.replace("ú", "&uacute;");


        str = str.replace("ñ", "&ntilde;");

        return str;
    }

    public static boolean isTag(String c){
        if (c.contains("<") && c.contains(">")) return true;
        return false;
    }

    public static boolean isSymbol(String c){
        if (c.length() == 0) return false;
        if (c.equalsIgnoreCase("<barran>")) return true;
        switch(c.charAt(0)){
            case '-':
            case ':':
            case '=':
            case ',':
            case '!':
            case '@':
            case '#':
            case '$':
            case '%':
            case '*':
            case '(':
            case ')':
            case '+':
            case '_':
            case '?':
            case '{':
            case '}':
            case '[':
            case ']':

            case '.': return true;
            default: return false;

        }
    }

    public static boolean isPDelimiter(String c){
        if (c.length() == 0) return false;
        switch(c.charAt(0)){
            case ':':
            case ',':
            case '.': return true;
            default: return false;

        }
    }

    public String[] separateTokens(String c){
        c = c.replace(".", " . ");
        c = c.replace(",", " , ");
        c = c.replace("!", " ! ");
        c = c.replace(":", " : ");
        c = c.replace("-", " - ");
        c = c.replace("(", " ( ");
        c = c.replace(")", " ) ");
        c = c.replace("[", " [ ");
        c = c.replace("]", " ] ");
        c = c.replace("&", " & ");
        c = c.replace("*", " * ");
        c = c.replace("@", " @ ");

        String[] text = c.split(" ");

        for (String t: text){
            t = t.trim();
        }
        
        return text;
    }

    public String decideProbType(HashToken ht, String t){

        String nt = FileHandler.removeAcentos(t.toString().trim().toLowerCase());

        if (ht.exists(nt)){
            if (!FileHandler.isNumber(nt)){
                String probType = new String();
                LinkedList<TokenH> l  = ht.get(nt);
                int maior = 0;

                for (TokenH s: l){
                    if (s.getAttributeCount() > maior){
                        probType = s.getType();
                        maior = s.getAttributeCount();
                    }
                }
                //System.out.println(">" + nt + "<" + probType);
                return probType;
                
            }
        }else if (nt.length() == 4 && (nt.substring(0, 2).equals("19") || nt.substring(0, 2).equals("20"))){
            return "date"; //("year");
        }else{
            if (FileHandler.isNumber(nt.trim()) && !nt.equals("&")){
                return("number");
            } else if (nt.length() == 1 && ListCreator.isSymbol(nt.toString())){
                return ("symbol");
            }
            else return("notype");
        }
        return "notype";
    }

    public String[] constructWords(String str){
        StringBuilder s = new StringBuilder();
        int count = 0;
        int max = 10;
        String[] ans = new String[max];
        
        for (int j = 0; j < str.length(); ++j){
            //Separa as palavras
            if (Character.isLetterOrDigit(str.charAt(j))){
                //if the character is letter or digit, this is inserted on StringBuilder s    
                s.append(str.charAt(j));
            }else{
                //if the character is a symbol, the flow runs here.
                if (!s.toString().equals("")){
                    if (count >=  max - 1){
                        String[] aux = new String[max * 2];
                        System.arraycopy(ans, 0, aux, 0, ans.length);
                        
                        ans = aux;
                        max *= 2;
                    }
                    ans[count] = s.toString();
                    count++;
                    s = new StringBuilder();
                    s.append(str.charAt(j));

                    if (!Character.isSpace(str.charAt(0))){
                        ans[count] = s.toString();
                        count++;
                        s = new StringBuilder();
                    }
                }
            }
        }
        return ans;
    }

    public void createList(HashToken ht, HashToken hsw, HashToken hci, String fileName, FormatsCalculus fc) throws FileNotFoundException, IOException{
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        //BufferedReader in = new BufferedReader (new InputStreamReader(is, cs));
        InputStreamReader isr = new InputStreamReader(is, cs);
        
//        if (!isr.getEncoding().equalsIgnoreCase("UTF8")){
//            String encoding = isr.getEncoding();
//            isr.close();
//            cs = Charset.forName(encoding);
//            isr = new InputStreamReader(is, cs);
//        }
//
//        System.out.println(isr.getEncoding());

        BufferedReader in = new BufferedReader (isr);
        
        wl.clear();
        BufferedWriter fw       = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("saida.txt"), "UTF-8"));

        // while the file was not completely read.
        while (in.ready()){
                String str = fixTextCodification(FileHandler.removeAcentos(fixTextCodification(in.readLine())));//fixTextCodification(in.readLine());
                fw.write(str);
                
                if (str.matches(".*?<[^>]*>.*?") && getType().equals("")){
                    this.setType("html");
                }else if (!str.matches(".*?<[^>]*>.*?") && getType().equals("")){
                    this.setType("txt");
                }

                //System.out.println(getType());

                String[] cTags = getTextSplitedWithTag(str);
                boolean ref = false;

                    // run thru the vector
                for (int i = 0; i < cTags.length; i++){
                    if (cTags[i] != null && !cTags[i].equals("")){
                        if (isTag(cTags[i])){
                            //if the last is tag, the tags is insert together this one. Else, create a new element.
                            if (!(wl.peekLast() instanceof TagsNP)){
                                TagsNP t = new TagsNP();
                                t.insert(cTags[i]);
                                wl.add(t);
                            }else if (wl.size() > 0 && (wl.peekLast() instanceof TagsNP)){
                                TagsNP t = (TagsNP) wl.peekLast();
                                t.insert(cTags[i]);
                            }
                        }else{
                            str = cTags[i];
                            String[] tokens = this.separateTokens(str);

                            for (String t: tokens){

                                if (t!= null && !t.equals("")){

                                    //System.out.println("Token " + t + " Prob Type:" + this.decideProbType(ht, t));
                                    //System.out.println("t:" + t);
                                    if (!hci.exists(t)){
                                        TokenNP p = new TokenNP(t, this.decideProbType(ht, t));
                                        wl.add(p);
                                    }else{
                                        //System.out.println(t + "->location");
                                        TokenNP p = new TokenNP(t, "location");
                                        wl.add(p);
                                    }

                                }
                            }
                        }
                    }
                }
                if (getType().equals("txt")) wl.add(new TokenNP("<barran>", "symbol"));
            }

        fw.close();


        
        this.fixList(wl, fc);
        

    }
    
    /**
     * Verifying if some URL exists inner to str.
     * @param str to be verified.
     * @return the position for the url's start. -1 if it's not found.
     */
    public static int detectURL(String str){
        boolean s = str.matches(
                "^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\+&%\\$#\\=~])*$");
        
        int value = str.indexOf("http");
        if (s){
            if (value == -1){
                value = str.indexOf("ftp");
            }
        }
        
        return value;
    }

    public void createBlockList(HashToken ht, HashToken hsw, HashToken hci, String fileName, FormatsCalculus fc) throws FileNotFoundException, IOException{
        
        Charset cs = Charset.forName("ISO8859-1");
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(is, cs);
        
        BufferedReader in = new BufferedReader (isr);
        
        wl.clear();
        BufferedWriter fw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream("saida.txt")
            , "UTF-8")
        );

        // while the file was not completely read.
        String before = "";
        while (in.ready()){
                String str = in.readLine().toLowerCase();
                
//                System.out.println();
//                System.out.println(str);
//                System.out.println();
                
                int x = detectURL(str);
                int s = 0;
                
                StringBuilder sb = new StringBuilder();
                LinkedList<String> v = new LinkedList();
                        
                if (x != -1){
                    for (int k = x; k < str.length(); ++k){
                       sb.append(str.charAt(k));
                    }
                                
                    String[] k = ((FileHandler.justIsolatePunctuationCharacters(
                            fixTextCodification(
                                FileHandler.removeAcentos(
                                    sb.toString()
                                )
                            )
                        )).split(" "));

                    
                    //v.addAll(Arrays.<String>asList(k));
                     
                    for (String g: k){
                        if (!g.trim().equalsIgnoreCase("")){
                            //System.out.println(g);
                            v.add(g);
                            s++;
                        }
                    }

                    //System.out.println( x + " " + sb.toString() + " " + s);
                }
                
                
                str = FileHandler.justIsolatePunctuationCharacters(
                    fixTextCodification(
                        FileHandler.removeAcentos(
                            fixTextCodification(
                                str
                            )
                        )
                    )
                );
                
                fw.write(str);
                
                this.setType("txt");
                
                String[] cTags = str.split(" ");

                LinkedList<String> auxToken  = new LinkedList();
                LinkedList<String> auxValue  = new LinkedList();
                boolean locked = false;
                
                
                for (int i = 0; i < cTags.length; i++){
                    /* if it's not a symbol AND cTags[i] was different from '' */
                    if (!isSymbol(cTags[i]) && cTags[i].length() > 0){
                        if (v.size() >  0 && v.get(0).equalsIgnoreCase(cTags[i])){
                            /* It'll be used if the token was a link */
                            auxToken.add(sb.toString());
                            auxValue.add("");
                            i += s + 1;
                            x = -1;
                            wl.add(new TokenNP(sb.toString(), "note"));
                            sb = new StringBuilder();
                        }else{
                            /* Simply add it to the list :) */ 
                            auxToken.add(cTags[i]);
                            auxValue.add("");   
                            locked = true;
                        }
                    }else if (auxValue.size() > 0 && locked == true){

                        before = calculateProbabilityUsingPreviousBlock(ht, hsw, hci,
                                auxToken, auxValue, fc, before);
                                                
                        StringBuilder sb1 = new StringBuilder();
                        for (int j = 0; j < auxToken.size(); j++){
                            sb1.append(auxToken.get(j)).append(" ");
                        }
                         
                        wl.add(new TokenNP(sb1.toString(), auxValue.get(0)));
                        wl.add(new TokenNP(cTags[i].toString(), "symbol"));
                        
                        auxToken.clear();
                        auxValue.clear();
                    }else {
                        if (isSymbol(cTags[i])){
                            wl.add(new TokenNP(cTags[i].toString(), "symbol"));
                        }                       
                    }
                }
                
                /* Running here only at the end of the line. */
                if (auxToken.size() > 0){
                    before = calculateProbabilityUsingPreviousBlock(ht, hsw, hci, 
                            auxToken, auxValue, fc, before); 
                    StringBuilder sb1 = new StringBuilder();
                    for (int j = 0; j < auxToken.size(); j++){
                        sb1.append(auxToken.get(j)).append(" ");
                    }
                         
                    wl.add(new TokenNP(sb1.toString(), auxValue.get(0)));
                }
                wl.add(new TokenNP("<barran>", "symbol"));
                 
            }
        //cluster(wl);    
        fw.close();


        
        //this.fixList(wl, fc);
        //System.exit(0);

    }
    
    public void cluster(LinkedList<SuperClasse> wl){
        String previous = "";
        int previousIndex = -0;
        
        for(int i = 0; i < wl.size(); i++){
            if (wl.get(i) instanceof TokenNP){
                TokenNP t = (TokenNP) wl.get(i);
                
                if (isSymbol(t.getToken())) continue;
                
                if (previous.equalsIgnoreCase("")){
                    previous = t.getTag();
                    previousIndex = i;
                    continue;
                }
                
                System.out.println(t.getToken() + "<" + t.getTag() + "> " + previous);
                if (!previous.equalsIgnoreCase(t.getTag())){
                    
                    StringBuilder sb = new StringBuilder();
                    
                    int size = i - previousIndex;
                    int counter = 0;
                    
                    //System.out.println(size);
                    
                    for (int j = previousIndex; counter < size - 1; counter++){
                        TokenNP tp = ((TokenNP) wl.get(j));
                        
                        sb.append(tp.getToken()).append(" ");
                        wl.remove(j);
                        //j--;
                        //counter++;
                        //if (counter == size +1 ) break;
                    }
                    
                    
                    ((TokenNP) wl.get(previousIndex)).setToken(sb.toString());
                    ((TokenNP) wl.get(previousIndex)).setTag(previous);
                    previous = t.getTag();
                    previousIndex = i;
                }        
            }
        }
    }
    
    public LinkedList<String> getCitations(Formats f, LinkedList<SuperClasse> wl){
        LinkedList<LinkedList<String>> formatos = f.getFormats();
        LinkedList<Integer> n = new LinkedList();           //posicao
        LinkedList<Integer> indicador = new LinkedList();   //indicador do começo da citação atual
        
        LinkedList<String> resultados = new LinkedList();
                
        //initialize lists
        for (int i = 0; i < formatos.size(); i++){ n.add(0); indicador.add(-1); }
        
        //runs through list
        for (int i = 0; i < wl.size(); i++){
            
            //if it's a symbol
            if (wl.get(i) instanceof TokenNP && (((TokenNP) wl.get(i)).getTag().equals("symbol"))) continue;
            
            //runs formats list
            for (int j = 0; j < formatos.size(); j++){
                
                //System.out.println(i + " " + j)
//                System.out.println(((TokenNP) wl.get(i)).getTag() + " -> " + formatos.get(j).get(n.get(j)));
                if (((TokenNP) wl.get(i)).getTag().equalsIgnoreCase(formatos.get(j).get(n.get(j)))) {
                    
                    //System.out.println(((TokenNP) wl.get(i)).getToken());
                    if (indicador.get(j) == -1) {
                        indicador.set(j, i);
                    }
                    
                    n.set(j, n.get(j) + 1);
                    
                    if (n.get(j) == formatos.get(j).size()){
                        StringBuilder sb = new StringBuilder();
                        
                        for (int k = indicador.get(j); k < j; k++){
                            sb.append((TokenNP) wl.get(k)).append(" ");
                        }
                        
                        resultados.add(sb.toString());
//                        System.out.println(sb.toString());
                        sb = new StringBuilder();
                        n.set(j, 0);
                        indicador.set(j, -1);
                    }   
                }else{
                    n.set(j, 0);
                    indicador.set(j, -1);
                }
            }
        }
                
        return resultados;
    }
    
    private void calculateProbability(HashToken ht, HashToken hsw, HashToken hci, LinkedList<String> tokens
            , LinkedList<String> values){
        
        HashMap<String, Double> hm = new HashMap(); //Frequency
        HashMap<String, Integer> hc = new HashMap(); //Counting
        HashExternOperations heo = new HashExternOperations(ht);
        String[] types = {
            "author", "title", "booktitle", "journal", "volume",
            "number", "pages", "date", "location", "publisher",
            "institution", "editor", "note", "tech"
        };
        
        
        if (tokens.size() == 1 && tokens.get(0).length() > 1){
            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));
            return;
        }
        
        
        for (int i = 0; i < values.size(); i++){
            values.set(i, "notype");
        }
        
        for (int i = 0; i < values.size(); i++){
            //System.out.println(tokens.get(i) + "> " + (heo.searchForFrequency(tokens.get(i), "author") > 0));
            if (ht.exists(tokens.get(i)) || 
                    (heo.searchForFrequency(tokens.get(i), "author") > 0 && i == values.size() - 1)){
                values.set(i, "token");
            }else if (hsw.exists(tokens.get(i))){
                values.set(i, "notype");
            }else {values.set(i, "notype");} 
        }
       
        int totalUseful = 0;
        for (String s: values){
            if (s.equalsIgnoreCase("token") || s.equalsIgnoreCase("location")){
                //System.out.println("OK");
                totalUseful++;
            }
        }
        
        if (totalUseful == 0) return;
        
        for (String t: types){
            int actual = 0;
            int counter = 0;
            for(int i = 0; i < tokens.size(); i++){
                String s = tokens.get(i);
                String v = values.get(i);
                if (v.equalsIgnoreCase("token")){
                    //System.out.println(s + ">>" + v);
                    if (heo.searchForFrequency(s, t) > 0){
                        actual++;
                        counter += heo.searchForFrequency(s, t);
                    }
                }else {}
            }
            
            //System.out.println("Actual= " + actual + ": Tipo= " + t);
            //System.out.println();
            //System.out.println(t +": " + Double.valueOf(actual) + "/" + Double.valueOf(totalUseful) + " = " + (Double.valueOf(actual)/Double.valueOf(totalUseful)) );
            hm.put(t, (Double.valueOf(actual)/Double.valueOf(totalUseful)));
            hc.put(t, counter);
        }
        
        Double maior = 0.0;
        String result = "";
        
        Set<String> g = hm.keySet();
        for (String s: g){
            
            //System.out.println(s + ": " + hm.get(s) + ": " + totalUseful);
            if (hm.get(s) > maior){
                maior = hm.get(s);
                result = s;
            }
        }
        
        //Verify for draw
        int times = 0;
        for (String s: g){
            if (Double.compare(hm.get(s), maior) == 0){
                times++;
            }
        }
        
        int bigger = 0;
        
        if (times > 1){
            Set<String> c = hc.keySet();
            for (String s: c){
                if (hc.get(s) > bigger){
                    bigger = hc.get(s);
                    result = s;
                }
            } 
            //System.out.println("Entrou por empate: " + maior + ">" + result);
        }
        
        for (int i = 0; i < values.size(); i++){
            values.set(i, result);
        }

    }
    
        private String calculateProbabilityUsingPreviousBlock(HashToken ht, HashToken hsw, 
                HashToken hci, LinkedList<String> tokens, LinkedList<String> values, 
                FormatsCalculus fc, String previous){
        
        HashMap<String, Double> hm = new HashMap();         //Frequency
        HashMap<String, Integer> hc = new HashMap();        //Counting
        HashExternOperations heo = new HashExternOperations(ht);
        String[] types = {
            "author", "title", "booktitle", "journal", "volume",
            "number", "pages", "date", "location", "publisher",
            "institution", "editor", "note", "tech"
        };
        
        
        if (tokens.size() == 1 && tokens.get(0).length() > 1){
            //values.set(0, );
            
            String s = heo.searchForTheMoreOften(tokens.get(0));
            String f = fc.getTheMostProbable(previous);
            values.set(0, heo.searchForTheMoreOften(tokens.get(0)));
            
            //System.out.println("S: " + s + "(" + heo.searchForFrequency(tokens.get(0), s) 
            //        + ") | F: " + f + "(" + heo.searchForFrequency(tokens.get(0), f)
            //       + ") | previous: " + previous);
            
            return s;
        }
        
        
        for (int i = 0; i < values.size(); i++){
            values.set(i, "notype");
        }
        
        //System.out.println(ListCreator.detectURL("http://www.cifraclub.com.br/") + " " + heo.searchForFrequency("http://www.cifraclub.com.br/", "note"));
        
        for (int i = 0; i < values.size(); i++){
            //System.out.println(tokens.get(i) + "> " + (heo.searchForFrequency(tokens.get(i), "author") > 0));
            if ((ht.exists(tokens.get(i).trim())) || 
                    (heo.searchForFrequency(tokens.get(i).trim(), "author") > 0 && i == values.size() - 1) ||
                    (heo.searchForFrequency(tokens.get(i).trim(), "note")) > 0 && !ListCreator.isSymbol(tokens.get(i).trim())){
                values.set(i, "token");
            }else if (hsw.exists(tokens.get(i))){
                values.set(i, "notype");
            }else {values.set(i, "notype");} 
            //System.out.println(">" + tokens.get(i).trim() + " > " + values.get(i) + " > " + ht.exists(tokens.get(i).trim()));
        }
       
        int totalUseful = 0;
        for (String s: values){
            if (s.equalsIgnoreCase("token")){
                totalUseful++;
            }
        }
        
        if (totalUseful == 0) return "";
        
        for (String t: types){
            int actual = 0;
            int counter = 0;
            
            int k = 0;
            if (tokens.size() < values.size()) k = tokens.size();
            else if (tokens.size() > values.size()) k = values.size();
            else if (tokens.size() == values.size()) k = values.size();
            
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < k; i++){
                String s = tokens.get(i);
                String v = values.get(i);
                if (v.equalsIgnoreCase("token")){
                    //System.out.println(s + ">>" + v);
                    if (heo.searchForFrequency(s, t) > 0){
                        actual++;
                        counter += heo.searchForFrequency(s, t);
                    }
                }else {}
                
                sb.append(s).append(" ");
            }
            
            //System.out.println("Actual= " + actual + ": Tipo= " + t);
//            System.out.println(sb.toString());
//            System.out.println(t +": " + Double.valueOf(actual) + "/" + Double.valueOf(totalUseful) + " = " + (Double.valueOf(actual)/Double.valueOf(totalUseful)) );
            hm.put(t, (Double.valueOf(actual)/Double.valueOf(totalUseful)));
            hc.put(t, counter);
        }
        
        Double maior = 0.0;
        String result = "";
        
        Set<String> g = hm.keySet();
        for (String s: g){
            
            //System.out.println(s + ": " + hm.get(s) + ": " + totalUseful);
            if (hm.get(s) > maior){
                maior = hm.get(s);
                result = s;
            }
        }
        
        //Verify for draw
        int times = 0;
        for (String s: g){
            if (Double.compare(hm.get(s), maior) == 0){
                times++;
            }
        }
        
        int bigger = 0;
        
        if (times > 1){
            Set<String> c = hc.keySet();
            for (String s: c){
                if (hc.get(s) > bigger){
                    bigger = hc.get(s);
                    result = s;
                }
            } 
            //System.out.println("Entrou por empate: " + maior + ">" + result);
        }
        
        for (int i = 0; i < values.size(); i++){
            values.set(i, result);
        }
        
        return result;

    }
    
    private String printPartOfList(int begin, int end, LinkedList<SuperClasse> wl){
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i <= end; i++ ){
            sb.append(((TokenNP) wl.get(i)).getToken()).append(" ");
        }
        
        return sb.toString();
    }
    
    public static LinkedList<SuperClasse> createList(String entrada) throws FileNotFoundException, IOException{
        LinkedList<SuperClasse> ls = new LinkedList<SuperClasse>();
        // while the file was not completely read.
        String g[] = entrada.split(" ");

        for (String in: g){
            TokenNP t = new TokenNP(in, "symbol");
            if (!t.getToken().equalsIgnoreCase("")) ls.add(t);
        }

        return ls;

    }

       /**
     * This method performs a search for possible types that not exists in database.
     * @param global receives a list of tokens, loaded from a file.
     * @param formats is the formats loaded from file, compiled in a object Format.
     */
    public void verifyNoTypedTypes(LinkedList<SuperClasse> global, Formats formats){
        LinkedList<LinkedList<String>> format;
        ContentList aux, f;                               //aux percorre a lista, format o formato

        format = formats.getFormats();

        /* percorre a lista de formatos */
        for (LinkedList<String> l: format){
            for (String s: l){
                for (SuperClasse sc: global){
                    if (((TokenNP)sc).getTag().equalsIgnoreCase(s)){

                    }
                }
            }
        }
    }

    public LinkedList<SuperClasse> getList(){
        return wl;
    }
}
