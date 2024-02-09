package in.toralabs.library.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import in.toralabs.library.service.WhatsappTemplateService;
import in.toralabs.library.util.ResponseModel;
import in.toralabs.library.util.Utils;
import in.toralabs.library.util.whatsappModels.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class WhatsappTemplateServiceImpl implements WhatsappTemplateService {


    @Override
    public ResponseEntity<ResponseModel> sendNotifyMessage(String number, String name, String var2, String templateName, String mediaFileName, boolean isEnglishLanguage) {
        ResponseModel responseModel = new ResponseModel();
        WhatsAppMainModel whatsAppMainModel = new WhatsAppMainModel();
        whatsAppMainModel.setMessagingProduct("whatsapp");
        whatsAppMainModel.setRecipientType("individual");
        whatsAppMainModel.setTo("91" + number);

//        whatsAppMainModel.setTo("91" + "9111886712");
        whatsAppMainModel.setType("template");

        Template template = new Template();
        template.setName(templateName);

        Language language = new Language();
        if (isEnglishLanguage) {
            language.setCode("en");
        } else {
            language.setCode("hi");
        }

        template.setLanguage(language);

        List<Component> components = new ArrayList<>();
        components.add(new Component());
        components.add(new Component());

        components.get(0).setType("header");
        components.get(1).setType("body");

        // parameters for message template header
        List<Parameter> parameterHeaderList = new ArrayList<>();
        parameterHeaderList.add(new Parameter());
        parameterHeaderList.get(0).setType("image");
        Image image = new Image();

        // hardcoding as of now
        image.setLink("https://toralabs.tech/generatePromotionImages/" + mediaFileName);
        parameterHeaderList.get(0).setImage(image);
        components.get(0).setParameters(parameterHeaderList);

        // parameters for message template body
        List<Parameter> parameterBodyList = new ArrayList<>();
        parameterBodyList.add(new Parameter());
        parameterBodyList.add(new Parameter());
        parameterBodyList.add(new Parameter());

        parameterBodyList.get(0).setType("text");
        parameterBodyList.get(1).setType("text");
        parameterBodyList.get(2).setType("text");

        parameterBodyList.get(0).setText(name);
        parameterBodyList.get(1).setText(var2);
        if (isEnglishLanguage) {
            parameterBodyList.get(2).setText("Do not reply to this message. You can directly call us, in case of any queries.");
        } else {
            parameterBodyList.get(2).setText("इस संदेश का उत्तर न दें। किसी भी प्रश्न के मामले में आप सीधे हमें कॉल कर सकते हैं।");
        }
        components.get(1).setParameters(parameterBodyList);

        template.setComponents(components);

        whatsAppMainModel.setTemplate(template);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            String requestBody = objectMapper.writeValueAsString(whatsAppMainModel);

            System.out.println("RequestBody for Whatsapp Api is: \n" + requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(Utils.generateToken());

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<WhatsAppResponseModel> whatsAppResponseModel = restTemplate.exchange(Utils.MESSAGES_URL, HttpMethod.POST, requestEntity, WhatsAppResponseModel.class);
            if (whatsAppResponseModel.getStatusCode() == HttpStatus.OK) {
                responseModel.setStatus("Message sent successfully");
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            } else {
                throw new Exception(whatsAppResponseModel.toString());
            }
        } catch (Exception e) {
            // do nothing
            responseModel.setStatus("Error sending the receipt message on Whatsapp.");
            responseModel.setExceptionMessage(e.getMessage());
            System.out.println("error is:\n" + e.getMessage() + e.getCause() + "\n" + e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @Override
    public ResponseEntity<ResponseModel> sendImageMessage(String number, String name, String var2, String templateName, String mediaFileName, boolean isEnglishLanguage) {
        ResponseModel responseModel = new ResponseModel();
        WhatsAppMainModel whatsAppMainModel = new WhatsAppMainModel();
        whatsAppMainModel.setMessagingProduct("whatsapp");
        whatsAppMainModel.setRecipientType("individual");
        whatsAppMainModel.setTo("91" + number);

//        whatsAppMainModel.setTo("91" + "9111886712");
        whatsAppMainModel.setType("template");

        Template template = new Template();
        template.setName(templateName);

        Language language = new Language();
        if (isEnglishLanguage) {
            language.setCode("en");
        } else {
            language.setCode("hi");
        }
        template.setLanguage(language);

        List<Component> components = new ArrayList<>();
        components.add(new Component());
        components.add(new Component());

        components.get(0).setType("header");
        components.get(1).setType("body");

        // parameters for message template header
        List<Parameter> parameterHeaderList = new ArrayList<>();
        parameterHeaderList.add(new Parameter());
        parameterHeaderList.get(0).setType("image");
        Image image = new Image();

        // hardcoding as of now
        image.setLink("https://toralabs.tech/generatePromotionImages/" + mediaFileName);
        parameterHeaderList.get(0).setImage(image);
        components.get(0).setParameters(parameterHeaderList);


        // parameters for message template body
        List<Parameter> parameterBodyList = new ArrayList<>();
        parameterBodyList.add(new Parameter());
        parameterBodyList.add(new Parameter());
        parameterBodyList.add(new Parameter());

        parameterBodyList.get(0).setType("text");
        parameterBodyList.get(1).setType("text");
        parameterBodyList.get(2).setType("text");

        parameterBodyList.get(0).setText(name);
        parameterBodyList.get(1).setText(var2);
        if (isEnglishLanguage) {
            parameterBodyList.get(2).setText("Do not reply to this message. You can directly call us, in case of any queries.");
        } else {
            parameterBodyList.get(2).setText("इस संदेश का उत्तर न दें। किसी भी प्रश्न के मामले में आप सीधे हमें कॉल कर सकते हैं।");
        }
        components.get(1).setParameters(parameterBodyList);

        template.setComponents(components);

        whatsAppMainModel.setTemplate(template);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            String requestBody = objectMapper.writeValueAsString(whatsAppMainModel);

            System.out.println("RequestBody for Whatsapp Api is: \n" + requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(Utils.generateToken());

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<WhatsAppResponseModel> whatsAppResponseModel = restTemplate.exchange(Utils.MESSAGES_URL, HttpMethod.POST, requestEntity, WhatsAppResponseModel.class);
            if (whatsAppResponseModel.getStatusCode() == HttpStatus.OK) {
                responseModel.setStatus("Message sent successfully");
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            } else {
                throw new Exception(whatsAppResponseModel.toString());
            }
        } catch (Exception e) {
            // do nothing
            responseModel.setStatus("Error sending the receipt message on Whatsapp.");
            responseModel.setExceptionMessage(e.getMessage());
            System.out.println("error is:\n" + e.getMessage() + e.getCause() + "\n" + e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    @Override
    public ResponseEntity<ResponseModel> sendVideoMessage(String number, String name, String var2, String templateName, String mediaFileName, boolean isEnglishLanguage) {
        ResponseModel responseModel = new ResponseModel();
        WhatsAppMainModel whatsAppMainModel = new WhatsAppMainModel();
        whatsAppMainModel.setMessagingProduct("whatsapp");
        whatsAppMainModel.setRecipientType("individual");
        whatsAppMainModel.setTo("91" + number);

//        whatsAppMainModel.setTo("91" + "9111886712");
        whatsAppMainModel.setType("template");

        Template template = new Template();
        template.setName(templateName);

        Language language = new Language();
        if (isEnglishLanguage) {
            language.setCode("en");
        } else {
            language.setCode("hi");
        }
        template.setLanguage(language);

        List<Component> components = new ArrayList<>();
        components.add(new Component());
        components.add(new Component());

        components.get(0).setType("header");
        components.get(1).setType("body");

        // parameters for message template header
        List<Parameter> parameterHeaderList = new ArrayList<>();
        parameterHeaderList.add(new Parameter());
        parameterHeaderList.get(0).setType("video");
        Video video = new Video();

        // hardcoding as of now
        video.setLink("https://toralabs.tech/generatePromotionVideos/" + mediaFileName);
        parameterHeaderList.get(0).setVideo(video);
        components.get(0).setParameters(parameterHeaderList);


        // parameters for message template body
        List<Parameter> parameterBodyList = new ArrayList<>();
        parameterBodyList.add(new Parameter());
        parameterBodyList.add(new Parameter());
        parameterBodyList.add(new Parameter());

        parameterBodyList.get(0).setType("text");
        parameterBodyList.get(1).setType("text");
        parameterBodyList.get(2).setType("text");

        parameterBodyList.get(0).setText(name);
        parameterBodyList.get(1).setText(var2);
        if (isEnglishLanguage) {
            parameterBodyList.get(2).setText("Do not reply to this message. You can directly call us, in case of any queries.");
        } else {
            parameterBodyList.get(2).setText("इस संदेश का उत्तर न दें। किसी भी प्रश्न के मामले में आप सीधे हमें कॉल कर सकते हैं।");
        }
        components.get(1).setParameters(parameterBodyList);

        template.setComponents(components);

        whatsAppMainModel.setTemplate(template);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            String requestBody = objectMapper.writeValueAsString(whatsAppMainModel);

            System.out.println("RequestBody for Whatsapp Api is: \n" + requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(Utils.generateToken());

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<WhatsAppResponseModel> whatsAppResponseModel = restTemplate.exchange(Utils.MESSAGES_URL, HttpMethod.POST, requestEntity, WhatsAppResponseModel.class);
            if (whatsAppResponseModel.getStatusCode() == HttpStatus.OK) {
                responseModel.setStatus("Message sent successfully");
                responseModel.setExceptionMessage("");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            } else {
                throw new Exception(whatsAppResponseModel.toString());
            }
        } catch (Exception e) {
            // do nothing
            responseModel.setStatus("Error sending the receipt message on Whatsapp.");
            responseModel.setExceptionMessage(e.getMessage());
            System.out.println("error is:\n" + e.getMessage() + e.getCause() + "\n" + e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

}
