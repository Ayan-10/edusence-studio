package com.example.edusence_studio.services.modules;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PDFGeneratorService {

    public byte[] generatePdfFromText(String title, String content) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
            PDType1Font bodyFont = PDType1Font.HELVETICA;

            float yPosition = 750;
            float margin = 50;
            float lineHeight = 14;

            // Add title
            contentStream.beginText();
            contentStream.setFont(titleFont, 16);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText(title);
            contentStream.endText();
            yPosition -= 30;

            // Split content into lines that fit the page width
            List<String> lines = wrapText(content, 80); // 80 chars per line

            // Add content
            contentStream.setFont(bodyFont, 12);
            for (String line : lines) {
                if (yPosition < 50) {
                    // New page needed
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(bodyFont, 12);
                    yPosition = 750;
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(line);
                contentStream.endText();
                yPosition -= lineHeight;
            }

            contentStream.close();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error generating PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] paragraphs = text.split("\n\n");
        
        for (String para : paragraphs) {
            if (para.trim().isEmpty()) {
                lines.add("");
                continue;
            }
            
            String[] words = para.trim().split("\\s+");
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                String testLine = currentLine.length() > 0 
                    ? currentLine + " " + word 
                    : word;
                
                if (testLine.length() > maxWidth && currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine.append(currentLine.length() > 0 ? " " + word : word);
                }
            }
            
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
            lines.add(""); // Empty line between paragraphs
        }
        
        return lines;
    }
}
