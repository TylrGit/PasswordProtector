package org.tyler.passprotector.passprotector;

import javafx.scene.control.Button;

public class FileTableRow {
    // True = locked(encrypted), False = unlocked(unencrypted)
    private Boolean lockStatus;
    private final Button lockColumn;
    private String fileNameColumn;

    public FileTableRow(boolean lockStatus, Button encryptionStatus, String fileName) {
        this.lockStatus = lockStatus;
        this.lockColumn = encryptionStatus;
        this.fileNameColumn = fileName;
    }

    public Button getLockColumn() {
        return lockColumn;
    }

    public String getFileNameColumn() {
        return fileNameColumn;
    }

    public void setFileNameColumn(String fileNameColumn) {
        this.fileNameColumn = fileNameColumn;
    }

    public Boolean getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Boolean lockStatus) {
        this.lockStatus = lockStatus;
    }
}
