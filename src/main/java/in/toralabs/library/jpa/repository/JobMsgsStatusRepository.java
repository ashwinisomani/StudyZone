package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.JobMsgsStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobMsgsStatusRepository extends JpaRepository<JobMsgsStatusModel, Long> {

    List<JobMsgsStatusModel> findByJobId(String jobId);
}
