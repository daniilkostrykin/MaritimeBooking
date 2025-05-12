package org.example.dao;

import org.example.entity.Port;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class PortDAO {
    public void save(Port port) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(port);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public Port findById(String portId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Port.class, portId);
        }
    }

    public List<Port> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Port", Port.class).list();
        }
    }

    public void update(Port port) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(port);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void delete(String portId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Port port = session.get(Port.class, portId);
                if (port != null) {
                    session.remove(port);
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

    /**
     * Получает список стран, из которых отправляются рейсы с билетами, имеющими
     * страховку
     */
    public List<String> getCountriesWithInsuredTickets() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT p.country FROM Port p " +
                            "JOIN VoyageStage vs ON vs.departurePort = p " +
                            "JOIN Ticket t ON t.voyageId = vs.voyageId AND t.vesselId = vs.vesselId " +
                            "WHERE t.insurance = true " +
                            "ORDER BY p.country",
                    String.class).list();
        }
    }
}