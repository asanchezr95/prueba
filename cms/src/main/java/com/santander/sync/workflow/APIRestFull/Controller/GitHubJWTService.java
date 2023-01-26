package com.santander.sync.workflow.APIRestFull.Controller;

import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.stereotype.Service;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

/**
 * The type Git hub jwt service.
 */
@Service
public class GitHubJWTService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubJWTService.class);

    private final String appId;
    private final String privateKey;

    /**
     * Instantiates a new Git hub jwt service.
     *
     * @param appId      the app id
     * @param privateKey the private key
     */
    public GitHubJWTService(@Value("113742") final String appId,
                            @Value("-----BEGIN RSA PRIVATE KEY-----\n"
                                   + "MIIEogIBAAKCAQEA4M3dqF4xGJMNnWNDmUusleXWEnR/gMqDBClXMSZpvJ7DUrL9\n"
                                   + "RT46naYNxhVPHoJnNJQByDQszj1JpKqYV6YHjnZQzlO4OWGADTedii3syajzLntG\n"
                                   + "weyehyVwVXx/1AAONb18buKGYjjaG94SP3EU+wvLFsqaTq+zE2YvUJYEeOngSduR\n"
                                   + "lbNQy3puu70tpaayj09LbiZPHW/s9caEIION5j0b4dhRvdSx7ij844wunPeHnt9d\n"
                                   + "Jww47pwxQ+DBPVtSaK37oj/GGh/36e09YQL6J1qlGbF/oxgAKej/p25vqdEfQQsS\n"
                                   + "oMfx2FNBKmGdvECvXbYGpRhJ6MyDUJV5HC+0yQIDAQABAoIBADE8pbnEt5gcTTUV\n"
                                   + "kq62plxGdLaiEXMR59Q3gcNgGHSZiUpJrbNGx+vTfNzPuf6CrTwU1rcUxmN9hO2t\n"
                                   + "96Pq12jafSRTrdvTgQpkDfs/x8b7XBfoJD1BKA+Noab3l7/FK1eV4vUeZDSLIypL\n"
                                   + "X0/J4sCBer/JLDbjwKMVFHPgB6a7mbAw0V8MaYwKjixk1ndfFLVxCCFHPB3dd1sx\n"
                                   + "NVlVUQBSnin3dTwValybVRIiuHXVbMMwh6350oYAaWD7CYCCLm154U8OuyUZU0zt\n"
                                   + "EyS/OSbgHskbFrz8qqZvSvswPLnksmdcsyPvq76EFGJIm9FhrF8jxIV1aU4IHGKK\n"
                                   + "hf9BewECgYEA8I2fX3nVjweEuJRqUdpGp7fwGNydqwNjeQaVeswPXm8p0j8asxFW\n"
                                   + "AqJbtUz9Z59qbochITfj1BssfGACC66sl/YrbgD8mP3Lb2NuoVQdfEK7U65Bc/af\n"
                                   + "EdiC4k8iUFy9qjCwB/ta4JM3pfKrXYiG27TNslCtll/o7lCBZkfPpHkCgYEA7z1Z\n"
                                   + "hhznji3lemPoeg5n/saTfyU3gPKbCanExRWgxC1+9xYIAUXBTrPkFAT+hOuuy4lK\n"
                                   + "r2Mp9FPUcsULSZgImUfhIn9pTaTvnf5xxG5yLJCZFPlDCJGx+RINBcthj6hdUtHz\n"
                                   + "Mt67gqjzLT342AwbLFr3mhzzsbm7IBmHvXn2XtECgYA59uZga0s6m+UvVRQJhT9h\n"
                                   + "SsFgPEkB3F8r2ppKbMGHT+IT94DHhXXkTJsspNCrF47d81HYX09W3PVrQvc7OWv9\n"
                                   + "ciQk7bfwZbePr7YIyewQ9UOOdn7vIo5SDon7XnY2RyKsz+6a/cZ4NwFBy6ffcfaN\n"
                                   + "GpQ2U5qAkbvfMSb9LnCqgQKBgAqxMjwVNKAdGwFyT6SM2kb/tv3auvVPYnB3Hu0z\n"
                                   + "/0BlTCRc3rS32mDbF1lxs7JbGi/MjgLyqqBZ0sfiWJPMcK3xEXXKPfmTYoYsJgeC\n"
                                   + "bCXjvIO8dCuA33RjQyO8KDsUaj9WVMk0PSskw+GEwIdfWyOLWT+RBa8uvtc7bN6i\n"
                                   + "AcnRAoGAZReqfhWOn07OMUO7GeUCuao+pdiDo14dMlAahJcPntgu/N5s2Qty3CB+\n"
                                   + "80AUD8CtdTJRm/dWzCMVNfBnlRkBiSMKBZ1DUTF3uXq6DTNjTOsaM1FafyI7ww+x\n"
                                   + "vuzMyo+4nVNpZguRJAe/B7LdNAKU4dvIbfM0r0wAaQ0NA03Z7Ds=\n"
                                   + "-----END RSA PRIVATE KEY-----") final String privateKey) {
        this.appId = appId;
        this.privateKey = privateKey;
    }

    /**
     * Generate jwt string.
     *
     * @return the string
     * @throws InvalidKeySpecException  the invalid key spec exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public String generateJWT() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(9l, ChronoUnit.MINUTES)))
                .setIssuer(this.appId)
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String rsaPrivateKey = this.privateKey;

        rsaPrivateKey = rsaPrivateKey.replace("-----BEGIN PRIVATE KEY-----", "");
        rsaPrivateKey = rsaPrivateKey.replace("-----END PRIVATE KEY-----", "");

        RsaKeyConverters.pkcs8();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privKey = kf.generatePrivate(keySpec);
        return privKey;
    }
}