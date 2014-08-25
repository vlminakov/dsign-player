package json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.Options;
import main.Path;
import main.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.apache.http.message.BasicNameValuePair;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import dsnet.HttpConstants;
import dsnet.HttpCore;
import system.Sheduler;

public class JsonParser implements IJSONConst, Path {
	private Sheduler sheduler;

	public JsonParser(String url, Sheduler sheduler) {
		//downloading json
		this.sheduler = sheduler;
		
		HttpCore httpEngine = new HttpCore();
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("upid", Options.getInstance().getUpid()));
			processJson(httpEngine.getFromServer(HttpConstants.HTTP_BASE_URL, HttpConstants.HTTP_GET_TT_PATH, params));
		} catch (InterruptedException | ExecutionException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void processJson(String jsonString){
		parse(jsonString, this.sheduler);
	}

	public Sheduler parse(String json, Sheduler sheduler) {
		try {
			JSONObject main = new JSONObject(json);
			JSONObject table = main.getJSONObject(TIME_TABLE);
			JSONArray days = table.getJSONArray(DAY);
			for (int i = 0; i < days.length(); i++) {
				JSONObject day = days.getJSONObject(i);
				String date = day.getString(DATE);
				if (!date.equalsIgnoreCase(getCurrentTimeStamp("dd.MM.yyyy")))
					continue;
				sheduler.setCycle(day.getBoolean(CYCLED));
				sheduler.setTimeToStart(getTime(date + " " + day.getString(TIME)));
				sheduler.addResource(Resource.getLogoResources(sheduler.getTimeToStart()));
				JSONArray files = day.getJSONArray(FILE);
				for (int j = 0; j < files.length(); j++) {
					JSONObject file = files.getJSONObject(j);
					Resource resource = new Resource(Path.MEDIA_URL + file.getString(NAME), file.getLong(DURATION));
					resource.setName(file.getString(NAME));
					sheduler.addResource(resource);
				}
			}
			
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return sheduler;
	}

	private long getTime(String time) {
		DateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC+3"));
		Date date;
		try {
			date = sdf.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static String getCurrentTimeStamp(String format) {
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

}
