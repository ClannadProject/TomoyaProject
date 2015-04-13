package de.knoobie.project.tomoya.view.search;

import com.vaadin.navigator.Navigator;
import de.knoobie.project.clannadutils.common.StringUtils;
import de.knoobie.project.fuko.database.domain.Album;
import de.knoobie.project.fuko.database.domain.Product;
import de.knoobie.project.ryou.filesystem.utils.AlbumFileSystem;
import de.knoobie.project.ryou.filesystem.utils.ProductFileSystem;
import de.knoobie.project.tomoya.utils.SearchResultView;
import de.knoobie.project.tomoya.utils.TomoyaUtils;
import java.io.IOException;
import java.nio.file.Path;

public class SearchResultProduct extends SearchResultView<Product> {

    private String picturePath;

    public SearchResultProduct(Navigator navigator, Product result) {
        super(navigator, result, "product");
    }

    @Override
    public String getBase64EncodedImageString(Product result) {
        try {
            Path productPath = ProductFileSystem.getProductDirectory(result, false);

            if (result.getPicture() != null && !StringUtils.isEmpty(result.getPicture().getPictureLocation())) {
                picturePath = TomoyaUtils.generatePicturebase64Encoded(productPath, result.getPicture(), "width=\"64px;\" height=\"64px;\"");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return picturePath;
    }

}
