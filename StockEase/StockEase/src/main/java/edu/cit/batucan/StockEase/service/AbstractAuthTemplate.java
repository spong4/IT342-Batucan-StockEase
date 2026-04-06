package edu.cit.batucan.StockEase.service;

import edu.cit.batucan.StockEase.dto.AuthResponse;

/**
 * Template Method Pattern - AbstractAuthTemplate
 * Defines the skeleton of authentication operations.
 * Both register and login follow the same steps:
 * 1. Validate input
 * 2. Process (save user OR verify credentials)
 * 3. Generate tokens
 * 4. Build and return response
 *
 * Subclasses override specific steps without changing the overall flow.
 */
public abstract class AbstractAuthTemplate<T> {

    /**
     * Template method - defines the fixed skeleton of auth operations
     * This method cannot be overridden (final)
     */
    public final AuthResponse execute(T request) {
        validateInput(request);       // Step 1 - validate
        processRequest(request);      // Step 2 - process
        return buildResponse(request); // Step 3 - build response
    }

    // Steps that subclasses must implement
    protected abstract void validateInput(T request);
    protected abstract void processRequest(T request);
    protected abstract AuthResponse buildResponse(T request);
}
