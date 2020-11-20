package com.simboorm.simboorm.utility;

import com.simboorm.simboorm.utility.crypto.BlockCipher;
import com.simboorm.simboorm.utility.crypto.BlockCipherMode;
import com.simboorm.simboorm.utility.crypto.LEA;
import com.simboorm.simboorm.utility.crypto.PKCS5Padding;

public class LEACrypto {
    // 객체 생성
    static BlockCipherMode cipher = new LEA.ECB();

    public static Byte[] EnCrypt(String key) {

        // 암호화
        cipher.init(BlockCipher.Mode.ENCRYPT, key.getBytes());
        cipher.setPadding(new PKCS5Padding(16));
//        ct1 = cipher.update(pt1);
//        ct2 = cipher.doFinal(pt2);
        return null;
    }

    public static Byte[] Decrypt(String key) {
        // 복호화
        cipher.init(BlockCipher.Mode.DECRYPT, key.getBytes());
        cipher.setPadding(new PKCS5Padding(16));
//        pt1 = cipher.update(ct1);
//        pt2 = cipher.doFinal(ct2);
        return null;
    }

}
