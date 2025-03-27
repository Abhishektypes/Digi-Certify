package com.example.certificateviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PDFGenerator {
    public interface PDFGenerationCallback {
        void onPDFGenerated(String filePath);
        void onPDFGenerationFailed(Exception e);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void generateCertificate(Context context, String templatePath, String certId,
                                           String name, String course, String issueDate,
                                           PDFGenerationCallback callback) {
        try {
            File svgFile = new File(templatePath);
            if (!svgFile.exists()) {
                throw new Exception("Template SVG file not found at " + templatePath);
            }

            // ✅ Read SVG file as String
            String svgContent = new String(Files.readAllBytes(svgFile.toPath()), StandardCharsets.UTF_8);

            // ✅ Replace placeholders with actual values
            svgContent = svgContent.replace("{{NAME}}", name)
                    .replace("{{COURSE}}", course)
                    .replace("{{DATE}}", issueDate)
                    .replace("{{CERTIFICATE_ID}}", certId);

            // ✅ Convert SVG to Bitmap
            Bitmap svgBitmap = SvgToBitmapConverter.convert(svgContent);
            if (svgBitmap == null) {
                throw new Exception("Failed to convert SVG to Bitmap");
            }

            // ✅ Generate QR Code for certificate ID
            Bitmap qrBitmap = generateQRCode(certId);
            if (qrBitmap == null) {
                throw new Exception("Failed to generate QR Code");
            }

            // ✅ Convert Bitmap to PDF
            File pdfFile = new File("/storage/emulated/0/Download/Certificates/" + certId + ".pdf");
            bitmapToPdf(svgBitmap, qrBitmap, certId, pdfFile);

            Log.d("PDFDebug", "✅ PDF generated at: " + pdfFile.getAbsolutePath());
            callback.onPDFGenerated(pdfFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("PDFDebug", "❌ Failed to generate certificate: " + e.getMessage());
            callback.onPDFGenerationFailed(e);
        }
    }

    // ✅ Convert Bitmaps to PDF
    private static void bitmapToPdf(Bitmap certificateBitmap, Bitmap qrBitmap, String certId, File pdfFile) throws Exception {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                certificateBitmap.getWidth(), certificateBitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // ✅ Draw certificate image
        canvas.drawBitmap(certificateBitmap, 0, 0, null);

        // ✅ Adjust QR Code position (Move left & down)
        int qrSize = certificateBitmap.getWidth() / 6;
        int qrX = certificateBitmap.getWidth() - qrSize - 70; // 🔥 Moved 20 pixels left
        int qrY = certificateBitmap.getHeight() - qrSize - 70; // 🔥 Moved 30 pixels lower

        canvas.drawBitmap(qrBitmap, null, new android.graphics.Rect(qrX, qrY, qrX + qrSize, qrY + qrSize), null);

        pdfDocument.finishPage(page);

        // ✅ Save PDF file
        FileOutputStream fos = new FileOutputStream(pdfFile);
        pdfDocument.writeTo(fos);
        fos.close();
        pdfDocument.close();
    }

    // ✅ Generate QR Code
    private static Bitmap generateQRCode(String data) throws WriterException {
        BarcodeEncoder encoder = new BarcodeEncoder();
        return encoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 250, 250);
    }
}
