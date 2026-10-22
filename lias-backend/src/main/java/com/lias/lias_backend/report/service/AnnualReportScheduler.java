package com.lias.lias_backend.report.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.nio.file.*;
@Component @RequiredArgsConstructor
public class AnnualReportScheduler {
 private final ReportService reports;
 @Value("${app.upload.dir:uploads}") private String uploadDir;
 @Scheduled(cron="${app.reports.cron:0 0 2 1 1 *}",zone="UTC")
 public void generate()throws Exception {var folder=Paths.get(uploadDir,"reports");Files.createDirectories(folder);int year=LocalDate.now().getYear()-1;Files.write(folder.resolve("LIAS_Annual_Report_"+year+".pdf"),reports.generateAnnualReport(year));}
}
