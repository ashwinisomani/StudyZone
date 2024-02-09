package in.toralabs.library.jpa.model;

public class SaveCustomerDataResponseModel {
    private Integer allottedSeatNo;

    private String mobileNumber;

    private boolean hasBookedReserved;
    private String status;

    private String transactionId;

    private Long entryTimeInLong;

    private String name;

    private String startDate;

    private String endDate;

    private String slotDetails;

    public Integer getAllottedSeatNo() {
        return allottedSeatNo;
    }

    public void setAllottedSeatNo(Integer allottedSeatNo) {
        this.allottedSeatNo = allottedSeatNo;
    }

    public boolean isHasBookedReserved() {
        return hasBookedReserved;
    }

    public void setHasBookedReserved(boolean hasBookedReserved) {
        this.hasBookedReserved = hasBookedReserved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getEntryTimeInLong() {
        return entryTimeInLong;
    }

    public void setEntryTimeInLong(Long entryTimeInLong) {
        this.entryTimeInLong = entryTimeInLong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSlotDetails() {
        return slotDetails;
    }

    public void setSlotDetails(String slotDetails) {
        this.slotDetails = slotDetails;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return "SaveCustomerDataResponseModel{" +
                "allottedSeatNo=" + allottedSeatNo +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", hasBookedReserved=" + hasBookedReserved +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", entryTimeInLong=" + entryTimeInLong +
                ", name='" + name + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", slotDetails='" + slotDetails + '\'' +
                '}';
    }
}
