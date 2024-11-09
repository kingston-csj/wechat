package pers.kinson.wechat.logic.discussion.message.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {
    final String name;
    final Image image;
    final ImageView imageView;
    private BooleanProperty selected = new SimpleBooleanProperty();

    public Person(Image image, String name) {
        this.name = name;
        this.image = image;
        this.imageView = new ImageView(image);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
