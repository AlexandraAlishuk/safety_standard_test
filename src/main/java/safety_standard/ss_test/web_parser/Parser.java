package safety_standard.ss_test.web_parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import safety_standard.ss_test.entities.DocumentEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Парсит страничку с URL: https://fstec.ru/dokumenty/vse-dokumenty (URL может измениться, но струтура останется)
public class Parser {

    Map mapOfLink = new HashMap<String, String>();

    public Map<String, String> checkLinkForDocuments(String url) {

        try {
            // Выключаем проверку сертификата
            DisableSslVerification.disableSslVerification();

            Document doc = Jsoup.connect(url).get();

            Elements categories = doc.select("h3.page-header.item-title");

            for (Element element: categories) {
                Element link = element.child(0);
                String nameLink = link.text();
                String addressLink = link.attr("href");

                int lastIndexOf = addressLink.lastIndexOf("/");
                int length = addressLink.length();

                mapOfLink.put(nameLink, url + addressLink.substring(lastIndexOf, length));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapOfLink;
    }

    public void checkDocuments(String url, String categoryOfDocument,  List<DocumentEntity> listOfDoc) {

        try {
            // Выключаем проверку сертификата
            DisableSslVerification.disableSslVerification();

            Document document = Jsoup
                    .connect(url)
                    .maxBodySize(0)  // неограниченный размер
                    .timeout(60000)  // Тайм-аут 60 секунд
                    .get();

            Elements categories = document.select("div.item-content");

            for (Element element: categories) {
                String nameDocument = element.selectFirst("a").text();
                String d = element.selectFirst("time").attr("datetime");
                Instant dateCreate = Instant.parse(d);
                String description = element.selectFirst("p").text();
                Long dataArticleId = Long.valueOf(element.child(1).attr("data-article-id"));

                DocumentEntity documentEntity = new DocumentEntity(nameDocument, dateCreate, Instant.now(), null, categoryOfDocument, description, dataArticleId);

                listOfDoc.add(documentEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
