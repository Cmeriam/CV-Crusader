package JobScrapping

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.JavascriptExecutor
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.testng.Reporter
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class NewIndeed {
    private var driver: WebDriver? = null

    @Test
    fun parameterPassing() {
        driver?.findElement(By.id("text-input-what"))?.clear()
        driver?.findElement(By.id("text-input-what"))?.sendKeys("RestAssured")
        val whereBox = driver?.findElement(By.id("text-input-where"))
        whereBox?.click()
        whereBox?.sendKeys(Keys.CONTROL + "a")
        whereBox?.sendKeys(Keys.DELETE)
        Thread.sleep(2000)

        val findButton = driver?.findElement(By.className("icl-WhatWhere-buttonWrapper"))
        findButton?.submit()
        Thread.sleep(1000)

        driver?.findElement(By.id("filter-dateposted"))?.click()
        driver?.findElement(By.linkText("Last 24 hours"))?.click()
        if (driver?.findElement(By.id("popover-foreground"))?.isDisplayed == true) {
            driver?.findElement(By.id("popover-x"))?.click()
        }
        Thread.sleep(1000)

        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollBy(0,300)")

        Reporter.log("Scrolled page")

        println("Page Title:" + driver?.title)
        println("Jobs Count: " + driver?.findElement(By.id("searchCount"))?.text)
        val totalJobs = driver?.findElement(By.id("searchCount"))?.text?.split(" ")?.get(3)
        println(totalJobs)
        val jobs = driver?.findElements(By.className("slider_container"))
        val jobsPerPage = jobs?.size ?: 0
        println(jobsPerPage)

        val wb = XSSFWorkbook()
        val sh = wb.createSheet()

        sh.createRow(0)
        sh.getRow(0).createCell(0).setCellValue("JobTitle")
        sh.getRow(0).createCell(1).setCellValue("JobLocation")
        sh.getRow(0).createCell(2).setCellValue("JobCategory")
        sh.getRow(0).createCell(3).setCellValue("Job company name")
        sh.getRow(0).createCell(4).setCellValue("Job date posted")
        sh.getRow(0).createCell(5).setCellValue("Job description")
        sh.getRow(0).createCell(6).setCellValue("Job link")
        sh.getRow(0).createCell(7).setCellValue("Date Scrapped")

        for (i in 0 until jobsPerPage) {
            sh.createRow(i + 1)
            val datePosted = jobs[i].findElement(By.className("date")).text
            println("Date Posted: $datePosted")

            val resultContent = jobs[i].findElement(By.className("resultContent"))
            val jobTitle = resultContent.findElement(By.className("jobTitle"))
            jobTitle.click()
            Thread.sleep(1000)
            val jobcontainer = driver?.findElement(By.id("vjs-container"))
            jobcontainer?.click()
            Thread.sleep(1000)
            val wFrame = jobcontainer?.findElement(By.xpath("//*[@id=\"vjs-container-iframe\"]"))
            val jobLink = wFrame?.getAttribute("src")
            println("The Job Link is: $jobLink")
            Thread.sleep(1000)
            driver?.switchTo()?.frame(wFrame)
            Thread.sleep(2000)
            val m = driver?.findElement(By.xpath("//body"))
            val m1 = m?.findElement(By.className("jobsearch-JobComponent-embeddedHeader"))

            val jobTitleText = m1?.findElement(By.xpath("//div/h1[contains(@class,'icl-u-xs-mb')]"))?.text
            val jobCompanyName = m1?.findElement(By.xpath("//div[contains(@class,'icl-u-lg-mr')]"))?.text
            println("Job CompanyName is: $jobCompanyName")
            val jobLocation = m1?.findElement(By.xpath("//div[contains(@class,'icl-u-xs-mt')]//div[2]"))?.text

            val a = jobLocation?.lines()?.toList()
            if (jobTitleText?.contains("- job post") == true) {
                for (icnt in jobTitleText.length - 1 downTo 0) {
                    if (jobTitleText[icnt] == '-') {
                        println("Job Title is: ${jobTitleText.substring(0, icnt - 1)}")
                        break
                    }
                }
            }

            val isPresent = m1?.findElements(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]"))?.size ?: 0 > 0
            if (isPresent) {
                val isChildPresent = m1.findElement(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]"))
                        .findElements(By.xpath("//span[contains(@class,'icl-u-xs-mt')]")).size > 0
                if (isChildPresent) {
                    val jobCategory = m1.findElement(By.xpath("//div[contains(@class,'jobsearch-JobMetadataHeader-item ')]"))
                            .findElement(By.xpath("//span[contains(@class,'icl-u-xs-mt')]")).text
                    println("Job Category is: $jobCategory")
                } else {
                    println("Job Category is not available")
                }
            } else {
                println("Job Category is not available")
            }

            val jobDetail = m.findElement(By.className("jobsearch-JobComponent-embeddedBody"))
            jobDetail.click()
            val jobDescription = jobDetail
                    .findElement(By.xpath("//div[contains(@class,'jobsearch-jobDescriptionText')]")).text
            println("Job details is: $jobDescription")
            driver?.switchTo()?.defaultContent()
            Thread.sleep(2000)
            jobTitle.click()
            println("-------*******---------")

            sh.getRow(i + 1).createCell(0).setCellValue(jobTitleText)
            sh.getRow(i + 1).createCell(1).setCellValue(jobLocation)
            sh.getRow(i + 1).createCell(2).setCellValue(jobCategory)
            sh.getRow(i + 1).createCell(3).setCellValue(jobCompanyName)
            sh.getRow(i + 1).createCell(4).setCellValue(datePosted)
            sh.getRow(i + 1).createCell(5).setCellValue(jobDescription)
            sh.getRow(i + 1).createCell(6).setCellValue(jobLink)
            sh.getRow(i + 1).createCell(7).setCellValue(datePosted)
        }

        try {
            val excel = File("C:\\Users\\Swati\\Desktop\\RestAssured.xlsx")
            excel.createNewFile()
            val fos = FileOutputStream(excel)
            wb.write(fos)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BeforeTest
    fun beforeTest() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Swati\\chromedriver.exe")
        driver = ChromeDriver()
        driver?.get("https://www.indeed.com")
        driver?.manage()?.window()?.maximize()
    }

    @AfterTest
    fun afterTest() {
        driver?.close()
        driver?.quit()
    }
}
