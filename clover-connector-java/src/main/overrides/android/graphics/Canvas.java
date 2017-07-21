//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.graphics;

import android.graphics.Bitmap;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.PorterDuff.Mode;
import android.graphics.Region.Op;

public class Canvas {
  public static final int ALL_SAVE_FLAG = 31;
  public static final int CLIP_SAVE_FLAG = 2;
  public static final int CLIP_TO_LAYER_SAVE_FLAG = 16;
  public static final int FULL_COLOR_LAYER_SAVE_FLAG = 8;
  public static final int HAS_ALPHA_LAYER_SAVE_FLAG = 4;
  public static final int MATRIX_SAVE_FLAG = 1;

  public Canvas() {
    throw new RuntimeException("Stub!");
  }

  public Canvas(Bitmap bitmap) {
    throw new RuntimeException("Stub!");
  }

  public boolean isHardwareAccelerated() {
    throw new RuntimeException("Stub!");
  }

  public void setBitmap(Bitmap bitmap) {
    throw new RuntimeException("Stub!");
  }

  public boolean isOpaque() {
    throw new RuntimeException("Stub!");
  }

  public int getWidth() {
    throw new RuntimeException("Stub!");
  }

  public int getHeight() {
    throw new RuntimeException("Stub!");
  }

  public int getDensity() {
    throw new RuntimeException("Stub!");
  }

  public void setDensity(int density) {
    throw new RuntimeException("Stub!");
  }

  public int getMaximumBitmapWidth() {
    throw new RuntimeException("Stub!");
  }

  public int getMaximumBitmapHeight() {
    throw new RuntimeException("Stub!");
  }

  public int save() {
    throw new RuntimeException("Stub!");
  }

  public int save(int saveFlags) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayer(RectF bounds, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayer(float left, float top, float right, float bottom, Paint paint, int saveFlags) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayer(float left, float top, float right, float bottom, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayerAlpha(RectF bounds, int alpha) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
    throw new RuntimeException("Stub!");
  }

  public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha) {
    throw new RuntimeException("Stub!");
  }

  public void restore() {
    throw new RuntimeException("Stub!");
  }

  public int getSaveCount() {
    throw new RuntimeException("Stub!");
  }

  public void restoreToCount(int saveCount) {
    throw new RuntimeException("Stub!");
  }

  public void translate(float dx, float dy) {
    throw new RuntimeException("Stub!");
  }

  public void scale(float sx, float sy) {
    throw new RuntimeException("Stub!");
  }

  public final void scale(float sx, float sy, float px, float py) {
    throw new RuntimeException("Stub!");
  }

  public void rotate(float degrees) {
    throw new RuntimeException("Stub!");
  }

  public final void rotate(float degrees, float px, float py) {
    throw new RuntimeException("Stub!");
  }

  public void skew(float sx, float sy) {
    throw new RuntimeException("Stub!");
  }

  public void concat(Matrix matrix) {
    throw new RuntimeException("Stub!");
  }

  public void setMatrix(Matrix matrix) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public void getMatrix(Matrix ctm) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public final Matrix getMatrix() {
    throw new RuntimeException("Stub!");
  }

  public boolean clipRect(RectF rect, Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipRect(Rect rect, Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipRect(RectF rect) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipRect(Rect rect) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipRect(float left, float top, float right, float bottom, Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipRect(float left, float top, float right, float bottom) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipRect(int left, int top, int right, int bottom) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipPath(Path path, Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean clipPath(Path path) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public boolean clipRegion(Region region, Op op) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public boolean clipRegion(Region region) {
    throw new RuntimeException("Stub!");
  }

  public DrawFilter getDrawFilter() {
    throw new RuntimeException("Stub!");
  }

  public void setDrawFilter(DrawFilter filter) {
    throw new RuntimeException("Stub!");
  }

  public boolean quickReject(RectF rect, Canvas.EdgeType type) {
    throw new RuntimeException("Stub!");
  }

  public boolean quickReject(Path path, Canvas.EdgeType type) {
    throw new RuntimeException("Stub!");
  }

  public boolean quickReject(float left, float top, float right, float bottom, Canvas.EdgeType type) {
    throw new RuntimeException("Stub!");
  }

  public boolean getClipBounds(Rect bounds) {
    throw new RuntimeException("Stub!");
  }

  public final Rect getClipBounds() {
    throw new RuntimeException("Stub!");
  }

  public void drawRGB(int r, int g, int b) {
    throw new RuntimeException("Stub!");
  }

  public void drawARGB(int a, int r, int g, int b) {
    throw new RuntimeException("Stub!");
  }

  public void drawColor(int color) {
    throw new RuntimeException("Stub!");
  }

  public void drawColor(int color, Mode mode) {
    throw new RuntimeException("Stub!");
  }

  public void drawPaint(Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawPoints(float[] pts, int offset, int count, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawPoints(float[] pts, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawPoint(float x, float y, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawLines(float[] pts, int offset, int count, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawLines(float[] pts, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawRect(RectF rect, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawRect(Rect r, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawRect(float left, float top, float right, float bottom, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawOval(RectF oval, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawOval(float left, float top, float right, float bottom, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawCircle(float cx, float cy, float radius, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawPath(Path path, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public void drawBitmap(int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public void drawBitmap(int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawBitmapMesh(Bitmap bitmap, int meshWidth, int meshHeight, float[] verts, int vertOffset, int[] colors, int colorOffset, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawVertices(Canvas.VertexMode mode, int vertexCount, float[] verts, int vertOffset, float[] texs, int texOffset, int[] colors, int colorOffset, short[] indices, int indexOffset, int indexCount, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawText(char[] text, int index, int count, float x, float y, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawText(String text, float x, float y, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawText(String text, int start, int end, float x, float y, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public void drawPosText(char[] text, int index, int count, float[] pos, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  /** @deprecated */
  @Deprecated
  public void drawPosText(String text, float[] pos, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawTextOnPath(char[] text, int index, int count, Path path, float hOffset, float vOffset, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawTextOnPath(String text, Path path, float hOffset, float vOffset, Paint paint) {
    throw new RuntimeException("Stub!");
  }

  public void drawPicture(Picture picture) {
    throw new RuntimeException("Stub!");
  }

  public void drawPicture(Picture picture, RectF dst) {
    throw new RuntimeException("Stub!");
  }

  public void drawPicture(Picture picture, Rect dst) {
    throw new RuntimeException("Stub!");
  }

  public static enum VertexMode {
    TRIANGLES,
    TRIANGLE_FAN,
    TRIANGLE_STRIP;

    private VertexMode() {
    }
  }

  public static enum EdgeType {
    AA,
    BW;

    private EdgeType() {
    }
  }
}
