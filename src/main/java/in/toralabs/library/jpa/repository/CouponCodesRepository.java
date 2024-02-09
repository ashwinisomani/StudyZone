package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.CouponCodeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponCodesRepository extends JpaRepository<CouponCodeModel, String> {

}
