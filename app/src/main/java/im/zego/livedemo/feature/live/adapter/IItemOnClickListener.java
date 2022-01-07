package im.zego.livedemo.feature.live.adapter;

import androidx.annotation.Nullable;

/**
 * Created by rocket_wang on 2021/12/29.
 */
public interface IItemOnClickListener<T> {
    void onClick(@Nullable T t);
}