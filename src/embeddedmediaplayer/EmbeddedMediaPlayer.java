package embeddedmediaplayer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
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

    private static final String INTRO =  "PrimoVideo.mp4", SUSPANCE = "SecondoVideo.mp4";
    private static final int CANDIDATES = 150, WINNERS = 6;

    public class MyBoolean {
        private boolean bool = false;
        void set(boolean bool){
            this.bool = bool;
        }
        boolean get(){
            return bool;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("I.T.E.T. Leonardo Da Vinci");
        primaryStage.setFullScreen(true);

        MediaView mediaView = new MediaView();
        openNewVideo(mediaView, INTRO);
        mediaView.getMediaPlayer().setOnEndOfMedia(() -> mediaView.getMediaPlayer().seek(Duration.ZERO));

        Group pippo = new Group();
        pippo.getChildren().add(mediaView);
        Scene scene = new Scene(pippo, 1920, 1080);
        scene.setRoot(pippo);
        scene.setOnKeyPressed(event -> sayWinners(scene, mediaView));
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    @SuppressWarnings("StatementWithEmptyBody")
    private void sayWinners(Scene scene, MediaView view){
        for(int i = 0; i < WINNERS; i++){
            scene.setOnKeyPressed(null);
            openNewVideo(view, SUSPANCE);
            while(!view.getMediaPlayer().getStatus().equals(MediaPlayer.Status.STOPPED)){}
            openNewVideo(view, String.format("%d.jpg", winner()));
            MyBoolean keyListener = new MyBoolean();
            scene.setOnKeyPressed(event -> keyListener.set(true));
            while (!keyListener.get()){}
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

    private void openNewVideo(MediaView view, String path){
        MediaPlayer player = new MediaPlayer(new Media(getClass().getResource(path).toExternalForm()));
        player.setAutoPlay(true);
        view.setMediaPlayer(player);
    }

    public static void main(String[] args) {launch(args);}
}
