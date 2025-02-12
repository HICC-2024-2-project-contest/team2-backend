package com.hiccproject.moaram.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    // S3에 이미지 업로드
    public void uploadImage(Long exhibitionId,
                            String savePath,
                            String bucketName,
                            MultipartFile image) throws IOException {
        String uniqueFileName = exhibitionId + ".jpeg";
        String filePath = savePath + uniqueFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try (InputStream inputStream = image.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, filePath, inputStream, metadata));
        }
    }

    public void uploadImages(Long itemId,
                             String savePath,
                             String bucketName,
                             List<MultipartFile> images) throws IOException {
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            String uniqueFileName = itemId + "_" + i + ".jpeg";
            String filePath = savePath + uniqueFileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            metadata.setContentType(image.getContentType());

            try (InputStream inputStream = image.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucketName, filePath, inputStream, metadata));
            }
        }
    }


    // S3에서 이미지 가져오기
    public String getImageBase64(String exhibitionId,
                                 String savePath,
                                 String bucketName) throws IOException {
        String filePath = savePath + exhibitionId + ".jpeg";
        S3Object s3Object = amazonS3.getObject(bucketName, filePath);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] imageBytes = IOUtils.toByteArray(inputStream);

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public List<String> getImagesBase64(List<String> imageNames, String savePath, String bucketName) {
        // 각 이미지 ID에 대해 비동기적으로 이미지를 Base64로 변환
        List<CompletableFuture<String>> futures = imageNames.stream()
                .map(imageName -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return getImageBase64(imageName, savePath, bucketName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null; // 실패 시 null 반환
                    }
                }))
                .collect(Collectors.toList());

        // 모든 CompletableFuture가 완료될 때까지 대기하고 결과 반환
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();  // 비동기 처리 완료까지 대기

        return futures.stream()
                .map(CompletableFuture::join) // 각 CompletableFuture의 결과를 가져옴
                .collect(Collectors.toList());
    }
}
