package com.gunnarro.android.simplepass.validator;

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

    public static int passwordStrengthNumber(String password) {
        RuleResult ruleResult = passwordValidator.validate(new PasswordData(password));
        return ruleResult.getDetails().size();
    }

    public static List<String> passwordStrength(String password) {
        RuleResult ruleResult = passwordValidator.validate(new PasswordData(password));
        return ruleResult.getDetails().stream().map(RuleResultDetail::getErrorCode).collect(Collectors.toList());
    }

    public static boolean isValid(String password) {
        return passwordValidator.validate(new PasswordData(password)).isValid();
    }
}
