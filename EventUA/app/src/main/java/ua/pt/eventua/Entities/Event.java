package ua.pt.eventua.Entities;

import java.util.ArrayList;

public class Event
{
    // Member variables representing the title and information about the sport.
    private String eventId, name, organizer, place, pic_url, start_at,  finish_at, latitute, longitude, description ;
    private Float price;
    private ArrayList<String> floors;
    public Event(String eventId, String name, String organizer, String place, ArrayList<String> floors, String pic_url,
                 String start_at, String finish_at, String latitute,
                 String longitude, String description, Float price )
    {
        this.eventId=eventId;
        this.name=name;
        this.organizer=organizer;
        this.place=place;
        this.floors=floors;
        this.pic_url=pic_url;
        this.start_at=start_at;
        this.finish_at=finish_at;
        this.latitute=latitute;
        this.longitude=longitude;
        this.description=description;
        this.price=price;
    }

    public Float getPrice() {return price;}

    public void setFloors(Float price) {price = price;}

    public ArrayList<String> getFloors() {return floors;}

    public void setFloors(ArrayList<String> f) {floors = f;}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getFinish_at() {
        return finish_at;
    }

    public void setFinish_at(String finish_at) {
        this.finish_at = finish_at;
    }

    public String getLatitute() {
        return latitute;
    }

    public void setLatitute(String latitute) {
        this.latitute = latitute;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
