package in.toralabs.library.controllers;

import in.toralabs.library.jpa.repository.AsyncJobsRepository;
import in.toralabs.library.jpa.repository.JobMsgsStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobsController {
    @Autowired
    private AsyncJobsRepository asyncJobsRepository;

    @Autowired
    private JobMsgsStatusRepository jobMsgsStatusRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/getAllAsyncJobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllAsyncJobs() {
        return ResponseEntity.ok(asyncJobsRepository.findAllByOrderByStartDtmDesc());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getJobMsgsFromJobId/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getJobMsgsFromJobId(@PathVariable("jobId") String jobId) {
        return ResponseEntity.ok(jobMsgsStatusRepository.findByJobId(jobId));
    }
}
