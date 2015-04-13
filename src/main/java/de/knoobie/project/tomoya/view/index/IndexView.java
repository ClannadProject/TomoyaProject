package de.knoobie.project.tomoya.view.index;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import de.knoobie.project.tomoya.utils.TomoyaView;

/**
 *
 * @author cKnoobie
 */
public class IndexView extends TomoyaView implements View {

    public static final String VIEW_NAME = "index";
    
//    private Label name;
//    private Label cover;

    public IndexView() {
        super("data");
    }

    @Override
    public void init() {
        addComponent(new Label("<a href=\"!#!album/50563\">Album: 50563</a>", ContentMode.HTML), "header.name");
//        addComponent(new Label("<a href=\"!#!album/50564\">Album: 50564</a>", ContentMode.HTML), "");
        addComponent(new Label("<a href=\"!#!album/49046\">Album: 49046</a><a href=\"!#!album/18076\">Album: 18076</a>", ContentMode.HTML), "");
    }

    @Override
    public void select(String query) {
//        try {
//            if (StringUtils.isEmpty(query)) {
//                System.out.println("Empty und so!");
//                return;
//            }
//            int id = StringUtils.getInt(query, 0, true);
//
//            if (id == 0) {
//                System.out.println("Wrong ID übergeben");
//                return;
//            }
//
//            Album album = FukoDB.getInstance().getAlbumService().findByVGMdbID(id);
//            if (album == null) {
//                System.out.println("Couldn't find Album in DB - Update from VGMDB");
//                DatabaseOperationResult result = FukoDB.getInstance().getAlbumService().
//                        updateWithRelations(Album.getFromVGMDB(VGMdb.getAlbum(query)), true);
//                album = (Album) result.getResult();
//            }
//
//            System.out.println(album.getName());
//            
//            name.setValue("<h2>"+album.getName()+"</h2>");
//            
//            for(Picture picture : album.getPictures()){
//                if(picture.isCover()){
//                    cover.setValue("<img src=\""+picture.getUrlSmall()+"\"/>");
//                }
//            }
//            
//
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(IndexView.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (JsonSyntaxException ex) {
//            Logger.getLogger(IndexView.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexView.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
