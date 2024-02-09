package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.TimeSlotCostMappingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotCostMappingRepository extends JpaRepository<TimeSlotCostMappingModel, Integer> {

    @Query(value = "select time_duration_hrs from lib_time_slot_and_cost_mapping where time_slot_title = ?1", nativeQuery = true)
    public String getTimeDurationHours(@Param("time_slot") String timeSlot);

    @Query(value = "select time_slot_desc from lib_time_slot_and_cost_mapping where time_slot_title = ?1", nativeQuery = true)
    public String getTimeSlotDescription(@Param("time_slot") String timeSlot);

    List<TimeSlotCostMappingModel> findAllByOrderByIdAsc();
}
