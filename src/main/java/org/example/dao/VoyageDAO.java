package org.example.dao;

import org.example.entity.Voyage;
import org.example.entity.VoyageId;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class VoyageDAO {

    public void save(Voyage voyage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(voyage);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public Voyage findById(VoyageId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Voyage.class, id);
        }
    }

    public List<Voyage> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Voyage", Voyage.class).list();
        }
    }

    public List<Voyage> findByStatus(String statusStr) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Voyage.VoyageStatus status = Voyage.VoyageStatus.valueOf(statusStr);
            Query<Voyage> query = session.createQuery(
                    "FROM Voyage v WHERE v.status = :status",
                    Voyage.class);
            query.setParameter("status", status);
            return query.list();
        }
    }

    public void update(Voyage voyage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(voyage);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void delete(VoyageId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Voyage voyage = session.get(Voyage.class, id);
                if (voyage != null) {
                    session.remove(voyage);
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}