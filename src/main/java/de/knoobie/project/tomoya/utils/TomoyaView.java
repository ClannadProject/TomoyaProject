package de.knoobie.project.tomoya.utils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;

/**
 *
 * @author cKnoobie
 */
public abstract class TomoyaView extends CustomLayout implements View {
    
    public TomoyaView(String layoutname){
        super(layoutname);
        init();
    }
    
    public abstract void init();
    
    public abstract void select(String query);
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        select(event.getParameters());
    }
}
