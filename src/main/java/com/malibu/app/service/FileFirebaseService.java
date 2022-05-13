package com.malibu.app.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileFirebaseService {
    String bucketName = "springfirebase-3b777.appspot.com";
    String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/%s?alt=media";

    public String upload(MultipartFile multipartFile) {

        try {

            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(getExtension(fileName));
            File file = convertToFile(multipartFile, fileName);
            String TEMP_URL = uploadFile(file, fileName);
            file.delete();

            return TEMP_URL;
        } catch (Exception e) {
            //TODO handle Exception
            return null;
        }

    }

    public ResponseEntity<String> download(String fileName) throws IOException {
        String destFileName = UUID.randomUUID().toString().concat(getExtension(fileName));
        String destFilePath = "C:\\Users\\User\\Desktop\\MALIBU\\Firebase_" + destFileName;


        Storage storage = StorageOptions.newBuilder().setCredentials(getCredentials()).build().getService();
        Blob blob = storage.get(BlobId.of(bucketName, fileName));

        blob.downloadTo(Paths.get(destFilePath));
        return new ResponseEntity<>("Successfully Downloaded!", HttpStatus.OK);
    }

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Storage storage = StorageOptions.newBuilder().setCredentials(getCredentials()).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private Credentials getCredentials() throws IOException {
        return GoogleCredentials
                .fromStream(new FileInputStream("C:\\Users\\User\\Desktop\\MALIBU\\Firebase_cred\\springfirebase-key.json"));

    }
}
