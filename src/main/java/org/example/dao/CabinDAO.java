package org.example.dao;

import org.example.entity.Cabin;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class CabinDAO {
    public List<Cabin> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Cabin", Cabin.class).list();
        }
    }
    // Можно добавить другие методы по необходимости
}