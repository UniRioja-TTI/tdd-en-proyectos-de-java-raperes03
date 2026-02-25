package com.tt1.test;

import java.util.*;

public class DBStub {

    private Map<String, ToDo> todos = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    public Map<String, ToDo> getTodos() {
        return todos;
    }

    public Set<String> getEmails() {
        return emails;
    }
}