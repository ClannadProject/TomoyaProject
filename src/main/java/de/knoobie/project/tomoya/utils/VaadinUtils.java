package de.knoobie.project.tomoya.utils;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import de.knoobie.project.clannadutils.common.StringUtils;

public class VaadinUtils {

    public enum ButtonStyle {

        PRIMARY("button-primary"),
        SUCCESS("button-primary"),
        ERROR("button-primary"),
        WARNING("button-primary"),
        WHITE("button-primary"),
        SECONDARY("secondary-button");

        public static final String DEFAULT = "pure-button";
        private final String style;

        ButtonStyle(String style) {
            this.style = style;
        }

        public String getStyle() {
            return DEFAULT+ ""+style;
        }
        
        
    }

    public static Label generateLinkLabel(String caption) {
        Label linkLabel = new Label(StringUtils.EMPTY, ContentMode.HTML);
        linkLabel.setCaption(StringUtils.trim(caption));
        return linkLabel;
    }

    public static Button create(String caption, ButtonStyle style) {
        Button b = new Button(caption);
        b.setStyleName(style.getStyle());
        b.setImmediate(true);
        return b;
    }
}
