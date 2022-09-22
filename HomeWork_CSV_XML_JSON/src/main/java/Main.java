import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //Task #1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list1 = parseCSV(columnMapping, fileName);
        String json1 = listToJson(list1);
        writeStringToJSON(json1, "new(csv) data.json");

        //Task #2
        String fileName2 = "data.xml";
        List<Employee> list2 = parseXML(fileName2);
        String json2 = listToJson(list2);
        writeStringToJSON(json2, "new(xml) data.json");

        //Task #3
        String fileName3 = "new(csv) data.json";
        String json3 = readString(fileName3);
        List<Employee> list3 = jsonToList(json3);
        list3.forEach(System.out::println);
    }

    private static List<Employee> jsonToList(String json3) {
        List<Employee> employeeList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json3);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object jsonObject : jsonArray) {
                Employee employee = gson.fromJson(jsonObject.toString(), Employee.class);
                employeeList.add(employee);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return employeeList;
    }

    private static String readString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            return String.valueOf(stringBuilder);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(fileName));

        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                String id = element.getElementsByTagName("id").item(0).getTextContent();
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                String age = element.getElementsByTagName("age").item(0).getTextContent();
                Employee employee = new Employee(Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age));
                employeeList.add(employee);
            }
        }
        return employeeList;

    }

    private static void writeStringToJSON(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
