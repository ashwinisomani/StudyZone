package in.toralabs.library.jpa.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

public class AsyncJobsModelPK implements Serializable {
    private String jobId;
    private Timestamp startDtm;

    @Id
    @Column(name = "job_id")
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Id
    @Column(name = "start_dtm")
    public Timestamp getStartDtm() {
        return startDtm;
    }

    public void setStartDtm(Timestamp startDtm) {
        this.startDtm = startDtm;
    }
}
