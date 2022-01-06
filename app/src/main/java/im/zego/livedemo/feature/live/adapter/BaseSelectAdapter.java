package im.zego.livedemo.feature.live.adapter;

import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.ArrayList;
import java.util.List;

/**
 * 单选和多选的BaseAdapter
 */
public abstract class BaseSelectAdapter<T, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    private List<T> multiSelectList;
    protected int currentSelectItemPosition = -1;
    private MultiSelectAction<T> selectAction;
    private SelectType currentType;

    public BaseSelectAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(K helper, T item) {
        boolean isSelect = false;
        switch (currentType) {
            case multi:
                isSelect = getMultiSelectItems().contains(item);
                break;
            case single:
                isSelect = (currentSelectItemPosition == helper.getAdapterPosition());
                break;
        }
        selectConvert(isSelect, helper, item);
    }

    /**
     * 点击Item的通知
     *
     * @param position
     */
    public void notifySelect(int position,boolean canselSelect) {
        if (getData() == null || getData().size() == 0 || position >= getData().size()) {
            return;
        }
        switch (currentType) {
            case single:
                if(currentSelectItemPosition == position && canselSelect){  //点击同一个按钮的时候，选中效果取消
                    currentSelectItemPosition = -1;
                    notifyItemChanged(position);
                }else {
                    int temp = currentSelectItemPosition;
                    currentSelectItemPosition = position;
                    notifyItemChanged(temp);
                    notifyItemChanged(position);
                }
                break;
            case multi:

                T item = getData().get(position);
                boolean isSelect = getMultiSelectItems().contains(item);
                if (!isSelect) {
                    getMultiSelectItems().add(item);
                } else {
                    getMultiSelectItems().remove(item);
                }
                notifyItemChanged(position);
                break;
            default:
                break;
        }
    }

    public BaseSelectAdapter setMultiSelectList(List<T> multiSelectList) {
        this.multiSelectList = multiSelectList;
        return this;
    }

    public BaseSelectAdapter setCurrentSelectItemPosition(int currentSelectItemPosition) {
        this.currentSelectItemPosition = currentSelectItemPosition;
        return this;
    }

    public BaseSelectAdapter<T, K> setCurrentType(SelectType currentType) {
        this.currentType = currentType;
        return this;
    }

    public BaseSelectAdapter setMultiSelectAction(MultiSelectAction<T> selectAction) {
        this.selectAction = selectAction;
        return this;
    }

    public T getSingeSelectItem() {
        return currentSelectItemPosition < 0 ? null : getData().get(currentSelectItemPosition);
    }

    public List<T> getMultiSelectItems() {
        if (multiSelectList == null) {
            multiSelectList = new ArrayList<>();
        }
        return multiSelectList;
    }

    /**
     * @param isSelect 是否被选中
     * @param helper
     * @param item
     */
    protected abstract void selectConvert(boolean isSelect, K helper, T item);

    /**
     * 单选还是多选
     */
    public enum SelectType {
        single, multi;
    }

    /**
     * 点击item发生的改变
     *
     * @param
     */
    public interface MultiSelectAction<T> {
        // 返回true为选中,false 不选中
        boolean action(T t);
    }

}
