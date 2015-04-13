package de.knoobie.project.tomoya.view.product;

import de.knoobie.project.tomoya.view.artist.*;
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
import de.knoobie.project.fuko.database.domain.Product;
import de.knoobie.project.fuko.database.domain.embeddable.AlbumLink;
import de.knoobie.project.fuko.database.service.FukoDB;
import de.knoobie.project.nagisa.gson.util.VGMdb;
import de.knoobie.project.ryou.filesystem.utils.ArtistFileSystem;
import de.knoobie.project.ryou.filesystem.utils.ProductFileSystem;
import de.knoobie.project.tomoya.utils.TomoyaUtils;
import de.knoobie.project.tomoya.utils.TomoyaView;
import de.knoobie.project.tomoya.utils.VaadinUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductView extends TomoyaView implements View {

    public static final String VIEW_NAME = "product";

    private CssLayout functions;
    private Label name, info;
    private Label coverImage, images, description;
    private String imagesHTML;

    private TabSheet tabSheet;
    private Product selectedProduct;

    public ProductView() {
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
                FileUtils.openFileBrowser(ProductFileSystem.getProductDirectory(selectedProduct, false));
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
//        coverImage.setWidth("250px");
//        coverImage.setHeight("250px");
        addComponent(coverImage, "content.image");

        images = new Label("", ContentMode.HTML);
        description = new Label("", ContentMode.PREFORMATTED);

    }

    private Component buildProductInformation() {

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

    private Label aliases, organizations, releaseDate, productType, titles, franchises, releases, websites;

    private Component buildDetailsForm() {
        FormLayout fields = new FormLayout();
        fields.setSizeFull();
        fields.setSpacing(false);
        fields.setMargin(false);

        fields.addComponent(aliases = VaadinUtils.generateLinkLabel("Aliases"));
        fields.addComponent(organizations = VaadinUtils.generateLinkLabel("Organizations"));
        fields.addComponent(productType = VaadinUtils.generateLinkLabel("ProductType"));
        fields.addComponent(releaseDate = VaadinUtils.generateLinkLabel("ReleaseDate"));
        fields.addComponent(titles = VaadinUtils.generateLinkLabel("Titles"));
        fields.addComponent(releases = VaadinUtils.generateLinkLabel("Releases"));
        fields.addComponent(franchises = VaadinUtils.generateLinkLabel("Franchises"));
        fields.addComponent(websites = VaadinUtils.generateLinkLabel("Websites"));

        return fields;
    }

    @Override
    public void select(String query) {
        try {

            selectedProduct = null;

            if (StringUtils.isEmpty(query)) {
                System.out.println("Empty und so!");
                return;
            }
            int id = StringUtils.getInt(query, 0, true);

            if (id == 0) {
                System.out.println("Wrong ID übergeben");
                return;
            }

            selectedProduct = FukoDB.getInstance().getProductService().findByVGMdbID(id);
            if (selectedProduct == null) {
                System.out.println("Couldn't find Product in DB - Update from VGMDB");
                DatabaseOperationResult result = FukoDB.getInstance().getProductService().
                        updateWithRelations(Product.getFromVGMDB(VGMdb.getProduct(query)), true);
                selectedProduct = (Product) result.getResult();
            }

            if (selectedProduct == null) {
                return;
            }

            if (StringUtils.isEmpty(selectedProduct.getFolderName())) {
                ProductFileSystem.createCompleteProductStructure(selectedProduct, true);
                selectedProduct = FukoDB.getInstance().getProductService().findByVGMdbID(selectedProduct.getVgmdbID());
            }

            System.out.println(selectedProduct.getName());

            name.setValue(selectedProduct.getName());

            Path productPath = ProductFileSystem.getProductDirectory(selectedProduct, false);

            if (selectedProduct.getPicture() != null && !StringUtils.isEmpty(selectedProduct.getPicture().getPictureLocation())) {
                coverImage.setValue(TomoyaUtils.generatePicturebase64Encoded(productPath, selectedProduct.getPicture(), ""));
            }
            tabSheet.removeAllComponents();
            tabSheet.addTab(buildProductInformation(), "Details");

            description.setValue(StringUtils.trim(selectedProduct.getDescription()));
            tabSheet.addTab(description, "Description");

            String names = "";
            for (Name n : selectedProduct.getNames()) {
                names = names.concat(n.getName()).concat(", ");
            }
            aliases.setValue(names);
            releases.setValue(TomoyaUtils.generateHTML(selectedProduct.getReleases(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            franchises.setValue(TomoyaUtils.generateHTML(selectedProduct.getFranchises(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            websites.setValue(TomoyaUtils.generateHTML(selectedProduct.getWebsites(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            titles.setValue(TomoyaUtils.generateHTML(selectedProduct.getTitles(), TomoyaUtils.DEFAULT_PATH_TO_ROOT));

            organizations.setValue(StringUtils.trim(selectedProduct.getOrganizations()));
            releaseDate.setValue(StringUtils.trim(selectedProduct.getReleaseDate()));
            productType.setValue(StringUtils.trim(selectedProduct.getProductType() == null ? ""
                    : selectedProduct.getProductType().getHumanizedName()));

            generateTrackTabs();

        } catch (IllegalArgumentException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void generateTrackTabs() {
        tabSheet.addTab(createAlbumLinkLayout("Related Albums", selectedProduct.getRelatedAlbums()), "Related Albums");
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
        trackTable.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        trackTable.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);

        for (AlbumLink album : ablums) {
            trackTable.addItem(album);
            trackTable.getContainerProperty(album, "releaseDate").setValue(StringUtils.trim(album.getReleaseDate()));
            Label albumName = VaadinUtils.generateLinkLabel(null);
            albumName.setValue(TomoyaUtils.generateHTML(new ArrayList<>(
                    Arrays.asList(album)), TomoyaUtils.DEFAULT_PATH_TO_ROOT));
            trackTable.getContainerProperty(album, "name").setValue(albumName);
            trackTable.getContainerProperty(album, "catalog").setValue(StringUtils.trim(album.getAlbumCatalog()));
        }
        trackTable.setPageLength(trackTable.getContainerDataSource().size());
        layout.addComponent(trackTable);

        return layout;
    }

}
