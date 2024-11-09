package pers.kinson.wechat.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import jforgame.commons.NumberUtil;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.discussion.message.vo.Person;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateDiscussionController implements ControlledStage, Initializable {

    @FXML
    private TableView tableView;

    private ObservableList<Person> myFriends;

    @FXML
    private void handleComplete() {
        // 例如，获取选择的联系人并创建讨论组
        List<Person> collect = myFriends.stream().filter(e -> e.isSelected()).collect(Collectors.toList());
        System.out.println(collect.size());
    }

    @FXML
    private void handleCancel() {
        // 处理取消按钮的逻辑
        // 例如，关闭窗口
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.id.CreateDiscussion);
    }

    @Override
    public void onStageShown() {
        tableView.getColumns().clear();

        List<Person> friends = Context.friendManager.getFriends().stream().map(f -> new Person(new Image("@../../login/img/headimag.png"), f.getFullName())).collect(Collectors.toList());
        myFriends = FXCollections.observableArrayList(
                friends
        );

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // 多选框列
        TableColumn<Person, Boolean> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellValueFactory(param -> param.getValue().selectedProperty());
        selectColumn.setCellFactory(p -> new CheckBoxTableCell<>());

        TableColumn<Person, ImageView> imageColumn = new TableColumn<>("Image");
        TableColumn<Person, String> nameColumn = new TableColumn<>("Name");

        // 设置多选
        tableView.getSelectionModel().setCellSelectionEnabled(true);

        // 为列绑定数据和工厂
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        int rowsToShow = myFriends.size(); // 您想要显示的行数
        tableView.setFixedCellSize(100);
        tableView.setPrefHeight(tableView.getFixedCellSize() * rowsToShow);
        // 添加列到TableView
        tableView.getColumns().addAll(selectColumn, imageColumn, nameColumn);

        // 设置数据
        tableView.setItems(myFriends);

        // 设置多选模式
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    static class CheckBoxTableCell<S, Boolean> extends TableCell<S, Boolean> {

        private final CheckBox checkBox;

        public CheckBoxTableCell() {
            this.checkBox = new CheckBox();
            this.setGraphic(checkBox);
            checkBox.setOnAction(event -> {
                Person value = (Person) getTableView().getItems().get(getIndex());
                value.setSelected(checkBox.isSelected());
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                checkBox.setIndeterminate(false);
                checkBox.setSelected(false);
            } else {
                checkBox.setSelected(NumberUtil.booleanValue(item));
            }
        }
    }


}
