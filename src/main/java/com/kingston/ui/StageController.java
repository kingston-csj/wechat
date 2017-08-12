package com.kingston.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
			Pane tmpPane = (Pane)loader.load();
			ControlledStage controlledStage = (ControlledStage)loader.getController();
			controlledStage.setController(this);
			
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
	
	public boolean setStage(String name) {
		Stage stage = this.getStageBy(name);
		if (stage == null) {
			return false;
		}
		stage.show();
		return true;
	}
	
	public boolean switchStage(String toShow, String toClose) {
		getStageBy(toClose).close();
		setStage(toShow);
		
		return true;
	}
	
	public boolean unloadStage(String name) {
		return this.stages.remove(name) != null;
	}
	
}
