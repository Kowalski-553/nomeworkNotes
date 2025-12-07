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
    private List<Note> noteList;

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

        NotesViewModel viewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        adapter = new NoteAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // тестовые данные, временное решение
        if (viewModel.getNotes().getValue() == null) {
            List<Note> initialNotes = new ArrayList<>();
            initialNotes.add(new Note("Важная задача", "Сделать презентацию к пятнице. Нужно собрать данные по продажам.", false));
            initialNotes.add(new Note("Покупки", "Молоко, хлеб, яйца, бананы. Не забыть корм для кота.", true));
            initialNotes.add(new Note("Спорт", "Пойти в зал минимум 3 раза на этой неделе. Выполнить программу спины.", false));
            viewModel.setNotes(initialNotes);
        }

        // смотрим за изменениями в ViewModel и обновляем адаптер (через адаптер?)
        viewModel.getNotes().observe(this, notes -> {
            adapter.updateNotes(notes);
            binding.emptyView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
        });

        setSupportActionBar(binding.toolbar);
    }
}