package me.amiee.nicetab;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


class CrossFadeDrawable extends Drawable implements Drawable.Callback {
    private Drawable mFading;
    private Drawable mBase;
    private float mProgress;
    private int mAlpha;
    private int mChangingConfigs;
    private ColorFilter mColorFilter;
    private boolean mFilterBitmap;
    private boolean mDither;
    private int mColorFilterColor;
    private PorterDuff.Mode mColorFilterMode;

    public CrossFadeDrawable() {
    }

    public void setFading(Drawable d) {
        if (mFading != d) {
            if (mFading != null) {
                mFading.setCallback(null);
            }

            mFading = d;
            if (d != null) {
                initDrawable(d);
            }

            invalidateSelf();
        }
    }

    public void setBase(Drawable d) {
        if (mBase != d) {
            if (mBase != null) {
                mBase.setCallback(null);
            }

            mBase = d;
            initDrawable(d);
            invalidateSelf();
        }
    }

    public void setProgress(float progress) {
        float updated = clamp(progress);
        if (updated != mProgress) {
            mProgress = updated;
            invalidateSelf();
        }
    }

    private float clamp(float value) {
        return value < 0f ? 0f : (value > 1f ? 1f : value);
    }

    private void initDrawable(Drawable d) {
        d.setCallback(this);
        d.setState(getState());
        if (mColorFilter != null) {
            d.setColorFilter(mColorFilter);
        }

        if (mColorFilterMode != null) {
            d.setColorFilter(mColorFilterColor, mColorFilterMode);
        }

        d.setDither(mDither);
        d.setFilterBitmap(mFilterBitmap);
        d.setBounds(getBounds());
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBase != null && (mProgress < 1.0F || mFading == null)) {
            mBase.setAlpha(255);
            mBase.draw(canvas);
        }

        if (mFading != null && mProgress > 0.0F) {
            mFading.setAlpha((int) (255.0F * mProgress));
            mFading.draw(canvas);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        int fading = mFading == null ? -1 : mFading.getIntrinsicWidth();
        int base = mBase == null ? -1 : mBase.getIntrinsicHeight();
        return Math.max(fading, base);
    }

    @Override
    public int getIntrinsicHeight() {
        int fading = mFading == null ? -1 : mFading.getIntrinsicHeight();
        int base = mBase == null ? -1 : mBase.getIntrinsicHeight();
        return Math.max(fading, base);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (mBase != null) {
            mBase.setBounds(bounds);
        }

        if (mFading != null) {
            mFading.setBounds(bounds);
        }

        invalidateSelf();
    }

//    @Override
//    public void jumpToCurrentState() {
//        if (mFading != null) {
//            mFading.jumpToCurrentState();
//        }
//
//        if (mBase != null) {
//            mBase.jumpToCurrentState();
//        }
//    }

    @Override
    public void setChangingConfigurations(int configs) {
        if (mChangingConfigs != configs) {
            mChangingConfigs = configs;
            if (mFading != null) {
                mFading.setChangingConfigurations(configs);
            }

            if (mBase != null) {
                mBase.setChangingConfigurations(configs);
            }
        }
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        if (mFilterBitmap != filter) {
            mFilterBitmap = filter;
            if (mFading != null) {
                mFading.setFilterBitmap(filter);
            }

            if (mBase != null) {
                mBase.setFilterBitmap(filter);
            }
        }
    }

    @Override
    public void setDither(boolean dither) {
        if (mDither != dither) {
            mDither = dither;
            if (mFading != null) {
                mFading.setDither(dither);
            }

            if (mBase != null) {
                mBase.setDither(dither);
            }
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mColorFilter != cf) {
            mColorFilter = cf;
            if (mFading != null) {
                mFading.setColorFilter(cf);
            }

            if (mBase != null) {
                mBase.setColorFilter(cf);
            }
        }
    }

    @Override
    public void setColorFilter(int color, PorterDuff.Mode mode) {
        if (mColorFilterColor != color || mColorFilterMode != mode) {
            mColorFilterColor = color;
            mColorFilterMode = mode;
            if (mFading != null) {
                mFading.setColorFilter(color, mode);
            }

            if (mBase != null) {
                mBase.setColorFilter(color, mode);
            }
        }
    }

    @Override
    public void clearColorFilter() {
        if (mColorFilterMode != null) {
            mColorFilterMode = null;
            if (mFading != null) {
                mFading.clearColorFilter();
            }

            if (mBase != null) {
                mBase.clearColorFilter();
            }
        }
    }

    @Override
    public int getChangingConfigurations() {
        return mChangingConfigs;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean changed = false;
        if (mFading != null) {
            changed = mFading.setState(state);
        }

        if (mBase != null) {
            changed |= mBase.setState(state);
        }

        return changed;
    }

    @Override
    protected boolean onLevelChange(int level) {
        boolean changed = false;
        if (mFading != null) {
            changed = mFading.setLevel(level);
        }

        if (mBase != null) {
            changed |= mBase.setLevel(level);
        }

        return changed;
    }

    @Override
    public boolean isStateful() {
        return mFading != null && mFading.isStateful() || mBase != null && mBase.isStateful();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha != mAlpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
    }

    Drawable getBase() {
        return mBase;
    }

    Drawable getFading() {
        return mFading;
    }

    @Override
    public int getOpacity() {
        return resolveOpacity(mFading == null ? 0 : mFading.getOpacity(), mBase == null ? 0 : mBase.getOpacity());
    }

//    @Override
//    public void invalidateDrawable(Drawable who) {
//        if ((who == mFading || who == mBase) && getCallback() != null) {
//            getCallback().invalidateDrawable(this);
//        }
//    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if ((who == mFading || who == mBase)) {
            invalidateSelf();
        }
    }

//    @Override
//    public void scheduleDrawable(Drawable who, Runnable what, long when) {
//        if ((who == mFading || who == mBase) && getCallback() != null) {
//            getCallback().scheduleDrawable(this, what, when);
//        }
//    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if ((who == mFading || who == mBase)) {
            scheduleSelf(what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if ((who == mFading || who == mBase)) {
            unscheduleSelf(what);
        }
    }

//    @Override
//    public void unscheduleDrawable(Drawable who, Runnable what) {
//        if ((who == mFading || who == mBase) && getCallback() != null) {
//            getCallback().unscheduleDrawable(this, what);
//        }
//    }
}
