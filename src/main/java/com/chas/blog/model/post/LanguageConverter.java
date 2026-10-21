package com.chas.blog.model.post;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, String> {

    @Override
    public String convertToDatabaseColumn(Language language) {
        return language.getCode();
    }

    @Override
    public Language convertToEntityAttribute(String code) {
        return Language.fromCode(code);
    }

}
