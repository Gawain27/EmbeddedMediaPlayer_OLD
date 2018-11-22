package embeddedmediaplayer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public class EmbeddedMediaPlayer extends Application {

    private static final String INTRO =  "INTRO.mp4", SUSPANCE = "SUSPANCE.mp4", PATH = "";
    private static final int CANDIDATES = 8, WINNERS = 2;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("I.T.E.T. Leonardo Da Vinci");
        primaryStage.setFullScreen(true);
        Group root = new Group();
        Scene scene = new Scene(root, 1920, 1080);
        MediaView mediaView = new MediaView();
        openNewVideo(scene, mediaView, INTRO,
                event -> sayWinners(scene, mediaView),
                () -> mediaView.getMediaPlayer().seek(Duration.ZERO));
        root.getChildren().add(mediaView);
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean[] listener = new boolean[2];
    @SuppressWarnings("StatementWithEmptyBody")
    private void sayWinners(Scene scene, MediaView view){
        for(int i = 0; i < WINNERS; i++){
            listener[0] = false;
            openNewVideo(scene, view, SUSPANCE, null, () -> listener[0] = true);
            while(!listener[0]){}
            listener[1] = false;
            openNewVideo(scene, view, String.format("%d.jpg", winner()), event -> listener[1] = true, null);
            while (!listener[1]){}
        }
        endOfLife(scene, view);
    }

    private void endOfLife(Scene scene, MediaView view){

    }

    private Set<Integer> winners = new HashSet<>();
    private int winner(){
        Random rand = new Random();
        int w;
        do w = rand.nextInt(CANDIDATES);
            while (winners.contains(w) || new File(String.format("%d.jpg", w)).exists());
        winners.add(w);
        return w;
    }

    private void openNewVideo(Scene scene, MediaView view, String path, EventHandler<KeyEvent> event, Runnable onEnd){
        MediaPlayer player = new MediaPlayer(new Media(getClass().getResource(path).toExternalForm()));
        player.setOnEndOfMedia(onEnd);
        player.setAutoPlay(true);
        view.setMediaPlayer(player);
        scene.setOnKeyPressed(event);
    }

    public static void main(String[] args) {launch(args);}
}
