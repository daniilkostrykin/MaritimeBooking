package org.example.dao;

import org.example.entity.Ticket;
import org.example.entity.TicketId;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TicketDAO {

    public void save(Ticket ticket) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(ticket);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public Ticket findById(TicketId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Ticket.class, id);
        }
    }

    public List<Ticket> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Ticket", Ticket.class).list();
        }
    }

    public void update(Ticket ticket) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(ticket);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void delete(TicketId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Ticket ticket = session.get(Ticket.class, id);
                if (ticket != null) {
                    session.remove(ticket);
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

    public List<Ticket> findTicketsForPriceUpdate(int minWeight, LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ticket t WHERE t.purchaseDate < :date " +
                    "AND t.luggageWeight > :minWeight " +
                    "AND t.voyage.status = 'active' " +
                    "ORDER BY t.id";

            Query<Ticket> query = session.createQuery(hql, Ticket.class);
            query.setParameter("date", date);
            query.setParameter("minWeight", minWeight);

            return query.list();
        }
    }

    public void updateTicketPrice(Ticket ticket, double newPrice) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                ticket.setPrice(BigDecimal.valueOf(newPrice));
                session.merge(ticket);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void updateLuggagePrices(int minWeight, LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                String hql = "UPDATE Ticket t SET t.price = t.price + (t.luggageWeight - :minWeight) * 1000 " +
                        "WHERE t.purchaseDate < :date " +
                        "AND t.luggageWeight > :minWeight " +
                        "AND t.voyage.status = 'active'";

                Query<?> query = session.createQuery(hql);
                query.setParameter("minWeight", minWeight);
                query.setParameter("date", date);

                query.executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public List<Ticket> findByCustomerEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Ticket> query = session.createQuery(
                    "FROM Ticket t WHERE t.customer.email = :email",
                    Ticket.class);
            query.setParameter("email", email);
            return query.list();
        }
    }

    public List<Ticket> findTicketsForYearWithMaxPrice(int year, double maxPrice) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ticket t WHERE YEAR(t.purchaseDate) = :year AND t.price <= :maxPrice ORDER BY t.id";
            Query<Ticket> query = session.createQuery(hql, Ticket.class);
            query.setParameter("year", year);
            query.setParameter("maxPrice", BigDecimal.valueOf(maxPrice));
            return query.list();
        }
    }

    public List<Ticket> findByMealType(String mealType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Ticket> query = session.createQuery(
                    "FROM Ticket t WHERE t.mealType = :mealType",
                    Ticket.class);
            query.setParameter("mealType", mealType);
            return query.list();
        }
    }

    public List<Ticket> findInsuredTicketsByDepartureCountry(String country) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Ticket> query = session.createQuery(
                    "SELECT t FROM Ticket t " +
                            "JOIN t.voyage v " +
                            "JOIN FETCH t.customer " +
                            "JOIN VoyageStage vs ON vs.voyageId = v.id AND vs.vesselId = v.vesselId " +
                            "JOIN vs.departurePort p " +
                            "WHERE p.country = :country AND t.insurance = true AND vs.stopNumber = 1",
                    Ticket.class);
            query.setParameter("country", country.toLowerCase());
            return query.list();
        }
    }

    public List<Object[]> getAverageCheckByCustomer() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Object[]> query = session.createQuery(
                    "SELECT t.customer.email, COUNT(t), AVG(t.price) " +
                            "FROM Ticket t " +
                            "GROUP BY t.customer.email " +
                            "ORDER BY AVG(t.price) DESC",
                    Object[].class);
            return query.list();
        }
    }

    public List<Ticket> findInsuredTicketsByCountry(String country) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Ticket t WHERE t.insuranceRequired = true AND t.customer.citizenship = :country",
                    Ticket.class)
                    .setParameter("country", country)
                    .list();
        }
    }

    public List<Integer> findDistinctYears() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT YEAR(t.purchaseDate) FROM Ticket t ORDER BY YEAR(t.purchaseDate)",
                    Integer.class)
                    .list();
        }
    }
}