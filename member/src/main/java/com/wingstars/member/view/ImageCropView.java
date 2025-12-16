package com.wingstars.member.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ImageCropView extends View {
    private Bitmap originalBitmap;
    private Matrix imageMatrix = new Matrix();
    private Matrix inverseMatrix = new Matrix();
    private Paint cropFramePaint;
    private Paint overlayPaint;
    private Paint guideLinePaint;
    private Path cropPath = new Path();
    private RectF cropRect = new RectF();
    private RectF imageRect = new RectF();

    private float aspectRatioWidth = 1;
    private float aspectRatioHeight = 1;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int RESIZE = 3;
    private int mode = NONE;

    private float[] lastTouch = new float[2];
    private float[] startTouch = new float[2];
    private float[] lastPointer = new float[4];

    private static final int HANDLE_SIZE = 40; // dp
    private float handleSize;

    private int cornerHandle = 0;
    private int edgeHandle = 0;

    private static final int TOP_LEFT = 1;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_RIGHT = 3;
    private static final int BOTTOM_LEFT = 4;

    private static final int TOP = 5;
    private static final int RIGHT = 6;
    private static final int BOTTOM = 7;
    private static final int LEFT = 8;

    private boolean showGuidelines = false;
    private int minCropWidth = 0; // dp
    private int minCropHeight = 0; // dp
    private float density;

    public ImageCropView(Context context) {
        super(context);
        init(context);
    }

    public ImageCropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        density = getResources().getDisplayMetrics().density;
        handleSize = HANDLE_SIZE * density;
        minCropWidth = (int) (minCropWidth * density);
        minCropHeight = (int) (minCropHeight * density);

        cropFramePaint = new Paint();
        cropFramePaint.setColor(Color.WHITE);
        cropFramePaint.setStrokeWidth(2 * density);
        cropFramePaint.setStyle(Paint.Style.STROKE);
        cropFramePaint.setAntiAlias(true);

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.parseColor("#000000"));
        overlayPaint.setStyle(Paint.Style.FILL);
        overlayPaint.setAntiAlias(true);

        guideLinePaint = new Paint();
        guideLinePaint.setColor(Color.parseColor("#000000"));
        guideLinePaint.setStrokeWidth(1 * density);
        guideLinePaint.setStyle(Paint.Style.STROKE);
        guideLinePaint.setAlpha(180);
        guideLinePaint.setAntiAlias(true);
    }

    public void setImageBitmap(Bitmap bitmap) {
        originalBitmap = bitmap;
        resetCropRect();
        invalidate();
    }

    public void setAspectRatio(float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        aspectRatioWidth = width;
        aspectRatioHeight = height;
        resetCropRect();
        invalidate();
    }

    public void setShowGuidelines(boolean show) {
        showGuidelines = show;
        invalidate();
    }

    private void resetCropRect() {
        if (originalBitmap == null || getWidth() == 0) {
            return;
        }

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float imageWidth = originalBitmap.getWidth();
        float imageHeight = originalBitmap.getHeight();

        float scaleX = viewWidth / imageWidth;
        float scaleY = viewHeight / imageHeight;
        float scale = Math.min(scaleX, scaleY) * 0.9f;

        float centerX = (viewWidth - imageWidth * scale) / 2;
        float centerY = (viewHeight - imageHeight * scale) / 2;

        imageMatrix.reset();
        imageMatrix.postScale(scale, scale);
        imageMatrix.postTranslate(centerX, centerY);
        imageMatrix.invert(inverseMatrix);

        updateImageRect();

        // 初始化裁剪框
        float cropWidth, cropHeight;
        if (aspectRatioWidth > 0 && aspectRatioHeight > 0) {
            float aspectRatio = aspectRatioWidth / aspectRatioHeight;
            if (imageRect.width() / imageRect.height() > aspectRatio) {
                cropHeight = imageRect.height() * 1f;
                cropWidth = cropHeight * aspectRatio;
            } else {
                cropWidth = imageRect.width() * 1f;
                cropHeight = cropWidth / aspectRatio;
            }
        } else {
            cropWidth = imageRect.width() * 1f;
            cropHeight = imageRect.height() * 1f;
        }

        float cropLeft = imageRect.centerX() - cropWidth / 2;
        float cropTop = imageRect.centerY() - cropHeight / 2;
        Log.e("resetCropRect","left ="+cropLeft+",top="+cropTop+",right="+cropLeft + cropWidth+",bottom="+cropTop + cropHeight);
        cropRect.set(cropLeft, cropTop, cropLeft + cropWidth, cropTop + cropHeight);
    }

    private void updateImageRect() {
        if (originalBitmap == null) {
            return;
        }
        imageRect.set(0, 0, originalBitmap.getWidth(), originalBitmap.getHeight());
        imageMatrix.mapRect(imageRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (originalBitmap == null) {
            return;
        }

        // 绘制图片
        canvas.drawBitmap(originalBitmap, imageMatrix, null);

        // 创建裁剪区域
       // cropPath.reset();
       // cropPath.addRect(cropRect, Path.Direction.CW);

        // 保存当前画布状态
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.ALL_SAVE_FLAG);

        // 绘制覆盖层
       // canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        // 清除裁剪区域的覆盖层
        overlayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(cropPath, overlayPaint);
        overlayPaint.setXfermode(null);

        // 绘制裁剪框
       // canvas.drawRect(cropRect, cropFramePaint);

        // 绘制九宫格参考线
        if (showGuidelines) {
            float thirdWidth = cropRect.width() / 3;
            float thirdHeight = cropRect.height() / 3;

            // 垂直线
            canvas.drawLine(cropRect.left + thirdWidth, cropRect.top,
                    cropRect.left + thirdWidth, cropRect.bottom, guideLinePaint);
            canvas.drawLine(cropRect.left + thirdWidth * 2, cropRect.top,
                    cropRect.left + thirdWidth * 2, cropRect.bottom, guideLinePaint);

            // 水平线
            canvas.drawLine(cropRect.left, cropRect.top + thirdHeight,
                    cropRect.right, cropRect.top + thirdHeight, guideLinePaint);
            canvas.drawLine(cropRect.left, cropRect.top + thirdHeight * 2,
                    cropRect.right, cropRect.top + thirdHeight * 2, guideLinePaint);
        }

        // 绘制裁剪框角落和边缘的操作点
        //drawHandles(canvas);

        // 恢复画布状态
        canvas.restoreToCount(saveCount);
    }

    private void drawHandles(Canvas canvas) {
        // 绘制四个角落的操作点
        canvas.drawCircle(cropRect.left, cropRect.top, handleSize / 2, cropFramePaint);
        canvas.drawCircle(cropRect.right, cropRect.top, handleSize / 2, cropFramePaint);
        canvas.drawCircle(cropRect.right, cropRect.bottom, handleSize / 2, cropFramePaint);
        canvas.drawCircle(cropRect.left, cropRect.bottom, handleSize / 2, cropFramePaint);

        // 绘制四条边的中点操作点
        canvas.drawCircle(cropRect.left + cropRect.width() / 2, cropRect.top, handleSize / 3, cropFramePaint);
        canvas.drawCircle(cropRect.right, cropRect.top + cropRect.height() / 2, handleSize / 3, cropFramePaint);
        canvas.drawCircle(cropRect.left + cropRect.width() / 2, cropRect.bottom, handleSize / 3, cropFramePaint);
        canvas.drawCircle(cropRect.left, cropRect.top + cropRect.height() / 2, handleSize / 3, cropFramePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        if (originalBitmap == null) {
            return super.onTouchEvent(event);
        }

        float[] touchPoint = new float[]{event.getX(), event.getY()};

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = checkHandlePressed(touchPoint);
                if (mode == NONE) {
                    mode = DRAG;
                }
                lastTouch[0] = event.getX();
                lastTouch[1] = event.getY();
                startTouch[0] = event.getX();
                startTouch[1] = event.getY();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    lastPointer[0] = event.getX(0);
                    lastPointer[1] = event.getY(0);
                    lastPointer[2] = event.getX(1);
                    lastPointer[3] = event.getY(1);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    float dx = event.getX() - lastTouch[0];
                    float dy = event.getY() - lastTouch[1];

                    imageMatrix.postTranslate(dx, dy);
                    imageMatrix.invert(inverseMatrix);
                    updateImageRect();

                    lastTouch[0] = event.getX();
                    lastTouch[1] = event.getY();
                } else if (mode == ZOOM && event.getPointerCount() == 2) {
                    float[] currentPointer = new float[4];
                    currentPointer[0] = event.getX(0);
                    currentPointer[1] = event.getY(0);
                    currentPointer[2] = event.getX(1);
                    currentPointer[3] = event.getY(1);

                    float oldDist = (float) Math.hypot(lastPointer[2] - lastPointer[0],
                            lastPointer[3] - lastPointer[1]);
                    float newDist = (float) Math.hypot(currentPointer[2] - currentPointer[0],
                            currentPointer[3] - currentPointer[1]);

                    if (oldDist > 10f) {
                        float scale = newDist / oldDist;

                        float[] center = new float[2];
                        center[0] = (currentPointer[0] + currentPointer[2]) / 2;
                        center[1] = (currentPointer[1] + currentPointer[3]) / 2;

                        imageMatrix.postScale(scale, scale, center[0], center[1]);
                        imageMatrix.invert(inverseMatrix);
                        updateImageRect();
                    }

                    System.arraycopy(currentPointer, 0, lastPointer, 0, 4);
                } else if (mode >= TOP_LEFT && mode <= LEFT) {
                    resizeCropRect(touchPoint);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }

        return true;
    }

    private int checkHandlePressed(float[] point) {
        if (isInCornerHandle(point, TOP_LEFT)) return TOP_LEFT;
        if (isInCornerHandle(point, TOP_RIGHT)) return TOP_RIGHT;
        if (isInCornerHandle(point, BOTTOM_RIGHT)) return BOTTOM_RIGHT;
        if (isInCornerHandle(point, BOTTOM_LEFT)) return BOTTOM_LEFT;

        if (isInEdgeHandle(point, TOP)) return TOP;
        if (isInEdgeHandle(point, RIGHT)) return RIGHT;
        if (isInEdgeHandle(point, BOTTOM)) return BOTTOM;
        if (isInEdgeHandle(point, LEFT)) return LEFT;

        if (isInCropRect(point)) return DRAG;

        return NONE;
    }

    private boolean isInCornerHandle(float[] point, int handle) {
        float left, top, right, bottom;

        switch (handle) {
            case TOP_LEFT:
                left = cropRect.left - handleSize;
                top = cropRect.top - handleSize;
                right = cropRect.left + handleSize;
                bottom = cropRect.top + handleSize;
                break;
            case TOP_RIGHT:
                left = cropRect.right - handleSize;
                top = cropRect.top - handleSize;
                right = cropRect.right + handleSize;
                bottom = cropRect.top + handleSize;
                break;
            case BOTTOM_RIGHT:
                left = cropRect.right - handleSize;
                top = cropRect.bottom - handleSize;
                right = cropRect.right + handleSize;
                bottom = cropRect.bottom + handleSize;
                break;
            case BOTTOM_LEFT:
                left = cropRect.left - handleSize;
                top = cropRect.bottom - handleSize;
                right = cropRect.left + handleSize;
                bottom = cropRect.bottom + handleSize;
                break;
            default:
                return false;
        }

        return point[0] >= left && point[0] <= right &&
                point[1] >= top && point[1] <= bottom;
    }

    private boolean isInEdgeHandle(float[] point, int handle) {
        float left, top, right, bottom;
        float edgeTolerance = handleSize / 2;

        switch (handle) {
            case TOP:
                left = cropRect.left + edgeTolerance;
                top = cropRect.top - edgeTolerance;
                right = cropRect.right - edgeTolerance;
                bottom = cropRect.top + edgeTolerance;
                break;
            case RIGHT:
                left = cropRect.right - edgeTolerance;
                top = cropRect.top + edgeTolerance;
                right = cropRect.right + edgeTolerance;
                bottom = cropRect.bottom - edgeTolerance;
                break;
            case BOTTOM:
                left = cropRect.left + edgeTolerance;
                top = cropRect.bottom - edgeTolerance;
                right = cropRect.right - edgeTolerance;
                bottom = cropRect.bottom + edgeTolerance;
                break;
            case LEFT:
                left = cropRect.left - edgeTolerance;
                top = cropRect.top + edgeTolerance;
                right = cropRect.left + edgeTolerance;
                bottom = cropRect.bottom - edgeTolerance;
                break;
            default:
                return false;
        }

        return point[0] >= left && point[0] <= right &&
                point[1] >= top && point[1] <= bottom;
    }

    private boolean isInCropRect(float[] point) {
        return cropRect.contains(point[0], point[1]);
    }

    private void resizeCropRect(float[] point) {
        float originalLeft = cropRect.left;
        float originalTop = cropRect.top;
        float originalRight = cropRect.right;
        float originalBottom = cropRect.bottom;

        float newLeft = originalLeft;
        float newTop = originalTop;
        float newRight = originalRight;
        float newBottom = originalBottom;

        switch (mode) {
            case TOP_LEFT:
                newLeft = point[0];
                newTop = point[1];
                break;
            case TOP_RIGHT:
                newRight = point[0];
                newTop = point[1];
                break;
            case BOTTOM_RIGHT:
                newRight = point[0];
                newBottom = point[1];
                break;
            case BOTTOM_LEFT:
                newLeft = point[0];
                newBottom = point[1];
                break;
            case TOP:
                newTop = point[1];
                break;
            case RIGHT:
                newRight = point[0];
                break;
            case BOTTOM:
                newBottom = point[1];
                break;
            case LEFT:
                newLeft = point[0];
                break;
        }

        // 应用最小尺寸限制
        if (newRight - newLeft < minCropWidth) {
            if (mode == LEFT || mode == TOP_LEFT || mode == BOTTOM_LEFT) {
                newLeft = originalRight - minCropWidth;
            } else {
                newRight = originalLeft + minCropWidth;
            }
        }

        if (newBottom - newTop < minCropHeight) {
            if (mode == TOP || mode == TOP_LEFT || mode == TOP_RIGHT) {
                newTop = originalBottom - minCropHeight;
            } else {
                newBottom = originalTop + minCropHeight;
            }
        }

        // 应用宽高比限制
        if (aspectRatioWidth > 0 && aspectRatioHeight > 0) {
            float targetRatio = aspectRatioWidth / aspectRatioHeight;
            float currentWidth = newRight - newLeft;
            float currentHeight = newBottom - newTop;
            float currentRatio = currentWidth / currentHeight;

            if (Math.abs(currentRatio - targetRatio) > 0.01) {
                // 需要调整以匹配宽高比
                if ((mode == TOP_LEFT || mode == BOTTOM_LEFT || mode == LEFT) ||
                        (mode == TOP || mode == BOTTOM) && currentRatio < targetRatio) {
                    // 调整高度
                    float newHeight = currentWidth / targetRatio;
                    if (mode == TOP_LEFT || mode == TOP || mode == TOP_RIGHT) {
                        newTop = newBottom - newHeight;
                    } else {
                        newBottom = newTop + newHeight;
                    }
                } else {
                    // 调整宽度
                    float newWidth = currentHeight * targetRatio;
                    if (mode == TOP_LEFT || mode == LEFT || mode == BOTTOM_LEFT) {
                        newLeft = newRight - newWidth;
                    } else {
                        newRight = newLeft + newWidth;
                    }
                }
            }
        }

        // 更新裁剪框
        cropRect.set(newLeft, newTop, newRight, newBottom);
    }

    public Bitmap getCroppedBitmap() {
        if (originalBitmap == null) {
            return null;
        }

        float[] srcPoints = new float[]{
                cropRect.left, cropRect.top,
                cropRect.right, cropRect.top,
                cropRect.right, cropRect.bottom,
                cropRect.left, cropRect.bottom
        };

        float[] dstPoints = new float[8];
        inverseMatrix.mapPoints(dstPoints, srcPoints);

        float left = Math.min(Math.min(dstPoints[0], dstPoints[2]),
                Math.min(dstPoints[4], dstPoints[6]));
        float top = Math.min(Math.min(dstPoints[1], dstPoints[3]),
                Math.min(dstPoints[5], dstPoints[7]));
        float right = Math.max(Math.max(dstPoints[0], dstPoints[2]),
                Math.max(dstPoints[4], dstPoints[6]));
        float bottom = Math.max(Math.max(dstPoints[1], dstPoints[3]),
                Math.max(dstPoints[5], dstPoints[7]));

        // 确保在图像边界内
        left = Math.max(0, left);
        top = Math.max(0, top);
        right = Math.min(originalBitmap.getWidth(), right);
        bottom = Math.min(originalBitmap.getHeight(), bottom);

        // 如果宽高太小，返回null
        if (right - left < 1 || bottom - top < 1) {
            return null;
        }

        // 计算目标尺寸
        int targetWidth = (int) (right - left);
        int targetHeight = (int) (bottom - top);

        // 如果指定了宽高比，调整目标尺寸
        if (aspectRatioWidth > 0 && aspectRatioHeight > 0) {
            float targetRatio = aspectRatioWidth / aspectRatioHeight;
            if (targetWidth / (float) targetHeight > targetRatio) {
                targetHeight = (int) (targetWidth / targetRatio);
            } else {
                targetWidth = (int) (targetHeight * targetRatio);
            }
        }

        // 创建裁剪后的Bitmap
        Bitmap croppedBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                originalBitmap.getConfig());
        Canvas canvas = new Canvas(croppedBitmap);

        // 创建变换矩阵
        Matrix transform = new Matrix();
        transform.postTranslate(-left, -top);

        // 如果指定了宽高比，需要进行缩放
        if (aspectRatioWidth > 0 && aspectRatioHeight > 0) {
            float scaleX = (float) targetWidth / (right - left);
            float scaleY = (float) targetHeight / (bottom - top);
            transform.postScale(scaleX, scaleY);
        }

        // 绘制裁剪区域
        canvas.drawBitmap(originalBitmap, transform, null);

        return croppedBitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (originalBitmap != null) {
            resetCropRect();
        }
    }
}
