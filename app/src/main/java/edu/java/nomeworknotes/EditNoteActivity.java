package edu.java.nomeworknotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import com.google.android.material.textfield.TextInputEditText;
import android.app.DatePickerDialog;
import edu.java.nomeworknotes.databinding.EditNoteActivityBinding;

public class EditNoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "extra_note";
    private static final String EXTRA_IS_NEW = "extra_is_new";

    private EditNoteActivityBinding binding;
    private boolean isNewNote;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, EditNoteActivity.class);
        intent.putExtra(EXTRA_IS_NEW, true);
        return intent;
    }

    public static Intent editIntent(Activity activity, Note note) {
        Intent intent = new Intent(activity, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        intent.putExtra(EXTRA_IS_NEW, false);
        return intent;
    }

    public static Note getNoteFromResult(Intent result) {
        return (Note) result.getSerializableExtra(EXTRA_NOTE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditNoteActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Note note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);
        isNewNote = getIntent().getBooleanExtra(EXTRA_IS_NEW, false);

        // если редактируем, то заполняем поля уже тем, что есть для этой заметки
        if (note != null && !isNewNote) {
            binding.editTextTitle.setText(note.getTitle());
            binding.editTextDescription.setText(note.getDescription());
            binding.checkDone.setChecked(note.isDone());
        }

        TextInputEditText editTextDeadline = binding.editTextDeadline;

        Calendar calendar = Calendar.getInstance();
        editTextDeadline.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // блин, депрекейтед, но у меня почему-то не получилось с MaterialDatePicker, попробую позже
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d.%02d.%04d", selectedDay, selectedMonth + 1, selectedYear);
                        editTextDeadline.setText(date);
                    },
                    year, month, day
            );
            dialog.show();
        });

        // формируем выпадашку с приоритетами
        String[] priorities = getResources().getStringArray(R.array.priority_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPriority.setAdapter(adapter);

        // кнопки отмены и сохранения
        binding.btnCancel.setOnClickListener(v -> {
                    setResult(RESULT_CANCELED, null);
                    finish();
                });
        binding.btnSave.setOnClickListener(v -> {
            String title = binding.editTextTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, R.string.msg_enter_title, Toast.LENGTH_SHORT).show();
                return;
            }

            String deadline = binding.editTextDeadline.getText().toString().trim();
            if (deadline.isEmpty()) {
                deadline = getString(R.string.deadline_not_set);
            }

            Note resultNote = new Note(
                    title,
                    binding.editTextDescription.getText().toString(),
                    binding.checkDone.isChecked(),
                    deadline
            );

            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_NOTE, resultNote);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}