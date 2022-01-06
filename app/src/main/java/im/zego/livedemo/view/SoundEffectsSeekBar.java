package im.zego.livedemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import im.zego.livedemo.R;


public class SoundEffectsSeekBar extends ConstraintLayout {

    private TextView tvSeekbarProgress;
    private AppCompatSeekBar seekBar;

    public SoundEffectsSeekBar(@NonNull Context context) {
        this(context, null);
    }

    public SoundEffectsSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundEffectsSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarWithSound);
        View view = inflate(context, R.layout.layout_volume_seekbar, this);
        String name = typedArray.getString(R.styleable.SeekBarWithSound_seekbar_name);
        typedArray.recycle();

        TextView mTvSeekBarName = view.findViewById(R.id.tv_seekbar_name);
        tvSeekbarProgress = view.findViewById(R.id.tv_seekbar_progress);
        seekBar = view.findViewById(R.id.seekBar);

        mTvSeekBarName.setText(name);

    }

    public void setProgress(int progress) {
        tvSeekbarProgress.setText(String.valueOf(progress));
        seekBar.setProgress(progress);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        seekBar.setOnSeekBarChangeListener(listener);
    }
}
