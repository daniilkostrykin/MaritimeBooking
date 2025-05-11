package org.example.dao;

import org.example.entity.VoyageStage;
import org.example.entity.VoyageStageId;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class VoyageStageDAO {
    public void save(VoyageStage voyageStage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(voyageStage);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public VoyageStage findById(VoyageStageId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(VoyageStage.class, id);
        }
    }

    public List<VoyageStage> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM VoyageStage", VoyageStage.class).list();
        }
    }

    public List<VoyageStage> findByVoyageId(Long voyageId, String vesselId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM VoyageStage vs WHERE vs.voyageId = :voyageId AND vs.vesselId = :vesselId",
                    VoyageStage.class)
                    .setParameter("voyageId", voyageId)
                    .setParameter("vesselId", vesselId)
                    .list();
        }
    }

    public void update(VoyageStage voyageStage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(voyageStage);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void delete(VoyageStageId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                VoyageStage voyageStage = session.get(VoyageStage.class, id);
                if (voyageStage != null) {
                    session.remove(voyageStage);
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