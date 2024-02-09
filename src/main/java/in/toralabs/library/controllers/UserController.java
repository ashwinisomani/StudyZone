package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.TimeSlotCostMappingModel;
import in.toralabs.library.jpa.model.UserDetailModel;
import in.toralabs.library.jpa.model.UserDetailModelPK;
import in.toralabs.library.jpa.repository.TimeSlotCostMappingRepository;
import in.toralabs.library.jpa.repository.UserDetailRepository;
import in.toralabs.library.service.CommonService;
import in.toralabs.library.service.UserService;
import in.toralabs.library.util.ResponseModel;
import in.toralabs.library.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@RestController
public class UserController {
    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private TimeSlotCostMappingRepository timeSlotCostMappingRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserService userService;

    @Value("${project.images}")
    private String rootPathForImages;

    @Value("${project.receipts}")
    private String rootPathForReceipts;

    private final ResponseModel responseModel = new ResponseModel();

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping(value = "/saveCustomerData", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveCustomerData(@RequestPart(value = "idProofFile", required = false) Optional<MultipartFile> idProofFile,
                                                   @RequestPart(value = "customerImgFile", required = false) Optional<MultipartFile> customerPhotoFile,
                                                   @RequestPart(value = "paymentProofFile", required = false) Optional<MultipartFile> paymentProofFile,
                                                   @RequestPart(value = "userDetails", required = true) UserDetailModel userDetailModel,
                                                   @RequestPart(value = "entryDate", required = true) String entryDate,
                                                   @RequestPart(value = "exitDate", required = true) String exitDate,
                                                   @RequestPart(value = "recordCreationDate", required = false) Optional<String> recordCreationDate
    ) {
        try {
            System.out.println("Action status : " + userDetailModel.getActionStatus());
            if (userDetailRepository.checkIfCustomerIsActive(userDetailModel.getMobileNumber()) > 0 && "A".equals(userDetailModel.getActionStatus())) {
                throw new Exception("Customer is already active in one of the slots");
            }
            String idProofFileName = userDetailModel.getMobileNumber().replaceAll(" ", "") + "_" + userDetailModel.getName().replaceAll(" ", "") + "_" + System.currentTimeMillis() + "_id_proof_image.jpg";
            String customerPhotoFileName = userDetailModel.getMobileNumber().replaceAll(" ", "") + "_" + userDetailModel.getName().replaceAll(" ", "") + "_" + System.currentTimeMillis() + "_customer_selfie_image.jpg";
            String paymentProofFileName = userDetailModel.getMobileNumber().replaceAll(" ", "") + "_" + userDetailModel.getName().replaceAll(" ", "") + "_" + System.currentTimeMillis() + "_payment_proof_image.jpg";

            File dir = new File(rootPathForImages);
            if (!dir.exists()) {
                dir.mkdir();
            }

            if (idProofFile.isEmpty() && (userDetailModel.getIdProofUrl() == null || !userDetailModel.getIdProofUrl().startsWith("http"))) {
                throw new Exception("ID proof url is missing or does not starts with http.");
            }
            if (customerPhotoFile.isEmpty() && (userDetailModel.getCustomerPhotoUrl() == null || !userDetailModel.getCustomerPhotoUrl().startsWith("http"))) {
                throw new Exception("Customer proof url is missing or does not starts with http.");
            }
            if (paymentProofFile.isEmpty() && (userDetailModel.getPaymentProofUrl() == null || !userDetailModel.getPaymentProofUrl().startsWith("http"))) {
                throw new Exception("Payment proof url is missing or does not starts with http.");
            }

            if (idProofFile.isPresent()) {
                Files.copy(idProofFile.get().getInputStream(), Paths.get(rootPathForImages + File.separator + idProofFileName));
                userDetailModel.setIdProofUrl(idProofFileName);
            } else {
                String[] s = userDetailModel.getIdProofUrl().split("/");
                userDetailModel.setIdProofUrl(s[s.length - 1]);
            }
            if (customerPhotoFile.isPresent()) {
                Files.copy(customerPhotoFile.get().getInputStream(), Paths.get(rootPathForImages + File.separator + customerPhotoFileName));
                userDetailModel.setCustomerPhotoUrl(customerPhotoFileName);
            } else {
                String[] s = userDetailModel.getCustomerPhotoUrl().split("/");
                userDetailModel.setCustomerPhotoUrl(s[s.length - 1]);
            }
            if (paymentProofFile.isPresent()) {
                Files.copy(paymentProofFile.get().getInputStream(), Paths.get(rootPathForImages + File.separator + paymentProofFileName));
                userDetailModel.setPaymentProofUrl(paymentProofFileName);
            } else {
                String[] s = userDetailModel.getPaymentProofUrl().split("/");
                userDetailModel.setPaymentProofUrl(s[s.length - 1]);
            }

            return userService.saveUserInfo(userDetailModel, entryDate, exitDate, recordCreationDate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Failed to save customer data. ");
            responseModel.setExceptionMessage(e.getMessage() + ". " + e.getCause());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/fetchAllCustomers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchAllCustomers() {
        List<UserDetailModel> userDetailModelList = userDetailRepository.findAllByOrderByExitDateAsc();
        if (userDetailModelList.size() > 0) {
            userDetailModelList.forEach(userDetailModel -> commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel));
            return ResponseEntity.ok().body(userDetailModelList);
        } else {
            responseModel.setStatus("No customer found in the database");
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
    }

    @GetMapping(value = "/fetchAllActiveCustomers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchAllActiveCustomers(@RequestParam(value = "timeSlot") String timeSlot) {
        try {
            if (timeSlot == null) {
                responseModel.setStatus("Time Slot cannot be null");
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
            } else {
                List<UserDetailModel> userDetailModelList;
                HashMap<String, String> timeSlotDescMap = new HashMap<>();
                List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAll();
                for (TimeSlotCostMappingModel timeCostModel : timeSlotCostMappingModelList) {
                    timeSlotDescMap.put(timeCostModel.getTimeSlotTitle(), timeCostModel.getTimeSlotDesc());
                }
                if (timeSlot.equals("All")) {
                    userDetailModelList = userDetailRepository.findAllActiveCustomers();
                    if (userDetailModelList.size() > 0) {
                        userDetailModelList.forEach(userDetailModel -> {
                            commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel);
                            String slot = userDetailModel.getTimeSlotBookedTemp();
                            userDetailModel.setTimeSlotBookedTemp(slot + " (" + timeSlotDescMap.get(slot) + ")");
                        });
                        return ResponseEntity.ok().body(userDetailModelList);
                    } else {
                        throw new Exception();
                    }
                } else if (timeSlot.equals("Customers")) {
                    //customers near end date
                    userDetailModelList = userDetailRepository.fetchNearEndDateCustomers();
                    logger.info("Customers near end date count " + userDetailModelList.size());
                    if (userDetailModelList.size() > 0) {
                        userDetailModelList.forEach(userDetailModel -> {
                            commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel);
                            String slot = userDetailModel.getTimeSlotBookedTemp();
                            userDetailModel.setTimeSlotBookedTemp(slot + " (" + timeSlotDescMap.get(slot) + ")");
                        });
                        logger.info("Customers near end date count " + userDetailModelList.size());
                        return ResponseEntity.ok().body(userDetailModelList);
                    } else {
                        throw new Exception();
                    }
                } else if (timeSlot.equals("Old")) {
                    userDetailModelList = userDetailRepository.fetchOldCustomers();
                    logger.info("Old Customers count before " + userDetailModelList.size());
                    if (userDetailModelList.size() > 0) {
                        LinkedHashSet<UserDetailModel> set = new LinkedHashSet<>();
                        HashSet<String> mobileNumberSet = new HashSet<>();
                        HashSet<String> activeCustomersNumberSet = new HashSet<>();
                        for (UserDetailModel userDetailModel : userDetailModelList) {
                            // using mobile numbers set to remove the duplicate old customers.
                            commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel);
                            String slot = userDetailModel.getTimeSlotBookedTemp();
                            userDetailModel.setTimeSlotBookedTemp(slot + " (" + timeSlotDescMap.get(slot) + ")");
                            // remove the number from old customers even if there are many entries with same number,
                            // and only one is active among that
                            if (userDetailRepository.checkIfCustomerIsActive(userDetailModel.getMobileNumber()) > 0) {
                                activeCustomersNumberSet.add(userDetailModel.getMobileNumber());
                                continue;
                            }
                            if (!mobileNumberSet.contains(userDetailModel.getMobileNumber())) {
                                set.add(userDetailModel);
                                mobileNumberSet.add(userDetailModel.getMobileNumber());
                            }
                        }
                        userDetailModelList.clear();
                        for (UserDetailModel model : set) {
                            if (!activeCustomersNumberSet.contains(model.getMobileNumber())) {
                                userDetailModelList.add(model);
                            }
                        }
                        logger.info("Old Customers count after removing active customers " + userDetailModelList.size());
                        return ResponseEntity.ok().body(userDetailModelList);
                    } else {
                        throw new Exception();
                    }
                } else if (timeSlot.equals("Future")) {
                    //customers near end date
                    userDetailModelList = userDetailRepository.fetchFutureCustomers();
                    if (userDetailModelList.size() > 0) {
                        userDetailModelList.forEach(userDetailModel -> {
                            commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel);
                            String slot = userDetailModel.getTimeSlotBookedTemp();
                            userDetailModel.setTimeSlotBookedTemp(slot + " (" + timeSlotDescMap.get(slot) + ")");
                        });
                        return ResponseEntity.ok().body(userDetailModelList);
                    } else {
                        throw new Exception();
                    }
                } else if (timeSlot.equals("ReservedActive")) {
                    userDetailModelList = userDetailRepository.findAllActiveReservedCustomers();
                    if (userDetailModelList.size() > 0) {
                        userDetailModelList.forEach(userDetailModel -> {
                            commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel);
                            String slot = userDetailModel.getTimeSlotBookedTemp();
                            userDetailModel.setTimeSlotBookedTemp(slot + " (" + timeSlotDescMap.get(slot) + ")");
                        });
                        return ResponseEntity.ok().body(userDetailModelList);
                    } else {
                        throw new Exception();
                    }
                } else {
                    userDetailModelList = userDetailRepository.findAllActiveCustomerInTimeSlot(timeSlot);
                    if (userDetailModelList.size() > 0) {
                        userDetailModelList.forEach(userDetailModel -> {
                            commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel);
                            String slot = userDetailModel.getTimeSlotBookedTemp();
                            userDetailModel.setTimeSlotBookedTemp(slot + " (" + timeSlotDescMap.get(slot) + ")");
                        });
                        return ResponseEntity.ok().body(userDetailModelList);
                    } else {
                        throw new Exception();
                    }
                }
            }
        } catch (Exception e) {
            responseModel.setStatus("No customer found in the database");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
    }

    @GetMapping(value = "/fetchCustomerDetail/{mobileNumber}/{entryTimeInLong}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchCustomerDetail(@PathVariable("mobileNumber") String mobileNumber, @PathVariable("entryTimeInLong") Long entryTimeInLong) throws Exception {
        UserDetailModelPK userDetailModelPK = new UserDetailModelPK();
        userDetailModelPK.setEntryTimeInLong(entryTimeInLong);
        userDetailModelPK.setMobileNumber(mobileNumber);
        Optional<UserDetailModel> model = userDetailRepository.findById(userDetailModelPK);
        if (model.isPresent()) {
            UserDetailModel userDetailModel = model.get();
            HashMap<String, String> timeSlotDescMap = new HashMap<>();
            List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAll();
            for (TimeSlotCostMappingModel timeCostModel : timeSlotCostMappingModelList) {
                timeSlotDescMap.put(timeCostModel.getTimeSlotTitle(), timeCostModel.getTimeSlotDesc());
            }
            String timeSlot = userDetailModel.getTimeSlotBookedTemp();
            userDetailModel.setTimeSlotBookedTemp(timeSlot + " (" + timeSlotDescMap.get(timeSlot) + ")");
            logger.info("Time slot booked is: " + userDetailModel.getTimeSlotBookedTemp());
            commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel);
            return ResponseEntity.ok().body(userDetailModel);
        } else {
            responseModel.setStatus("No customer found with mobile number " + mobileNumber);
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
    }

    @PostMapping(value = "/deleteCustomerDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteCustomerDetail(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("entryTimeInLong") Long entryTimeInLong) throws Exception {
        UserDetailModelPK userDetailModelPK = new UserDetailModelPK();
        userDetailModelPK.setEntryTimeInLong(entryTimeInLong);
        userDetailModelPK.setMobileNumber(mobileNumber);
        Optional<UserDetailModel> userDetailModel = userDetailRepository.findById(userDetailModelPK);
        if (userDetailModel.isPresent()) {
            userDetailRepository.delete(userDetailModel.get());
            responseModel.setStatus("Customer deleted successfully " + mobileNumber);
            responseModel.setExceptionMessage("");
            return ResponseEntity.ok().body(responseModel);
        } else {
            responseModel.setStatus("No customer found with mobile number " + mobileNumber);
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
    }

    @GetMapping(value = "/profiles/images/{imagePath}")
    public ResponseEntity<Object> getUserDetailImage(@PathVariable("imagePath") String imagePath) {
        try {
            InputStream resource = new FileInputStream(rootPathForImages + File.separator + imagePath);
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (imagePath.split("\\.")[imagePath.split("\\.").length - 1].equals("png")) {
                mediaType = MediaType.IMAGE_PNG;
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(resource.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + e.getCause());
            responseModel.setStatus("Unable to fetch the image.");
            responseModel.setExceptionMessage(e.getMessage() + " " + e.getCause());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/fetchActiveCustomersRightNow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchActiveCustomersRightNow() {
        HashMap<String, TreeSet<Integer>> timeCostMap = new HashMap<>();
        List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAll();
        for (TimeSlotCostMappingModel model : timeSlotCostMappingModelList) {
            timeCostMap.put(model.getTimeSlotTitle(),
                    new TreeSet<>(Utils.getIntegerListFromStringArray(model.getTimeDurationHrs().split(","))));
        }
        LocalTime localTime = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        String currentHour = localTime.toString().split(":")[0];
        List<String> slotList = new ArrayList<>();
        for (Map.Entry<String, TreeSet<Integer>> e : timeCostMap.entrySet()) {
            if (e.getValue().contains(Integer.parseInt(currentHour))) {
                slotList.add(e.getKey());
            }
        }
        if (slotList.size() == 0) {
            responseModel.setStatus("No customers found right now.");
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);// return response model.
        }
        List<UserDetailModel> userDetailModelList = userDetailRepository.findActiveCustomersRightNow(slotList);
        if (userDetailModelList.size() == 0) {
            responseModel.setStatus("No customers found right now.");
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);    // return response model.
        } else {
            userDetailModelList.forEach(userDetailModel -> commonService.updateListWithImageUrlsRatherThanFileNames(userDetailModel));
            return ResponseEntity.status(HttpStatus.OK).body(userDetailModelList);  // return list of user detail model.
        }
    }

    @GetMapping(value = "/getTimeSlotsForCustomerScreen")
    public ResponseEntity<Object> getTimeSlotsForCustomerScreen() {
        List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAllByOrderByIdAsc();
        List<String> list = new ArrayList<>();
        timeSlotCostMappingModelList.forEach(model -> {
            list.add(model.getTimeSlotTitle() + " (" + model.getTimeSlotDesc() + ")");
        });
        list.add("All Active Customers");
        list.add("Active Customers Right Now");
        list.add("Customers Near End Date");
        list.add("Old Customers");
        list.add("Future Customers");
        list.add("Reserved Active Customers");
        return ResponseEntity.ok(list);
    }
}
