package in.toralabs.library.service.impl;

import in.toralabs.library.jpa.model.SaveCustomerDataResponseModel;
import in.toralabs.library.jpa.model.UserDetailModel;
import in.toralabs.library.jpa.model.UserDetailModelPK;
import in.toralabs.library.jpa.repository.TimeSlotCostMappingRepository;
import in.toralabs.library.jpa.repository.UserDetailRepository;
import in.toralabs.library.service.UserService;
import in.toralabs.library.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private TimeSlotCostMappingRepository timeSlotCostMappingRepository;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<Object> saveUserInfo(UserDetailModel userDetailModel, String entryDate, String exitDate, Optional<String> recordCreationDate) throws Exception {
        // name must not contain any comma values (that will cause issues in marketing apis)

        String updatedName = userDetailModel.getName().replaceAll(",", "");

        userDetailModel.setName(updatedName);
        userDetailModel.setEntryDate(simpleDateFormat.parse(entryDate));
        userDetailModel.setExitDate(simpleDateFormat.parse(exitDate));

        Long entryTimeInLong = 0L;
        if ("A".equals(userDetailModel.getActionStatus())) {
            entryTimeInLong = System.currentTimeMillis();
            userDetailModel.setEntryTimeInLong(entryTimeInLong);
        } else {
            entryTimeInLong = userDetailModel.getEntryTimeInLong();
        }
        if (recordCreationDate.isPresent()) {
            userDetailModel.setRecordCreationDate(simpleDateFormat.parse(recordCreationDate.get()));
        }
        userDetailModel.setModificationDtm(Utils.getCurrentTimestampInIST());

        logger.info("Before save and flush in saveUserInfo");
        userDetailModel = userDetailRepository.saveAndFlush(userDetailModel);
        logger.info("After save and flush in saveUserInfo");

        UserDetailModelPK userDetailModelPK = new UserDetailModelPK();
        userDetailModelPK.setMobileNumber(userDetailModel.getMobileNumber());

        userDetailModelPK.setEntryTimeInLong(entryTimeInLong);
        Optional<UserDetailModel> newUser = userDetailRepository.findById(userDetailModelPK);
        SaveCustomerDataResponseModel saveCustomerDataResponseModel = new SaveCustomerDataResponseModel();
        saveCustomerDataResponseModel.setAllottedSeatNo(newUser.get().getBookedSeatNumber());
        saveCustomerDataResponseModel.setHasBookedReserved(newUser.get().isHasBookedReservedSeat());
        saveCustomerDataResponseModel.setStatus("Saved customer data successfully.");
        saveCustomerDataResponseModel.setTransactionId(newUser.get().getTransactionId() + "");
        saveCustomerDataResponseModel.setEntryTimeInLong(newUser.get().getEntryTimeInLong());
        saveCustomerDataResponseModel.setName(newUser.get().getName());
        saveCustomerDataResponseModel.setMobileNumber(newUser.get().getMobileNumber());

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        String startDate = outputFormat.format(inputFormat.parse(newUser.get().getEntryDate().toString()));
        String endDate = outputFormat.format(inputFormat.parse(newUser.get().getExitDate().toString()));

        saveCustomerDataResponseModel.setStartDate(startDate);
        saveCustomerDataResponseModel.setEndDate(endDate);
        String timeSlot = newUser.get().getTimeSlotBookedTemp();
        saveCustomerDataResponseModel.setSlotDetails(timeSlot + " (" + timeSlotCostMappingRepository.getTimeSlotDescription(timeSlot) + ")");
        return ResponseEntity.status(HttpStatus.OK).body(saveCustomerDataResponseModel);
    }
}