package com.chas.blog.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class SlugGeneratorTest {

    @ParameterizedTest
    @CsvSource({
            "Jello World, jello-world",
            "This is a test., this-is-a-test",
            "Slug with special characters: @#$%^&*(), slug-with-special-characters",
            "'Multiple    spaces', multiple-spaces",
            "Mixed CASE and Numbers 123, mixed-case-and-numbers-123",
            "Accented characters: áéíóú, accented-characters-aeiou",
            "\'Non-Latin characters: 你好, мир, مرحبا\' ,non-latin-characters",
            "- Starts with hyphen, starts-with-hyphen",
            "Ends with hyphen -, ends-with-hyphen",
            "Double--hyphen, double-hyphen",
            "Special characters: !@#$%^&*()_+, special-characters",
            "Multiple spaces    in the middle, multiple-spaces-in-the-middle",
            "'Trailing spaces   ', trailing-spaces",
            "'Leading spaces   ', leading-spaces",
            "Mixed CASE and Numbers 123, mixed-case-and-numbers-123",
    })
    void testSlugGeneration(String title, String expectedSlug) {

        assertThat(SlugGenerator.fromTitle(title)).isEqualTo(expectedSlug);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Привет мир", "!!!", "   " })
    void rejectsTitleWithoutLatinCharacters(String title) {
        assertThatThrownBy(() -> SlugGenerator.fromTitle(title))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be converted to a valid slug: "+ title);
    }

}
