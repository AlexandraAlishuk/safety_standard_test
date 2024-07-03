package safety_standard.ss_test.service;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import safety_standard.ss_test.dto.Category;
import safety_standard.ss_test.entities.DocumentEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// Класс, описывающий запросы к БД
@Repository
public class DocumentService {

    @Autowired
    private SessionFactory factory;

    @Transactional
    public void saveListOfDocWithCheckExists(List<DocumentEntity> listOfDoc) {
        Long id = null;
        Session session = factory.getCurrentSession();

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
    }

    @Transactional
    public String getUrl() {
        String url = "";

        Session session = factory.getCurrentSession();
        Query<String> query = session.createQuery("select value from ConstantEntity where name='URL'");
        url = query.uniqueResult();
        return url;
    }

    @Transactional
    public String getPath() {
        String path = "";

        Session session = factory.getCurrentSession();
        Query<String> query = session.createQuery("select value from ConstantEntity where name='PATH'");
        path = query.uniqueResult();

        return path;
    }

    @Transactional
    public void saveParameters(String URL, String PATH) {
        Session session = factory.getCurrentSession();

        String urlHql = "update ConstantEntity " +
                "set value = :URL " +
                "where name='URL'";
        session.createQuery(urlHql).setParameter("URL", URL).executeUpdate();

        String pathHql = "update ConstantEntity " +
                "set value = :PATH " +
                "where name='PATH'";
        session.createQuery(pathHql).setParameter("PATH", PATH).executeUpdate();
    }

    @Transactional
    public List<Category> category() {
        List<Category> categoryList = new ArrayList<>();

        Session session = factory.getCurrentSession();

        String hql = "select new safety_standard.ss_test.dto.Category(category, count(case when dateDelete is null then 1 end)) " +
                "from DocumentEntity " +
                "group by category " +
                "order by category";
        Query<Category> query = session.createQuery(hql, Category.class);
        categoryList = query.getResultList();

        return categoryList;
    }

    @Transactional
    public List<Category> categoryWithAddition(Instant dateCSVFile) {
        List<Category> categoryList = new ArrayList<>();

        Session session = factory.getCurrentSession();

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

        return categoryList;
    }
}