package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.AsyncJobsModel;
import in.toralabs.library.jpa.model.AsyncJobsModelPK;
import in.toralabs.library.jpa.model.JobMsgsStatusModel;
import in.toralabs.library.jpa.repository.AsyncJobsRepository;
import in.toralabs.library.jpa.repository.JobMsgsStatusRepository;
import in.toralabs.library.jpa.repository.TimeSlotCostMappingRepository;
import in.toralabs.library.jpa.repository.UserDetailRepository;
import in.toralabs.library.service.WhatsappTemplateService;
import in.toralabs.library.util.NameAndMobileModel;
import in.toralabs.library.util.ResponseModel;
import in.toralabs.library.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class WhatsappTemplateController {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private TimeSlotCostMappingRepository timeSlotCostMappingRepository;

    @Autowired
    private WhatsappTemplateService whatsappTemplateService;

    @Autowired
    private JobMsgsStatusRepository jobMsgsStatusRepository;

    @Autowired
    private AsyncJobsRepository asyncJobsRepository;

    private static final Logger logger = LoggerFactory.getLogger(WhatsappTemplateController.class);


    @Async
    @PostMapping(value = "/sendNotifyTemplate/{var2}/{mediaFileName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendNotifyTemplate(@RequestBody List<NameAndMobileModel> list, @PathVariable("var2") String var2, @PathVariable(value = "mediaFileName") String mediaFileName) {
        long startTime = System.currentTimeMillis();
        var2 = var2.replaceAll("\\|", " ").replaceAll("~", "%").replaceAll("-newLine-", " ");

        System.out.println("var2 is: " + var2);
        System.out.println("mediaFileName is: " + mediaFileName);
        System.out.println("List size is: " + list.size());

        AtomicLong successMsgs = new AtomicLong();
        AtomicLong failedMsgs = new AtomicLong();

        // add an entry to async jobs table to track this job
        AsyncJobsModel asyncJobsModel = new AsyncJobsModel();
        String jobId = UUID.randomUUID().toString();
        Timestamp startDtm = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        asyncJobsModel.setJobId(jobId);
        asyncJobsModel.setStartDtm(startDtm);
        asyncJobsModel.setStatus("In Progress");
        asyncJobsModel.setJobType("Marketing (Official)");
        asyncJobsModel.setTotalMsgs((long) list.size());
        asyncJobsModel.setSuccessMsgs(0L);
        asyncJobsModel.setFailedMsgs(0L);
        asyncJobsRepository.save(asyncJobsModel);

        Vector<JobMsgsStatusModel> jobMsgsStatusModelList = new Vector<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20); // Thread pool with 20 threads

        int batchSize = 20; // Number of threads to execute in each batch
        long delayBetweenBatches = 10000; // Delay between batches in milliseconds (10 seconds)

        for (int i = 0; i < list.size(); i += batchSize) {
            List<NameAndMobileModel> batch = list.subList(i, Math.min(i + batchSize, list.size()));

            for (NameAndMobileModel model : batch) {
                String finalVar = var2;
                executorService.execute(() -> {
                    // Your existing code for sending messages and updating the status
                    logger.info("Thread id is: " + Thread.currentThread().getId() + " name is : " + Thread.currentThread().getName() + " state is: " + Thread.currentThread().getState());
                    ResponseEntity<ResponseModel> entityResponseModel;
                    if (model.getName() == null || "".equals(model.getName().trim())) {
                        entityResponseModel = whatsappTemplateService.sendNotifyMessage(model.getMobileNo().trim(), "customer", finalVar, Utils.MARKETING_MESSAGE_4, mediaFileName,true);
                    } else {
                        entityResponseModel = whatsappTemplateService.sendNotifyMessage(model.getMobileNo().trim(), model.getName().trim(), finalVar, Utils.MARKETING_MESSAGE_4, mediaFileName, true);
                    }
                    JobMsgsStatusModel jobMsgsStatusModel = new JobMsgsStatusModel();
                    jobMsgsStatusModel.setJobId(jobId);
                    jobMsgsStatusModel.setMobileNumber(model.getMobileNo().trim());
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
                System.out.printf("Time taken for sending notify whatsapp messages is %d seconds.\n", timeTaken);
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

    @Async
    @PostMapping(value = "/sendWishesTemplate/{var2}/{mediaFileName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendWishesTemplate(@RequestBody List<NameAndMobileModel> list, @PathVariable("var2") String var2, @PathVariable(value = "mediaFileName") String mediaFileName) {
        long startTime = System.currentTimeMillis();
        var2 = var2.replaceAll("\\|", " ").replaceAll("~", "%").replaceAll("-newLine-", " ");

        System.out.println("var2 is: " + var2);
        System.out.println("mediaFileName is: " + mediaFileName);
        System.out.println("List size is: " + list.size());

        AtomicLong successMsgs = new AtomicLong();
        AtomicLong failedMsgs = new AtomicLong();

        // add an entry to async jobs table to track this job
        AsyncJobsModel asyncJobsModel = new AsyncJobsModel();
        String jobId = UUID.randomUUID().toString();
        Timestamp startDtm = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        asyncJobsModel.setJobId(jobId);
        asyncJobsModel.setStartDtm(startDtm);
        asyncJobsModel.setStatus("In Progress");
        asyncJobsModel.setJobType("Marketing (Official)");
        asyncJobsModel.setTotalMsgs((long) list.size());
        asyncJobsModel.setSuccessMsgs(0L);
        asyncJobsModel.setFailedMsgs(0L);
        asyncJobsRepository.save(asyncJobsModel);

        Vector<JobMsgsStatusModel> jobMsgsStatusModelList = new Vector<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20); // Thread pool with 20 threads

        int batchSize = 20; // Number of threads to execute in each batch
        long delayBetweenBatches = 10000; // Delay between batches in milliseconds (10 seconds)

        for (int i = 0; i < list.size(); i += batchSize) {
            List<NameAndMobileModel> batch = list.subList(i, Math.min(i + batchSize, list.size()));

            for (NameAndMobileModel model : batch) {
                String finalVar = var2;
                executorService.execute(() -> {
                    // Your existing code for sending messages and updating the status
                    logger.info("Thread id is: " + Thread.currentThread().getId() + " name is : " + Thread.currentThread().getName() + " state is: " + Thread.currentThread().getState());
                    ResponseEntity<ResponseModel> entityResponseModel;
                    if (model.getName() == null || "".equals(model.getName().trim())) {
                        entityResponseModel = whatsappTemplateService.sendImageMessage(model.getMobileNo().trim(), "customer", finalVar, Utils.MARKETING_MESSAGE_3, mediaFileName, true);
                    } else {
                        entityResponseModel = whatsappTemplateService.sendImageMessage(model.getMobileNo().trim(), model.getName().trim(), finalVar, Utils.MARKETING_MESSAGE_3, mediaFileName, true);
                    }
                    JobMsgsStatusModel jobMsgsStatusModel = new JobMsgsStatusModel();
                    jobMsgsStatusModel.setJobId(jobId);
                    jobMsgsStatusModel.setMobileNumber(model.getMobileNo().trim());
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
                System.out.printf("Time taken for sending wishes whatsapp messages is %d seconds.\n", timeTaken);
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

    @Async
    @PostMapping(value = "/sendMarketingEnglishTemplateWithVideo/{var2}/{mediaFileName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendMarketingEnglishTemplateWithVideo(@RequestBody List<NameAndMobileModel> list, @PathVariable("var2") String var2, @PathVariable(value = "mediaFileName") String mediaFileName) {
        long startTime = System.currentTimeMillis();
        var2 = var2.replaceAll("\\|", " ").replaceAll("~", "%").replaceAll("-newLine-", " ");

        System.out.println("var2 is: " + var2);
        System.out.println("mediaFileName is: " + mediaFileName);
        System.out.println("List size is: " + list.size());

        AtomicLong successMsgs = new AtomicLong();
        AtomicLong failedMsgs = new AtomicLong();

        // add an entry to async jobs table to track this job
        AsyncJobsModel asyncJobsModel = new AsyncJobsModel();
        String jobId = UUID.randomUUID().toString();
        Timestamp startDtm = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        asyncJobsModel.setJobId(jobId);
        asyncJobsModel.setStartDtm(startDtm);
        asyncJobsModel.setStatus("In Progress");
        asyncJobsModel.setJobType("Marketing (Official)");
        asyncJobsModel.setTotalMsgs((long) list.size());
        asyncJobsModel.setSuccessMsgs(0L);
        asyncJobsModel.setFailedMsgs(0L);
        asyncJobsRepository.save(asyncJobsModel);

        Vector<JobMsgsStatusModel> jobMsgsStatusModelList = new Vector<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20); // Thread pool with 20 threads

        int batchSize = 20; // Number of threads to execute in each batch
        long delayBetweenBatches = 10000; // Delay between batches in milliseconds (10 seconds)

        for (int i = 0; i < list.size(); i += batchSize) {
            List<NameAndMobileModel> batch = list.subList(i, Math.min(i + batchSize, list.size()));

            for (NameAndMobileModel model : batch) {
                String finalVar = var2;
                executorService.execute(() -> {
                    // Your existing code for sending messages and updating the status
                    logger.info("Thread id is: " + Thread.currentThread().getId() + " name is : " + Thread.currentThread().getName() + " state is: " + Thread.currentThread().getState());
                    ResponseEntity<ResponseModel> entityResponseModel;
                    if (model.getName() == null || "".equals(model.getName().trim())) {
                        entityResponseModel = whatsappTemplateService.sendVideoMessage(model.getMobileNo().trim(), "customer", finalVar, Utils.MARKETING_MESSAGE_1, mediaFileName, true);
                    } else {
                        entityResponseModel = whatsappTemplateService.sendVideoMessage(model.getMobileNo().trim(), model.getName().trim(), finalVar, Utils.MARKETING_MESSAGE_1, mediaFileName, true);
                    }
                    JobMsgsStatusModel jobMsgsStatusModel = new JobMsgsStatusModel();
                    jobMsgsStatusModel.setJobId(jobId);
                    jobMsgsStatusModel.setMobileNumber(model.getMobileNo().trim());
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
                System.out.printf("Time taken for sending marketing whatsapp messages in English language with a video, is %d seconds.\n", timeTaken);
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

    @Async
    @PostMapping(value = "/sendMarketingHindiTemplateWithImage/{var2}/{mediaFileName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendMarketingHindiTemplateWithImage(@RequestBody List<NameAndMobileModel> list, @PathVariable("var2") String var2, @PathVariable(value = "mediaFileName") String mediaFileName) {
        long startTime = System.currentTimeMillis();
        var2 = var2.replaceAll("\\|", " ").replaceAll("~", "%").replaceAll("-newLine-", " ");

        System.out.println("var2 is: " + var2);
        System.out.println("mediaFileName is: " + mediaFileName);
        System.out.println("List size is: " + list.size());

        AtomicLong successMsgs = new AtomicLong();
        AtomicLong failedMsgs = new AtomicLong();

        // add an entry to async jobs table to track this job
        AsyncJobsModel asyncJobsModel = new AsyncJobsModel();
        String jobId = UUID.randomUUID().toString();
        Timestamp startDtm = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        asyncJobsModel.setJobId(jobId);
        asyncJobsModel.setStartDtm(startDtm);
        asyncJobsModel.setStatus("In Progress");
        asyncJobsModel.setJobType("Marketing (Official)");
        asyncJobsModel.setTotalMsgs((long) list.size());
        asyncJobsModel.setSuccessMsgs(0L);
        asyncJobsModel.setFailedMsgs(0L);
        asyncJobsRepository.save(asyncJobsModel);

        Vector<JobMsgsStatusModel> jobMsgsStatusModelList = new Vector<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20); // Thread pool with 20 threads

        int batchSize = 20; // Number of threads to execute in each batch
        long delayBetweenBatches = 10000; // Delay between batches in milliseconds (10 seconds)

        for (int i = 0; i < list.size(); i += batchSize) {
            List<NameAndMobileModel> batch = list.subList(i, Math.min(i + batchSize, list.size()));

            for (NameAndMobileModel model : batch) {
                String finalVar = var2;
                executorService.execute(() -> {
                    // Your existing code for sending messages and updating the status
                    logger.info("Thread id is: " + Thread.currentThread().getId() + " name is : " + Thread.currentThread().getName() + " state is: " + Thread.currentThread().getState());
                    ResponseEntity<ResponseModel> entityResponseModel;
                    if (model.getName() == null || "".equals(model.getName().trim())) {
                        entityResponseModel = whatsappTemplateService.sendImageMessage(model.getMobileNo().trim(), "customer", finalVar, Utils.MARKETING_MESSAGE_2, mediaFileName, false);
                    } else {
                        entityResponseModel = whatsappTemplateService.sendImageMessage(model.getMobileNo().trim(), model.getName().trim(), finalVar, Utils.MARKETING_MESSAGE_2, mediaFileName, false);
                    }
                    JobMsgsStatusModel jobMsgsStatusModel = new JobMsgsStatusModel();
                    jobMsgsStatusModel.setJobId(jobId);
                    jobMsgsStatusModel.setMobileNumber(model.getMobileNo().trim());
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
                System.out.printf("Time taken for sending marketing whatsapp messages in Hindi language with a image, is %d seconds.\n", timeTaken);
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
