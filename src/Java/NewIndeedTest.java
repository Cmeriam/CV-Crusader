package Java;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Time;
import java.time.Instant;
import java.util.List;


public class NewIndeedTest {
    WebDriver driver;

    int page = 1, jobsPerPage = 0;

    //@Parameters("keyWord")
    @BeforeTest
    public void beforeTest() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Hazrtine\\Downloads\\chromedriver-win32\\chromedriver-win32\\chromedriver.exe");
        this.driver = new ChromeDriver();

        driver.get("https://www.indeed.com");
        driver.manage().window().maximize();
    }


    @Test
    public void parameterPassing() throws Throwable {
        // Verifying the keyword on UI
        driver.findElement(By.id("text-input-what")).clear();
        // Thread.sleep(2000);
        driver.findElement(By.id("text-input-what")).sendKeys("Computer");
        //Thread.sleep(1000);

        /*
        WebElement WhereBox = driver.findElement(By.id("label-text-input-what"));
        // Sending keyword value
        WhereBox.click();
        WhereBox.sendKeys(Keys.CONTROL + "a");
        WhereBox.sendKeys(Keys.DELETE);
        System.out.println("The Where box is cleared");
        Thread.sleep(2000);
        This code block seems redundant, will revisit
         */

        WebElement FindButton = driver.findElement(By.className("yosegi-InlineWhatWhere-primaryButton"));
        FindButton.submit();
        System.out.println("The Find Jobs button is clicked");
        Thread.sleep(500);

        driver.findElement(By.id("filter-dateposted")).click();
        // yosegi-FilterPill-dropdownListItemLink
        driver.findElement(By.linkText("Last 24 hours")).click();
        Thread.sleep(1000);
        try {
            if (driver.findElement(By.id("popover-foreground")).isDisplayed())
            driver.findElement(By.id("popover-x")).click();
        } catch (Exception e) {
            System.out.println("whatever popover-foreground is, it doesn't exist.");
        }
        Thread.sleep(1000);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,300)");

        Reporter.log("Scrolled page");


        System.out.println("Page Title: " + driver.getTitle());
        List<WebElement> jobs = driver.findElements(By.className("slider_container"));

        int jobsPerPage = jobs.size();
        if (this.jobsPerPage >= 15) {


            page++;
        }
        System.out.println(jobsPerPage);


        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sh = wb.createSheet();

        sh.createRow(0);
        sh.getRow(0).createCell(0).setCellValue("JobTitle");
        sh.getRow(0).createCell(1).setCellValue("JobLocation");
        sh.getRow(0).createCell(2).setCellValue("JobCategory");
        sh.getRow(0).createCell(3).setCellValue("Job company name");
        sh.getRow(0).createCell(4).setCellValue("Job date posted");
        sh.getRow(0).createCell(5).setCellValue("Job description");
        sh.getRow(0).createCell(6).setCellValue("Job link");

        for (int i = 0; i < jobsPerPage; i++) {
            String sjobcategory = "";
            sh.createRow(i + 1);
            String DatePosted = jobs.get(i).findElement(By.className("date")).getText();
            System.out.println("Date Retrieved: " + Time.from(Instant.now()));

            WebElement resultContent = jobs.get(i).findElement(By.className("resultContent"));
            WebElement jobtitle = resultContent.findElement(By.className("jobTitle"));
            // System.out.println(jobTitle.getText().substring(3));
            jobtitle.click();
            Thread.sleep(1000);
            String jobLink;
            try {
                WebElement jobcontainer = driver.findElement(By.className("jcs-JobTitle"));
                jobcontainer.click();
                jobLink = jobcontainer.getAttribute("href");
                System.out.println("The Job Link is: " + jobLink);
                Thread.sleep(1000);
            } catch (Exception e) { //pagination i++
                System.out.println("Something Happened. I think that it might've reached the end here but not too sure.");
                break;
            }

            WebElement m = driver.findElement(By.className("jobsearch-JobInfoHeader-title"));
            String jobTitle = m.findElement(By.xpath("//span")).getText();

            WebElement m1 = driver.findElement(By.className("css-2wyr5j"));
            Thread.sleep(3500); //JobCompanyName is weirdly sensitive, Thread.sleep() is required.
            String JobCompanyName = driver.findElement(By.className("css-775knl")).getText();
            System.out.println("Job Company Name is: " + JobCompanyName);
            String jobLocation = m1.findElement(By.cssSelector("[data-testid='job-location']")).getText();
            System.out.println(jobLocation);

            boolean isPresent = !m1.findElements(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]")).isEmpty();

            if (isPresent) {
                boolean isChildPresent = !m1
                        .findElement(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]"))
                        .findElements(By.xpath("//span[contains(@class,'icl-u-xs-mt')]")).isEmpty();
                if (isChildPresent) {
                    sjobcategory = m1
                            .findElement(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]"))
                            .findElement(By.xpath("//span[contains(@class,'icl-u-xs-mt')]")).getText();
                    System.out.println("Job Category is: " + sjobcategory);
                } else {
                    System.out.println("Job Category is not available");

                }

            } else {

                System.out.println("Job Category is not available");

            }

            String JobDescription = driver.findElement(By.xpath("//div[contains(@class,'jobsearch-jobDescriptionText')]")).getText();

            System.out.println("Job details is: " + JobDescription);


            driver.switchTo().defaultContent();
            Thread.sleep(2000);
            jobtitle.click();
            System.out.println("-------*******---------");

            sh.getRow(i + 1).createCell(0).setCellValue(jobTitle);
            sh.getRow(i + 1).createCell(1).setCellValue(jobLocation);
            sh.getRow(i + 1).createCell(2).setCellValue(sjobcategory);
            sh.getRow(i + 1).createCell(3).setCellValue(JobCompanyName);
            sh.getRow(i + 1).createCell(4).setCellValue(DatePosted);
            sh.getRow(i + 1).createCell(5).setCellValue(JobDescription);
            sh.getRow(i + 1).createCell(6).setCellValue(jobLink);

        }

        try {
            //File excel = new File("C:\\Users\\Swati\\Desktop\\RestAssured.xlsx");

            File excel = new File("C:\\Users\\Hazrtine\\Downloads\\testExcel.xlsx");
            if (excel.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(excel);
                wb.write(fos);
            }
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @AfterTest
    public void afterTest() {
        driver.close();
        driver.quit();
    }
}