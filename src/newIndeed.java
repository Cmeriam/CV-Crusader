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
import java.util.List;


public class newIndeed {
    WebDriver driver;

    int i, jobsperpage = 0;

    //@Parameters("keyWord")
    @BeforeTest
    public void beforeTest() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Hazrtine\\Downloads\\chromedriver-win32\\chromedriver-win32\\chromedriver.exe");
        this.driver = new ChromeDriver();

        driver.get("https://www.indeed.com");
        driver.manage().window().maximize();

//		driver.findElement(By.xpath("//*[@id='text-input-where']")).sendKeys(Keys.chord(Keys.CONTROL, "a"));
//		driver.findElement(By.xpath("//*[@id='text-input-where']")).sendKeys(Keys.DELETE);
//		Thread.sleep(2000);
    }


    @Test
    public void parameterPassing() throws InterruptedException {
        // Verifying the keyword on UI
        driver.findElement(By.id("text-input-what")).clear();
        // Thread.sleep(2000);
        driver.findElement(By.id("text-input-what")).sendKeys("I HATE BLACK PEOPLE");
        // Thread.sleep(1000);

        WebElement WhereBox = driver.findElement(By.id("label-text-input-what"));
        // Sending keyword value
        WhereBox.click();
        WhereBox.sendKeys(Keys.CONTROL + "a");
        WhereBox.sendKeys(Keys.DELETE);
        System.out.println("The Where box is cleared");
        Thread.sleep(2000);

        WebElement FindButton = driver.findElement(By.className("icl-WhatWhere-buttonWrapper"));
        FindButton.submit();
        System.out.println("The Find Jobs button is clicked");
        Thread.sleep(1000);

        driver.findElement(By.id("filter-dateposted")).click();
        // yosegi-FilterPill-dropdownListItemLink
        driver.findElement(By.linkText("Last 24 hours")).click();
        if (driver.findElement(By.id("popover-foreground")).isDisplayed()) {
            driver.findElement(By.id("popover-x")).click();
        }
        Thread.sleep(1000);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,300)");

        Reporter.log("Scrolled page");


        System.out.println("Page Title:" + driver.getTitle());
        System.out.println("Jobs Count: " + driver.findElement(By.id("searchCount")).getText());
        String Totaljobs = driver.findElement(By.id("searchCount")).getText().split(" ")[3];
        System.out.println(Totaljobs);
        List<WebElement> jobs = driver.findElements(By.className("slider_container"));

        int jobsPerPage = jobs.size();
        if (jobsperpage >= 15) {

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
        sh.getRow(0).createCell(7).setCellValue("Date Scrapped");

        for (int i = 0; i < jobsPerPage; i++) {
            String sjobcategory = "";
            sh.createRow(i + 1);
            String DatePosted = jobs.get(i).findElement(By.className("date")).getText();
            System.out.println("Date Posted: " + DatePosted);

            WebElement resultContent = jobs.get(i).findElement(By.className("resultContent"));
            WebElement jobTitle = resultContent.findElement(By.className("jobTitle"));
            // System.out.println(jobTitle.getText().substring(3));
            jobTitle.click();
            Thread.sleep(1000);
            WebElement jobcontainer = driver.findElement(By.id("vjs-container"));
            jobcontainer.click();
            Thread.sleep(1000);
            WebElement wFrame = jobcontainer.findElement(By.xpath("//*[@id=\"vjs-container-iframe\"]"));
            // System.out.println("Identifying the IFrame ");
            wFrame.click();
            Thread.sleep(1000);
            // System.out.println("Clicking on the IFrame ");
            Thread.sleep(1000);
            String jobLink = wFrame.getAttribute("src");
            System.out.println("The Job Link is: " + jobLink);
            Thread.sleep(1000);
            driver.switchTo().frame(wFrame);
            Thread.sleep(2000);
            WebElement m = driver.findElement(By.xpath("//body"));
            WebElement m1 = m.findElement(By.className("jobsearch-JobComponent-embeddedHeader"));
            // String content = m1.getText();
            m1.click();
            // System.out.println(content);

            String jobtitle = m1.findElement(By.xpath("//div/h1[contains(@class,'icl-u-xs-mb')]")).getText();
            // System.out.println(content1);

            // System.out.println("Job Title is: " + content1);


            String jobcompnayname = m1.findElement(By.xpath("//div[contains(@class,'icl-u-lg-mr')]")).getText();
            System.out.println("Job CompanyName is: " + jobcompnayname);
            String joblocation = m1.findElement(By.xpath("//div[contains(@class,'icl-u-xs-mt')]//div[2]")).getText();

            Object[] a = joblocation.lines().toArray();

            if (jobtitle.contains("- job post")) {
                // char[] ch = content1.toCharArray();
                for (int icnt = jobtitle.length() - 1; icnt >= 0; icnt--) {
                    if (jobtitle.charAt(icnt) == '-') {
                        System.out.println("Job Title is: " + jobtitle.substring(0, icnt - 1));
                        break;

                    }

                }
            }


            Boolean isPresent = m1.findElements(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]"))
                    .size() > 0;

            if (isPresent) {
                Boolean isChildPresent = m1
                        .findElement(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]"))
                        .findElements(By.xpath("//span[contains(@class,'icl-u-xs-mt')]")).size() > 0;
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

            // jobsearch-InlineCompanyRating
            WebElement jobdetail = m.findElement(By.className("jobsearch-JobComponent-embeddedBody"));
            jobdetail.click();
            /*
             * String DatePosted = jobdetail.findElement(By.xpath(
             * "//div[contains(@class,'jobsearch-JobMetadataFooter')]")).getText();
             * //System.out.println(DatePosted);
             *
             * Object[] dp = DatePosted.lines().toArray();
             *
             * System.out.println("Job Posted is: " + dp[1].toString());
             */

            String JobDescription = jobdetail
                    .findElement(By.xpath("//div[contains(@class,'jobsearch-jobDescriptionText')]")).getText();

            // String content =
            // m.findElement(By.xpath("//[@id=\"viewJobSSRRoot\"]/div[1]/div/div/div/div[1]/div/div[1]/div/div[1]/div[1]")).getText();

            System.out.println("Job details is: " + JobDescription);


            driver.switchTo().defaultContent();
            Thread.sleep(2000);
            jobTitle.click();
            System.out.println("-------*******---------");

            sh.getRow(i + 1).createCell(0).setCellValue(jobtitle);
            sh.getRow(i + 1).createCell(1).setCellValue(joblocation);
            sh.getRow(i + 1).createCell(2).setCellValue(sjobcategory);
            sh.getRow(i + 1).createCell(3).setCellValue(jobcompnayname);
            sh.getRow(i + 1).createCell(4).setCellValue(DatePosted);
            sh.getRow(i + 1).createCell(5).setCellValue(JobDescription);
            sh.getRow(i + 1).createCell(6).setCellValue(jobLink);
            sh.getRow(i + 1).createCell(7).setCellValue(DatePosted);

        }

        try {
            //File excel = new File("C:\\Users\\Swati\\Desktop\\RestAssured.xlsx");

            File excel = new File("C:\\Users\\Swati\\Desktop\\RestAssured.xlsx");
            excel.createNewFile();
            FileOutputStream fos = new FileOutputStream(excel);
            wb.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterTest
    public void afterTest() {
        driver.close();
        driver.quit();
    }
}