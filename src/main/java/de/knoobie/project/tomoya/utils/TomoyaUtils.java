/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.knoobie.project.tomoya.utils;

import de.knoobie.project.clannadutils.common.FileUtils;
import de.knoobie.project.clannadutils.common.ListUtils;
import de.knoobie.project.clannadutils.common.StringUtils;
import de.knoobie.project.fuko.database.domain.embeddable.Link;
import de.knoobie.project.fuko.database.domain.embeddable.Picture;
import de.knoobie.project.ryou.filesystem.domain.RyouPath;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author cKnoobie
 */
public class TomoyaUtils {

    public static String DEFAULT_PATH_TO_ROOT = "#!";

    public static String generateHTML(List<? extends Link> links, String urlPathToRoot) {
        String htmlContent = StringUtils.EMPTY;

        if (ListUtils.isEmpty(links)) {
            return htmlContent;
        }

        for (Link link : links) {
            if (StringUtils.isEmpty(link.getLink())) {
                htmlContent = htmlContent.concat(StringUtils.trim(link.getPrimaryName().getName())).concat(", ");
            } else {
                htmlContent = htmlContent.concat("<a href=\"" + urlPathToRoot + StringUtils.trim(link.getLink()) + "\">").
                        concat(StringUtils.trim(link.getPrimaryName().getName())).concat("</a>, ");
            }
        }

        return StringUtils.removeLastCharacters(htmlContent, 2);
    }

    public static String generatePicturebase64Encoded(Path path, Picture picture, String sizeValues) {

        RyouPath coverPath = null;
        String encodedBytes = "";
        if (!StringUtils.isEmpty(picture.getPictureLocation())) {
            if (picture.isCover()) {
                coverPath = RyouPath.create(path.toAbsolutePath().toString()
                        + FileUtils.getFileSystem().getSeparator(), picture.getPictureLocation());
            } else {
                coverPath = RyouPath.create(path.toAbsolutePath().toString()
                        + FileUtils.getFileSystem().getSeparator(), "Scans", picture.getPictureLocation());
            }
            try {
                encodedBytes = Base64.getEncoder().encodeToString(Files.readAllBytes(coverPath.getPath()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (picture.isCover()) {
            return "<img class=\"pure-img\" " + sizeValues + " src=\"data:image/jpeg;base64," + encodedBytes + "\"/>";
        } else {
            return "<img class=\"pure-img\" " + sizeValues + " src=\"data:image/jpeg;base64," + encodedBytes + "\">" + picture.getPictureLocation() + "</img>";
        }

    }
}
