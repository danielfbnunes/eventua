package ua.pt.eventua.Entities;

public class Comment {

    private String image_resource;
    private String username;
    private String text;
    private String rating;

    public Comment(String image_resource, String username, String text, String rating) {
        this.image_resource = image_resource;
        this.username = username;
        this.text = text;
        this.rating = rating;
    }

    public String getImage_resource() {
        return image_resource;
    }

    public void setImage_resource(String image_resource) {
        this.image_resource = image_resource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
