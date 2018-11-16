/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package embeddedmediaplayer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

@SuppressWarnings("SpellCheckingInspection")
public class EmbeddedMediaPlayer extends Application {

    private static final String PrimoVideo =  "PrimoVideo.mp4";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Embedded Media Player");
        Group root = new Group();
        primaryStage.setFullScreen(true);
        Media media = new Media(getClass().getResource(PrimoVideo).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setStyle("-fx-background-color: black;");
        root.getChildren().add(mediaView);
        Scene scene = new Scene(root, 540, 241);
        scene.setRoot(root);
        scene.setOnKeyPressed(event -> onKeyPressed(scene, mediaView));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void onKeyPressed(Scene scene, MediaView view){
        openNewVideo(view, "");
        String pathToImage = "";
        view.getMediaPlayer().setOnEndOfMedia(() -> {
            /*
            SELEZIONE E INDIVIDUAZIONE PERCORSO IMMAGINE
             */
            openNewVideo(view, pathToImage);
        });

    }

    public void openNewVideo(MediaView view, String path){
        view.setMediaPlayer(new MediaPlayer(new Media(getClass().getResource(path).toExternalForm())));
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
