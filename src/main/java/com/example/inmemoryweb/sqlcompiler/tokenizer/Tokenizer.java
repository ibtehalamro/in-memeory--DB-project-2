package com.example.inmemoryweb.sqlcompiler.tokenizer;

import java.util.List;

public interface Tokenizer {
    List<String> getTokens(String input);
}
