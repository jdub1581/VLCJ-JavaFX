/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.vlcplayer.playerComponents.mediaLibrary;

import java.io.File;
import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

/**
 *
 * @author Dub-Laptop
 */
public class ThumbnailLoader extends Service<Void> {

    private static final float VLC_THUMBNAIL_POSITION = 30.0f / 100.0f;
    private MediaPlayerFactory factory;
    private MediaPlayer player;
    private int index = 0;
    private List<File> fileList;
    private final String[] VLC_ARGS = {
        "--intf", "dummy", /* no interface */
        "--vout", "dummy", /* we don't want video (output) */
        "--no-audio", /* we don't want audio (decoding) */
        "--no-video-title-show", /* nor the filename displayed */
        "--no-stats", /* no stats */
        "--no-sub-autodetect-file", /* we don't want subtitles */
        "--no-disable-screensaver", /* we don't want interfaces */
        "--no-snapshot-preview", /* no blending in dummy vout */};
    private boolean finished = false;

    public ThumbnailLoader(List<File> list) {
        this.fileList = list;
        this.factory = new MediaPlayerFactory(VLC_ARGS);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                player = factory.newHeadlessMediaPlayer();
                player.startMedia(fileList.get(index).getAbsolutePath());
                player.setPosition(VLC_THUMBNAIL_POSITION);

                Thread.sleep(300);// allows time for player to load file

                final String filename;
                if (fileList.get(index).getName().endsWith(".mp4")) {
                    filename = fileList.get(index).getName().replace(".mp4", "");
                    boolean save = player.saveSnapshot(new File("c:/thumbNails/" + filename + ".png"));
                    if (save) {
                        System.out.println("saved" + filename);
                    }
                } else if (fileList.get(index).getName().endsWith(".avi")) {
                    filename = fileList.get(index).getName().replace(".avi", "");
                    boolean save = player.saveSnapshot(new File("c:/thumbNails/" + filename + ".png"));
                    if (save) {
                        System.out.println("saved" + filename);
                    }
                } else if (fileList.get(index).getName().endsWith(".mkv")) {
                    filename = fileList.get(index).getName().replace(".mkv", "");
                    boolean save = player.saveSnapshot(new File("c:/thumbNails/" + filename + ".png"));
                    if (save) {
                        System.out.println("saved" + filename);
                    }
                }
                updateProgress(index, fileList.size());
                return null;
            }
        };
    }

    @Override
    protected void failed() {
        super.failed();
        player.release();
        factory.release();
        System.out.println("failed ");
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        System.out.println("success");
        System.out.println(this.getProgress());
        player.release();
        if (index < fileList.size()) {
            restart();
        } else if (index == fileList.size()) {
            factory.release();
            finished = true;
        }
        index++;
    }

    public boolean isFinished() {
        return finished;
    }
    
}
