package im.zego.livedemo.feature.live.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

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

    public void notifySelect(int position, boolean canselSelect) {
        if (getData() == null || getData().size() == 0 || position >= getData().size()) {
            return;
        }
        switch (currentType) {
            case single:
                if (currentSelectItemPosition == position && canselSelect) {
                    currentSelectItemPosition = -1;
                    notifyItemChanged(position);
                } else {
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

    protected abstract void selectConvert(boolean isSelect, K helper, T item);

    public enum SelectType {
        single, multi
    }

    public interface MultiSelectAction<T> {

        boolean action(T t);
    }

}
