package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.SeatsInfoModel;
import in.toralabs.library.jpa.repository.SeatsInfoRepository;
import in.toralabs.library.service.SeatsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeatsInfoController {

    @Autowired
    SeatsInfoRepository seatsInfoRepository;

    @Autowired
    SeatsInfoService seatsInfoService;

    @GetMapping(value = "/fetchSeatsInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchSeatsInfo() {
        return ResponseEntity.ok().body(seatsInfoRepository.findAll());
    }

    @PostMapping(value = "/updateSeatsInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateSeatsInfo(@RequestBody SeatsInfoModel seatsInfoModel) {
        return seatsInfoService.updateSeatsInfoInDb(seatsInfoModel);
    }
}
