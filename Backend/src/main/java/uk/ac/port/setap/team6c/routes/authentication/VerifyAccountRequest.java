package uk.ac.port.setap.team6c.routes.authentication;

public record VerifyAccountRequest(String accountIdentifier, String verificationCode) {
}
