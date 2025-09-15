package com.zikan.salary;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@SpringBootApplication
public class SalaryDisbursementApplication {
    public static void main(String[] args) throws InterruptedException {

//        System.setProperty("webdriver.chrome.driver", "C:\\Browser drivers\\chromedriver.exe");

        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.shaadi.com");
        driver.manage().window().maximize();
        Thread.sleep(3000);
        driver.quit();


        // Uncomment this to start Spring Boot application
        // SpringApplication.run(SalaryDisbursementApplication.class, args);
    }
}