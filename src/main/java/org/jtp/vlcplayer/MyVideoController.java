package org.jtp.vlcplayer;

import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ParallelTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Dub-Laptop
 */
public class MyVideoController extends AnchorPane implements Initializable {

    public MyVideoController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MyVideoPlayer.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MyVideoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private Stage stage;
    @FXML
    private Button playButton;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button ejectButton;
    @FXML
    private Button eqButton;
    @FXML
    private Button playListButton;
    @FXML
    private Button libraryButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button fullScreenButton;
    @FXML
    private ImageView videoCanvas;
    @FXML
    private ImageView volImage;
    @FXML
    private Slider timeSlider;
    @FXML
    private Slider volSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private AnchorPane controlPanel;
    @FXML
    private AnchorPane topControls;
    @FXML
    private Pane resizePane;

    private SimpleStringProperty timeProperty;

    private final Image clearPlaylist = new Image(getClass().getResourceAsStream("/MediaIcons/media_playlist_clear.png"));
    private final Image refreshPlaylist = new Image(getClass().getResourceAsStream("/MediaIcons/media_playlist_refresh.png"));
    private final Image playPic = new Image(getClass().getResourceAsStream("/MediaIcons/media_playback_start.png"));
    private final Image pausePic = new Image(getClass().getResourceAsStream("/MediaIcons/media_playback_pause.png"));
    private final Image volLow = new Image(getClass().getResourceAsStream("/MediaIcons/audio_volume_low_newschool.png"));
    private final Image volHigh = new Image(getClass().getResourceAsStream("/MediaIcons/audio_volume_high_newschool.png"));
    private final Image volMute = new Image(getClass().getResourceAsStream("/MediaIcons/audio_volume_muted_newschool.png"));
    private ImageView play;

    private ParallelTransition transition;
    private FadeTransition fade = null;

    private WritableImage vidImage;
    private PixelWriter pixelWriter;
    private WritablePixelFormat<ByteBuffer> byteBgraInstance;
    private double imgWidth;
    private double imgHeight;

    private DirectMediaPlayerComponent mediaComponent;
    private static MediaPlayer player;
    private long mediaLength;
    private boolean isPaused;

    private static int index = 0;

    private double mouX;
    private double mouY;
    private double dragOffsetX;
    private double stageMinWidth = 600;
    private double stageMinHeight = stageMinWidth / 1.78;

    private AnimationTimer timer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgWidth = videoCanvas.getFitWidth();
        imgHeight = imgWidth / 1.78;

        initStage();
        initSliders();
        initLabels();
        initResize();
        initButtonFunctions();
        initMediaComponents();
        initTimer();
    }

    private void initTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(mediaComponent.getMediaPlayer().isPlaying()){
                    renderFrame();
                }
            }
        };
        timer.start();
    }

    
    protected void startTimer() {
        timer.start();
    }

    
    protected void stopTimer() {
        timer.stop();
        mediaComponent.getMediaPlayer().stop();
        mediaComponent.getMediaPlayer().release();
    }

    public void initStage() {
        this.stage = MainApp.getStage();

        controlPanel.setLayoutX(controlCenter());
        stage.widthProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                controlPanel.setLayoutX(controlCenter());
            }
        });
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {

                videoCanvas.setFitHeight(t1.doubleValue() / 1.78);
            }
        });
        videoCanvas.fitWidthProperty().bind(stage.widthProperty());

        vidImage = new WritableImage((int) imgWidth, (int) imgHeight);
        videoCanvas.setImage(vidImage);

        addWindowDrag(topControls);

    }

    private double controlCenter() {
        double center = (stage.getWidth() / 2) - (controlPanel.getWidth() / 2);
        return center;
    }

    private void initButtonFunctions() {
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (transition != null) {
                    transition.stop();
                }
                transition = ParallelTransitionBuilder.create()
                        .children(
                                FadeTransitionBuilder.create()
                                .node(topControls)
                                .toValue(1.0)
                                .duration(Duration.millis(200))
                                .interpolator(Interpolator.EASE_OUT)
                                .build(),
                                FadeTransitionBuilder.create()
                                .node(controlPanel)
                                .toValue(1.0)
                                .duration(Duration.millis(200))
                                .interpolator(Interpolator.EASE_OUT)
                                .build())
                        .build();
                transition.play();
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                if (stage.isFullScreen() && controlPanel.getOpacity() == 0) {
                    if (transition != null) {
                        transition.stop();
                    }
                    setCursor(Cursor.DEFAULT);
                    transition = ParallelTransitionBuilder.create()
                            .children(
                                    FadeTransitionBuilder.create()
                                    .node(topControls)
                                    .toValue(1.0)
                                    .duration(Duration.millis(200))
                                    .interpolator(Interpolator.EASE_OUT)
                                    .build(),
                                    FadeTransitionBuilder.create()
                                    .node(controlPanel)
                                    .toValue(1.0)
                                    .duration(Duration.millis(200))
                                    .interpolator(Interpolator.EASE_OUT)
                                    .build())
                            .build();
                    transition.play();
                } else if (stage.isFullScreen() && controlPanel.getOpacity() > 0) {
                    setCursor(Cursor.DISAPPEAR);
                    transition = ParallelTransitionBuilder.create()
                            .children(
                                    FadeTransitionBuilder.create()
                                    .node(topControls)
                                    .toValue(0.0)
                                    .duration(Duration.millis(800))
                                    .interpolator(Interpolator.EASE_OUT)
                                    .build(),
                                    FadeTransitionBuilder.create()
                                    .node(controlPanel)
                                    .toValue(0.0)
                                    .duration(Duration.millis(800))
                                    .interpolator(Interpolator.EASE_OUT)
                                    .build())
                            .build();
                    transition.play();
                }

            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (transition != null) {
                    transition.stop();
                }
                transition = ParallelTransitionBuilder.create()
                        .children(
                                FadeTransitionBuilder.create()
                                .node(topControls)
                                .toValue(0.0)
                                .duration(Duration.millis(800))
                                .interpolator(Interpolator.EASE_OUT)
                                .build(),
                                FadeTransitionBuilder.create()
                                .node(controlPanel)
                                .toValue(0.0)
                                .duration(Duration.millis(800))
                                .interpolator(Interpolator.EASE_OUT)
                                .build())
                        .build();
                transition.play();
            }
        });
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (player != null) {
                    player.release();
                }
                Platform.exit();
            }
        });
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.play();
                }
            }
        });
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            }
        });
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (player != null) {
                    player.stop();
                }
            }
        });
        ejectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ExtensionFilter filter = new ExtensionFilter("Video", "*.mp4", "*.avi", "*.mkv");
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(filter);

                File f = fc.showOpenDialog(null);
                if (player != null) {
                    player.stop();
                }
                player.startMedia(f.getAbsolutePath());

                titleLabel.setText(player.getMediaMeta().getTitle());
                mediaLength = player.getLength();
                timeSlider.setMax((double) mediaLength);
                volSlider.setValue((double) player.getVolume());
            }
        });
        /* eqButton.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent t) {
                
         }
         });*/
        libraryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                createMediaLibrary();
            }
        });
        fullScreenButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                } else {
                    stage.setFullScreen(true);
                }
            }
        });
    }

    public void initSliders() {
        volSlider.valueProperty().addListener(new VolumeListener());
        volSlider.valueProperty().addListener(new VolumeChangeListener());

        timeSlider.valueProperty().addListener(new TimeChangeListener());
    }

    public void initLabels() {
        timeProperty = new SimpleStringProperty();
        timeProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                timeLabel.setText(t1);
            }
        });
    }

    private String timeFormat(long time) {
        long t = mediaLength - time;
        if (t >= 3600000) {
            return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(t),
                    TimeUnit.MILLISECONDS.toMinutes(t) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(t)),
                    TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(t)));
        } else {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(t) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(t)),
                    TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(t)));
        }
    }

    private void updateComponents(final Long currentTime) {
        //updater.start();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                timeSlider.setValue(currentTime);
                timeProperty.set(timeFormat((long) currentTime));
            }
        });

    }

    public void initMediaComponents() {
        //play = (ImageView) playButton.getGraphic();

        NativeLibrary.addSearchPath("libvlc", "C:/Program Files (x86)/VideoLan/VLC/");

        pixelWriter = vidImage.getPixelWriter();
        byteBgraInstance = PixelFormat.getByteBgraPreInstance();
        mediaComponent = new DirectMediaPlayerComponent(new BufferFormatCallback() {

            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                return new RV32BufferFormat((int) imgWidth, (int) imgHeight);
            }
        }) {//"RV32", (int) imgWidth, (int) imgHeight, (int) imgWidth * 4) {

        };
        player = mediaComponent.getMediaPlayer();
        player.setVolume(50);
        player.addMediaPlayerEventListener(new MediaPlayerEventListener() {

            @Override
            public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void opening(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void buffering(MediaPlayer mediaPlayer, float newCache) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                if (isPaused) {
                    isPaused = false;
                }
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        //play.setImage(pausePic);
                        playButton.setGraphic(play);
                    }
                });
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                isPaused = true;
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        //play.setImage(playPic);
                        //playButton.setGraphic(play);
                    }
                });
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void forward(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void backward(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {

            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                updateComponents(newTime);
            }

            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void pausableChanged(MediaPlayer mediaPlayer, int newSeekable) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mediaFreed(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void newMedia(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void endOfSubItems(MediaPlayer mediaPlayer) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
            }

            @Override
            public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
            }

            @Override
            public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
            }

            @Override
            public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
            }

            @Override
            public void mediaSubItemTreeAdded(MediaPlayer mp, libvlc_media_t l) {
            }
        });
    }

    protected final void renderFrame() {
        Memory[] nativeBuffers = mediaComponent.getMediaPlayer().lock();
        if (nativeBuffers != null) {
// FIXME there may be more efficient ways to do this...
// Since this is now being called by a specific rendering time, independent of the native video callbacks being
// invoked, some more defensive conditional checks are needed
            Memory nativeBuffer = nativeBuffers[0];
            if (nativeBuffer != null) {
                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaComponent.getMediaPlayer()).getBufferFormat();
                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), byteBgraInstance, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
        }
        mediaComponent.getMediaPlayer().unlock();
    }

    public void initResize() {
        resizePane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                resizePane.setCursor(Cursor.H_RESIZE);
                t.consume();
            }
        });
        resizePane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                resizePane.setCursor(Cursor.DEFAULT);
                t.consume();
            }
        });
        resizePane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                dragOffsetX = (stage.getX() + stage.getWidth() - t.getScreenX());
                t.consume();
                System.out.println(dragOffsetX + " : " + mouX);
            }
        });
        resizePane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                double x = t.getScreenX() + dragOffsetX;
                System.out.println(x - stage.getX());
                double width = (x - stage.getX());
                double height = width / 1.78;
                stage.setWidth(Math.max(stageMinWidth, width));
                stage.setHeight(Math.max(stageMinHeight, height));
                t.consume();
            }
        });
    }

    private void addWindowDrag(Node n) {
        n.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mouX = me.getSceneX();
                mouY = me.getSceneY();
            }
        });
        n.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                stage.setX(me.getScreenX() - mouX);
                stage.setY(me.getScreenY() - mouY);
            }
        });
    }

    private class TimeChangeListener implements InvalidationListener {

        @Override
        public void invalidated(Observable o) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (timeSlider.isPressed()) {
                        long seek = (long) timeSlider.getValue();
                        player.setTime(seek);
                    } else {
                        //do nothing
                    }
                }
            });
        }
    }

    private class VolumeChangeListener implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
            player.setVolume(t1.intValue());
        }
    }

    private class VolumeListener implements InvalidationListener {

        @Override
        public void invalidated(Observable o) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    int vol = player.getVolume();
                    volSlider.setValue((double) vol);
                    if (volSlider.getValue() == 0) {
                        volImage.setImage(volMute);
                    } else if (volSlider.getValue() > 0 && volSlider.getValue() < 55) {
                        volImage.setImage(volLow);
                    } else if (volSlider.getValue() > 55) {
                        volImage.setImage(volHigh);
                    }
                }
            });
        }
    }

    public void createMediaLibrary() {
        Parent lib = null;
        try {
            lib = FXMLLoader.load(getClass().getResource("MediaLibrary.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(MyVideoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene scene = new Scene(lib);
        Stage libStage = new Stage();
        libStage.setScene(scene);
        libStage.show();
    }

    public static MediaPlayer getPlayer() {
        return player;
    }

}//==============================EOF============================================
