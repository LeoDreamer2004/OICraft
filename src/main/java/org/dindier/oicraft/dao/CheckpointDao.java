package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Checkpoint;

public interface CheckpointDao {
    /**
     * Save a new checkpoint in the database
     *
     * @param checkpoint the checkpoint to save
     * @return A {@code Checkpoint} class with the checkpoint's ID set.
     */
    Checkpoint saveCheckpoint(Checkpoint checkpoint);
}
