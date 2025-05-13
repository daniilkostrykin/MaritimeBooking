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

    public Voyage findById(Long id, String vesselId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            VoyageId voyageId = new VoyageId();
            voyageId.setId(id);
            voyageId.setVesselId(vesselId);
            return session.get(Voyage.class, voyageId);
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

    public List<Voyage> findByStatusAndMealType(String statusStr, String mealType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Voyage.VoyageStatus status = Voyage.VoyageStatus.valueOf(statusStr);
            return session.createQuery(
                    "SELECT DISTINCT v FROM Voyage v " +
                            "JOIN v.tickets t " +
                            "WHERE v.status = :status AND t.mealType = :mealType",
                    Voyage.class)
                    .setParameter("status", status)
                    .setParameter("mealType", mealType)
                    .list();
        }
    }

    public List<Object[]> getVoyageWithVessels() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT v.id, v.vesselId, vs.name " +
                            "FROM Voyage v JOIN v.vessel vs",
                    Object[].class).list();
        }
    }

    public void delete(Long id, String vesselId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Voyage voyage = findById(id, vesselId);
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

    public List<Object[]> getVoyageRoute(Long voyageId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                    "SELECT v.id as voyage_id, v.vessel_id, vs.name as vessel_name, v.status as voyage_status, " +
                            "vst.stop_number, " +
                            "dp.un_locode as departure_port_code, dp.name as departure_port_name, " +
                            "dp.city as departure_port_city, dp.country as departure_port_country, " +
                            "vst.departure_datetime, " +
                            "ap.un_locode as arrival_port_code, ap.name as arrival_port_name, " +
                            "ap.city as arrival_port_city, ap.country as arrival_port_country, " +
                            "vst.arrival_datetime " +
                            "FROM maritime_booking.voyages v " +
                            "JOIN maritime_booking.vessels vs ON v.vessel_id = vs.imo " +
                            "JOIN maritime_booking.voyage_stages vst ON vst.voyage_id = v.id AND vst.vessel_id = v.vessel_id "
                            +
                            "JOIN maritime_booking.ports dp ON vst.departure_port_id = dp.un_locode " +
                            "JOIN maritime_booking.ports ap ON vst.arrival_port_id = ap.un_locode " +
                            "WHERE v.id = :voyageId " +
                            "ORDER BY vst.stop_number",
                    Object[].class)
                    .setParameter("voyageId", voyageId)
                    .getResultList();
        }
    }

    public List<Object[]> getVoyageCities(Long voyageId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                    "SELECT vst.stop_number, dp.city as departure_city, ap.city as arrival_city " +
                            "FROM maritime_booking.voyage_stages vst " +
                            "JOIN maritime_booking.ports dp ON vst.departure_port_id = dp.un_locode " +
                            "JOIN maritime_booking.ports ap ON vst.arrival_port_id = ap.un_locode " +
                            "WHERE vst.voyage_id = :voyageId " +
                            "ORDER BY vst.stop_number",
                    Object[].class)
                    .setParameter("voyageId", voyageId)
                    .list();
        }
    }

    public List<Object[]> getTicketSales(String year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                    "SELECT TO_CHAR(t.purchase_date, 'Month') as month, " +
                            "dp.country as departure_country, " +
                            "ap.country as arrival_country, " +
                            "COUNT(t.id) as tickets_count, " +
                            "SUM(t.price) as total_revenue, " +
                            "AVG(t.price) as avg_price, " +
                            "ROUND(SUM(CASE WHEN t.insurance THEN 1 ELSE 0 END)::numeric / COUNT(t.id) * 100, 2) as insurance_percent "
                            +
                            "FROM maritime_booking.tickets t " +
                            "JOIN maritime_booking.voyages v ON t.voyage_id = v.id AND t.vessel_id = v.vessel_id " +
                            "JOIN maritime_booking.voyage_stages vs ON vs.voyage_id = v.id AND vs.vessel_id = v.vessel_id AND vs.stop_number = 1 "
                            +
                            "JOIN maritime_booking.ports dp ON vs.departure_port_id = dp.un_locode " +
                            "JOIN (SELECT vs2.voyage_id, vs2.vessel_id, MAX(vs2.stop_number) as max_stop " +
                            "      FROM maritime_booking.voyage_stages vs2 " +
                            "      GROUP BY vs2.voyage_id, vs2.vessel_id) last_stage " +
                            "ON v.id = last_stage.voyage_id AND v.vessel_id = last_stage.vessel_id " +
                            "JOIN maritime_booking.voyage_stages vs_last " +
                            "ON vs_last.voyage_id = last_stage.voyage_id AND vs_last.vessel_id = last_stage.vessel_id "
                            +
                            "AND vs_last.stop_number = last_stage.max_stop " +
                            "JOIN maritime_booking.ports ap ON vs_last.arrival_port_id = ap.un_locode " +
                            "WHERE EXTRACT(YEAR FROM t.purchase_date) = :year " +
                            "GROUP BY month, dp.country, ap.country " +
                            "ORDER BY month",
                    Object[].class)
                    .setParameter("year", year)
                    .list();
        }
    }

    public List<Object[]> getTotalRevenue() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(
                    "SELECT vs.departure_port_id, vs.arrival_port_id, SUM(t.price) as total_revenue " +
                            "FROM maritime_booking.tickets t " +
                            "JOIN maritime_booking.voyages v ON t.voyage_id = v.id AND t.vessel_id = v.vessel_id " +
                            "JOIN maritime_booking.voyage_stages vs ON vs.voyage_id = v.id AND vs.vessel_id = v.vessel_id "
                            +
                            "GROUP BY vs.departure_port_id, vs.arrival_port_id " +
                            "ORDER BY total_revenue DESC",
                    Object[].class)
                    .list();
        }
    }
}