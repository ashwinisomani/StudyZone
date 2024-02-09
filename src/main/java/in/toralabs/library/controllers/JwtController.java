package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.JwtTokenModel;
import in.toralabs.library.jpa.repository.JwtTokenRepository;
import in.toralabs.library.security.JwtUtil;
import in.toralabs.library.util.AuthenticateModel;
import in.toralabs.library.util.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class JwtController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    private final ResponseModel responseModel = new ResponseModel();

    private static final Logger logger = LoggerFactory.getLogger(JwtController.class);

    @PostMapping(value = "/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody AuthenticateModel authenticateModel) throws Exception {
        logger.info("Username is " + authenticateModel.getUsername() + " password is " + authenticateModel.getPassword() + " location is " + authenticateModel.getLocation() + " deviceModel is " + authenticateModel.getDeviceModel());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticateModel.getUsername().trim(), authenticateModel.getPassword().trim()));
        } catch (Exception e) {
            logger.info("Exception message " + e.getMessage());
            responseModel.setStatus("Failed to authenticate");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
        String jwtToken = jwtUtil.generateToken(authenticateModel.getUsername());
        JwtTokenModel jwtTokenModel = new JwtTokenModel();
        jwtTokenModel.setEnabled(false);
        jwtTokenModel.setTokenId(jwtToken);
        jwtTokenModel.setCreationDate(new Date());
        jwtTokenModel.setLocation(authenticateModel.getLocation());
        jwtTokenModel.setDeviceModel(authenticateModel.getDeviceModel());
        jwtTokenModel.setCreationDtm(new Timestamp(System.currentTimeMillis()));
        jwtTokenModel.setUserName(authenticateModel.getUsername());
        jwtTokenRepository.save(jwtTokenModel);
        return ResponseEntity.ok(jwtToken);
    }

    @GetMapping(value = "/login/{authToken}")
    public ResponseEntity<String> login(@PathVariable(value = "authToken") String authToken) throws Exception {
        Optional<JwtTokenModel> optionalJwtTokenModel = jwtTokenRepository.findById(authToken);
        if (optionalJwtTokenModel.isPresent()) {
            JwtTokenModel jwtTokenModel = optionalJwtTokenModel.get();
            jwtTokenModel.setEnabled(true);
            jwtTokenRepository.save(jwtTokenModel);
            return ResponseEntity.ok(authToken);
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(authToken);
        }
    }

    @GetMapping(value = "/logoutFromAllDevices")
    public ResponseEntity<Object> logoutFromAllDevices() {
        try {
            jwtTokenRepository.disableAllJwtTokens();
            return ResponseEntity.status(HttpStatus.OK).body("Logged out of all devices");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Failed to logout from all devices");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/logout/{authToken}")
    public ResponseEntity<Object> logout(@PathVariable("authToken") String authToken) {
        try {
            boolean isAuthTokenPresentInDB = jwtTokenRepository.findById(authToken).isPresent();
            if (isAuthTokenPresentInDB) {
                jwtTokenRepository.disableJwtTokenId(authToken);
            }
            return ResponseEntity.status(HttpStatus.OK).body("Logged out");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Failed to logout");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @GetMapping(value = "/getAllActiveSessions")
    public ResponseEntity<Object> getAllActiveSessions() {
        try {
            List<JwtTokenModel> list = jwtTokenRepository.findAllActiveTokens();
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Failed to fetch active sessions");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteOlderDisabledJwtTokens() {
        // deleting 2-3 days older disabled jwt tokens from db
        logger.info("Executing the deleteOlderDisabledJwtTokens");
        try {
            jwtTokenRepository.deleteAllJwtTokensCreatedBeforeAFewDays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
