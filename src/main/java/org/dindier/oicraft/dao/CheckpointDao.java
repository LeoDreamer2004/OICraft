package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Checkpoint;

import java.util.List;

public interface CheckpointDao {
    void createCheckpoint(Checkpoint checkpoint);

    List<Checkpoint> getCheckpointsBySubmissionId(int id);
}
