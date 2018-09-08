package com.it_tao_idcard.utils;

import android.support.annotation.NonNull;
import android.util.Log;

public class StringUtils {

	public static String integer216HexString(int integer) {
		String of = String.valueOf(Integer.toHexString(integer));

		// 进制转换

		if (of.length() < 2) {

			of = "0" + of;

		}

		return of.toUpperCase();
	}


	/**
	 * 方式三 
	 *
	 * @param bytes
	 * @return
	 */
	public static String bytes2hex03(byte[] bytes)
	{
		final String HEX = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes)
		{
			// 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数  
			sb.append(HEX.charAt((b >> 4) & 0x0f));
			// 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数  
			sb.append(HEX.charAt(b & 0x0f));
		}

		return sb.toString();
	}

	/**
	 * 方式二 
	 *
	 * @param bytes
	 * @return
	 */
	public static String bytes2hex02(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes)
		{
			// 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制  
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位  
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}

	/** *//**
	 * 把16进制字符串转换成字节数组  
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/** *//**
	 * 把字节数组转换成16进制字符串  
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	/*
     * 字符串转16进制字符串
     */
	public static String string2HexString(String s) throws Exception{

		String r = bytes2HexString(string2Bytes(s));
		return r;
	}

	/*
     * 字符串转字节数组
     */
	public static byte[] string2Bytes(String s){
		byte[] r = s.getBytes();
		return r;
	}

	/*
     * 字节数组转16进制字符串
     */
	public static String bytes2HexString(byte[] b) {
		String r = "";

		for (int i = 0; i < b.length; i++) {
			String hex = getHexByte2String(b[i]);
			r += hex.toUpperCase();
		}

		return r;
	}

	@NonNull
	private static String getHexByte2String(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		if (hex.length() == 1) {
            hex = '0' + hex;
        }
		return hex;
	}

	public static byte[] hex2byte(String hex) {
		String digital = "0123456789ABCDEF";
		String hex1 = hex.replace(" ", "");
		char[] hex2char = hex1.toCharArray();
		byte[] bytes = new byte[hex1.length() / 2];
		byte temp;
		for (int p = 0; p < bytes.length; p++) {
			temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);
			temp += digital.indexOf(hex2char[2 * p + 1]);
			bytes[p] = (byte) (temp & 0xff);
		}
		return bytes;
	}


	/**
	 * Convert hex string to byte[]  
	 * @param hexString the hex string  
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte  
	 * @param c char  
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	//将指定byte数组以16进制的形式打印到控制台   
	public static void printHexString( byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = getHexByte2String(b[i]);
			System.out.print(hex.toUpperCase() );
			Log.e("printHexString  ", hex.toUpperCase() );
		}

	}

	/*
     * 字节转10进制
     */
	public static int byte2Int(byte b){
		int r = (int) b;
		return r;
	}

	/*
     * 10进制转字节
     */
	public static byte int2Byte(int i){
		byte r = (byte) i;
		return r;
	}

	/*
     * 字节数组转字符串
     */
	public static String bytes2String(byte[] b) throws Exception {
		String r = new String (b,"UTF-8");
		return r;
	}


	/*
     * 16进制字符串转字符串
     */
	public static String hex2String(String hex) throws Exception{
		String r = bytes2String(hexString2Bytes(hex));
		return r;
	}
	/*
     * 16进制字符串转字节数组
     */
	public static byte[] hexString2Bytes(String hex) {

		if ((hex == null) || (hex.equals(""))){
			return null;
		}
		else if (hex.length()%2 != 0){
			return null;
		}
		else{
			hex = hex.toUpperCase();
			int len = hex.length()/2;
			byte[] b = new byte[len];
			char[] hc = hex.toCharArray();
			for (int i=0; i<len; i++){
				int p=2*i;
				b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
			}
			return b;
		}

	}
	/**
	 * 字节数组转换为十六进制字符串
	 *
	 * @param b
	 *            byte[] 需要转换的字节数组
	 * @return String 十六进制字符串
	 */
	public static final String byte2hex(byte b[]) {
		if (b == null) {
			throw new IllegalArgumentException(
					"Argument b ( byte array ) is null! ");
		}
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xff);
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

}
