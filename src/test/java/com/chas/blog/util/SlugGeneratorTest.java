package com.chas.blog.util;

import org.junit.jupiter.api.Test;

public class SlugGeneratorTest {

    @Test 
    void testSlugGeneration() {
        String title = "Título de Teste com Acentos e Espaços";
        String expectedSlug = "titulo-de-teste-com-acentos-e-espacos";
        String actualSlug = SlugGenerator.fromTitle(title);
        assert actualSlug.equals(expectedSlug) : "Expected: " + expectedSlug + ", but got: " + actualSlug;
    }

}
