package com.example.inmemoryweb.Utils;

import org.springframework.stereotype.Component;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Component
public class TokensChecker implements Utils {

    public Predicate<String> isTokenEqualFROM = (token) -> token.equals("FROM");
    public Predicate<String> isTokenEqualStar = (token) -> token.equals("*");
    public Predicate<String> isTokenEqualSET = (token) -> token.equals("SET");
    public Predicate<String> isTokenEqualWHERE = (token) -> token.equals("WHERE");
    public Predicate<String> isTokenEqualVALUES = (token) -> token.equals("VALUES");
    public Predicate<String> isTokenEqualINTO = (token) -> token.equals("INTO");
    public BiPredicate<Integer, Integer> isTokenIndexLessThanTokensCount = (index, tokensNumber) -> index < tokensNumber;
}