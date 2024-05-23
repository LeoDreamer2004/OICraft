package org.dindier.oicraft.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.repository.CheckpointRepository;
import org.dindier.oicraft.model.Checkpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("checkpointDao")
@Slf4j
public class JpaCheckpointDao implements CheckpointDao {
    private CheckpointRepository checkpointRepository;

    @Autowired
    public void setCheckpointRepository(CheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
    }

    @Override
    public Checkpoint saveCheckpoint(Checkpoint checkpoint) {
        return this.checkpointRepository.save(checkpoint);
    }
}
