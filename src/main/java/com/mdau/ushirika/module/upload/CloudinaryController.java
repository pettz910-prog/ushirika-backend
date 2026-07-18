package com.mdau.ushirika.module.upload;

import com.mdau.ushirika.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    /**
     * Returns signed Cloudinary upload parameters for direct browser-to-Cloudinary upload.
     * Requires authentication — any logged-in member may upload.
     */
    @GetMapping("/sign")
    public ResponseEntity<ApiResponse<CloudinarySignedParams>> sign(
            @RequestParam(defaultValue = "ushirika/claims") String folder) {
        return ResponseEntity.ok(ApiResponse.ok(cloudinaryService.signUpload(folder)));
    }
}
