package com.example.certificateviewer;

import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class BlockchainHelper {
    private static HashMap<String, String> blockchainMock = new HashMap<>();

    // ✅ Compute SHA-256 Hash for Certificate
    public static String computeHash(Certificate certificate) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = certificate.getStudentName() + certificate.getCourse() + certificate.getIssueDate();
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("BlockchainHelper", "SHA-256 algorithm not found", e);
            return null;
        }
    }

    // ✅ Store Certificate Hash in Mock Blockchain
    public static void storeCertificate(Certificate certificate) {
        String hash = computeHash(certificate);
        if (hash != null) {
            blockchainMock.put(certificate.getId(), hash);
            certificate.setCertificateHash(hash); // Save hash in the certificate object
            Log.d("BlockchainHelper", "Stored Certificate Hash: " + hash);
        } else {
            Log.e("BlockchainHelper", "Failed to generate certificate hash!");
        }
    }


    // ✅ Verify Certificate Hash
    public static boolean verifyCertificate(Certificate certificate) {
        String storedHash = blockchainMock.get(certificate.getId());
        String currentHash = computeHash(certificate);
        return storedHash != null && storedHash.equals(currentHash);
    }

    private static void certificateHashUpdated(String hash) {
        Log.d("BlockchainHelper", "Stored certificate with hash: " + hash);
    }
}
