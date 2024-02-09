
package in.toralabs.library.util.whatsappModels;

public class WhatsAppMainModel {

    private String messaging_product;

    private String recipient_type;

    private String to;

    private String type;

    private Template template;

    public String getMessagingProduct() {
        return messaging_product;
    }

    public void setMessagingProduct(String messagingProduct) {
        this.messaging_product = messagingProduct;
    }

    public String getRecipientType() {
        return recipient_type;
    }

    public void setRecipientType(String recipientType) {
        this.recipient_type = recipientType;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

}
