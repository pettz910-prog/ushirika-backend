package com.mdau.ushirika.module.upload;

public record CloudinarySignedParams(
        String cloudName,
        String apiKey,
        long   timestamp,
        String signature,
        String folder
) {}
