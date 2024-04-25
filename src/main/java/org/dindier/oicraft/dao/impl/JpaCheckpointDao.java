package org.dindier.oicraft.dao.impl;

import org.dindier.oicraft.dao.CheckpointDao;
import org.dindier.oicraft.dao.repository.CheckpointRepository;
import org.dindier.oicraft.dao.repository.SubmissionRepository;
import org.dindier.oicraft.model.Checkpoint;
import org.dindier.oicraft.model.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("checkpointDao")
public class JpaCheckpointDao implements CheckpointDao {
    private CheckpointRepository checkpointRepository;
    private SubmissionRepository submissionRepository;
    private final Logger logger = LoggerFactory.getLogger(JpaCheckpointDao.class);

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
        checkpoint = this.checkpointRepository.save(checkpoint);
        logger.info("Create checkpoint for submission {} (id: {})",
                checkpoint.getSubmission().getId(), checkpoint.getId());
        return checkpoint;
    }

    @Override
    public Checkpoint updateCheckpoint(Checkpoint checkpoint) {
        checkpoint = this.checkpointRepository.save(checkpoint);
        logger.info("Update checkpoint for submission {} (id: {})",
                checkpoint.getSubmission().getId(), checkpoint.getId());
        return checkpoint;
    }

    @Override
    public Iterable<Checkpoint> getCheckpointsBySubmissionId(int id) {
        return submissionRepository
                .findById(id)
                .map(Submission::getCheckpoints)
                .orElse(List.of());
    }
}
