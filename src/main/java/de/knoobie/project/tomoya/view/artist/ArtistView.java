package de.knoobie.project.tomoya.view.artist;

import com.google.gson.JsonSyntaxException;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.knoobie.project.clannadutils.common.FileUtils;
import de.knoobie.project.clannadutils.common.StringUtils;
import de.knoobie.project.fuko.database.bo.DatabaseOperationResult;
import de.knoobie.project.fuko.database.domain.Artist;
import de.knoobie.project.fuko.database.domain.Name;
import de.knoobie.project.fuko.database.domain.embeddable.AlbumLink;
import de.knoobie.project.fuko.database.service.FukoDB;
import de.knoobie.project.nagisa.gson.util.VGMdb;
import de.knoobie.project.ryou.filesystem.utils.ArtistFileSystem;
import de.knoobie.project.tomoya.utils.TomoyaUtils;
import de.knoobie.project.tomoya.utils.TomoyaView;
import de.knoobie.project.tomoya.utils.VaadinUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArtistView extends TomoyaView implements View {

    public static final String VIEW_NAME = "artist";

    private CssLayout functions;
    private Label name, info;
    private Label coverImage, description;
    private String imagesHTML;

    private TabSheet tabSheet;
    private Artist selectedArtist;

    public ArtistView() {
        super("data");
    }

    @Override
    public void init() {
        name = new Label("", ContentMode.HTML);
        name.setWidth("100%");
        addComponent(name, "header.name");

        info = new Label("", ContentMode.HTML);
        info.setWidth("100%");
        addComponent(info, "header.subinfo");

        functions = new CssLayout();
        Button createInExplorer = VaadinUtils.create("Create", VaadinUtils.ButtonStyle.SECONDARY);
        Button openInExplorer = VaadinUtils.create("Open in Explorer", VaadinUtils.ButtonStyle.SECONDARY);
        openInExplorer.addClickListener((Button.ClickEvent event) -> {
            try {
                FileUtils.openFileBrowser(ArtistFileSystem.getArtistDirectory(selectedArtist, false));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        functions.addComponent(createInExplorer);
        functions.addComponent(openInExplorer);
        addComponent(functions, "header.buttons");

        tabSheet = new TabSheet();
        tabSheet.setWidth("100%");
        addComponent(tabSheet, "content.infos");

        coverImage = new Label("", ContentMode.HTML);
        addComponent(coverImage, "content.image");

        description = new Label("", ContentMode.PREFORMATTED);

    }

    private Component buildArtistDetails() {

        HorizontalLayout details = new HorizontalLayout();
        details.setWidth(100.0f, Unit.PERCENTAGE);
        details.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        details.setMargin(true);
        details.setSpacing(true);

        Component detailsForm = buildDetailsForm();
        details.addComponent(detailsForm);
        details.setExpandRatio(detailsForm, 1);
        return details;
    }

    private Label aliases, creditedWorks, birthdate, birthplace, bloodtype, 
            gender, bandMemberOf, currentMember, formerMember, formed, websites;

    private Component buildDetailsForm() {
        FormLayout fields = new FormLayout();
        fields.setSizeFull();
        fields.setSpacing(false);
        fields.setMargin(false);

        fields.addComponent(aliases = VaadinUtils.generateLinkLabel("Aliases"));
        fields.addComponent(birthdate = VaadinUtils.generateLinkLabel("Birthdate"));
        fields.addComponent(birthplace = VaadinUtils.generateLinkLabel("Birthplace"));
        fields.addComponent(bloodtype = VaadinUtils.generateLinkLabel("Bloodtype"));
        fields.addComponent(gender = VaadinUtils.generateLinkLabel("Gender"));
        fields.addComponent(bandMemberOf = VaadinUtils.generateLinkLabel("Bandmember of"));
        fields.addComponent(formed = VaadinUtils.generateLinkLabel("Formed"));
        fields.addComponent(currentMember = VaadinUtils.generateLinkLabel("Current Member"));
        fields.addComponent(formerMember = VaadinUtils.generateLinkLabel("Former Member"));
        fields.addComponent(websites = VaadinUtils.generateLinkLabel("Websites"));
        fields.addComponent(creditedWorks = VaadinUtils.generateLinkLabel("Credited Works"));

        return fields;
    }

    @Override
    public void select(String query) {
        try {

            selectedArtist = null;

            if (StringUtils.isEmpty(query)) {
                System.out.println("Empty und so!");
                return;
            }
            int id = StringUtils.getInt(query, 0, true);

            if (id == 0) {
                System.out.println("Wrong ID übergeben");
                return;
            }

            selectedArtist = FukoDB.getInstance().getArtistService().findByVGMdbID(id);
            if (selectedArtist == null) {
                System.out.println("Couldn't find Artist in DB - Update from VGMDB");
                DatabaseOperationResult result = FukoDB.getInstance().getArtistService().
                        updateWithRelations(Artist.getFromVGMDB(VGMdb.getArtist(query)), true);
                selectedArtist = (Artist) result.getResult();
            }

            if (selectedArtist == null) {
                return;
            }

            if (StringUtils.isEmpty(selectedArtist.getFolderName())) {
                ArtistFileSystem.createCompleteArtistStructure(selectedArtist, true);
                selectedArtist = FukoDB.getInstance().getArtistService().findByVGMdbID(selectedArtist.getVgmdbID());
            }

            System.out.println(selectedArtist.getName());

            name.setValue(selectedArtist.getName());

            Path artistPath = ArtistFileSystem.getArtistDirectory(selectedArtist, false);

            if (selectedArtist.getPicture() != null && !StringUtils.isEmpty(selectedArtist.getPicture().getPictureLocation())) {
                coverImage.setValue(TomoyaUtils.generatePicturebase64Encoded(artistPath, selectedArtist.getPicture(), ""));
            }
            tabSheet.removeAllComponents();
            tabSheet.addTab(buildArtistDetails(), "Details");

            String names = "";
            for (Name n : selectedArtist.getNames()) {
                names = names.concat(n.getName()).concat(", ");
            }
            aliases.setValue(names);

            description.setValue(StringUtils.trim(selectedArtist.getDescription()));
            tabSheet.addTab(description, "Description");

            bandMemberOf.setValue(TomoyaUtils.generateHTML(selectedArtist.getBandMemberOf(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            currentMember.setValue(TomoyaUtils.generateHTML(selectedArtist.getCurrentMember(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            websites.setValue(TomoyaUtils.generateHTML(selectedArtist.getWebsites(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            formerMember.setValue(TomoyaUtils.generateHTML(selectedArtist.getFormerMember(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));

            birthdate.setValue(StringUtils.trim(selectedArtist.getBirthdate()));
            birthplace.setValue(StringUtils.trim(selectedArtist.getBirthplace()));
            bloodtype.setValue(StringUtils.trim(selectedArtist.getBloodtype()));
            gender.setValue(StringUtils.trim(selectedArtist.getGender() == null ? "" : selectedArtist.getGender().getHumanizedName()));
            formed.setValue(StringUtils.trim(selectedArtist.getFormed()));
            creditedWorks.setValue(StringUtils.trim(selectedArtist.getCreditedWorks()));

            generateTrackTabs();

        } catch (IllegalArgumentException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void generateTrackTabs() {
        tabSheet.addTab(createAlbumLinkLayout("Discography", selectedArtist.getDiscography()), "Discography");
        tabSheet.addTab(createAlbumLinkLayout("Featured on", selectedArtist.getFeaturedOn()), "Featured on");
    }
//

    private VerticalLayout createAlbumLinkLayout(String listName, List<AlbumLink> ablums) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.addComponent(new Label("<h3>" + listName + "</h3>", ContentMode.HTML));
        Table trackTable = new Table();
        trackTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        trackTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        trackTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        trackTable.addStyleName(ValoTheme.TABLE_SMALL);
        trackTable.setSortEnabled(true);
        trackTable.setWidth("100%");
        trackTable.addContainerProperty("releaseDate", String.class, "");
        trackTable.addContainerProperty("name", Label.class, null);
        trackTable.addContainerProperty("catalog", String.class, "");
        trackTable.addContainerProperty("role", String.class, "");
        trackTable.setColumnAlignment("role", Table.Align.RIGHT);
        trackTable.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        trackTable.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
//        trackTable.setVisibleColumns("title", "duration");
//        trackTable.setColumnHeaders("Title", "Duration");

        for (AlbumLink album : ablums) {
            trackTable.addItem(album);
            trackTable.getContainerProperty(album, "releaseDate").setValue(StringUtils.trim(album.getReleaseDate()));
            Label albumName = VaadinUtils.generateLinkLabel(null);
            albumName.setValue(TomoyaUtils.generateHTML(new ArrayList<>(
                    Arrays.asList(album)), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            trackTable.getContainerProperty(album, "name").setValue(albumName);
            trackTable.getContainerProperty(album, "catalog").setValue(StringUtils.trim(album.getAlbumCatalog()));

            trackTable.getContainerProperty(album, "role").setValue(StringUtils.trim(album.getRole()));
        }
        trackTable.setPageLength(trackTable.getContainerDataSource().size());
        layout.addComponent(trackTable);

        return layout;
    }

}
