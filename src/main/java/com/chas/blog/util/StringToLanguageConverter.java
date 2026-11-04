package com.chas.blog.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.chas.blog.model.post.Language;

@Component 
public class StringToLanguageConverter implements Converter<String, Language> {

    @Override
    public Language convert(String source) {
        return Language.fromCode(source);
    }

}
