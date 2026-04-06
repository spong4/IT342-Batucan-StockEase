package edu.cit.batucan.StockEase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Adapter Pattern - ApiResponse
 * Adapts different types of data (success data, error details) into
 * a single unified response format expected by all API consumers
 * (web frontend, mobile app).
 *
 * Before Adapter: Controllers returned raw objects with inconsistent formats
 * After Adapter: All responses follow { success, data, error, timestamp } structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Boolean success;
    private T data;
    private ErrorDetails error;
    private LocalDateTime timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String message;
        private Object details;
    }

    /**
     * Adapter method - adapts any success data T into standard ApiResponse format
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Adapter method - adapts error details into standard ApiResponse format
     */
    public static <T> ApiResponse<T> error(String code, String message, Object details) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Adapter method - adapts simple error message into standard ApiResponse format
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return error(code, message, null);
    }
}
