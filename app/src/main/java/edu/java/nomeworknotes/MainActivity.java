package edu.java.nomeworknotes;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import edu.java.nomeworknotes.databinding.ActivityMainBinding;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


public class MainActivity extends AppCompatActivity {

    public static class NotesViewModel extends ViewModel {
        private final MutableLiveData<List<Note>> notes = new MutableLiveData<>();

        public LiveData<List<Note>> getNotes() {
            return notes;
        }

        public void setNotes(List<Note> newNotes) {
            notes.setValue(new ArrayList<>(newNotes));
        }
    }

    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private NotesViewModel viewModel;

    private ActivityResultLauncher<Intent> editNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                        List<Note> current = new ArrayList<>(this.viewModel.getNotes().getValue());
                        boolean found = false;
                        for (int i = 0; i < current.size(); i++) {
                            // пока ищем заметку для обновления по заголовку, но вообще тут нужен id
                            if (current.get(i).getTitle().equals(updatedNote.getTitle())) {
                                current.set(i, updatedNote);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            current.add(updatedNote);
                        }
                        this.viewModel.setNotes(current);
                    }
                });

        adapter = new NoteAdapter(new ArrayList<>(),
                note -> {
                    editNoteLauncher.launch(EditNoteActivity.editIntent(this, note));
                },
                note -> {
                    List<Note> current = new ArrayList<>(viewModel.getNotes().getValue());
                    current.remove(note);
                    viewModel.setNotes(current);
                }
        );
        recyclerView.setAdapter(adapter);

        // тестовые данные, временное решение
        if (viewModel.getNotes().getValue() == null) {
            List<Note> initialNotes = new ArrayList<>();
            initialNotes.add(new Note("Важная задача", "Сделать презентацию к пятнице. Нужно собрать данные по продажам.", false, "2026-06-01"));
            initialNotes.add(new Note("Покупки", "Молоко, хлеб, яйца, бананы. Не забыть корм для кота.", true, "2026-06-01"));
            initialNotes.add(new Note("Спорт", "Пойти в зал минимум 3 раза на этой неделе. Выполнить программу спины.", false, "2026-06-01"));
            viewModel.setNotes(initialNotes);
        }

        // смотрим за изменениями в ViewModel и обновляем адаптер (через адаптер?)
        viewModel.getNotes().observe(this, notes -> {
            adapter.updateNotes(notes);
            binding.emptyView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
        });

        setSupportActionBar(binding.toolbar);

        // кнопка добавления новой заметки
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = EditNoteActivity.newIntent(this);
            editNoteLauncher.launch(intent);
        });
    }
}