package com.national.btlock.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by admin on 2016/7/20.
 */
public class StringUtilBle {

    private final static String TAG = "StringUtilBle";

    /**
     * @param bytes
     * @param order 倒序 或 正序  -1 1
     * @return
     */
    public static String bytes2String(byte[] bytes, int order) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (order == -1) {
            builder.append("0x");
            for (int i = bytes.length - 1; i >= 0; i--) {
                builder.append(String.format("%02X", bytes[i]));
            }
        } else if (order == 1) {
            builder.append("0x");
            for (int i = 0; i < bytes.length; i++) {
                builder.append(String.format("%02X", bytes[i]));
            }
        }
        return builder.toString();
    }

    public static long StringToLong(String str) {
        String lowerCase = str.toLowerCase();
        final String HEX_PREF = "0x";

        int radix = 10;     //默认十进制
        if (str.startsWith(HEX_PREF)) {
            str = str.substring(HEX_PREF.length());
            radix = 16;
        }

        Long lval = Long.parseLong(str, radix);
        return lval;
    }

    public static int StringToInt(String str) {

        if (isEmpty(str)) {
            return 0;
        }
        Long lval = StringToLong(str);

        return (int) lval.longValue();
    }

    public static boolean equalsIgnoreCase(String str0, String str1) {
        if (null == str0 && null == str1) {
            return true;
        }

        if ((null == str0 && null != str1) ||
                (null != str0 && null == str1)) {
            return false;
        }

        return str0.equalsIgnoreCase(str1);
    }

    public static boolean isEmpty(String str) {
        if (null == str || "".equalsIgnoreCase(str)) {
            return true;
        }
        return false;
    }

    public static String shortToHexString(int data) {
//        StringBuffer sb = new StringBuffer(4);
        String sTemp;

        String intHexString = "0000" + Integer.toHexString(Integer.valueOf(data));
        int len = intHexString.length();
        sTemp = intHexString.substring(len - 4, len);

        return sTemp;
    }


    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    public static String toUtf8(String str) {
        String result = "";
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return result;
    }


    public static String getToken(String sidMd5, String noMd5, String mac, String time) {
        String result = "";

        Log.i(TAG, "sidMd5=" + sidMd5 + " midMd5" + MD5.md5(noMd5 + mac + time) + " time=" + time);

        result = sidMd5 + MD5.md5(noMd5 + mac + time) + time;

        return result;
    }


    public static byte loUint16(short v) {
        return (byte) (v & 0xFF);
    }

    public static byte hiUint16(short v) {
        return (byte) (v >> 8);
    }

    public static short buildUint16(byte hi, byte lo) {
        return (short) (((hi << 8) + (lo & 0xff)));
    }

    public static int buildUint(byte hi, byte lo) {
        return (int) (((hi << 8) + (lo & 0xff)) & 0xFFFF);
    }

    public static short int2Short(short a, short b) {

        return (short) ((a + b) & 0xFFFF);
    }


    public static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (isAsciiPrintable(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }


    private static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }


    public static byte hexStringToByte2(String hexString) {
        hexString = hexString.toUpperCase();
        char[] hexChars = hexString.toCharArray();

        byte value = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
        ;

        return value;

    }

    public static byte hexStringToByte(String hexString) {
//        if (hexString == null || hexString.equals("")) {
//            return null;
//        }
//
//        if(hexString.length()!=2){
//            return null;
//        }

        char[] hexChars = hexString.toCharArray();
        byte value;


        byte[] ret = hexStringToBytes(hexString);

        return ret[0];

    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        if (hexString.length() % 2 != 0) {
//			hexString="0"+hexString;
            hexString = "0" + hexString;
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    // 从字节数组到十六进制字符串转化
    public static String Bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    public static int byteStrToInt(String valueStr) {
        valueStr = valueStr.toUpperCase();
        if (valueStr.length() % 2 != 0) {
            valueStr = "0" + valueStr;
        }

        int returnValue = 0;

        int length = valueStr.length();

        for (int i = 0; i < length; i++) {

            int value = charToByte(valueStr.charAt(i));

            returnValue += Math.pow(16, length - i - 1) * value;
        }
        return returnValue;
    }

    public static int bytesToInt(byte[] values) {

        String valueStr = Bytes2HexString(values);
        if (valueStr.length() % 2 != 0) {
            valueStr = "0" + valueStr;
        }

        int returnValue = 0;

        int length = valueStr.length();

        for (int i = 0; i < length; i++) {

            int value = charToByte(valueStr.charAt(i));

            returnValue += Math.pow(16, length - i - 1) * value;
        }
        return returnValue;
    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 0) & 0x1);
    }

    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * @param src
     * @param start
     * @param length
     * @return
     */
    public static byte[] subByteArray(byte[] src, int start, int length) {


        byte[] result = new byte[length];

        System.arraycopy(src, start, result, 0, length);


        return result;

    }

    public static byte intToByte(int i) {
        byte result = (byte) (i & 0xFF);

        return result;
    }


    public static byte[] intToByteArray2(int i) {
        byte[] result = new byte[2];
//        result[0] = (byte) ((i >> 24) & 0xFF);
//        result[1] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }


    public static byte[] intToByteArray(long i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }


    public static byte[] intToByteArray_LITTLE_ENDIAN(long i) {
        byte[] result = new byte[4];
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }


    public static int bytes2Int(byte[] bytes) {
        int time = (bytes[0] << 24) & 0xFF000000 |
                (bytes[1] << 16) & 0xFF0000 |
                (bytes[2] << 8) & 0xFF00 |
                (bytes[3]) & 0xFF;
        return time;
    }

    public static int bytes2Int_LITTLE_ENDIAN(byte[] bytes) {
        int time = (bytes[3] << 24) & 0xFF000000 |
                (bytes[2] << 16) & 0xFF0000 |
                (bytes[1] << 8) & 0xFF00 |
                (bytes[0]) & 0xFF;
        return time;
    }


    public static int bytes2Int2(byte[] bytes) {
        int time = (bytes[1] << 8) & 0xFF00 | (bytes[0]) & 0xFF;
        return time;
    }


    public static String deleteCRLFOnce(String input) {

        return input.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");

    }


}
