package in.toralabs.library.jpa.model;

public class SummaryModel {
    private String timeSlot;
    private Integer customerCount;
    private Integer amount;
    private Integer deduction;
    private Integer totalAmount;

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getDeduction() {
        return deduction;
    }

    public void setDeduction(Integer deduction) {
        this.deduction = deduction;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "SummaryModel{" +
                "timeSlot=" + timeSlot +
                ", customerCount=" + customerCount +
                ", amount=" + amount +
                ", deduction=" + deduction +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
