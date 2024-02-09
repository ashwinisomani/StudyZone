package in.toralabs.library.jpa.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lib_coupon_codes")
public class CouponCodeModel {
    private String couponName;

    private Integer couponDiscount;

    @Id
    @Column(name = "coupon_name")
    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    @Basic
    @Column(name = "coupon_discount")
    public int getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(int couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponCodeModel that = (CouponCodeModel) o;
        return couponName.equals(that.couponName) && Objects.equals(couponDiscount, that.couponDiscount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(couponName, couponDiscount);
    }

    @Override
    public String toString() {
        return "CouponCodeModel{" +
                "couponName='" + couponName + '\'' +
                ", couponDiscount=" + couponDiscount +
                '}';
    }
}
