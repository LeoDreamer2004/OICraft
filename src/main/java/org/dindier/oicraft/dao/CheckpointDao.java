package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Checkpoint;

import java.util.List;

public interface CheckpointDao {
    void createCheckpoint(Checkpoint checkpoint);

    Iterable<Checkpoint> getCheckpointsBySubmissionId(int id);
}
