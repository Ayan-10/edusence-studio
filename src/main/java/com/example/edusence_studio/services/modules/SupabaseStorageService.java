package com.example.edusence_studio.services.modules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    private final RestTemplate restTemplate;

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.service-key:}")
    private String supabaseServiceKey;

    @Value("${supabase.bucket:pdfs}")
    private String bucket;

    private void validateConfiguration() {
        if (supabaseUrl == null || supabaseUrl.trim().isEmpty()) {
            throw new RuntimeException(
                "Supabase URL is not configured. Please set SUPABASE_URL environment variable in your .env file. " +
                "Example: SUPABASE_URL=https://your-project-id.supabase.co"
            );
        }
        if (supabaseServiceKey == null || supabaseServiceKey.trim().isEmpty()) {
            throw new RuntimeException(
                "Supabase Service Key is not configured. Please set SUPABASE_SERVICE_KEY environment variable in your .env file. " +
                "Get it from Supabase Dashboard > Settings > API > service_role key"
            );
        }
    }

    public String uploadPdf(MultipartFile file, String folder) {
        try {
            validateConfiguration();
            
            // Sanitize filename to only use safe characters (alphanumeric, hyphens, underscores)
            String sanitizedFileName = sanitizeFileName(getFileNameWithoutExtension(file.getOriginalFilename()));
            String fileName = UUID.randomUUID() + "-" + sanitizedFileName + ".pdf";
            String filePath = folder + "/" + fileName;

            // Ensure URL doesn't end with slash
            String baseUrl = supabaseUrl.trim().endsWith("/") ? supabaseUrl.trim().substring(0, supabaseUrl.trim().length() - 1) : supabaseUrl.trim();
            
            // Since filename is sanitized, we can use it directly in the URL path
            // Supabase Storage API expects the path as-is in the URL
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s", baseUrl, bucket, filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setBearerAuth(supabaseServiceKey);
            headers.set("apikey", supabaseServiceKey); // Supabase requires both Authorization and apikey headers
            headers.set("x-upsert", "true");

            HttpEntity<byte[]> request = new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.PUT,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Construct public URL
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", baseUrl, bucket, filePath);
                log.info("Successfully uploaded PDF to Supabase: {}", publicUrl);
                return publicUrl;
            } else {
                throw new RuntimeException("Failed to upload PDF to Supabase: " + response.getStatusCode());
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 404) {
                String errorBody = e.getResponseBodyAsString();
                log.error("Supabase API error: {} {} {} {}", errorBody, supabaseUrl, supabaseServiceKey, bucket);
                if (errorBody != null) {
                    if (errorBody.contains("Bucket not found")) {
                        log.error("Supabase bucket '{}' not found. Please create it in your Supabase dashboard.", bucket);
                        throw new RuntimeException(
                            String.format(
                                "Supabase bucket '%s' not found. Please create it in your Supabase dashboard:\n" +
                                "1. Go to https://supabase.com/dashboard\n" +
                                "2. Select your project\n" +
                                "3. Go to Storage section\n" +
                                "4. Click 'New bucket'\n" +
                                "5. Name it '%s'\n" +
                                "6. Make it PUBLIC (toggle 'Public bucket' to ON)\n" +
                                "7. Click 'Create bucket'",
                                bucket, bucket
                            )
                        );
                    } else if (errorBody.contains("Object not found") || errorBody.contains("not_found")) {
                        throw new RuntimeException(
                            String.format(
                                "Supabase Storage error: Object not found. This might mean:\n" +
                                "1. The bucket '%s' doesn't exist - create it in Supabase Dashboard > Storage\n" +
                                "2. The bucket exists but is not PUBLIC - make it public in bucket settings\n" +
                                "3. Check your SUPABASE_URL and SUPABASE_SERVICE_KEY in .env file\n" +
                                "Upload URL attempted:",
                                bucket
                            )
                        );
                    }
                }
            }
            log.error("Failed to upload PDF to Supabase: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to upload PDF to Supabase: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to upload PDF to Supabase", e);
            throw new RuntimeException("Failed to upload PDF to Supabase: " + e.getMessage(), e);
        }
    }

    public String uploadPdfBytes(byte[] pdfBytes, String folder, String filename) {
        try {
            validateConfiguration();
            
            // Sanitize filename to only use safe characters (alphanumeric, hyphens, underscores)
            String sanitizedFileName = sanitizeFileName(getFileNameWithoutExtension(filename));
            String fileName = UUID.randomUUID() + "-" + sanitizedFileName + ".pdf";
            String filePath = folder + "/" + fileName;

            // Ensure URL doesn't end with slash
            String baseUrl = supabaseUrl.trim().endsWith("/") ? supabaseUrl.trim().substring(0, supabaseUrl.trim().length() - 1) : supabaseUrl.trim();
            
            // Since filename is sanitized, we can use it directly in the URL path
            // Supabase Storage API expects the path as-is in the URL
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s", baseUrl, bucket, filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setBearerAuth(supabaseServiceKey);
            headers.set("apikey", supabaseServiceKey); // Supabase requires both Authorization and apikey headers
            headers.set("x-upsert", "true");

            ByteArrayResource resource = new ByteArrayResource(pdfBytes) {
                @Override
                public long contentLength() {
                    return pdfBytes.length;
                }
            };

            HttpEntity<ByteArrayResource> request = new HttpEntity<>(resource, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.PUT,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Construct public URL
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", baseUrl, bucket, filePath);
                log.info("Successfully uploaded PDF bytes to Supabase: {}", publicUrl);
                return publicUrl;
            } else {
                throw new RuntimeException("Failed to upload PDF to Supabase: " + response.getStatusCode());
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 404) {
                String errorBody = e.getResponseBodyAsString();
                if (errorBody != null && errorBody.contains("Bucket not found")) {
                    log.error("Supabase bucket '{}' not found. Please create it in your Supabase dashboard.", bucket);
                    throw new RuntimeException(
                        String.format(
                            "Supabase bucket '%s' not found. Please create it in your Supabase dashboard:\n" +
                            "1. Go to https://supabase.com/dashboard\n" +
                            "2. Select your project\n" +
                            "3. Go to Storage section\n" +
                            "4. Click 'New bucket'\n" +
                            "5. Name it '%s'\n" +
                            "6. Make it PUBLIC (toggle 'Public bucket' to ON)\n" +
                            "7. Click 'Create bucket'",
                            bucket, bucket
                        )
                    );
                }
            }
            log.error("Failed to upload PDF bytes to Supabase: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to upload PDF to Supabase: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to upload PDF bytes to Supabase", e);
            throw new RuntimeException("Failed to upload PDF to Supabase: " + e.getMessage(), e);
        }
    }

    public byte[] downloadPdf(String fileUrl) throws IOException {
        try {
            // Supabase public URLs are directly accessible via HTTP
            // For public buckets, we can download directly without auth
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity;
            
            if (fileUrl.contains("/public/")) {
                // Public URL - no auth needed, use empty entity
                entity = new HttpEntity<>(headers);
            } else {
                // Private URL - use service key for authentication
                headers.setBearerAuth(supabaseServiceKey);
                entity = new HttpEntity<>(headers);
            }

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    fileUrl,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully downloaded PDF from Supabase: {} ({} bytes)", fileUrl, response.getBody().length);
                return response.getBody();
            } else {
                throw new IOException("Failed to download PDF: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to download PDF from Supabase: {}", fileUrl, e);
            throw new IOException("Failed to download PDF from Supabase: " + e.getMessage(), e);
        }
    }

    private String getFileNameWithoutExtension(String filename) {
        if (filename == null) {
            return "file";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(0, lastDot);
        }
        return filename;
    }

    /**
     * Sanitizes a filename to only contain safe characters for Supabase Storage.
     * Removes or replaces special characters with hyphens.
     * 
     * @param filename The original filename
     * @return Sanitized filename with only alphanumeric characters, hyphens, and underscores
     */
    private String sanitizeFileName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "file";
        }
        // Replace all non-alphanumeric characters (except hyphens and underscores) with hyphens
        // Also replace spaces with hyphens
        String sanitized = filename.replaceAll("[^a-zA-Z0-9_-]", "-")
                .replaceAll("\\s+", "-")  // Replace spaces with hyphens
                .replaceAll("-+", "-")    // Replace multiple consecutive hyphens with single hyphen
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
        
        // Limit length to 200 characters to avoid issues
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }
        
        return sanitized.isEmpty() ? "file" : sanitized;
    }
}
