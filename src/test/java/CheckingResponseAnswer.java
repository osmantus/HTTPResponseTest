import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class CheckingResponseAnswer {

    @Test
    public void checkingResponse() throws IOException {
        open("https://rozetka.com.ua/");

        List<String> hRefList = new ArrayList();

        ElementsCollection categories = $$(By.xpath("//a[@class='menu-categories__link']"));
        for (SelenideElement category : categories) {
            String hRefText = category.getAttribute("href");
            hRefList.add(hRefText);
        }
        System.out.println("Checking each link on existence:");
        int errCount = 0;
        List<String> errLinks = new ArrayList();
        for (int i = 0; i < hRefList.size(); i++) {
            if (!checkingResp((i+1), hRefList.get(i))) {
                errCount++;
                errLinks.add(hRefList.get(i));
            }
        }
        if (errCount > 0) {
            if (errCount == 1) {
                Assert.fail("1 link without code 200: " + errLinks.get(0));
            } else if (errCount > 1) {
                Assert.fail(errCount + " links without code 200: " + errLinks);
            }
        }
    }

    public boolean checkingResp(int index, String linksOnThePage) throws IOException {
        int responseCode = 0;
        boolean isNotError = true;
        try {
            URL url = new URL(linksOnThePage);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            responseCode = http.getResponseCode();

            if (responseCode != 200) {
                throw new AssertionError();
            } else {
                System.out.println(index + ") " + linksOnThePage + " - " + "the response code is " + responseCode);
            }
        } catch (AssertionError assertionError) {
            System.out.println(index + ") " + linksOnThePage + " - " + "the response code is not 200 (" + responseCode + ").");
            isNotError = false;
        } catch (Exception e) {
            isNotError = false;
            Assert.fail("Exception: " + e.getStackTrace());
        }
        return isNotError;
    }
}
