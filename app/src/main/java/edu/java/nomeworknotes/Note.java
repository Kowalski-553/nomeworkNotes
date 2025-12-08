package edu.java.nomeworknotes;
import java.io.Serializable;
public class Note implements Serializable {
    private String title;
    private String description;
    private boolean isDone;
    private String deadline;

    public Note(String title, String description, boolean isDone, String deadline) {
        this.title = title;
        this.description = description;
        this.isDone = isDone;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
