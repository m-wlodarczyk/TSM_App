import event.EventList;

import java.io.File;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class Main {

    public static JSONObject convert(String xml) {
        if (xml == null || xml.isEmpty()) {
            return null;
        }
        return XML.toJSONObject(xml);
    }

    public static void main(String[] args) {
        final String fileName = "events.xml";
        File xmlFile = new File(fileName);
        JSONObject obj = null;
        try {
            byte[] b = Files.readAllBytes(xmlFile.toPath());
            String xml = new String(b);
            obj = convert(xml);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        EventList events = new EventList(obj.getJSONObject("root").getJSONArray("event"));
        for (int i=0; i<events.length(); i++){
            System.out.println(events.get(i));
        }
        System.out.println(events.length());
    }
}
