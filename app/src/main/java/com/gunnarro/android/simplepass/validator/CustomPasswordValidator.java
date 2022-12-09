package com.gunnarro.android.simplepass.validator;

import android.util.Log;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ref https://www.passay.org/reference/
 * check password strength https://www.passwordmonster.com/
 */
public class CustomPasswordValidator {

    private PasswordValidator passwordValidator;
    private int minPasswordLength = 8;
    private int maxPasswordLength = 30;
    private int numberOfUpperCaseChars = 1;
    private int numberOfLowerCaseChars = 1;
    private int numberOfDigits = 1;
    private int numberOfSpecialChars = 1;

    public CustomPasswordValidator() {
        configure();
        Log.d("CustomPasswordValidator", "initialized");
    }

    public void reConfigureLengthRule(int minLength, int maxLength) {
        this.minPasswordLength = minLength;
        this.maxPasswordLength = maxLength;
        configure();
    }

    public void reConfigureUpperCaseRule(int numberOfUpperCaseChars) {
        this.numberOfUpperCaseChars = numberOfUpperCaseChars;
        configure();
    }

    public void reConfigureLowerCaseRule(int numberOfLowerCaseChars) {
        this.numberOfLowerCaseChars = numberOfLowerCaseChars;
        configure();
    }

    public void reConfigureDigitRule(int numberOfDigits) {
        this.numberOfDigits = numberOfDigits;
        configure();
    }

    public void reConfigureSpecialCharsRule(int numberOfSpecialChars) {
        this.numberOfSpecialChars = numberOfSpecialChars;
        configure();
    }

    public List<String> passwordStrength(String password) {
        RuleResult ruleResult = passwordValidator.validate(new PasswordData(password));
        return ruleResult.getDetails().stream().map(RuleResultDetail::getErrorCode).collect(Collectors.toList());
    }

    public List<String> passwordStrengthValidation(String password) {
        List<String> list = new ArrayList<>();
        RuleResult ruleResult = passwordValidator.validate(new PasswordData(password));
        ruleResult.getDetails().forEach(r -> {
            String validationError = (r.getParameters().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .filter(s -> !s.contains("valid") && !s.contains("matching"))
                    .collect(Collectors.joining(", ")));
            list.add(r.getErrorCode() + ": " + validationError);
        });
        return list;
    }

    public String passwordStrengthStatus(String password) {
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

    private void configure() {
        passwordValidator = new PasswordValidator(Arrays.asList(
                // length between 8 and 16 characters
                new LengthRule(minPasswordLength, maxPasswordLength),
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, numberOfUpperCaseChars),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, numberOfLowerCaseChars),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, numberOfDigits),
                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, numberOfSpecialChars),
                // define some illegal sequences that will fail when >= 5 chars long
                // alphabetical is of the form 'abcde', numerical is '34567', qwery is 'asdfg'
                // the false parameter indicates that wrapped sequences are allowed; e.g. 'xyzabc'
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                // no whitespace
                new WhitespaceRule()));
        Log.d("CustomPasswordValidator", "reconfigured");
    }

    enum PasswordStrengthEnum {
        WEAK, FAIR, GOOD, STRONG
    }
}
