package de.knoobie.project.tomoya.view.search;

import com.vaadin.navigator.Navigator;
import de.knoobie.project.clannadutils.common.StringUtils;
import de.knoobie.project.fuko.database.domain.Artist;
import de.knoobie.project.ryou.filesystem.utils.ArtistFileSystem;
import de.knoobie.project.tomoya.utils.SearchResultView;
import de.knoobie.project.tomoya.utils.TomoyaUtils;
import java.io.IOException;
import java.nio.file.Path;

public class SearchResultArtist extends SearchResultView<Artist> {

    private String picturePath;

    public SearchResultArtist(Navigator navigator, Artist result) {
        super(navigator, result, "artist");
    }

    @Override
    public String getBase64EncodedImageString(Artist result) {
        try {
            Path albumPath = ArtistFileSystem.getArtistDirectory(result, false);

            if (result.getPicture() != null && !StringUtils.isEmpty(result.getPicture().getPictureLocation())) {
                picturePath = TomoyaUtils.generatePicturebase64Encoded(albumPath, result.getPicture(), "width=\"64px;\" height=\"64px;\"");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return picturePath;
    }

}
