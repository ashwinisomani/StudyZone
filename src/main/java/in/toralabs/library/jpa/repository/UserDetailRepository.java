package in.toralabs.library.jpa.repository;

import in.toralabs.library.jpa.model.UserDetailModel;
import in.toralabs.library.jpa.model.UserDetailModelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetailModel, UserDetailModelPK> {

    // do not use overlaps in any query. as it does not work in native query using jpa.

    List<UserDetailModel> findAllByOrderByExitDateAsc();

    List<UserDetailModel> findAllByOrderByRecordCreationDateDesc();

    @Query(value = "select * from lib_customer_detail where time_slot_booked_temp = ?1 and current_date >= entry_date and current_date <= exit_date order by exit_date asc", nativeQuery = true)
    List<UserDetailModel> findAllActiveCustomerInTimeSlot(@Param("time_slot") String timeSlot);

    @Query(value = "select * from lib_customer_detail where time_slot_booked_temp in ?1 and current_date >= entry_date and current_date <= exit_date order by exit_date asc", nativeQuery = true)
    List<UserDetailModel> findActiveCustomersRightNow(@Param("time_slots") List<String> timeSlots);

    @Query(value = "select count(*) from lib_customer_detail where mobile_no = ?1 and exit_date >= date(now()) and entry_date <= date(now())", nativeQuery = true)
    Integer checkIfCustomerIsActive(@Param("mobile_number") String mobileNumber);

    @Query(value = "select * from lib_info.lib_customer_detail where time_slot_booked_temp = ?1 and has_booked_reserved = true and entry_date <= ?3 AND exit_date >= ?2", nativeQuery = true)
    List<UserDetailModel> fetchAllReservedWithDateOverlapping(@Param("time_slot") String timeSlot, @Param("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @Param("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);

    @Query(value = "select * from lib_customer_detail where time_slot_booked_temp != ?1 and has_booked_reserved = true and " +
            "(entry_date, exit_date) overlaps (?2, ?3) = true", nativeQuery = true)
    List<UserDetailModel> fetchAllReservedWithDateOverlappingInOtherSlots(@Param("time_slot") String timeSlot, @Param("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @Param("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);

    @Query(value = "select * from lib_customer_detail where time_slot_booked_temp = ?1 and has_booked_reserved = false and " +
            "(entry_date, exit_date) overlaps (?2, ?3) = true", nativeQuery = true)
    List<UserDetailModel> fetchAllActiveUnreservedSeats(@Param("time_slot") String timeSlot, @Param("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @Param("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);

    @Query(value = "select * from lib_customer_detail where date(now()) + 10 > exit_date and date(now()) <= exit_date order by exit_date asc", nativeQuery = true)
    List<UserDetailModel> fetchNearEndDateCustomers();

    @Query(value = "select * from lib_customer_detail where exit_date < date(now()) order by exit_date desc", nativeQuery = true)
    List<UserDetailModel> fetchOldCustomers();

    @Query(value = "select * from lib_customer_detail where entry_date > date(now()) order by entry_date asc", nativeQuery = true)
    List<UserDetailModel> fetchFutureCustomers();

    @Query(value = "select * from lib_customer_detail where current_date >= entry_date and current_date <= exit_date order by exit_date asc", nativeQuery = true)
    List<UserDetailModel> findAllActiveCustomers();

    @Query(value = "select * from lib_customer_detail where current_date >= entry_date and current_date <= exit_date and has_booked_reserved = true order by booked_seat_number asc, exit_date asc", nativeQuery = true)
    List<UserDetailModel> findAllActiveReservedCustomers();

    @Query(value = "select * from lib_customer_detail where current_date >= entry_date and current_date <= exit_date and has_booked_reserved = false order by booked_seat_number asc, exit_date asc", nativeQuery = true)
    List<UserDetailModel> findAllActiveUnreservedCustomers();

    @Query(value = "select coalesce(time_slot_booked_temp, 'Total') as timeSlot, count(*) as customerCount, coalesce(sum(cost), 0) as amount, coalesce(sum(discount), 0) as deduction, coalesce(sum(final_cost), 0) as totalAmount from lib_customer_detail where record_created_date between ?1 and ?2 group by rollup(time_slot_booked_temp)", nativeQuery = true)
    List<Map<String, Object>> fetchSummaryInGivenPeriod(@Param("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @Param("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);
}