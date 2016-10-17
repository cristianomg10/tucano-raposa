/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NovoExtrator.structures;

import NovoExtrator.extrator.ListCreator;
import NovoExtrator.filehandlers.FileHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import NovoExtrator.structures.PCitation;

/**
 *
 */
public class Rules {

    private LinkedList<SuperClasse> wl;

    public Rules(LinkedList<SuperClasse> list) {
        wl = list;
    }

    public boolean textualOne(String s) {
        if (FileHandler.isNumber(s)) {
            return false;
        }
        if (ListCreator.isSymbol(s)) {
            return false;
        }
        if (s.equalsIgnoreCase("<barran>")) {
            return false;
        }
        if (s.length() < 2) {
            return false;
        }
        return true;
    }

    public HashMap<String, Integer> getPattern(PCitation p) {
        LinkedList<LinkedList<String>> padrao = new LinkedList();
        StringBuilder unit_pad = new StringBuilder();
        HashMap<String, Integer> h = new HashMap();

        for (int j = (p.getBegin() == -1 ? 0 : p.getBegin()); j <= p.getEnd(); j++) {
            SuperClasse sc = wl.get(j);

            if (sc instanceof TokenNP) {
                TokenNP t = (TokenNP) sc;

                if (textualOne(t.getToken())) {
                    if (h.containsKey(unit_pad.toString())) {
                        h.put(unit_pad.toString(), h.get(unit_pad.toString()) + 1);
                    } else {
                        h.put(unit_pad.toString(), 1);
                    }
                    return h;
                } else {
                    if (FileHandler.isNumber(t.getToken())) {
                        unit_pad.append("%d ");
                    } else if (ListCreator.isSymbol(t.getToken())) {
                        unit_pad.append(t.getToken()).append(" ");
                    } else if (t.getToken().length() < 2) {
                        unit_pad.append("%s ");
                    }

                }

            }
        }

        return h;
    }

    public void applyRules(Storage s) {
        restart:
        for (int i = 0; i < s.getStorage().size(); i++) {
            PCitation p = s.getStorage().get(i);

            //system.out.println(this.getPattern(p));

            boolean ret = this.dashRule(p);
            ret |= this.parenthesisRule(p);
            ret |= this.numberSignRule(p);
            ret |= this.equalSignRule(p);
            ret |= this.atQuestionRule(p);
            ret |= this.questionRule(p);
            ret |= this.plusSignRule(p);

            ret |= this.twoPointsRule(p);
            //ret &= this.titleRule(p);


            ////system.out.println();
            ////system.out.println(i + " > " + ret);

            if (ret) {
                //system.out.print("NÃO PASSOU: ");
                for (int j = p.getBegin(); j <= p.getEnd(); j++) {
                    //system.out.print(((TokenNP) wl.get(j)).getToken() + " ");
                }
                //system.out.println();

                for (int j = p.getBegin(); j <= p.getEnd(); j++) {
                    //system.out.print(((TokenNP) wl.get(j)).getTag() + " ");
                }
                //system.out.println();

                s.getStorage().remove(i);
                i--;
                //continue restart;
            } else {
                //system.out.print("PASSOU: ");
                for (int j = p.getBegin(); j <= p.getEnd(); j++) {
                    //system.out.print(((TokenNP) wl.get(j)).getToken() + " ");
                }
                //system.out.println();
            }

        }
    }

    public boolean datesRule(PCitation cit) {
        boolean found = false;

        for (int i = cit.getBegin(); i <= cit.getEnd() && cit.getBegin() > 0; i++) {
            SuperClasse sc = wl.get(i);
            if (sc instanceof TokenNP) {
                TokenNP t = (TokenNP) sc;
                ////system.out.println("ENTROU DATA");
                if (t.getTag().equalsIgnoreCase("date") && !found) {
                    found = true;
                } else if (t.getTag().equalsIgnoreCase("date") && found) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean pagesRule(PCitation cit) {
        boolean found = false;

        for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
            SuperClasse sc = wl.get(i);
            if (sc instanceof TokenNP) {
                TokenNP t = (TokenNP) sc;
                ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                if (t.getTag().equalsIgnoreCase("pages") && !found) {
                    found = true;
                } else if (t.getTag().equalsIgnoreCase("pages") && found) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean titleRule(PCitation cit) {
        int found = 0;

        for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
            SuperClasse sc = wl.get(i);
            if (sc instanceof TokenNP) {
                TokenNP t = (TokenNP) sc;
                ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                if (t.getTag().equalsIgnoreCase("title") && found < 3) {
                    found++;
                } else if (t.getTag().equalsIgnoreCase("title") && found == 3) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean dashRule(PCitation cit) {
        int found = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                    if (t.getToken().contains("- -")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean parenthesisRule(PCitation cit) {
        int open = 0;
        int close = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                    if (t.getToken().equals("(")) {
                        open++;
                    } else if (t.getToken().equals(")")) {
                        close++;
                    }

                    if (open > 3 || close > 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean questionRule(PCitation cit) {
        int open = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                    if (t.getToken().contains("?")) {
                        open++;
                    }

                    if (open >= 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean atQuestionRule(PCitation cit) {
        int open = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                    if (t.getToken().contains("@")) {
                        open++;
                    }

                    if (open == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean equalSignRule(PCitation cit) {
        int open = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">" + t.getToken().indexOf("="));
                    if (t.getToken().indexOf("=") != -1) {
                        open++;
                    }

                    if (open == 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean plusSignRule(PCitation cit) {
        int open = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">" + t.getToken().indexOf("="));
                    if (t.getToken().indexOf("+") != -1) {
                        open++;
                    }

                    if (open == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean twoPointsRule(PCitation cit) {
        int open = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">" + t.getToken().indexOf("="));
                    if (t.getToken().equals(":")) {
                        open++;
                    }

                    if (open >= 4) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean numberSignRule(PCitation cit) {
        int open = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                    if (t.getToken().contains("#")) {
                        open++;
                    }

                    if (open >= 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean bracketsRule(PCitation cit) {
        int open = 0;
        int close = 0;

        if (cit.getEnd() - cit.getBegin() > 0) {

            if (cit.getBegin() == -1) {
                cit.setBegin(0);
            }

            for (int i = cit.getBegin(); i <= cit.getEnd(); i++) {
                SuperClasse sc = wl.get(i);
                if (sc instanceof TokenNP) {
                    TokenNP t = (TokenNP) sc;
                    ////system.out.println(t.getToken() + "<" + t.getTag()  + ">");
                    if (t.getToken().contains("[")) {
                        open++;
                    } else if (t.getToken().contains("]")) {
                        close++;
                    }

                    if (open > 2 || close > 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void auxilie(LinkedList<Integer> b, LinkedList<Integer> start, Storage st, LinkedList<Integer> d) {
        b.clear();
        start.clear();


        boolean recognized = false;
        int count = 0;

        for (int i = 0; i < d.size(); i++) {
            if (d.get(i) <= 10 && !recognized) {
                count++;
                recognized = true;
            } else if (d.get(i) <= 10 && recognized) {
                count++;
            } else if (d.get(i) > 10 && recognized) {

                b.add(count);
                start.add(i - count);

                count = 0;
                recognized = false;



            } else if (d.get(i) > 10 && !recognized) {
            }
        }
    }

    public void recognizeBlocks(Storage st, LinkedList<Integer> d) {
        LinkedList<Integer> b = new LinkedList();
        LinkedList<Integer> start = new LinkedList();

        this.auxilie(b, start, st, d);

        int v = start.get(0);

        /* eliminate the first */
        LinkedList<PCitation> pc = (LinkedList<PCitation>) st.getStorage().clone();

        for (int i = 0; i <= v; i++) {
            st.getStorage().set(i, new PCitation(-1, -1));
        }

    }
}
