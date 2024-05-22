package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;
import org.dindier.oicraft.util.code.lang.Status;

@Entity
@Data
public class Checkpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Submission submission;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private IOPair ioPair;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    private int usedTime;

    private int usedMemory;

    private String info;

    protected Checkpoint() {
    }

    public Checkpoint(Submission submission, IOPair ioPair) {
        this.submission = submission;
        this.ioPair = ioPair;
        this.status = Status.P;
        this.usedTime = 0;
        this.usedMemory = 0;
        this.info = "";
    }

    public String getStatusString() {
        return status.toString();
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
