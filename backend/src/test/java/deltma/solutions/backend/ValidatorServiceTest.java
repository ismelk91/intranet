package deltma.solutions.backend;

import deltma.solutions.backend.services.ValidatorService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorServiceTest {

    private final ValidatorService validatorService = new ValidatorService();

    // Email validation tests
    @Test
    void validateEmail_ValidEmail_NoExceptionThrown() {
        String validEmail = "test@example.com";
        assertDoesNotThrow(() -> validatorService.validateEmail(validEmail));
    }

    @Test
    void validateEmail_InvalidEmail_ThrowsException() {
        String invalidEmail = "invalidemail";
        assertThrows(IllegalArgumentException.class, () -> validatorService.validateEmail(invalidEmail));
    }

    // Password validation tests
    @Test
    void validatePassword_ValidPassword_NoExceptionThrown() {
        String validPassword = "!StRoNgP@ssWorD123";
        assertDoesNotThrow(() -> validatorService.validatePassword(validPassword));
    }

    @Test
    void validatePassword_InvalidPassword_ThrowsException() {
        String invalidPassword = "weakpassword";
        assertThrows(IllegalArgumentException.class, () -> validatorService.validatePassword(invalidPassword));
    }

    // First Name validation tests
    @Test
    void validateFirstName_ValidInput_NoExceptionThrown() {
        String validInput = "Jakob";
        String fieldName = "First Name";
        assertDoesNotThrow(() -> validatorService.validateString(validInput, fieldName));
    }

    @Test
    void validateFirstName_NullInput_ThrowsException() {
        String invalidInput = null;
        String fieldName = "First Name";
        assertThrows(IllegalArgumentException.class, () -> validatorService.validateString(invalidInput, fieldName));
    }

    // Last Name validation tests
    @Test
    void validateLastName_ValidInput_NoExceptionThrown() {
        // Arrange
        String validInput = "Pietrzyk";
        String fieldName = "Last Name";
        assertDoesNotThrow(() -> validatorService.validateString(validInput, fieldName));
    }

    @Test
    void validateLastName_NullInput_ThrowsException() {
        String invalidInput = null;
        String fieldName = "Last Name";
        assertThrows(IllegalArgumentException.class, () -> validatorService.validateString(invalidInput, fieldName));
    }

    @Test
    public void testInvalidNameShort() {
        String invalidName = "A";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validatorService.validateFirstName(invalidName));
        assertEquals("Invalid name format", exception.getMessage());
    }

    @Test void testInvalidNameLongerThan32() {
        String invalidName = "AAAAAAAAAABBBBBBBBBBCCCCCCCCCCDDDDDDDDDD";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validatorService.validateFirstName(invalidName));
    }

    // Phone Number validation tests
    @Test
    void validatePhoneNumber_ValidInput_NoExceptionThrown() {
        String validInput = "12345678";
        assertDoesNotThrow(() -> validatorService.validatePhoneNumber(validInput));
    }

    @Test
    void validatePhoneNumber_NullInput_ThrowsException() {
        String invalidInput = null;
        assertThrows(IllegalArgumentException.class, () -> validatorService.validatePhoneNumber(invalidInput));
    }

    @Test
    void validatePhoneNumber_EmptyInput_ThrowsException() {
        String invalidInput = "";
        assertThrows(IllegalArgumentException.class, () -> validatorService.validatePhoneNumber(invalidInput));
    }

    // User validation tests
    /*@Test
    void validateUser_NullUser_ThrowsException() {
        User user = null;
        assertThrows(IllegalArgumentException.class, () -> validatorService.validateUser(user));
    }

    @Test
    void validateUser_ValidUser_NoExceptionThrown() {
        User user = User.builder()
                .email("test@example.com")
                .firstName("Jakob")
                .lastName("Pietrzyk")
                .password("password")
                .phoneNumber("123456789")
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();
        assertDoesNotThrow(() -> validatorService.validateUser(user));
    }*/

    // Integer validation tests
    @Test
    void validateInteger_ValidInput_NoExceptionThrown() {
        Integer validInput = 42;
        String fieldName = "Phone Number";
        assertDoesNotThrow(() -> validatorService.validateInteger(validInput, fieldName));
    }

    // more
}


