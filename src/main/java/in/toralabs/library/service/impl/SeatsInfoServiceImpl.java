package in.toralabs.library.service.impl;

import in.toralabs.library.jpa.model.SeatsInfoModel;
import in.toralabs.library.jpa.repository.SeatsInfoRepository;
import in.toralabs.library.service.SeatsInfoService;
import in.toralabs.library.util.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SeatsInfoServiceImpl implements SeatsInfoService {
    private static final Logger logger = LoggerFactory.getLogger(SeatsInfoServiceImpl.class);

    @Autowired
    SeatsInfoRepository seatsInfoRepository;

    ResponseModel responseModel = new ResponseModel();

    @Override
    public ResponseEntity<Object> updateSeatsInfoInDb(SeatsInfoModel seatsInfoModel) {
        try {
            logger.info("Trying to update the seats info --> Reserved Seats: " + seatsInfoModel.getReservedSeats() + ", Total Seats: " + seatsInfoModel.getTotalSeats());
            seatsInfoRepository.deleteAll();
            return ResponseEntity.ok().body(seatsInfoRepository.save(seatsInfoModel));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Unable to update the seats info.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }
}
