package org.dindier.oicraft.util.code.lang;

/**
 * The status for a checkpoint
 */
public enum Status {
    P, // Pending
    AC, // Accepted
    WA, // Wrong Answer
    TLE, // Time Limit Exceeded
    MLE, // Memory Limit Exceeded
    RE, // Runtime Error
    CE, // Compile Error
    UKE // Unknown Error
}
