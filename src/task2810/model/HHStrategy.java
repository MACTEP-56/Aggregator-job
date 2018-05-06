package task2810.model;

import task2810.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy {
    private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc = null;
        int pageNum = 0;
        while (true) {
            try {
                doc = getDocument(searchString, pageNum++);
                if (doc==null)
                    break;
            } catch (IOException e) {
            }
            Elements elements = doc.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");
            if (elements.size()==0)
                break;
            for (Element element: elements) {
                Vacancy vacancy = new Vacancy();

                vacancy.setTitle(element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-title").text());
                vacancy.setCity(element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address").text());
                vacancy.setCompanyName(element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer").text());

                String salary = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-compensation").text();
                vacancy.setSalary(salary != null?salary:"");

                vacancy.setUrl(element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-title").attr("href"));
                vacancy.setSiteName("http://hh.ua");

                vacancies.add(vacancy);
            }
        }

        return vacancies;
    }

    protected Document getDocument(String searchString, int pageNum) throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, searchString, pageNum)).
                userAgent("Chrome/57.0.2987.133 (jsoup)").referrer("?").get();
    }
}
