package com.example.inmemoryweb.databasestructure;

import com.example.inmemoryweb.Configuration.CrudCommands;

public interface Factory<T> {

    T getObjectFromFactory(CrudCommands crudCommands);
}
