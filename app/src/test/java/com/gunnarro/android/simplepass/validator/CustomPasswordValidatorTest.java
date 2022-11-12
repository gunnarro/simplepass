package com.gunnarro.android.simplepass.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class CustomPasswordValidatorTest {

    CustomPasswordValidator customPasswordValidator;
    @BeforeEach
    void init() {
        customPasswordValidator = new CustomPasswordValidator();
    }

    @Test
    void passwordStrengthInvalid() {
        List<String> result = customPasswordValidator.passwordStrength("swor_dwwewe");
        assertEquals("[INSUFFICIENT_UPPERCASE, INSUFFICIENT_DIGIT]", result.toString());
        assertEquals("FAIR", CustomPasswordValidator.mapToPasswordStrength(result));
    }

    @Test
    void passwordStrengthInvalidBrokenRuleParameters() {
        List<String> result = customPasswordValidator.passwordStrengthValidation("sword");
        assertEquals("[TOO_SHORT: minimumLength=8, maximumLength=30, INSUFFICIENT_UPPERCASE: minimumRequired=1, INSUFFICIENT_DIGIT: minimumRequired=1, INSUFFICIENT_SPECIAL: minimumRequired=1]", result.toString());
    }

    @Test
    void passwordStrengthValid() {
        List<String> result = customPasswordValidator.passwordStrength("my-password-A-12");
        assertEquals("[]", result.toString());
        assertEquals("STRONG", CustomPasswordValidator.mapToPasswordStrength(result));
    }

    @Test
    void reConfigureValidationLengthRule() {
        assertTrue(customPasswordValidator.passwordStrength("qA-sr1lR").isEmpty());
        customPasswordValidator.reConfigureLengthRule(10, 30);
        assertEquals("[TOO_SHORT]", customPasswordValidator.passwordStrength("qA-sr1lR").toString());
    }

    @Test
    void reConfigureValidationLowerCaseRule() {
        assertTrue(customPasswordValidator.passwordStrength("qA-sr1lR").isEmpty());
        customPasswordValidator.reConfigureLowerCaseRule(5);
        assertEquals("[INSUFFICIENT_LOWERCASE]", customPasswordValidator.passwordStrength("qA-sr1lR").toString());
    }

    @Test
    void reConfigureValidationUpperCaseRule() {
        assertTrue(customPasswordValidator.passwordStrength("qA-sr1lR").isEmpty());
        customPasswordValidator.reConfigureUpperCaseRule(3);
        assertEquals("[INSUFFICIENT_UPPERCASE]", customPasswordValidator.passwordStrength("qA-sr1lR").toString());
    }
}
