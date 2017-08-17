package com.myside.util;

import com.myside.entity.U_User;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.security.MessageDigest;
import java.util.Random;

public class EndecryptUtils {

    /**
     * 加工密码，和登录一致。
     * @param user
     * @return
     */
    public static U_User md5Pswd(U_User user){
        //密码为   email + '#' + pswd，然后MD5
        user.setPswd(md5Pswd(user.getEmail(),user.getPswd()));
        return user;
    }
    /**
     * 字符串返回值
     * @param email
     * @param pswd
     * @return
     */
    public static String md5Pswd(String email ,String pswd){
        pswd = String.format("%s#%s", email,pswd);
        pswd = getMD5(pswd);
        return pswd;
    }

    /**
     * 获取随机的数值。
     * @param length	长度
     * @return
     */
    public static String getRandom620(Integer length){
        String result = "";
        Random rand = new Random();
        int n = 20;
        if(null != length && length > 0){
            n = length;
        }
        boolean[]  bool = new boolean[n];
        int randInt = 0;
        for(int i = 0; i < length ; i++) {
            do {
                randInt  = rand.nextInt(n);

            }while(bool[randInt]);

            bool[randInt] = true;
            result += randInt;
        }
        return result;
    }
    /**
     * MD5 加密
     * @param str
     * @return
     * @throws Exception
     */
    public static String  getMD5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            //LoggerUtils.fmtError(this.getClass(),e, "MD5转换异常！message：%s", e.getMessage());
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }


    /**
         * 通过username,password,salt,校验密文是否匹配 ，校验规则其实在配置文件中，这里为了清晰，写下来
         * @param username 用户名
         * @param password 原密码
         * @param salt  盐
         * @param md5cipherText 密文
         * @return
         */

    public static  boolean checkMd5Password(String username,String password,String salt,String md5cipherText)
    {
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(username),"username不能为空");
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(password),"password不能为空");
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(md5cipherText),"md5cipherText不能为空");
        //组合username,两次迭代，对密码进行加密
        String password_cipherText= new Md5Hash(password,username+salt,2).toHex();
        return md5cipherText.equals(password_cipherText);
    }
}
