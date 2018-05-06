package task2810.view;

import task2810.Controller;
import task2810.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlView implements View {
    private Controller controller;
//    private final String filePath = "./Aggregator-job/src/" + this.getClass().getPackage().getName().replaceAll("\\.", "/") + "/vacancies.html";
    private final String filePath = "C:\\Users\\MACTEP\\IdeaProjects\\Aggregator-job\\src\\task2810\\view\\vacancies.html";
    @Override
    public void update(List<Vacancy> vacancies) {
        try {
            updateFile(getUpdatedFileContent(vacancies));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void userCitySelectEmulationMethod() {
        controller.onCitySelect("Kiev");
    }

    private String getUpdatedFileContent(List<Vacancy> vacancies) throws IOException {
        Document doc;
        try {
            doc = getDocument();

            Elements elementVacancy = doc.getElementsByAttributeValue("class", "vacancy");
            for (int i=0;i<elementVacancy.size();i++){
                elementVacancy.get(i).remove();
            }

            Element templateElement = doc.getElementsByClass("template").first();
            Element cloneTemplateElement = templateElement.clone();
            cloneTemplateElement.removeClass("template");
            cloneTemplateElement.removeAttr("style");

            Vacancy vacancy;

            for (int i=0;i<vacancies.size();i++){
                vacancy = vacancies.get(i);
                Element e = cloneTemplateElement.clone();

                //add city
                e.getElementsByClass("city").first().text(vacancy.getCity());
                // add companyName
                e.getElementsByClass("companyName").first().text(vacancy.getCompanyName());
                // add salary
                e.getElementsByClass("salary").first().text(vacancy.getSalary());
                //add url
                Element elementLink = e.getElementsByClass("title").first();
                elementLink.text(vacancy.getTitle());
                elementLink.attr("href", vacancy.getUrl());

                templateElement.before(e.outerHtml());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Some exception occurred";



        }
        return doc.toString();
    }

    private void updateFile(String file) throws IOException{
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(file);
        }
    }

    protected Document getDocument() throws IOException {
        return Jsoup.parse(new File(filePath), "UTF-8");
    }
}
