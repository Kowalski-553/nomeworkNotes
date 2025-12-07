package edu.java.nomeworknotes;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import androidx.recyclerview.widget.DiffUtil;
import edu.java.nomeworknotes.databinding.ItemNoteBinding;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
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
        holder.bind(note);
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

        void bind(Note note) {
            binding.textTitle.setText(note.getTitle());
            binding.textDescription.setText(note.getDescription());
            binding.checkDone.setChecked(note.isDone());

            binding.checkDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                note.setDone(isChecked);
            });
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
        NoteDiffCallback callback = new NoteDiffCallback(this.noteList, newNotes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.noteList = new ArrayList<>(newNotes);
        diffResult.dispatchUpdatesTo(this);
    }
}