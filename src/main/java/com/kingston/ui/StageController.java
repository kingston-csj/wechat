package com.kingston.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageController {

	private Map<String, Stage> stages = new HashMap<>();

	public void addStage(String name, Stage stage) {
		this.stages.put(name, stage);
	}

	public Stage getStageBy(String name) {
		return this.stages.get(name);
	}

	public void setPrimaryStage(String name, Stage stage) {
		this.addStage(name, stage);
	}

	public Stage loadStage(String name, String resource, StageStyle... styles) {
		Stage result = null;
		try{
			URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
			FXMLLoader loader = new FXMLLoader(url);
			Pane tmpPane = (Pane)load(resource, Pane.class, ResourceBundle.getBundle("i18n/message"));
			ControlledStage controlledStage = (ControlledStage)loader.getController();
			//			controlledStage.setController(this);
			Scene tmpScene = new Scene(tmpPane);
			result = new Stage();
			result.setScene(tmpScene);

			for (StageStyle style:styles) {
				result.initStyle(style);
			}
			this.addStage(name, result);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Stage loadStage(String name, String resource, ResourceBundle resources, StageStyle... styles) {
		Stage result = null;
		try{
			Pane tmpPane = (Pane)load(resource, Pane.class, resources);

			Scene tmpScene = new Scene(tmpPane);
			result = new Stage();
			result.setScene(tmpScene);
			this.addStage(name, result);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T load(String resource, Class<T> clazz) {
		try{
			URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
			FXMLLoader loader = new FXMLLoader(url);
			return (T)loader.load();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T load(String resource, Class<T> clazz, ResourceBundle resources) {
		try{
			URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
			return (T)FXMLLoader.load(url, resources);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public Stage setStage(String name) {
		Stage stage = this.getStageBy(name);
		if (stage == null) {
			return null;
		}
		stage.show();
		return stage;
	}

	public boolean switchStage(String toShow, String toClose) {
		getStageBy(toClose).close();
		setStage(toShow);

		return true;
	}

	public void closeStge(String name) {
		Stage target = getStageBy(name);
		target.close();
	}

	public boolean unloadStage(String name) {
		return this.stages.remove(name) != null;
	}

}
