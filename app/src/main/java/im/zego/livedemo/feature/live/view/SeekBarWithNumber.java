package im.zego.livedemo.feature.live.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSeekBar;
import im.zego.livedemo.R;

public class SeekBarWithNumber extends AppCompatSeekBar {

    /**
     * 文本的颜色
     */
    private int mTitleTextColor;
    /**
     * 文本的大小
     */
    private float mTitleTextSize;
    private String mTitleText;//文字的内容

    /**
     * 背景图片
     */
    private int img;
    private Bitmap map;
    //bitmap对应的宽高
    private float img_width, img_height;
    Paint paint;

    private float numTextWidth;
    //测量seekbar的规格
    private Rect rect_seek;
    private Paint.FontMetrics fm;
    /**
     * 文本中轴线X坐标
     */
    private float textCenterX;
    /**
     * 文本baseline线Y坐标
     */
    private float textBaselineY;

    private int offsetValue;

    public SeekBarWithNumber(Context context) {
        this(context, null);
    }

    public SeekBarWithNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarWithNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekBarWithNumber, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.SeekBarWithNumber_textSize) {
                mTitleTextSize = array.getDimension(attr, 15f);
            } else if (attr == R.styleable.SeekBarWithNumber_textColor) {
                mTitleTextColor = array.getColor(attr, Color.WHITE);
            } else if (attr == R.styleable.SeekBarWithNumber_textBackground) {
                img = array.getResourceId(attr, R.drawable.liveshow_icon_indicator);
            }
        }
        array.recycle();
        getImgWH();
        paint = new Paint();
        paint.setAntiAlias(true);//设置抗锯齿
        paint.setTextSize(mTitleTextSize);//设置文字大小
        paint.setColor(mTitleTextColor);//设置文字颜色
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        //设置控件的padding 给提示文字留出位置
        int halfBgWidth = (int) Math.ceil(img_width) / 2;
        int halfBgHeight = (int) Math.ceil(img_height) + 5;
        setPadding(halfBgWidth, halfBgHeight, halfBgWidth, 0);

    }

    /**
     * 获取图片的宽高
     */
    private void getImgWH() {
        map = BitmapFactory.decodeResource(getResources(), img);
        img_width = map.getWidth();
        img_height = map.getHeight();
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, Float alpha) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM == origin) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getTextLocation();//定位文本绘制的位置
        rect_seek = this.getProgressDrawable().getBounds();
        //定位文字背景图片的位置
        float bm_x = rect_seek.width() * (getProgress() - offsetValue) / getMax();

        float bm_y = 0.0f;
        //计算文字的中心位置在bitmap
        double text_x = bm_x + (img_width - numTextWidth) / 2;
        double text_y = textBaselineY + bm_y + (0.16 * img_height / 2);

        canvas.drawBitmap(map, bm_x, bm_y, paint);//画背景图
        canvas.drawText(mTitleText, (float) text_x, (float) text_y, paint);//画文字
    }

    // 计算SeekBar数值文字的显示位置
    private void getTextLocation() {
        fm = paint.getFontMetrics();
        mTitleText = getProgress() + "";
        numTextWidth = paint.measureText(mTitleText);
//        textBaselineY = (img_height / 2 - fm.descent + (fm.descent - fm.ascent) / 2);
        textBaselineY = (img_height - 8) / 2;
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress - offsetValue);
    }

    @Override
    public int getProgress() {
        return super.getProgress() + offsetValue;
    }

    public void setOffsetValue(int offset) {
        offsetValue = offset;
        postInvalidate();
    }

    public int getOffsetValue() {
        return offsetValue;
    }

    @Override
    public synchronized void setMax(int max) {
        super.setMax(max);
    }
}
