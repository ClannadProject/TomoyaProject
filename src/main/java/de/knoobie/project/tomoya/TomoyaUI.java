package de.knoobie.project.tomoya;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import de.knoobie.project.tomoya.view.TomoyaNavigator;
import javax.servlet.annotation.WebInitParam;

/**
 *
 */
@Theme("tomoya")
@Widgetset("de.knoobie.project.tomoya.TomoyaWidgetset")
public class TomoyaUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
         Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("Controller");

//        addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(new TomoyaNavigator(this));
        setSizeFull();
    }

    public static TomoyaUI get() {
        return (TomoyaUI) UI.getCurrent();
    }

    @WebServlet(value = "/*", asyncSupported = true, initParams = {
        @WebInitParam(name = "ui", value = "de.knoobie.project.tomoya.TomoyaUI"),
        @WebInitParam(name = "productionMode", value = "false")})
    public static class Servlet extends VaadinServlet {
    }
}
