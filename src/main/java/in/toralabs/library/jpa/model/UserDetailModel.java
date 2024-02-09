package in.toralabs.library.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "lib_customer_detail")
@IdClass(UserDetailModelPK.class)
public class UserDetailModel {
    private String name;
    private String mobileNumber;
    private String emailId;
    private String examName;
    @JsonIgnore
    private Date entryDate;
    private Integer durationInDays;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date exitDate;
    private String aadharCardNumber;
    private String idProofUrl;
    private String customerPhotoUrl;
    private String paymentProofUrl;
    private boolean hasBookedReservedSeat;
    private Integer bookedSeatNumber = -1;

    private String timeSlotBookedMain;
    private String timeSlotBookedTemp;

    private Integer cost;
    private Integer discount;
    private Integer finalCost;

    private String gender;

    private Long transactionId;

    private Timestamp modificationDtm;

    private Long entryTimeInLong;

    private String otherImagesUrl;

    private String modeOfTransport;

    @JsonIgnore
    private Date recordCreationDate;

    // A is for adding a customer, U is for updating a customer
    @Transient
    private String actionStatus = "A";

    @Basic(optional = true)
    @Column(name = "aadhar_card_no")
    public String getAadharCardNumber() {
        return aadharCardNumber;
    }

    public void setAadharCardNumber(String aadharCardNumber) {
        this.aadharCardNumber = aadharCardNumber;
    }

    @Basic(optional = false)
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "mobile_no")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Basic(optional = true)
    @Column(name = "email_id")
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Basic(optional = true)
    @Column(name = "exam_name")
    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    @Basic(optional = false)
    @Column(name = "entry_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Date getEntryDate() {
        return entryDate;
    }

    @JsonIgnore
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    @Basic(optional = false)
    @Column(name = "exit_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Date getExitDate() {
        return exitDate;
    }

    @JsonIgnore
    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
    }

    @Basic(optional = true)
    @Column(name = "id_proof_url")
    public String getIdProofUrl() {
        return idProofUrl;
    }

    public void setIdProofUrl(String idProofUrl) {
        this.idProofUrl = idProofUrl;
    }

    @Basic(optional = true)
    @Column(name = "customer_photo_url")
    public String getCustomerPhotoUrl() {
        return customerPhotoUrl;
    }

    public void setCustomerPhotoUrl(String customerPhotoUrl) {
        this.customerPhotoUrl = customerPhotoUrl;
    }

    @Basic(optional = true)
    @Column(name = "payment_proof_url")
    public String getPaymentProofUrl() {
        return paymentProofUrl;
    }

    public void setPaymentProofUrl(String paymentProofUrl) {
        this.paymentProofUrl = paymentProofUrl;
    }

    @Basic
    @Column(name = "has_booked_reserved")
    public boolean isHasBookedReservedSeat() {
        return hasBookedReservedSeat;
    }

    public void setHasBookedReservedSeat(boolean hasBookedReservedSeat) {
        this.hasBookedReservedSeat = hasBookedReservedSeat;
    }

    @Basic(optional = false)
    @Column(name = "booked_seat_number")
    public Integer getBookedSeatNumber() {
        return bookedSeatNumber;
    }

    public void setBookedSeatNumber(Integer bookedSeatNumber) {
        this.bookedSeatNumber = bookedSeatNumber;
    }

    @Basic(optional = true)
    @Column(name = "time_slot_booked_main")
    public String getTimeSlotBookedMain() {
        return timeSlotBookedMain;
    }

    public void setTimeSlotBookedMain(String timeSlotBookedMain) {
        this.timeSlotBookedMain = timeSlotBookedMain;
    }

    @Basic(optional = false)
    @Column(name = "time_slot_booked_temp")
    public String getTimeSlotBookedTemp() {
        return timeSlotBookedTemp;
    }

    public void setTimeSlotBookedTemp(String timeSlotBookedTemp) {
        this.timeSlotBookedTemp = timeSlotBookedTemp;
    }

    @Basic(optional = false)
    @Column(name = "cost")
    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Basic(optional = false)
    @Column(name = "discount")
    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    @Basic(optional = false)
    @Column(name = "final_cost")
    public Integer getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(Integer finalCost) {
        this.finalCost = finalCost;
    }

    @Basic(optional = false)
    @Column(name = "gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Generated(value = GenerationTime.INSERT)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lib_customer_detail_transaction_id_gen")
    @SequenceGenerator(name = "lib_customer_detail_transaction_id_gen", sequenceName = "lib_customer_detail_transaction_id_seq", allocationSize = 1)
    @Column(name = "transaction_id", insertable = false, updatable = false)
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    @Basic(optional = false)
    @Column(name = "duration_in_days")
    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    @Basic(optional = false)
    @Column(name = "modification_dtm")
    public Timestamp getModificationDtm() {
        return modificationDtm;
    }

    public void setModificationDtm(Timestamp modificationDtm) {
        this.modificationDtm = modificationDtm;
    }

    @Id
    @Column(name = "entry_time_in_long")
    public Long getEntryTimeInLong() {
        return entryTimeInLong;
    }

    public void setEntryTimeInLong(Long entryTimeInLong) {
        this.entryTimeInLong = entryTimeInLong;
    }

    @Basic
    @Column(name = "mode_of_transport")
    public String getModeOfTransport() {
        return modeOfTransport;
    }

    public void setModeOfTransport(String modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
    }

    @Basic
    @Column(name = "other_images_url")
    public String getOtherImagesUrl() {
        return otherImagesUrl;
    }

    public void setOtherImagesUrl(String otherImagesUrl) {
        this.otherImagesUrl = otherImagesUrl;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }

    @Basic(optional = false)
    @Column(name = "record_created_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Date getRecordCreationDate() {
        return recordCreationDate;
    }

    @JsonIgnore
    public void setRecordCreationDate(Date recordCreationDate) {
        this.recordCreationDate = recordCreationDate;
    }


}
