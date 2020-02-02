package ru.otus.homework.database.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ms_client.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.ArrayList;
import java.util.List;

public class DBServiceImpl implements DBService {

    private final SessionFactory sessionFactory;
    private static Logger logger = LoggerFactory.getLogger(DBServiceImpl.class);

    public DBServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public long saveUser(User user) {
        logger.info("Save user");
        long id = 0L;

        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            session.saveOrUpdate(user);
            id = user.getId();

            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
            logger.error("Save is not successful", ex);
        }
        return id;
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Get all users");
        List<User> users = new ArrayList<>();

        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            users = session
                    .createQuery("from User", User.class)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
            logger.error("Get all users is not successful", ex);
        }
        return users;
    }
}
