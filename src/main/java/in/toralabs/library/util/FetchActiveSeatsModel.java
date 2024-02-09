package in.toralabs.library.util;

import java.util.TreeSet;

public class FetchActiveSeatsModel {
    private String timeSlotWithDesc;
    private Integer totalSeats;
    private Integer totalReservedSeats;
    private Integer totalUnreservedSeats;
    private TreeSet<Integer> remainingFreeSeatsInCurrentSlotList;
    private Integer occupiedReservedSeatsInCurrentSlot;

    private TreeSet<Integer> seatsActivelyUsedInOtherSlots;

    public String getTimeSlotWithDesc() {
        return timeSlotWithDesc;
    }

    public void setTimeSlotWithDesc(String timeSlotWithDesc) {
        this.timeSlotWithDesc = timeSlotWithDesc;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getTotalReservedSeats() {
        return totalReservedSeats;
    }

    public void setTotalReservedSeats(Integer totalReservedSeats) {
        this.totalReservedSeats = totalReservedSeats;
    }

    public Integer getTotalUnreservedSeats() {
        return totalUnreservedSeats;
    }

    public void setTotalUnreservedSeats(Integer totalUnreservedSeats) {
        this.totalUnreservedSeats = totalUnreservedSeats;
    }

    public Integer getOccupiedReservedSeatsInCurrentSlot() {
        return occupiedReservedSeatsInCurrentSlot;
    }

    public void setOccupiedReservedSeatsInCurrentSlot(Integer occupiedReservedSeatsInCurrentSlot) {
        this.occupiedReservedSeatsInCurrentSlot = occupiedReservedSeatsInCurrentSlot;
    }

    public TreeSet<Integer> getRemainingFreeSeatsInCurrentSlotList() {
        return remainingFreeSeatsInCurrentSlotList;
    }

    public void setRemainingFreeSeatsInCurrentSlotList(TreeSet<Integer> remainingFreeSeatsInCurrentSlotList) {
        this.remainingFreeSeatsInCurrentSlotList = remainingFreeSeatsInCurrentSlotList;
    }

    public TreeSet<Integer> getSeatsActivelyUsedInOtherSlots() {
        return seatsActivelyUsedInOtherSlots;
    }

    public void setSeatsActivelyUsedInOtherSlots(TreeSet<Integer> seatsActivelyUsedInOtherSlots) {
        this.seatsActivelyUsedInOtherSlots = seatsActivelyUsedInOtherSlots;
    }
}
