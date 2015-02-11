/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.vlcplayer.playerComponents;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jtp.vlcplayer.MediaInterface;
import org.jtp.vlcplayer.playerComponents.mediaLibrary.MediaLibrary;
import org.jtp.vlcplayer.playerComponents.playlist.PlayList;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 *
 * @author Dub-Laptop
 */
public class MediaBar implements Initializable, MediaInterface{
    

    //==========================================================================
    private VideoPlayer vidPlayer = new VideoPlayer();
    private MediaPlayer player;
    private PlayList playList;
    private MediaLibrary mediaLibrary;
    
    @FXML private Button playButton;
    @FXML private Button backButton;
    @FXML private Button fwdButton;
    @FXML private Button stopButton;
    @FXML private Button ejectButton;
    @FXML private Button playListButton;
    @FXML private Button eqButton;
    @FXML private Button mediaLibraryButton;
    @FXML private ImageView volImage;
    @FXML private Label tileLabel;
    @FXML private Label titleLabel;
    @FXML private Slider timeSlider;
    @FXML private Slider volSlider;
    
    
    
    private Image play = new Image(getClass().getResourceAsStream("MediaIcons/media_playback_start.png")); 
    private Image pause = new Image(getClass().getResourceAsStream("MediaIcons/media_playback_pause.png"));
    private Image volLow = new Image(getClass().getResourceAsStream("MediaIcons/audio_volume_low_newschool.png")); 
    private Image volHigh = new Image(getClass().getResourceAsStream("MediaIcons/audio_volume_high_newschool.png"));
    private Image volMute = new Image(getClass().getResourceAsStream("MediaIcons/audio_volume_muted_newschool.png"));
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        
        addListeners();
        //=====================MediaPlayer======================================
        playListButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                
                if(!playList.isVisible()){
                    playList.setVisible(true);
                }else{
                    playList.setVisible(false);
                }
            }
        });

        //======================================================================
    }
    
    public void addListeners(){
        volSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                if(volSlider.getValue() == 0){
                    volImage.setImage(volMute);
                }else if(volSlider.getValue() > 0 && volSlider.getValue() < 0.70){
                    volImage.setImage(volLow);
                }else if(volSlider.getValue() > 0.70){
                    volImage.setImage(volHigh);
                }
            }
        });
        
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return this.player;
    }

    
}
