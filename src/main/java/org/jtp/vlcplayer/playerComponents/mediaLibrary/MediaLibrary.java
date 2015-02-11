/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.vlcplayer.playerComponents.mediaLibrary;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

/**
 *
 * @author Dub-Laptop
 */
public class MediaLibrary implements Initializable {

    @FXML private TilePane tilePane;
    
    private String mrl;
    private ObservableList<ImageView> images = FXCollections.observableArrayList();
    private List<File> fileList = new ArrayList<>();
    private Map<String, ImageView> map;
    private List<LibraryEntry> libEntries = FXCollections.observableArrayList();
    static int totalFiles = 0;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        
        //DirectoryChooser dc = new DirectoryChooser();
        //File f = dc.showDialog(null);
        
        LibraryLoader.generateMissingThumbs();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(MediaLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO                  
        
        //tilePane.setPrefColumns(4);
        tilePane.setHgap(5);
        tilePane.setVgap(15);
        tilePane.setPrefTileHeight(85);
        tilePane.setPrefTileWidth(150);
        tilePane.setPadding(new Insets(5, 5, 15, 5));
        
        map = LibraryLoader.getMappedItems();
        for(String s : map.keySet()){
            LibraryEntry e = new LibraryEntry(map.get(s), new File(s));
            libEntries.add(e);            
        }
        tilePane.getChildren().addAll(libEntries);
        
    }

    private void refresh() {
        tilePane.getChildren().clear();
        map = LibraryLoader.getMappedItems();
        for(String s : map.keySet()){
            LibraryEntry e = new LibraryEntry(map.get(s), new File(s));
            tilePane.getChildren().add(e);
        }
    }

    public List<LibraryEntry> getLibEntries() {
        return libEntries;
    }
    
    
}//====================================EOF======================================
