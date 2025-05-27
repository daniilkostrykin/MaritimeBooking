package org.example.Practice;

import org.hibernate.Session;
import java.util.List;
import org.example.entity.*;

public class HibernateManager {

    // 2. Покупка билета
    public static List<Object[]> getActiveVoyages(Session session) {
        String hql = "SELECT v.id, v.vesselId FROM Voyage v WHERE v.status = :status";
        return session.createQuery(hql, Object[].class)
                .setParameter("status", Voyage.VoyageStatus.active)
                .getResultList();
    }

    // Покупка билета - вторая вкладка
    public static List<Object[]> getVessels(Session session) {
        String hql = "SELECT v.imo, v.name FROM Vessel v";
        return session.createQuery(hql, Object[].class).getResultList();
    }

    // Покупка билета - вторая вкладка
    public static List<Object[]> getCabins(Session session) {
        String hql = "SELECT c.id, c.vesselId, c.category, c.capacity, c.windowView FROM Cabin c";
        return session.createQuery(hql, Object[].class).getResultList();
    }

    // Покупка билета - вторая вкладка
    public static boolean checkCustomerExists(Session session, String email) {
        String hql = "SELECT COUNT(c) FROM Customer c WHERE c.email = :email";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    // Покупка билета - вторая вкладка
    public static void buyTicket(Session session, String email, int voyageId, String vesselId,
            int cabinId, double price, String paymentMethod, String mealType,
            boolean insurance, int luggageWeight, String purchaseDate) {
        session.beginTransaction();
        try {
            Customer customer = session.get(Customer.class, email);

            String hql = "FROM Voyage v WHERE v.id = :id AND v.vesselId = :vesselId";
            Voyage voyage = session.createQuery(hql, Voyage.class)
                    .setParameter("id", (long) voyageId)
                    .setParameter("vesselId", vesselId)
                    .getSingleResult();

            hql = "FROM Cabin c WHERE c.id = :id AND c.vesselId = :vesselId";
            Cabin cabin = session.createQuery(hql, Cabin.class)
                    .setParameter("id", (long) cabinId)
                    .setParameter("vesselId", vesselId)
                    .getSingleResult();

            Ticket ticket = new Ticket();
            ticket.setCustomer(customer);
            ticket.setVoyage(voyage);
            ticket.setCabin(cabin);
            ticket.setPrice(new java.math.BigDecimal(price));
            ticket.setPaymentMethod(Ticket.PaymentMethod.valueOf(paymentMethod));
            ticket.setMealType(Ticket.MealType.valueOf(mealType));
            ticket.setInsurance(insurance);
            ticket.setLuggageWeight(luggageWeight);
            ticket.setPurchaseDate(java.time.LocalDate.parse(purchaseDate));

            session.persist(ticket);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    // 3. Добавление клиента
    public static void addClient(Session session, String lastName, String firstName, String middleName,
            String passportSeries, String birthDate, String email) {
        session.beginTransaction();
        try {
            Customer customer = new Customer();
            customer.setLastName(lastName);
            customer.setFirstName(firstName);
            customer.setMiddleName(middleName);
            customer.setPassportSeries(passportSeries);
            customer.setBirthDate(java.time.LocalDate.parse(birthDate));
            customer.setEmail(email);

            session.persist(customer);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    // 4. Билеты за год с ценой меньше
    public static List<Object[]> getAvailableYears(Session session) {
        String hql = "SELECT DISTINCT EXTRACT(YEAR FROM t.purchaseDate) as year FROM Ticket t ORDER BY year";
        return session.createQuery(hql, Object[].class).getResultList();
    }

    public static List<Object[]> getTicketsForYearWithPrice(Session session, int year, double maxPrice) {
        String hql = "SELECT t.id, t.price FROM Ticket t " +
                "WHERE EXTRACT(YEAR FROM t.purchaseDate) = :year AND t.price < :maxPrice";
        return session.createQuery(hql, Object[].class)
                .setParameter("year", year)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }

    // 5. Билеты клиента
    public static List<Object[]> getCustomersWithTickets(Session session) {
        String hql = "SELECT c.email, c.lastName, c.firstName FROM Customer c " +
                "WHERE EXISTS (SELECT 1 FROM Ticket t WHERE t.customer = c)";
        return session.createQuery(hql, Object[].class).getResultList();
    }

    public static List<Object[]> getCustomerTickets(Session session, String email) {
        String hql = "SELECT t.id, t.price FROM Ticket t WHERE t.customer.email = :email";
        return session.createQuery(hql, Object[].class)
                .setParameter("email", email)
                .getResultList();
    }

    // 6. Клиенты по питанию
    public static List<Object[]> getCustomersByMealType(Session session, Ticket.MealType mealType) {
        String hql = "SELECT c.email, c.firstName FROM Customer c " +
                "JOIN c.tickets t WHERE t.mealType = :mealType";
        return session.createQuery(hql, Object[].class)
                .setParameter("mealType", mealType)
                .getResultList();
    }

    // 7. Рейсы по статусу и питанию
    public static List<Voyage> getVoyagesWithVessels(Session session) {
        String hql = "FROM Voyage v";
        return session.createQuery(hql, Voyage.class).getResultList();
    }

    public static List<Object[]> getAvailableVoyageStatuses(Session session) {
        String sql = "SELECT DISTINCT status FROM maritime_booking.voyages ORDER BY status";
        return session.createNativeQuery(sql, Object[].class).getResultList();
    }

    public static boolean hasVoyagesWithStatus(Session session, String status) {
        String sql = "SELECT COUNT(*) FROM maritime_booking.voyages WHERE status = :status";
        Long count = session.createNativeQuery(sql, Long.class)
                .setParameter("status", status)
                .getSingleResult();
        return count > 0;
    }

    public static List<Object[]> getAvailableMealTypesByStatus(Session session, String status) {
        String sql = "SELECT DISTINCT tickets.meal_type " +
                "FROM maritime_booking.tickets " +
                "JOIN maritime_booking.voyages ON tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id "
                +
                "WHERE voyages.status = :status " +
                "ORDER BY tickets.meal_type";
        return session.createNativeQuery(sql, Object[].class)
                .setParameter("status", status)
                .getResultList();
    }

    public static List<Object[]> getVoyagesByStatusAndMeal(Session session, String status, String mealType) {
        String sql = "SELECT DISTINCT voyages.id, voyages.status " +
                "FROM maritime_booking.voyages " +
                "JOIN maritime_booking.tickets ON tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id "
                +
                "JOIN maritime_booking.customers ON tickets.email = customers.email " +
                "WHERE voyages.status = :status AND tickets.meal_type = :mealType " +
                "ORDER BY voyages.id";
        return session.createNativeQuery(sql, Object[].class)
                .setParameter("status", status)
                .setParameter("mealType", mealType)
                .getResultList();
    }

    // 8. Билеты со страховкой по стране
    public static List<String> getCountriesWithInsuredTickets(Session session) {
        String hql = "SELECT DISTINCT stage.departurePort.country " +
                "FROM Ticket t " +
                "JOIN t.voyage voy " + // Связь из Ticket к Voyage
                "JOIN voy.stages stage " + // Связь из Voyage к его VoyageStage (список)
                // Hibernate сам разберется с join-условиями для композитных ключей, если
                // маппинги верны
                "WHERE t.insurance = true AND stage.stopNumber = 1 " + // stopNumber - поле в сущности VoyageStage
                "ORDER BY stage.departurePort.country";
        return session.createQuery(hql, String.class).getResultList();
    }

    public static List<Object[]> getInsuredTicketsByCountry(Session session, String country) {
        String hql = "SELECT t.id, t.price " + // Убедитесь, что t.price имеет совместимый тип (например, BigDecimal)
                "FROM Ticket t " +
                "JOIN t.voyage voy " +
                "JOIN voy.stages stage " +
                "WHERE stage.departurePort.country = :countryToFilter " + // Используем другой плейсхолдер
                "AND t.insurance = true " +
                "AND stage.stopNumber = 1";
        return session.createQuery(hql, Object[].class)
                .setParameter("countryToFilter", country)
                .getResultList();
    }

    // 9. Добавить клиента и билет
    public static int addClientAndTicket(Session session, String email, String lastName, String firstName,
            String middleName, String birthDate, String passportSeries, int voyageId, String vesselId,
            int cabinId, double price, String paymentMethod, String mealType,
            boolean insurance, int luggageWeight, String purchaseDate) {
        session.beginTransaction();
        try {
            Customer customer = new Customer();
            customer.setEmail(email);
            customer.setLastName(lastName);
            customer.setFirstName(firstName);
            customer.setMiddleName(middleName);
            customer.setBirthDate(java.time.LocalDate.parse(birthDate));
            customer.setPassportSeries(passportSeries);

            session.persist(customer);

            String hql = "FROM Voyage v WHERE v.id = :id AND v.vesselId = :vesselId";
            Voyage voyage = session.createQuery(hql, Voyage.class)
                    .setParameter("id", (long) voyageId)
                    .setParameter("vesselId", vesselId)
                    .getSingleResult();

            hql = "FROM Cabin c WHERE c.id = :id AND c.vesselId = :vesselId";
            Cabin cabin = session.createQuery(hql, Cabin.class)
                    .setParameter("id", (long) cabinId)
                    .setParameter("vesselId", vesselId)
                    .getSingleResult();

            Ticket ticket = new Ticket();
            ticket.setCustomer(customer);
            ticket.setVoyage(voyage);
            ticket.setCabin(cabin);
            ticket.setPrice(new java.math.BigDecimal(price));
            ticket.setPaymentMethod(Ticket.PaymentMethod.valueOf(paymentMethod));
            ticket.setMealType(Ticket.MealType.valueOf(mealType));
            ticket.setInsurance(insurance);
            ticket.setLuggageWeight(luggageWeight);
            ticket.setPurchaseDate(java.time.LocalDate.parse(purchaseDate));

            session.persist(ticket);
            session.getTransaction().commit();
            return ticket.getId().intValue();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    // 10. Удалить рейс и всё связанное
    public static void deleteVoyage(Session session, int voyageId, String vesselId) {
        session.beginTransaction();
        try {
            String hql = "FROM Voyage v WHERE v.id = :id AND v.vesselId = :vesselId";
            Voyage voyage = session.createQuery(hql, Voyage.class)
                    .setParameter("id", (long) voyageId)
                    .setParameter("vesselId", vesselId)
                    .getSingleResult();
            if (voyage != null) {
                session.remove(voyage);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    // 11. Корректировка цены багажа
    public static List<Object[]> getFutureLuggagePrices(Session session, double minWeight, String date) {
        String hql = "SELECT t.id, t.luggageWeight, t.price, " +
                "CASE WHEN t.luggageWeight > :minWeight THEN t.price * 1.1 ELSE t.price END as updatedPrice, " +
                "t.purchaseDate FROM Ticket t " +
                "WHERE t.purchaseDate <= :date";
        return session.createQuery(hql, Object[].class)
                .setParameter("minWeight", minWeight)
                .setParameter("date", java.time.LocalDate.parse(date))
                .getResultList();
    }

    public static void updateLuggagePrice(Session session, double minWeight, String date) {
        session.beginTransaction();
        try {
            String hql = "UPDATE Ticket t SET t.price = t.price * 1.1 " +
                    "WHERE t.luggageWeight > :minWeight AND t.purchaseDate <= :date";
            session.createMutationQuery(hql)
                    .setParameter("minWeight", minWeight)
                    .setParameter("date", java.time.LocalDate.parse(date))
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static List<Object[]> getUpdatedLuggageTickets(Session session, double minWeight, String date) {
        String hql = "SELECT t.id, t.luggageWeight, t.price, t.purchaseDate FROM Ticket t " +
                "WHERE t.luggageWeight > :minWeight AND t.purchaseDate <= :date";
        return session.createQuery(hql, Object[].class)
                .setParameter("minWeight", minWeight)
                .setParameter("date", java.time.LocalDate.parse(date))
                .getResultList();
    }

    // 12. Маршрут рейса
    public static List<Object[]> getVoyagesWithVesselDetails(Session session) {
        String hql = "SELECT v.id, v.vesselId, v.name, v.status FROM Voyage v";
        return session.createQuery(hql, Object[].class).getResultList();
    }

    public static List<Object[]> getVoyageRoute(Session session, int voyageId) {
        String sql = "SELECT v.id as voyage_id, v.vessel_id as vessel_id, v.status as voyage_status, " +
                "vs.stop_number as stop_number, " +
                "p.name as port_name, p.country as port_country " +
                "FROM maritime_booking.voyages v " +
                "JOIN maritime_booking.voyage_stages vs ON v.id = vs.voyage_id AND v.vessel_id = vs.vessel_id " +
                "JOIN maritime_booking.ports p ON vs.departure_port_id = p.un_locode " +
                "WHERE v.id = :voyageId " +
                "ORDER BY vs.stop_number";
        return session.createNativeQuery(sql, Object[].class)
                .setParameter("voyageId", voyageId)
                .getResultList();
    }

    // 13. Продажи билетов
    public static List<Object[]> getTicketSales(Session session, String year) {
        String hql = "SELECT EXTRACT(MONTH FROM t.purchaseDate) as month, " +
                "COUNT(t) as ticket_count, " +
                "SUM(t.price) as total_revenue " +
                "FROM Ticket t " +
                "WHERE EXTRACT(YEAR FROM t.purchaseDate) = :year " +
                "GROUP BY EXTRACT(MONTH FROM t.purchaseDate) " +
                "ORDER BY month";
        return session.createQuery(hql, Object[].class)
                .setParameter("year", Integer.parseInt(year))
                .getResultList();
    }

    // 14. Средний чек по клиентам
    public static List<Object[]> getAverageCheck(Session session) {
        String hql = "SELECT c.country, " +
                "AVG(t.price) as avg_price, " +
                "COUNT(t) as ticket_count " +
                "FROM Customer c " +
                "JOIN c.tickets t " +
                "GROUP BY c.country " +
                "ORDER BY avg_price DESC";
        return session.createQuery(hql, Object[].class).getResultList();
    }

    // 15. Выручка по маршрутам
    public static List<Object[]> getTotalRevenue(Session session) {
        String hql = "SELECT EXTRACT(YEAR FROM t.purchaseDate) as year, " +
                "SUM(t.price) as total_revenue " +
                "FROM Ticket t " +
                "GROUP BY EXTRACT(YEAR FROM t.purchaseDate) " +
                "ORDER BY year";
        return session.createQuery(hql, Object[].class).getResultList();
    }

    // 16. Произвольный HQL-запрос
    public static List<Object[]> executeCustomQuery(Session session, String sql) { // Имя параметра лучше сменить на sql
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL-запрос не может быть пустым.");
        }
        org.hibernate.query.NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
        return query.getResultList();
    }
}