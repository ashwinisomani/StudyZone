package in.toralabs.library.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import in.toralabs.library.jpa.model.EnquiryModel;
import in.toralabs.library.jpa.model.UserDetailModel;
import in.toralabs.library.jpa.model.UserDetailModelPK;
import in.toralabs.library.jpa.repository.EnquiryRepository;
import in.toralabs.library.jpa.repository.TimeSlotCostMappingRepository;
import in.toralabs.library.jpa.repository.UserDetailRepository;
import in.toralabs.library.service.CommonService;
import in.toralabs.library.util.ResponseModel;
import in.toralabs.library.util.Utils;
import in.toralabs.library.util.whatsappModels.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

@Service
public class CommonServiceImpl implements CommonService {

    @Value("${project.images}")
    private String rootPathForImages;

    @Value("${project.promotion-images}")
    private String rootPathForPromotionImages;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private TimeSlotCostMappingRepository timeSlotCostMappingRepository;

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Override
    public void generatePdf(UserDetailModel userDetailModel, String rootPathForReceipts) throws Exception {
        File dir = new File(rootPathForReceipts);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String pdfFileName = rootPathForReceipts + File.separator + userDetailModel.getTransactionId() + ".pdf";
        PdfWriter pdfWriter = new PdfWriter(pdfFileName);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4);
        PdfFont titleFont = PdfFontFactory.createFont(FontConstants.HELVETICA);

        InputStream in = new FileInputStream(rootPathForPromotionImages + File.separator + "/Library-Rules.png");
        ImageData id = ImageDataFactory.create(in.readAllBytes());
        Image image = new Image(id);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        float documentWidth = document.getPdfDocument().getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();
        float documentHeight = document.getPdfDocument().getDefaultPageSize().getHeight() - document.getTopMargin() - document.getBottomMargin();
        image.scaleToFit(documentWidth, documentHeight);

        Paragraph paragraph = new Paragraph().add(image)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(paragraph);

        document.add(new AreaBreak());

        InputStream inputStream = new FileInputStream(rootPathForImages + File.separator + "/logo.jpeg");
        ImageData imageData = ImageDataFactory.create(inputStream.readAllBytes());
        Image img = new Image(imageData);
        img.setHorizontalAlignment(HorizontalAlignment.CENTER);

        paragraph = new Paragraph().add(img.scaleToFit(510, 100))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(paragraph);

        Paragraph paraDesc = new Paragraph("Smart Study Zone, 80-B Gautam Nagar Main Road, Near Vidhyarthi Book Depo, Mob: 8269006294")
                .setBold().setFont(titleFont).setFontSize(16.5f)
                .setBorder(new SolidBorder(Color.BLACK, 1))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(227, 30, 37))
                .setMarginBottom(10f);
        document.add(paraDesc);

        DottedLine dottedLine = new DottedLine(2, 4);
        dottedLine.setLineWidth(3f);
        dottedLine.setColor(Color.BLACK);
        document.add(new LineSeparator(dottedLine));
        userDataForReceipt(userDetailModel, document);

        // footer
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new IEventHandler() {
            @Override
            public void handleEvent(Event event) {

                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                PdfDocument pdf = docEvent.getDocument();
                PdfPage page = docEvent.getPage();
                float x = page.getPageSize().getWidth() / 2 - 5;
                float y = 10;

                Rectangle pageSize = page.getPageSize();
                PdfCanvas pdfCanvas = new PdfCanvas(
                        page.getLastContentStream(), page.getResources(), pdf);
                Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
                Paragraph footer = new Paragraph("Generated on " + new Date().toString()).setFont(titleFont).setFontSize(7f);
                canvas.showTextAligned(footer, x, y, TextAlignment.CENTER);
                pdfCanvas.release();
            }
        });
        document.close();
    }


    private void userDataForReceipt(UserDetailModel userDetailModel, Document document) throws IOException {
        float[] pointColumnWidths = {300F, 400F};
        Table table = new Table(pointColumnWidths);
        table.setFontSize(14f).setFontColor(Color.BLACK);
        table.setMargins(20f, 10f, 10f, 10f);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // name
        table.addCell(new Cell().add("Name").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getName()).setPadding(2f));


        // mobile number
        table.addCell(new Cell().add("Mobile Number").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getMobileNumber()).setPadding(2f));

        // email id
        if (userDetailModel.getEmailId() != null && !userDetailModel.getEmailId().isBlank()) {
            table.addCell(new Cell().add("Email").setBold().setPadding(2f));
            table.addCell(new Cell().add(userDetailModel.getEmailId()).setPadding(2f));
        }

        // exam name
        if (userDetailModel.getExamName() != null && !userDetailModel.getExamName().isBlank()) {
            table.addCell(new Cell().add("Exam Name").setBold().setPadding(2f));
            table.addCell(new Cell().add(userDetailModel.getExamName()).setPadding(2f));
        }

        // entry date
        table.addCell(new Cell().add("Entry Date (dd-MM-yyyy)").setBold().setPadding(2f));

        String[] entryDate = userDetailModel.getEntryDate().toString().split(" ")[0].split("-");
        table.addCell(new Cell().add(entryDate[2] + "-" + entryDate[1] + "-" + entryDate[0]).setPadding(2f));

        // duration in months
        table.addCell(new Cell().add("Duration (in days)").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getDurationInDays() + "").setPadding(2f));

        // exit data
        table.addCell(new Cell().add("Exit Date (dd-MM-yyyy)").setBold().setPadding(2f));
        // show in dd-MM-yyyy
        String[] exitDate = userDetailModel.getExitDate().toString().split(" ")[0].split("-");
        table.addCell(new Cell().add(exitDate[2] + "-" + exitDate[1] + "-" + exitDate[0]).setPadding(2f));


        // aadhar card number
        if (userDetailModel.getAadharCardNumber() != null && !userDetailModel.getAadharCardNumber().isBlank()) {
            table.addCell(new Cell().add("Aadhar Number").setBold().setPadding(2f));
            table.addCell(new Cell().add(userDetailModel.getAadharCardNumber()).setPadding(2f));
        }


        // has booked a reserved seat
        table.addCell(new Cell().add("Booked Reserved Seat").setBold().setPadding(2f));
        if (userDetailModel.isHasBookedReservedSeat()) {
            table.addCell(new Cell().add("Yes").setPadding(2f));
        } else {
            table.addCell(new Cell().add("No").setPadding(2f));
        }

        // booked seat number
        if (userDetailModel.getBookedSeatNumber() != -1) {
            table.addCell(new Cell().add("Seat Number").setBold().setPadding(2f));
            table.addCell(new Cell().add(userDetailModel.getBookedSeatNumber() + "").setPadding(2f));
        }

        // time slot booked
        if (userDetailModel.getTimeSlotBookedMain() != null && !userDetailModel.getTimeSlotBookedMain().isBlank()) {
            table.addCell(new Cell().add("Time Slot Booked").setBold().setPadding(2f));
            table.addCell(new Cell().add(userDetailModel.getTimeSlotBookedTemp() + " " + getTimeSlotDesc(userDetailModel.getTimeSlotBookedTemp())).setPadding(2f));
        }

        // Gender
        table.addCell(new Cell().add("Gender").setBold().setPadding(2f));
        if (userDetailModel.getGender().equals("M")) {
            table.addCell(new Cell().add("Male").setPadding(2f));
        } else {
            table.addCell(new Cell().add("Female").setPadding(2f));
        }

        // Amount
        table.addCell(new Cell().add("Amount").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getCost() + "").setPadding(2f));

        // discount
        table.addCell(new Cell().add("Deduction").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getDiscount() + "").setPadding(2f));

        // amount paid
        table.addCell(new Cell().add("Amount Paid").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getFinalCost() + "").setPadding(2f));

        // transaction id
        table.addCell(new Cell().add("Transaction ID").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getTransactionId() + "").setPadding(2f));

        // mode of transport
        table.addCell(new Cell().add("Mode of Transport").setBold().setPadding(2f));
        table.addCell(new Cell().add(userDetailModel.getModeOfTransport() + "").setPadding(2f));

        // aadhar card number
        if (userDetailModel.getCustomerPhotoUrl() != null && !userDetailModel.getCustomerPhotoUrl().isBlank()) {
            table.addCell(new Cell().add("Photo").setBold().setPadding(2f));
            InputStream inputStream = new FileInputStream(rootPathForImages + File.separator + userDetailModel.getPaymentProofUrl());
            ImageData imageData = ImageDataFactory.create(inputStream.readAllBytes());
            Image img = new Image(imageData);
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            Cell imageCell = new Cell();
            imageCell.add(img.scaleToFit(200, 200));
            table.addCell(imageCell).setPadding(5f).setHorizontalAlignment(HorizontalAlignment.CENTER);
        }

        document.add(table);
    }

    private String getTimeSlotDesc(String timeSlotTitle) {
        return "(" + timeSlotCostMappingRepository.getTimeSlotDescription(timeSlotTitle) + ")";
    }

    @Override
    public void updateListWithImageUrlsRatherThanFileNames(UserDetailModel userDetailModel) {
        userDetailModel.setIdProofUrl("https://toralabs.tech/profiles/images/" + userDetailModel.getIdProofUrl());
        userDetailModel.setCustomerPhotoUrl("https://toralabs.tech/profiles/images/" + userDetailModel.getCustomerPhotoUrl());
        userDetailModel.setPaymentProofUrl("https://toralabs.tech/profiles/images/" + userDetailModel.getPaymentProofUrl());
    }

    @Override
    public ResponseEntity<ResponseModel> sendMessage(String mobileNumber, Long entryTimeInLong, HashMap<String, String> timeSlotDescMap) {
        ResponseModel responseModel = new ResponseModel();
        UserDetailModelPK userDetailModelPK = new UserDetailModelPK();
        userDetailModelPK.setEntryTimeInLong(entryTimeInLong);
        userDetailModelPK.setMobileNumber(mobileNumber);
        Optional<UserDetailModel> userDetailModelOptional = userDetailRepository.findById(userDetailModelPK);
        if (userDetailModelOptional.isEmpty()) {
            responseModel.setStatus("Not a valid mobile number");
            responseModel.setExceptionMessage("");
            return ResponseEntity.ok().body(responseModel);
        } else {
            UserDetailModel userDetailModel = userDetailModelOptional.get();
            WhatsAppMainModel whatsAppMainModel = new WhatsAppMainModel();
            whatsAppMainModel.setMessagingProduct("whatsapp");
            whatsAppMainModel.setRecipientType("individual");
            System.out.println("Whatsapp sendMessage " + mobileNumber);
            whatsAppMainModel.setTo("91" + mobileNumber);
            whatsAppMainModel.setType("template");

            Template template = new Template();
            template.setName(Utils.INVOICE_MESSAGE);

            Language language = new Language();
            language.setCode("en");

            template.setLanguage(language);

            List<Component> components = new ArrayList<>();
            components.add(new Component());
            components.add(new Component());

            components.get(0).setType("header");
            components.get(1).setType("body");

            // parameters for message template header
            List<Parameter> parameterHeaderList = new ArrayList<>();
            parameterHeaderList.add(new Parameter());
            parameterHeaderList.get(0).setType("document");
            in.toralabs.library.util.whatsappModels.Document document = new in.toralabs.library.util.whatsappModels.Document();
            document.setFilename(userDetailModel.getTransactionId() + ".pdf");

            // hardcoding as of now
            document.setLink("https://toralabs.tech/generateReceiptPdf/" + encodeMobileNumber(mobileNumber) + "/" + entryTimeInLong);
            parameterHeaderList.get(0).setDocument(document);
            components.get(0).setParameters(parameterHeaderList);


            // parameters for message template body
            List<Parameter> parameterBodyList = new ArrayList<>();
            parameterBodyList.add(new Parameter());
            parameterBodyList.add(new Parameter());
            parameterBodyList.add(new Parameter());
            parameterBodyList.add(new Parameter());
            parameterBodyList.add(new Parameter());

            parameterBodyList.get(0).setType("text");
            parameterBodyList.get(1).setType("text");
            parameterBodyList.get(2).setType("text");
            parameterBodyList.get(3).setType("text");
            parameterBodyList.get(4).setType("text");

            parameterBodyList.get(0).setText(userDetailModel.getName().trim());

            String[] entryDate = userDetailModel.getEntryDate().toString().split(" ")[0].split("-");
            String[] exitDate = userDetailModel.getExitDate().toString().split(" ")[0].split("-");

            parameterBodyList.get(1).setText(entryDate[2] + "/" + entryDate[1] + "/" + entryDate[0]);
            parameterBodyList.get(2).setText(exitDate[2] + "/" + exitDate[1] + "/" + exitDate[0]);
            parameterBodyList.get(3).setText(userDetailModel.getTimeSlotBookedTemp() + " (" + timeSlotDescMap.get(userDetailModel.getTimeSlotBookedTemp()) + ")");
            parameterBodyList.get(4).setText("Do not reply to this message. You can directly call us, in case of any queries.");

            components.get(1).setParameters(parameterBodyList);

            template.setComponents(components);

            whatsAppMainModel.setTemplate(template);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
                String requestBody = objectMapper.writeValueAsString(whatsAppMainModel);

                System.out.println("RequestBody for Whatsapp Api is: \n" + requestBody);

                String url = "https://graph.facebook.com/v16.0/116630038102241/messages";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String[] tokenArray = "69 65 65 67 116 82 54 89 84 79 78 111 66 65 66 85 105 97 89 77 55 107 119 70 74 82 68 65 67 104 101 108 99 77 113 69 90 67 49 90 67 98 118 54 56 98 56 105 120 49 89 79 90 65 103 70 85 78 78 77 57 88 88 116 53 114 111 80 106 65 55 66 106 115 112 81 117 115 55 54 53 104 103 89 84 120 57 104 66 77 103 70 72 54 110 72 108 74 90 67 87 107 86 120 122 66 50 110 98 75 114 108 81 86 55 110 68 109 112 113 84 81 67 75 85 73 110 83 90 65 104 65 67 80 77 82 89 116 75 117 100 52 69 65 90 65 85 108 120 75 80 75 82 73 49 49 98 111 117 111 73 70 101 66 111 85 90 67 118 67 76 114 116 49 79 117 120 90 66 108 67 116 90 65 109 113 90 65 57 122 119 69 65 104 51 85 117 83 119 90 67 103 48 99 57 121 88 83 117 119 90 68 90 68".split(" ");
                StringBuilder mainToken = new StringBuilder();
                for (String token : tokenArray) {
                    mainToken.append((char) Integer.parseInt(token));
                }
                headers.setBearerAuth(mainToken.toString());

                System.out.println("Token is: \n" + mainToken);
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<WhatsAppResponseModel> whatsAppResponseModel = restTemplate.exchange(url, HttpMethod.POST, requestEntity, WhatsAppResponseModel.class);
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

    @Override
    public String decodeMobileNumber(String encodedMobileNumber) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(3, 1);
        map.put(5, 2);
        map.put(6, 3);
        map.put(9, 4);
        map.put(7, 5);
        map.put(1, 6);
        map.put(0, 7);
        map.put(4, 8);
        map.put(2, 9);
        map.put(8, 0);
        for (int i = 0; i < encodedMobileNumber.length(); i++) {
            stringBuilder.append(map.get(Integer.parseInt(encodedMobileNumber.charAt(i) + "")));
        }
        // 1234564890 -> 3569719428
        return stringBuilder.toString();
    }

    private String encodeMobileNumber(String mobileNumber) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, 3);
        map.put(2, 5);
        map.put(3, 6);
        map.put(4, 9);
        map.put(5, 7);
        map.put(6, 1);
        map.put(7, 0);
        map.put(8, 4);
        map.put(9, 2);
        map.put(0, 8);
        for (int i = 0; i < mobileNumber.length(); i++) {
            stringBuilder.append(map.get(Integer.parseInt(mobileNumber.charAt(i) + "")));
        }
        // 3569719428 -> 1234564890
        return stringBuilder.toString();
    }

    @Override
    public String reportOfAllCustomers() throws ParseException {
        List<UserDetailModel> userDetailModelList = userDetailRepository.findAllByOrderByRecordCreationDateDesc();
        StringBuilder sb = new StringBuilder();
        sb.append("Name");
        sb.append(",");
        sb.append("Mobile Number");
        sb.append(",");
        sb.append("Email ID");
        sb.append(",");
        sb.append("Exam Name");
        sb.append(",");
        sb.append("Entry Date");
        sb.append(",");
        sb.append("Duration (in days)");
        sb.append(",");
        sb.append("Exit Data");
        sb.append(",");
        sb.append("Aadhar Card Number");
        sb.append(",");
        sb.append("ID Proof URL");
        sb.append(",");
        sb.append("Customer Photo URL");
        sb.append(",");
        sb.append("Payment Proof URL");
        sb.append(",");
        sb.append("Has Booked Reserved");
        sb.append(",");
        sb.append("Booked Seat Number");
        sb.append(",");
        sb.append("Time Slot Booked Main");
        sb.append(",");
        sb.append("Time Slot Booked Temp");
        sb.append(",");
        sb.append("Amount");
        sb.append(",");
        sb.append("Deduction");
        sb.append(",");
        sb.append("Total Amount");
        sb.append(",");
        sb.append("Gender");
        sb.append(",");
        sb.append("Transaction ID");
        sb.append(",");
        sb.append("Modification DTM");
        sb.append(",");
        sb.append("Entry Time In Long");
        sb.append(",");
        sb.append("Mode of Transport");
        sb.append(",");
        sb.append("Creation Date");
        sb.append(",");
        sb.append("\r\n");
        for (UserDetailModel model : userDetailModelList) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            String creationDate = outputFormat.format(inputFormat.parse(model.getRecordCreationDate().toString()));

            sb.append(model.getName());
            sb.append(",");
            sb.append(model.getMobileNumber());
            sb.append(",");
            if (model.getEmailId() == null) sb.append("");
            else sb.append(model.getEmailId());
            sb.append(",");
            if (model.getExamName() == null) sb.append("");
            else sb.append(model.getExamName());
            sb.append(",");
            String[] entryDate = model.getEntryDate().toString().split(" ")[0].split("-");
            sb.append(entryDate[2] + "-" + entryDate[1] + "-" + entryDate[0]);
            sb.append(",");
            sb.append(model.getDurationInDays());
            sb.append(",");
            String[] exitDate = model.getExitDate().toString().split(" ")[0].split("-");
            sb.append(exitDate[2] + "-" + exitDate[1] + "-" + exitDate[0]);
            sb.append(",");
            if (model.getAadharCardNumber() == null) sb.append("");
            else sb.append(model.getAadharCardNumber());
            sb.append(",");
            sb.append("https://toralabs.tech/profiles/images/" + model.getIdProofUrl());
            sb.append(",");
            sb.append("https://toralabs.tech/profiles/images/" + model.getCustomerPhotoUrl());
            sb.append(",");
            sb.append("https://toralabs.tech/profiles/images/" + model.getPaymentProofUrl());
            sb.append(",");
            sb.append(model.isHasBookedReservedSeat());
            sb.append(",");
            sb.append(model.getBookedSeatNumber());
            sb.append(",");
            if (model.getTimeSlotBookedMain() == null) sb.append("");
            else sb.append(model.getTimeSlotBookedMain());
            sb.append(",");
            sb.append(model.getTimeSlotBookedTemp());
            sb.append(",");
            sb.append(model.getCost());
            sb.append(",");
            sb.append(model.getDiscount());
            sb.append(",");
            sb.append(model.getFinalCost());
            sb.append(",");
            sb.append(model.getGender());
            sb.append(",");
            sb.append(model.getTransactionId());
            sb.append(",");
            sb.append(model.getModificationDtm());
            sb.append(",");
            sb.append(model.getEntryTimeInLong());
            sb.append(",");
            sb.append(model.getModeOfTransport());
            sb.append(",");
            sb.append(creationDate);
            sb.append(",");
            sb.append("\r\n");
        }
        return sb.toString();
    }

    @Override
    public String getCustomizedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name");
        sb.append(",");
        sb.append("Mobile Number");
        sb.append(",");
        sb.append("Status");
        sb.append(",");
        sb.append("\r\n");

        List<UserDetailModel> activeUnreservedCustomersList = userDetailRepository.findAllActiveUnreservedCustomers();
        List<UserDetailModel> activeReservedCustomersList = userDetailRepository.findAllActiveReservedCustomers();
        List<EnquiryModel> enquiryModelList = enquiryRepository.findAll();
        List<UserDetailModel> oldCustomersList = userDetailRepository.fetchOldCustomers();

        if (oldCustomersList.size() > 0) {
            LinkedHashSet<UserDetailModel> set = new LinkedHashSet<>();
            HashSet<String> mobileNumberSet = new HashSet<>();
            HashSet<String> activeCustomersNumberSet = new HashSet<>();
            for (UserDetailModel userDetailModel : oldCustomersList) {
                // using mobile numbers set to remove the duplicate old customers.
                updateListWithImageUrlsRatherThanFileNames(userDetailModel);
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

        for (UserDetailModel model : activeReservedCustomersList) {
            sb.append(model.getName());
            sb.append(",");
            sb.append(model.getMobileNumber());
            sb.append(",");
            sb.append("Active Reserved");
            sb.append(",");
            sb.append("\r\n");
        }

        for (UserDetailModel model : activeUnreservedCustomersList) {
            sb.append(model.getName());
            sb.append(",");
            sb.append(model.getMobileNumber());
            sb.append(",");
            sb.append("Active Unreserved");
            sb.append(",");
            sb.append("\r\n");
        }

        for (UserDetailModel model : oldCustomersList) {
            sb.append(model.getName());
            sb.append(",");
            sb.append(model.getMobileNumber());
            sb.append(",");
            sb.append("Old");
            sb.append(",");
            sb.append("\r\n");
        }

        HashSet<String> set = new HashSet<>();

        activeReservedCustomersList.forEach(it -> set.add(it.getMobileNumber()));
        activeUnreservedCustomersList.forEach(it -> set.add(it.getMobileNumber()));
        oldCustomersList.forEach(it -> set.add(it.getMobileNumber()));

        for (EnquiryModel model : enquiryModelList) {
            if (!set.add(model.getMobileNo())) {
                continue;
            }
            sb.append(model.getName());
            sb.append(",");
            sb.append(model.getMobileNo());
            sb.append(",");
            sb.append("Enquiry");
            sb.append(",");
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
