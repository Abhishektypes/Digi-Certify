# Digi-Certify
The Blockchain-Based Digital Certificate Verifier is an Android application designed to issue, verify, and manage digital certificates securely using blockchain technology. The app provides an efficient and tamper-proof system for educational institutions and organizations to generate certificates with unique QR codes and validate them instantly.

Key Features:
✅ Secure Certificate Issuance: Institutions can issue certificates with unique IDs and cryptographic hashes stored on the blockchain.
✅ Blockchain Verification: The app uses SHA-256 hashing to ensure certificates remain immutable and verifiable.
✅ QR Code Integration: Each certificate includes a QR code for quick verification, generated using ZXing.
✅ Role-Based Access:

Admins (Teachers): Can issue and manage certificates.

Students/Employers: Can scan and verify certificates.
✅ Firebase Integration:

Firestore Database for certificate records.

Firebase Authentication for secure login.

Firebase Storage for storing certificate PDFs.
✅ Canva Template Support: Users can upload custom-designed certificate templates (PNG/PDF) with predefined placeholders for QR codes and certificate IDs.
✅ PDF Preview & Generation: Before finalizing, users can preview certificates generated using the Canva template.
✅ Error-Free Implementation: The app ensures a smooth user experience with properly arranged Java files and optimized Firebase security rules.

Technology Stack:
Android Studio (Ladybug Version) – Java-based development

Firebase – Firestore for database, Storage for PDFs

ZXing Library – QR code generation and scanning

Blockchain (SHA-256) – Hashing certificate data for security

PDFViewer Library – Displaying PDF certificates within the app

Future Enhancements:
🔹 Bulk Certificate Generation: Automate issuing multiple certificates at once.
🔹 Student Correction Requests: Allow students to request corrections for certificate details.
🔹 Advanced Role Management: Custom claims in Firebase for more role flexibility.

This app aims to streamline digital certification, reduce fraud, and provide a seamless verification process using blockchain and QR-based authentication.
