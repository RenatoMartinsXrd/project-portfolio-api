package com.projectportfolio.shared.exception;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(OffsetDateTime timestamp, int status, String message, List<String> details) {
}
