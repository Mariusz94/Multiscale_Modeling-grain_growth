package pl.mariuszlyszczarz;

import javafx.beans.property.SimpleStringProperty;

public class TableInfoGrainModel implements Comparable {
    private SimpleStringProperty id;
    private SimpleStringProperty size;
    private SimpleStringProperty percent;
    private SimpleStringProperty color;

    public TableInfoGrainModel(String id, String size, String percent, String color) {
        this.id = new SimpleStringProperty(id);
        this.size = new SimpleStringProperty(size);
        this.percent = new SimpleStringProperty(percent);
        this.color = new SimpleStringProperty(color);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getSize() {
        return size.get();
    }

    public SimpleStringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public String getPercent() {
        return percent.get();
    }

    public SimpleStringProperty percentProperty() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent.set(percent);
    }

    public String getColor() {
        return color.get();
    }

    public SimpleStringProperty colorProperty() {
        return color;
    }

    public void setColor(String color) {
        this.color.set(color);
    }

    @Override
    public String toString() {
        return "TableInfoGrainModel{" +
                "id=" + id +
                ", size=" + size +
                ", percent=" + percent +
                ", color=" + color +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(Double.parseDouble(((TableInfoGrainModel) o).percent.get()), Double.parseDouble(this.percent.get()));
    }
}
