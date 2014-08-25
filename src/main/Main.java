package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import dsnet.HttpConstants;
import dsnet.HttpCore;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import json.JsonParser;
import media.Player;
import system.IUpdateServiceDelegate;
import system.Sheduler;
import system.UpdateService;
import utils.FileLoader;

public class Main extends Application implements IUpdateServiceDelegate {

	private Player			player;
	private UpdateService	service;
	private Sheduler		sheduler;

	@Override
	public void start(final Stage stage) {
		stage.setTitle("Player");
		
		//Setup window layout
		final Group root = new Group();
		Scene scene = new Scene(root, 800, 480, Color.BLACK);

		player = new Player(sheduler.getResource()) {
			//the action of end of media file / end time to show picture
			@Override
			public void onEndFile(Node view) {
				//get new media file
				Resource resource = sheduler.getResource();
				if (resource != null) {
					//delete current view with video/audio/bitmap
					root.getChildren().remove(view);
					//starting to play
					playNext(resource);
					//adding view with new media
					root.getChildren().add(player.getView());
					//resize picture with sizes of window
					if (resource.getType() == Resource.BITMAP)
						player.resizeAll();
				} else {
					System.out.println("resource is null");
				}
			}
		};

		//resize listener
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue,
					Number oldSceneWidth, Number newSceneWidth) {
				player.resizeAll();
			}
		});

		scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue,
					Number oldSceneWidth, Number newSceneWidth) {
				player.resizeAll();
			}
		});
		
		//mouse listener
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setFullScreen(!stage.isFullScreen());
			}
		});

		//end of forming layout
		root.getChildren().add(player.getView());
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.show();
	}

	private void initShedule() {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("upid", Options.getInstance().getUpid()));
		HttpCore core = new HttpCore();
		try {
			JSONObject cred = new JSONObject(core.getFromServer(HttpConstants.HTTP_BASE_URL, HttpConstants.HTTP_GET_CREDENTIALS_PATH, params));
			Options.getInstance().setEmail(cred.getString("email"));
			Options.getInstance().setPassword(cred.getString("password"));
			System.out.println(Options.getInstance().getEmail());
			System.out.println(Options.getInstance().getPassword());
		} catch (JSONException | InterruptedException | ExecutionException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		service = new UpdateService(new Runnable() {
			@Override
			public void run() {
				if (player != null)
					player.stopPlaying();
				sheduler = new Sheduler();
//				sheduler.addResource(Resource.getLogoResources(0));
				new JsonParser(Path.JSON_URL, sheduler);
				FileLoader.startLoading(sheduler.getResources());
			}
		}, this);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		if (player != null)
			player.stopPlaying();
		service.setStart(false);
		boolean retry = true;
		while (retry) {
			try {
				service.join();
				retry = false;
			} catch (InterruptedException exception) {
				retry = true;
				System.out.println(exception.getLocalizedMessage());
			}
		}
	}

	public Main() {
		//init shedule=))
		initShedule();
	}

	public static void main(String[] args) {
		launch(args);
		new Main();
	}

	@Override
	public Vector<Resource> getResources() {
		if (this.sheduler != null)
			return this.sheduler.getResources();
		
		return null;
	}
}