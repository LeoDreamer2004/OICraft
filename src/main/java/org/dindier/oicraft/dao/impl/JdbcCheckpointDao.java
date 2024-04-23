package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.repository.CheckpointRepository;
import org.dindier.oicraft.dao.repository.SubmissionRepository;
import org.dindier.oicraft.model.Checkpoint;
import org.dindier.oicraft.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("checkpointDao")
public class JdbcCheckpointDao implements CheckpointDao {
    private CheckpointRepository checkpointRepository;
    private SubmissionRepository submissionRepository;

    @Autowired
    public void setCheckpointRepository(CheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
    }

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    public Checkpoint createCheckpoint(Checkpoint checkpoint) {
        return this.checkpointRepository.save(checkpoint);
    }

    @Override
    public Iterable<Checkpoint> getCheckpointsBySubmissionId(int id) {
        return submissionRepository
                .findById(id)
                .map(Submission::getCheckpoints)
                .orElse(null);
    }
}
