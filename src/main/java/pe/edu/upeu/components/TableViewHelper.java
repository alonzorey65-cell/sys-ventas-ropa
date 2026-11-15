package pe.edu.upeu.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.function.Function;

public class TableViewHelper {

    /**
     * Configura y llena una columna de una TableView de manera rápida.
     */
    public static <T, S> void configurarColumna(TableColumn<T, S> columna, String propiedad) {
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
    }

    /**
     * Convierte una lista estándar de Java en un ObservableList apto para TableView.
     */
    public static <T> ObservableList<T> getObservableList(List<T> lista) {
        return FXCollections.observableArrayList(lista);
    }
}