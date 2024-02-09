package in.toralabs.library.jpa.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class UserDetailModelPK implements Serializable {

    private String mobileNumber;

    private Long entryTimeInLong;

    @Id
    @Column(name = "mobile_no")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Id
    @Column(name = "entry_time_in_long")
    public Long getEntryTimeInLong() {
        return entryTimeInLong;
    }

    public void setEntryTimeInLong(Long entryTimeInLong) {
        this.entryTimeInLong = entryTimeInLong;
    }
}
