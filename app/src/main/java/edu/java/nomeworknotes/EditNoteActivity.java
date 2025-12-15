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
import android.app.TimePickerDialog;
import edu.java.nomeworknotes.databinding.EditNoteActivityBinding;

public class EditNoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "extra_note";
    private static final String EXTRA_IS_NEW = "extra_is_new";

    private EditNoteActivityBinding binding;
    private boolean isNewNote;
    private Note currentNote;
    private ArrayAdapter<String> arrayAdapter;

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

        currentNote = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);
        isNewNote = getIntent().getBooleanExtra(EXTRA_IS_NEW, false);

        String[] priorities = getResources().getStringArray(R.array.priority_options);
        arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, priorities);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPriority.setAdapter(arrayAdapter);

        // если редактируем, то заполняем поля уже тем, что есть для этой заметки
        if (currentNote != null && !isNewNote) {
            binding.editTextTitle.setText(currentNote.getTitle());
            binding.editTextDescription.setText(currentNote.getDescription());
            binding.checkDone.setChecked(currentNote.isDone());
            binding.editTextDeadline.setText(currentNote.getDeadline());
            int idx = arrayAdapter.getPosition(currentNote.getPriority());
            if (idx >= 0) binding.spinnerPriority.setSelection(idx);
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
                        // Получаем текущее время для установки времени по умолчанию
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        // добавил выбор времени
                        TimePickerDialog timeDialog = new TimePickerDialog(
                                this,
                                (timeView, selectedHour, selectedMinute) -> {
                                    String date = String.format("%02d.%02d.%04d %02d:%02d", 
                                        selectedDay, selectedMonth + 1, selectedYear, selectedHour, selectedMinute);
                                    editTextDeadline.setText(date);
                                },
                                hour, minute, true);
                        timeDialog.show();
                    },
                    year, month, day
            );
            dialog.show();
        });

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

            String priority = (String) binding.spinnerPriority.getSelectedItem();

            if (isNewNote) {
                // Создаем новую заметку
                currentNote = new Note(
                        title,
                        binding.editTextDescription.getText().toString(),
                        binding.checkDone.isChecked(),
                        deadline,
                        priority
                );
            } else {
                // Обновляем существующую заметку
                currentNote.setTitle(title);
                currentNote.setDescription(binding.editTextDescription.getText().toString());
                currentNote.setDone(binding.checkDone.isChecked());
                currentNote.setDeadline(deadline);
                currentNote.setPriority(priority);
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_NOTE, currentNote);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
