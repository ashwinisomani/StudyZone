package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.SeatsInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatsInfoRepository extends JpaRepository<SeatsInfoModel, Integer> {

}
