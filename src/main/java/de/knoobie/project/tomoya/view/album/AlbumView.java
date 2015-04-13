package de.knoobie.project.tomoya.view.album;

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
import de.knoobie.project.fuko.database.domain.Album;
import de.knoobie.project.fuko.database.domain.AlbumDisc;
import de.knoobie.project.fuko.database.domain.AlbumTrack;
import de.knoobie.project.fuko.database.domain.Name;
import de.knoobie.project.fuko.database.service.FukoDB;
import de.knoobie.project.nagisa.gson.model.bo.enums.VGMdbNameLanguage;
import de.knoobie.project.nagisa.gson.util.VGMdb;
import de.knoobie.project.ryou.filesystem.utils.AlbumFileSystem;
import de.knoobie.project.tomoya.utils.TomoyaUtils;
import de.knoobie.project.tomoya.utils.TomoyaView;
import de.knoobie.project.tomoya.utils.VaadinUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlbumView extends TomoyaView implements View {

    public static final String VIEW_NAME = "album";

    private CssLayout functions;
    private Label name, info;
    private Label coverImage, images, description;
    private String imagesHTML;

    private TabSheet tabSheet;
    private Album selectedAlbum;

    public AlbumView() {
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
                FileUtils.openFileBrowser(AlbumFileSystem.getAlbumDirectory(selectedAlbum, false));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        Button playAlbum = VaadinUtils.create("Play Album", VaadinUtils.ButtonStyle.SECONDARY);
        functions.addComponent(createInExplorer);
        functions.addComponent(openInExplorer);
        functions.addComponent(playAlbum);
        addComponent(functions, "header.buttons");

        tabSheet = new TabSheet();
        tabSheet.setWidth("100%");
        addComponent(tabSheet, "content.infos");

        coverImage = new Label("", ContentMode.HTML);
//        coverImage.setWidth("250px");
//        coverImage.setHeight("250px");
        addComponent(coverImage, "content.image");

        images = new Label("", ContentMode.HTML);
        description = new Label("", ContentMode.PREFORMATTED);

    }

    private Component buildMovieDetails() {

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

    private Label catalog, releaseDate, releaseFormat, releasePrice, mediaFormat, classification,
            publishedBy, composedBy, arrangedBy, performedBy, lyricsBy, relatedProducts, relatedAlbums, releaseEvents;

    private Component buildDetailsForm() {
        FormLayout fields = new FormLayout();
        fields.setSizeFull();
        fields.setSpacing(false);
        fields.setMargin(false);

        fields.addComponent(relatedProducts = VaadinUtils.generateLinkLabel("Related Products"));
        fields.addComponent(catalog = VaadinUtils.generateLinkLabel("Catalog Number"));
        fields.addComponent(releaseDate = VaadinUtils.generateLinkLabel("Release Date"));
        fields.addComponent(releaseFormat = VaadinUtils.generateLinkLabel("Publish Format"));
        fields.addComponent(releasePrice = VaadinUtils.generateLinkLabel("Release Price"));
        fields.addComponent(releaseEvents = VaadinUtils.generateLinkLabel("Release Events"));
        fields.addComponent(mediaFormat = VaadinUtils.generateLinkLabel("Media Format"));
        fields.addComponent(classification = VaadinUtils.generateLinkLabel("Classification"));
        fields.addComponent(publishedBy = VaadinUtils.generateLinkLabel("Published by"));
        fields.addComponent(composedBy = VaadinUtils.generateLinkLabel("Composed by"));
        fields.addComponent(arrangedBy = VaadinUtils.generateLinkLabel("Arranged by"));
        fields.addComponent(performedBy = VaadinUtils.generateLinkLabel("Performed by"));
        fields.addComponent(lyricsBy = VaadinUtils.generateLinkLabel("Lyrics by"));
        fields.addComponent(relatedAlbums = VaadinUtils.generateLinkLabel("Related Albums"));

        return fields;
    }
    

    @Override
    public void select(String query) {
        try {

            selectedAlbum = null;

            if (StringUtils.isEmpty(query)) {
                System.out.println("Empty und so!");
                return;
            }
            int id = StringUtils.getInt(query, 0, true);

            if (id == 0) {
                System.out.println("Wrong ID übergeben");
                return;
            }

            selectedAlbum = FukoDB.getInstance().getAlbumService().findByVGMdbID(id);
            if (selectedAlbum == null) {
                System.out.println("Couldn't find Album in DB - Update from VGMDB");
                DatabaseOperationResult result = FukoDB.getInstance().getAlbumService().
                        updateWithRelations(Album.getFromVGMDB(VGMdb.getAlbum(query)), true);
                selectedAlbum = (Album) result.getResult();
            }

            if (selectedAlbum == null) {
                return;
            }

            if (StringUtils.isEmpty(selectedAlbum.getFolderName())) {
                AlbumFileSystem.createCompleteAlbumStructure(selectedAlbum, true);
                selectedAlbum = FukoDB.getInstance().getAlbumService().findByVGMdbID(selectedAlbum.getVgmdbID());
            }

            System.out.println(selectedAlbum.getName());

            name.setValue(selectedAlbum.getName());
//            String names = "";
//            for(Name n : selectedAlbum.getNames()){
//                names = names.concat(n.getName()).concat(", ");
//            }
//            info.setValue("<p class=\"email-content-subtitle\">"+names+"</p>");

            Path albumPath = AlbumFileSystem.getAlbumDirectory(selectedAlbum, false);

            selectedAlbum.getPictures().stream().filter((picture) -> (picture.isCover())).forEach((picture) -> {
//                System.out.println("picture? ");
//                System.out.println("Albumpath: "+ albumPath.toAbsolutePath().toString());
//                System.out.println(TomoyaUtils.generatePicturebase64Encoded(albumPath, picture, ""));
                coverImage.setValue(TomoyaUtils.generatePicturebase64Encoded(albumPath, picture, ""));
            });

            imagesHTML = StringUtils.EMPTY;

            selectedAlbum.getPictures().stream().filter((picture) -> (!picture.isCover())).forEach((picture) -> {
                imagesHTML = imagesHTML.concat("<div class=\"pure-u-1-3\">"
                        + TomoyaUtils.generatePicturebase64Encoded(albumPath, picture, "") + "</div>");
            });
            images.setValue("<div class=\"pure-g\">" + imagesHTML + "</div>");

            tabSheet.removeAllComponents();
            tabSheet.addTab(buildMovieDetails(), "Details");

            description.setValue(StringUtils.trim(selectedAlbum.getDescription()));
            tabSheet.addTab(description, "Description");

            tabSheet.addTab(images, "Scans");

            relatedProducts.setValue(TomoyaUtils.generateHTML(selectedAlbum.getRepresentedProducts(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            relatedAlbums.setValue(TomoyaUtils.generateHTML(selectedAlbum.getRelatedAlbums(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            catalog.setValue(StringUtils.trim(selectedAlbum.getAlbumCatalog()));
            releaseDate.setValue(StringUtils.trim(selectedAlbum.getReleaseDate()));
            releaseFormat.setValue(StringUtils.trim(selectedAlbum.getReleaseFormat()));
            releasePrice.setValue(StringUtils.trim(selectedAlbum.getReleasePrice()) + StringUtils.trim(selectedAlbum.getReleaseCurrency()));
            releaseEvents.setValue(TomoyaUtils.generateHTML(selectedAlbum.getReleaseEvents(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            mediaFormat.setValue(StringUtils.trim(selectedAlbum.getMediaFormat()));
            classification.setValue(StringUtils.trim(selectedAlbum.getClassification()));
            publishedBy.setValue(TomoyaUtils.generateHTML(selectedAlbum.getPublisher(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            composedBy.setValue(TomoyaUtils.generateHTML(selectedAlbum.getComposers(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            arrangedBy.setValue(TomoyaUtils.generateHTML(selectedAlbum.getArrangers(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            performedBy.setValue(TomoyaUtils.generateHTML(selectedAlbum.getPerformers(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            lyricsBy.setValue(TomoyaUtils.generateHTML(selectedAlbum.getLyricists(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));

            generateTrackTabs();

        } catch (IllegalArgumentException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }
    }



    private void generateTrackTabs() {

        List<VGMdbNameLanguage> languages = new ArrayList<>();

        selectedAlbum.getDiscs().stream().forEach((disc) -> {
            disc.getTracks().stream().forEach((AlbumTrack track) -> {
                track.getTrackNames().stream().filter((Name trackname)
                        -> (!languages.contains(trackname.getNameLanguage()))).forEach((trackname) -> {
                            languages.add(trackname.getNameLanguage());
                        });
            });
        });
        languages.stream().forEach((lang) -> {
            tabSheet.addTab(createTrackLayout(lang), "TrackList - " + lang.getHumanizedName());
        });
    }

    private VerticalLayout createTrackLayout(VGMdbNameLanguage language) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");

        for (AlbumDisc disc : selectedAlbum.getDiscs()) {
            layout.addComponent(new Label("<h3>" + disc.getName() + "</h3>", ContentMode.HTML));

            Table trackTable = new Table();
            trackTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
            trackTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
            trackTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
            trackTable.addStyleName(ValoTheme.TABLE_SMALL);
            trackTable.setSortEnabled(false);
            trackTable.setWidth("100%");
            trackTable.setColumnAlignment("duration", Table.Align.RIGHT);
            trackTable.setRowHeaderMode(Table.RowHeaderMode.INDEX);
            trackTable.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
            trackTable.addContainerProperty("title", String.class, "");
            trackTable.addContainerProperty("duration", String.class, "");
            trackTable.addContainerProperty("position", Integer.class, 0);

            for (AlbumTrack t : disc.getTracks()) {
                trackTable.addItem(t);
                trackTable.getContainerProperty(t, "position").setValue(t.getTrackPosition());
                trackTable.getContainerProperty(t, "title").setValue(
                        t.getTrackNames().stream().filter(
                                x -> x.getNameLanguage() == language)
                        .collect(Collectors.toList()).get(0).getName());
                trackTable.getContainerProperty(t, "duration").setValue(t.getTrackLength());
            }
            trackTable.setVisibleColumns("title", "duration");
            trackTable.setColumnHeaders("Title", "Duration");
////            trackTable.setColumnExpandRatio("title", 2);
////            trackTable.setColumnExpandRatio("duration", 1);

            trackTable.setSortContainerPropertyId("position");
            trackTable.setSortAscending(true);
            trackTable.setPageLength(trackTable.getContainerDataSource().size());
            layout.addComponent(trackTable);
            layout.addComponent(new Label("Length " + disc.getDiscLengh()));
        }

        return layout;
    }

}
