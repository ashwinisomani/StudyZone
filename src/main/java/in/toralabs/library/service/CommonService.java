package in.toralabs.library.service;

import in.toralabs.library.jpa.model.UserDetailModel;
import in.toralabs.library.util.ResponseModel;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.HashMap;

public interface CommonService {

    void generatePdf(UserDetailModel userDetailModel, String rootPathForReceipts) throws Exception;

    void updateListWithImageUrlsRatherThanFileNames(UserDetailModel userDetailModel);

    ResponseEntity<ResponseModel> sendMessage(String mobileNumber, Long entryTimeInLong, HashMap<String, String> timeSlotDescMap);

    String decodeMobileNumber(String encodedMobileNumber);

    String reportOfAllCustomers() throws ParseException;

    String getCustomizedReport();
}