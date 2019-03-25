package ua.pt.eventua.Entities;

public class Talk
{
    // Member variables representing the title and information about the sport.
    private String title;
    private String hours;
    private String description;
    private String speakers;
    private String imageResource;
    private int talk_id;


    public Talk(String title, String hours, String speakers, String description, String imageResource) {
        this.title = title;
        this.hours = hours;
        this.speakers = speakers;
        this.description = description;
        this.imageResource = imageResource;
    }


    public String getTitle() {
        return title;
    }
    public String getHours() { return hours; }
    public String getSpeakers() { return speakers; }
    public String getDescription() { return description; }
    public String getImageResource() {return imageResource;}
    public void setTalkId(int id) {this.talk_id = id;}
    public int getTalkId() {return talk_id;}
}
