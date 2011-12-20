package org.ccci.util.properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.util.text.TextEncryptor;

public class CcciPropsTextEncryptor implements TextEncryptor
{
    private StandardPBEStringEncryptor encryptor;
    
    public CcciPropsTextEncryptor(String pwd, boolean strong)
    {
        encryptor = new StandardPBEStringEncryptor();
        
        if(strong) encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        else encryptor.setAlgorithm("PBEWithMD5AndDES");
        
        encryptor.setPassword(pwd);
    }

    @Override
    public String encrypt(String message)
    {
        return encryptor.encrypt(message);
    }

    @Override
    public String decrypt(String encryptedMessage)
    {
        return encryptor.decrypt(encryptedMessage);
    }

}
