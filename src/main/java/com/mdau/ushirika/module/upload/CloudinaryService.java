package com.mdau.ushirika.module.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class CloudinaryService {

    @Value("${app.cloudinary.cloud-name}")
    private String cloudName;

    @Value("${app.cloudinary.api-key}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret}")
    private String apiSecret;

    /**
     * Generates Cloudinary signed upload parameters for direct browser-to-Cloudinary upload.
     * The signature covers folder + timestamp, which is all the frontend will send.
     */
    public CloudinarySignedParams signUpload(String folder) {
        long timestamp = System.currentTimeMillis() / 1000L;
        // Cloudinary signature string: alphabetical params joined by & then appended with api_secret
        String toSign = "folder=" + folder + "&timestamp=" + timestamp + apiSecret;
        String signature = sha1Hex(toSign);
        return new CloudinarySignedParams(cloudName, apiKey, timestamp, signature, folder);
    }

    private static String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(40);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 not available", e);
        }
    }
}
