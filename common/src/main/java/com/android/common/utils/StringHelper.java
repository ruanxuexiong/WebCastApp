package com.android.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class StringHelper {

	
	public static List<Integer> getSplitIntegers(String message){
		
		List<Integer> list = new ArrayList<Integer>();
		
		String[] items = message.split(",");
		for (String string : items) {
			
			int number = toInt(string);
			if(number==0) continue;
			
			list.add(toInt(string));
		}
		
		return list;
	}
	
	public static int getRandomInteger(List<Integer> list){
		
		if(list.size()==0) return 0;
		
		Random random = new Random();
		int len = random.nextInt(list.size());
		
		return list.get(len);
	}
	
	public static void toast(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static Spanned getError(String errorMessage){
		
		return Html.fromHtml("<font color='red'>"+errorMessage+"</font>");
	}
	
	public static String HideFillString(String oldString,int max){
		
		if(oldString.length()<max) return oldString;
		
		return oldString.substring(0,max)+"...";
	}
	
	public static String getValidateNumber() {
		
		Random random = new Random();
		String validateCode = "";
		for (int index = 0; index < 4; index++) {
			validateCode += random.nextInt(10);
		}

		return validateCode;
		
	}

	public static String fullLength(int number, int level, String fullChar) {
		
		String newString = number + "";
		if (newString.length() >= level)
			return newString;

		int fixLength = level - newString.length();
		for (int index = 0; index < fixLength; index++) {
			newString = fullChar + newString;
		}

		return newString;
		
	}

	public static int toInt(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException errException) {
			return 0;
		}
	}

	public static boolean checkInt(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception errException) {
			return false;
		}
	}

	public static double toDouble(String oldString, double defaultValue) {
		
		try {
			return Double.parseDouble(oldString);
		} catch (NumberFormatException errException) {
			return defaultValue;
		}
		
	}

	public static String HiddenOldString(String oldString,int startNumber,int endNumber) {

		if (oldString.length() < 8) return oldString;

		int hidden = oldString.length() / 3;
		String newString = oldString.substring(0, hidden);
		for (int index = 0; index < hidden; index++) {
			newString += "*";
		}
		newString += oldString.substring(hidden * 2 - 1, oldString.length());
		return newString;

	}

	public static List<Integer> getRamdoms(int min, int max, int number) {
		
		int[] tmps = new int[max - min];
		List<Integer> list = new ArrayList<Integer>();

		Random random = new Random();
		for (int index = min; index < max; index++) {
			tmps[index - min] = index;
		}
		TreeSet<Integer> ts = new TreeSet<Integer>();
		while (ts.size() < number) {
			int n = random.nextInt(max - min);
			ts.add(tmps[n]);
		}

		Iterator<Integer> iter;
		for (iter = ts.iterator(); iter.hasNext();) {
			list.add(iter.next());
		}
		return list;
		
	}

	public static String readRaw(Context content, int id) {
		
		InputStream inputStream = content.getResources().openRawResource(id);
		return readFile(inputStream);
		
	}

	public static String readFile(InputStream inputStream) {
		
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "gbk");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
		
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证身份证
	 * @param cardId
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static boolean checkCardId(String cardId){
		
		IDCard idCard = new IDCard();
		String result = idCard.IDCardValidate(cardId);
		
		if(result.equals("")) return true;
		Log.i("CardID", "CardId:"+result);
		return false;
	}
	/**
	 * 验证手机号码
	 * 
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		//return true;
		boolean flag = false;
		try {
			Pattern regex = Pattern.compile("^((1[3,4,5,7,8][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	    车牌号格式：汉字 + A-Z + 5位A-Z或0-9
	   （只包括了普通车牌号，教练车和部分部队车等车牌号不包括在内）
	 */
	public static boolean isCarNumber(String carNumber) {
        
        String carnumRegex = "[\u4e00-\u9fa5]{1}[A-Z_a-z]{1}\\s{0,1}[A-Z_0-9]{5}";
        
        return TextUtils.isEmpty(carNumber) ? false : carNumber.matches(carnumRegex);
    }

	public static String read(TextView textView){
		
		return textView.getText().toString().trim();
	}
	

	public static boolean checkMinLength(String value,int length){
		if(value==null || value=="") return false;
		
		return value.length()>=length;
	}

	public static int getSexByCardId(String cardId){
		
		if(!checkCardId(cardId)) return 0;
		
		String lastChar = cardId.substring(cardId.length()-1,1);
		int sexIndex = StringHelper.toInt(lastChar);
		
		return (sexIndex+1)%2;
	}
	
	public static String formatPrice(double price){
		
		return new java.text.DecimalFormat("￥0.00").format(price);
	}
	
	public static String getGUIDString(){
		UUID guid = UUID.randomUUID();
		return guid.toString().replaceAll("-", "");
	}
	
	/** 获取屏幕的宽度 */
	public static int getScreenWidth(Activity context) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	public static int getScreenHeight(Activity context) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
	
	
	@SuppressWarnings("unchecked")
	public static<T> T as(Object value,Class<T> cls){
		
//		if (!TextUtils.isEmpty(type)) {
//			if (type.equals("String")) {
//				
//				return (T) value.toString();
//			} else if (type.equals("int") || type.equals("Integer")) {
//				
//				return (T) (Integer)value;
//			} else if (type.equals("long") || type.equals("Long")) {
//				
//				return (T) (Long)value;
//			} else if (type.equals("double") || type.equals("Double")) {
//				
//				return (T) (Double)value;
//			} else if (type.equals("boolean") || type.equals("Boolean")) {
//				
//				return (T) (Boolean)value;
//			} else if (type.equals("short") || type.equals("Short")) {
//				
//				return (T) (Short)value;
//			}
//			
//		}
		
		return null;
	}

	public static String getVersionCode(Context context){

		try {
			return ""+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getVersionName(Context context){

		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}