package edu.java.nomeworknotes;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public NoteItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        // Чтобы у первого элемента не было отступа сверху, а отступ снизу был у всех, сделал так
        if (position != 0) {
            outRect.top = verticalSpaceHeight / 2;
        }
        outRect.bottom = verticalSpaceHeight / 2;
    }
}