package ru.alfaintegral.deviceProduction.exceptions;

public class GeneralException extends GlobalAppException {
    public GeneralException(int status, String message) {
        super(status, message);
    }
}
