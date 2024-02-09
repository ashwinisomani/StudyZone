package in.toralabs.library.jpa.model;

import javax.persistence.*;

@Entity
@Table(name = "lib_job_msgs_status")
public class JobMsgsStatusModel {
    private Long id;
    private String jobId;
    private String mobileNumber;
    private String msgStatus;

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lib_job_msgs_status_id_gen")
    @SequenceGenerator(name = "lib_job_msgs_status_id_gen", sequenceName = "lib_job_msgs_status_id_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Column(name = "job_id")
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Basic(optional = false)
    @Column(name = "mobile_number")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Basic(optional = false)
    @Column(name = "msg_status")
    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }
}
