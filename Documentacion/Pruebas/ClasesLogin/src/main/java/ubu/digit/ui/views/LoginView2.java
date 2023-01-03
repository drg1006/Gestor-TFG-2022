package ubu.digit.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.ErrorMessage;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ubu.digit.security.*;
import ubu.digit.util.Constants;
import ubu.digit.util.UtilMethods;
import ubu.digit.webService.CoreCourseGetCoursesByField;
import ubu.digit.webService.CoreCourseGetUserAdministrationOptions;
import ubu.digit.webService.CoreUserGetCourseUserProfiles;
import ubu.digit.webService.CoreWebserviceGetSiteInfo;
import ubu.digit.ui.entity.Course;
import ubu.digit.ui.entity.MoodleUser;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;

/**
 * Vista de inicio de sesión.
 * 
 * @author Diana Bringas Ochoa
 */
@Route(value = "Logins")
@PageTitle("Logins ")
public class LoginView2 extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm(); 

    public LoginView2(){
        addClassName("login-view");
        setSizeFull(); 
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login"); 

        add(new H1("Vaadin CRM"), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if(beforeEnterEvent.getLocation()  
        .getQueryParameters()
        .getParameters()
        .containsKey("error")) {
            login.setError(true);
        }
    }
}