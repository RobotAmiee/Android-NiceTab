package me.amiee.nicetab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;


public class NiceTabLayout extends HorizontalScrollView {
    /**
     * Tab mode
     */
    public enum TabMode {
        TITLE_ONLY(0), // Default
        ICON_ONLY(1),
        BOTH(2);

        TabMode(int value) {
            intValue = value;
        }

        static TabMode fromInt(int intValue) {
            switch (intValue) {
                case 0:
                    return TITLE_ONLY;
                case 1:
                    return ICON_ONLY;
                case 2:
                    return BOTH;
            }
            return null;
        }

        final int intValue;
    }

    /**
     * Tab color blend mode
     */
    public enum TabColorBlendMode {
        NONE(0), // No color blend
        DEFAULT_SELECTED(1), // Default color and selected color blend, this is Default
        NEXT_SELECTED(2); // next tabs selected color blend

        TabColorBlendMode(int value) {
            intValue = value;
        }

        public static TabColorBlendMode fromInt(int intValue) {
            switch (intValue) {
                case 0:
                    return NONE;
                case 1:
                    return DEFAULT_SELECTED;
                case 2:
                    return NEXT_SELECTED;
            }
            return null;
        }

        final int intValue;
    }

    private static final int DEFAULT_DEFAULT_TAB_COLOR = 0x66000000;
    private static final int DEFAULT_SELECTED_TAB_COLOR = 0xffffffff;

    private static final int DEFAULT_TAB_OFFSET_DP = 24;
    private static final int DEFAULT_TAB_PADDING_TOP_BOTTOM_DP = 0;
    private static final int DEFAULT_TAB_PADDING_LEFT_RIGHT_DP = 16;
    private static final int DEFAULT_DRAWABLE_PADDING_DP = 0;
    private static final int DEFAULT_TEXT_SIZE_SP = 12;
    private static final boolean DEFAULT_TEXT_ALL_CAPS = true;
    private static final boolean DEFAULT_ICON_CROSS_FADE = true;
    private static final boolean DEFAULT_ICON_TINT = true;

    private TabMode mTabMode;
    private TabColorBlendMode mTabColorBlendMode;
    private int mTabBackground;
    private int mTabOffset;
    private int[] mDefaultTabColors;
    private int[] mSelectedTabColors;
    private int mTabPaddingTop;
    private int mTabPaddingBottom;
    private int mTabPaddingLeft;
    private int mTabPaddingRight;
    private int mDrawablePadding;
    private int mTabViewLayoutId;
    private int mTabViewTextOrImageViewId;

    private float mTextSize;
    private boolean mTextAllCaps;
    private int mTextStyle;

    private boolean mIconCrossFade;
    private boolean mIconTint;

    private final NiceTabStrip mNiceTabStrip;
    private TabProvider mTabProvider;
    private TabColorize mTabColorize;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;
    private float mCurrentPositionOffset;
    private boolean mUserSetPadding;
    private int mCachedPaddingLeft;
    private int mCachedPaddingRight;
    private boolean mUserSetBackground;
    private Drawable mCachedBackground;

    public NiceTabLayout(Context context) {
        this(context, null);
    }

    public NiceTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NiceTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        setClipToPadding(false);

        mCachedPaddingLeft = getPaddingLeft();
        mCachedPaddingRight = getPaddingRight();
        mCachedBackground = getBackground();

        final DisplayMetrics dm = getResources().getDisplayMetrics();

        final float density = dm.density;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NiceTabLayout, defStyle, 0);

        mTabMode = TabMode.fromInt(a.getInt(R.styleable.NiceTabLayout_ntlTabMode, TabMode.TITLE_ONLY.intValue));

        mTabColorBlendMode = TabColorBlendMode.fromInt(a.getInt(R.styleable.NiceTabLayout_ntlTabColorBlendMode, TabColorBlendMode.DEFAULT_SELECTED.intValue));

        mTabBackground = a.getResourceId(R.styleable.NiceTabLayout_ntlTabBackground, NO_ID);

        mTabOffset = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlTabOffset, (int) (DEFAULT_TAB_OFFSET_DP * density));

        final int defaultTabColor = a.getColor(R.styleable.NiceTabLayout_ntlDefaultTabColor, DEFAULT_DEFAULT_TAB_COLOR);
        final int defaultTabColorsId = a.getResourceId(R.styleable.NiceTabLayout_ntlDefaultTabColors, NO_ID);
        mDefaultTabColors = (defaultTabColorsId == NO_ID) ? new int[]{defaultTabColor} : getResources().getIntArray(defaultTabColorsId);
        final int selectedTabColor = a.getColor(R.styleable.NiceTabLayout_ntlSelectedTabColor, DEFAULT_SELECTED_TAB_COLOR);
        final int selectedTabColorsId = a.getResourceId(R.styleable.NiceTabLayout_ntlSelectedTabColors, NO_ID);
        mSelectedTabColors = (selectedTabColorsId == NO_ID) ? new int[]{selectedTabColor} : getResources().getIntArray(selectedTabColorsId);

        final int padding = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlTabPadding, -1);
        mTabPaddingTop = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlTabPaddingTop, padding == -1 ? (int) (DEFAULT_TAB_PADDING_TOP_BOTTOM_DP * density) : 0);
        mTabPaddingBottom = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlTabPaddingBottom, padding == -1 ? (int) (DEFAULT_TAB_PADDING_TOP_BOTTOM_DP * density) : 0);
        mTabPaddingLeft = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlTabPaddingLeft, padding == -1 ? (int) (DEFAULT_TAB_PADDING_LEFT_RIGHT_DP * density) : 0);
        mTabPaddingRight = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlTabPaddingRight, padding == -1 ? (int) (DEFAULT_TAB_PADDING_LEFT_RIGHT_DP * density) : 0);
        mTabViewLayoutId = a.getResourceId(R.styleable.NiceTabLayout_ntlTabViewLayoutId, NO_ID);
        mTabViewTextOrImageViewId = a.getResourceId(R.styleable.NiceTabLayout_ntlTabViewTextOrImageViewId, NO_ID);

        mDrawablePadding = a.getDimensionPixelSize(R.styleable.NiceTabLayout_ntlDrawablePadding, (int) (DEFAULT_DRAWABLE_PADDING_DP * density));
        mTextSize = a.getDimension(R.styleable.NiceTabLayout_ntlTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP, dm));
        mTextAllCaps = a.getBoolean(R.styleable.NiceTabLayout_ntlTextAllCaps, DEFAULT_TEXT_ALL_CAPS);
        mTextStyle = a.getInt(R.styleable.NiceTabLayout_ntlTextStyle, Typeface.BOLD);

        mIconCrossFade = a.getBoolean(R.styleable.NiceTabLayout_ntlIconCrossFade, DEFAULT_ICON_CROSS_FADE);
        mIconTint = a.getBoolean(R.styleable.NiceTabLayout_ntlIconTint, DEFAULT_ICON_TINT);

        a.recycle();

        if (mTabViewLayoutId != NO_ID && mTabViewTextOrImageViewId != NO_ID) {
            setCustomTabView(mTabViewLayoutId, mTabViewTextOrImageViewId);
        }

        mTabColorize = new SimpleTabColorize();
        ((SimpleTabColorize) mTabColorize).setDefaultTabColors(mDefaultTabColors);
        ((SimpleTabColorize) mTabColorize).setSelectedTabColors(mSelectedTabColors);

        mNiceTabStrip = new NiceTabStrip(context, attrs);

        addView(mNiceTabStrip, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Set the custom {@link NiceTabStrip.TabStripColorize} to be used, if set to null, then uses default TabColorize
     * <p/>
     * If you only require simple customisation then you can use
     * {@link #setIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
     * similar effects.
     */
    public void setTabStripColorize(NiceTabStrip.TabStripColorize tabColorize) {
        mNiceTabStrip.setTabStripColorize(tabColorize);
    }

    /**
     * Set the custom {@link TabColorize} to be used, if set to null, then uses default TabColorize
     * <p/>
     * If you only require simple customisation then you can use
     * {@link #setIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
     * similar effects.
     */
    public void setTabColorize(TabColorize tabColorize) {
        if (tabColorize != null) {
            mTabColorize = tabColorize;
        } else {
            mTabColorize = new SimpleTabColorize();
            ((SimpleTabColorize) mTabColorize).setDefaultTabColors(mDefaultTabColors);
            ((SimpleTabColorize) mTabColorize).setSelectedTabColors(mSelectedTabColors);
        }
        resetTabStrip();
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setIndicatorColors(int... colors) {
        mNiceTabStrip.setIndicatorColors(colors);
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setDividerColors(int... colors) {
        mNiceTabStrip.setDividerColors(colors);
    }

    /**
     * Sets the colors to be used for each tab's default state. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setDefaultTabColors(int... colors) {
        mDefaultTabColors = colors;
        SimpleTabColorize simpleTabColorize = new SimpleTabColorize();
        simpleTabColorize.setDefaultTabColors(colors);
        simpleTabColorize.setSelectedTabColors(mSelectedTabColors);
        mTabColorize = simpleTabColorize;
        resetTabStrip();
    }

    /**
     * Sets the colors to be used for each tab's selected state. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedTabColors(int... colors) {
        mSelectedTabColors = colors;
        SimpleTabColorize simpleTabColorize = new SimpleTabColorize();
        simpleTabColorize.setDefaultTabColors(mDefaultTabColors);
        simpleTabColorize.setSelectedTabColors(colors);
        mTabColorize = simpleTabColorize;
        resetTabStrip();
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link NiceTabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param tabViewLayoutId          layout id to be inflated
     * @param tabViewTextOrImageViewId id of the {@link android.widget.TextView} or {@link android.widget.ImageView} in the inflated view
     */
    public void setCustomTabView(int tabViewLayoutId, int tabViewTextOrImageViewId) {
        mTabProvider = new TitleIconTabProvider(getContext(), tabViewLayoutId, tabViewTextOrImageViewId);
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param provider {@link TabProvider}
     */
    public void setCustomTabView(TabProvider provider) {
        mTabProvider = provider;
    }

    /**
     * Set the custom layout for title only to be inflated for the tab views.
     *
     * @param tabViewLayoutId layout id to be inflated
     * @param textViewId      id of the {@link android.widget.TextView} in the inflated view
     */
    public void setTitleTabView(int tabViewLayoutId, int textViewId) {
        mTabMode = TabMode.TITLE_ONLY;
        mTabProvider = new TitleIconTabProvider(getContext(), tabViewLayoutId, textViewId);
    }

    /**
     * Set the custom layout for icon only to be inflated for the tab views.
     *
     * @param tabViewLayoutId layout id to be inflated
     * @param imageViewId     id of the {@link android.widget.ImageView} in the inflated view
     */
    public void setIconTabView(int tabViewLayoutId, int imageViewId) {
        mTabMode = TabMode.ICON_ONLY;
        mTabProvider = new TitleIconTabProvider(getContext(), tabViewLayoutId, imageViewId);
    }

    /**
     * Set the custom layout for both title and icon to be inflated for the tab views.
     *
     * @param tabViewLayoutId   layout id to be inflated
     * @param textOrImageViewId id of the {@link android.widget.TextView} in the inflated view
     */
    public void setTitleIconTabView(int tabViewLayoutId, int textOrImageViewId) {
        mTabMode = TabMode.BOTH;
        mTabProvider = new TitleIconTabProvider(getContext(), tabViewLayoutId, textOrImageViewId);
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /**
     * Sets weather tab distribute evenly, if true, every tab width will set to the most wide tab width.
     */
    public void setDistributeEvenly(final boolean distributeEvenly) {
        mNiceTabStrip.setTabDistributeEvenly(distributeEvenly);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToTab(mViewPager.getCurrentItem(), 0);
            }
        }, 100);
    }

    /**
     * Sets weather selected tab layout in center.
     */
    public void setTabSelectedCenter(final boolean tabSelectedCenter) {
        mNiceTabStrip.setTabSelectedCenter(tabSelectedCenter);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToTab(mViewPager.getCurrentItem(), 0);
            }
        }, 100);
    }

    /**
     * Sets blurred view.
     *
     * @param blurredView            the view to be blurred. (Set null to cancel blur)
     * @param blurredBackgroundColor the view's background color. (if blurredView's background is color, give the color to me, if
     *                               is not color, give 0{@link android.graphics.Color#TRANSPARENT} to me, if blurredView's background is null, give a proper color to me, i.e
     *                               blurredView's parent background color or window's background color.
     */
    public void setBlurredView(View blurredView, int blurredBackgroundColor) {
        if (blurredView != null) {
            mUserSetBackground = false;
            setBackgroundDrawable(null);
            mUserSetBackground = true;
        } else {
            mUserSetBackground = false;
            setBackgroundDrawable(mCachedBackground);
            mUserSetBackground = true;
        }

        mNiceTabStrip.setBlurredView(blurredView, blurredBackgroundColor);
    }

    /**
     * Sets the blur radius.
     *
     * @param radius the blur radius.
     */
    public void setBlurRadius(int radius) {
        mNiceTabStrip.setBlurRadius(radius);
    }

    /**
     * Sets the blur down sample factor.
     *
     * @param factor the blur down sample factor.
     */
    public void setDownSampleFactor(int factor) {
        mNiceTabStrip.setDownSampleFactor(factor);

    }

    /**
     * Sets the blur overlay color.
     *
     * @param color the blur overlay color.
     */
    public void setOverlayColor(int color) {
        mNiceTabStrip.setOverlayColor(color);
    }

    public void invalidateBlur() {
        mNiceTabStrip.invalidate();
    }

    /**
     * Sets indicator underline divider draw order.
     */
    public void setDrawOrder(NiceTabStrip.DrawOrder drawOrder) {
        mNiceTabStrip.setDrawOrder(drawOrder);
    }

    /**
     * Sets underline show hide.
     *
     * @param showUnderline show hide.
     */
    public void setShowUnderline(boolean showUnderline) {
        mNiceTabStrip.setShowUnderline(showUnderline);
    }

    /**
     * Sets divider show hide.
     *
     * @param showDivider show hide.
     */
    public void setShowDivider(boolean showDivider) {
        mNiceTabStrip.setShowDivider(showDivider);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToTab(mViewPager.getCurrentItem(), 0);
            }
        }, 100);
    }

    /**
     * Sets indicator show hide.
     *
     * @param showIndicator show hide.
     */
    public void setShowIndicator(boolean showIndicator) {
        mNiceTabStrip.setShowIndicator(showIndicator);
    }

    /**
     * Sets tab mode.
     *
     * @param tabMode title only, icon only, both.
     */
    public void setTabMode(TabMode tabMode) {
        if (mTabMode != tabMode) {
            mTabMode = tabMode;
            populateTabStrip();
        }
    }

    /**
     * Sets tab color blend mode.
     *
     * @param tabColorBlendMode none, default selected, next selected.
     */
    public void setTabColorBlendMode(TabColorBlendMode tabColorBlendMode) {
        mTabColorBlendMode = tabColorBlendMode;
    }

    /**
     * Sets tab badge(normal size).
     *
     * @param position tab position.
     * @param text     tab badge text, if text is null, do nothing.
     */
    public void setBadge(int position, String text) {
        mNiceTabStrip.setBadge(position, text);
    }

    /**
     * Sets tab badge(small size).
     *
     * @param position tab position.
     */
    public void setBadgeSmall(int position) {
        mNiceTabStrip.setBadgeSmall(position);
    }

    /**
     * Remove tab badge with given position.
     *
     * @param position tab position.
     * @return the badge or null(no position or no badges).
     */
    public Badge removeBadge(int position) {
        return mNiceTabStrip.removeBadge(position);
    }

    /**
     * Gets tab badge text.
     *
     * @param position tab position.
     * @return null(small badge, or tab has no badge with given position) or badge text.
     */
    public String getBadgeText(int position) {
        return mNiceTabStrip.getBadgeText(position);
    }

    /**
     * Clears all tab badges.
     */
    public void clearBadge() {
        mNiceTabStrip.clearBadge();
    }

    /**
     * Register a callback to be invoked when indicator color changed.
     *
     * @param listener The callback that will run
     */
    public void setOnIndicatorColorChangedListener(NiceTabStrip.OnIndicatorColorChangedListener listener) {
        mNiceTabStrip.setOnIndicatorColorChangedListener(listener);
    }

    int getCachedPaddingLeft() {
        return mCachedPaddingLeft;
    }

    int getCachedPaddingRight() {
        return mCachedPaddingRight;
    }

    /**
     * Create a default view to be used for tabs.
     */
    private View createDefaultTabView(int position) {
        final PagerAdapter adapter = mViewPager.getAdapter();
        View view;

        switch (mTabMode) {
            case TITLE_ONLY: {
                TextView textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setText(adapter.getPageTitle(position));
                textView.setTextColor(mTabColorize.getDefaultTabColor(position));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                textView.setTypeface(null, mTextStyle);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

                if (mTabBackground != NO_ID) {
                    textView.setBackgroundResource(mTabBackground);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    TypedValue outValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    textView.setBackgroundResource(outValue.resourceId);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    textView.setAllCaps(mTextAllCaps);
                }

                textView.setPadding(mTabPaddingLeft, mTabPaddingTop, mTabPaddingRight, mTabPaddingBottom);

                if (position == mViewPager.getCurrentItem()) {
                    textView.setTextColor(mTabColorize.getSelectedTabColor(position));
                    textView.setSelected(true);
                }

                view = textView;
                break;
            }
            case ICON_ONLY: {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

                if (mTabBackground != NO_ID) {
                    imageView.setBackgroundResource(mTabBackground);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    TypedValue outValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    imageView.setBackgroundResource(outValue.resourceId);
                }

                Drawable drawable = ContextCompat.getDrawable(getContext(), ((IconTabProvider) adapter).getPageIconResId(position));

                if (mIconCrossFade && drawable instanceof StateListDrawable) {
                    try {
                        StateListDrawable stateListDrawable = (StateListDrawable) drawable;
                        int fadingIndex = StateListDrawableHelper.getStateDrawableIndex(stateListDrawable, new int[]{android.R.attr.state_selected});
                        Drawable fading = StateListDrawableHelper.getStateDrawable(stateListDrawable, fadingIndex);
                        int baseIndex = StateListDrawableHelper.getStateDrawableIndex(stateListDrawable, new int[]{0});
                        Drawable base = StateListDrawableHelper.getStateDrawable(stateListDrawable, baseIndex);
                        CrossFadeDrawable cd = new CrossFadeDrawable();
                        cd.setFading(fading);
                        tintDrawable(cd.getFading(), mTabColorize.getSelectedTabColor(position));
                        cd.setBase(base);
                        tintDrawable(cd.getBase(), mTabColorize.getDefaultTabColor(position));
                        imageView.setImageDrawable(cd);
                    } catch (Exception e) {
                        imageView.setImageDrawable(drawable);
                    }
                } else {
                    imageView.setImageDrawable(drawable);
                }

                imageView.setPadding(mTabPaddingLeft, mTabPaddingTop, mTabPaddingRight, mTabPaddingBottom);

                if (position == mViewPager.getCurrentItem()) {
                    Drawable d = imageView.getDrawable();
                    if (d instanceof CrossFadeDrawable) {
                        crossFadeDrawable(d, 1);
                    } else {
                        tintDrawable(d, mTabColorize.getSelectedTabColor(position));
                    }
                    imageView.setSelected(true);
                } else {
                    tintDrawable(imageView.getDrawable(), mTabColorize.getDefaultTabColor(position));
                }

                view = imageView;
                break;
            }
            case BOTH: {
                TextView textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setText(adapter.getPageTitle(position));
                textView.setTextColor(mTabColorize.getDefaultTabColor(position));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                textView.setTypeface(null, mTextStyle);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

                if (mTabBackground != NO_ID) {
                    textView.setBackgroundResource(mTabBackground);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    TypedValue outValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    textView.setBackgroundResource(outValue.resourceId);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    textView.setAllCaps(mTextAllCaps);
                }

                textView.setPadding(mTabPaddingLeft, mTabPaddingTop, mTabPaddingRight, mTabPaddingBottom);

                Drawable drawable = ContextCompat.getDrawable(getContext(), ((IconTabProvider) adapter).getPageIconResId(position));

                if (mIconCrossFade && drawable instanceof StateListDrawable) {
                    try {
                        StateListDrawable stateListDrawable = (StateListDrawable) drawable;
                        int fadingIndex = StateListDrawableHelper.getStateDrawableIndex(stateListDrawable, new int[]{android.R.attr.state_selected});
                        Drawable fading = StateListDrawableHelper.getStateDrawable(stateListDrawable, fadingIndex);
                        int baseIndex = StateListDrawableHelper.getStateDrawableIndex(stateListDrawable, new int[]{0});
                        Drawable base = StateListDrawableHelper.getStateDrawable(stateListDrawable, baseIndex);
                        CrossFadeDrawable cd = new CrossFadeDrawable();
                        cd.setFading(fading);
                        cd.setFading(fading);
                        tintDrawable(cd.getFading(), mTabColorize.getSelectedTabColor(position));
                        cd.setBase(base);
                        tintDrawable(cd.getBase(), mTabColorize.getDefaultTabColor(position));
                        textView.setCompoundDrawablesWithIntrinsicBounds(null, cd, null, null);
                    } catch (Exception e) {
                        textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    }
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                }

                textView.setCompoundDrawablePadding(mDrawablePadding);

                if (position == mViewPager.getCurrentItem()) {
                    textView.setTextColor(mTabColorize.getSelectedTabColor(position));

                    Drawable d = textView.getCompoundDrawables()[1];
                    if (d instanceof CrossFadeDrawable) {
                        crossFadeDrawable(d, 1);
                    } else {
                        tintDrawable(d, mTabColorize.getSelectedTabColor(position));
                    }
                    textView.setSelected(true);
                } else {
                    Drawable d = textView.getCompoundDrawables()[1];
                    if (!(d instanceof CrossFadeDrawable)) {
                        tintDrawable(d, mTabColorize.getDefaultTabColor(position));
                    }
                }

                view = textView;
                break;
            }
            default: {
                throw new IllegalStateException("Invalid tab mode: " + mTabMode);
            }
        }

        return view;
    }

    /**
     * Populate TabStrip tabs.
     */
    private void populateTabStrip() {
        mNiceTabStrip.removeAllViews();

        final PagerAdapter adapter = mViewPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();

        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View tabView;

            if (mTabProvider != null) {
                tabView = mTabProvider.createTabView(mNiceTabStrip, i, adapter);
            } else {
                tabView = createDefaultTabView(i);
            }

            tabView.setOnClickListener(tabClickListener);

            mNiceTabStrip.addView(tabView);
        }
    }

    /**
     * Reset all tab to defaults and select current page tab.
     */
    private void resetTabStrip() {
        int childCount = mNiceTabStrip.getChildCount();
        int selectedPosition = mViewPager.getCurrentItem();
        for (int i = 0; i < childCount; i++) {
            View view = getNeededView(i);

            switch (mTabMode) {
                case TITLE_ONLY: {
                    ((TextView) view).setTextColor(i == selectedPosition ? mTabColorize.getSelectedTabColor(i) : mTabColorize.getDefaultTabColor(i));
                    break;
                }
                case ICON_ONLY: {
                    Drawable drawable = ((ImageView) view).getDrawable();
                    if (mIconCrossFade && drawable instanceof CrossFadeDrawable) {
                        tintDrawable(((CrossFadeDrawable) drawable).getFading(), mTabColorize.getSelectedTabColor(i));
                        tintDrawable(((CrossFadeDrawable) drawable).getBase(), mTabColorize.getDefaultTabColor(i));
                        crossFadeDrawable(drawable, i == selectedPosition ? 1f : 0f);
                    } else {
                        final int color = i == selectedPosition ? mTabColorize.getSelectedTabColor(i) : mTabColorize.getDefaultTabColor(i);
                        tintDrawable(drawable, color);
                    }
                    break;
                }
                case BOTH: {
                    final int color = i == selectedPosition ? mTabColorize.getSelectedTabColor(i) : mTabColorize.getDefaultTabColor(i);
                    ((TextView) view).setTextColor(color);

                    Drawable drawable = ((TextView) view).getCompoundDrawables()[1];
                    if (mIconCrossFade && drawable instanceof CrossFadeDrawable) {
                        tintDrawable(((CrossFadeDrawable) drawable).getFading(), mTabColorize.getSelectedTabColor(i));
                        tintDrawable(((CrossFadeDrawable) drawable).getBase(), mTabColorize.getDefaultTabColor(i));
                        crossFadeDrawable(drawable, i == selectedPosition ? 1f : 0f);
                    } else {
                        tintDrawable(drawable, color);
                    }
                    break;
                }
            }

            view.setSelected(i == selectedPosition);
        }
    }

    private View getNeededView(int position) {
        if (mTabProvider != null && mTabProvider instanceof TitleIconTabProvider) {
            final int imageViewId = ((TitleIconTabProvider) mTabProvider).getTextOrImageViewId();
            return mNiceTabStrip.getChildAt(position).findViewById(imageViewId);
        } else {
            return mNiceTabStrip.getChildAt(position);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        changeTabLayoutPadding(mNiceTabStrip.isTabSelectedCenter(), mNiceTabStrip.isTabDistributeEvenly());
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if (mUserSetPadding) {
            mCachedPaddingLeft = getPaddingLeft();
            mCachedPaddingRight = getPaddingRight();
        }
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        if (mUserSetPadding) {
            mCachedPaddingLeft = getPaddingLeft();
            mCachedPaddingRight = getPaddingRight();
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        if (mUserSetBackground) {
            mCachedBackground = getBackground();
        }
    }

    void changeTabLayoutPadding(boolean tabSelectedCenter, boolean isTabDistributeEvenly) {
        if (tabSelectedCenter) {
            final int paddingLeft, paddingRight;
            if (isTabDistributeEvenly) {
                paddingLeft = (getWidth() - mNiceTabStrip.getTabEvenlyWidth()) / 2;
                paddingRight = (getWidth() - mNiceTabStrip.getTabEvenlyWidth()) / 2;
            } else {
                paddingLeft = (getWidth() - mNiceTabStrip.getFirstTabWidth()) / 2;
                paddingRight = (getWidth() - mNiceTabStrip.getLastTabWidth()) / 2;
            }
            mUserSetPadding = false;
            setPadding(paddingLeft, getPaddingTop(), paddingRight, getPaddingBottom());
            mUserSetPadding = true;
            setClipToPadding(false);
        } else {
            mUserSetPadding = false;
            setPadding(mCachedPaddingLeft, getPaddingTop(), mCachedPaddingRight, getPaddingBottom());
            mUserSetPadding = true;
        }
    }

    private void scrollToTab(int position, int positionOffset) {
        final int tabStripChildCount = mNiceTabStrip.getChildCount();
        if (tabStripChildCount == 0 || position < 0 || position >= tabStripChildCount) {
            return;
        }

        View selectedTab = mNiceTabStrip.getChildAt(position);
        if (selectedTab != null) {
            int targetScrollX = selectedTab.getLeft() + positionOffset;
            if (mNiceTabStrip.isTabSelectedCenter()) {
                targetScrollX -= (mNiceTabStrip.getChildAt(0).getWidth() - selectedTab.getWidth()) / 2;
            } else if (position > 0 || positionOffset > 0) {
                targetScrollX -= mTabOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final int tabStripChildCount = mNiceTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mNiceTabStrip.onViewPagerPageChanged(position, positionOffset);

            final float delta = positionOffset - mCurrentPositionOffset;
            mCurrentPositionOffset = positionOffset;

            final View tabView = mNiceTabStrip.getChildAt(position);
            final View nextTabView = mNiceTabStrip.getChildAt(position + 1);

            if (tabView != null && nextTabView != null && 0f < positionOffset && positionOffset < 1f) {
                final float offset;
                final int extraOffset;

                final int dividerWidthPadding = mNiceTabStrip.isShowDivider()
                        ? mNiceTabStrip.getDividerWidth() + mNiceTabStrip.getDividerPaddingLeft() + mNiceTabStrip.getDividerPaddingRight()
                        : 0;

                if (mNiceTabStrip.isTabSelectedCenter()) {
                    offset = (tabView.getWidth() + nextTabView.getWidth()) / 2 + dividerWidthPadding;
                } else {
                    offset = tabView.getWidth() + dividerWidthPadding;
                }

                extraOffset = (int) (positionOffset * offset);

                scrollToTab(position, extraOffset);

                if (mTabColorBlendMode != TabColorBlendMode.NONE) {
                    swapTabColor(position, positionOffset, delta < 0);
                }
                if (mIconCrossFade) {
                    swapTabCrossFade(position, positionOffset);
                }
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                scrollToTab(mViewPager.getCurrentItem(), 0);
                if (mViewPager.getCurrentItem() < (mNiceTabStrip.getChildCount() - 1)) {
                    if (mTabColorBlendMode != TabColorBlendMode.NONE) {
                        swapTabColor(mViewPager.getCurrentItem(), 0f, true);
                    }
                    if (mIconCrossFade) {
                        swapTabCrossFade(mViewPager.getCurrentItem(), 0f);
                    }
                }
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_SETTLING && mCurrentPositionOffset == 0f) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetTabStrip();
                    }
                }, 100);
            }

            mCurrentPositionOffset = 0f;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }
    }

    /**
     * Swap Tab effect(color or cross fade)
     *
     * @param position       The page position
     * @param positionOffset The page position offset
     */
    private void swapTabColor(int position, float positionOffset, boolean left) {
        final int colorDefault = mTabColorize.getDefaultTabColor(position);
        final int colorSelected = mTabColorize.getSelectedTabColor(position);
        final int nextColorDefault = mTabColorize.getDefaultTabColor(position + 1);
        final int nextColorSelected = mTabColorize.getSelectedTabColor(position + 1);

        int color = colorDefault;
        int nextColor = nextColorSelected;

        if (mTabColorBlendMode == TabColorBlendMode.DEFAULT_SELECTED) {
            if (colorDefault != colorSelected) {
                color = ColorUtils.blendColors(colorDefault, colorSelected, positionOffset);
            }

            if (nextColorDefault != nextColorSelected) {
                nextColor = ColorUtils.blendColors(nextColorSelected, nextColorDefault, positionOffset);
            }
        } else if (mTabColorBlendMode == TabColorBlendMode.NEXT_SELECTED) {
            if (!left) {
                if (colorDefault != colorSelected) {
                    color = ColorUtils.blendColors(colorDefault, colorSelected, positionOffset);
                }

                if (nextColorSelected != colorSelected) {
                    nextColor = ColorUtils.blendColors(nextColorSelected, colorSelected, positionOffset);
                }
            } else {
                if (colorSelected != nextColorSelected) {
                    color = ColorUtils.blendColors(colorSelected, nextColorSelected, 1 - positionOffset);
                }

                if (nextColorDefault != nextColorSelected) {
                    nextColor = ColorUtils.blendColors(nextColorDefault, nextColorSelected, 1 - positionOffset);
                }
            }
        }

        setTabColor(getNeededView(position), color);
        setTabColor(getNeededView(position + 1), nextColor);
    }

    private void swapTabCrossFade(int position, float positionOffset) {
        setTabCrossFade(position, 1f - positionOffset);
        setTabCrossFade(position + 1, positionOffset);
    }

    private void setTabColor(View view, int color) {
        switch (mTabMode) {
            case TITLE_ONLY: {
                setTabTitleColor((TextView) view, color);
                break;
            }
            case ICON_ONLY: {
                setTabIconColor((ImageView) view, color);
                break;
            }
            case BOTH: {
                setTabTitleIconColor((TextView) view, color);
                break;
            }
        }
    }

    private void setTabCrossFade(int position, float progress) {
        switch (mTabMode) {
            case ICON_ONLY: {
                setTabIconCrossFade(position, progress);
                break;
            }
            case BOTH: {
                setTabTitleIconCrossFade(position, progress);
                break;
            }
        }
    }

    private void setTabTitleColor(TextView tabTitleView, int color) {
        tabTitleView.setTextColor(color);
    }

    private void setTabIconColor(ImageView tabIconView, int color) {
        Drawable drawable = tabIconView.getDrawable();
        if (!(drawable instanceof CrossFadeDrawable)) {
            tintDrawable(drawable, color);
        }
    }

    private void setTabIconCrossFade(int position, float progress) {
        final ImageView tabIconView = (ImageView) getNeededView(position);

        Drawable drawable = tabIconView.getDrawable();
        if (drawable instanceof CrossFadeDrawable) {
            crossFadeDrawable(drawable, progress);
        }
    }

    private void setTabTitleIconColor(TextView tabTitleView, int color) {
        Drawable drawable = tabTitleView.getCompoundDrawables()[1];
        if (!(drawable instanceof CrossFadeDrawable)) {
            tintDrawable(drawable, color);
        }

        tabTitleView.setTextColor(color);
    }

    private void setTabTitleIconCrossFade(int position, float progress) {
        final TextView tabTitleView = (TextView) getNeededView(position);

        Drawable drawable = tabTitleView.getCompoundDrawables()[1];
        if (drawable instanceof CrossFadeDrawable) {
            crossFadeDrawable(drawable, progress);
        }
    }

    private void tintDrawable(Drawable drawable, int color) {
        if (mIconTint) {
            drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    private void crossFadeDrawable(Drawable drawable, float progress) {
        CrossFadeDrawable cd = (CrossFadeDrawable) drawable.mutate();
        cd.setProgress(progress);
    }

    private class TabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            for (int i = 0; i < mNiceTabStrip.getChildCount(); i++) {
                if (v == mNiceTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

    /**
     * Create the custom tabs in the tab layout. Set with
     * {@link #setCustomTabView(TabProvider)}
     */
    public interface TabProvider {

        /**
         * @return Return the View of {@code position} for the Tabs
         */
        View createTabView(ViewGroup container, int position, PagerAdapter adapter);
    }

    public interface IconTabProvider {

        /**
         * @return Return the resource id of {@code position} for the Tabs' Icons
         */
        int getPageIconResId(int position);
    }

    private class TitleIconTabProvider implements TabProvider {
        private final LayoutInflater mInflater;
        private final int mTabViewLayoutId;
        private final int mTabViewTextOrImageViewId;

        private TitleIconTabProvider(Context context, int tabViewLayoutId, int textOrImageViewId) {
            if (tabViewLayoutId == NO_ID || textOrImageViewId == NO_ID) {
                throw new NullPointerException("'tabViewLayoutId' or 'textOrImageViewId' can not be NO_ID");
            }

            mInflater = LayoutInflater.from(context);
            mTabViewLayoutId = tabViewLayoutId;
            mTabViewTextOrImageViewId = textOrImageViewId;
        }

        @Override
        public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
            View tabView = mInflater.inflate(mTabViewLayoutId, container, false);

            View tabTextOrImageView = tabView.findViewById(mTabViewTextOrImageViewId);

            switch (mTabMode) {
                case TITLE_ONLY: {
                    TextView tabTextView = (TextView) tabTextOrImageView;
                    tabTextView.setText(adapter.getPageTitle(position));
                    // TODO　Set Color?
                    break;
                }
                case ICON_ONLY: {
                    if (adapter instanceof IconTabProvider) {
                        ImageView tabImageView = (ImageView) tabTextOrImageView;
                        tabImageView.setImageResource(((IconTabProvider) adapter).getPageIconResId(position));
                        // TODO　Set Color?
                    } else {
                        throw new ClassCastException("pager adapter must implements NiceTabLayout.IconTabProvider");
                    }
                    break;
                }
                case BOTH: {
                    TextView tabTextView = (TextView) tabTextOrImageView;
                    tabTextView.setText(adapter.getPageTitle(position));
                    if (adapter instanceof IconTabProvider) {
                        final int drawablePadding = tabTextView.getCompoundDrawablePadding();
                        tabTextView.setCompoundDrawablesWithIntrinsicBounds(0, ((IconTabProvider) adapter).getPageIconResId(position), 0, 0);
                        tabTextView.setCompoundDrawablePadding(drawablePadding);
                    } else {
                        throw new ClassCastException("pager adapter must implements NiceTabLayout.IconTabProvider");
                    }
                    // TODO　Set Color?
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid tab mode: " + mTabMode);
                }
            }

            return tabView;
        }

        public int getTextOrImageViewId() {
            return mTabViewTextOrImageViewId;
        }
    }

    /**
     * Allows complete control over the colors in the Tab view. Set with
     * {@link #setTabColorize(TabColorize)}.
     */
    public interface TabColorize {
        /**
         * @return return the default color of the Tab view of {@code position}.
         */
        int getDefaultTabColor(int position);

        /**
         * @return return the selected color of the Tab view of {@code position}.
         */
        int getSelectedTabColor(int position);
    }

    private static class SimpleTabColorize implements TabColorize {
        private int[] mDefaultTabColors;
        private int[] mSelectedTabColors;

        @Override
        public final int getDefaultTabColor(int position) {
            return mDefaultTabColors[position % mDefaultTabColors.length];
        }

        @Override
        public final int getSelectedTabColor(int position) {
            return mSelectedTabColors[position % mSelectedTabColors.length];
        }

        void setDefaultTabColors(int... colors) {
            mDefaultTabColors = colors;
        }

        void setSelectedTabColors(int... colors) {
            mSelectedTabColors = colors;
        }
    }

    private boolean mScrollable = true;

    /**
     * When tabs width less than layout width, set true to disable user scrolling.
     *
     * @param enabled enable user scroll or not.
     */
    void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return mScrollable && super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        return mScrollable && super.onInterceptTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        mNiceTabStrip.invalidate();
    }
}