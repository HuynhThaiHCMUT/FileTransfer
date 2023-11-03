package com.computernetwork.filetransfer.Model;

import java.sql.Date;

public class ServerFileData {
    private String name;
    private Long size;
    private String description;
    private Date uploadedDate;
    private String owner;
    private String ownerIP;
    private boolean isOnline;

    public ServerFileData(String name, Long size, String description, Date uploadedDate, String owner, String ownerIP, boolean isOnline) {
        this.name = name;
        this.size = size;
        this.description = description;
        this.uploadedDate = uploadedDate;
        this.owner = owner;
        this.ownerIP = ownerIP;
        this.isOnline = isOnline;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerIP() {
        return ownerIP;
    }

    public void setOwnerIP(String ownerIP) {
        this.ownerIP = ownerIP;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String formattedFileSize() {
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
}
