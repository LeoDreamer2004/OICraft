package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Checkpoint;

public interface CheckpointDao {
    /**
     * Create a new checkpoint in the database
     *
     * @param checkpoint the checkpoint to create
     * @return A {@code Checkpoint} class with the checkpoint's ID set.
     */
    Checkpoint createCheckpoint(Checkpoint checkpoint);

    /**
     * Update a checkpoint in the database
     *
     * @param checkpoint the checkpoint to update
     * @return A {@code Checkpoint} class with updated information.
     */
    @SuppressWarnings("UnusedReturnValue")
    Checkpoint updateCheckpoint(Checkpoint checkpoint);

    /**
     * Get all checkpoints corresponding to a submission
     *
     * @param id the ID of the submission
     * @return A list of {@code Checkpoint} classes that correspond to the submission.
     * If the submission has no checkpoint, an empty list will be returned.
     */
    Iterable<Checkpoint> getCheckpointsBySubmissionId(int id);
}
