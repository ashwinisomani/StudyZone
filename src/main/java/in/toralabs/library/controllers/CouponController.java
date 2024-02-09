package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.CouponCodeModel;
import in.toralabs.library.jpa.repository.CouponCodesRepository;
import in.toralabs.library.util.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CouponController {
    @Autowired
    CouponCodesRepository couponCodesRepository;

    private final ResponseModel responseModel = new ResponseModel();

    @GetMapping(value = "/fetchAllCouponCodes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchAllCouponCodes() {
        List<CouponCodeModel> couponCodeModelList = couponCodesRepository.findAll();
        if (couponCodeModelList.size() > 0) {
            return ResponseEntity.ok().body(couponCodeModelList);
        } else {
            responseModel.setStatus("No coupon codes found in the database.");
            responseModel.setExceptionMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
    }

    @PostMapping(value = "/addNewCouponCode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addNewCouponCode(@RequestBody CouponCodeModel couponCodeModel) {
        try {
            return ResponseEntity.ok().body(couponCodesRepository.save(couponCodeModel));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Unable to add this new coupon code.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @PostMapping(value = "/deleteCouponCode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteCouponCode(@RequestBody CouponCodeModel couponCodeModel) {
        try {
            couponCodesRepository.delete(couponCodeModel);
            responseModel.setStatus("Coupon deleted successfully.");
            responseModel.setExceptionMessage("");
            return ResponseEntity.ok().body(responseModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Unable to delete this coupon.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }
}
