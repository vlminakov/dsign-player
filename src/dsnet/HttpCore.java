package dsnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpRequestFutureTask;

public class HttpCore {
	private HttpClient client;
	private ExecutorService execService;
	private FutureRequestExecutionService requestExecService;
	private ResponseHandler<String> handler;
	private HttpGet request;
	
	public HttpCore() {
		this.client = HttpClientBuilder.create().setMaxConnPerRoute(5).setMaxConnTotal(5).build();
        this.handler = new ResponseHandler<String>() {
			
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 1024);
				StringBuilder sb = new StringBuilder();
				String str = "";
				while ((str = reader.readLine()) != null){
					sb.append(str);
				}
				
				return sb.toString();
			}
		};
	}
	
	public String getFromServer(String baseUrl, String path, List<NameValuePair> params) throws InterruptedException, ExecutionException, IOException{
		String resultString = "";
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		this.execService = Executors.newFixedThreadPool(5);
        this.requestExecService = new FutureRequestExecutionService(client, execService);
        if (params != null && params.size() > 0){
        	/*this.request = (HttpGet) RequestBuilder.get().setUri(baseUrl + path).addParameters((NameValuePair[])params.toArray()).build();
        } else {
         	this.request = (HttpGet) RequestBuilder.get().setUri(baseUrl + path).build();*/
        	for (NameValuePair nameValuePair : params) {
				sb.append(nameValuePair.getName()).append("=").append(nameValuePair.getValue()).append("&");
			}
        	sb.setLength(sb.length() - 1);
        }
        request = new HttpGet(baseUrl + path + sb.toString());
        HttpRequestFutureTask<String> rfTask = this.requestExecService.execute(request, HttpClientContext.create(), handler);
        resultString = rfTask.get();
        System.out.println(resultString);
        requestExecService.close();
        
		return resultString;
	}
	
	public boolean postToServer(String baseUrl, String path, String jsonStr) throws Exception {
		if (this.client == null)
			throw new Exception("Client not initialized");
		
        HttpPost req = new HttpPost(baseUrl + path);
        req.setEntity(new StringEntity(jsonStr, ContentType.create("application/json")));
        HttpResponse resp = client.execute(req);
        
        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
        	return true;
        }
        
        return false;
	}
}
