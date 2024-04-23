package org.dindier.oicraft.dao;

import org.dindier.oicraft.model.Checkpoint;

import java.util.List;

public interface CheckpointDao {
    Checkpoint createCheckpoint(Checkpoint checkpoint);

    Iterable<Checkpoint> getCheckpointsBySubmissionId(int id);
}
