package com.springboot.project.controller;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.springboot.project.common.baseController.BaseController;
import lombok.SneakyThrows;

@RestController
public class AuthorizationAlipayController extends BaseController {

    @GetMapping("/sign_in/alipay/generate_qr_code")
    @SneakyThrows
    public ResponseEntity<?> generateQrCode() {
        var url = new URIBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm")
                .setParameter("app_id", "2021002177648626").setParameter("scope", "auth_user")
                .setParameter("redirect_uri", "https://kame-sennin.com/abc").setParameter("state", "init").build();
        var bitMatrix = new QRCodeWriter().encode(url.toString(), BarcodeFormat.QR_CODE, 200, 200);
        try (var output = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", output);
            var pngData = output.toByteArray();
            var imageData = Base64.getEncoder().encodeToString(pngData);
            var imageUrl = "data:image/png;base64," + imageData;
            return ResponseEntity.ok(imageUrl);
        }
    }

}
