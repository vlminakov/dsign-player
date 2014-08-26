package media;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import dsnet.HttpConstants;
import dsnet.HttpCore;
import main.Options;
import main.Resource;
import utils.RatioCalculator;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public abstract class Player {
	
	private Media		media;
	private MediaView	mediaView;
	private MediaPlayer	mediaPlayer;
	private int			countPlayed;
	
	private Image		image;
	private ImageView	imageView;
	
	private Resource	resource;
	
	public abstract void onEndFile(Node view);
	
	public Player(Resource resource) {
		System.out.println("plauer constructor:  --  " + resource.getName());
		playNext(resource);
	}
	
	//start to play nex media
	public void playNext(final Resource resource) {
		this.resource = resource;
		countPlayed = 0;
		//denug info
		System.out.println(resource.getName());
		if (resource.getType() == Resource.INCOMPATABLE) {
			onEndFile(getView());
		}
		//this.resource = resource;
		if (resource.getName() != null){
			//sending statistics to the server
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					if (resource.getName().equals("logo")){
						return;
					}
					HttpCore http = new HttpCore();
					SimpleDateFormat sdf_d = new SimpleDateFormat("dd.MM.yyyy");
					SimpleDateFormat sdf_t = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date(System.currentTimeMillis());
					String d = sdf_d.format(date);
					String t = sdf_t.format(date);
					JSONObject jObject = new JSONObject();
					try {
						jObject.put("filename", new String(resource.getName()));
						jObject.put("upid", new String(Options.getInstance().getUpid()));
						jObject.put("date", new String(d));
						jObject.put("time", new String(t));
						String jsonStr = jObject.toString();
						http.postToServer(HttpConstants.HTTP_BASE_URL, HttpConstants.HTTP_ADD_STATISTICS_PATH, jsonStr);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
		// end sending statistics
		
		switch (resource.getType()) {
			case Resource.MUSIC:
			case Resource.VIDEO:
				media = new Media(resource.getPath());
				mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setCycleCount((int)resource.getCount());
				System.out.println(mediaPlayer.getCycleCount());
				mediaPlayer.setAutoPlay(true);
				mediaView = new MediaView(mediaPlayer);
				mediaView.setPreserveRatio(true);
				mediaPlayer.setOnEndOfMedia(new Runnable() {
					@Override
					public void run() {
						System.out.println("countPlayed = " + countPlayed);
						if (++countPlayed < Player.this.resource.getCount()){//"Player.this" - because I don't like to use FINAL
							System.out.println("countPlayed = " + countPlayed);
							mediaPlayer.play();
						}
						else {
							countPlayed = 0;
							onEndFile(getView());
						}
					}
				});
				//when file was loaded - resize it
				mediaPlayer.setOnReady(new Runnable() {
					@Override
					public void run() {
						resizeAll();
					}
				});
				break;

			case Resource.BITMAP:
				image = new Image(resource.getPath());
				imageView = new ImageView(image);
				imageView.setPreserveRatio(true);
				imageView.setSmooth(true);// beautiful effect)
				imageView.setCache(true);// ???? 
				//setup time to show bitmap
				long count = resource.getCount();
				if (resource.getId() == null || !resource.getId().equals(Resource.LOGO_ID))
					count *= 1000;
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								onEndFile(getView());
							}
						});
					}
				}, count);
				break;
		}
	}

	public void resizeAll() {
		RatioCalculator calculator = null;
		switch (resource.getType()) {
			case Resource.BITMAP:
				//setup bitmap's sizes equals with sizes of window
				imageView.setFitWidth(imageView.getScene().getWidth());
				imageView.setFitHeight(imageView.getScene().getHeight());
				//calculating shiftX, shiftY for translate image to the center of window
				calculator = new RatioCalculator(
						imageView.getFitWidth(),
						imageView.getFitHeight(),
						image.widthProperty().doubleValue(),
						image.heightProperty().doubleValue()
						);
				//translating
				imageView.setTranslateX(calculator.shiftX);
				imageView.setTranslateY(calculator.shiftY);
				break;
			case Resource.MUSIC:
			case Resource.VIDEO:
				// ^^^ see on top)
				mediaView.setFitWidth(mediaView.getScene().getWidth());
				mediaView.setFitHeight(mediaView.getScene().getHeight());
				calculator = new RatioCalculator(
						mediaView.getFitWidth(),
						mediaView.getFitHeight(),
						media.widthProperty().doubleValue(),
						media.heightProperty().doubleValue()
						);
				mediaView.setTranslateX(calculator.shiftX);
				mediaView.setTranslateY(calculator.shiftY);
				break;
		}
	}

	//get current view
	public Node getView() {
		switch (resource.getType()) {
			case Resource.MUSIC:
			case Resource.VIDEO:
				return mediaView;
			case Resource.BITMAP:
				return imageView;
		}
		return mediaView;
	}

	public void stopPlaying() {
		if (mediaPlayer != null)
			mediaPlayer.stop();
	}
}
