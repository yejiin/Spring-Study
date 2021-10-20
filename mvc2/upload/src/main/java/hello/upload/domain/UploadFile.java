package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    private String uploadFileName;
    private String storeFileName;
}
