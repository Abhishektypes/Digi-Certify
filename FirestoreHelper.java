package com.example.certificateviewer;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void saveCertificate(Certificate certificate) {
        Map<String, Object> certData = new HashMap<>();
        certData.put("id", certificate.getId());
        certData.put("studentName", certificate.getStudentName());
        certData.put("course", certificate.getCourse());
        certData.put("issueDate", certificate.getIssueDate());
        certData.put("certificateHash", certificate.getCertificateHash());

        db.collection("certificates")
                .document(certificate.getId())
                .set(certData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ Certificate stored successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "❌ Error storing certificate", e));
    }

    public interface CertificateCallback {
        void onCertificateFetched(Certificate certificate);
        void onError(Exception e);
    }

    public void getCertificateById(String certId, CertificateCallback callback) {
        DocumentReference docRef = db.collection("certificates").document(certId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Certificate certificate = documentSnapshot.toObject(Certificate.class);
                        callback.onCertificateFetched(certificate);
                    } else {
                        callback.onError(new Exception("Certificate not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }
}
