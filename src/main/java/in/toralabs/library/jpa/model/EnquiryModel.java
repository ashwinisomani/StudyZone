package in.toralabs.library.jpa.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lib_enquiry")
public class EnquiryModel {
    private String name;
    private String mobileNo;
    private String whatsappNo;
    private String address;
    private String slotDetails;
    private String remark1;
    private String remark2;
    private String remark3;

    private Long id;

    private Date creationDate;

    @Basic(optional = true)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic(optional = false)
    @Column(name = "mobile_no")
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @Basic(optional = false)
    @Column(name = "whatsapp_no")
    public String getWhatsappNo() {
        return whatsappNo;
    }

    public void setWhatsappNo(String whatsappNo) {
        this.whatsappNo = whatsappNo;
    }

    @Basic(optional = true)
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic(optional = true)
    @Column(name = "slot_details")
    public String getSlotDetails() {
        return slotDetails;
    }

    public void setSlotDetails(String slotDetails) {
        this.slotDetails = slotDetails;
    }

    @Basic(optional = true)
    @Column(name = "remark_1")
    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    @Basic(optional = true)
    @Column(name = "remark_2")
    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    @Basic(optional = true)
    @Column(name = "remark_3")
    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lib_enquiry_id_gen")
    @SequenceGenerator(name = "lib_enquiry_id_gen", sequenceName = "lib_enquiry_id_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Column(name = "creation_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
