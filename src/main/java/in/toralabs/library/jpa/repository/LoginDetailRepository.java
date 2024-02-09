package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.LoginModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDetailRepository extends JpaRepository<LoginModel, Long> {
    LoginModel findByUserName(String userName);
}
