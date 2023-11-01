package com.computernetwork.filetransfer.Model;

import java.sql.Date;

public class FileData {
    private String name;
    private Long size;
    private String description;
    private Date uploadedDate;
    private String fileLocation;

    public FileData(String name, Long size, String description, String fileLocation) {
        this.name = name;
        this.size = size;
        this.description = description;
        uploadedDate = new Date(new java.util.Date().getTime());
        this.fileLocation = fileLocation;
    }

    public FileData(String name, Long size, String description, Date uploadedDate, String fileLocation) {
        this.name = name;
        this.size = size;
        this.description = description;
        this.uploadedDate = uploadedDate;
        this.fileLocation = fileLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String formatedFileSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            double sizeInKB = size / 1024.0;
            return String.format("%.2f KB", sizeInKB);
        } else if (size < 1024 * 1024 * 1024) {
            double sizeInMB = size / (1024.0 * 1024);
            return String.format("%.2f MB", sizeInMB);
        } else {
            double sizeInGB = size / (1024.0 * 1024 * 1024);
            return String.format("%.2f GB", sizeInGB);
        }
    }

    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            double sizeInKB = sizeInBytes / 1024.0;
            return String.format("%.2f KB", sizeInKB);
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            double sizeInMB = sizeInBytes / (1024.0 * 1024);
            return String.format("%.2f MB", sizeInMB);
        } else {
            double sizeInGB = sizeInBytes / (1024.0 * 1024 * 1024);
            return String.format("%.2f GB", sizeInGB);
        }
    }
}
