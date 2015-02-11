/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.vlcplayer.playerComponents.mediaLibrary;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.jtp.vlcplayer.playerComponents.playlist.PlayList;
import org.jtp.vlcplayer.playerComponents.playlist.PlaylistItem;

/**
 *
 * @author Dub-Laptop
 */
public class LibraryEntry extends Parent {

    private ImageView imageView;
    private File path;
    private BorderPane bg;
    private Label name;
    private double imgWidth = 150;
    private double imgHeight = 85;
    private double addWidth = 25;
    private double addHeight = 25;
    private StackPane stack;
    private Image addToPlaylist;
    private ImageView addGraphic;
    private Button add;
    private PlayList playList;
    private boolean added = false;
    // on hover play 15 sec clip

    public LibraryEntry(final ImageView imageView, final File path) {
                
        this.bg = new BorderPane();
        
        this.path = path;

        this.imageView = imageView;
        this.imageView.setFitWidth(imgWidth);
        this.imageView.setFitHeight(imgHeight);

        this.stack = new StackPane();
                
        this.addToPlaylist = new Image(getClass().getResourceAsStream("list_add.png"));
        
        this.addGraphic = new ImageView();
        this.addGraphic.setFitWidth(addWidth);
        this.addGraphic.setFitHeight(addHeight);   
        this.addGraphic.setImage(addToPlaylist);
        
        this.add = new Button();
        this.add.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.add.setGraphic(addGraphic);
        this.add.setTranslateX(60);
        this.add.setTranslateY(-30);
        this.add.setStyle("-fx-background-color: transparent;");  
        
        this.add.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                //playList = MyVideoController.getPlayList();
                PlaylistItem item = new PlaylistItem(path,imageView.getImage());
                if(!added){
                    //playList.addPlayListItem(item);
                    added = true;
                }else if(added){
                    //playList.removePlayListItem(item);
                    added= false;
                }
            }
        });
        
        this.stack.getChildren().addAll(this.imageView, this.add);        
        
        this.name = new Label(this.path.getName());
        this.name.setMaxWidth(imgWidth);
        this.name.setTextFill(Color.WHITE);       

        this.bg.setCenter(this.stack);
        this.bg.setBottom(name);
        this.getChildren().add(bg);
        
    }
    public File getPath() {
        return path;
    }
}
