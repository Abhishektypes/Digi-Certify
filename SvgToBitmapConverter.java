package com.example.certificateviewer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.util.Base64;
import com.caverock.androidsvg.SVG;

public class SvgToBitmapConverter {
    public static Bitmap convert(String svgContent) {
        try {
            // ✅ Load SVG from string
            SVG svg = SVG.getFromString(svgContent);
            Picture picture = svg.renderToPicture();

            // ✅ Convert to Bitmap
            Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawPicture(picture);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
