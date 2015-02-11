/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.vlcplayer.playerComponents.playlist;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Dub-Laptop
 */
public class PlaylistItem extends AnchorPane implements Initializable {

    
    @FXML private Label titleLabel;
    @FXML private Label timeLabel;
    @FXML private Tooltip titleToolTip;
    @FXML private ImageView view;
    
    private File file;
    private Image image;
        
    public PlaylistItem(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlaylistItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(PlaylistItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public PlaylistItem(File file, Image image){
        this();
        this.file= file;
        this.image = image;
        this.view.setImage(this.image);
        
        
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public File getFile() {
        return file;
    }
    
    private String timeFormat(long time){
        
        if(time >= 3600000){
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time),
            TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
            TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        }else {
            return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
            TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        }
    }
}

