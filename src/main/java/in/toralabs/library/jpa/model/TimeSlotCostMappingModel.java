package in.toralabs.library.jpa.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lib_time_slot_and_cost_mapping")
public class TimeSlotCostMappingModel {
    private Integer id;
    private String timeSlotTitle;
    private String timeSlotDesc;
    private String timeDurationHrs;

    private Integer cost1DayReserved;
    private Integer cost7DayReserved;
    private Integer cost15DayReserved;

    private Integer cost30DayReserved;
    private Integer cost90DayReserved;

    private Integer cost30DayUnreserved;
    private Integer cost90DayUnreserved;

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lib_time_slot_and_cost_mapping_id_gen")
    @SequenceGenerator(name = "lib_time_slot_and_cost_mapping_id_gen", sequenceName = "lib_time_slot_and_cost_mapping_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Column(name = "time_slot_title")
    public String getTimeSlotTitle() {
        return timeSlotTitle;
    }

    public void setTimeSlotTitle(String timeSlotTitle) {
        this.timeSlotTitle = timeSlotTitle;
    }

    @Basic(optional = false)
    @Column(name = "time_slot_desc")
    public String getTimeSlotDesc() {
        return timeSlotDesc;
    }

    public void setTimeSlotDesc(String timeSlotDesc) {
        this.timeSlotDesc = timeSlotDesc;
    }

    @Basic(optional = false)
    @Column(name = "time_duration_hrs")
    public String getTimeDurationHrs() {
        return timeDurationHrs;
    }

    public void setTimeDurationHrs(String timeDurationHrs) {
        this.timeDurationHrs = timeDurationHrs;
    }

    @Basic
    @Column(name = "cost_1_day_reserved")
    public Integer getCost1DayReserved() {
        return cost1DayReserved;
    }

    public void setCost1DayReserved(Integer cost1DayReserved) {
        this.cost1DayReserved = cost1DayReserved;
    }

    @Basic
    @Column(name = "cost_7_day_reserved")
    public Integer getCost7DayReserved() {
        return cost7DayReserved;
    }

    public void setCost7DayReserved(Integer cost7DayReserved) {
        this.cost7DayReserved = cost7DayReserved;
    }

    @Basic
    @Column(name = "cost_15_day_reserved")
    public Integer getCost15DayReserved() {
        return cost15DayReserved;
    }

    public void setCost15DayReserved(Integer cost15DayReserved) {
        this.cost15DayReserved = cost15DayReserved;
    }

    @Basic
    @Column(name = "cost_30_day_reserved")
    public Integer getCost30DayReserved() {
        return cost30DayReserved;
    }

    public void setCost30DayReserved(Integer cost30DayReserved) {
        this.cost30DayReserved = cost30DayReserved;
    }

    @Basic
    @Column(name = "cost_90_day_reserved")
    public Integer getCost90DayReserved() {
        return cost90DayReserved;
    }

    public void setCost90DayReserved(Integer cost90DayReserved) {
        this.cost90DayReserved = cost90DayReserved;
    }

    @Basic
    @Column(name = "cost_30_day_unreserved")
    public Integer getCost30DayUnreserved() {
        return cost30DayUnreserved;
    }

    public void setCost30DayUnreserved(Integer cost30DayUnreserved) {
        this.cost30DayUnreserved = cost30DayUnreserved;
    }

    @Basic
    @Column(name = "cost_90_day_unreserved")
    public Integer getCost90DayUnreserved() {
        return cost90DayUnreserved;
    }

    public void setCost90DayUnreserved(Integer cost90DayUnreserved) {
        this.cost90DayUnreserved = cost90DayUnreserved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlotCostMappingModel model = (TimeSlotCostMappingModel) o;
        return id.equals(model.id) && timeSlotTitle.equals(model.timeSlotTitle) && timeSlotDesc.equals(model.timeSlotDesc) && timeDurationHrs.equals(model.timeDurationHrs) && Objects.equals(cost1DayReserved, model.cost1DayReserved) && Objects.equals(cost7DayReserved, model.cost7DayReserved) && Objects.equals(cost15DayReserved, model.cost15DayReserved) && cost30DayReserved.equals(model.cost30DayReserved) && cost90DayReserved.equals(model.cost90DayReserved) && cost30DayUnreserved.equals(model.cost30DayUnreserved) && cost90DayUnreserved.equals(model.cost90DayUnreserved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeSlotTitle, timeSlotDesc, timeDurationHrs, cost1DayReserved, cost7DayReserved, cost15DayReserved, cost30DayReserved, cost90DayReserved, cost30DayUnreserved, cost90DayUnreserved);
    }

    @Override
    public String toString() {
        return "TimeSlotCostMappingModel{" +
                "id=" + id +
                ", timeSlotTitle='" + timeSlotTitle + '\'' +
                ", timeSlotDesc='" + timeSlotDesc + '\'' +
                ", timeDurationHrs='" + timeDurationHrs + '\'' +
                ", cost1DayReserved=" + cost1DayReserved +
                ", cost7DayReserved=" + cost7DayReserved +
                ", cost15DayReserved=" + cost15DayReserved +
                ", cost30DayReserved=" + cost30DayReserved +
                ", cost90DayReserved=" + cost90DayReserved +
                ", cost30DayUnreserved=" + cost30DayUnreserved +
                ", cost90DayUnreserved=" + cost90DayUnreserved +
                '}';
    }
}
