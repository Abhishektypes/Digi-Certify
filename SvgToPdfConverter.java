//package com.example.certificateviewer;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import org.apache.batik.transcoder.Transcoder;
//import org.apache.batik.transcoder.TranscoderInput;
//import org.apache.batik.transcoder.TranscoderOutput;
//import org.apache.fop.svg.PDFTranscoder;
//import android.util.Log;
//
//
//public class SvgToPdfConverter {
//    public static void convertSvgToPdf(String svgFilePath, String outputPdfPath) {
//        try {
//            File svgFile = new File(svgFilePath);
//            File pdfFile = new File(outputPdfPath);
//
//            if (!svgFile.exists()) {
//                throw new Exception("SVG file not found at: " + svgFilePath);
//            }
//
//            // ✅ Use Apache Batik to convert SVG to PDF
//            Transcoder transcoder = new PDFTranscoder();
//            TranscoderInput input = new TranscoderInput(svgFile.toURI().toString());
//            OutputStream os = new FileOutputStream(pdfFile);
//            TranscoderOutput output = new TranscoderOutput(os);
//
//            transcoder.transcode(input, output);
//            os.flush();
//            os.close();
//
//            Log.d("SvgToPdfConverter", "✅ PDF generated successfully at: " + outputPdfPath);
//        } catch (Exception e) {
//            Log.e("SvgToPdfConverter", "❌ Failed to convert SVG to PDF: " + e.getMessage());
//        }
//    }
//}
