//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.graphics;
import java.util.Locale;

public class Paint {
  public static final int ANTI_ALIAS_FLAG = 1;
  public static final int DEV_KERN_TEXT_FLAG = 256;
  public static final int DITHER_FLAG = 4;
  public static final int EMBEDDED_BITMAP_TEXT_FLAG = 1024;
  public static final int FAKE_BOLD_TEXT_FLAG = 32;
  public static final int FILTER_BITMAP_FLAG = 2;
  public static final int HINTING_OFF = 0;
  public static final int HINTING_ON = 1;
  public static final int LINEAR_TEXT_FLAG = 64;
  public static final int STRIKE_THRU_TEXT_FLAG = 16;
  public static final int SUBPIXEL_TEXT_FLAG = 128;
  public static final int UNDERLINE_TEXT_FLAG = 8;

  public Paint() {
    throw new RuntimeException("Stub!");
  }

  public Paint(int flags) {
    throw new RuntimeException("Stub!");
  }

  public Paint(Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void reset() {
    throw new RuntimeException("Stub!");
  }

  public void set(Paint src) {
    throw new RuntimeException("Stub!");
  }

  public native int getFlags();

  public native void setFlags(int var1);

  public native int getHinting();

  public native void setHinting(int var1);

  public final boolean isAntiAlias() {
    throw new RuntimeException("Stub!");
  }

  public native void setAntiAlias(boolean var1);

  public final boolean isDither() {
    throw new RuntimeException("Stub!");
  }

  public native void setDither(boolean var1);

  public final boolean isLinearText() {
    throw new RuntimeException("Stub!");
  }

  public native void setLinearText(boolean var1);

  public final boolean isSubpixelText() {
    throw new RuntimeException("Stub!");
  }

  public native void setSubpixelText(boolean var1);

  public final boolean isUnderlineText() {
    throw new RuntimeException("Stub!");
  }

  public native void setUnderlineText(boolean var1);

  public final boolean isStrikeThruText() {
    throw new RuntimeException("Stub!");
  }

  public native void setStrikeThruText(boolean var1);

  public final boolean isFakeBoldText() {
    throw new RuntimeException("Stub!");
  }

  public native void setFakeBoldText(boolean var1);

  public final boolean isFilterBitmap() {
    throw new RuntimeException("Stub!");
  }

  public native void setFilterBitmap(boolean var1);

  public Paint.Style getStyle() {
    throw new RuntimeException("Stub!");
  }

  public void setStyle(Paint.Style style) {
    throw new RuntimeException("Stub!");
  }

  public native int getColor();

  public native void setColor(int var1);

  public native int getAlpha();

  public native void setAlpha(int var1);

  public void setARGB(int a, int r, int g, int b) {
    throw new RuntimeException("Stub!");
  }

  public native float getStrokeWidth();

  public native void setStrokeWidth(float var1);

  public native float getStrokeMiter();

  public native void setStrokeMiter(float var1);

  public Paint.Cap getStrokeCap() {
    throw new RuntimeException("Stub!");
  }

  public void setStrokeCap(Paint.Cap cap) {
    throw new RuntimeException("Stub!");
  }

  public Paint.Join getStrokeJoin() {
    throw new RuntimeException("Stub!");
  }

  public void setStrokeJoin(Paint.Join join) {
    throw new RuntimeException("Stub!");
  }

  public boolean getFillPath(Path src, Path dst) {
    throw new RuntimeException("Stub!");
  }

  public Shader getShader() {
    throw new RuntimeException("Stub!");
  }

  public Shader setShader(Shader shader) {
    throw new RuntimeException("Stub!");
  }

  public ColorFilter getColorFilter() {
    throw new RuntimeException("Stub!");
  }

  public ColorFilter setColorFilter(ColorFilter filter) {
    throw new RuntimeException("Stub!");
  }

  public Xfermode getXfermode() {
    throw new RuntimeException("Stub!");
  }

  public Xfermode setXfermode(Xfermode xfermode) {
    throw new RuntimeException("Stub!");
  }

  public PathEffect getPathEffect() {
    throw new RuntimeException("Stub!");
  }

  public PathEffect setPathEffect(PathEffect effect) {
    throw new RuntimeException("Stub!");
  }

  public MaskFilter getMaskFilter() {
    throw new RuntimeException("Stub!");
  }

  public MaskFilter setMaskFilter(MaskFilter maskfilter) {
    throw new RuntimeException("Stub!");
  }

  public Typeface getTypeface() {
    throw new RuntimeException("Stub!");
  }

  public Typeface setTypeface(Typeface typeface) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public Rasterizer getRasterizer() {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public Rasterizer setRasterizer(Rasterizer rasterizer) {
    throw new RuntimeException("Stub!");
  }

  public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
    throw new RuntimeException("Stub!");
  }

  public void clearShadowLayer() {
    throw new RuntimeException("Stub!");
  }

  public Paint.Align getTextAlign() {
    throw new RuntimeException("Stub!");
  }

  public void setTextAlign(Paint.Align align) {
    throw new RuntimeException("Stub!");
  }

  public Locale getTextLocale() {
    throw new RuntimeException("Stub!");
  }

  public void setTextLocale(Locale locale) {
    throw new RuntimeException("Stub!");
  }

  public native boolean isElegantTextHeight();

  public native void setElegantTextHeight(boolean var1);

  public native float getTextSize();

  public native void setTextSize(float var1);

  public native float getTextScaleX();

  public native void setTextScaleX(float var1);

  public native float getTextSkewX();

  public native void setTextSkewX(float var1);

  public float getLetterSpacing() {
    throw new RuntimeException("Stub!");
  }

  public void setLetterSpacing(float letterSpacing) {
    throw new RuntimeException("Stub!");
  }

  public String getFontFeatureSettings() {
    throw new RuntimeException("Stub!");
  }

  public void setFontFeatureSettings(String settings) {
    throw new RuntimeException("Stub!");
  }

  public native float ascent();

  public native float descent();

  public native float getFontMetrics(Paint.FontMetrics var1);

  public Paint.FontMetrics getFontMetrics() {
    throw new RuntimeException("Stub!");
  }

  public native int getFontMetricsInt(Paint.FontMetricsInt var1);

  public Paint.FontMetricsInt getFontMetricsInt() {
    throw new RuntimeException("Stub!");
  }

  public float getFontSpacing() {
    throw new RuntimeException("Stub!");
  }

  public float measureText(char[] text, int index, int count) {
    throw new RuntimeException("Stub!");
  }

  public float measureText(String text, int start, int end) {
    throw new RuntimeException("Stub!");
  }

  public float measureText(String text) {
    throw new RuntimeException("Stub!");
  }

  public float measureText(CharSequence text, int start, int end) {
    throw new RuntimeException("Stub!");
  }

  public int breakText(char[] text, int index, int count, float maxWidth, float[] measuredWidth) {
    throw new RuntimeException("Stub!");
  }

  public int breakText(CharSequence text, int start, int end, boolean measureForwards, float maxWidth, float[] measuredWidth) {
    throw new RuntimeException("Stub!");
  }

  public int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth) {
    throw new RuntimeException("Stub!");
  }

  public int getTextWidths(char[] text, int index, int count, float[] widths) {
    throw new RuntimeException("Stub!");
  }

  public int getTextWidths(CharSequence text, int start, int end, float[] widths) {
    throw new RuntimeException("Stub!");
  }

  public int getTextWidths(String text, int start, int end, float[] widths) {
    throw new RuntimeException("Stub!");
  }

  public int getTextWidths(String text, float[] widths) {
    throw new RuntimeException("Stub!");
  }

  public void getTextPath(char[] text, int index, int count, float x, float y, Path path) {
    throw new RuntimeException("Stub!");
  }

  public void getTextPath(String text, int start, int end, float x, float y, Path path) {
    throw new RuntimeException("Stub!");
  }

  public void getTextBounds(String text, int start, int end, Rect bounds) {
    throw new RuntimeException("Stub!");
  }

  public void getTextBounds(char[] text, int index, int count, Rect bounds) {
    throw new RuntimeException("Stub!");
  }

  protected void finalize() throws Throwable {
    throw new RuntimeException("Stub!");
  }

  public static class FontMetricsInt {
    public int ascent;
    public int bottom;
    public int descent;
    public int leading;
    public int top;

    public FontMetricsInt() {
      throw new RuntimeException("Stub!");
    }

    public String toString() {
      throw new RuntimeException("Stub!");
    }
  }

  public static class FontMetrics {
    public float ascent;
    public float bottom;
    public float descent;
    public float leading;
    public float top;

    public FontMetrics() {
      throw new RuntimeException("Stub!");
    }
  }

  public static enum Align {
    CENTER,
    LEFT,
    RIGHT;

    private Align() {
    }
  }

  public static enum Join {
    BEVEL,
    MITER,
    ROUND;

    private Join() {
    }
  }

  public static enum Cap {
    BUTT,
    ROUND,
    SQUARE;

    private Cap() {
    }
  }

  public static enum Style {
    FILL,
    FILL_AND_STROKE,
    STROKE;

    private Style() {
    }
  }
}
