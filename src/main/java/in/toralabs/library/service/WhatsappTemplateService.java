package in.toralabs.library.service;

import in.toralabs.library.util.ResponseModel;
import org.springframework.http.ResponseEntity;

public interface WhatsappTemplateService {

    // only for notifyTemplate
    ResponseEntity<ResponseModel> sendNotifyMessage(String number, String name, String var2, String templateName, String mediaFileName, boolean isEnglishLanguage);

    // always use png images
    ResponseEntity<ResponseModel> sendImageMessage(String number, String name, String var2, String templateName, String mediaFileName, boolean isEnglishLanguage);

    // always use mp4 videos
    ResponseEntity<ResponseModel> sendVideoMessage(String number, String name, String var2, String templateName, String mediaFileName, boolean isEnglishLanguage);
}
