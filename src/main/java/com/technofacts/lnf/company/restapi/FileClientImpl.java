package com.technofacts.lnf.company.restapi;

import com.technofacts.lnf.dto.file.FileDto;
import com.technofacts.lnf.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.exception.LnFException;
import com.technofacts.lnf.service.file.FileFolderService;
import com.technofacts.lnf.service.file.FileService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;


@Service
@Transactional
@Slf4j
public class FileClientImpl extends BaseWebClientService implements FileService, FileFolderService {

    private final WebClient webClient;

    @Value("${aws.s3.bucket.service}")
    private String s3Service;

    @Autowired
    public FileClientImpl(@Qualifier("fileService") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String uploadFile(String folder, MultipartFile file) {
        // Build the multipart body
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("folder", folder);
        bodyBuilder.part("file", file.getResource());

        try {
            // Create the web request, adding JWT token if available
            WebClient.RequestHeadersSpec<?> spec = webClient.post()
                    .uri(s3Service + "/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()));

            // Conditionally add the JWT token to the request headers
            addJwtToken(spec);

            // Execute the request and block to get the response
            return spec.retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("File Upload failed. Status code: {}, Message: {}", e.getStatusCode(), e.getMessage(), e);
            throw new LnFException("File Upload failed {}", e);
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred during file upload {}", e.getMessage());
            throw new LnFException("File to upload the file", e);
        }
    }

    @Override
    public List<String> uploadFiles(String folder, List<MultipartFile> files) {
        return Collections.emptyList();
    }

    @Override
    public List<String> findFilesInFolder(String folderName) {
        List<String> files;
        try {
            WebClient.RequestHeadersSpec<?> spec = webClient.get()
                    .uri(s3Service + "/folder-name?folderName={folderName}", folderName)
                    .accept(MediaType.APPLICATION_JSON);

            // Conditionally add the JWT token to the request headers
            addJwtToken(spec);

            // Execute the request and block to get the response
            files = spec.retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                    })
                    .block();

        } catch (Exception ex) {
            log.error("Failed to get the files from the folder {}", ex.getMessage());
            throw new LnFException("Failed to get the files from the folder", ex);
        }
        return files;
    }

    @Override
    public void delete(List<String> filePaths) {
        try {
            String fileNames = String.join(",", filePaths);
            WebClient.RequestHeadersSpec<?> spec = webClient
                    .delete()
                    .uri(s3Service + "?filePaths=" + fileNames)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            // Conditionally add the JWT token to the request headers
            addJwtToken(spec);

            // Execute the request and block to get the response
            spec.retrieve()
                    .toBodilessEntity()
                    .block();
            log.debug("File with filePaths : {} successfully deleted", filePaths);
        } catch (Exception e) {
            log.error("Failed to delete files with filePaths {}: {}", filePaths, e.getMessage());
            throw new LnFException("Failed to delete files with filePaths " + filePaths, e);
        }
    }

    public ResponseEntity<byte[]> findFile(String filePath) {
        return null;
    }

    @Override
    public ResponseEntity<byte[]> findFileContent(String filePath) {
        try {
            WebClient.RequestHeadersSpec<?> spec = webClient.get()
                    .uri(s3Service + "/content" + "?filePath={filePath}", filePath);

            // Conditionally add the JWT token to the request headers
            addJwtToken(spec);

            // Execute the request and block to get the response
            ResponseEntity<byte[]> response = spec
                    .retrieve()
                    .toEntity(byte[].class)
                    .block();
            log.debug("file is retrieved with filePath {} from the folder", filePath);
            return response;
        } catch (Exception ex) {
            log.error("Failed to retrieve the file from the folder {}", ex.getMessage());
            throw new LnFException("Failed to retrieve the file from the folder", ex);
        }
    }

    @Override
    public List<FileDto> findFiles(String folderName) {
        List<FileDto> files;
        try {
            WebClient.RequestHeadersSpec<?> spec = webClient.get()
                    .uri(s3Service + "/folder-names?folderName={folderName}", folderName)
                    .accept(MediaType.APPLICATION_JSON);

            // Conditionally add the JWT token to the request headers
            addJwtToken(spec);

            // Execute the request and block to get the response
            files = spec.retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<FileDto>>() {
                    })
                    .block();

        } catch (Exception ex) {
            log.error("Failed to fetch the files in the folder {}", folderName);
            throw new LnFException("Failed to fetch the files in the folder", ex);
        }
        return files;
    }

}
