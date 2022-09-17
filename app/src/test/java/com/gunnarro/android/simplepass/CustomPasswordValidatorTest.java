package com.gunnarro.android.simplepass;

import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CustomPasswordValidatorTest {

    @Test
    public void passwordStrengthInvalid() {
        List<String> result = CustomPasswordValidator.passwordStrength("my-password");
        Assert.assertEquals("[INSUFFICIENT_UPPERCASE, INSUFFICIENT_DIGIT]", result.toString());
        Assert.assertEquals("FAIR", CustomPasswordValidator.mapToPasswordStrength(result));
    }

    @Test
    public void passwordStrengthValid() {
        List<String> result = CustomPasswordValidator.passwordStrength("my-password-A-12");
        Assert.assertEquals("[]", result.toString());
        Assert.assertEquals("STRONG", CustomPasswordValidator.mapToPasswordStrength(result));
    }
}
