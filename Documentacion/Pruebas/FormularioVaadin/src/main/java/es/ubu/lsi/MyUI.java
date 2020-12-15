package es.ubu.lsi;

import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label label;
	private TextField textField;
	private DateField date;
	private CheckBox checkbox;
	private ListSelect select;
	private Button button;
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	FormLayout layout = new FormLayout();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		setContent(layout);

		label = new Label("Texto no editable");
		layout.addComponent(label);

		textField = new TextField("Nombre");
		textField.setValue("Tu nombre");
		layout.addComponent(textField);

		date = new DateField("Fecha");
		date.setValue(new Date());
		date.setResolution(Resolution.DAY);
		layout.addComponent(date);

		checkbox = new CheckBox("Checkbox");
		checkbox.setValue(true);
		layout.addComponent(checkbox);

		select = new ListSelect("Lista");
		select.addItems("item1", "item2", "item3");
		select.setNullSelectionAllowed(false);
		select.setRows(3);
		select.setValue("item1");
		layout.addComponent(select);

		button = new Button("Actualizar");
		button.addClickListener(new AceptarBotonListener());
		layout.addComponent(button);
    }
    
    private class AceptarBotonListener implements Button.ClickListener {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
    	public void buttonClick(ClickEvent event){
    		label.setValue("Hola: " + textField.getValue() + ". Has seleccionado el dia: "
    				+  date.getValue().toString() + " ,el valor del checkbox es " + checkbox.getValue().toString()
    				+ " y el item seleccionado es: " + select.getValue().toString());
    	}
    	
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
    }
}
