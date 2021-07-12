package com.springboot.project.controller;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.springboot.project.model.UserModel;

@RestController
public class AuthorizationController extends BaseController {

	@PostMapping("/sign_in/email/send_verification_code")
	public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
		this.authorizationEmailUtil.sendVerificationCode(email);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/sign_in/email/generate_token")
	public ResponseEntity<?> generateToken(@RequestParam String email, @RequestParam String verificationCode)
			throws URISyntaxException {
		this.authorizationEmailUtil.checkVerificationCode(email, verificationCode);
		var tokenModel = this.tokenUtil.generateToken(email);
		return ResponseEntity.ok(tokenModel);
	}

	@GetMapping("/sign_in/alipay/generate_qr_code")
	public ResponseEntity<?> generateQrCode() throws Throwable {
		var url = new URIBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm")
				.setParameter("app_id", "2021002177648626").setParameter("scope", "auth_user")
				.setParameter("redirect_uri", "https://kame-sennin.com/abc").setParameter("state", "init").build();
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(url.toString(), BarcodeFormat.QR_CODE, 200, 200);
		try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
			byte[] pngData = pngOutputStream.toByteArray();
			String imageData = Base64.getEncoder().encodeToString(pngData);
			var imageUrl = "data:image/png;base64," + imageData;
			return ResponseEntity.ok(imageUrl);
		}
	}

	@GetMapping("/get_user_info")
	public ResponseEntity<?> getUserInfo() {
		UserModel userModel = new UserModel();
		userModel.setId(this.permissionUtil.getUserId(request));
		userModel.setUsername(this.permissionUtil.getUsername(request));
		userModel.setEmail(this.permissionUtil.getEmail(request));
		return ResponseEntity.ok(userModel);
	}

}
