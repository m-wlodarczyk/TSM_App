package event;

import org.json.JSONObject;

public class Event {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private String date;

    public Event() {
    }

    public Event(JSONObject eventJSON) {
        this.id = (Integer) eventJSON.get("event_id");
        this.name = eventJSON
                .getJSONObject("event_version")
                .getJSONObject("version")
                .getString("evtml_name");
        this.description = eventJSON
                .getJSONObject("event_version")
                .getJSONObject("version")
                .getString("evtml_desc");
        this.address = eventJSON
                .getJSONObject("event_address")
                .getString("street");
        this.date = eventJSON.getString("event_start");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Event [ id: " + id + "; name: " + name + "; address: " + address + "; date: " + date + " ]";
    }
}
