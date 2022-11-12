package com.gunnarro.android.simplepass.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CustomPasswordValidatorTest {

    CustomPasswordValidator customPasswordValidator;
    @BeforeEach
    public void init() {
        customPasswordValidator = new CustomPasswordValidator();
    }

    @Test
    public void passwordStrengthInvalid() {
        List<String> result = CustomPasswordValidator.passwordStrength("swor_dwwewe");
        assertEquals("[INSUFFICIENT_UPPERCASE, INSUFFICIENT_DIGIT]", result.toString());
        assertEquals("FAIR", CustomPasswordValidator.mapToPasswordStrength(result));
    }

    @Test
    public void passwordStrengthInvalidBrokenRuleParameters() {
        List<String> result = CustomPasswordValidator.passwordStrengthValidation("sword");
        assertEquals("[TOO_SHORT: minimumLength=8, maximumLength=30, INSUFFICIENT_UPPERCASE: minimumRequired=1, INSUFFICIENT_DIGIT: minimumRequired=1, INSUFFICIENT_SPECIAL: minimumRequired=1]", result.toString());
    }

    @Test
    public void passwordStrengthValid() {
        List<String> result = CustomPasswordValidator.passwordStrength("my-password-A-12");
        assertEquals("[]", result.toString());
        assertEquals("STRONG", CustomPasswordValidator.mapToPasswordStrength(result));
    }

    @Test
    public void reConfigureValidationLengthRule() {
        assertTrue(CustomPasswordValidator.passwordStrength("qA-sr1lR").isEmpty());
        customPasswordValidator.reConfigureLengthRule(10, 30);
        assertEquals("[TOO_SHORT]", CustomPasswordValidator.passwordStrength("qA-sr1lR").toString());
    }

    @Test
    public void reConfigureValidationLowerCaseRule() {
        assertTrue(CustomPasswordValidator.passwordStrength("qA-sr1lR").isEmpty());
        customPasswordValidator.reConfigureLowerCaseRule(5);
        assertEquals("[INSUFFICIENT_LOWERCASE]", CustomPasswordValidator.passwordStrength("qA-sr1lR").toString());
    }

    @Test
    public void reConfigureValidationUpperCaseRule() {
        assertTrue(CustomPasswordValidator.passwordStrength("qA-sr1lR").isEmpty());
        customPasswordValidator.reConfigureUpperCaseRule(3);
        assertEquals("[INSUFFICIENT_UPPERCASE]", CustomPasswordValidator.passwordStrength("qA-sr1lR").toString());
    }
}
