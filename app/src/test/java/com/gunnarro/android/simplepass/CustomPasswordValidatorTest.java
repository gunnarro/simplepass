package com.gunnarro.android.simplepass;

import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import java.util.List;

public class CustomPasswordValidatorTest {

    @Test
    public void passwordStrengthInvalid() {
        List<String> result = CustomPasswordValidator.passwordStrength("swor_dwwewe");
        Assert.assertEquals("[INSUFFICIENT_UPPERCASE, INSUFFICIENT_DIGIT]", result.toString());
        Assert.assertEquals("FAIR", CustomPasswordValidator.mapToPasswordStrength(result));
    }

    @Test
    public void passwordStrengthInvalidBrokenRuleParameters() {
        List<String> result = CustomPasswordValidator.passwordStrengthValidation("sword");
        Assert.assertEquals("[TOO_SHORT: minimumLength=8, maximumLength=30, INSUFFICIENT_UPPERCASE: minimumRequired=1, INSUFFICIENT_DIGIT: minimumRequired=1, INSUFFICIENT_SPECIAL: minimumRequired=1]", result.toString());
    }

    @Test
    public void passwordStrengthValid() {
        List<String> result = CustomPasswordValidator.passwordStrength("my-password-A-12");
        Assert.assertEquals("[]", result.toString());
        Assert.assertEquals("STRONG", CustomPasswordValidator.mapToPasswordStrength(result));
    }

    @Test
    public void getRules() {
        //CustomPasswordValidator.getActiveRules().forEach( r -> System.out.println(r.));
      //  Assert.assertEquals("STRONG", CustomPasswordValidator.getActiveRules());
    }
}
