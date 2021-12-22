package im.zego.livedemo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int mDividerWidth;
    private int mFirstRowTopMargin = 0;
    private boolean isNeedSpace = false;
    private boolean isLastRowNeedSpace = false;
    private Context mContext;

    public GridLayoutItemDecoration(Context context, int dividerWidth, boolean isNeedSpace) {
        this(context, dividerWidth, 0, isNeedSpace, false);
    }

    public GridLayoutItemDecoration(Context context, int dividerWidth, int firstRowTopMargin, boolean isNeedSpace) {
        this(context, dividerWidth, firstRowTopMargin, isNeedSpace, false);
    }

    public GridLayoutItemDecoration(Context context, int dividerWidth, int firstRowTopMargin, boolean isNeedSpace, boolean isLastRowNeedSpace) {
        this(context, dividerWidth, firstRowTopMargin, isNeedSpace, isLastRowNeedSpace, Color.TRANSPARENT);
    }

    public GridLayoutItemDecoration(Context context, int dividerWidth, int firstRowTopMargin, boolean isNeedSpace, boolean isLastRowNeedSpace, @ColorInt int color) {
        mDividerWidth = dividerWidth;
        this.isNeedSpace = isNeedSpace;
        this.mContext = context;
        this.isLastRowNeedSpace = isLastRowNeedSpace;
        this.mFirstRowTopMargin = firstRowTopMargin;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
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
        int maxAllDividerWidth = getMaxDividerWidth(view, spanCount / spanSize);

        int spaceWidth = 0;
        if (isNeedSpace)
            spaceWidth = mDividerWidth;

        int eachItemWidth = maxAllDividerWidth / spanCount;
        int dividerItemWidth = (maxAllDividerWidth - 2 * spaceWidth) / (spanCount - 1);

        left = itemPosition % spanCount * (dividerItemWidth - eachItemWidth) + spaceWidth;
        right = eachItemWidth - left;
        bottom = 0;
        if (mFirstRowTopMargin > 0 && isFirstRow(parent, itemPosition, spanCount, childCount)) {
            top = mFirstRowTopMargin;

        }
        if (!isLastRowNeedSpace && isLastRow(parent, itemPosition, spanCount, childCount)) {
        }

        if (isLastRow(parent, itemPosition, spanCount, childCount)) {
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            right = left;
        }

        if (childCount == 1) {
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            left = 0;
            right = 0;
            top = 0;
            bottom = 0;
        }

        outRect.set(left, top, right, bottom);

    }

    private int getMaxDividerWidth(View view, int spanCount) {
        int itemWidth = view.getLayoutParams().width;
        int itemHeight = view.getLayoutParams().height;

        int screenWidth = Math.min(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);

        int maxDividerWidth = screenWidth - itemWidth * spanCount;
        if (itemHeight < 0 || itemWidth < 0 || (isNeedSpace && maxDividerWidth <= (spanCount - 1) * mDividerWidth)) {
            view.getLayoutParams().width = getAttachCloumnWidth(spanCount);
            view.getLayoutParams().height = getAttachCloumnWidth(spanCount);

            maxDividerWidth = screenWidth - view.getLayoutParams().width * spanCount;
        }
        return maxDividerWidth;
    }

    private int getAttachCloumnWidth(int spanCount) {
        int itemWidth = 0;
        int spaceWidth = 0;
        try {
            int width = Math.min(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);
            if (isNeedSpace)
                spaceWidth = 2 * mDividerWidth;
            itemWidth = (width - spaceWidth) / spanCount - 40;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemWidth;
    }

    private boolean isFirstColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos % spanCount == 0) {
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
            return childCount > 1 && pos == childCount - 1;
        }
        return false;
    }

    private boolean isFirstRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos / spanCount + 1) == 1) {
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
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(position);
        }
        return spanCount;
    }

}
