package org.tyler.passprotector.passprotector;

public class PasswordTableRow {
    // Must be public for gson, @Expose isn't working nor getters
    public String sourceColumn;
    public String usernameColumn;
    public String passwordColumn;

    public PasswordTableRow(String sourceColumn, String usernameColumn, String passwordColumn) {
        this.sourceColumn = sourceColumn;
        this.usernameColumn = usernameColumn;
        this.passwordColumn = passwordColumn;
    }

    public String getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(String sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public String getUsernameColumn() {
        return usernameColumn;
    }

    public void setUsernameColumn(String usernameColumn) {
        this.usernameColumn = usernameColumn;
    }

    public String getPasswordColumn() {
        return passwordColumn;
    }

    public void setPasswordColumn(String passwordColumn) {
        this.passwordColumn = passwordColumn;
    }
}
