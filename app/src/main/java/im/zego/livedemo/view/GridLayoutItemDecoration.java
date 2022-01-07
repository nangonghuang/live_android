package im.zego.livedemo.view;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private int mDividerWidth;

    public GridLayoutItemDecoration(int dividerWidth) {
        this.mDividerWidth = dividerWidth;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int top = 0;
        int left = 0;
        int right = 0;
        int bottom = 0;

        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        int spanSize = getSpanSize(parent, itemPosition);
        int childCount = parent.getAdapter().getItemCount();

        boolean firstColumn = isFirstColumn(parent, itemPosition, spanCount, childCount);
        boolean lastColumn = isLastColumn(parent, itemPosition, spanCount, childCount);
        boolean firstRow = isFirstRow(parent, itemPosition, spanCount, childCount);
        boolean lastRow = isLastRow(parent, itemPosition, spanCount, childCount);

        if (firstColumn) {
            left = 0;
            right = mDividerWidth / 2;
        } else if (lastColumn) {
            left = mDividerWidth / 2;
            right = 0;
        } else {
            left = mDividerWidth / 2;
            right = mDividerWidth / 2;
        }

        if (firstRow) {
            top = 0;
            bottom = mDividerWidth / 2;
        } else if (lastRow) {
            top = mDividerWidth / 2;
            bottom = 0;
        } else {
            top = mDividerWidth / 2;
            bottom = mDividerWidth / 2;
        }

        outRect.set(left, top, right, bottom);
    }

    private boolean isFirstColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos % spanCount) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return pos >= childCount - spanCount;
        }
        return false;
    }

    private boolean isFirstRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos < spanCount) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private int getSpanSize(RecyclerView parent, int position) {
        int spanSize = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanSize = ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(position);
        }
        return spanSize;
    }

}
