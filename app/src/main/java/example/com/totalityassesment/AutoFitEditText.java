package example.com.totalityassesment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.widget.EditText;

/**
 * Created by rbshubham on 17-01-2020.
 */
public class AutoFitEditText extends EditText {
    private static final int NO_LINE_LIMIT = -1;
    private final RectF _availableSpaceRect = new RectF();
    private final SparseIntArray _textSizesCached = new SparseIntArray();
    private final SizeTester _sizeTester;
    private float _maxTextSize;
    private float _spacingMult = 1.0f;
    private float _spacingAdd = 0.0f;
    private Float _minTextSize;
    private int _widthLimit;
    private int _maxLines;
    private boolean _enableSizeCache = true;
    private boolean _initiallized = false;
    private TextPaint paint;

    private interface SizeTester {

        int onTestSize(int suggestedSize, RectF availableSpace);
    }

    public AutoFitEditText(final Context context) {
        this(context, null, 0);
    }

    public AutoFitEditText(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitEditText(final Context context, final AttributeSet attrs,
                           final int defStyle) {
        super(context, attrs, defStyle);
        _minTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                30, getResources().getDisplayMetrics());
        _maxTextSize = getTextSize();
        if (_maxLines == 0)
            _maxLines = NO_LINE_LIMIT;
        _sizeTester = new SizeTester() {
            final RectF textRect = new RectF();

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public int onTestSize(final int suggestedSize,
                                  final RectF availableSPace) {
                paint.setTextSize(suggestedSize);
                final String text = getText().toString();
                final boolean singleline = getMaxLines() == 1;
                if (singleline) {
                    textRect.bottom = paint.getFontSpacing();
                    textRect.right = paint.measureText(text);
                } else {
                    final StaticLayout layout = new StaticLayout(text, paint,
                            _widthLimit, Layout.Alignment.ALIGN_NORMAL, _spacingMult,
                            _spacingAdd, true);
                    if (getMaxLines() != NO_LINE_LIMIT
                            && layout.getLineCount() > getMaxLines())
                        return 1;
                    textRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    for (int i = 0; i < layout.getLineCount(); i++)
                        if (maxWidth < layout.getLineWidth(i))
                            maxWidth = (int) layout.getLineWidth(i);
                    textRect.right = maxWidth;
                }
                textRect.offsetTo(0, 0);
                if (availableSPace.contains(textRect))
                    return -1;
                return 1;
            }
        };
        _initiallized = true;
    }

    @Override
    public void setTypeface(final Typeface tf) {
        if (paint == null)
            paint = new TextPaint(getPaint());
        paint.setTypeface(tf);
        super.setTypeface(tf);
    }

    @Override
    public void setTextSize(final float size) {
        _maxTextSize = size;
        _textSizesCached.clear();
        adjustTextSize();
    }

    @Override
    public void setMaxLines(final int maxlines) {
        super.setMaxLines(maxlines);
        _maxLines = maxlines;
        reAdjust();
    }

    @Override
    public int getMaxLines() {
        return _maxLines;
    }

    @Override
    public void setSingleLine() {
        super.setSingleLine();
        _maxLines = 1;
        reAdjust();
    }

    @Override
    public void setSingleLine(final boolean singleLine) {
        super.setSingleLine(singleLine);
        if (singleLine)
            _maxLines = 1;
        else
            _maxLines = NO_LINE_LIMIT;
        reAdjust();
    }

    @Override
    public void setLines(final int lines) {
        super.setLines(lines);
        _maxLines = lines;
        reAdjust();
    }

    @Override
    public void setTextSize(final int unit, final float size) {
        final Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        _maxTextSize = TypedValue.applyDimension(unit, size,
                r.getDisplayMetrics());
        _textSizesCached.clear();
        adjustTextSize();
    }

    @Override
    public void setLineSpacing(final float add, final float mult) {
        super.setLineSpacing(add, mult);
        _spacingMult = mult;
        _spacingAdd = add;
    }

    public void setMinTextSize(final Float minTextSize) {
        _minTextSize = minTextSize;
        reAdjust();
    }

    public Float get_minTextSize() {
        return _minTextSize;
    }

    private void reAdjust() {
        adjustTextSize();
    }

    private void adjustTextSize() {
        if (!_initiallized)
            return;
        final int startSize =  Math.round(_minTextSize);
        final int heightLimit = getMeasuredHeight()
                - getCompoundPaddingBottom() - getCompoundPaddingTop();
        _widthLimit = getMeasuredWidth() - getCompoundPaddingLeft()
                - getCompoundPaddingRight();
        if (_widthLimit <= 0)
            return;
        _availableSpaceRect.right = _widthLimit;
        _availableSpaceRect.bottom = heightLimit;
        super.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                efficientTextSizeSearch(startSize, (int) _maxTextSize,
                        _sizeTester, _availableSpaceRect));
    }

    public void setEnableSizeCache(final boolean enable) {
        _enableSizeCache = enable;
        _textSizesCached.clear();
        adjustTextSize();
    }

    private int efficientTextSizeSearch(final int start, final int end,
                                        final SizeTester sizeTester, final RectF availableSpace) {
        if (!_enableSizeCache)
            return binarySearch(start, end, sizeTester, availableSpace);
        final String text = getText().toString();
        final int key = text == null ? 0 : text.length();
        int size = _textSizesCached.get(key);
        if (size != 0)
            return size;
        size = binarySearch(start, end, sizeTester, availableSpace);
        _textSizesCached.put(key, size);
        return size;
    }

    private int binarySearch(final int start, final int end,
                             final SizeTester sizeTester, final RectF availableSpace) {
        int lastBest = start;
        int lo = start;
        int hi = end - 1;
        int mid = 0;
        while (lo <= hi) {
            mid = lo + hi >>> 1;
            final int midValCmp = sizeTester.onTestSize(mid, availableSpace);
            if (midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else if (midValCmp > 0) {
                hi = mid - 1;
                lastBest = hi;
            } else
                return mid;
        }

        return lastBest;
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start,
                                 final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        reAdjust();
    }

    @Override
    protected void onSizeChanged(final int width, final int height,
                                 final int oldwidth, final int oldheight) {
        _textSizesCached.clear();
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (width != oldwidth || height != oldheight)
            reAdjust();
    }


}
