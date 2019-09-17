package net.safester.application.http.dto;

/**
 * DTO for Message
 * 
 * @author abecquereau
 *
 */
public final class CouponDTO {

    private final String status = "OK";
    private String coupon;

    public CouponDTO() {

    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
	return "CouponDTO [status=" + status + ", coupon=" + coupon + "]";
    }

   
}
