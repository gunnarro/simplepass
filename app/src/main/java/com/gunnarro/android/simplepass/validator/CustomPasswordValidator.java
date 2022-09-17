package com.gunnarro.android.simplepass.validator;

import android.graphics.Color;

import com.gunnarro.android.simplepass.utility.AESCrypto;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;
import org.passay.WhitespaceRule;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ref https://www.passay.org/reference/
 * check password strength https://www.passwordmonster.com/
 */
public class CustomPasswordValidator {

    enum PasswordStrengthEnum {
        WEAK(Color.RED), FAIR(Color.MAGENTA), GOOD(Color.BLUE), STRONG(Color.GREEN);

        private final int color;

        PasswordStrengthEnum(int color) {
            this.color = color;
        }
    }

    private static final PasswordValidator passwordValidator;
    static {
        passwordValidator = new org.passay.PasswordValidator(Arrays.asList(
                // length between 8 and 16 characters
                new LengthRule(8, 30),
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // define some illegal sequences that will fail when >= 5 chars long
                // alphabetical is of the form 'abcde', numerical is '34567', qwery is 'asdfg'
                // the false parameter indicates that wrapped sequences are allowed; e.g. 'xyzabc'
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                // no whitespace
                new WhitespaceRule()));
    }

    private CustomPasswordValidator() {
    }

    public static List<String> passwordStrength(String password) {
        RuleResult ruleResult = passwordValidator.validate(new PasswordData(password));
        return ruleResult.getDetails().stream().map(RuleResultDetail::getErrorCode).collect(Collectors.toList());
    }

    public static boolean isValid(String password) {
        return passwordValidator.validate(new PasswordData(password)).isValid();
    }

    public static String passwordStrengthStatus(String password) {
        return mapToPasswordStrength(passwordStrength(password));
    }

    public static String mapToPasswordStrength(List<String> passwordValidationResult) {
        if (passwordValidationResult.isEmpty()) {
            return PasswordStrengthEnum.STRONG.name();
        } else if (passwordValidationResult.size() == 1) {
            return PasswordStrengthEnum.GOOD.name();
        } else if (passwordValidationResult.size() == 2) {
            return PasswordStrengthEnum.FAIR.name();
        } else {
            return PasswordStrengthEnum.WEAK.name();
        }
    }
}
