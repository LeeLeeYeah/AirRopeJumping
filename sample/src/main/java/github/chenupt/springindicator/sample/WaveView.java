package github.chenupt.springindicator.sample;

        import android.content.Context;
        import android.content.res.TypedArray;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.graphics.Rect;
        import android.graphics.RectF;
        import android.os.Parcel;
        import android.os.Parcelable;
        import android.util.AttributeSet;
        import android.util.TypedValue;
        import android.view.View;
        import android.widget.ProgressBar;

/**
 * Created by kai.wang on 6/17/14.
 */
public class WaveView extends View {

    private Path aboveWavePath = new Path();

    private Path blowWavePath = new Path();

    private Paint aboveWavePaint = new Paint();

    private Paint blowWavePaint = new Paint();

    private final int default_above_wave_alpha = 50;

    private final int default_blow_wave_alpha = 30;

    private final int default_above_wave_color = 0xfffcfcfc;


    private final int default_blow_wave_color = 0xfffcfcfc;

    private final int default_progress = 80;

    private int waveToTop;

    private int aboveWaveColor;

    private int blowWaveColor;

    private int progress;

    private int offsetIndex = 0;

    /** wave length */
    private final int x_zoom = 150;

    /** wave crest */
    private final int y_zoom = 3;// 控制上下起伏的高度

    /** offset of X */
    private final float offset = 0.5f;

    private final float max_right = x_zoom * offset;

    // wave animation
    private float aboveOffset = 0.0f;

    private float blowOffset = 4.0f;

    /** offset of Y */
    private float animOffset = 0.05f;// 控制上下起伏的频率

    // refresh thread
    private RefreshProgressRunnable mRefreshProgressRunnable;

    private Rect r;

    private int mStokeWidth;

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, defStyle,
                0);
        mStokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources()
                .getDisplayMetrics());
        aboveWaveColor = attributes.getColor(R.styleable.WaveView_above_wave_color, default_above_wave_color);
        blowWaveColor = attributes.getColor(R.styleable.WaveView_blow_wave_color, default_blow_wave_color);
        progress = attributes.getInt(R.styleable.WaveView_progress, default_progress);

        setProgress(progress);

        initializePainters();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // r = new Rect(0, 0, getRight(), getBottom());
        // canvas.drawRect(r, mPaint);

        canvas.drawPath(blowWavePath, blowWavePaint);
        canvas.drawPath(aboveWavePath, aboveWavePaint);

        Paint mPaint = new Paint();
        mPaint.setColor(0xfffafafa);
        mPaint.setAntiAlias(true);// 抗锯齿
        // 绘制空心圆矩形
        canvas.save();
        Path path = new Path();
        path.reset();
        path.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF();
        rectF.set(0, 0, getWidth(), getHeight());
        path.addOval(rectF, Path.Direction.CCW);
        rectF.set(0, 0, getHeight(), getBottom());
        path.addRoundRect(rectF, 0, 0, Path.Direction.CW);
        canvas.drawPath(path, mPaint);
        canvas.restore();

        // 定义画笔2
        Paint paint2 = new Paint();
        // 消除锯齿
        paint2.setAntiAlias(true);
        // 设置画笔的颜色
        paint2.setColor(Color.GRAY);
        paint2.setStrokeWidth(mStokeWidth);

        paint2.setStyle(Paint.Style.STROKE);
        // 画一个空心圆
        canvas.drawCircle((float) ((getWidth() >> 1)), (float) (getHeight() >> 1),
                (float) ((getWidth() >> 1) - (paint2.getStrokeWidth()) / 2), paint2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    private void initializePainters() {
        aboveWavePaint.setColor(aboveWaveColor);
        aboveWavePaint.setAlpha(default_above_wave_alpha);
        aboveWavePaint.setStyle(Paint.Style.FILL);
        aboveWavePaint.setAntiAlias(true);

        blowWavePaint.setColor(blowWaveColor);
        blowWavePaint.setAlpha(default_blow_wave_alpha);
        blowWavePaint.setStyle(Paint.Style.FILL);
        blowWavePaint.setAntiAlias(true);
    }

    /**
     * calculate wave track
     */
    private void calculatePath() {

        aboveWavePath.reset();
        blowWavePath.reset();

        getWaveOffset();

        aboveWavePath.moveTo(0, getHeight());
        for (float i = 0; x_zoom * i <= getRight() + max_right; i += offset) {
            aboveWavePath.lineTo((x_zoom * i), (float) (y_zoom * Math.cos(i + aboveOffset)) + waveToTop);
        }
        aboveWavePath.lineTo(getRight(), getHeight());

        blowWavePath.moveTo(0, getHeight());
        for (float i = 0; x_zoom * i <= getRight() + max_right; i += offset) {
            blowWavePath.lineTo((x_zoom * i), (float) (y_zoom * Math.cos(i + blowOffset)) + waveToTop);
        }
        blowWavePath.lineTo(getRight(), getHeight());
    }

    public void setProgress(int progress) {
        this.progress = progress > 100 ? 100 : progress;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshProgressRunnable = new RefreshProgressRunnable();
        post(mRefreshProgressRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mRefreshProgressRunnable);
    }

    private void getWaveOffset() {
        if (blowOffset > Float.MAX_VALUE - 100) {
            blowOffset = 0;
        } else {
            blowOffset += animOffset;
        }

        if (aboveOffset > Float.MAX_VALUE - 100) {
            aboveOffset = 0;
        } else {
            aboveOffset += animOffset;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.progress = progress;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setProgress(ss.progress);
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (WaveView.this) {
                waveToTop = (int) (getHeight() * (1f - progress / 100f));

                calculatePath();

                invalidate();

                postDelayed(this, 16);
            }
        }
    }

    private static class SavedState extends BaseSavedState {
        int progress;

        /**
         * Constructor called from {@link ProgressBar#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
