package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.EnquiryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnquiryRepository extends JpaRepository<EnquiryModel, Long> {

    @Query(value = "select count(*) from lib_enquiry where mobile_no = ?1", nativeQuery = true)
    Integer checkIfMobileNumberIsPresent(@Param("mobile_no") String mobileNumber);

    List<EnquiryModel> findAllByOrderByCreationDateDesc();

    List<EnquiryModel> findTop100ByOrderByCreationDateDesc();
    List<EnquiryModel> findTop200ByOrderByCreationDateDesc();
    List<EnquiryModel> findTop300ByOrderByCreationDateDesc();
    List<EnquiryModel> findTop400ByOrderByCreationDateDesc();
    List<EnquiryModel> findTop500ByOrderByCreationDateDesc();

}