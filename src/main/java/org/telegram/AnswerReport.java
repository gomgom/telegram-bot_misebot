package org.telegram;

/**
 * Created by Gomgom on 2016-04-20.
 * AnswerReport 클래스는 JSON을 분석해 PM 수치 등을 리턴하는 클래스입니다.
 */

import org.telegram.BotConfig;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AnswerReport {
	// URL 주소를 받아 리스트(list) 값의 JSONArray를 제공하는 메소드입니다.
	public static JSONArray takeJSONArray(String checkURL)
	{
		InputStream 			is = null;
		ByteArrayOutputStream 	baos = null;
		
		JSONArray listArray = new JSONArray();
		
		try {
			URL postUrl = new URL(checkURL);
			HttpURLConnection con = (HttpURLConnection)postUrl.openConnection();
		    con.connect(); // URL을 연결합니다.

		    is = con.getInputStream();
		    baos = new ByteArrayOutputStream(); // ByteArrayOutputStream을 준비합니다.
		    byte[] byteBuffer = new byte[1024];
		    byte[] byteData = null;
		    int nLength = 0;
		    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
		    	baos.write(byteBuffer, 0, nLength);
		    }
		    byteData = baos.toByteArray(); // ByteData 배열을 완섭합니다.
		    String gotJSONdata = new String(byteData); // JSON 원본 데이터를 String 형태로 저장합니다.
	
		    JSONParser jPars = new JSONParser(); // 파서를 준비합니다.
		    Object obj = jPars.parse(gotJSONdata); // 파서를 통해 JSONObject를 받아옵니다.
		    JSONObject parsedJSON = (JSONObject)obj;
		    
		    listArray = (JSONArray)parsedJSON.get("list"); // list의 JSONArray를 분리해 냅니다.

		} catch (Exception e) {
			e.printStackTrace();
		}

	    return listArray;
	}
	
	// 전송받은 지역을 이용해 TM Code 근처의 스테이션(측정국) 이름을 반환하는 메소드입니다.
	public static String getTMCode(String locationName)
	{
		String 	getTMAddr = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName=";
				getTMAddr = getTMAddr.concat(locationName).concat("&pageNo=1&numOfRows=1&_returnType=json&ServiceKey=").concat(BotConfig.TOKENOPENAPI);
				 // 전송받은 지역의 TM 코드를 반환하는 API입니다.
		String 	getStaAddr = null;		
		String 	answer = null;   // 결과값을 준비합니다.
		
		
		JSONArray listArray = takeJSONArray(getTMAddr);
		JSONObject gotData = (JSONObject) listArray.get(0);
		    
		getStaAddr = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?tmX=";
		getStaAddr = getStaAddr.concat(gotData.get("tmX").toString()).concat("&tmY=").concat(gotData.get("tmY").toString());
		getStaAddr = getStaAddr.concat("&pageNo=1&numOfRows=1&_returnType=json&ServiceKey=").concat(BotConfig.TOKENOPENAPI);
		 // TM 코드를 통해 가장 가까운 기지국을 반환받는 API입니다.
		
		JSONArray finalArray = takeJSONArray(getStaAddr);
		JSONObject gotFinalData = (JSONObject) finalArray.get(0);
		
		answer = gotFinalData.get("stationName").toString();
		    
		return answer;
	}
	
	// 지역 이름을 받아, 그 지역 근처 측정국의 PM 10(미세먼지) 수치를 제공하는 메소드입니다.
	public static String getPMStatus(String locationName)
	{
		String 	getPMAddr = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?ServiceKey=";
				getPMAddr = getPMAddr.concat(BotConfig.TOKENOPENAPI).concat("&numOfRows=1&pageSize=1&pageNo=1&startPage=1&stationName=");
				getPMAddr = getPMAddr.concat(getTMCode(locationName)).concat("&dataTerm=DAILY&_returnType=json");
				 // PM 10 수치를 제공받기 위한 API 주소입니다.
		String 	answer = null;   // 결과값을 준비합니다.
		
		
		JSONArray listArray = takeJSONArray(getPMAddr);
		JSONObject gotData = (JSONObject) listArray.get(0);
		    
		String checker = null;
		    
		if (Integer.valueOf(gotData.get("pm10Value").toString()) <= 30)
			checker = "좋음";
		else if (Integer.valueOf(gotData.get("pm10Value").toString()) <= 80)
			checker = "보통";
		else if (Integer.valueOf(gotData.get("pm10Value").toString()) <= 150)
			checker = "나쁨";
		else
			checker = "매우 나쁨";
		    
		StringBuilder sb = new StringBuilder(gotData.get("pm10Value").toString()).append(" << 입력하신 ").append(locationName).append(" 지역의 PM10 수치입니다.\n");
		sb.append("이 곳의 미세먼지 농도는 ").append(checker).append(" 정도입니다. ^^");
		answer = sb.toString();
		
		return answer;
	}
}
