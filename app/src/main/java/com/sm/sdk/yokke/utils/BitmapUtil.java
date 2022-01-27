package com.sm.sdk.yokke.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class BitmapUtil {
    /**
     * Replace a certain color value in the bitmap with a new color
     */
    public static Bitmap replaceBitmapColor(Bitmap oldBitmap, int oldColor, int newColor) {
        // Related instructions can refer to http://xys289187120.blog.51cto.com/3361352/657590/
        Bitmap bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        // Get all the pixels of the bitmap in a loop
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Get the color value of each point in the Bitmap image
                // Will need to fill the color value if not
                // Explain here if the color is completely transparent or completely black, the return value is 0
                // getPixel() does not have a transparent channel, getPixel32() only has a transparent part, so full transparency is 0x00000000
                // Opaque black is 0xFF000000. If the transparent part is not calculated, it will be 0.
                int color = bitmap.getPixel(j, i);
                // Store the color value in an array for easy modification later
                if (color == oldColor) {
                    bitmap.setPixel(j, i, newColor); // Replace white with transparent color
                }

            }
        }
        return bitmap;
    }

    /**
     * Overlay the two bitmaps into one bitmap, based on the length and width of the underlying bitmap
     *
     * @param backBitmap  Bitmap at the bottom
     * @param frontBitmap Bitmap overlaid
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {
        boolean bool = backBitmap == null || backBitmap.isRecycled() || frontBitmap == null || frontBitmap.isRecycled();
        if (bool) {
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        int width = backBitmap.getWidth();
        int height = backBitmap.getHeight();
        Rect baseRect = new Rect(0, 0, width, height);

        width = frontBitmap.getWidth();
        height = frontBitmap.getHeight();
        Rect frontRect = new Rect(0, 0, width, height);
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }

    public static Bitmap scale(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return bitmap;
    }


    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
        return gray;
    }

    /**
     * Bitmap image binarization tool
     */
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth();             // Get the width of the bitmap
        int height = bmp.getHeight();           // Get the height of the bitmap
        int[] pixels = new int[width * height]; // Create an array of pixels by the size of the bitmap

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = (grey & 0x00FF0000) >> 16;
                int green = (grey & 0x0000FF00) >> 8;
                int blue = grey & 0x000000FF;
                pixels[width * i + j] = RGB2Gray(red, green, blue) > 128 ? 0xffffffff : 0xff000000;
            }
        }

        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }

    /**
     * Get YUV data of bitmap
     */
    private static byte[] getYUVByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = rgb2YCbCr420(pixels, width, height);
        bitmap.recycle();
        bitmap = null;
        return data;
    }

    /**
     * Crop
     *
     * @param bitmap    Original image
     * @param maxWidth  Maximum width
     * @param maxHeight maximum height
     * @return Cropped image
     */
    private static Bitmap cropBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int x = 0;
        int y = 0;
        if (w > maxWidth) {
            x = (w - maxWidth) / 2;
            w = maxWidth;
        }
        if (h > maxHeight) {
            y = (h - maxHeight) / 2;
            h = maxHeight;
        }
        return Bitmap.createBitmap(bitmap, x, y, w, h, null, false);
    }

    private static byte[] rgb2YCbCr420(int[] pixels, int width, int height) {
        int len = width * height;
        // yuv format array size, y brightness occupies len length, u and v each occupy len/4 length.
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Mask the transparency value of ARGB
                int rgb = pixels[i * width + j] & 0x00FFFFFF;
                // The color order of the pixels is bgr, shift operation.
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;
                // 套用公式
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // rgb2yuv
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.147 * r - 0.289 * g + 0.437 * b);
                // v = (int) (0.615 * r - 0.515 * g - 0.1 * b);
                // RGB conversion YCbCr
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.1687 * r - 0.3313 * g + 0.5 * b + 128);
                // if (u > 255)
                // u = 255;
                // v = (int) (0.5 * r - 0.4187 * g - 0.0813 * b + 128);
                // if (v > 255)
                // v = 255;
                // Adjustment
                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);
                // Assignment
                yuv[i * width + j] = (byte) y;
                yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                yuv[len + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }
}
