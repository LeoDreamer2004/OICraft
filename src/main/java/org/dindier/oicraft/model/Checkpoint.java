package org.dindier.oicraft.model;

import jakarta.persistence.*;

@Entity
public class Checkpoint {
    @Id
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Submission submission;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private IOPair ioPair;

    public enum Status {
        P, AC, WA, TLE, MLE, RE, CE;

        public static Status fromString(String status) {
            for (Status s: Status.values()) {
                if (s.toString().equals(status)) {
                    return s;
                }
            }
            return null;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    private int usedTime;
    private int usedMemory;
    private String info;

    protected Checkpoint() {
    }

    public Checkpoint(Submission submission, IOPair ioPair, Status status, int usedTime, int usedMemory, String info) {
        this.submission = submission;
        this.ioPair = ioPair;
        this.status = status;
        this.usedTime = usedTime;
        this.usedMemory = usedMemory;
        this.info = info;
    }

    public Submission getSubmission() {
        return submission;
    }

    public IOPair getIOPair() {
        return ioPair;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(Status status) {
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

    public int getId() {
        return id;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public void setIoPair(IOPair ioPair) {
        this.ioPair = ioPair;
    }

    /**
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
