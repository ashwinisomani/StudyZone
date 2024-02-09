package in.toralabs.library.jpa.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "lib_async_jobs")
@IdClass(AsyncJobsModelPK.class)
public class AsyncJobsModel {
    private String jobId;
    private Timestamp startDtm;
    private Timestamp endDtm;
    private String status;
    private Long totalTimeInSecs;
    private Long totalMsgs;
    private String jobType;
    private Long successMsgs;
    private Long failedMsgs;

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

    @Basic(optional = true)
    @Column(name = "end_dtm")
    public Timestamp getEndDtm() {
        return endDtm;
    }

    public void setEndDtm(Timestamp endDtm) {
        this.endDtm = endDtm;
    }

    @Basic(optional = false)
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic(optional = true)
    @Column(name = "total_time_in_secs")
    public Long getTotalTimeInSecs() {
        return totalTimeInSecs;
    }

    public void setTotalTimeInSecs(Long totalTimeInSecs) {
        this.totalTimeInSecs = totalTimeInSecs;
    }

    @Basic(optional = false)
    @Column(name = "total_msgs")
    public Long getTotalMsgs() {
        return totalMsgs;
    }

    public void setTotalMsgs(Long totalMsgs) {
        this.totalMsgs = totalMsgs;
    }

    @Basic(optional = false)
    @Column(name = "job_type")
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    @Basic(optional = false)
    @Column(name = "success_msgs")
    public Long getSuccessMsgs() {
        return successMsgs;
    }

    public void setSuccessMsgs(Long successMsgs) {
        this.successMsgs = successMsgs;
    }

    @Basic(optional = false)
    @Column(name = "failed_msgs")
    public Long getFailedMsgs() {
        return failedMsgs;
    }

    public void setFailedMsgs(Long failedMsgs) {
        this.failedMsgs = failedMsgs;
    }
}
