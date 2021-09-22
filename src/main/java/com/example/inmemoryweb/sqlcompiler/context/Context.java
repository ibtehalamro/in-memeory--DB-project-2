package com.example.inmemoryweb.sqlcompiler.context;


import com.example.inmemoryweb.databasestructure.QueryResult;


 public interface Context {
      QueryResult execute();
}
