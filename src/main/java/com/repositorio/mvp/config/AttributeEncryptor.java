package com.repositorio.mvp.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

/**
 * Conversor JPA para criptografar automaticamente colunas no banco de dados usando AES.
 */
@Component
@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    
    // ATENÇÃO: Em produção, essa chave DEVE vir de variável de ambiente!
    // Exemplo usando 32 caracteres para AES-256
    private static final String SECRET_KEY = System.getenv("DB_ENCRYPTION_KEY") != null 
            ? System.getenv("DB_ENCRYPTION_KEY") 
            : "ChaveSecretaDe32CaracteresParaO!"; 

    private final Key key;
    private final Cipher cipher;

    public AttributeEncryptor() throws Exception {
        this.key = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
        this.cipher = Cipher.getInstance(AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao criptografar o dado para o banco.", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao descriptografar o dado do banco.", e);
        }
    }
}