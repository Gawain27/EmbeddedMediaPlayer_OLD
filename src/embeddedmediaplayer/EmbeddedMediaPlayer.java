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
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Ingresso in sayWinners");
                for(int i = 0; i < WINNERS; i++){
                    listener[0] = false;
                    System.out.println("Inizio apertura suspance");
                    openNewVideo(scene, view, SUSPANCE, event -> {}, () -> listener[0] = true);
                    System.out.println("Fine apertura suspance");
                    while(!listener[0]){}
                    listener[1] = false;
                    openNewVideo(scene, view, String.format("%s.mp4", winner()), event -> listener[1] = true, () -> {});
                    while (!listener[1]){}
                }
                endOfLife(scene, view);
            }
        }).start();
    }

    private void endOfLife(Scene scene, MediaView view){

    }

    private Set<Integer> winners = new HashSet<>();
    private int winner() {
        Random rand = new Random();
        int w;
        do {
            w = rand.nextInt(CANDIDATES);
            System.out.printf("Numero estratto non verificato: %d\n", w);
        }
            while (winners.contains(w) || new File(String.format("%d.mp4", w)).exists());
        winners.add(w);
        System.out.printf("Numero estratto: %d\n", w);
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
