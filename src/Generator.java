import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;


public class Generator {
    static int widthPage;
    static int heightPage;
    static List<Integer> width = new ArrayList<>();
    static List<String> title = new ArrayList<>();
    static StringBuilder builder = new StringBuilder();
    static int k = 0;

    public static void main(String[] args) {

        try {
            //считывание xml
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("settings.xml");
            Node page = document.getElementsByTagName("page").item(0);
            NodeList columns = document.getElementsByTagName("columns").item(0).getChildNodes();
            Element element = (Element) page;
            widthPage = parseInt(element.getElementsByTagName("width").item(0).getTextContent());
            heightPage = parseInt(element.getElementsByTagName("height").item(0).getTextContent());

            for (int i = 0; i < columns.getLength(); i++) {
                Node node = columns.item(i);
                if (node.getNodeType() != Node.TEXT_NODE) {
                    Element tmpElement = (Element) node;
                    width.add(parseInt(tmpElement.getElementsByTagName("width").item(0).getTextContent()));
                    title.add(tmpElement.getElementsByTagName("title").item(0).getTextContent());
                }
            }

            head();

            //чтение и обработка tsv
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("source-data.tsv"), "UTF-16"));
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                List<String> tmpList = new ArrayList<>();
                for (String str : tmp.split("\t")) {
                    tmpList.add(str);
                }
                strBuilder((ArrayList<String>) tmpList);
            }

            // Запись в файл
            PrintWriter writer = new PrintWriter("example-report.txt", "UTF-16");
            writer.println(builder);
            writer.close();
           // System.out.println(builder.toString());

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void head() {
        strConstructor((ArrayList<String>) title);
        strEmphasize();
    }

    //строковый контроллер
    public static void strBuilder(ArrayList<String> list) {
        if (list.get(0).length() <= width.get(0) && list.get(1).length() <= width.get(1) && list.get(2).length() <= width.get(2)) {
            strConstructor(list);
            strEmphasize();
        } else {
            int max = max(list);
            if (k+max+1> heightPage){
                builder.append("~\n");
                k=0;
                head();
            }
            complexStr(list);
        }
    }

    // Вычисление максимального необходимого количества строк
    public static int max(ArrayList<String> list) {
        int max;
        int a = list.get(0).length() / width.get(0);
        if (list.get(0).length() % width.get(0) != 0) a++;
        int b = list.get(1).length() / width.get(1);
        if (list.get(1).length() % width.get(1) != 0) b++;
        int c = list.get(2).length() / width.get(2);
        if (list.get(2).length() % width.get(2) != 0) c++;
        if (a > b) {
            if (a > c)
                max = a;
            else max = c;
        } else if (b > c)
            max = b;
        else max = c;
        return max;
    }

    // запись строки
    public static void strConstructor(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            builder.append("| ");
            builder.append(list.get(i));
            for (int j = 0; j <= width.get(i) - list.get(i).length(); j++)
                builder.append(" ");
        }
        builder.append("|\n");
        k++;
    }

    //подчеркивание
    public static void strEmphasize() {
        for (int i = 0; i < widthPage; i++)
            builder.append("-");
        builder.append("\n");
        k++;
    }

    // обработка длинных строк
    public static void complexStr(ArrayList<String> list) {
        List<String> tmpList1 = new ArrayList<>();
        List<String> tmpList2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).length() > width.get(i)) {
                if (list.get(i).charAt(width.get(i) - 2) == '-' || list.get(i).charAt(width.get(i) - 2) == '/') {
                    tmpList1.add(list.get(i).substring(width.get(i) - 1).trim());
                    tmpList2.add(list.get(i).substring(0, width.get(i) - 1).trim());
                } else {
                    tmpList1.add(list.get(i).substring(width.get(i)).trim());
                    tmpList2.add(list.get(i).substring(0, width.get(i)).trim());
                }
            } else {
                tmpList1.add("");
                tmpList2.add(list.get(i));
            }
        }
        strConstructor((ArrayList<String>) tmpList2);
        strBuilder((ArrayList<String>) tmpList1);
    }
}