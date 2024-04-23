package org.dindier.oicraft.model;

import jakarta.persistence.*;

@Entity
public class Checkpoint {
    @EmbeddedId
    private SubmissionAndIOPair submissionAndIOPair;

    public enum Status {
        P, AC, WA, TLE, MLE, RE, CE
    }

    @Enumerated(EnumType.STRING)
    private Status status;

    private int usedTime;
    private int usedMemory;
    private String info;

    protected Checkpoint() {
    }

    // public Checkpoint(int submissionId, int ioPairId) {
    //     this.submissionAndIOPair = new SubmissionAndIOPair(submissionId, ioPairId);
    // }

    @Embeddable
    public static class SubmissionAndIOPair {
        @OneToOne
        @JoinColumn(name = "submission_id")
        private Submission submission;

        @OneToOne
        @JoinColumn(name = "io_pair_id")
        private IOPair ioPair;

        protected SubmissionAndIOPair() {
        }

        public int getSubmissionId() {
            return submission.getId();
        }

        public void setSubmissionId(int submissionId) {
            this.submission.setId(submissionId);
        }

        public IOPair getIOPair() {
            return ioPair;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SubmissionAndIOPair other)) {
                return false;
            }
            return submission.getId() == other.submission.getId() && ioPair.equals(other.ioPair);
        }

        @Override
        public int hashCode() {
            return submission.getId() + ioPair.hashCode();
        }
    }

    public int getSubmissionId() {
        return submissionAndIOPair.getSubmissionId();
    }

    public void setSubmissionId(int submissionId) {
        this.submissionAndIOPair.setSubmissionId(submissionId);
    }

    public IOPair getIOPair() {
        return submissionAndIOPair.getIOPair();
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
