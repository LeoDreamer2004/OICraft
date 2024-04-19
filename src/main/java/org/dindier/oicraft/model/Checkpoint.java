package org.dindier.oicraft.model;

public class Checkpoint {
    private int submissionId;
    private int ioPairId;
    private String status; // P, AC, WA, TLE, MLE, RE, CE
    private int usedTime;
    private int usedMemory;
    private String info;

    public Checkpoint(int submissionId, int ioPairId) {
        this.submissionId = submissionId;
        this.ioPairId = ioPairId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getIoPairId() {
        return ioPairId;
    }

    public void setIoPairId(int ioPairId) {
        this.ioPairId = ioPairId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(int usedTime) {
        this.usedTime = usedTime;
    }

    public int getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    /*
    * A method to format the usage string
    */
    public String formatUsageString() {
        String timeStr, memoryStr;
        if (usedTime < 1000) {
            timeStr = usedTime + "ms";
        } else {
            timeStr = String.format("%.2f", usedTime / 1000.0) + "s";
        }

        if (usedMemory < 1024) {
            memoryStr = usedMemory + "KB";
        } else {
            memoryStr = String.format("%.2f", usedMemory / 1024.0) + "MB";
        }

        return timeStr + " / " + memoryStr;
    }
}
