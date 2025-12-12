package edu.java.nomeworknotes;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import edu.java.nomeworknotes.databinding.ActivityMainBinding;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.Application;
import androidx.core.splashscreen.SplashScreen;

import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity {

    public static class NotesViewModel extends AndroidViewModel {
        private NoteRepository repository;
        private LiveData<List<Note>> allNotes;
        
        public NotesViewModel(@NonNull Application application) {
            super(application);
            repository = new NoteRepository(application);
            allNotes = repository.getAllNotes();
        }
        
        public LiveData<List<Note>> getAllNotes() {
            return allNotes;
        }
        
        public void insert(Note note) {
            repository.insert(note);
        }
        
        public void update(Note note) {
            repository.update(note);
        }
        
        public void delete(Note note) {
            repository.delete(note);
        }
    }

    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private NotesViewModel viewModel;

    private ActivityResultLauncher<Intent> editNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        // читаем настройки из sharedPreferences
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // добавляем отступы используя декоратор
        int spaceInDp = (int) getResources().getDimension(R.dimen.vertical_spacing);
        recyclerView.addItemDecoration(new NoteItemDecoration(spaceInDp));

        this.viewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        // вернулись с экрана редактирования и обновляем список заметок в зависимости от результата
        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Note updatedNote = EditNoteActivity.getNoteFromResult(result.getData());
                        if (updatedNote.getId() > 0) {
                            // обновляем запись на экране
                            viewModel.update(updatedNote);
                        } else {
                            // или добавляем новую
                            viewModel.insert(updatedNote);
                        }
                    }
                });

        adapter = new NoteAdapter(new ArrayList<>(),
                note -> {
                    editNoteLauncher.launch(EditNoteActivity.editIntent(this, note));
                },
                note -> {
                    viewModel.delete(note);
                },
                note -> {
                    viewModel.update(note);
                }
        );
        recyclerView.setAdapter(adapter);

        // смотрим за изменениями в ViewModel и обновляем адаптер
        viewModel.getAllNotes().observe(this, notes -> {
            adapter.updateNotes(notes);
            binding.emptyView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // кнопка добавления новой заметки
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = EditNoteActivity.newIntent(this);
            editNoteLauncher.launch(intent);
        });
    }

    // добавляем меню в тулбар
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // обрабатываем нажатие на кнопку настроек
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}