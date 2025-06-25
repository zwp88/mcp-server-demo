package com.example.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.internal.Logger;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;

/**
 * @author Administrator
 */
public class JwtUtil {
    static org.slf4j.Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    public static String getJwtToken(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        // Private key

        privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").trim();
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EdDSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // Header
        String headerJson = "{\"alg\": \"EdDSA\", \"kid\": \"C95D3AJKVH\"}";

        // Payload
        long iat = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - 30;
        long exp = iat + 900;
        String payloadJson = "{\"sub\": \"39TNF2PCY4\", \"iat\": " + iat + ", \"exp\": " + exp + "}";

        // Base64url header+payload
        String headerEncoded = Base64.getUrlEncoder().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payloadEncoded = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String data = headerEncoded + "." + payloadEncoded;

        // Sign
        Signature signer = Signature.getInstance("EdDSA");
        signer.initSign(privateKey);
        signer.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signature = signer.sign();

        String signatureEncoded = Base64.getUrlEncoder().encodeToString(signature);

        return data + "." + signatureEncoded;
    }


    public static Claims validateEdDSAJwt(String jwtToken, String publicKeyPem) throws Exception {
        // 处理PEM格式的公钥
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        // 解码Base64编码的公钥
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);

        // 生成PublicKey对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EdDSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        return (Claims) Jwts.parser().verifyWith(publicKey).build().parse(jwtToken).getPayload();
        // 验证JWT并解析claims
       // return Jwts.parser().setSigningKey(publicKey).build().parseClaimsJws(jwtToken).getBody();


    }

    public static void main(String[] args) throws Exception {


        try {
            Claims claims =validateEdDSAJwt(getJwtToken("""
                -----BEGIN PRIVATE KEY-----
                MC4CAQAwBQYDK2VwBCIEIK9K0NSzrutZI8pp+JxWRMd2wip0Q0ajWTBq9xXt3dNU
                -----END PRIVATE KEY-----
                """), """
                -----BEGIN PUBLIC KEY-----
                MCowBQYDK2VwAyEAZGplfDwghEEoC56x6ZipQYBWwnjOcIfq7//Hsy46ibg=
                -----END PUBLIC KEY-----
                """);
            logger.info("JWT验证成功!");
            logger.info("Subject: {}", claims.getSubject());
            logger.info("Issued At: {}", claims.getIssuedAt());
            logger.info("Expiration: {}", claims.getExpiration());
        } catch (SignatureException e) {
            logger.error("签名验证失败: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("JWT验证失败: {}", e.getMessage());
        }

// Print Token
//        System.out.println("Signature:\n" + signatureEncoded);
//        System.out.println("JWT:\n" + jwt);
    }
}
