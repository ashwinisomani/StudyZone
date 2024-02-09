package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.*;
import in.toralabs.library.jpa.repository.*;
import in.toralabs.library.service.CommonService;
import in.toralabs.library.util.AppVersionModel;
import in.toralabs.library.util.FetchActiveSeatsModel;
import in.toralabs.library.util.ResponseModel;
import in.toralabs.library.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class CommonController {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private TimeSlotCostMappingRepository timeSlotCostMappingRepository;

    @Autowired
    private SeatsInfoRepository seatsInfoRepository;

    @Autowired
    private CommonService commonService;

    @Value("${project.receipts}")
    private String rootPathForReceipts;

    @Value("${project.promotionVideos}")
    private String rootPathForVideos;

    @Value("${project.reports}")
    private String rootPathForReports;

    @Value("${project.promotionImages}")
    private String rootPathForPromotionImages;

    @Value("${project.apks}")
    private String rootPathForApks;

    @Autowired
    private JobMsgsStatusRepository jobMsgsStatusRepository;

    @Autowired
    private AsyncJobsRepository asyncJobsRepository;

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    private final ResponseModel responseModel = new ResponseModel();

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value = "/generateSummary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generateSummary(@RequestParam(value = "startDate", required = false) String startDate,
                                                  @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            List<Map<String, Object>> mapList = userDetailRepository.fetchSummaryInGivenPeriod(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate));
            List<SummaryModel> summaryModelList = new ArrayList<>();
            if (mapList.size() > 1) {
                for (Map<String, Object> stringObjectMap : mapList) {
                    SummaryModel summaryModel = new SummaryModel();
                    summaryModel.setTimeSlot((String) stringObjectMap.get("timeslot"));
                    System.out.println(summaryModel.getTimeSlot());
                    summaryModel.setCustomerCount(Integer.parseInt(String.valueOf(((BigInteger) stringObjectMap.get("customercount")).intValue())));
                    System.out.println(summaryModel.getCustomerCount());
                    summaryModel.setAmount(Integer.parseInt(String.valueOf(((BigInteger) stringObjectMap.get("amount")).intValue())));
                    System.out.println(summaryModel.getAmount());
                    summaryModel.setDeduction(Integer.parseInt(String.valueOf(((BigInteger) stringObjectMap.get("deduction")).intValue())));
                    summaryModel.setTotalAmount(Integer.parseInt(String.valueOf(((BigInteger) stringObjectMap.get("totalamount")).intValue())));
                    summaryModelList.add(summaryModel);
                }
                return ResponseEntity.status(HttpStatus.OK).body(summaryModelList);
            } else {
                responseModel.setStatus("No summary report found for this period");
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Failed to generate summary.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/availableSeats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> availableSeats(@RequestParam(value = "timeSlot") String timeSlot,
                                                 @RequestParam("isReserved") boolean isReserved,
                                                 @RequestParam(value = "entryDate") String entryDate,
                                                 @RequestParam(value = "exitDate") String exitDate) {
        try {
            FetchActiveSeatsModel fetchActiveSeatsModel = new FetchActiveSeatsModel();
            List<Integer> timeList = Utils.getIntegerListFromStringArray(
                    timeSlotCostMappingRepository.getTimeDurationHours(timeSlot).split(","));

            Integer startTime = timeList.get(0);
            Integer endTime = timeList.get(timeList.size() - 1);

            HashMap<String, TreeSet<Integer>> timeCostMap = new HashMap<>();
            HashMap<String, String> timeSlotDescMap = new HashMap<>();
            List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAll();
            for (TimeSlotCostMappingModel model : timeSlotCostMappingModelList) {
                timeCostMap.put(model.getTimeSlotTitle(),
                        new TreeSet<>(Utils.getIntegerListFromStringArray(model.getTimeDurationHrs().split(","))));
                timeSlotDescMap.put(model.getTimeSlotTitle(), model.getTimeSlotDesc());
            }

            SeatsInfoModel seatsInfoModel = seatsInfoRepository.findAll().get(0);
            Integer totalSeatsCount = seatsInfoModel.getTotalSeats();
            Integer reservedSeatsCount = seatsInfoModel.getReservedSeats();
            Integer unreservedSeatsCount = seatsInfoModel.getUnReservedSeats();

            fetchActiveSeatsModel.setTotalSeats(totalSeatsCount);
            fetchActiveSeatsModel.setTotalReservedSeats(reservedSeatsCount);
            fetchActiveSeatsModel.setTotalUnreservedSeats(unreservedSeatsCount);
            fetchActiveSeatsModel.setTimeSlotWithDesc(timeSlot + " (" + timeSlotDescMap.get(timeSlot) + ")");

            TreeSet<Integer> seatsActivelyUsedInOtherSlotsSet = new TreeSet<>();
            TreeSet<Integer> allSeatsActivelyUsedInOtherSlotsSet = new TreeSet<>();
            TreeSet<Integer> activeCustomersInCurrentSlotSet = new TreeSet<>();
            TreeSet<Integer> allSeatsSet = new TreeSet<>(); // from 1 to totalSeatCount
            for (int i = 1; i <= totalSeatsCount; i++) {
                allSeatsSet.add(i);
            }

            userDetailRepository.fetchAllReservedWithDateOverlapping(timeSlot, simpleDateFormat.parse(entryDate), simpleDateFormat.parse(exitDate)).forEach(model -> {
                activeCustomersInCurrentSlotSet.add(model.getBookedSeatNumber());
//                System.out.println("activeCustomersInCurrentSlotSet " + model.getBookedSeatNumber());
            });

//            System.out.println("activeCustomersInCurrentSlotSet size " + activeCustomersInCurrentSlotSet.size());

            userDetailRepository.fetchAllReservedWithDateOverlappingInOtherSlots(timeSlot, simpleDateFormat.parse(entryDate), simpleDateFormat.parse(exitDate)).forEach(model -> {
                seatsActivelyUsedInOtherSlotsSet.add(model.getBookedSeatNumber());
                TreeSet<Integer> set1 = timeCostMap.get(model.getTimeSlotBookedTemp());   // B, C
                TreeSet<Integer> set2 = timeCostMap.get(timeSlot); // D
                boolean modified = false;
                if (set1.size() > set2.size()) {
                    for (Integer i : set1) {
                        if (set2.contains(i)) {
                            modified = true;
                            break;
                        }
                    }
                } else {
                    for (Integer i : set2) {
                        if (set1.contains(i)) {
                            modified = true;
                            break;
                        }
                    }
                }
                if (!modified) {
                    seatsActivelyUsedInOtherSlotsSet.add(model.getBookedSeatNumber()); // C -> 1
                } else {
                    allSeatsActivelyUsedInOtherSlotsSet.add(model.getBookedSeatNumber()); // B -> 1
                }
            });
            if (isReserved) {
                allSeatsSet.removeAll(allSeatsActivelyUsedInOtherSlotsSet);
                seatsActivelyUsedInOtherSlotsSet.removeAll(allSeatsActivelyUsedInOtherSlotsSet);

                allSeatsSet.removeAll(activeCustomersInCurrentSlotSet);
                allSeatsSet.removeAll(seatsActivelyUsedInOtherSlotsSet);
                fetchActiveSeatsModel.setOccupiedReservedSeatsInCurrentSlot(activeCustomersInCurrentSlotSet.size());
                fetchActiveSeatsModel.setRemainingFreeSeatsInCurrentSlotList(allSeatsSet);
                fetchActiveSeatsModel.setSeatsActivelyUsedInOtherSlots(seatsActivelyUsedInOtherSlotsSet);
                return ResponseEntity.status(HttpStatus.OK).body(fetchActiveSeatsModel);
            } else {
                List<UserDetailModel> userDetailModelList = userDetailRepository.fetchAllActiveUnreservedSeats(timeSlot, simpleDateFormat.parse(entryDate), simpleDateFormat.parse(exitDate));
                responseModel.setStatus("Unreserved Seats Count:" + unreservedSeatsCount + ",Occupied Count:" + userDetailModelList.size());
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Failed to fetch available seats.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/generateReport")
    public ResponseEntity<Object> generateReport() {
        try {
            File dir = new File(rootPathForReports);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String reportName = System.currentTimeMillis() + ".csv";
            File file = new File(rootPathForReports + File.separator + reportName);
            PrintWriter pw = new PrintWriter(file);
            pw.write(commonService.reportOfAllCustomers());
            pw.close();
            InputStream resource = new FileInputStream(rootPathForReports + File.separator + reportName);
            file.delete();
            MediaType mediaType = MediaType.parseMediaType("text/csv");
            return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).
                    header("Content-disposition", "attachment; filename=" + reportName)
                    .body(resource.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + e.getCause());
            responseModel.setStatus("Unable to generate the report.");
            responseModel.setExceptionMessage(e.getMessage() + " " + e.getCause());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/generateReceiptPdf/{encodedMobileNumber}/{entryTimeInLong}")
    public ResponseEntity<Object> generateReceiptPdf(@PathVariable("encodedMobileNumber") String
                                                             encodedMobileNumber, @PathVariable("entryTimeInLong") Long entryTimeInLong) {
        String mobileNumber = commonService.decodeMobileNumber(encodedMobileNumber);
        UserDetailModelPK userDetailModelPK = new UserDetailModelPK();
        userDetailModelPK.setEntryTimeInLong(entryTimeInLong);
        userDetailModelPK.setMobileNumber(mobileNumber);
        Optional<UserDetailModel> userDetailModel = userDetailRepository.findById(userDetailModelPK);
        if (userDetailModel.isEmpty()) {
            responseModel.setStatus("Not a valid mobile number");
            responseModel.setExceptionMessage("");
            return ResponseEntity.ok().body(responseModel);
        } else {
            try {
                commonService.generatePdf(userDetailModel.get(), rootPathForReceipts);
                InputStream resource = new FileInputStream(rootPathForReceipts + File.separator + userDetailModel.get().getTransactionId() + ".pdf");
                File file = new File(rootPathForReceipts + File.separator + userDetailModel.get().getTransactionId() + ".pdf");
                file.delete();
                MediaType mediaType = MediaType.APPLICATION_PDF;
                return ResponseEntity.status(HttpStatus.OK).
                        contentType(mediaType).
                        header("Content-disposition", "attachment; filename=" + userDetailModel.get().getTransactionId() + ".pdf").
                        body(resource.readAllBytes());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                responseModel.setStatus("Error generating PDF receipt for transaction id " + userDetailModel.get().getTransactionId());
                responseModel.setExceptionMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
            }
        }
    }

    @GetMapping(value = "/generatePromotionVideos/{videoName}")
    public ResponseEntity<Object> generatePromotionVideos(@PathVariable("videoName") String videoName) {
        // videos must be in mp4 format only.
        try {
            InputStream resource = new FileInputStream(rootPathForVideos + File.separator + videoName);
            MediaType mediaType = MediaType.parseMediaType("video/mp4");
            return ResponseEntity.status(HttpStatus.OK).
                    contentType(mediaType).
                    header("Content-disposition", "attachment; filename=" + videoName + ".mp4").
                    body(resource.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Error fetching the promotion video with file name as " + videoName + ".mp4");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/generatePromotionImages/{imageName}")
    public ResponseEntity<Object> generatePromotionImages(@PathVariable("imageName") String imageName) {
        try {
            // image name must come with extension from frontend (.jpg, .jpeg or .png supported)
            InputStream resource = new FileInputStream(rootPathForPromotionImages + File.separator + imageName);
            String[] extension = imageName.split("\\.");
            MediaType mediaType = MediaType.parseMediaType("image/" + extension[extension.length - 1]);
            return ResponseEntity.status(HttpStatus.OK).
                    contentType(mediaType).
                    header("Content-disposition", "attachment; filename=" + imageName).
                    body(resource.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Error fetching the promotion image with file name as " + imageName);
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/getAppVersion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAppVersion() {
        // to stop the apk to use this backend, pass value as -1 for appVersion
        AppVersionModel appVersionModel = new AppVersionModel(41);
        return ResponseEntity.ok(appVersionModel);
    }

    @GetMapping(value = "/getLatestApk")
    public ResponseEntity<Object> getLatestApk() {
        try {
            InputStream resource = new FileInputStream(rootPathForApks + File.separator + "balajee-library.apk");
            MediaType mediaType = MediaType.parseMediaType("application/vnd.android.package-archive");
            return ResponseEntity.status(HttpStatus.OK).
                    contentType(mediaType).
                    header("Content-disposition", "attachment; filename=" + "balajee-library.apk").
                    body(resource.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Error fetching the latest APK.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/generateCustomizedReport")
    public ResponseEntity<Object> generateCustomizedReport() {
        try {
            File dir = new File(rootPathForReports);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String reportName = System.currentTimeMillis() + ".csv";
            File file = new File(rootPathForReports + File.separator + reportName);
            PrintWriter pw = new PrintWriter(file);
            pw.write(commonService.getCustomizedReport());
            pw.close();
            InputStream resource = new FileInputStream(rootPathForReports + File.separator + reportName);
            MediaType mediaType = MediaType.parseMediaType("text/csv");
            file.delete();
            return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).
                    header("Content-disposition", "attachment; filename=" + reportName)
                    .body(resource.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + e.getCause());
            responseModel.setStatus("Unable to generate the report.");
            responseModel.setExceptionMessage(e.getMessage() + " " + e.getCause());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @PostMapping(value = "/sendSingleReceiptOnWhatsapp/{mobileNo}/{entryTimeInLong}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> sendSingleReceiptOnWhatsapp(@PathVariable("mobileNo") String
                                                                      mobileNo, @PathVariable("entryTimeInLong") String entryTimeInLong) {
        long startTime = System.currentTimeMillis();

        List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAllByOrderByIdAsc();
        HashMap<String, String> timeSlotDescMap = new HashMap<>();
        for (TimeSlotCostMappingModel model : timeSlotCostMappingModelList) {
            timeSlotDescMap.put(model.getTimeSlotTitle(), model.getTimeSlotDesc());
        }
        AtomicBoolean status = new AtomicBoolean(false);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            ResponseEntity<ResponseModel> entityResponseModel = commonService.sendMessage(mobileNo, Long.parseLong(entryTimeInLong), timeSlotDescMap);
            if (entityResponseModel.getBody().getStatus().equals("Message sent successfully")) {
                status.set(true);
            } else {
                status.set(false);
            }
        });
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
                System.out.printf("Time taken for sending whatsapp messages is %d seconds.\n", timeTaken);
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException called: \n" + e.getCause() + "\n" + e.getMessage() + "\n");
        }
        if (status.get()) {
            return ResponseEntity.status(HttpStatus.OK).body("Receipt shared successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Some error occurred. Unable to share the receipt.");
        }
    }

    @Async
    @PostMapping(value = "/sendReceiptOnWhatsapp", produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendReceiptOnWhatsapp(@RequestBody List<String> commaSeparatedMobileNumberAndEntryTimeList) {
        long startTime = System.currentTimeMillis();
        AtomicLong successMsgs = new AtomicLong();
        AtomicLong failedMsgs = new AtomicLong();

        List<TimeSlotCostMappingModel> timeSlotCostMappingModelList = timeSlotCostMappingRepository.findAllByOrderByIdAsc();

        HashMap<String, String> timeSlotDescMap = new HashMap<>();
        for (TimeSlotCostMappingModel model : timeSlotCostMappingModelList) {
            timeSlotDescMap.put(model.getTimeSlotTitle(), model.getTimeSlotDesc());
        }

        // add an entry to async jobs table to track this job
        AsyncJobsModel asyncJobsModel = new AsyncJobsModel();
        String jobId = UUID.randomUUID().toString();
        Timestamp startDtm = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        asyncJobsModel.setJobId(jobId);
        asyncJobsModel.setStartDtm(startDtm);
        asyncJobsModel.setStatus("In Progress");
        asyncJobsModel.setJobType("Receipt");
        asyncJobsModel.setTotalMsgs((long) commaSeparatedMobileNumberAndEntryTimeList.size());
        asyncJobsModel.setSuccessMsgs(0L);
        asyncJobsModel.setFailedMsgs(0L);
        asyncJobsRepository.save(asyncJobsModel);

        Vector<JobMsgsStatusModel> jobMsgsStatusModelList = new Vector<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20); // Thread pool with 20 threads

        int batchSize = 20; // Number of threads to execute in each batch
        long delayBetweenBatches = 10000; // Delay between batches in milliseconds (10 seconds)

        for (int i = 0; i < commaSeparatedMobileNumberAndEntryTimeList.size(); i += batchSize) {
            List<String> batch = commaSeparatedMobileNumberAndEntryTimeList.subList(i, Math.min(i + batchSize, commaSeparatedMobileNumberAndEntryTimeList.size()));

            for (String s : batch) {
                String[] arr = s.split(",");
                executorService.execute(() -> {
                    // Your existing code for sending messages and updating the status
                    ResponseEntity<ResponseModel> entityResponseModel = commonService.sendMessage(arr[0], Long.parseLong(arr[1]), timeSlotDescMap);
                    JobMsgsStatusModel jobMsgsStatusModel = new JobMsgsStatusModel();
                    jobMsgsStatusModel.setJobId(jobId);
                    jobMsgsStatusModel.setMobileNumber(arr[0].trim());
                    if (entityResponseModel.getBody().getStatus().equals("Message sent successfully")) {
                        jobMsgsStatusModel.setMsgStatus("Success");
                        successMsgs.incrementAndGet();
                    } else {
                        jobMsgsStatusModel.setMsgStatus("Failed");
                        failedMsgs.incrementAndGet();
                    }
                    jobMsgsStatusModelList.add(jobMsgsStatusModel);
                });
            }

            try {
                Thread.sleep(delayBetweenBatches); // Wait for the specified delay
            } catch (InterruptedException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(30, TimeUnit.MINUTES)) {
                long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
                jobMsgsStatusRepository.saveAll(jobMsgsStatusModelList);
                AsyncJobsModelPK asyncJobsModelPK = new AsyncJobsModelPK();
                asyncJobsModelPK.setJobId(jobId);
                asyncJobsModelPK.setStartDtm(startDtm);
                Optional<AsyncJobsModel> jobModel = asyncJobsRepository.findById(asyncJobsModelPK);
                if (jobModel.isPresent()) {
                    asyncJobsModel = jobModel.get();
                    asyncJobsModel.setStatus("Completed");
                    asyncJobsModel.setEndDtm(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
                    asyncJobsModel.setStartDtm(startDtm);
                    asyncJobsModel.setJobId(jobId);
                    asyncJobsModel.setTotalTimeInSecs(timeTaken);
                    asyncJobsModel.setSuccessMsgs(successMsgs.get());
                    asyncJobsModel.setFailedMsgs(failedMsgs.get());
                    asyncJobsRepository.save(asyncJobsModel);
                }
                System.out.printf("Time taken for sending whatsapp receipts is %d seconds.\n", timeTaken);
            } else {
                throw new Exception("Task terminated as it took more than 30 minutes");
            }
        } catch (Exception e) {
            long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
            jobMsgsStatusRepository.saveAll(jobMsgsStatusModelList);
            AsyncJobsModelPK asyncJobsModelPK = new AsyncJobsModelPK();
            asyncJobsModelPK.setJobId(jobId);
            asyncJobsModelPK.setStartDtm(startDtm);
            Optional<AsyncJobsModel> jobModel = asyncJobsRepository.findById(asyncJobsModelPK);
            if (jobModel.isPresent()) {
                asyncJobsModel = jobModel.get();
                asyncJobsModel.setStatus("Completed");
                asyncJobsModel.setEndDtm(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
                asyncJobsModel.setStartDtm(startDtm);
                asyncJobsModel.setJobId(jobId);
                asyncJobsModel.setTotalTimeInSecs(timeTaken);
                asyncJobsModel.setSuccessMsgs(successMsgs.get());
                asyncJobsModel.setFailedMsgs(failedMsgs.get());
                asyncJobsRepository.save(asyncJobsModel);
            }
            System.out.println("InterruptedException called: \n" + e.getCause() + "\n" + e.getMessage() + "\n");
        }
    }
}