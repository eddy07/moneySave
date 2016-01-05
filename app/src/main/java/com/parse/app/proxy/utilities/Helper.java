package com.parse.app.proxy.utilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Helper {
	
	
    public static String javascriptEscape(String s) {
        Map<String, String> javascriptEscapeReference = new HashMap<String, String>();

        // Matching from http://www.javascripter.net/faq/accentedcharacters.htm
        javascriptEscapeReference.put("À", "%C0");
        javascriptEscapeReference.put("Á", "%C1");
        javascriptEscapeReference.put("Â", "%C2");
        javascriptEscapeReference.put("Ã", "%C3");
        javascriptEscapeReference.put("Ä", "%C4");
        javascriptEscapeReference.put("Å", "%C5");
        javascriptEscapeReference.put("Æ", "%C6");
        javascriptEscapeReference.put("Ç", "%C7");
        javascriptEscapeReference.put("È", "%C8");
        javascriptEscapeReference.put("É", "%C9");
        javascriptEscapeReference.put("Ê", "%CA");
        javascriptEscapeReference.put("Ë", "%CB");
        javascriptEscapeReference.put("Ì", "%CC");
        javascriptEscapeReference.put("Í", "%CD");
        javascriptEscapeReference.put("Î", "%CE");
        javascriptEscapeReference.put("Ï", "%CF");
        javascriptEscapeReference.put("Ð", "%D0");
        javascriptEscapeReference.put("Ñ", "%D1");
        javascriptEscapeReference.put("Ò", "%D2");
        javascriptEscapeReference.put("Ó", "%D3");
        javascriptEscapeReference.put("Ô", "%D4");
        javascriptEscapeReference.put("Õ", "%D5");
        javascriptEscapeReference.put("Ö", "%D6");
        javascriptEscapeReference.put("Ø", "%D8");
        javascriptEscapeReference.put("Ù", "%D9");
        javascriptEscapeReference.put("Ú", "%DA");
        javascriptEscapeReference.put("Û", "%DB");
        javascriptEscapeReference.put("Ü", "%DC");
        javascriptEscapeReference.put("Ý", "%DD");
        javascriptEscapeReference.put("Þ", "%DE");
        javascriptEscapeReference.put("ß", "%DF");
        javascriptEscapeReference.put("à", "%E0");
        javascriptEscapeReference.put("á", "%E1");
        javascriptEscapeReference.put("â", "%E2");
        javascriptEscapeReference.put("ã", "%E3");
        javascriptEscapeReference.put("ä", "%E4");
        javascriptEscapeReference.put("å", "%E5");
        javascriptEscapeReference.put("æ", "%E6");
        javascriptEscapeReference.put("ç", "%E7");
        javascriptEscapeReference.put("è", "%E8");
        javascriptEscapeReference.put("é", "%E9");
        javascriptEscapeReference.put("ê", "%EA");
        javascriptEscapeReference.put("ë", "%EB");
        javascriptEscapeReference.put("ì", "%EC");
        javascriptEscapeReference.put("í", "%ED");
        javascriptEscapeReference.put("î", "%EE");
        javascriptEscapeReference.put("ï", "%EF");
        javascriptEscapeReference.put("ð", "%F0");
        javascriptEscapeReference.put("ñ", "%F1");
        javascriptEscapeReference.put("ò", "%F2");
        javascriptEscapeReference.put("ó", "%F3");
        javascriptEscapeReference.put("ô", "%F4");
        javascriptEscapeReference.put("õ", "%F5");
        javascriptEscapeReference.put("ö", "%F6");
        javascriptEscapeReference.put("ø", "%F8");
        javascriptEscapeReference.put("ù", "%F9");
        javascriptEscapeReference.put("ú", "%FA");
        javascriptEscapeReference.put("û", "%FB");
        javascriptEscapeReference.put("ü", "%FC");
        javascriptEscapeReference.put("ý", "%FD");
        javascriptEscapeReference.put("þ", "%FE");
        javascriptEscapeReference.put("ÿ", "%FF");
        javascriptEscapeReference.put("Œ", "%u0152");
        javascriptEscapeReference.put("œ", "%u0153");
        javascriptEscapeReference.put("Š", "%u0160");
        javascriptEscapeReference.put("š", "%u0161");
        javascriptEscapeReference.put("Ÿ", "%u0178");
        javascriptEscapeReference.put("ƒ", "%u0192");

        StringBuilder sb = new StringBuilder();
        int n = s.length();

        for (int i = 0; i < n; i++) {
            String c = String.valueOf(s.charAt(i));
            if (javascriptEscapeReference.containsKey(c)) {
                sb.append(javascriptEscapeReference.get(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
    
    //http://www.herongyang.com/Cryptography/SHA1-Message-Digest-in-Java.html
    public static String sha1(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(input.getBytes("UTF-8"));
        byte[] output = md.digest();
        return bytesToHex(output);
    }
    
    private static String bytesToHex(byte[] b) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buf = new StringBuilder();
        for (int j = 0; j < b.length; j++) {
            buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
            buf.append(hexDigit[b[j] & 0x0f]);
        }
        return buf.toString();
    }    

}
