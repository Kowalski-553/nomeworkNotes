package edu.java.nomeworknotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import androidx.recyclerview.widget.DiffUtil;
import edu.java.nomeworknotes.databinding.ItemNoteBinding;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.content.Context;
import java.util.Calendar;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private OnDoneChangeListener onDoneChangeListener;

    public interface OnEditClickListener {
        void onEditClick(Note note);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Note note);
    }

    public interface OnDoneChangeListener {
        void onDoneChange(Note note);
    }

    public NoteAdapter(List<Note> noteList, OnEditClickListener onEditClickListener, OnDeleteClickListener onDeleteClickListener, OnDoneChangeListener onDoneChangeListener) {
        this.noteList = noteList;
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onDoneChangeListener = onDoneChangeListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.bind(note, onEditClickListener, onDeleteClickListener, onDoneChangeListener);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final ItemNoteBinding binding;

        NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    void bind(Note note, OnEditClickListener editListener, OnDeleteClickListener deleteListener, OnDoneChangeListener doneChangeListener) {
        binding.textTitle.setText(note.getTitle());
        binding.textDescription.setText(note.getDescription());
        binding.checkDone.setChecked(note.isDone());
        binding.textPriority.setText(note.getPriority()!=null?note.getPriority():"");
        binding.textDeadline.setText(note.getDeadline());
 
        // подсвечиваю просроченные сроки
        if (note.getDeadline()!=null && !note.getDeadline().isEmpty()) {
            Calendar deadlineTime = DeadlineNotificationService.parseDeadline(note.getDeadline());
            if (deadlineTime!=null && deadlineTime.getTimeInMillis() < System.currentTimeMillis()) {
                binding.textDeadline.setTextColor(android.graphics.Color.RED);
            } else {
                binding.textDeadline.setTextColor(android.graphics.Color.BLACK);
            }
        } else {
            binding.textDeadline.setTextColor(android.graphics.Color.BLACK);
        }
 
        binding.checkDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            note.setDone(isChecked);
            if (doneChangeListener != null) {
                doneChangeListener.onDoneChange(note);
            }
        });
 
        // контекстное меню по долгому нажатию
        binding.getRoot().setOnLongClickListener(v -> {
            showContextMenu(v, note, editListener, deleteListener);
            return true;
        });
    }

        private void showContextMenu(View view, Note note, OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.note_context_menu);

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_edit) {
                    editListener.onEditClick(note);
                    return true;
                } else if (id == R.id.menu_delete) {
                    showDeleteConfirmationDialog(view.getContext(), note, deleteListener);
                    return true;
                }
                return false;
            });
            // как-то сложно включаются иконки в контекстном меню. Или я чего-то не так делаю? Честно нагуглил только такое...
            try {
                Field mPopup = popup.getClass().getDeclaredField("mPopup");
                mPopup.setAccessible(true);
                Object menuPopupHelper = mPopup.get(popup);
                Method setForceShowIcon = menuPopupHelper.getClass().getMethod("setForceShowIcon", boolean.class);
                setForceShowIcon.invoke(menuPopupHelper, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            popup.show();
        }

        private void showDeleteConfirmationDialog(Context context, Note note, OnDeleteClickListener deleteListener) {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle(R.string.confirm_delete_title)
                    .setMessage(context.getString(R.string.confirm_delete_message, note.getTitle()))
                    .setPositiveButton(R.string.confirm_delete_positive, (dialog, which) -> deleteListener.onDeleteClick(note))
                    .setNegativeButton(R.string.confirm_delete_negative, null)
                    .show();
        }
    }

    private static class NoteDiffCallback extends DiffUtil.Callback {
        private final List<Note> oldList;
        private final List<Note> newList;

        NoteDiffCallback(List<Note> oldList, List<Note> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getTitle().equals(newList.get(newItemPosition).getTitle());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Note oldNote = oldList.get(oldItemPosition);
            Note newNote = newList.get(newItemPosition);
            return oldNote.getDescription().equals(newNote.getDescription()) &&
                    oldNote.isDone() == newNote.isDone();
        }
    }
    public void updateNotes(List<Note> newNotes) {
        this.noteList = new ArrayList<>(newNotes);
        notifyDataSetChanged();
    }
}
