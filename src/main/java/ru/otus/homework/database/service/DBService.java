package ru.otus.homework.database.service;

import ru.otus.homework.ms_client.model.User;

import java.util.List;

public interface DBService {

    long saveUser(User user);

    List<User> getAllUsers();
}
