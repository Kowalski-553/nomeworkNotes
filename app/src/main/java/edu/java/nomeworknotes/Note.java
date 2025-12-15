package edu.java.nomeworknotes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    
    @ColumnInfo(name = "title")
    private String title;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "is_done")
    private boolean isDone;
    
    @ColumnInfo(name = "deadline")
    private String deadline;

    @ColumnInfo(name = "priority")
    private String priority;

    public Note(String title, String description, boolean isDone, String deadline, String priority) {
        this.title = title;
        this.description = description;
        this.isDone = isDone;
        this.deadline = deadline;
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
}
