package edu.java.nomeworknotes;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    
    public NoteRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        noteDao = db.noteDao();
        allNotes = noteDao.getAllNotes();
    }
    
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
    
    public void insert(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            noteDao.insert(note);
        });
    }
    
    public void update(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            noteDao.update(note);
        });
    }
    
    public void delete(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            noteDao.delete(note);
        });
    }
    
    public void deleteById(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            noteDao.deleteById(id);
        });
    }
}