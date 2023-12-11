package com.kursinis.ptkursinis.helpers;

public class StringHelper {
    public static String camelCaseConverter(String input) {
            if (input == null || input.isEmpty()) {
                return input;
            }
            String[] words = input.split("[\\s_]+");
            StringBuilder camelCase = new StringBuilder(words[0].toLowerCase());

            for (int i = 1; i < words.length; i++) {
                String word = words[i];
                if (!word.isEmpty()) {
                    camelCase.append(word.substring(0, 1).toUpperCase());
                    if (word.length() > 1) {
                        camelCase.append(word.substring(1));
                    }
                }
            }
            return camelCase.toString();
        }
}
