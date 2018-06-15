package com.trade.util;

import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.trade.json.BaseJson;

public class JsonUtil {

	private static String getResult1(int code,String msg,Object result){
		BaseJson json=new BaseJson();
		json.setCode(code);
		json.setMsg(msg);
		json.setResult(result);
		Gson gson=new Gson();
		return gson.toJson(json);
	}
	//��ȡresult��Ϊ�ַ�����ֵ�ԵĽ��
/**
 * result������һ������
 * {"code":200,"msg":"��¼�ɹ�","result":{"pwd":"111","name":"1001"}}
 * @param code
 * @param msg
 * @param map
 * @return
 */
	public static String getResult(int code,String msg,HashMap<String, String> map){
		return getResult1(code, msg, map);
	}
	
	//��ȡresult��Ӧ�ַ����Ľ��
	/**
	 * {"code":200,"msg":"��¼�ɹ�","result":"1001"}
	 * @param code
	 * @param msg
	 * @param value
	 */
	public static String getResult(int code,String msg,String value){
		return getResult1(code, msg, value);
	}

	/*
	 * ��result������ָ�����Ƶ�JSON������ʵ����ֻ��Ҫ�趨�����Ϊ�������
	 * {"code":200,"msg":"��¼�ɹ�","result":{"userInfo":{"pwd":"111","name":"1001"}}}
	 */
	public static String getResultObj(int code,String msg,HashMap<String, HashMap<String, String>> map){
		return getResult1(code, msg, map);
	}
	public static String getResultObj1(int code,String msg,HashMap<String, Object> map){
		return getResult1(code, msg, map);
	}
	/**
	 * ָ��result��jsonArray������jsonArray����
	 * {"code":200,"msg":"��¼�ɹ�","result":{"portList":[{"id":"1","type":"����","status":"����"},{"id":"2","type":"����","status":"ռ��"}]}}
	 * @param code
	 * @param msg
	 * @param map
	 * @return
	 */
	public static String getResultArray(int code,String msg,HashMap<String, List> map){
		return getResult1(code, msg, map);
	}
}
