package me.amiee.nicetab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;


public class NiceTabStrip extends ViewGroup {
    private static final boolean DEFAULT_TAB_DISTRIBUTE_EVENLY = false;
    private static final boolean DEFAULT_TAB_SELECTED_CENTER = false;

    private static final int DEFAULT_BLUR_RADIUS = 16;
    private static final int DEFAULT_DOWN_SAMPLE_FACTOR = 8;
    private static final int DEFAULT_OVERLAY_COLOR = 0xaa000000;

    /**
     * Indicator underline divider draw order
     */
    public enum DrawOrder {
        INDICATOR_UNDERLINE_DIVIDER(0), // Default
        INDICATOR_DIVIDER_UNDERLINE(1),
        UNDERLINE_INDICATOR_DIVIDER(2),
        UNDERLINE_DIVIDER_INDICATOR(3),
        DIVIDER_INDICATOR_UNDERLINE(4),
        DIVIDER_UNDERLINE_INDICATOR(5);

        DrawOrder(int value) {
            intValue = value;
        }

        static DrawOrder fromInt(int intValue) {
            switch (intValue) {
                case 0:
                    return INDICATOR_UNDERLINE_DIVIDER;
                case 1:
                    return INDICATOR_DIVIDER_UNDERLINE;
                case 2:
                    return UNDERLINE_INDICATOR_DIVIDER;
                case 3:
                    return UNDERLINE_DIVIDER_INDICATOR;
                case 4:
                    return DIVIDER_INDICATOR_UNDERLINE;
                case 5:
                    return DIVIDER_UNDERLINE_INDICATOR;
            }
            return null;
        }

        final int intValue;
    }

    /**
     * Default underline attrs
     */
    private static final boolean DEFAULT_SHOW_UNDERLINE = true;

    public enum UnderlineGravity {
        TOP(0),
        BOTTOM(1); // Default

        UnderlineGravity(int value) {
            intValue = value;
        }

        static UnderlineGravity fromInt(int intValue) {
            switch (intValue) {
                case 0:
                    return TOP;
                case 1:
                    return BOTTOM;
            }
            return null;
        }

        final int intValue;
    }

    private static final int DEFAULT_UNDERLINE_COLOR = 0x33000000;
    private static final float DEFAULT_UNDERLINE_HEIGHT_DP = 2;

    /**
     * Default divider attrs
     */
    private static final boolean DEFAULT_SHOW_DIVIDER = true;
    private static final int DEFAULT_DIVIDER_COLOR = 0x20000000;
    private static final float DEFAULT_DIVIDER_WIDTH_DP = 1;
    private static final float DEFAULT_DIVIDER_PADDING_TOP_BOTTOM_DP = 8;
    private static final float DEFAULT_DIVIDER_PADDING_LEFT_RIGHT_DP = 0;

    /**
     * Default indicator attrs
     */
    private static final boolean DEFAULT_SHOW_INDICATOR = true;

    public enum IndicatorGravity {
        TOP(0),
        CENTER(1),
        BOTTOM(2); // Default

        IndicatorGravity(int value) {
            intValue = value;
        }

        static IndicatorGravity fromInt(int intValue) {
            switch (intValue) {
                case 0:
                    return TOP;
                case 1:
                    return CENTER;
                case 2:
                    return BOTTOM;
            }
            return null;
        }

        final int intValue;
    }

    private static final int DEFAULT_INDICATOR_COLOR = 0xff33b5e5;
    private static final float DEFAULT_INDICATOR_HEIGHT_DP = 8;
    private static final float DEFAULT_INDICATOR_CORNER_RADIUS_DP = 0;
    private static final float DEFAULT_INDICATOR_PADDING_TOP_BOTTOM_DP = 0;

    /**
     * Badge gravity
     */
    public enum BadgeGravity {
        LEFT(0),
        CENTER_LEFT(1),
        CENTER_RIGHT(2),
        RIGHT(3); // Default

        BadgeGravity(int value) {
            intValue = value;
        }

        static BadgeGravity fromInt(int intValue) {
            switch (intValue) {
                case 0:
                    return LEFT;
                case 1:
                    return CENTER_LEFT;
                case 2:
                    return CENTER_RIGHT;
                case 3:
                    return RIGHT;
            }
            return null;
        }

        final int intValue;
    }

    /**
     * Default badge attrs
     */
    private static final int DEFAULT_BADGE_TEXT_COLOR = 0xffffffff;
    private static final int DEFAULT_BADGE_TEXT_SIZE_SP = 10;
    private static final int DEFAULT_BADGE_CORNER_RADIUS_DP = 8;
    private static final int DEFAULT_BADGE_HEIGHT_DP = 16;
    private static final int DEFAULT_BADGE_MIN_WIDTH_DP = 16;
    private static final int DEFAULT_BADGE_MAX_WIDTH_DP = 32;
    private static final int DEFAULT_BADGE_PADDING_LEFT_RIGHT_DP = 4;
    private static final int DEFAULT_BADGE_SMALL_SIZE_DP = 8;
    private static final int DEFAULT_BADGE_BACKGROUND_COLOR = 0xffff2600;

    private boolean mTabDistributeEvenly;
    private boolean mTabSelectedCenter;

    private View mBlurredView; // if not null, than blur.
    private int mBlurredBackgroundColor; // if blurred view's background is null, this will used for blurred background.
    private int mBlurRadius;
    private int mDownSampleFactor;
    private int mOverlayColor;
    private int mBlurredViewWidth;
    private int mBlurredViewHeight;
    private Bitmap mBitmapToBlur;
    private Bitmap mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput;
    private Allocation mBlurOutput;
    private boolean mDownSampleFactorChanged;

    private DrawOrder mDrawOrder;

    // @begin underline properties
    private boolean mShowUnderline;
    private UnderlineGravity mUnderlineGravity;
    private int mUnderlineHeight;
    private int mUnderlinePaddingTop;
    private int mUnderlinePaddingBottom;
    private Paint mUnderlinePaint;
    // @end underline properties

    // @begin divider properties
    private boolean mShowDivider;
    private int mDividerWidth;
    private int[] mDividerColors;
    private int mDividerPaddingTop;
    private int mDividerPaddingBottom;
    private int mDividerPaddingLeft;
    private int mDividerPaddingRight;
    private Paint mDividerPaint;
    // @end divider properties

    // @begin indicator properties
    private boolean mShowIndicator;
    private IndicatorGravity mIndicatorGravity;
    private int[] mIndicatorColors;
    private int mIndicatorHeight;
    private float mIndicatorCornerRadius;
    private int mIndicatorPaddingTop;
    private int mIndicatorPaddingBottom;
    private Paint mIndicatorPaint;
    // @end indicator properties

    // @begin badge properties
    private BadgeGravity mBadgeGravity;
    private Drawable mBadgeBackground;
    private int mBadgeHeight;
    private int mBadgeMinWidth;
    private int mBadgeMaxWidth;
    private int mBadgeMarginLeft;
    private int mBadgeMarginRight;
    private int mBadgeMarginTop;
    private int mBadgePaddingLeftRight;
    private int mBadgeSmallSize;
    private TextPaint mBadgeTextPaint;
    // @end badge properties

    private int mSelectedPosition;
    private float mSelectionOffset;
    private int mFirstTabWidth;
    private int mLastTabWidth;
    private int mTabEvenlyWidth;
    private TabStripColorize mTabStripColorize;
    private Map<Integer, Badge> mBadgesMap;

    boolean isTabDistributeEvenly() {
        return mTabDistributeEvenly;
    }

    void setTabDistributeEvenly(boolean tabDistributeEvenly) {
        if (tabDistributeEvenly != mTabDistributeEvenly) {
            mTabDistributeEvenly = tabDistributeEvenly;
            requestLayout();
        }
    }

    boolean isTabSelectedCenter() {
        return mTabSelectedCenter;
    }

    void setTabSelectedCenter(boolean tabSelectedCenter) {
        if (tabSelectedCenter != mTabSelectedCenter) {
            mTabSelectedCenter = tabSelectedCenter;
            requestLayout();
        }
    }

    void setDrawOrder(DrawOrder drawOrder) {
        if (drawOrder != null && drawOrder != mDrawOrder) {
            mDrawOrder = drawOrder;
            invalidate();
        }
    }

    void setShowUnderline(boolean showUnderline) {
        if (showUnderline != mShowUnderline) {
            mShowUnderline = showUnderline;
            invalidate();
        }
    }

    boolean isShowDivider() {
        return mShowDivider;
    }

    void setShowDivider(boolean showDivider) {
        if (showDivider != mShowDivider) {
            mShowDivider = showDivider;
            requestLayout();
        }
    }

    int getDividerWidth() {
        return mDividerWidth;
    }

    int getDividerPaddingLeft() {
        return mDividerPaddingLeft;
    }

    int getDividerPaddingRight() {
        return mDividerPaddingRight;
    }

    void setShowIndicator(boolean showIndicator) {
        if (showIndicator != mShowIndicator) {
            mShowIndicator = showIndicator;
            invalidate();
        }
    }

    void setBadge(int position, String text) {
        if (position < 0 || position > getChildCount()) {
            throw new IllegalArgumentException("Position must between 0 with tabs count.");
        }

        if (mBadgesMap == null) {
            mBadgesMap = new HashMap<>();
        }

        if (text != null) {
            mBadgesMap.put(position, new Badge(text));
            invalidate();
        }
    }

    void setBadgeSmall(int position) {
        if (position < 0 || position > getChildCount()) {
            throw new IllegalArgumentException("Position must between 0 with tabs count.");
        }

        if (mBadgesMap == null) {
            mBadgesMap = new HashMap<>();
        }

        mBadgesMap.put(position, new Badge());
        invalidate();
    }

    Badge removeBadge(int position) {
        if (position < 0 || position > getChildCount()) {
            return null;
        }

        if (mBadgesMap == null) {
            return null;
        }

        Badge badge = mBadgesMap.remove(position);

        if (badge != null) {
            invalidate();
        }

        return badge;
    }

    String getBadgeText(int position) {
        return mBadgesMap == null || !mBadgesMap.containsKey(position) ? null : mBadgesMap.get(position).getText();
    }

    void clearBadge() {
        if (mBadgesMap != null) {
            mBadgesMap.clear();
            invalidate();
        }
    }

    int getFirstTabWidth() {
        return mFirstTabWidth;
    }

    int getLastTabWidth() {
        return mLastTabWidth;
    }

    int getTabEvenlyWidth() {
        return mTabEvenlyWidth;
    }

    public NiceTabStrip(Context context) {
        this(context, null);
    }

    public NiceTabStrip(Context context, AttributeSet attrs) {
        super(context);

        setWillNotDraw(false);

        final DisplayMetrics dm = getResources().getDisplayMetrics();

        final float density = dm.density;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NiceTabLayout);

        // Tab attrs
        mTabDistributeEvenly = a.getBoolean(R.styleable.NiceTabLayout_ntlTabDistributeEvenly, DEFAULT_TAB_DISTRIBUTE_EVENLY);
        mTabSelectedCenter = a.getBoolean(R.styleable.NiceTabLayout_ntlTabSelectedCenter, DEFAULT_TAB_SELECTED_CENTER);

        mBlurRadius = a.getInt(R.styleable.NiceTabLayout_ntlBlurRadius, DEFAULT_BLUR_RADIUS);
        mDownSampleFactor = a.getInt(R.styleable.NiceTabLayout_ntlDownSampleFactor, DEFAULT_DOWN_SAMPLE_FACTOR);
        mOverlayColor = a.getInt(R.styleable.NiceTabLayout_ntlOverlayColor, DEFAULT_OVERLAY_COLOR);

        mDrawOrder = DrawOrder.fromInt(a.getInt(R.styleable.NiceTabLayout_ntlDrawOrder, DrawOrder.INDICATOR_UNDERLINE_DIVIDER.intValue));

        // Underline attrs
        mShowUnderline = a.getBoolean(R.styleable.NiceTabLayout_ntlShowUnderline, DEFAULT_SHOW_UNDERLINE);
        mUnderlineGravity = UnderlineGravity.fromInt(a.getInt(R.styleable.NiceTabLayout_ntlUnderlineGravity, UnderlineGravity.BOTTOM.intValue));
        final int underlineColor = a.getColor(R.styleable.NiceTabLayout_ntlUnderlineColor, DEFAULT_UNDERLINE_COLOR);
        mUnderlineHeight = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlUnderlineHeight, (int) (DEFAULT_UNDERLINE_HEIGHT_DP * density));
        mUnderlinePaddingTop = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlUnderlinePaddingTop, 0);
        mUnderlinePaddingBottom = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlUnderlinePaddingBottom, 0);

        mUnderlinePaint = new Paint();
        mUnderlinePaint.setColor(underlineColor);

        // Divider attrs
        mShowDivider = a.getBoolean(R.styleable.NiceTabLayout_ntlShowDivider, DEFAULT_SHOW_DIVIDER);
        int dividerColor = a.getColor(R.styleable.NiceTabLayout_ntlDividerColor, DEFAULT_DIVIDER_COLOR);
        final int dividerColorsId = a.getResourceId(R.styleable.NiceTabLayout_ntlDividerColors, NO_ID);
        mDividerColors = (dividerColorsId == NO_ID) ? new int[]{dividerColor} : getResources().getIntArray(dividerColorsId);
        mDividerWidth = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlDividerWidth, (int) (DEFAULT_DIVIDER_WIDTH_DP * density));
        final int padding = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlDividerPadding, -1);
        mDividerPaddingTop = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlDividerPaddingTop, padding == -1 ? (int) (DEFAULT_DIVIDER_PADDING_TOP_BOTTOM_DP * density) : 0);
        mDividerPaddingBottom = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlDividerPaddingBottom, padding == -1 ? (int) (DEFAULT_DIVIDER_PADDING_TOP_BOTTOM_DP * density) : 0);
        mDividerPaddingLeft = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlDividerPaddingLeft, padding == -1 ? (int) (DEFAULT_DIVIDER_PADDING_LEFT_RIGHT_DP * density) : 0);
        mDividerPaddingRight = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlDividerPaddingRight, padding == -1 ? (int) (DEFAULT_DIVIDER_PADDING_LEFT_RIGHT_DP * density) : 0);

        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth(mDividerWidth);

        // Indicator attrs
        mShowIndicator = a.getBoolean(R.styleable.NiceTabLayout_ntlShowIndicator, DEFAULT_SHOW_INDICATOR);
        mIndicatorGravity = IndicatorGravity.fromInt(a.getInt(R.styleable.NiceTabLayout_ntlIndicatorGravity, IndicatorGravity.BOTTOM.intValue));
        final int indicatorColor = a.getColor(R.styleable.NiceTabLayout_ntlIndicatorColor, DEFAULT_INDICATOR_COLOR);
        final int indicatorColorsId = a.getResourceId(R.styleable.NiceTabLayout_ntlIndicatorColors, NO_ID);
        mIndicatorColors = (indicatorColorsId == NO_ID) ? new int[]{indicatorColor} : getResources().getIntArray(indicatorColorsId);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlIndicatorHeight, (int) (DEFAULT_INDICATOR_HEIGHT_DP * density));
        mIndicatorCornerRadius = a.getDimension(R.styleable.NiceTabLayout_ntlIndicatorCornerRadius, DEFAULT_INDICATOR_CORNER_RADIUS_DP * density);
        mIndicatorPaddingTop = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlIndicatorPaddingTop, (int) (DEFAULT_INDICATOR_PADDING_TOP_BOTTOM_DP * density));
        mIndicatorPaddingBottom = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlIndicatorPaddingBottom, (int) (DEFAULT_INDICATOR_PADDING_TOP_BOTTOM_DP * density));

        mIndicatorPaint = new Paint();

        // Badge attrs
        mBadgeGravity = BadgeGravity.fromInt(a.getInt(R.styleable.NiceTabLayout_ntlBadgeGravity, BadgeGravity.RIGHT.intValue));
        final int badgeTextColor = a.getColor(R.styleable.NiceTabLayout_ntlBadgeTextColor, DEFAULT_BADGE_TEXT_COLOR);
        final float badgeTextSize = a.getDimension(R.styleable.NiceTabLayout_ntlBadgeTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_BADGE_TEXT_SIZE_SP, dm));
        mBadgeHeight = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeHeight, (int) (DEFAULT_BADGE_HEIGHT_DP * density));
        final int badgeCornerRadius = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeCornerRadius, (int) (DEFAULT_BADGE_CORNER_RADIUS_DP * density));
        mBadgeMinWidth = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeMinWidth, (int) (DEFAULT_BADGE_MIN_WIDTH_DP * density));
        mBadgeMaxWidth = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeMaxWidth, (int) (DEFAULT_BADGE_MAX_WIDTH_DP * density));
        mBadgeMarginLeft = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeMarginLeft, 0);
        mBadgeMarginRight = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeMarginRight, 0);
        mBadgeMarginTop = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeMarginTop, 0);
        mBadgePaddingLeftRight = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgePaddingLeftRight, (int) (DEFAULT_BADGE_PADDING_LEFT_RIGHT_DP * density));
        mBadgeSmallSize = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlBadgeSmallSize, (int) (DEFAULT_BADGE_SMALL_SIZE_DP * density));
        if (a.hasValue(R.styleable.NiceTabLayout_ntlBadgeBackground)) {
            Drawable drawable = a.getDrawable(R.styleable.NiceTabLayout_ntlBadgeBackground);
            if (drawable instanceof ColorDrawable) {
                setUpBadgeBackground(badgeCornerRadius, a.getColor(R.styleable.NiceTabLayout_ntlBadgeBackground, DEFAULT_BADGE_BACKGROUND_COLOR));
            } else {
                mBadgeBackground = a.getDrawable(R.styleable.NiceTabLayout_ntlBadgeBackground);
            }
        } else {
            setUpBadgeBackground(badgeCornerRadius, DEFAULT_BADGE_BACKGROUND_COLOR);
        }

        mBadgeTextPaint = new TextPaint();
        mBadgeTextPaint.setAntiAlias(true);
        mBadgeTextPaint.setColor(badgeTextColor);
        mBadgeTextPaint.setTextSize(badgeTextSize);

        a.recycle();

        if (mDownSampleFactor <= 0) {
            throw new IllegalArgumentException("Down sample factor must be greater than 0.");
        }

        applyColor(mOverlayColor);

        initRenderScript(context);

        // Setup TabStripColorize
        mTabStripColorize = new SimpleTabStripColorize();
        ((SimpleTabStripColorize) mTabStripColorize).setDividerColors(mDividerColors);
        ((SimpleTabStripColorize) mTabStripColorize).setIndicatorColors(mIndicatorColors);
    }

    void setBlurredView(View blurredView, int blurredBackgroundColor) {
        mBlurredView = blurredView;
        mBlurredBackgroundColor = blurredBackgroundColor;
    }

    void setBlurRadius(int radius) {
        mBlurScript.setRadius(radius);
    }

    void setDownSampleFactor(int factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Down Sample factor must be greater than 0.");
        }

        if (mDownSampleFactor != factor) {
            mDownSampleFactor = factor;
            mDownSampleFactorChanged = true;
        }
    }

    void setOverlayColor(int color) {
        applyColor(color);
    }

    private void applyColor(int color) {
        int alpha = Color.alpha(color);
        if (alpha > 0xdd) {
            mOverlayColor = Color.argb(0xdd, Color.red(color), Color.green(color), Color.blue(color));
        } else {
            mOverlayColor = color;
        }
    }

    private void initRenderScript(Context context) {
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        mBlurScript.setRadius(mBlurRadius);
    }

    private void setUpBadgeBackground(float badgeCornerRadius, int color) {
        float[] cornerRadius = new float[]{badgeCornerRadius, badgeCornerRadius, badgeCornerRadius, badgeCornerRadius, badgeCornerRadius, badgeCornerRadius, badgeCornerRadius, badgeCornerRadius};
        mBadgeBackground = new ShapeDrawable(new RoundRectShape(cornerRadius, null, null));
        ((ShapeDrawable) mBadgeBackground).getPaint().setColor(color);
    }

    void setTabStripColorize(TabStripColorize tabStripColorize) {
        if (tabStripColorize != null) {
            mTabStripColorize = tabStripColorize;
        } else {
            mTabStripColorize = new SimpleTabStripColorize();
            ((SimpleTabStripColorize) mTabStripColorize).setDividerColors(mDividerColors);
            ((SimpleTabStripColorize) mTabStripColorize).setIndicatorColors(mIndicatorColors);
        }
        invalidate();
    }

    void setIndicatorColors(int... colors) {
        mIndicatorColors = colors;
        SimpleTabStripColorize simpleTabStripColorize = new SimpleTabStripColorize();
        simpleTabStripColorize.setIndicatorColors(colors);
        simpleTabStripColorize.setDividerColors(mDividerColors);
        mTabStripColorize = simpleTabStripColorize;
        invalidate();
    }

    void setDividerColors(int... colors) {
        mDividerColors = colors;
        SimpleTabStripColorize simpleTabStripColorize = new SimpleTabStripColorize();
        simpleTabStripColorize.setIndicatorColors(mIndicatorColors);
        simpleTabStripColorize.setDividerColors(colors);
        mTabStripColorize = simpleTabStripColorize;
        invalidate();
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        if (mBlurredView != null || mShowIndicator) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        NiceTabLayout tabLayout = ((NiceTabLayout) getParent());
        if (tabLayout.getMeasuredWidth() > 0) {
            int maxChildWidth = 0;
            int totalChildWidth = 0;

            final int count = getChildCount();

            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);

                final int childWidth = child.getMeasuredWidth();
                maxChildWidth = Math.max(maxChildWidth, childWidth);
                totalChildWidth += childWidth;

                if (i == 0) {
                    mFirstTabWidth = childWidth;
                }

                if (i == count - 1) {
                    mLastTabWidth = childWidth;
                }
            }

            final int totalDividerWidthPadding = mShowDivider ? (count - 1) * (mDividerWidth + mDividerPaddingLeft + mDividerPaddingRight) : 0;
            final int parentWidth = tabLayout.getMeasuredWidth() - tabLayout.getCachedPaddingLeft() - tabLayout.getCachedPaddingRight();

            if (mTabDistributeEvenly) {
                totalChildWidth = maxChildWidth * count;
                final int childWidth;

                if ((totalChildWidth + totalDividerWidthPadding) > parentWidth) {
                    childWidth = maxChildWidth;
                } else {
                    if (mTabSelectedCenter) {
                        childWidth = maxChildWidth;
                    } else {
                        childWidth = (parentWidth - totalDividerWidthPadding) / count;
                    }
                }

                mTabEvenlyWidth = childWidth;

                for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);

                    final int widthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
                    final int heightSpec = MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY);

                    child.measure(widthSpec, heightSpec);

                }
            }

            int mTotalWidth = totalChildWidth + totalDividerWidthPadding;
            int width = mTotalWidth;

            if (mTotalWidth < tabLayout.getMeasuredWidth()) {
                if (mTabSelectedCenter) {
                    width = mTotalWidth;
                    tabLayout.setScrollingEnabled(false);
                } else {
                    width = parentWidth;
                }
            }

            tabLayout.changeTabLayoutPadding(mTabSelectedCenter, mTabDistributeEvenly);

            final int height = resolveSize(0, heightMeasureSpec);

            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        int childLeft = 0;
        final int dividerPaddingWidth = mShowDivider ? mDividerWidth + mDividerPaddingRight + mDividerPaddingLeft : 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            child.layout(childLeft, parentTop, childLeft + width, Math.min(parentTop + height, parentBottom));
            childLeft += (width + dividerPaddingWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBlurBackground(canvas);

        switch (mDrawOrder) {
            case INDICATOR_UNDERLINE_DIVIDER: {
                drawIndicator(canvas);
                drawUnderline(canvas);
                drawDivider(canvas);
                break;
            }
            case INDICATOR_DIVIDER_UNDERLINE: {
                drawIndicator(canvas);
                drawDivider(canvas);
                drawUnderline(canvas);
                break;
            }
            case UNDERLINE_INDICATOR_DIVIDER: {
                drawUnderline(canvas);
                drawIndicator(canvas);
                drawDivider(canvas);
                break;
            }
            case UNDERLINE_DIVIDER_INDICATOR: {
                drawUnderline(canvas);
                drawDivider(canvas);
                drawIndicator(canvas);
                break;
            }
            case DIVIDER_INDICATOR_UNDERLINE: {
                drawDivider(canvas);
                drawIndicator(canvas);
                drawUnderline(canvas);
                break;
            }
            case DIVIDER_UNDERLINE_INDICATOR: {
                drawDivider(canvas);
                drawUnderline(canvas);
                drawIndicator(canvas);
                break;
            }
            default: {
                drawIndicator(canvas);
                drawUnderline(canvas);
                drawDivider(canvas);
                break;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
    }

    private void drawBlurBackground(Canvas canvas) {
        if (mBlurredView != null) {
            if (prepare()) {
                if (mBlurredBackgroundColor != 0) {
                    mBitmapToBlur.eraseColor(mBlurredBackgroundColor);
                }
                mBlurredView.draw(mBlurringCanvas);
                blur();

                NiceTabLayout tabLayout = (NiceTabLayout) getParent();

                if (tabLayout != null) {
                    final int pPaddingLeft = tabLayout.getPaddingLeft();
                    final int pPaddingRight = tabLayout.getPaddingRight();

                    canvas.save();

                    if (tabLayout.getPaddingLeft() != 0 || tabLayout.getPaddingRight() != 0) {
                        // allow drawing out of bounds
                        Rect clipBounds = canvas.getClipBounds();
                        clipBounds.inset(-(pPaddingLeft + pPaddingRight), 0);
                        canvas.clipRect(clipBounds, Region.Op.REPLACE);
                    }

                    canvas.translate(mBlurredView.getLeft() - getLeft() + tabLayout.getScrollX(), mBlurredView.getTop() - getTop());
                    canvas.scale(mDownSampleFactor, mDownSampleFactor);
                    canvas.drawBitmap(mBlurredBitmap, 0, 0, null);
                    canvas.drawColor(mOverlayColor);
                    canvas.restore();
                }
            }
        }
    }

    protected boolean prepare() {
        final int width = mBlurredView.getWidth();
        final int height = mBlurredView.getHeight();

        if (mBlurringCanvas == null || mDownSampleFactorChanged
                || mBlurredViewWidth != width || mBlurredViewHeight != height) {
            mDownSampleFactorChanged = false;

            mBlurredViewWidth = width;
            mBlurredViewHeight = height;

            int scaledWidth = width / mDownSampleFactor;
            int scaledHeight = height / mDownSampleFactor;

            // The following manipulation is to avoid some RenderScript artifacts at the edge.
            scaledWidth = scaledWidth - scaledWidth % 4 + 4;
            scaledHeight = scaledHeight - scaledHeight % 4 + 4;

            if (mBlurredBitmap == null
                    || mBlurredBitmap.getWidth() != scaledWidth
                    || mBlurredBitmap.getHeight() != scaledHeight) {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight,
                                                    Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }

                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight,
                                                     Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }
            }

            mBlurringCanvas = new Canvas(mBitmapToBlur);
            mBlurringCanvas.scale(1f / mDownSampleFactor, 1f / mDownSampleFactor);
            mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
        }
        return true;
    }

    protected void blur() {
        mBlurInput.copyFrom(mBitmapToBlur);
        mBlurScript.setInput(mBlurInput);
        mBlurScript.forEach(mBlurOutput);
        mBlurOutput.copyTo(mBlurredBitmap);
    }

    /**
     * Draw indicator
     *
     * @param canvas The canvas
     */
    private void drawIndicator(Canvas canvas) {
        if (mShowIndicator) {
            final int height = getHeight();
            final int childCount = getChildCount();

            if (childCount > 0) {
                View tabView = getChildAt(mSelectedPosition);
                int left = tabView.getLeft();
                int right = tabView.getRight();
                int color = mTabStripColorize.getIndicatorColor(mSelectedPosition);

                if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
                    int nextColor = mTabStripColorize.getIndicatorColor(mSelectedPosition + 1);
                    if (color != nextColor) {
                        color = ColorUtils.blendColors(nextColor, color, mSelectionOffset);
                    }

                    // Draw the selection partway between the tabs
                    View nextTabView = getChildAt(mSelectedPosition + 1);
                    left = (int) (mSelectionOffset * nextTabView.getLeft() + (1.0f - mSelectionOffset) * left);
                    right = (int) (mSelectionOffset * nextTabView.getRight() + (1.0f - mSelectionOffset) * right);
                }

                mIndicatorPaint.setColor(color);

                RectF rectF = new RectF(left, height - mIndicatorHeight - mIndicatorPaddingBottom, right, height - mIndicatorPaddingBottom);
                switch (mIndicatorGravity) {
                    case TOP: {
                        rectF.set(left, mIndicatorPaddingTop, right, mIndicatorHeight + mIndicatorPaddingTop);
                        break;
                    }
                    case CENTER: {
                        rectF.set(left, (height - mIndicatorHeight) / 2, right, (height + mIndicatorHeight) / 2);
                        break;
                    }
                }

                if (mIndicatorCornerRadius > 0f) {
                    mIndicatorPaint.setAntiAlias(true);
                    canvas.drawRoundRect(rectF, mIndicatorCornerRadius, mIndicatorCornerRadius, mIndicatorPaint);
                } else {
                    mIndicatorPaint.setAntiAlias(false);
                    canvas.drawRect(rectF, mIndicatorPaint);
                }

                if (mOnIndicatorColorChangedListener != null) {
                    mOnIndicatorColorChangedListener.onIndicatorColorChanged((NiceTabLayout) this.getParent(), color);
                }
            }
        }
    }

    /**
     * Draw underline
     *
     * @param canvas The canvas
     */
    private void drawUnderline(Canvas canvas) {
        if (mShowUnderline) {
            View parent = (View) getParent();
            if (parent != null && (parent.getPaddingLeft() != 0 || parent.getPaddingRight() != 0)) {
                final int pPaddingLeft = parent.getPaddingLeft();
                final int pPaddingRight = parent.getPaddingRight();

                canvas.save();

                // allow drawing out of bounds
                Rect clipBounds = canvas.getClipBounds();
                clipBounds.inset(-(pPaddingLeft + pPaddingRight), 0);
                canvas.clipRect(clipBounds, Region.Op.REPLACE);

                if (mUnderlineGravity == UnderlineGravity.TOP) {
                    // If on top ignore bottom padding
                    canvas.drawRect(-pPaddingLeft, mUnderlinePaddingTop, getWidth() + pPaddingLeft + pPaddingRight, mUnderlineHeight + mUnderlinePaddingTop, mUnderlinePaint);
                } else {
                    // If on bottom ignore top padding
                    canvas.drawRect(-pPaddingLeft, getHeight() - mUnderlineHeight - mUnderlinePaddingBottom, getWidth() + pPaddingLeft + pPaddingRight, getHeight() - mUnderlinePaddingBottom, mUnderlinePaint);
                }

                canvas.restore();
            } else {
                if (mUnderlineGravity == UnderlineGravity.TOP) {
                    // If on top ignore bottom padding
                    canvas.drawRect(0, mUnderlinePaddingTop, getWidth(), mUnderlineHeight + mUnderlinePaddingTop, mUnderlinePaint);
                } else {
                    // If on bottom ignore top padding
                    canvas.drawRect(0, getHeight() - mUnderlineHeight - mUnderlinePaddingBottom, getWidth(), getHeight() - mUnderlinePaddingBottom, mUnderlinePaint);
                }
            }
        }
    }

    /**
     * Draw divider
     *
     * @param canvas The canvas
     */
    private void drawDivider(Canvas canvas) {
        if (mShowDivider) {
            final int childCount = getChildCount();
            final int height = getHeight() - mDividerPaddingTop - mDividerPaddingBottom;

            for (int i = 0; i < childCount - 1; i++) {
                final View child = getChildAt(i);
                mDividerPaint.setColor(mTabStripColorize.getDividerColor(i));
                canvas.drawLine(child.getRight() + mDividerPaddingLeft, mDividerPaddingTop, child.getRight() + mDividerPaddingLeft,
                                mDividerPaddingTop + height, mDividerPaint);
            }
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        drawBadge(canvas);
    }

    /**
     * Draw badge
     *
     * @param canvas The canvas
     */
    private void drawBadge(Canvas canvas) {
        if (mBadgesMap != null && !mBadgesMap.isEmpty()) {
            int badgeLeft = 0, badgeRight = 0, badgeBottom, badgeWidth;
            int badgeHeight;
            float textWidth = 0, textHeight = 0;
            Rect textBounds = new Rect();

            for (int i : mBadgesMap.keySet()) {
                final View child = getChildAt(i);
                final Badge badge = mBadgesMap.get(i);
                String text = badge.getText();

                if (badge.isSmall()) {
                    badgeWidth = mBadgeSmallSize;
                    badgeHeight = mBadgeSmallSize;
                } else {
                    badgeHeight = mBadgeHeight;
                    if (text != null) {
                        mBadgeTextPaint.getTextBounds(text, 0, text.length(), textBounds);
                        textWidth = mBadgeTextPaint.measureText(text); // Use measureText to calculate width
                        textHeight = textBounds.height(); // Use height from getTextBounds()
                    } else {
                        textWidth = 0;
                        textHeight = 0;
                    }

                    if (textWidth > 0) {
                        if (textWidth > (mBadgeMaxWidth - mBadgePaddingLeftRight * 2)) {
                            textWidth = mBadgeMaxWidth - mBadgePaddingLeftRight * 2;
                            text = TextUtils.ellipsize(text, mBadgeTextPaint, textWidth, TextUtils.TruncateAt.END).toString();
                            badgeWidth = mBadgeMaxWidth;
                        } else {
                            badgeWidth = Math.max(Math.round((mBadgePaddingLeftRight * 2) + textWidth), mBadgeMinWidth);
                        }
                    } else {
                        badgeWidth = mBadgeMinWidth;
                    }
                }

                badgeBottom = mBadgeMarginTop + badgeHeight;

                switch (mBadgeGravity) {
                    case LEFT: {
                        badgeLeft = child.getLeft() + mBadgeMarginLeft;
                        badgeRight = badgeLeft + badgeWidth;
                        break;
                    }
                    case CENTER_LEFT: {
                        final int center = child.getRight() - child.getMeasuredWidth() / 2;
                        badgeRight = center - mBadgeMarginRight;
                        badgeLeft = badgeRight - badgeWidth;
                        break;
                    }
                    case CENTER_RIGHT: {
                        final int center = child.getRight() - child.getMeasuredWidth() / 2;
                        badgeLeft = center + mBadgeMarginLeft;
                        badgeRight = badgeLeft + badgeWidth;
                        break;
                    }
                    case RIGHT: {
                        badgeRight = child.getRight() - mBadgeMarginRight;
                        badgeLeft = badgeRight - badgeWidth;
                        break;
                    }
                }

                mBadgeBackground.setBounds(badgeLeft, mBadgeMarginTop, badgeRight, badgeBottom);
                mBadgeBackground.draw(canvas);

                if (text != null && textWidth > 0) {
                    canvas.drawText(text, mBadgeBackground.getBounds().centerX() - (textWidth / 2f), mBadgeBackground.getBounds().centerY() + (textHeight / 2f), mBadgeTextPaint);
                }
            }
        }
    }

    /**
     * Allows complete control over the colors in the tab layout. Set with
     * {@link #setTabStripColorize(TabStripColorize)}.
     */
    public interface TabStripColorize {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

        /**
         * @return return the color of the divider drawn to the right of {@code position}.
         */
        int getDividerColor(int position);
    }

    private static class SimpleTabStripColorize implements TabStripColorize {
        private int[] mIndicatorColors;
        private int[] mDividerColors;

        @Override
        public final int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        @Override
        public final int getDividerColor(int position) {
            return mDividerColors[position % mDividerColors.length];
        }

        void setIndicatorColors(int... colors) {
            mIndicatorColors = colors;
        }

        void setDividerColors(int... colors) {
            mDividerColors = colors;
        }
    }

    private OnIndicatorColorChangedListener mOnIndicatorColorChangedListener;

    void setOnIndicatorColorChangedListener(OnIndicatorColorChangedListener onIndicatorColorChangedListener) {
        mOnIndicatorColorChangedListener = onIndicatorColorChangedListener;
    }

    /**
     * Allows get current indicator color. Set with
     * {@link me.amiee.nicetab.NiceTabLayout#setOnIndicatorColorChangedListener(OnIndicatorColorChangedListener)}.
     */
    public interface OnIndicatorColorChangedListener {
        void onIndicatorColorChanged(NiceTabLayout tabLayout, int color);
    }
}
