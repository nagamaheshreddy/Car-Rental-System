package com.Trimble.TrimbleCars.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Trimble.TrimbleCars.Request.RequestDTO;
import com.Trimble.TrimbleCars.Service.LeaseReportService;



@RestController
@RequestMapping("/api/leases")
public class LeaseReportController {

    @Autowired
    private LeaseReportService leaseReportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLeaseHistory(@RequestBody RequestDTO requestDTO) {
        try {
            // Generate the PDF report
            byte[] pdfReport = leaseReportService.generateLeaseHistoryReport(requestDTO.getCustomerId());

            // Create headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=lease_history_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfReport);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}

