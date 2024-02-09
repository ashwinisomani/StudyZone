
package in.toralabs.library.util.whatsappModels;

import java.util.List;

public class WhatsAppResponseModel {

    private String messaging_product;

    private List<Contact> contacts;

    private List<Message> messages;

    public String getMessagingProduct() {
        return messaging_product;
    }

    public void setMessagingProduct(String messagingProduct) {
        this.messaging_product = messagingProduct;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "WhatsAppResponseModel{" +
                "messaging_product='" + messaging_product + '\'' +
                ", contacts=" + contacts +
                ", messages=" + messages +
                '}';
    }
}
