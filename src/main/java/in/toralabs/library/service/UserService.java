package in.toralabs.library.service;

import in.toralabs.library.jpa.model.UserDetailModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserService {

    @Transactional
    ResponseEntity<Object> saveUserInfo(UserDetailModel userDetailModel, String entryDate, String exitDate, Optional<String> recordCreationDate) throws Exception;
}
