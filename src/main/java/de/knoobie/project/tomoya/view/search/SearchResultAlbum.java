package de.knoobie.project.tomoya.view.search;

import com.vaadin.navigator.Navigator;
import de.knoobie.project.fuko.database.domain.Album;
import de.knoobie.project.ryou.filesystem.utils.AlbumFileSystem;
import de.knoobie.project.tomoya.utils.SearchResultView;
import de.knoobie.project.tomoya.utils.TomoyaUtils;
import java.io.IOException;
import java.nio.file.Path;

public class SearchResultAlbum extends SearchResultView<Album>{

    private String picturePath;
    
    public SearchResultAlbum(Navigator navigator, Album result){
        super(navigator, result, "album");
    }

    @Override
    public String getBase64EncodedImageString(Album result) {
        try {
            Path albumPath = AlbumFileSystem.getAlbumDirectory(result, false);
            
            result.getPictures().stream().filter((picture) -> (picture.isCover())).forEach((picture) -> {
                picturePath = TomoyaUtils.generatePicturebase64Encoded(albumPath, picture, "width=\"64px;\" height=\"64px;\"");
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return picturePath;
    }
    
}
