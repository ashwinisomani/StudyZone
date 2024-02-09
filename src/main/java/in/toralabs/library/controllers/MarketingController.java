package in.toralabs.library.controllers;

import in.toralabs.library.jpa.model.EnquiryModel;
import in.toralabs.library.jpa.model.UserDetailModel;
import in.toralabs.library.jpa.repository.EnquiryRepository;
import in.toralabs.library.jpa.repository.UserDetailRepository;
import in.toralabs.library.util.MarketingModel;
import in.toralabs.library.util.NameAndMobileModel;
import in.toralabs.library.util.ResponseModel;
import in.toralabs.library.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@RestController
public class MarketingController {
    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Value("${project.promotion-videos}")
    private String rootPathForPromotionVideos;

    @Value("${project.promotion-images}")
    private String rootPathForPromotionImages;

    private final ResponseModel responseModel = new ResponseModel();

    @GetMapping(value = "/getCategories")
    public ResponseEntity<Object> getCategories() {
        List<String> list = new ArrayList<>();
        list.add("Old Customers");
        list.add("Active Customers");
        list.add("Active Reserved Customers");
        list.add("Active Unreserved Customers");
        list.add("All Enquiries");
        list.add("Enquiry (Latest 100)");
        list.add("Enquiry (Latest 200)");
        list.add("Enquiry (Latest 300)");
        list.add("Enquiry (Latest 400)");
        list.add("Enquiry (Latest 500)");
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/getPromotionVideosName")
    public ResponseEntity<Object> getPromotionVideosName() {
        // always use mp4 videos
        List<String> list = new ArrayList<>();
        File[] files = new File(rootPathForPromotionVideos).listFiles();
        if (files != null) {
            for (File file: files) {
                list.add(file.getName());
            }
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/getPromotionImagesName")
    public ResponseEntity<Object> getPromotionImagesName() {
        // always use png images
        List<String> list = new ArrayList<>();
        File[] files = new File(rootPathForPromotionImages).listFiles();
        if (files != null) {
            for (File file: files) {
                list.add(file.getName());
            }
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/category/{categoryType}")
    public ResponseEntity<Object> getCustomersByCategory(@PathVariable(value = "categoryType") String categoryType) {
        try {
            // category type will contain a symbol '+' to remove whitespaces. replace it with space again before moving to code.
            // Old+Customers -> Old Customers

            List<UserDetailModel> usersList = new ArrayList<>();
            List<EnquiryModel> enquiryList = new ArrayList<>();
            List<NameAndMobileModel> list = new ArrayList<>();

            switch (categoryType.replace("+", " ").toLowerCase()) {
                case "old customers":
                    usersList = getOldCustomers();
                    break;
                case "active customers":
                    usersList = userDetailRepository.findAllActiveCustomers();
                    break;
                case "active reserved customers":
                    usersList = userDetailRepository.findAllActiveReservedCustomers();
                    break;
                case "active unreserved customers":
                    usersList = userDetailRepository.findAllActiveUnreservedCustomers();
                    break;
                case "all enquiries":
                    enquiryList = enquiryRepository.findAllByOrderByCreationDateDesc();
                    break;
                case "enquiry (latest 100)":
                    enquiryList = enquiryRepository.findTop100ByOrderByCreationDateDesc();
                    break;
                case "enquiry (latest 200)":
                    enquiryList = enquiryRepository.findTop200ByOrderByCreationDateDesc();
                    break;
                case "enquiry (latest 300)":
                    enquiryList = enquiryRepository.findTop300ByOrderByCreationDateDesc();
                    break;
                case "enquiry (latest 400)":
                    enquiryList = enquiryRepository.findTop400ByOrderByCreationDateDesc();
                    break;
                case "enquiry (latest 500)":
                    enquiryList = enquiryRepository.findTop500ByOrderByCreationDateDesc();
                    break;
            }

            usersList.forEach(model -> {
                NameAndMobileModel nameAndMobileModel = new NameAndMobileModel(model.getName(), model.getMobileNumber());
                list.add(nameAndMobileModel);
            });
            enquiryList.forEach(model -> {
                NameAndMobileModel nameAndMobileModel = new NameAndMobileModel(model.getName(), model.getMobileNo());
                list.add(nameAndMobileModel);
            });

            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseModel.setStatus("Failed to getCustomersByCategory.");
            responseModel.setExceptionMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }


    @GetMapping(value = "/getMarketingTemplates")
    public ResponseEntity<Object> getMarketingTemplates() {
        // this api needs to updated according to changes made in whatsapp cloud messaging templates
        List<MarketingModel> list = new ArrayList<>();

        // 1
        MarketingModel marketingModel = new MarketingModel();
        List<String> hints = new ArrayList<>();

        marketingModel.setTitle("Marketing Template 1");
        marketingModel.setMediaType("Video");
        marketingModel.setLanguage("English");
        marketingModel.setTemplateName(Utils.MARKETING_MESSAGE_1);
        hints.add("SampleName");
        hints.add("upto 10% discount");
        hints.add("Do not reply to this message. You can directly call us, in case of any queries.");
        marketingModel.setHintsList(hints);

        marketingModel.setHasFooterPresent(true);
        marketingModel.setHasContactButtonPresent(true);
        marketingModel.setHasWebButtonPresent(true);
        marketingModel.setFooterText("Balajee Library, Near Vidhyarthi Book Depo, Chetak Bridge");
        marketingModel.setContactButtonText("Contact");
        marketingModel.setWebButtonText("Locate on map");

        marketingModel.setDescription("Dear {{1}},\n" +
                "\n" +
                "Enjoy <b>{{2}} at Balajee Library</b> \uD83D\uDCDA\n" +
                "\n" +
                "Please feel free to invite your friends to join us. \n" +
                "\n" +
                "<b>Facilities provided are as follows:</b>\n" +
                "\n" +
                "• \uD83C\uDF2C️ Fully air conditioned environment.\n" +
                "• \uD83D\uDCBA Comfortable chairs for seating.\n" +
                "• ⚡ Power backup in case of electricity cut.\n" +
                "• \uD83E\uDDF9 Full time cleaning staff offering deep cleaning everyday.\n" +
                "• \uD83C\uDF89 Chill out zone.\n" +
                "• \uD83D\uDCA7 Filtered RO water.\n" +
                "• \uD83C\uDF10 High Speed Wifi.\n" +
                "• \uD83D\uDCF0 Wide variety of newspapers, daily.\n" +
                "• \uD83D\uDD12 Locker facility.\n" +
                "• \uD83D\uDD70️ Disciplined environment.\n" +
                "\n" +
                "<b>Note:</b> <b>{{3}}</b>");

        marketingModel.setMarketingUrl(Utils.BASE_URL + "sendMarketingEnglishTemplateWithVideo/var2/mediaFileName");
        list.add(marketingModel);

        // 2
        marketingModel = new MarketingModel();
        hints = new ArrayList<>();

        marketingModel.setTitle("Marketing Template 2");
        marketingModel.setMediaType("Image");
        marketingModel.setLanguage("Hindi");
        marketingModel.setTemplateName(Utils.MARKETING_MESSAGE_2);
        hints.add("ग्राहक");
        hints.add("20% छूट");
        hints.add("इस संदेश का जवाब न दें। यदि कोई प्रश्न हो, तो आप हमें सीधे कॉल कर सकते हैं।");
        marketingModel.setHintsList(hints);

        marketingModel.setHasFooterPresent(true);
        marketingModel.setHasContactButtonPresent(true);
        marketingModel.setHasWebButtonPresent(true);
        marketingModel.setFooterText("बालाजी लाइब्रेरी, विद्यार्थी बुक डेपो के पास, चेतक ब्रिज");
        marketingModel.setContactButtonText("संपर्क करें");
        marketingModel.setWebButtonText("मैप पर ढूंढें");

        marketingModel.setDescription("नमस्ते <b>{{1}}</b>,\n" +
                "\n" +
                "<b>बालाजी लाइब्रेरी (स्मार्ट स्टडी ज़ोन)</b>, में {{2}} का आनंद लें \uD83D\uDCDA\n" +
                "\n" +
                "कृपया इसके लिए अपने दोस्तों को आमंत्रित करें।\n" +
                "\n" +
                "<b>निम्नलिखित सुविधाएं प्रदान की जाती हैं:</b>\n" +
                "\n" +
                "• \uD83C\uDF2C️ पूरी तरह से एयर कंडीशन करने वाला वातावरण।\n" +
                "• \uD83D\uDCBA आरामदायक कुर्सी सुविधा।\n" +
                "• ⚡ बिजली कटौती की स्थिति में पावर बैकअप।\n" +
                "• \uD83E\uDDF9 हर दिन सफाई की सेवा प्रदान करने वाले पूर्णकालिक सफाई कर्मचारी।\n" +
                "• \uD83D\uDCA7 फ़िल्टर किया हुआ RO पानी।\n" +
                "• \uD83C\uDF10 तेज़ गति वाला वाईफ़ाई।\n" +
                "• \uD83D\uDCF0 अखबारों की विस्तृत विविधता हर रोज़।\n" +
                "• \uD83D\uDD12 लॉकर सुविधा।\n" +
                "• \uD83D\uDD70️ अनुशासित वातावरण।\n" +
                "\n" +
                "<b>नोट:</b> <b>{{3}}</b>");

        marketingModel.setMarketingUrl(Utils.BASE_URL + "sendMarketingHindiTemplateWithImage/var2/mediaFileName");
        list.add(marketingModel);

        // 3
        marketingModel = new MarketingModel();
        hints = new ArrayList<>();

        marketingModel.setTitle("Wishes Template");
        marketingModel.setMediaType("Image");
        marketingModel.setLanguage("English");
        marketingModel.setTemplateName(Utils.MARKETING_MESSAGE_3);
        hints.add("SampleName");
        hints.add("Happy Diwali");
        hints.add("Do not reply to this message. You can directly call us, in case of any queries.");
        marketingModel.setHintsList(hints);

        marketingModel.setHasFooterPresent(true);
        marketingModel.setHasContactButtonPresent(true);
        marketingModel.setHasWebButtonPresent(true);
        marketingModel.setFooterText("Balajee Library, Chetak Bridge");
        marketingModel.setContactButtonText("Contact");
        marketingModel.setWebButtonText("Locate on map");

        marketingModel.setDescription("Dear {{1}},\n" +
                "✨ <b>Balajee Library</b> ✨ wishes you {{2}}.\uD83C\uDF1F\n" +
                "\n" +
                "<b>May your journey be filled with knowledge, growth, and success.</b> \uD83D\uDCDA✨\n" +
                "<b>Have a bright future ahead.</b> \uD83C\uDF08\n" +
                "\n" +
                "<b>Note:</b> <b>{{3}}</b>");

        marketingModel.setMarketingUrl(Utils.BASE_URL + "sendWishesTemplate/var2/mediaFileName");
        list.add(marketingModel);

        // 4
        marketingModel = new MarketingModel();
        hints = new ArrayList<>();

        marketingModel.setTitle("Notify Template");
        marketingModel.setMediaType("Image");
        marketingModel.setLanguage("English");
        marketingModel.setTemplateName(Utils.MARKETING_MESSAGE_4);
        hints.add("SampleName");
        hints.add("there will be a power cut today");
        hints.add("Do not reply to this message. You can directly call us, in case of any queries.");
        marketingModel.setHintsList(hints);

        marketingModel.setHasFooterPresent(true);
        marketingModel.setHasContactButtonPresent(true);
        marketingModel.setHasWebButtonPresent(true);
        marketingModel.setFooterText("Balajee Library, Near Vidhyarthi Book Depo, Chetak Bridge");
        marketingModel.setContactButtonText("Contact");
        marketingModel.setWebButtonText("Join on Telegram");

        marketingModel.setDescription("Dear {{1}},\n" +
                "\uD83D\uDCDA Balajee Library \uD83D\uDCDA wants to notify you that {{2}}. \uD83D\uDCE2\n" +
                "Stay connected with us on our Telegram channel for more updates and details. \uD83D\uDCF2\n" +
                "\n" +
                "<b>Note:</b> <b>{{3}}</b>");

        marketingModel.setMarketingUrl(Utils.BASE_URL + "sendNotifyTemplate/var2/mediaFileName");
        list.add(marketingModel);
        return ResponseEntity.ok(list);
    }

    private List<UserDetailModel> getOldCustomers() {
        List<UserDetailModel> oldCustomersList = userDetailRepository.fetchOldCustomers();

        if (oldCustomersList.size() > 0) {
            LinkedHashSet<UserDetailModel> set = new LinkedHashSet<>();
            HashSet<String> mobileNumberSet = new HashSet<>();
            HashSet<String> activeCustomersNumberSet = new HashSet<>();
            for (UserDetailModel userDetailModel : oldCustomersList) {
                // using mobile numbers set to remove the duplicate old customers.
                // remove the number from old customers even if there are many entries with same number,
                // and only one is active among that
                if (userDetailRepository.checkIfCustomerIsActive(userDetailModel.getMobileNumber()) > 0) {
                    activeCustomersNumberSet.add(userDetailModel.getMobileNumber());
                    continue;
                }
                if (!mobileNumberSet.contains(userDetailModel.getMobileNumber())) {
                    set.add(userDetailModel);
                    mobileNumberSet.add(userDetailModel.getMobileNumber());
                }
            }
            oldCustomersList.clear();
            for (UserDetailModel model : set) {
                if (!activeCustomersNumberSet.contains(model.getMobileNumber())) {
                    oldCustomersList.add(model);
                }
            }
        }
        return oldCustomersList;
    }
}
