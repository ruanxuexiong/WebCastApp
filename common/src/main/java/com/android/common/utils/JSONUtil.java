package com.android.common.utils;

import android.text.TextUtils;

import com.android.common.BaseApplication;
import com.android.common.Consts;
import com.android.common.DbConst;
import com.android.common.models.BaseModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

	public static String VALUE_SUCCESS = BaseApplication.getInstance().getSuccessField();
	public static String VALUE_Message =BaseApplication.getInstance().getMessageField();
	public static String VALUE_Result =BaseApplication.getInstance().getResultField();
	
	public static boolean isInArray(JSONArray array, String key, String value) {

		for (int index = 0; index < array.length(); index++) {

			try {
				JSONObject object = array.getJSONObject(index);
				if (object == null)
					continue;

				Object object2 = object.get(key);
				if (object2 == null)
					continue;

				if (object2.toString().equals(value)) {
					return true;
				}
			} catch (JSONException e) {
			}
		}

		return false;
	}

	public static <T> T getValueFromList(JSONArray array, int index,
			String key, T defaultValue) {
		try {
			JSONObject object = array.getJSONObject(index);

			return get(object, key, defaultValue);

		} catch (JSONException e) {
			return defaultValue;
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T get(JSONObject object, String key, T defaultValue) {

		if(object == null) return defaultValue;
		
		try {
			Object object2 = object.get(key);
			
			if (object2 == null) return defaultValue;
			
			Class<?> valueClass = defaultValue.getClass();
			if (valueClass == int.class || valueClass == Integer.class) {
				return (T) (Integer) object.getInt(key);
			} else if (valueClass == double.class || valueClass == Double.class) {
				return (T) (Double) object.getDouble(key);
			} else if (valueClass == boolean.class || valueClass == Boolean.class) {
				return (T) (Boolean) object.getBoolean(key);
			} else if (valueClass == long.class || valueClass == Long.class) {
				return (T) (Long) object.getLong(key);
			}
			
			T value =  (T) object.get(key).toString();
			if (valueClass == String.class) {
				if(value == null || value.toString().toLowerCase().equals("null")){
					return defaultValue;
				}
			}
			return value;

		} catch (JSONException e) {
			return defaultValue;
		}
	}

	public static JSONObject getJsonObject(JSONObject object,String key){
		

		try {
			return object.getJSONObject(key);

		} catch (JSONException e) {
			return null;
		}
	}
	
	public static List<String> getStrings(JSONArray arr) {
		
		List<String> list = new ArrayList<String>();
		try {
			for (int index = 0; index < arr.length(); index++) {
				list.add(arr.getString(index));
			}
			
		} catch (JSONException e) {
		}
		return list;
	}
	
	public static List<String> getStrings(JSONObject object, String key) {

		List<String> list = new ArrayList<String>();
		try {
			JSONArray array = object.getJSONArray(key);

			for (int index = 0; index < array.length(); index++) {
				list.add(array.getString(index));
			}

		} catch (JSONException e) {
		}
		return list;
	}

	public static List<String> getStrings(JSONArray array, String key) {

		List<String> list = new ArrayList<String>();
		try {

			for (int index = 0; index < array.length(); index++) {
				list.add(array.getJSONObject(index).getString(key));
			}

		} catch (JSONException e) {
		}
		return list;
	}

	public static String getString(JSONArray array, String key, String splitCard) {

		String builString = "";
		try {

			for (int index = 0; index < array.length(); index++) {
				builString += array.getJSONObject(index).getString(key)
						+ splitCard;
			}

			if (builString.length() > 0) {
				builString = builString.substring(0, builString.length()
						- splitCard.length());
			}

		} catch (JSONException e) {
		}
		return builString;
	}

	public static JSONArray getArray(JSONObject object, String key) {

		try {
			return object.getJSONArray(key);

		} catch (JSONException e) {
		}
		return new JSONArray();
	}

	public static List<JSONObject> getList(JSONObject object, String key) {

		if (object == null) return null;

		List<JSONObject> list = new ArrayList<>();

		try {
			JSONArray array = object.optJSONArray(key);

			if (array == null) return null;

			for (int index = 0; index < array.length(); index++) {
				list.add(array.getJSONObject(index));
			}

		} catch (JSONException e) {
			return null;
		}
		return list;
	}

	public static JSONObject toJsonObject(String value) {

		if (value == null || value.length() == 0)
			return null;

		try {
			return new JSONObject(value);
		} catch (JSONException e) {
			return null;
		}
	}

	public static JSONObject getJsonObject(JSONArray array, int index) {
		try {
			return array.getJSONObject(index);
		} catch (JSONException e) {
			return new JSONObject();
		}
	}

	public static <T> JSONArray removeObjectFromArray(JSONArray array,
			JSONObject object, String key, T defaultValue) {
		T obj1 = get(object, key, defaultValue);

		JSONArray tmpArray = new JSONArray();
		if (array == null)
			return tmpArray;

		for (int index = 0; index < array.length(); index++) {
			T obj2 = getValueFromList(array, index, key, defaultValue);

			if (!obj1.equals(obj2) && !obj1.equals(defaultValue)) {
				try {
					tmpArray.put(array.getJSONObject(index));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return tmpArray;
	}

	public static <T> List<JSONObject> removeObjectFromList(
			List<JSONObject> array, JSONObject object, String key,
			T defaultValue) {
		T obj1 = get(object, key, defaultValue);

		List<JSONObject> tmpArray = new ArrayList<JSONObject>();

		for (int index = 0; index < array.size(); index++) {
			T obj2 = get(array.get(index), key, defaultValue);

			if (obj1.equals(defaultValue) || !obj1.equals(obj2)) {
				tmpArray.add(array.get(index));
			} 
		}

		return tmpArray;
	}

	public static <T> List<JSONObject> copyList(
			List<JSONObject> array) {
		

		List<JSONObject> tmpArray = new ArrayList<JSONObject>();

		for (int index = 0; index < array.size(); index++) {
			
			JSONObject object = array.get(index);
			
			 JSONObject newJson;
			try {
				newJson = new JSONObject(object.toString());
				tmpArray.add(newJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}

		return tmpArray;
	}

	public static <T> List<JSONObject> replaceObjectFromList(
			List<JSONObject> array, JSONObject object, String key,
			T defaultValue) {

		T obj1 = get(object, key, defaultValue);

		List<JSONObject> tmpArray = new ArrayList<JSONObject>();

		for (int index = 0; index < array.size(); index++) {
			T obj2 = get(array.get(index), key, defaultValue);

			if (obj1.equals(defaultValue) || !obj1.equals(obj2)) {
				tmpArray.add(array.get(index));
			} else {
				tmpArray.add(object);
			}
		}

		return tmpArray;
	}

	public static <T> void updateObject(JSONObject object, String key, T value) {

		if(object==null) return;
		try {
			
			object.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static <T> boolean isContainJsonObject(List<JSONObject> list,JSONObject jsonObject,String key,T defaultValue){
		
		T obj1 = get(jsonObject, key, defaultValue);
		if(obj1==defaultValue) return false;
		
		for (int index = 0; index < list.size(); index++) {
			T obj2 = get(list.get(index), key, defaultValue);
			
			if (obj1.equals(obj2)) return true;
		}
		
		return false;
		
	}
	
	public static <TModel> List<TModel> getListModel(List<JSONObject> jsonList,Class<TModel> cls){
		
		List<TModel> list = new ArrayList<TModel>();
		
		for (JSONObject jsonObject : jsonList) {
			TModel model = getModel(jsonObject, cls);
			if(model!=null){
				list.add(model);
			}
		}
		
		return list;
	}
	
	public static <TModel> TModel getModel(JSONObject object,Class<TModel> cls) {
		
		TModel model = createT(cls);
		
		return getModel(object, cls,model);
	}

	public static <TModel> TModel getModel(JSONObject object,Class<TModel> cls,TModel model) {

		if(model==null) return null;
		
		Field fields[] = cls.getFields();

		for (Field field : fields) {
			String fieldName = field.getName();
			try {
					field.set(model, object.get(fieldName));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return model;
	}
	
	public static <T> T createT(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> JSONObject find(List<JSONObject> dataSource,String key,T validateValue,T defaultValue){
		
		JSONObject rtnJsonObject = null;
		for (JSONObject jsonObject : dataSource) {
			
			T value = JSONUtil.get(jsonObject, key, defaultValue);
			if(value==null) continue;
			
			if(value.toString().equals(validateValue.toString())) {
				rtnJsonObject = jsonObject;
				break;
			}
		}
		
		return rtnJsonObject;
	}

	public static String getPictureUrl(JSONObject object, String key) {

		try {
			Object object2 = object.get(key);
			if (object2 == null) {
				return "";
			}
			return object.getString(key);

		} catch (JSONException e) {
			return "";
		}
	}

	   /** 
     * 将JAVA对象转换成JSON字符串 
     * @param obj 
     * @return 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */  
    public static String objectToJson(Object obj,List<Class> claList)
    {  
        if(obj==null){  
            return "null";  
        }  
        String jsonStr = "{";  
        Class<?> cla = obj.getClass();  
        Field fields[] = cla.getDeclaredFields();  
        for (Field field : fields) {  
            field.setAccessible(true);  
            if(field.getType() == long.class){  
                try {
					jsonStr += "\""+field.getName()+"\":"+field.getLong(obj)+",";
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }else if(field.getType() == double.class){  
                try {
					jsonStr += "\""+field.getName()+"\":"+field.getDouble(obj)+",";
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }else if(field.getType() == float.class){  
                try {
					jsonStr += "\""+field.getName()+"\":"+field.getFloat(obj)+",";
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }else if(field.getType() == int.class){  
                try {
					jsonStr += "\""+field.getName()+"\":"+field.getInt(obj)+",";
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }else if(field.getType() == boolean.class){  
                try {
					jsonStr += "\""+field.getName()+"\":"+field.getBoolean(obj)+",";
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }else if(field.getType() == Integer.class||field.getType() == Boolean.class  
                    ||field.getType() == Double.class||field.getType() == Float.class                     
                    ||field.getType() == Long.class){                 
                try {
					jsonStr += "\""+field.getName()+"\":"+field.get(obj)+",";
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }else if(field.getType() == String.class){  
                try {
					jsonStr += "\""+field.getName()+"\":\""+field.get(obj)+"\",";
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }else if(field.getType() == List.class){  
                String value;
				try {
					value = objectToJson((List<?>)field.get(obj),claList);
	                jsonStr += "\""+field.getName()+"\":"+value+",";  
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}                  
            }else{        
                if(claList!=null&&claList.size()!=0&&claList.contains(field.getType())){  
                    String value;
					try {
						value = objectToJson(field.get(obj),claList);
	                    jsonStr += "\""+field.getName()+"\":"+value+",";                      

					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
                }else{  
                    jsonStr += "\""+field.getName()+"\":null,";  
                }  
            }  
        }  
        jsonStr = jsonStr.substring(0,jsonStr.length()-1);  
        jsonStr += "}";  
            return jsonStr;       
    }  
      
    /** 
     * 将JAVA的LIST转换成JSON字符串 
     * @param list 
     * @return 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */  
    public static String listToJsonStr(List<?> list,List<Class> claList) {  
        if(list==null||list.size()==0){  
            return "[]";  
        }  
        String jsonStr = "[";  
        for (Object object : list) {  
            jsonStr += objectToJson(object,claList)+",";  
        }  
        jsonStr = jsonStr.substring(0,jsonStr.length()-1);  
        jsonStr += "]";       
        return jsonStr;  
    }     
    
    public static String getFormatPrice(JSONObject object,String key){
    	
    	double price = JSONUtil.get(object, key, 0.0);
    	return StringHelper.formatPrice(price);
    }
    
    public static JSONObject getStringToJson(String json){

		if (TextUtils.isEmpty(json)) return null;

		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static JSONArray getStringToJsonArray(String json){
    	JSONArray arr = null;
    	try {
    		arr = new JSONArray(json);
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	return arr;
    }

}
