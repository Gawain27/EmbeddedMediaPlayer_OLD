package embeddedmediaplayer;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


@SuppressWarnings("SpellCheckingInspection")
public class EmbeddedMediaPlayer extends Application {

    private Thread winnersThread;
    private Group mediaRoot, imageRoot;

    @Override
    public void start(Stage primaryStage) {
        Configs.getConfig();
        Rectangle2D reference = Screen.getScreens().get(Configs.SCREEN.getInt()).getVisualBounds();
        primaryStage.setX(reference.getMinX());
        primaryStage.setY(reference.getMinY());
        primaryStage.setTitle("I.T.E.T. Leonardo Da Vinci");
        primaryStage.setFullScreen(true);
        mediaRoot = new Group();
        imageRoot = new Group();
        Scene scene = new Scene(mediaRoot, 1920, 1080);
        MediaView mediaView = new MediaView();
        mediaRoot.getChildren().add(mediaView);
        ImageView imageView = new ImageView();
        MediaView sView = new MediaView();
        imageRoot.getChildren().add(imageView);
        imageRoot.getChildren().add(sView);
        System.out.println(imageRoot.getChildren().contains(sView));
        openNewVideo(scene, mediaView, Configs.INTRO.get(),
                event -> (winnersThread = new Thread(() -> sayWinners(scene, mediaView, imageView, sView))).start(),
                () -> mediaView.getMediaPlayer().seek(Duration.ZERO));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private final Object lock = new Object();
    private void sayWinners(Scene scene, MediaView mView, ImageView iView, MediaView sView) {
        for(int i = 0; i < Configs.CATEGORIES.getInt(); i++) {
            synchronized (lock) {
                try {
                    while (true) {
                        openNewVideo(scene, mView, Configs.SUSPANCE.get(), event -> {
                        }, () -> new Thread(() -> {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }).start());
                        lock.wait();
                        openNewImage(scene, iView, String.format("%s.jpg", winner(i)), event -> {
                            if (sView.getMediaPlayer()!= null)
                                sView.getMediaPlayer().stop();
                            if (event.getCode().equals(KeyCode.END)) winnersThread.interrupt();
                            else new Thread(() -> {
                                synchronized (lock) {
                                    lock.notifyAll();
                                }
                            }).start();
                        }, i, sView, Configs.WINNERSOUND.get());
                        lock.wait();
                    }
                } catch (InterruptedException ignored) {
                }
                winners = new HashSet<>();
            }
        }
            openNewImage(scene, iView, Configs.ENDIMAGE.get(), event -> System.exit(1), -1, sView, Configs.ENDSOUND.get());
    }

    private Set<Integer> winners = new HashSet<>();
    private int winner(int i) {
        Random rand = new Random();
        int w;
        do{
            System.out.println("I: " + i);
            w = rand.nextInt(Configs.CANDIDATES.getInt(i));
        }
        while (winners.contains(w) || new File(String.format("%s%d%s%s.jpg",
                Configs.PATH.get(),
                i,
                File.pathSeparatorChar,
                w)).exists());
        winners.add(w);
        return w;
    }

    @SuppressWarnings("SameParameterValue")
    private void openNewVideo(Scene scene, MediaView view, String path, EventHandler<KeyEvent> event, Runnable onEnd, int i){
        if(view.getMediaPlayer() != null) view.getMediaPlayer().stop();
        MediaPlayer player = new MediaPlayer(new Media(getResource(path,i)));
        player.setOnEndOfMedia(onEnd);
        player.setAutoPlay(true);
        view.setMediaPlayer(player);
        scene.setRoot(mediaRoot);
        scene.setOnKeyPressed(event);
        DoubleProperty elementWidth = view.fitWidthProperty();
        DoubleProperty elementHeight = view.fitHeightProperty();
        elementWidth.bind(Bindings.selectDouble(view.sceneProperty(), "width"));
        elementHeight.bind(Bindings.selectDouble(view.sceneProperty(), "height"));
        view.setPreserveRatio(true);
    }

    private void openNewVideo(Scene scene, MediaView view, String path, EventHandler<KeyEvent> event, Runnable onEnd){
        openNewVideo(scene, view, path, event, onEnd, -1);
    }

    private void openNewImage(Scene scene, ImageView view, String path,
                              EventHandler<KeyEvent> event, int i, MediaView mView, String soundPath){
        view.setImage(new Image(getResource(path, i)));
        if(mView != null && soundPath != null) {
            if(mView.getMediaPlayer() != null) mView.getMediaPlayer().stop();
            if(!soundPath.equals("null")) {
                System.out.println("Inizio apertura audio");
                mView.setMediaPlayer(new MediaPlayer(new Media(getResource(soundPath, -1))));
                mView.getMediaPlayer().setAutoPlay(true);
            }
        }
        scene.setRoot(imageRoot);
        scene.setOnKeyPressed(event);
        view.fitWidthProperty().bind(scene.widthProperty());
        view.fitHeightProperty().bind(scene.heightProperty());
        view.setPreserveRatio(true);
        double reducCoeff = (view.getFitWidth() / view.getImage().getWidth() >= view.getFitHeight() / view.getImage().getHeight())
                ? (view.getFitHeight() / view.getImage().getHeight())
                : (view.getFitWidth() / view.getImage().getWidth());
        view.setX((view.getFitWidth() - (view.getImage().getWidth() * reducCoeff)) / 2);
        view.setY((view.getFitHeight() - (view.getImage().getHeight() * reducCoeff)) / 2);
    }

    private String getResource(String resourceName, int i){
        try {
            if(i >= 0)
                return new File(String.format("%s%d/%s",
                        Configs.PATH.get(),
                        i,
                        resourceName)).toURI().toURL().toExternalForm();
            else
                return new File(resourceName).toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(String.format("%s non è un file valido!\n", resourceName));
    }

    public static void main(String[] args) {launch(args);}
}