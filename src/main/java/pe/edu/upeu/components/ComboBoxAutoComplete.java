package pe.edu.upeu.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.stream.Stream;

public class ComboBoxAutoComplete<T> {

    private final ComboBox<T> comboBox;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public ComboBoxAutoComplete(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        this.data = comboBox.getItems();

        this.comboBox.setEditable(true);
        this.comboBox.getEditor().setOnKeyPressed(event -> handleOnKeyPressed(event));
        this.comboBox.getEditor().setOnKeyReleased(event -> handleOnKeyReleased());
    }

    private void handleOnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            return;
        }

        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
            return;
        }

        if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }
    }

    private void handleOnKeyReleased() {
        String s = comboBox.getEditor().getText();
        if (s == null || s.isEmpty()) {
            comboBox.setItems(data);
            return;
        }

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T t : data) {
            if (t != null && t.toString().toLowerCase().contains(s.toLowerCase())) {
                list.add(t);
            }
        }

        comboBox.setItems(list);
        comboBox.hide();
        comboBox.show();

        if (!moveCaretToPos) {
            try {
                comboBox.getEditor().positionCaret(comboBox.getEditor().getText().length());
            } catch (Exception ignored) {}
        }
        moveCaretToPos = false;
    }
}