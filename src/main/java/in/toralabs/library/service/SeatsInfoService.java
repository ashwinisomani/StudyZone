package in.toralabs.library.service;

import in.toralabs.library.jpa.model.SeatsInfoModel;
import org.springframework.http.ResponseEntity;

public interface SeatsInfoService {
    ResponseEntity<Object> updateSeatsInfoInDb(SeatsInfoModel seatsInfoModel);
}
