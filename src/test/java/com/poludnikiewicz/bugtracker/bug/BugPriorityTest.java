package com.poludnikiewicz.bugtracker.bug;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BugPriorityTest {

    @ParameterizedTest
    @CsvSource({"p1, BugPriority.P1_CRITICAL", "critical, BugPriority.P1_CRITICAL", "p1_CRITICAL, BugPriority.P1_CRITICAL",
            "imPorTaNT, BugPriority.P2_IMPORTANT", "p2_imPorTaNT, BugPriority.P2_IMPORTANT", "p2, BugPriority.P2_IMPORTANT",
            "norMAL, BugPriority.P3_NORMAL", "p3, BugPriority.P3_NORMAL", "p3_norMAL, BugPriority.P3_NORMAL",
            "p4, BugPriority.P4_MARGINAL", "MARGINAL, BugPriority.P4_MARGINAL", "p4_MARGINAL, BugPriority.P4_MARGINAL",
            "p5, BugPriority.P5_REDUNDANT", "redundant, BugPriority.P5_REDUNDANT", "p5_ReduNDant, BugPriority.P5_REDUNDANT"})
    void sanitizePriorityInput_should_return_correct_BugPriority_enum(String input, @ConvertWith(StringArgConverter.class) BugPriority expected) {
        BugPriority actual = BugPriority.sanitizePriorityInput(input);
        assertEquals(expected, actual);
    }

    @Test
    void sanitizePriorityInput_should_throw_exception_upon_incorrect_form_of_arg() {
        BugPriority expected = BugPriority.UNSET;
        BugPriority actual = BugPriority.sanitizePriorityInput("p1_crucial");
        assertEquals(expected, actual);
    }

    static class StringArgConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            if (source.equals("BugPriority.P1_CRITICAL")) {
                return BugPriority.P1_CRITICAL;
            } else if (source.equals("BugPriority.P2_IMPORTANT")) {
                return BugPriority.P2_IMPORTANT;
            } else if (source.equals("BugPriority.P3_NORMAL")) {
                return BugPriority.P3_NORMAL;
            } else if (source.equals("BugPriority.P4_MARGINAL")) {
                return BugPriority.P4_MARGINAL;
            } else if (source.equals("BugPriority.P5_REDUNDANT")) {
                return BugPriority.P5_REDUNDANT;
            }
            throw new UnsupportedOperationException("Incorrect string value of source");
        }
    }
}