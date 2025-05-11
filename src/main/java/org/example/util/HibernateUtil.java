package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.example.entity.*;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Vessel.class);
            configuration.addAnnotatedClass(Voyage.class);
            configuration.addAnnotatedClass(VoyageStage.class);
            configuration.addAnnotatedClass(Port.class);
            configuration.addAnnotatedClass(Customer.class);
            configuration.addAnnotatedClass(Cabin.class);
            configuration.addAnnotatedClass(Ticket.class);
            return configuration.buildSessionFactory();
        } catch (Exception ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}