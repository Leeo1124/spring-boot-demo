package com.leeo.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.crypto.CryptoException;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.junit.Assert;
import org.junit.Test;


public class PasswordTests {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecretKey deskey = keygen.generateKey();//GAevYnznvgNCURavBhCr1w==
        System.out.println(Base64.encodeToString(deskey.getEncoded()));
    }
    @Test
    public void testAesCipherService() {
        CipherService cipherService = new AesCipherService();
        // aesCipherService.setKeySize(128);//设置key长度
        // byte[] key = aesCipherService.generateNewKey().getEncoded();
        // String base64 = Base64.encodeToString(key);
        byte[] key = Base64.decode("4AvVhmFLUs0KTA3Kprsdag==");
        System.out.println("key2==" + key.toString());
        System.out.println(Base64.encodeToString(key));
        // 生成key
        // Key key = aesCipherService.generateNewKey();

        String text = "hello";
        System.out.println(new String(text.getBytes()));
        // 加密
        try {
            String encrptText = cipherService.encrypt(text.getBytes(), key).toHex();
            System.out.println("encrptText==" + encrptText);
            // 解密
            String text2 = new String(cipherService.decrypt(Hex.decode(encrptText), key).getBytes());
            System.out.println("text2==" + text2);
            Assert.assertEquals(text, text2);
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void Base64(){
        String str = "hello";
        String base64Encoded = Base64.encodeToString(str.getBytes());
        String str2 = Base64.decodeToString(base64Encoded);
        Assert.assertEquals(str, str2);
    }
    
    @Test
    public void Hex(){//十六进制
        String str = "hello";
        String base64Encoded = Hex.encodeToString(str.getBytes());
        String str2 = new String(Hex.decode(base64Encoded.getBytes()));
        Assert.assertEquals(str, str2);
    }
    
    @Test
    public void Md5(){
        String str = "hello";
        String salt = "123";
        String md5 = new Md5Hash(str, salt, 2).toString();
        System.out.println(md5);
    }
    
    @Test
    public void Sha256(){
        String str = "hello";
        String salt = "123";
        String sha256 = new Sha256Hash(str, salt, 2).toString();
        System.out.println(sha256);
    }
    
    @Test
    public void Sha1(){
        String str = "123456";
        String salt = "iY71e4d123";
        String sha1 = new Sha1Hash(str, salt, 1024).toString();
        System.out.println(sha1);
    }
    
    @Test
    public void SimpleHash(){
        String str = "hello";
        String salt = "123";
        //内部使用MessageDigest
        String simpleHash = new SimpleHash("SHA-1", str, salt).toString();
        System.out.println(simpleHash);
    }
    
    /**
     * 1、首先创建一个DefaultHashService，默认使用SHA-512 算法；
       2、可以通过hashAlgorithmName属性修改算法；
       3、可以通过privateSalt设置一个私盐，其在散列时自动与用户传入的公盐混合产生一个新盐；
       4、可以通过generatePublicSalt属性在用户没有传入公盐的情况下是否生成公盐；
       5、可以设置randomNumberGenerator用于生成公盐；
       6、可以设置hashIterations属性来修改默认加密迭代次数；
       7、需要构建一个HashRequest，传入算法、数据、公盐、迭代次数。
     */
    @Test
    public void hashService(){
        DefaultHashService hashService = new DefaultHashService(); //默认算法SHA-512
        hashService.setHashAlgorithmName("SHA-512");
        hashService.setPrivateSalt(new SimpleByteSource("123")); //私盐，默认无
        hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false
        hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就这个
        hashService.setHashIterations(1); //生成Hash值的迭代次数
        HashRequest request = new HashRequest.Builder()
        .setAlgorithmName("MD5").setSource(ByteSource.Util.bytes("hello"))
        .setSalt(ByteSource.Util.bytes("123")).setIterations(2).build();
        String hex = hashService.computeHash(request).toHex();
        
        System.out.println(hex);
    }
    
    @Test
    public void AEC(){
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128); //设置key长度
        //生成key
        Key key = aesCipherService.generateNewKey();
        String text = "hello";
        //加密
        String encrptText = aesCipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();
        //解密
        String text2 = new String(aesCipherService.decrypt(Hex.decode(encrptText), key.getEncoded()).getBytes());
        Assert.assertEquals(text, text2);
    }
}
