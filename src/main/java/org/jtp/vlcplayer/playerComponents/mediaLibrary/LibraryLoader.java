/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtp.vlcplayer.playerComponents.mediaLibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Dub-Laptop
 */
public class LibraryLoader {

    private static File libFile = new File("c:/thumbnails/libs");
    private static File imgFile = new File("c:/thumbnails");
    private static Path libPath;
    private static List<String> lines;

    public static List<String> getLibraryLocations() {
        final List<String> list = new ArrayList<>();
        if (!libFile.exists()) {
            try {
                boolean c = libFile.createNewFile();
                return list;
            } catch (IOException ex) {
                Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (libFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(libFile));
                String s;
                while ((s = br.readLine()) != null) {
                    System.out.println(s);
                    list.add(s);
                }
            } catch (IOException ex) {
                Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    public static void addLibraryLocation(String dirPath) {
        System.err.println("File exists?");
        if (libFile.exists()) {
            try {
                try (BufferedWriter wr = new BufferedWriter(new FileWriter(libFile))) {
                    wr.newLine();
                    wr.append(dirPath);
                    wr.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(!libFile.exists()){
            try{
                libFile.createNewFile();
                    try (BufferedWriter wr = new BufferedWriter(new FileWriter(libFile))) {
                    wr.newLine();
                    wr.append(dirPath);
                    wr.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void removeLibLocation(String path) {
        lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(libFile));
            String s;
            while ((s = br.readLine()) != null) {
                lines.add(s);
            }
            for (String r : lines) {
                if (r.contentEquals(path)) {
                    lines.remove(r);
                }
            }
            libFile.delete();
            libFile = new File("libs");
            try (BufferedWriter wr = new BufferedWriter(new FileWriter(libFile))) {
                for (String n : lines) {
                    wr.append(n);
                    wr.newLine();
                }
                wr.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Map<String, ImageView> getMappedItems() {
        final Map<String, ImageView> map = new TreeMap<>();
        BufferedReader br = null;
        File file = null;
        final List<ImageView> images = new ArrayList<>();            
        
        try {
            for(File f : imgFile.listFiles()){
                if(f.getName().endsWith(".png")){
                    Image i = new Image(f.toURI().toURL().openStream());
                    ImageView v = new ImageView(i);
                    v.setId(f.getName().replace(".png", ""));
                    images.add(v);
                }
            }
            
            br = new BufferedReader(new FileReader(libFile));
            String s;
            while ((s = br.readLine()) != null) {
                file = new File(s);
                libPath = file.toPath();
            }
            Files.walkFileTree(libPath, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toFile().getName().endsWith(".mp4") || file.toFile().getName().endsWith(".avi") || file.toFile().getName().endsWith(".mkv")){
                        String name = file.toFile().getName().replaceAll(".mp4", "").replaceAll(".avi", "").replaceAll(".mkv", "");
                        for(ImageView v : images){
                            if(v.getId().contentEquals(name)){
                                map.put(file.toFile().getAbsolutePath(), v);
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return map;
    }
    
    public static void generateMissingThumbs(){
        final List<File> list = new ArrayList<>();
        final List<File> files = new ArrayList<>();
        lines = new ArrayList<>();
        BufferedReader br = null;
        
        try {            
            br = new BufferedReader(new FileReader(libFile));
            String s;
            while ((s = br.readLine()) != null) {
                lines.add(s);
            }
        } catch (IOException ex) {
            Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(String s : lines){
            try {
                final File file = new File(s);
                Files.walkFileTree(file.toPath(), new FileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toFile().getName().endsWith(".mp4") || file.toFile().getName().endsWith(".avi") || file.toFile().getName().endsWith(".mkv")){
                        String name = file.toFile().getName();
                            files.add(file.toFile());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(LibraryLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(File s : files){
            File f = new File(imgFile, s.getName().replace(".mp4", "").replace(".avi", "").replace(".mkv", "") + ".png");
            if(!f.exists()){
                list.add(s);
            }
        }
        //System.out.println(files);
        ThumbnailLoader loader = new ThumbnailLoader(list);
        loader.start();
    }
}
