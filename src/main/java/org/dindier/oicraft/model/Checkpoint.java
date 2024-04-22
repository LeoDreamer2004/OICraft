package org.dindier.oicraft.model;

import jakarta.persistence.*;

@Entity
public class Checkpoint {
    @EmbeddedId
    private SubmissionAndIOPair submissionAndIOPair;

    public enum Status {
        P, AC, WA, TLE, MLE, RE, CE
    }

    private Status status;

    private int usedTime;
    private int usedMemory;
    private String info;

    protected Checkpoint() {
    }

    public Checkpoint(int submissionId, int ioPairId) {
        this.submissionAndIOPair = new SubmissionAndIOPair(submissionId, ioPairId);
    }

    @Embeddable
    public static class SubmissionAndIOPair {
        // FIXME: This may be wrong
        @OneToOne
        @JoinColumn(name = "submission_id")
        private Submission submission;
        @OneToOne
        @JoinColumn(name = "io_pair_id")
        private IOPair ioPairId;

        public SubmissionAndIOPair(int submissionId, int ioPairId) {
            this.submission = new Submission();
            this.ioPairId = new IOPair();
            this.submission.setId(submissionId);
            this.ioPairId.setId(ioPairId);
        }

        protected SubmissionAndIOPair() {
        }

        public int getSubmissionId() {
            return submission.getId();
        }

        public void setSubmissionId(int submissionId) {
            this.submission.setUserId(submissionId);
        }

        public int getIoPairId() {
            return ioPairId.getId();
        }

        public void setIoPairId(int ioPairId) {
            this.ioPairId.setId(ioPairId);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SubmissionAndIOPair other)) {
                return false;
            }
            return submission.getId() == other.getSubmissionId() && ioPairId.getId() == other.getIoPairId();
        }

        @Override
        public int hashCode() {
            return submission.getId() * 31 + ioPairId.getId();
        }
    }

    public int getSubmissionId() {
        return submissionAndIOPair.getSubmissionId();
    }

    public void setSubmissionId(int submissionId) {
        this.submissionAndIOPair.setSubmissionId(submissionId);
    }

    public int getIoPairId() {
        return submissionAndIOPair.getIoPairId();
    }

    public void setIoPairId(int ioPairId) {
        this.submissionAndIOPair.setIoPairId(ioPairId);
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
