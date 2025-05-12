package org.example.dao;

import org.example.entity.Cabin;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class CabinDAO {
    public List<Cabin> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Cabin", Cabin.class).list();
        }
    }

    public Cabin findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Cabin.class, id);
        }
    }

    public Cabin findById(Long id, String vesselId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Cabin> query = session.createQuery(
                    "FROM Cabin c WHERE c.id = :id AND c.vessel.imo = :vesselId",
                    Cabin.class);
            query.setParameter("id", id);
            query.setParameter("vesselId", vesselId);
            return query.uniqueResult();
        }
    }
    // Можно добавить другие методы по необходимости
}