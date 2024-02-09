package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.EnquiryModel;
import in.toralabs.library.jpa.repository.EnquiryRepository;
import in.toralabs.library.util.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

@RestController
public class EnquiryController {

    private final ResponseModel responseModel = new ResponseModel();

    @Autowired
    EnquiryRepository enquiryRepository;

    @PostMapping(value = "/saveEnquiry/{creationDate}")
    public ResponseEntity<Object> saveEnquiry(@RequestBody EnquiryModel enquiryModel, @PathVariable("creationDate") String creationDate) {
        try {
            if (enquiryModel.getMobileNo() == null || enquiryModel.getWhatsappNo() == null) {
                responseModel.setStatus("Mobile number and whatsapp number cannot be empty");
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
            }
            if (enquiryRepository.checkIfMobileNumberIsPresent(enquiryModel.getMobileNo()) > 0) {
                responseModel.setStatus("This mobile number has already enquired. Unable to save it");
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
            }
            enquiryModel.setCreationDate(new SimpleDateFormat("yyyy-MM-dd").parse(creationDate));
            enquiryRepository.save(enquiryModel);
            responseModel.setStatus("Enquiry saved successfully");
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setStatus("Some error occurred");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/fetchAllEnquiries")
    public ResponseEntity<Object> fetchAllEnquiries() {
        return ResponseEntity.status(HttpStatus.OK).body(enquiryRepository.findAllByOrderByCreationDateDesc());
    }
}
