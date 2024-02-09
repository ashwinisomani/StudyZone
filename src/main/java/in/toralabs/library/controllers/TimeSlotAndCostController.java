package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.TimeSlotCostMappingModel;
import in.toralabs.library.jpa.repository.TimeSlotCostMappingRepository;
import in.toralabs.library.util.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TimeSlotAndCostController {

    @Autowired
    private TimeSlotCostMappingRepository timeSlotCostMappingRepository;

    private final ResponseModel responseModel = new ResponseModel();

    @GetMapping(value = "/fetchAllTimeSlotsAndCosts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchAllTimeSlotsAndCosts() {
        List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAllByOrderByIdAsc();
        if (timeSlotCostMappingModelList.size() > 0) {
            return ResponseEntity.ok().body(timeSlotCostMappingModelList);
        } else {
            responseModel.setStatus("No time slot and cost mapping found in the database.");
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
    }

    @PostMapping(value = "/addNewTimeSlotAndCost", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addNewTimeSlotAndCost(@RequestBody TimeSlotCostMappingModel timeSlotCostMappingModel) {
        try {
            return ResponseEntity.ok().body(timeSlotCostMappingRepository.save(timeSlotCostMappingModel));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Unable to add a new time slot and cost mapping.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @PostMapping(value = "/deleteTimeSlotAndCost", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteTimeSlotAndCost(@RequestBody TimeSlotCostMappingModel timeSlotCostMappingModel) {
        try {
            timeSlotCostMappingRepository.delete(timeSlotCostMappingModel);
            responseModel.setStatus("Entity deleted successfully.");
            responseModel.setExceptionMessage("");
            return ResponseEntity.ok().body(responseModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Unable to delete this time slot.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }
}
