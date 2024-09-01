package com.wh.common.test;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;

public final class BytesUtils {

    /**
     * 二进制字符串,转二进制数组,中间要用逗号隔开 只能处理无符号的数值
     * 例如：00111011,01111111都可以处理，如果01111111二进制数中的第一位是1，则会报错
     *
     * @param b
     * @return
     */
    public static byte[] bytesStringToBytes(String b) {
        if (b.length() < 0) {
            return null;
        }
        String[] in = b.split(",");
        byte[] by = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            by[i] = Byte.parseByte(in[i], 2);
        }
        return by;
    }

    /**
     * 二进制字符串，转十六进制字符串，中间要用逗号隔开 只能处理无符号的数值
     * 例如：00111011,01111111都可以处理，如果01111111二进制数中的第一位是1，则会报错
     */
    public static String bytesStringToHexString(String byteString) {
        if (byteString.length() < 0) {
            return null;
        }
        String[] inputs = byteString.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inputs.length; i++) {
            byte[] b = new byte[1];
            b[0] = Byte.parseByte(inputs[i], 2);
            sb.append(BytesUtils.bytesToHexString(b));
        }
        return sb.toString();
    }

    /**
     * 二进制数组转二进制字符串
     *
     * @param b
     * @return
     */
    public static String bytesToBytesString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String s = "";
        for (byte bs : b) {
            String sj = Integer.toBinaryString(bs);
            s += sj;
            int i = sj.length();
            if (i < 8) { // 8位不够，前面补零操作
                int in = 8 - i;
                s = addZeroHead(s, in);
                sb.append(s);
                s = "";
            }
        }
        return sb.toString();
    }

    /**
     * 前补零操作 二进制字符串中，不够八位
     *
     * @return
     */
    public static String addZeroHead(String src, int addZero) {
        String sr = src;
        String s = "";
        for (int f = 0; f < addZero; f++) {
            s += "0";
        }
        return sr = s + sr;
    }

    /**
     * 二进制数组转十六进制字符串<br/>
     *
     * @param b
     * @return String
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }

    public static String hexStringToText(String hexString) {
        // 将16进制字符串转换为字节数组
        byte[] bytes = new BigInteger(hexString, 16).toByteArray();

        // 去除前导零字节，因为可能会因为BigInteger转换时添加了一个前导零字节
        // 如果第一个字节是零，则去除它
        if (bytes[0] == 0) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }

        // 将字节数组转换为字符串
        return new String(bytes);
    }

    /**
     * @param b
     * @return
     */
    public static String bytesToHexString(byte b) {
        StringBuilder stringBuilder = new StringBuilder("");
        int v = b & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    /**
     * 二进制字符串，转十六进制字符串 只能处理无符号的数值
     * 例如：00111011,01111111都可以处理，如果01111111二进制书中的第一位是1，则会报错
     */
    public static String hexStringToBytesString(String hexString) {
        if (hexString.length() < 0) {
            return null;
        }
        String[] inputs = hexString.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inputs.length; i++) {
            byte[] b = new byte[1];
            b[0] = Byte.parseByte(inputs[i], 2);
            sb.append(BytesUtils.bytesToHexString(b));
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转二进制数组<br/>
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToBytes(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.toUpperCase();
        int length = s.length() / 2;
        char[] hexChars = s.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 字符转为byte<br/>
     * 把一个字符转成二进制<br/>
     *
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * int to bytes<br/>
     * 十进制转二进制数组；产生的数据在高<br/>
     *
     * @param i
     * @return
     */
    public static byte[] intToBytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (0xff & i);
        b[1] = (byte) ((0xff00 & i) >> 8);
        b[2] = (byte) ((0xff0000 & i) >> 16);
        b[3] = (byte) ((0xff000000 & i) >> 24);
        return b;
    }

    public static int getWORD(byte[] msg, int offset) {
        return ((0xff & msg[offset + 1]) | (0xff00 & (msg[offset] << 8)));
    }

    public static byte[] getBytesMsgID(int msg_ID) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((msg_ID & 0xff00) >> 8);
        bytes[1] = (byte) (msg_ID & 0xff);
        return bytes;
    }


    /**
     * bytes to int ；产生的int数据在高位<br/>
     * 二进制数组转十进制，数组必须大于4，小于4会出错<br/>
     *
     * @param b
     * @return
     */
    public static int bytesToInt(byte[] b) {
        if (b.length < 4) {
            return 0;
        }
        int n = b[0] & 0xFF;
        n |= ((b[1] << 8) & 0xFF00);
        n |= ((b[2] << 16) & 0xFF0000);
        n |= ((b[3] << 24) & 0xFF000000);
        return n;
    }

    /**
     * 合并两个byte数组 <br/>
     *
     * @param src 合并在前
     * @param des 合并在后
     * @return
     */
    public static byte[] getMergeBytes(byte[] src, byte[] des) {
        int ac = src.length;
        int bc = des.length;
        byte[] b = new byte[ac + bc];
        for (int i = 0; i < ac; i++) {
            b[i] = src[i];
        }
        for (int i = 0; i < bc; i++) {
            b[ac + i] = des[i];
        }
        return b;
    }

    /**
     * 合并三个byte数组 <br/>
     *
     * @param src 合并前
     * @param cen 合并中
     * @param des 合并后
     * @return 字节数组
     */
    public static byte[] getMergeBytes(byte[] src, byte[] cen, byte[] des) {
        int ac = src.length;
        int bc = cen.length;
        int cc = des.length;
        byte[] b = new byte[ac + bc + cc];
        for (int i = 0; i < ac; i++) {
            b[i] = src[i];
        }
        for (int i = 0; i < bc; i++) {
            b[ac + i] = cen[i];
        }
        for (int i = 0; i < cc; i++) {
            b[ac + bc + i] = des[i];
        }
        return b;
    }

    /**
     * 5个byte合并<br/>
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @return
     */
    public static byte[] getMergeBytesFive(byte[] a, byte[] b, byte[] c,
                                           byte[] d, byte[] e) {
        int ia = a.length;
        int ib = b.length;
        int ic = c.length;
        int id = d.length;
        int ie = e.length;
        byte[] arrs = new byte[ia + ib + ic];
        arrs = getMergeBytes(a, b, c);
        byte[] twoArr = new byte[id + ie];
        twoArr = getMergeBytes(d, e);
        byte[] bs = new byte[ia + ib + ic + id + ie];
        bs = getMergeBytes(arrs, twoArr);
        return bs;
    }

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static short getShort(byte[] bytes) {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static char getChar(byte[] bytes) {
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static int getInt(byte[] bytes) {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8))
                | (0xff0000 & (bytes[2] << 16))
                | (0xff000000 & (bytes[3] << 24));
    }

    public static long getLong(byte[] bytes) {
        return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8))
                | (0xff0000L & ((long) bytes[2] << 16))
                | (0xff000000L & ((long) bytes[3] << 24))
                | (0xff00000000L & ((long) bytes[4] << 32))
                | (0xff0000000000L & ((long) bytes[5] << 40))
                | (0xff000000000000L & ((long) bytes[6] << 48))
                | (0xff00000000000000L & ((long) bytes[7] << 56));
    }

    public static float getFloat(byte[] bytes) {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static double getDouble(byte[] bytes) {
        long l = getLong(bytes);
        System.out.println(l);
        return Double.longBitsToDouble(l);
    }

    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    // public static String getString(byte[] bytes)
    // {
    // return getString(bytes, 'GBK');
    // }

    //public static byte[] getBytes(String data)
    //{
//    return getBytes(data, 'GBK');
    //}

    // byts to uint32
    public static int byteArrayToInt32(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    // byts to uint16
    public static int byteArrayToInt16(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (2 - 1 - i) * 8;
            value += (b[i + offset] & 0x00FF) << shift;
        }
        return value;
    }

    // byts to uint8
    public static int byteArrayToInt8(byte[] b, int offset) {
        return (b[offset] & 0xFF);
    }

    // get byte of bit the value  获取位字节的值
    public static boolean getBit(byte[] b, int offset, int ops) {
        return (((b[offset] >> ops) & 0x1)) == 1 ? true : false;
    }

    public static long stringToTimeMillis(String strTime) {
        int YY = Integer.valueOf(strTime.substring(0, 4));
        int MM = Integer.valueOf(strTime.substring(4, 6));
        int DD = Integer.valueOf(strTime.substring(6, 8));
        int hh = Integer.valueOf(strTime.substring(8, 10));
        int mm = Integer.valueOf(strTime.substring(10, 12));
        int ss = Integer.valueOf(strTime.substring(12, 14));

        Calendar calendar = Calendar.getInstance();

        calendar.set(YY, MM - 1, DD, hh, mm, ss);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
