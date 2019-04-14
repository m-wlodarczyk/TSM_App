package event;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class EventList {
    private List<Event> events;

    public EventList(JSONArray eventsJSON) {
        events = new ArrayList<>();
        events.add(new Event(eventsJSON.getJSONObject(0)));
        int id_i = events.get(0).getId();
        Event e = null;
        for (int i = 1; i < eventsJSON.length(); i++) {
            e = new Event(eventsJSON.getJSONObject(i));
            int id_e = e.getId();
            if (id_e != id_i) {
                id_i = id_e;
                events.add(e);
            }
        }
    }

    public List<Event> getEvents() {
        return this.events;
    }

    public Event get(int i) {
        return this.events.get(i);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public int length() {
        return this.events.size();
    }
}
