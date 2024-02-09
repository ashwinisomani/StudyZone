package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.AsyncJobsModel;
import in.toralabs.library.jpa.model.AsyncJobsModelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsyncJobsRepository extends JpaRepository<AsyncJobsModel, AsyncJobsModelPK> {
    List<AsyncJobsModel> findAllByOrderByStartDtmDesc();
}
