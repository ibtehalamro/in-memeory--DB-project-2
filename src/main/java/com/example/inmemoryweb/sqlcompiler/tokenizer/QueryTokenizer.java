package com.example.inmemoryweb.sqlcompiler.tokenizer;

import com.example.inmemoryweb.Exceptions.TokenizerException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;

@Component("QueryTokenizer")
public class QueryTokenizer implements Tokenizer {

    private final BiPredicate<String, Integer> isCharIndexInRange = (query, charIndex) -> query.length() > charIndex;
    private final ToIntBiFunction<String, Integer> getNextQuoteIndex = (toBeTokenized, charIndex) -> {
        int index = toBeTokenized.indexOf('\'', charIndex + 1);
        if (isCharIndexInRange.test(toBeTokenized, index) && index > 0) {
            return index;
        } else {
            throw new TokenizerException("index out of bound, cannot find matching quote");
        }
    };
    private final BiFunction<String, Integer, String> getCharactersBetweenQuotes = (query, index) -> {
        int indexOfQuote = this.getNextQuoteIndex.applyAsInt(query, index);
        return query.substring(index + 1, indexOfQuote);
    };

    @Override
    public List<String> getTokens(String query) {
        String tokensQuery = prepareQueryToBeTokenized(query);

        return getQueryTokens(tokensQuery);
    }

    private String prepareQueryToBeTokenized(String query) {
        String queryToTokenize = query.trim();
        queryToTokenize += " ";
        return queryToTokenize;
    }

    public List<String> getQueryTokens(String query) {
        List<String> tokens = new ArrayList<>();
        int charIndex = 0;
        StringBuilder word = new StringBuilder();

        while (isCharIndexInRange.test(query, charIndex)) {

            char c = query.charAt(charIndex);

            if (c == '\'') {//begin of string
                String charactersBetweenQuotes;

                try {
                    charactersBetweenQuotes = getCharactersBetweenQuotes.apply(query, charIndex);
                } catch (Exception e) {
                    tokens.clear();
                    tokens.add(" ");
                    break;
                }

                tokens.add(charactersBetweenQuotes);
                charIndex += charactersBetweenQuotes.length() + 2;

            } else {
                if (c == ' ') {
                    if (word.length() > 0) {
                        tokens.add(word.toString());
                        word = new StringBuilder();
                    }
                    charIndex++;
                    continue;
                } else if (c == ',' || c == '(' || c == ')') {
                    charIndex++;
                    continue;
                }
                word.append(c);
                charIndex++;
            }
        }
        return tokens;
    }
}






