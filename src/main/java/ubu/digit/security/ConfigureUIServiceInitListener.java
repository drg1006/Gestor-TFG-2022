package ubu.digit.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;
import ubu.digit.ui.views.*;
@Component 
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

	@Override
	public void serviceInit(ServiceInitEvent event) { 
		event.getSource().addUIInitListener(uiEvent -> {
			final UI ui = uiEvent.getUI();
			ui.addBeforeEnterListener(this::authenticateNavigation);
		});
	}

	private void authenticateNavigation(BeforeEnterEvent event) { 
/*
	    if ((UploadView.class.equals(event.getNavigationTarget()) 
	            || AceptView.class.equals(event.getNavigationTarget()) 
	            || newProjectView.class.equals(event.getNavigationTarget()))
	            && !SecurityUtils.isUserLoggedIn()) {
	            event.rerouteTo(LoginView22.class);
	        }*/
	    System.out.println("estado "+LoginView.validado());
	    System.out.println("event "+event.getNavigationTarget());
	    if ((UploadView.class.equals(event.getNavigationTarget()) 
                || AceptView.class.equals(event.getNavigationTarget()) 
                || newProjectView.class.equals(event.getNavigationTarget()))
                && !LoginView.validado()) {
                event.rerouteTo(LoginView.class);
            }
	}
}