package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Checkpoint;

import java.util.List;

public interface CheckpointDao {
    /**
     * Create the checkpoint
     */
    void createCheckpoint(Checkpoint checkpoint);

    /**
     * Get the checkpoint by submission id
     */
    List<Checkpoint> getCheckpointsBySubmissionId(int id);
}
