package ubu.digit.ui.listeners;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Table;

/**
 * Listener que hace de filtro de una cadena de texto, para varias columnas de
 * una tabla.
 * 
 * @author Javier de la Fuente Barrios
 */
public class OrSimpleStringFilterListener implements TextChangeListener {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -8855180815378564739L;

	/**
	 * Filtro a aplicar.
	 */
	private Or filter = null;

	/**
	 * Tabla donde aplicar el filtro.
	 */
	private Table table;

	/**
	 * Nombre de la primera columna de la tabla.
	 */
	private String propertyId1;

	/**
	 * Nombre de la segunda columna de la tabla.
	 */
	private String propertyId2;

	/**
	 * Nombre de la tercera columna de la tabla.
	 */
	private String propertyId3;

	/**
	 * Constructor.
	 * 
	 * @param table
	 *            tabla
	 * @param propertyId1
	 *            primera columna
	 * @param propertyId2
	 *            segunda columna
	 * @param propertyId3
	 *            tercera columna
	 */
	public OrSimpleStringFilterListener(Table table, String propertyId1, String propertyId2, String propertyId3) {
		this.propertyId1 = propertyId1;
		this.propertyId2 = propertyId2;
		this.propertyId3 = propertyId3;
		this.table = table;
	}

	/**
	 * Operación a realizar al recibir el evento.
	 */
	@Override
	public void textChange(TextChangeEvent event) {
		Filterable f = (Filterable) table.getContainerDataSource();

		if (filter != null) {
			f.removeContainerFilter(filter);
		}

		filter = new Or(new SimpleStringFilter(propertyId1, event.getText(), true, false),
				new SimpleStringFilter(propertyId2, event.getText(), true, false),
				new SimpleStringFilter(propertyId3, event.getText(), true, false));
		f.addContainerFilter(filter);
	}
}