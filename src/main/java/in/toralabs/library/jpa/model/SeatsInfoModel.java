package in.toralabs.library.jpa.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lib_seats_info")
public class SeatsInfoModel {
    private Integer id;
    private Integer totalSeats;
    private Integer reservedSeats;
    private Integer unReservedSeats;

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lib_seats_info_id_gen")
    @SequenceGenerator(name = "lib_seats_info_id_gen", sequenceName = "lib_seats_info_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Column(name = "total_seats")
    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    @Basic(optional = false)
    @Column(name = "reserved_seats")
    public Integer getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(Integer reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    @Basic(optional = false)
    @Column(name = "unreserved_seats")
    public Integer getUnReservedSeats() {
        return unReservedSeats;
    }

    public void setUnReservedSeats(Integer unReservedSeats) {
        this.unReservedSeats = unReservedSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatsInfoModel that = (SeatsInfoModel) o;
        return id.equals(that.id) && totalSeats.equals(that.totalSeats) && reservedSeats.equals(that.reservedSeats) && unReservedSeats.equals(that.unReservedSeats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, totalSeats, reservedSeats, unReservedSeats);
    }

    @Override
    public String toString() {
        return "SeatsInfoModel{" +
                "id=" + id +
                ", totalSeats=" + totalSeats +
                ", reservedSeats=" + reservedSeats +
                ", unReservedSeats=" + unReservedSeats +
                '}';
    }
}
