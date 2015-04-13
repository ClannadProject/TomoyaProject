package de.knoobie.project.tomoya.utils;

import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.Navigator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import de.knoobie.project.fuko.database.domain.msc.MSCClannadMeta;

public abstract class SearchResultView<T extends MSCClannadMeta> extends CssLayout {
    
    private CustomLayout layout;
    private Label image, name, description, subinfo;
    
    public SearchResultView(Navigator navigator, T result, String stylename) {
        setSizeFull();
        layout = new CustomLayout("searchresult");
        layout.setSizeFull();
        addStyleName("searchresult-"+stylename);
        addComponent(layout);
        init(navigator, result);
    }
    
    public void select() {
        addStyleName("searchresult-selected");
    }
    
    public void deselect() {
        removeStyleName("searchresult-selected");
    }
    
    public void init(Navigator navigator, T result) {
        image = new Label("", ContentMode.HTML);
        image.setValue(getBase64EncodedImageString(result));
        layout.addComponent(image, "searchresult.image");
        
        name = new Label("", ContentMode.HTML);
        name.setValue(result.getName());
        layout.addComponent(name, "searchresult.name");
        
        description = new Label("", ContentMode.HTML);
//        description.setValue(result.getDescription());
        layout.addComponent(description, "searchresult.description");
        
        addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            select();
            navigator.navigateTo(result.getLink());
        });
    }
    
    public abstract String getBase64EncodedImageString(T result);
    
}
