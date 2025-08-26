package com.linkshortener.controller;

import com.linkshortener.entity.Link;
import com.linkshortener.service.LinkService;
import com.linkshortener.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class QRCodeController {
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private LinkService linkService;
    
    @GetMapping("/{linkId}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long linkId) {
        try {
            // Find the link by ID
            Optional<Link> linkOpt = linkService.findById(linkId);
            if (linkOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Link link = linkOpt.get();
            if (link.getQrCodePath() == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] qrCodeData = qrCodeService.getQRCode(link.getQrCodePath());
            if (qrCodeData == null) {
                return ResponseEntity.notFound().build();
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeData.length);
            
            return new ResponseEntity<>(qrCodeData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{linkId}/download")
    public ResponseEntity<byte[]> downloadQRCode(@PathVariable Long linkId) {
        try {
            // Find the link by ID
            Optional<Link> linkOpt = linkService.findById(linkId);
            if (linkOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Link link = linkOpt.get();
            if (link.getQrCodePath() == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] qrCodeData = qrCodeService.getQRCode(link.getQrCodePath());
            if (qrCodeData == null) {
                return ResponseEntity.notFound().build();
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeData.length);
            headers.setContentDispositionFormData("attachment", "qr-code-" + linkId + ".png");
            
            return new ResponseEntity<>(qrCodeData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
