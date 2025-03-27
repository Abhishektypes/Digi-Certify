package com.example.certificateviewer;

public class Certificate {
    private String id;
    private String studentName;
    private String course;
    private String issueDate;
    private String certificateHash;

    public Certificate() {
        // Default constructor required for Firestore
    }

    public Certificate(String id, String studentName, String course, String issueDate, String certificateHash) {
        this.id = id;
        this.studentName = studentName;
        this.course = course;
        this.issueDate = issueDate;
        this.certificateHash = certificateHash;
    }

    public String getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourse() {
        return course;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getCertificateHash() {
        return certificateHash;
    }

    public void setCertificateHash(String certificateHash) {
        this.certificateHash = certificateHash;
    }
}
