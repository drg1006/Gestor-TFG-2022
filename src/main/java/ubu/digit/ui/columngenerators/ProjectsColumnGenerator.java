package ubu.digit.ui.columngenerators;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

import ubu.digit.ui.beans.HistoricProjectBean;

/**
 * Generador de las columnas de los proyectos. Si el proyecto dispone de enlace
 * a repositorio, se añade un link a éste.
 * 
 * @author Javier de la Fuente Barrios
 *
 */
public class ProjectsColumnGenerator implements ColumnGenerator {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -5925982074718277269L;

	/**
	 * Genera la celda correspondiente.
	 */
	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
		HistoricProjectBean bean = (HistoricProjectBean) item.getBean();

		String title = bean.getTitle();
		String repositoryLink = bean.getRepositoryLink();

		if (!"".equals(repositoryLink)) {
			Link link = new Link(title, new ExternalResource(repositoryLink));
			link.setTargetName("_blank");
			return link;
		} else {
			return title;
		}
	}

}
