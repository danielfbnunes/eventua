package ua.pt.eventua.Entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Person
{
    // Member variables representing the title and information about the sport.
    int id;
    private String name;
    private String bio;
    private String imageResource;
    private String mail;
    private List<String> events;
    private PersonType pType;

    public enum PersonType { SPEAKER, PARTICIPANT;}



    public Person(int id, String name, String bio, String imageResource) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.imageResource = imageResource;
    }

    public void setMail(String mail){this.mail=mail;}
    public void setEvents(String e) {events = Arrays.asList(e.trim().split(","));}
    public void setEvents(List<String> e) {events = e;}
    public void setPersonType(PersonType t) {pType=t;}

    public int getId() {return id;}
    public String getName() {return name;}
    public String getBio() {return bio;}
    public String getImageResource() {return imageResource;}
    public String getMail() {return mail;}
    public List<String> getEvents() {return events;}
    public PersonType getPersonType() {return pType;}


}
