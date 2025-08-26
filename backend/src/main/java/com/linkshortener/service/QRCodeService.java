package com.linkshortener.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class QRCodeService {
    
    @Value("${app.qr-code-size:300}")
    private int qrCodeSize;
    
    private static final String QR_CODE_DIR = "qr-codes";
    
    public String generateQRCode(String url, Long linkId) throws WriterException, IOException {
        // Create QR code directory if it doesn't exist
        Path qrCodeDirectory = Paths.get(QR_CODE_DIR);
        if (!Files.exists(qrCodeDirectory)) {
            Files.createDirectories(qrCodeDirectory);
        }
        
        // Generate QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
        
        // Save QR code as PNG file
        String fileName = "qr_" + linkId + ".png";
        Path qrCodePath = qrCodeDirectory.resolve(fileName);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodePath);
        
        return qrCodePath.toString();
    }
    
    public byte[] getQRCode(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        }
        return null;
    }
    
    public void deleteQRCode(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete QR code file: " + e.getMessage());
        }
    }
}
