package com.ljq.privatefiledemo.patternlockerjava;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ljq.privatefiledemo.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class PatternLockerViewJava extends View {
    private boolean enableClear;
    /**
     * 绘制完后是否自动清除标志位，如果开启了该标志位，延时@freezeDuration毫秒后自动清除已绘制图案
     */
    private boolean enableAutoClean;
    /**
     * 能否跳过中间点标志位，如果开启了该标志，则可以不用连续
     */
    private boolean enableSkip;
    /**
     * 是否开启触碰反馈，如果开启了该标志，则每连接一个cell则会震动
     */
    private boolean enableHapticFeedback;
    /**
     * 绘制完成后多久可以清除（单位ms），只有在@enableAutoClean = true 时有效
     */
    private int freezeDuration;
    /**
     * 绘制连接线
     */
    @Nullable
    private ILockerLinkedLineViewJava linkedLineView;
    /**
     * 绘制未操作时的cell样式
     */
    @Nullable
    private INormalCellViewJava normalCellView;
    /**
     * 绘制操作时的cell样式
     */
    @Nullable
    private IHitCellViewJava hitCellView;
    /**
     * 是否是错误的图案
     */
    private boolean isError;
    /**
     * 终点x坐标
     */
    private float endX;
    /**
     * 终点y坐标
     */
    private float endY;
    /**
     * 记录绘制多少个cell，用于判断是否调用OnPatternChangeListener
     */
    private int hitSize;
    /**
     * 真正的cell数组
     */
    private List<CellBeanJava> cellBeanList;
    /**
     * 记录已绘制cell的id
     */
    private OnPatternChangeListenerJava listener;
    /**
     * 监听器
     */
    private Runnable action;
    private List<Integer> hitIndexList;
    private static final String TAG = "PatternLockerView";

    public PatternLockerViewJava(Context context) {
        super(context);
    }

    public PatternLockerViewJava(Context context,
            @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);

    }

    public PatternLockerViewJava(Context context, @androidx.annotation.Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    void init(Context context, @androidx.annotation.Nullable AttributeSet attrs,
            int defStyleAttr) {
        this.enableClear = true;
        this.hitIndexList = new ArrayList<Integer>();
        this.initAttrs(context, attrs, defStyleAttr);
        this.initData();
        this.action = (Runnable) (new Runnable() {
            public final void run() {
                PatternLockerViewJava.this.setEnabled(true);
                if (PatternLockerViewJava.this.enableClear) {
                    PatternLockerViewJava.this.clearHitState();
                }
            }
        });
    }

    public PatternLockerViewJava(Context context, @androidx.annotation.Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    public final boolean getEnableAutoClean() {
        return this.enableAutoClean;
    }

    public final void setEnableAutoClean(boolean var1) {
        this.enableAutoClean = var1;
    }

    public final boolean getEnableSkip() {
        return this.enableSkip;
    }

    public final void setEnableSkip(boolean var1) {
        this.enableSkip = var1;
    }

    public final boolean getEnableHapticFeedback() {
        return this.enableHapticFeedback;
    }

    public final void setEnableHapticFeedback(boolean var1) {
        this.enableHapticFeedback = var1;
    }

    public final int getFreezeDuration() {
        return this.freezeDuration;
    }

    public final void setFreezeDuration(int var1) {
        this.freezeDuration = var1;
    }

    @Nullable
    public final ILockerLinkedLineViewJava getLinkedLineView() {
        return this.linkedLineView;
    }

    public final void setLinkedLineView(@Nullable ILockerLinkedLineViewJava var1) {
        this.linkedLineView = var1;
    }

    @Nullable
    public final INormalCellViewJava getNormalCellView() {
        return this.normalCellView;
    }

    public final void setNormalCellView(@Nullable INormalCellViewJava var1) {
        this.normalCellView = var1;
    }

    @Nullable
    public final IHitCellViewJava getHitCellView() {
        return this.hitCellView;
    }

    public final void setHitCellView(@Nullable IHitCellViewJava var1) {
        this.hitCellView = var1;
    }

    private List<Integer> getHitIndexList() {
        return this.hitIndexList;
    }

    public final void setOnPatternChangedListener(@Nullable OnPatternChangeListenerJava listener) {
        this.listener = listener;
    }

    /**
     * 更改状态
     */
    public final void updateStatus(boolean isError) {
        this.isError = isError;
        this.invalidate();
    }

    /**
     * 清除已绘制图案
     */
    public final void clearHitState() {
        this.clearHitData();
        this.isError = false;
        OnPatternChangeListenerJava var10000 = this.listener;
        if (var10000 != null) {
            var10000.onClear(this);
        }

        this.invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean var4 = false;
        int a = Math.min(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(a, a);
    }

    protected void onDraw(@NotNull Canvas canvas) {
        this.initCellBeanList();
        this.drawLinkedLine(canvas);
        this.drawCells(canvas);
    }

    public boolean onTouchEvent(@NotNull MotionEvent event) {
        if (!this.isEnabled()) {
            return super.onTouchEvent(event);
        }
        else {
            boolean isHandle = false;
            switch (event.getAction()) {
                case 0:
                    this.handleActionDown(event);
                    isHandle = true;
                    break;
                case 1:
                    this.handleActionUp(event);
                    isHandle = true;
                    break;
                case 2:
                    this.handleActionMove(event);
                    isHandle = true;
            }

            this.invalidate();
            return isHandle ? true : super.onTouchEvent(event);
        }
    }

    private final void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray var10000 =
                context.obtainStyledAttributes(attrs, R.styleable.PatternLockerView, defStyleAttr,
                        0);
        Intrinsics.checkNotNullExpressionValue(var10000,
                "context.obtainStyledAttr…kerView, defStyleAttr, 0)");
        TypedArray ta = var10000;
        int normalColor = ta.getColor(R.styleable.PatternLockerView_plv_color,
                DefaultConfigJava.getInstance().getDefaultNormalColor());
        int hitColor = ta.getColor(
                R.styleable.PatternLockerView_plv_hitColor,
                DefaultConfigJava.getInstance().getDefaultHitColor());
        int errorColor = ta.getColor(
                R.styleable.PatternLockerView_plv_errorColor,
                DefaultConfigJava.getInstance().getDefaultErrorColor());
        int fillColor = ta.getColor(
                R.styleable.PatternLockerView_plv_fillColor,
                DefaultConfigJava.getInstance().getDefaultFillColor());
        int var10001 = R.styleable.PatternLockerView_plv_lineWidth;
        DefaultConfigJava var10002 = DefaultConfigJava.getInstance();
        Resources var10003 = this.getResources();
        Intrinsics.checkNotNullExpressionValue(var10003, "resources");
        float lineWidth = ta.getDimension(var10001, var10002.getDefaultLineWidth(var10003));
        this.freezeDuration = ta.getInteger(R.styleable.PatternLockerView_plv_freezeDuration, 1000);
        this.enableAutoClean =
                ta.getBoolean(R.styleable.PatternLockerView_plv_enableAutoClean, true);
        this.enableHapticFeedback =
                ta.getBoolean(R.styleable.PatternLockerView_plv_enableHapticFeedback, false);
        this.enableSkip = ta.getBoolean(R.styleable.PatternLockerView_plv_enableSkip, false);
        ta.recycle();
        DefaultStyleDecoratorJava styleDecorator = new DefaultStyleDecoratorJava(normalColor,
                fillColor,
                hitColor, errorColor, lineWidth);
        this.normalCellView =
                (INormalCellViewJava) (new DefaultLockerNormalCellViewJava(styleDecorator));
        this.hitCellView = (IHitCellViewJava) (new DefaultLockerHitCellViewJava(styleDecorator));
        this.linkedLineView =
                (ILockerLinkedLineViewJava) (new DefaultLockerLinkedLineViewJava(styleDecorator));
    }

    private void initData() {
        this.getHitIndexList().clear();
    }

    private void initCellBeanList() {
        if (((PatternLockerViewJava) this).cellBeanList == null) {
            int w = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
            int h = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
            this.cellBeanList = CellFactoryJava.buildCells(w, h);
        }
    }

    private final void drawLinkedLine(Canvas canvas) {
        Collection var2 = (Collection) this.getHitIndexList();
        if (!var2.isEmpty()) {
            if (this.linkedLineView != null) {
                this.linkedLineView
                        .draw(canvas, this.getHitIndexList(), this.cellBeanList, this.endX,
                                this.endY, this.isError);
            }
        }
    }

    private final void drawCells(Canvas canvas) {
        List var10000 = this.cellBeanList;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cellBeanList");
        }

        Iterator var4 = var10000.iterator();

        while (var4.hasNext()) {
            Object element$iv = var4.next();
            CellBeanJava it = (CellBeanJava) element$iv;
            if (it.isHit() && this.hitCellView != null) {
                IHitCellViewJava var9 = this.hitCellView;
                if (var9 != null) {
                    var9.draw(canvas, it, this.isError);
                }
            }
            else {
                INormalCellViewJava var8 = this.normalCellView;
                if (var8 != null) {
                    var8.draw(canvas, it);
                }
            }
        }

    }

    private final void handleActionDown(MotionEvent event) {
        //1. reset to default state
        this.clearHitData();
        //2. update hit state
        this.updateHitState(event);
        //3. notify listener
        OnPatternChangeListenerJava var10000 = this.listener;
        if (var10000 != null) {
            var10000.onStart(this);
        }

    }

    private final void handleActionMove(MotionEvent event) {
        //1. update hit state
        this.updateHitState(event);
        //2. update end point
        this.endX = event.getX();
        this.endY = event.getY();
        //3. notify listener if needed
        int size = this.getHitIndexList().size();
        if (this.hitSize != size) {
            this.hitSize = size;
            OnPatternChangeListenerJava var10000 = this.listener;
            if (var10000 != null) {
                var10000.onChange(this, this.getHitIndexList());
            }
        }

    }

    private final void handleActionUp(MotionEvent event) {
        //1. update hit state
        this.updateHitState(event);
        //2. update end point
        this.endX = 0.0F;
        this.endY = 0.0F;
        //3. notify listener
        OnPatternChangeListenerJava var10000 = this.listener;
        if (var10000 != null) {
            var10000.onComplete(this, this.getHitIndexList());
        }
        //4. startTimer if needed
        if (this.enableAutoClean && this.getHitIndexList().size() > 0) {
            this.startTimer();
        }

    }

    private final void updateHitState(MotionEvent event) {
        List var10000 = this.cellBeanList;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cellBeanList");
        }

        Iterator var4 = var10000.iterator();

        while (var4.hasNext()) {
            Object element$iv = var4.next();
            CellBeanJava it = (CellBeanJava) element$iv;
            if (!it.isHit() && it.of(event.getX(), event.getY())) {
                if (!this.enableSkip) {
                    Collection var8 = (Collection) this.getHitIndexList();
                    boolean var9 = false;
                    if (!var8.isEmpty()) {
                        var10000 = this.cellBeanList;
                        if (var10000 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("cellBeanList");
                        }

//                        CellBean last = (CellBean)var10000.get(((Number)CollectionsKt.last(this.getHitIndexList())).intValue());
                        CellBeanJava last =
                                (CellBeanJava) var10000.get((this.getHitIndexList())
                                        .get(getHitIndexList().size() - 1));
                        int mayId = (last.getId() + it.getId()) / 2;
                        if (!this.getHitIndexList().contains(mayId)) {
                            int var10 = last.getX() - it.getX();
                            if (Math.abs(var10) % 2 == 0) {
                                var10 = last.getY() - it.getY();
                                if (Math.abs(var10) % 2 == 0) {
                                    var10000 = this.cellBeanList;
                                    if (var10000 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException(
                                                "cellBeanList");
                                    }

                                    ((CellBeanJava) var10000.get(mayId)).setHit(true);
                                    this.getHitIndexList().add(mayId);
                                }
                            }
                        }
                    }
                }

                it.setHit(true);
                this.getHitIndexList().add(it.getId());
                this.hapticFeedback();
            }
        }

    }

    private final void clearHitData() {
        List<Integer> list = this.getHitIndexList();
        if (!list.isEmpty()) {
            this.getHitIndexList().clear();
            this.hitSize = 0;
            List var10000 = this.cellBeanList;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("cellBeanList");
            }
            Iterator var3 = var10000.iterator();
            while (var3.hasNext()) {
                Object element$iv = var3.next();
                CellBeanJava it = (CellBeanJava) element$iv;
                it.setHit(false);
            }
        }

    }

    protected void onDetachedFromWindow() {
        this.setOnPatternChangedListener(null);
        this.removeCallbacks(this.action);
        super.onDetachedFromWindow();
    }

    private final void startTimer() {
        this.setEnabled(false);
        this.postDelayed(this.action, (long) this.freezeDuration);
    }

    private final void hapticFeedback() {
        if (this.enableHapticFeedback) {
            this.performHapticFeedback(1, 3);
        }

    }

}
