package embeddedmediaplayer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("SpellCheckingInspection")
public class EmbeddedMediaPlayer extends Application {

    private static final String PrimoVideo =  "PrimoVideo.mp4", SecondoVideo = "SecondoVideo.mp4";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Embedded Media Player");
        Group root = new Group();
        primaryStage.setFullScreen(true);
        Media media = new Media(getClass().getResource(PrimoVideo).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
        MediaView mediaView = new MediaView(mediaPlayer);
        root.getChildren().add(mediaView);
        Scene scene = new Scene(root, 1240, 720);
        scene.setRoot(root);
        scene.setOnKeyPressed(event -> onKeyPressed(mediaView));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    boolean pressed = false;
    private void onKeyPressed(MediaView view){
        if(pressed)
            return;
        openNewVideo(view, SecondoVideo);
        //String pathToImage = "";
        view.getMediaPlayer().setOnEndOfMedia(() -> System.exit(1));
        pressed = true;
    }

    private void openNewVideo(MediaView view, String path){
        MediaPlayer player = new MediaPlayer(new Media(getClass().getResource(path).toExternalForm()));
        player.setAutoPlay(true);
        view.setMediaPlayer(player);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
