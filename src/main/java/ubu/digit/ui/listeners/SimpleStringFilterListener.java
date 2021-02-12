package ubu.digit.ui.listeners;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Table;

/**
 * Listener que hace de filtro de una cadena de texto, para una columna deuna
 * tabla.
 * 
 * @author Javier de la Fuente Barrios
 */
public class SimpleStringFilterListener implements TextChangeListener {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 9041696136649951139L;

	/**
	 * Filtro a aplicar.
	 */
	private SimpleStringFilter filter = null;

	/**
	 * Tabla donde aplicar el filtro.
	 */
	private Table table;

	/**
	 * Nombre de la columna de la tabla donde aplicar el filtro.
	 */
	private String propertyId;

	/**
	 * Constructor.
	 * 
	 * @param table
	 *            tabla
	 * @param propertyId
	 *            nombre de la columna
	 */
	public SimpleStringFilterListener(Table table, String propertyId) {
		this.propertyId = propertyId;
		this.table = table;
	}

	/**
	 * Operación a realizara al recibir un evento.
	 */
	@Override
	public void textChange(TextChangeEvent event) {
		Filterable f = (Filterable) table.getContainerDataSource();

		if (filter != null) {
			f.removeContainerFilter(filter);
		}

		filter = new SimpleStringFilter(propertyId, (String) event.getText(), true, false);
		f.addContainerFilter(filter);
	}
}
