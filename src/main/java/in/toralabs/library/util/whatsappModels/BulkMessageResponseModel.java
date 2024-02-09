package in.toralabs.library.util.whatsappModels;

import java.util.List;

public class BulkMessageResponseModel {
    List<String> successMobileNoList;

    List<String> failureMobileNoList;

    public List<String> getSuccessMobileNoList() {
        return successMobileNoList;
    }

    public void setSuccessMobileNoList(List<String> successMobileNoList) {
        this.successMobileNoList = successMobileNoList;
    }

    public List<String> getFailureMobileNoList() {
        return failureMobileNoList;
    }

    public void setFailureMobileNoList(List<String> failureMobileNoList) {
        this.failureMobileNoList = failureMobileNoList;
    }
}
