package com.gunnarro.android.simplepass;

import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import org.junit.Assert;
import org.junit.Test;

public class CustomPasswordValidatorTest {

    @Test
    public void passwordStrengthInvalid() {
        Assert.assertEquals("[INSUFFICIENT_UPPERCASE, INSUFFICIENT_DIGIT]",
                CustomPasswordValidator.passwordStrength("my-password").toString());
    }

    @Test
    public void passwordStrengthValid() {
        Assert.assertEquals("[]",
                CustomPasswordValidator.passwordStrength("my-password-A-12").toString());
    }
}
