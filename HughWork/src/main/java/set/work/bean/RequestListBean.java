package set.work.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 网络请求实体类
 */
public class RequestListBean extends ArrayList<Map<String, String>> implements Serializable, Cloneable{
	public int type = TYPE_URL;
	public static final int TYPE_STRING = 0;
	public static final int TYPE_URL = 1 ;
	public static final int TYPE_FILE = 2 ;
	public static final int TYPE_LIST = 3 ;
	private int requestType;
	private Object requestId;
	private Object requestTag;
	private Map<String, String> kvPair;
	private String requestValue;
	private Map<String, Map<String, String>> krPairMap;
	public RequestListBean(int requestType){
		this.setRequestType(requestType);
		kvPair = new LinkedHashMap<String, String>();
	}

	public void put(String key, String value) {
		if (!this.contains(kvPair))
			this.add(kvPair);
		kvPair.put(key, value);
	}

	public void put(String key, Map<String, String> valueMap) {
		if (krPairMap == null) {
			krPairMap = new LinkedHashMap<String, Map<String,String>>();
		}
		krPairMap.put(key, valueMap);
	}

	public JSONObject getBodyJson(String mainKey) {
		JSONObject object = new JSONObject();
		try {
			// body
			JSONObject jsonObj = new JSONObject();
			if (type == TYPE_LIST) {

			}
			// body 属性元素
			Map<String, String> map = getSingleMap();
			if (!map.isEmpty()) {
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = entry.getKey().toString();
					String val = entry.getValue().toString();
					jsonObj.put(key, val);
				}
			}
			// body 子元素
			Map<String, Map<String, String>> valueMap = getValueMaps();
			if (valueMap != null && valueMap.isEmpty()) {
				Iterator iter = valueMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = entry.getKey().toString();
					Map<String, String> val = (Map<String, String>) entry.getValue();
					JSONObject subObj = new JSONObject();
					Iterator subiter = val.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry subentry = (Map.Entry) subiter.next();
						String subkey = subentry.getKey().toString();
						String subval = subentry.getValue().toString();
						subObj.put(subkey, subval);
					}
					jsonObj.put(key, subObj);
				}
			}
			object.put(mainKey, jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return object;
	}

	/**
	 * 获取url的参数
	 * @return
	 */
	public String  getUrlParams() {
		String url = "";
		Iterator iter = kvPair.entrySet().iterator();
		try {
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				String val;
				val = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
				url = url + key + "=" + val + "&";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(url.length() > 0) {
			return  url.substring(0,url.length()-1);
		}
		return  url;
	}

	public Map<String, String> getSingleMap() {
		return kvPair;
	}

	public Map<String, Map<String, String>> getValueMaps() {
		return krPairMap;
	}

	public Object getRequestId() {
		return requestId;
	}

	public void setRequestId(Object requestId) {
		this.requestId = requestId;
	}
	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public String getRequestValue() {
		return requestValue;
	}

	public void setRequestValue(String requestValue) {
		this.requestValue = requestValue;
	}

	public Object getRequestTag() {
		return requestTag;
	}

	public void setRequestTag(Object requestTag) {
		this.requestTag = requestTag;
	}

	@Override
	public RequestListBean clone(){
		RequestListBean o = null;
		o = (RequestListBean) super.clone();
		return o;
	}

}
