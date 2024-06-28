package safety_standard.ss_test.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import safety_standard.ss_test.dto.Category;
import safety_standard.ss_test.entities.ConstantEntity;
import safety_standard.ss_test.entities.DocumentEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// Класс, описывающий запросы к БД
public class DocumentService {

    SessionFactory factory = new Configuration()
        .configure("hibernate.cfg.xml")
        .addAnnotatedClass(DocumentEntity.class)
        .buildSessionFactory();

    public void saveListOfDocWithCheckExists(List<DocumentEntity> listOfDoc) {
        try ( Session session = factory.getCurrentSession() ) {
            try {
                session.beginTransaction();
                Long id = null;

                // обновляем все записи на текущую дату, у которых дата удаления равна null
                String updateHql = "update DocumentEntity d " +
                        "set d.dateDelete = :currentDate " +
                        "where d.dateDelete is null";
                session.createQuery(updateHql).setParameter("currentDate", Instant.now()).executeUpdate();

                for (DocumentEntity document : listOfDoc) {
                    // проверяем существование документа в БД
                    String checkHql = "select id " +
                            "from DocumentEntity d " +
                            "where d.docName = :name and d.dateCreate = :dateCreate and d.dataArticleId = :dataArticleId";
                    Query<Long> query = session.createQuery(checkHql, Long.class);
                    query.setParameter("name", document.getDocName());
                    query.setParameter("dateCreate", document.getDateCreate());
                    query.setParameter("dataArticleId", document.getDataArticleId());
                    id = query.getSingleResultOrNull();

                    if (id == null) {
                        session.merge(document);
                    } else {
                        // Если запись существует, обновляем дату удаления обратно на null
                        String resetHql = "update DocumentEntity d " +
                                "set d.dateDelete = NULL " +
                                "where d.id = :id";
                        session.createQuery(resetHql).setParameter("id", id).executeUpdate();
                    }
                }
                session.getTransaction().commit();
            } catch (Exception e) {
                if (session.getTransaction().isActive() && session != null && session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        }
    }

    public String getUrl() {
        String url = "";

        try (Session session = factory.getCurrentSession()) {
            try {
                session.beginTransaction();

                Query<String> query = session.createQuery("select value from ConstantEntity where name='URL'");
                url = query.uniqueResult();

                session.getTransaction().commit();
            } catch (Exception e) {
                if (session.getTransaction().isActive() && session != null && session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        }
        return url;
    }

    public String getPath() {
        String path = "";

        try (Session session = factory.getCurrentSession()) {
            try {
                session.beginTransaction();
                Query<String> query = session.createQuery("select value from ConstantEntity where name='PATH'");
                path = query.uniqueResult();
                session.getTransaction().commit();
            } catch (Exception e) {
                if (session.getTransaction().isActive() && session != null && session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        }

        return path;
    }

    public void saveParameters(String URL, String PATH) {
        try (Session session = factory.getCurrentSession()) {
            try {
                session.beginTransaction();

                String urlHql = "update ConstantEntity " +
                        "set value = :URL " +
                        "where name='URL'";
                session.createQuery(urlHql).setParameter("URL", URL).executeUpdate();

                String pathHql = "update ConstantEntity " +
                        "set value = :PATH " +
                        "where name='PATH'";
                session.createQuery(pathHql).setParameter("PATH", PATH).executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                if (session.getTransaction().isActive() && session != null && session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        }
    }

    public List<Category> category() {
        List<Category> categoryList = new ArrayList<>();

        try (Session session = factory.getCurrentSession()) {
            try {
                session.beginTransaction();

                String hql = "select new safety_standard.ss_test.dto.Category(category, count(case when dateDelete is null then 1 end)) " +
                        "from DocumentEntity " +
                        "group by category " +
                        "order by category";
                Query<Category> query = session.createQuery(hql, Category.class);
                categoryList = query.getResultList();

                session.getTransaction().commit();
            } catch (Exception e) {
                if (session.getTransaction().isActive() && session != null && session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        }
        return categoryList;
    }

    public List<Category> categoryWithAddition(Instant dateCSVFile) {
        List<Category> categoryList = new ArrayList<>();

        try (Session session = factory.getCurrentSession()) {
            try {
                session.beginTransaction();

                String hql = "select new safety_standard.ss_test.dto.Category(" +
                        "category, " +
                        "count(case when dateDelete is null then 1 end), " +
                        "count(case when :dateCSVFile < dateCreate and dateDelete is null then 1 end), " +
                        "count(case when :dateCSVFile < dateDelete then 1 end)) " +
                        "from DocumentEntity " +
                        "group by category " +
                        "order by category";
                Query<Category> query = session.createQuery(hql, Category.class).setParameter("dateCSVFile", dateCSVFile);
                categoryList = query.getResultList();

                session.getTransaction().commit();
            } catch (Exception e) {
                if (session.getTransaction().isActive() && session != null && session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        }
        return categoryList;
    }
}