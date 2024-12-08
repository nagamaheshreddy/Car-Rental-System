package com.Trimble.TrimbleCars.Service;

import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Repository.CustomerRepository;
import com.Trimble.TrimbleCars.Repository.LeaseRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class LeaseReportService {

    @Autowired
    private LeaseRepository leaseRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    public byte[] generateLeaseHistoryReport(Long customerId) {
        // Fetch lease history data from the repository
        List<Lease> leaseHistory = leaseRepository.findByCustomer_CustomerId(customerId);
        
        if (leaseHistory.isEmpty()) {
            throw new RuntimeException("No lease history found for Customer ID: " + customerId);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Initialize PDF document
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            // Open the document for writing
            document.open();
            
            // Add Logo to the PDF
            Image logo = Image.getInstance(getClass().getClassLoader().getResource("static/images/Trimble.png"));
            logo.scaleToFit(100, 100);  
            logo.setAlignment(Image.ALIGN_CENTER); 
            document.add(logo);

            // Add Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Lease History Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add a blank line for spacing
            document.add(new Paragraph("\n"));

            // Add Table with Headers
            PdfPTable table = new PdfPTable(5); // 5 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 3 , 2, 2}); // Set column widths

            // Header Row
            table.addCell(createHeaderCell("Customer ID"));
            table.addCell(createHeaderCell("Lease ID"));
            table.addCell(createHeaderCell("Car Model"));
            table.addCell(createHeaderCell("Start Date"));
            table.addCell(createHeaderCell("End Date"));
            
            // Populate Table Rows with Lease Data
            for (Lease lease : leaseHistory) {
                String carModel = (lease.getCar() != null) ? lease.getCar().getModel() : "N/A";
                String startDate = (lease.getLeaseStartDate() != null) ? lease.getLeaseStartDate().toString() : "N/A";
                String endDate = (lease.getLeaseEndDate() != null) ? lease.getLeaseEndDate().toString() : "N/A";

                table.addCell(String.valueOf(customerId));
                table.addCell(String.valueOf(lease.getLeaseId()));
                table.addCell(carModel);
                table.addCell(startDate);
                table.addCell(endDate);
            }

            // Add Table to Document
            document.add(table);

            // Close the document
            document.close();

            return outputStream.toByteArray(); // Return the PDF as a byte array
        } catch (Exception e) {
            throw new RuntimeException("Error generating lease history report", e);
        }
    }
    
 // Helper method to create header cells
    private PdfPCell createHeaderCell(String text) {
    	PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        return cell;
    }

}
