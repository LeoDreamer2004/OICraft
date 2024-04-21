package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.model.Checkpoint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("checkpointDao")
public class JdbcCheckpointDao implements CheckpointDao {
    @Override
    public void createCheckpoint(Checkpoint checkpoint) {

    }

    @Override
    public List<Checkpoint> getCheckpointsBySubmissionId(int id) {
        // TODO: Implement this method


        // A temporary implementation for testing
        Checkpoint cp1 = new Checkpoint(1, 1);
        cp1.setStatus(Checkpoint.Status.AC);
        cp1.setInfo("Accepted");
        cp1.setUsedTime(24);
        cp1.setUsedMemory(1024);
        Checkpoint cp2 = new Checkpoint(1, 2);
        cp2.setStatus(Checkpoint.Status.WA);
        cp2.setInfo("Wrong Answer");
        cp2.setUsedTime(24);
        cp2.setUsedMemory(1024);
        Checkpoint cp3 = new Checkpoint(1, 3);
        cp3.setStatus(Checkpoint.Status.TLE);
        cp3.setInfo("Time Limit Exceeded");
        cp3.setUsedTime(1000);
        cp3.setUsedMemory(1024);
        return List.of(cp1, cp2, cp3);
    }
}
