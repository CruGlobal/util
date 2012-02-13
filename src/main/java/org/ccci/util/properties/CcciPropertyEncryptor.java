package org.ccci.util.properties;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.ccci.util.NkUtil;
import org.jasypt.util.text.TextEncryptor;

public class CcciPropertyEncryptor
{

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("key: ");
        String key = in.readLine();
        TextEncryptor encryptor = new CcciPropsTextEncryptor(key, false);
        while(true)
        {
            System.out.println("value: ");
            String val = in.readLine();
            if(NkUtil.isBlank(val)) break;
            
            
            if(val.startsWith("encryptedData:"))
            {
                System.out.println(encryptor.decrypt(val.substring("encryptedData:".length())));
            }
            else
            {
                System.out.println("encryptedData:"+encryptor.encrypt(val));
            }
        }
        in.close();
    }

}
