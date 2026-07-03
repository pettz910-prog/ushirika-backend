package com.mdau.ushirika.module.contact.dto;

import java.util.UUID;

/** Minimal response returned to the public after a message is submitted. */
public record ContactMessageSubmittedDto(
        UUID   id,
        String referenceCode
) {}
