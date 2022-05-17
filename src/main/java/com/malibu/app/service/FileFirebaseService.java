package com.malibu.app.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.malibu.app.dto.FireBaseResponseDto;
import org.apache.commons.lang3.tuple.Pair;
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

    public FireBaseResponseDto upload(MultipartFile multipartFile) {

        try {

            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(getExtension(fileName));
            File file = convertToFile(multipartFile, fileName);
            String urlTemplate = uploadFile(file, fileName);
            file.delete();

            return new FireBaseResponseDto().setFileName(fileName).setUrlTemplate(urlTemplate);
        } catch (Exception e) {
            //TODO handle Exception
            return null;
        }

    }

    public void deleteFile(String fileName) throws IOException {
        Storage storage = StorageOptions.newBuilder().setCredentials(getCredentials()).build().getService();
        storage.delete(BlobId.of(bucketName, fileName));
    }

    public static void main(String[] args) {
        System.out.println(org.hibernate.Version.getVersionString());
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
