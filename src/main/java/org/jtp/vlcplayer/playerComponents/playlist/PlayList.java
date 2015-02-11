/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.vlcplayer.playerComponents.playlist;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import org.jtp.vlcplayer.MyVideoController;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * FXML Controller class
 *
 * @author Dub-Laptop
 */
public class PlayList extends AnchorPane implements Initializable {

    public PlayList() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayList.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(PlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML private ListView<PlaylistItem> listView;
    private ObservableList<PlaylistItem> listItems = FXCollections.observableArrayList();
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
        listItems.addListener(new ListChangeListener<PlaylistItem>() {

            @Override
            public void onChanged(Change<? extends PlaylistItem> change) {
                while(change.next()){
                    if(change.wasAdded()){
                        listView.setItems(listItems);
                    }else if(change.wasRemoved()){
                        listView.setItems(listItems);
                    }
                }
            }
        });
        listView.setItems(listItems);
            MenuItem play = new MenuItem();
            play.setText("Play");
            play.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    MediaPlayer player = MyVideoController.getPlayer();
                    if(player != null){
                        //player.playMedia(listView.getSelectionModel().getSelectedItem().getFile().getAbsolutePath());
                    }
                }
            });
            MenuItem remove = new MenuItem("Remove from List");
            remove.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {                    
                    //listItems.remove(listView.getSelectionModel().getSelectedItem());
                }
            });
            ContextMenu menu = new ContextMenu();
            menu.getItems().addAll(play,remove);
        listView.setContextMenu(menu);
        
        
    }    
    
    public void addPlayListItem(PlaylistItem item){
        listItems.add(item);
    }
    public void removePlayListItem(PlaylistItem item){
        listItems.removeAll(item);        
    }

    public ObservableList<PlaylistItem> getListItems() {
        return listItems;
    }
    
    
}
