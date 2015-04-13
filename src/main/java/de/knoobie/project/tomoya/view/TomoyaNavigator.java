package de.knoobie.project.tomoya.view;

import com.google.gson.JsonSyntaxException;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import de.knoobie.project.clannadutils.common.StringUtils;
import de.knoobie.project.fuko.database.bo.DatabaseOperationResult;
import de.knoobie.project.fuko.database.domain.Album;
import de.knoobie.project.fuko.database.domain.Artist;
import de.knoobie.project.fuko.database.domain.Product;
import de.knoobie.project.fuko.database.service.FukoDB;
import de.knoobie.project.nagisa.gson.model.bo.VGMdbAlbum;
import de.knoobie.project.nagisa.gson.model.bo.VGMdbArtist;
import de.knoobie.project.nagisa.gson.model.bo.VGMdbProduct;
import de.knoobie.project.nagisa.gson.model.bo.VGMdbSearch;
import de.knoobie.project.nagisa.gson.util.TestVGMdb;
import de.knoobie.project.nagisa.gson.util.VGMdb;
import de.knoobie.project.tomoya.TomoyaUI;
import de.knoobie.project.tomoya.utils.TomoyaView;
import de.knoobie.project.tomoya.utils.VaadinUtils;
import de.knoobie.project.tomoya.view.album.AlbumView;
import de.knoobie.project.tomoya.view.artist.ArtistView;
import de.knoobie.project.tomoya.view.index.IndexView;
import de.knoobie.project.tomoya.view.product.ProductView;
import de.knoobie.project.tomoya.view.search.SearchResultAlbum;
import de.knoobie.project.tomoya.view.search.SearchResultArtist;
import de.knoobie.project.tomoya.view.search.SearchResultProduct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TomoyaNavigator extends TomoyaView {

    private Navigator navigator;
    private Map<String, Button> menuButtons;
    private List<SearchResultAlbum> searchResults;
    private VerticalLayout viewContainer;
    private CssLayout menu, searchResultContainer;
    private TextField searchField;

    public TomoyaNavigator(TomoyaUI ui) {
        super("navigator");
    }

    @Override
    public void init() {
        menuButtons = new HashMap<>();
        navigator = new Navigator(TomoyaUI.get(), viewContainer = new VerticalLayout());
        navigator.setErrorView(ErrorView.class);

        searchField = new TextField();
        searchField.setWidth("100%");
        searchField.setImmediate(true);

        addComponent(searchField, "sidebar.search.textfield");
        Button search = VaadinUtils.create("Suchen", VaadinUtils.ButtonStyle.SUCCESS);
        search.addClickListener((Button.ClickEvent event) -> {
            search();
        });
        addComponent(search, "sidebar.search.button");

        addComponent(menu = new CssLayout(), "sidebar.menu");
        addComponent(searchResultContainer = new CssLayout(), "search.result");

        addComponent(viewContainer, "page");

        addView(IndexView.class, IndexView.VIEW_NAME,
                IndexView.VIEW_NAME, FontAwesome.GLOBE);

        addView(AlbumView.class, AlbumView.VIEW_NAME,
                AlbumView.VIEW_NAME, FontAwesome.BEER);

        addView(ArtistView.class, ArtistView.VIEW_NAME,
                ArtistView.VIEW_NAME, FontAwesome.BEER);

        addView(ProductView.class, ProductView.VIEW_NAME,
                ProductView.VIEW_NAME, FontAwesome.BEER);

        navigator.navigateTo(IndexView.VIEW_NAME);

//        List<Album> albums = FukoDB.getInstance().findAll(Album.class);
//        albums.stream().forEach((album) -> {
//            searchResultContainer.addComponent(new SearchResultAlbum(navigator, album));
//        });
//        List<Artist> artists = FukoDB.getInstance().findAll(Artist.class);
//        artists.stream().forEach((artist) -> {
//            searchResultContainer.addComponent(new SearchResultArtist(navigator, artist));
//        });
    }

    private void search() {
        if (StringUtils.isEmpty(searchField.getValue())) {
            searchResultContainer.removeAllComponents();
            return;
        }

        try {
            VGMdbSearch searchResult = TestVGMdb.search(searchField.getValue());
            searchResultContainer.removeAllComponents();

            for (VGMdbAlbum album : searchResult.getAlbums()) {
                int id = Album.getFromVGMDB(album).getVgmdbID();
                Album selectedAlbum = FukoDB.getInstance().getAlbumService().findByVGMdbID(id);
                if (selectedAlbum == null) {
                    System.out.println("Couldn't find Album in DB - Update from VGMDB");
                    DatabaseOperationResult result = FukoDB.getInstance().getAlbumService().
                            updateWithRelations(Album.getFromVGMDB(VGMdb.getAlbum("" + id)), true);
                    selectedAlbum = (Album) result.getResult();
                }
                searchResultContainer.addComponent(new SearchResultAlbum(navigator, selectedAlbum));
            }

            for (VGMdbArtist artist : searchResult.getArtists()) {
                int id = Artist.getFromVGMDB(artist).getVgmdbID();
                Artist selectedArtist = FukoDB.getInstance().getArtistService().findByVGMdbID(id);
                if (selectedArtist == null) {
                    System.out.println("Couldn't find Artist in DB - Update from VGMDB");
                    DatabaseOperationResult result = FukoDB.getInstance().getArtistService().
                            updateWithRelations(Artist.getFromVGMDB(VGMdb.getArtist("" + id)), true);
                    selectedArtist = (Artist) result.getResult();
                }
                searchResultContainer.addComponent(new SearchResultArtist(navigator, selectedArtist));
            }

            for (VGMdbProduct product : searchResult.getProducts()) {
                int id = Product.getFromVGMDB(product).getVgmdbID();
                Product selectedProduct = FukoDB.getInstance().getProductService().findByVGMdbID(id);
                if (selectedProduct == null) {
                    System.out.println("Couldn't find Product in DB - Update from VGMDB");
                    DatabaseOperationResult result = FukoDB.getInstance().getProductService().
                            updateWithRelations(Product.getFromVGMDB(VGMdb.getProduct("" + id)), true);
                    selectedProduct = (Product) result.getResult();
                }
                searchResultContainer.addComponent(new SearchResultProduct(navigator, selectedProduct));
            }

        } catch (IllegalArgumentException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addView(Class<? extends TomoyaView> viewClass, final String name,
            String caption, Resource icon) {
        navigator.addView(name, viewClass);
        createViewButton(name, caption, icon);
    }

    private void createViewButton(final String name, String caption,
            Resource icon) {
        Button b = VaadinUtils.create(caption, VaadinUtils.ButtonStyle.PRIMARY);
        b.addClickListener((Button.ClickEvent event) -> {
            navigator.navigateTo(name);
        });
        b.setIcon(icon);
        menu.addComponent(b);
        menuButtons.put(name, b);
    }

    @Override
    public void select(String query) {

    }
}
