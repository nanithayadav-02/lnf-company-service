package com.lnf.company.exception;

import com.lnf.exception.*;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class LnFGlobalExceptionHandler {

    // ========================
    // REGEX PATTERNS
    // ========================
    private static final Pattern DUPLICATE_KEY_PATTERN = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\)");
    private static final Pattern FOREIGN_KEY_PATTERN = Pattern.compile("violates foreign key constraint \"(.*?)\"");
    private static final Pattern NULL_VALUE_PATTERN = Pattern.compile("null value in column \"(.*?)\"");
    private static final Pattern NULL_POINTER_FIELD_PATTERN = Pattern.compile("because \"(.*?)\" is null");
    private static final Pattern VALUE_TOO_LONG_PATTERN = Pattern.compile("value too long for type (\\w+)\\((\\d+)\\)");

    // ========================
    // CUSTOM EXCEPTIONS
    // ========================

    /**
     * Handles entity not found exceptions
     */
    @ExceptionHandler(LnFEntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFound(LnFEntityNotFoundException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request, false);
    }

    /**
     * Handles bad request exceptions
     */
    @ExceptionHandler(LnFBadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(LnFBadRequestException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request, false);
    }

    /**
     * Handles service unavailable exceptions
     */
    @ExceptionHandler(LnFServiceUnavailableException.class)
    public ResponseEntity<ApiResponse> handleServiceUnavailable(LnFServiceUnavailableException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.SERVICE_UNAVAILABLE, request, false);
    }

    /**
     * Handles generic LnF exceptions
     */
    @ExceptionHandler(LnFException.class)
    public ResponseEntity<ApiResponse> handleGenericLnFException(LnFException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request, true);
    }

    // ========================
    // DATABASE & PERSISTENCE
    // ========================

    /**
     * Handles data integrity violation exceptions
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String rootMsg = Optional.ofNullable(ex.getRootCause())
                .map(Throwable::getMessage)
                .orElse(ex.getMessage());

        String message = extractFriendlyMessageFromDatabase(rootMsg);
        log.debug("Database error: {}", rootMsg);

        return buildResponse(new LnFBadRequestException(message), HttpStatus.BAD_REQUEST, request, false);
    }

    /**
     * Handles constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String errorMsg = ex.getConstraintViolations().stream()
                .map(violation -> formatFieldName(violation.getPropertyPath().toString()) + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        LnFBadRequestException lnfEx = new LnFBadRequestException("Validation error: " + errorMsg);
        return buildResponse(lnfEx, HttpStatus.BAD_REQUEST, request, false);
    }

    /**
     * Handles JPA system exceptions and persistence exceptions
     */
    @ExceptionHandler({JpaSystemException.class, PersistenceException.class})
    public ResponseEntity<ApiResponse> handleJpaExceptions(RuntimeException ex, WebRequest request) {
        String message = Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse(ex.getMessage());

        LnFException lnfEx = new LnFException("Database operation failed: " + message, ex);
        log.error("JPA/Persistence error: {}", message, ex);

        return buildResponse(lnfEx, HttpStatus.INTERNAL_SERVER_ERROR, request, true);
    }

    /**
     * Handles locking exceptions
     */
    @ExceptionHandler({OptimisticLockingFailureException.class, PessimisticLockingFailureException.class})
    public ResponseEntity<ApiResponse> handleLockingExceptions(RuntimeException ex, WebRequest request) {
        LnFException lnfEx = new LnFException("Concurrency conflict: " + ex.getMessage(), ex);
        log.warn("Locking exception: {}", ex.getMessage());

        return buildResponse(lnfEx, HttpStatus.CONFLICT, request, false);
    }

    /**
     * Handles transaction exceptions
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ApiResponse> handleTransactionException(TransactionException ex, WebRequest request) {
        LnFException lnfEx = new LnFException("Transaction failed: " + ex.getMessage(), ex);
        log.error("Transaction error: {}", ex.getMessage(), ex);

        return buildResponse(lnfEx, HttpStatus.INTERNAL_SERVER_ERROR, request, true);
    }

    // ========================
    // VALIDATION
    // ========================

    /**
     * Handles validation exceptions for @Valid annotated request bodies
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> formatFieldName(error.getField()) + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        LnFBadRequestException badRequestEx = new LnFBadRequestException("Validation failed: " + errorMessage);
        return buildResponse(badRequestEx, HttpStatus.BAD_REQUEST, request, false);
    }

    /**
     * Handles binding exceptions
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse> handleBindException(BindException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> formatFieldName(error.getField()) + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        LnFBadRequestException badRequestEx = new LnFBadRequestException("Binding error: " + errorMessage);
        return buildResponse(badRequestEx, HttpStatus.BAD_REQUEST, request, false);
    }

    /**
     * Handles method argument type mismatch exceptions
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String paramName = formatFieldName(ex.getName());
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        String message = String.format("Parameter '%s' should be of type %s", paramName, requiredType);
        LnFBadRequestException badRequestEx = new LnFBadRequestException(message);

        return buildResponse(badRequestEx, HttpStatus.BAD_REQUEST, request, false);
    }

    // ========================
    // WEB & HTTP
    // ========================

    /**
     * Handles HTTP method not supported exceptions
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        String supportedMethods = ex.getSupportedHttpMethods() != null ?
                ex.getSupportedHttpMethods().stream().map(HttpMethod::name).collect(Collectors.joining(", ")) : "";

        String message = String.format("Method '%s' not supported. Supported methods: %s",
                ex.getMethod(), supportedMethods);

        LnFBadRequestException lnfEx = new LnFBadRequestException(message);
        return buildResponse(lnfEx, HttpStatus.METHOD_NOT_ALLOWED, request, false);
    }

    /**
     * Handles unsupported media type exceptions
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        String supportedTypes = ex.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String message = String.format("Media type '%s' not supported. Supported types: %s",
                ex.getContentType(), supportedTypes);

        LnFBadRequestException lnfEx = new LnFBadRequestException(message);
        return buildResponse(lnfEx, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, false);
    }

    /**
     * Handles maximum upload size exceeded exceptions
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, WebRequest request) {
        String maxFileSize = ex.getMaxUploadSize() > 0 ?
                (ex.getMaxUploadSize() / (1024 * 1024)) + "MB" : "unknown";

        String message = String.format("File upload size exceeded. Maximum allowed size is %s", maxFileSize);

        LnFBadRequestException lnfEx = new LnFBadRequestException(message);
        return buildResponse(lnfEx, HttpStatus.PAYLOAD_TOO_LARGE, request, false);
    }

    /**
     * Handles 404 not found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse> handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
        String message = String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL());
        LnFEntityNotFoundException lnfEx = new LnFEntityNotFoundException(message);

        return buildResponse(lnfEx, HttpStatus.NOT_FOUND, request, false);
    }

    // ========================
    // SECURITY
    // ========================

    /**
     * Handles access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        LnFException lnfEx = new LnFException("Access denied: insufficient permissions", ex);
        log.warn("Access denied: {}", ex.getMessage());

        return buildResponse(lnfEx, HttpStatus.FORBIDDEN, request, false);
    }

    /**
     * Handles authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse> handleAuthenticationException(RuntimeException ex, WebRequest request) {
        LnFException lnfEx = new LnFException("Authentication failed", ex);
        log.warn("Authentication failed: {}", ex.getMessage());

        return buildResponse(lnfEx, HttpStatus.UNAUTHORIZED, request, false);
    }

    // ========================
    // RUNTIME EXCEPTIONS
    // ========================

    /**
     * Handles illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        LnFBadRequestException lnfEx = new LnFBadRequestException("Invalid argument: " + ex.getMessage());
        log.debug("Illegal argument: {}", ex.getMessage());

        return buildResponse(lnfEx, HttpStatus.BAD_REQUEST, request, false);
    }

    /**
     * Handles illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalState(IllegalStateException ex, WebRequest request) {
        LnFException lnfEx = new LnFException("Operation failed due to invalid application state: " + ex.getMessage(), ex);
        log.error("Illegal state: {}", ex.getMessage(), ex);

        return buildResponse(lnfEx, HttpStatus.INTERNAL_SERVER_ERROR, request, true);
    }

    /**
     * Handles unsupported operation exceptions
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiResponse> handleUnsupportedOperation(UnsupportedOperationException ex, WebRequest request) {
        LnFException lnfEx = new LnFException("This operation is not supported", ex);
        log.error("Unsupported operation: {}", ex.getMessage(), ex);

        return buildResponse(lnfEx, HttpStatus.NOT_IMPLEMENTED, request, true);
    }

    /**
     * Handles null pointer exceptions by providing user-friendly messages
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> handleNullPointerException(NullPointerException ex, WebRequest request) {
        String friendlyMsg = extractFieldFromNullPointer(ex.getMessage());
        log.error("NullPointerException: {}", ex.getMessage(), ex);

        LnFException lnfEx = new LnFException(friendlyMsg, ex);
        return buildResponse(lnfEx, HttpStatus.INTERNAL_SERVER_ERROR, request, true);
    }

    /**
     * Fallback handler for all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex, WebRequest request) {
        String message = "An unexpected error occurred";
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        LnFException lnfEx = new LnFException(message, ex);
        return buildResponse(lnfEx, HttpStatus.INTERNAL_SERVER_ERROR, request, true);
    }

    // ========================
    // HELPER METHODS
    // ========================

    /**
     * Extracts user-friendly message from database error messages
     */
    private String extractFriendlyMessageFromDatabase(String rootMsg) {
        if (rootMsg == null) {
            return "Unknown data integrity issue.";
        }

        // Map of error patterns and their handlers
        Map<Predicate<String>, Function<String, String>> errorHandlers = Map.of(
                msg -> msg.contains("duplicate key"), this::handleDuplicateKeyViolation,
                msg -> msg.contains("violates foreign key constraint"), this::handleForeignKeyViolation,
                msg -> msg.contains("null value in column"), this::handleNullValueViolation,
                msg -> msg.contains("value too long"), this::handleValueTooLongViolation,
                msg -> msg.contains("because") && msg.contains("is null"), this::handleNullPointerViolation
        );

        // Find the first matching handler and apply it
        return errorHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().test(rootMsg))
                .findFirst()
                .map(entry -> entry.getValue().apply(rootMsg))
                .orElse("Data integrity violation: " + rootMsg);
    }

    /**
     * Handles duplicate key violations
     */
    private String handleDuplicateKeyViolation(String rootMsg) {
        return extractMessage(rootMsg, DUPLICATE_KEY_PATTERN,
                matcher -> String.format("The %s '%s' already exists. Please use a different value.",
                        formatFieldName(matcher.group(1)), matcher.group(2)),
                "Duplicate entry found. A unique field already exists.");
    }

    /**
     * Handles foreign key constraint violations
     */
    private String handleForeignKeyViolation(String rootMsg) {
        return extractMessage(rootMsg, FOREIGN_KEY_PATTERN,
                matcher -> String.format("Invalid reference in field '%s'. Related data does not exist.",
                        formatFieldName(matcher.group(1).replace("fk_", ""))),
                "Invalid reference to another entity. Make sure related data exists.");
    }

    /**
     * Handles null value constraint violations
     */
    private String handleNullValueViolation(String rootMsg) {
        return extractMessage(rootMsg, NULL_VALUE_PATTERN,
                matcher -> String.format("The field '%s' is required and cannot be null.",
                        formatFieldName(matcher.group(1))),
                "A required field is missing. Please provide all required values.");
    }

    /**
     * Handles null pointer violations
     */
    private String handleNullPointerViolation(String rootMsg) {
        return extractMessage(rootMsg, NULL_POINTER_FIELD_PATTERN,
                matcher -> String.format("The field '%s' is required and cannot be null.",
                        formatFieldName(matcher.group(1).substring(matcher.group(1).lastIndexOf('.') + 1))),
                "A required field reference is null. Please check all required values.");
    }

    /**
     * Handles value too long violations
     */
    private String handleValueTooLongViolation(String rootMsg) {
        return extractMessage(rootMsg, VALUE_TOO_LONG_PATTERN,
                matcher -> String.format("Input value exceeds maximum allowed length of %s characters.",
                        matcher.group(2)),
                "Input value is too long for the field.");
    }

    /**
     * Extracts field information from null pointer exception messages
     */
    private String extractFieldFromNullPointer(String message) {
        if (message == null || message.isBlank()) {
            return "An internal error occurred due to a null value.";
        }

        return extractMessage(message, NULL_POINTER_FIELD_PATTERN,
                matcher -> {
                    String fieldPath = matcher.group(1);
                    String fieldName = fieldPath.substring(fieldPath.lastIndexOf('.') + 1);
                    return String.format(
                            "A required object '%s' was null and caused a system error. Please ensure it is properly initialized.",
                            formatFieldName(fieldName));
                },
                "A required object was null and caused a system error.");
    }

    /**
     * Helper method to extract messages using regex patterns
     */
    private String extractMessage(String input, Pattern pattern, Function<Matcher, String> successMessage, String defaultMessage) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? successMessage.apply(matcher) : defaultMessage;
    }

    /**
     * Formats technical field names to user-friendly format
     */
    private String formatFieldName(String technicalName) {
        if (technicalName == null || technicalName.isBlank()) {
            return "unknown field";
        }

        // Convert snake_case or camelCase to human-readable format
        String[] words = technicalName.split("(?=[A-Z])|_");
        return Arrays.stream(words)
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "))
                .trim();
    }

    /**
     * Builds a consistent API response
     */
    private ResponseEntity<ApiResponse> buildResponse(LnFException ex, HttpStatus status, WebRequest request, boolean includeStackTrace) {
        ApiResponse response = new ApiResponse();
        response.setError(true);
        response.setTimestamp(new Date());
        response.setStatusCode(ex.getExceptionType().getCode());
        response.setStatusMessage(ex.getMessage());
        response.setApiDetails(request.getDescription(false));

        // In production, you might want to disable stack traces or make them conditional
        if (includeStackTrace) {
            response.setStackTrace(stackTraceToString(ex));
        }

        return new ResponseEntity<>(response, status);
    }

    /**
     * Converts stack trace to string
     */
    private String stackTraceToString(Throwable ex) {
        return Arrays.stream(ex.getStackTrace())
                .limit(20) // Limit stack trace depth to avoid huge responses
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

}