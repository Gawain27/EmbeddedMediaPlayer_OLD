package embeddedmediaplayer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public class EmbeddedMediaPlayer extends Application {

    private Thread winnersThread;
    private static Properties config = null;
    private Group mediaRoot, imageRoot;

    @Override
    public void start(Stage primaryStage) {
        loadConfig();
        primaryStage.setTitle("I.T.E.T. Leonardo Da Vinci");
        primaryStage.setFullScreen(true);
        mediaRoot = new Group();
        imageRoot = new Group();
        Scene scene = new Scene(mediaRoot, 1920, 1080);
        MediaView mediaView = new MediaView();
        mediaRoot.getChildren().add(mediaView);
        ImageView imageView = new ImageView();
        imageRoot.getChildren().add(imageView);
        openNewVideo(scene, mediaView, config.getProperty("INTRO"),
                event -> (winnersThread =new Thread(() -> sayWinners(scene, mediaView, imageView))).start(),
                () -> mediaView.getMediaPlayer().seek(Duration.ZERO));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void loadConfig(){
        if (config != null)
            return;
        File tmpFile = new File("config.proprieties");
        config = new Properties();
        if(!tmpFile.exists()){
            System.out.println("Ingresso in loadConfig!");
            try(OutputStream fileOutput = new FileOutputStream(tmpFile)) {
                tmpFile.createNewFile();
                config.setProperty("PATH", "videos");
                config.setProperty("INTRO", "INTRO.mp4");
                config.setProperty("SUSPANCE", "SUSPANCE.mp4");
                config.setProperty("CANDIDATES", "150");
                config.store(fileOutput, null);
            } catch (IOException ignored) {}
        } else {
            try(InputStream fileInput = new FileInputStream(tmpFile)){
                config.load(fileInput);
            } catch (IOException ignored) {}
        }
        tmpFile = new File(config.getProperty("PATH"));
        if(!tmpFile.exists())
            tmpFile.mkdirs();
    }

    private final Object lock = new Object();
    @SuppressWarnings({"StatementWithEmptyBody", "InfiniteLoopStatement"})
    private void sayWinners(Scene scene, MediaView mView, ImageView iView) {
        synchronized (lock) {
            while (true) {
                openNewVideo(scene, mView, config.getProperty("SUSPANCE"), event -> {
                    if (event.getCode().equals(KeyCode.END)) System.exit(1);
                }, () -> new Thread(() -> {synchronized (lock) {lock.notifyAll();}}).start());
                try {lock.wait();} catch (InterruptedException ignored) {}
                openNewImage(scene, iView, String.format("%s.jpg", winner()), event -> {
                    if (event.getCode().equals(KeyCode.END)) System.exit(1);
                    else new Thread(() -> {synchronized (lock) {lock.notifyAll();}}).start();
                });
                try {lock.wait();} catch (InterruptedException ignored) {}
            }
        }
    }

    private Set<Integer> winners = new HashSet<>();
    private int winner() {
        Random rand = new Random();
        int w;
        do w = rand.nextInt(readFromConfig("CANDIDATES"));
        while (winners.contains(w) || new File(String.format("%s%c%s.jpg",
                config.getProperty("PATH"),
                File.pathSeparatorChar,
                w)).exists());
        winners.add(w);
        return w;
    }

    @SuppressWarnings("SameParameterValue")
    private int readFromConfig(String path){
        return Integer.parseInt(config.getProperty(path));
    }

    static Properties getConfig(){
        return config;
    }

    private void openNewVideo(Scene scene, MediaView view, String path, EventHandler<KeyEvent> event, Runnable onEnd){
        if(view.getMediaPlayer() != null) view.getMediaPlayer().stop();
        MediaPlayer player = new MediaPlayer(new Media(getResource(path)));
        player.setOnEndOfMedia(onEnd);
        player.setAutoPlay(true);
        view.setMediaPlayer(player);
        scene.setRoot(mediaRoot);
        scene.setOnKeyPressed(event);
    }

    @SuppressWarnings("ConstantConditions")
    private void openNewImage(Scene scene, ImageView view, String path, EventHandler<KeyEvent> event){
        view.setImage(new Image(getResource(path)));
        scene.setRoot(imageRoot);
        scene.setOnKeyPressed(event);
    }

    private String getResource(String resourceName){
        try {
            return new File(String.format("%s/%s",
                    config.getProperty("PATH"),
                    resourceName)).toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {launch(args);}
}
